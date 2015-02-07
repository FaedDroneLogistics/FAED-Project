/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.internal.node;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.concurrent.ListenerCollection;
import org.ros.concurrent.ListenerCollection.SignalRunnable;
import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.internal.message.new_style.ServiceMessageDefinition;
import org.ros.internal.message.old_style.MessageDeserializer;
import org.ros.internal.message.old_style.MessageSerializer;
import org.ros.internal.message.old_style.ServiceMessageDefinitionFactory;
import org.ros.internal.node.client.MasterClient;
import org.ros.internal.node.client.Registrar;
import org.ros.internal.node.parameter.ParameterManager;
import org.ros.internal.node.response.Response;
import org.ros.internal.node.response.StatusCode;
import org.ros.internal.node.server.NodeIdentifier;
import org.ros.internal.node.server.SlaveServer;
import org.ros.internal.node.service.ServiceDefinition;
import org.ros.internal.node.service.ServiceFactory;
import org.ros.internal.node.service.ServiceIdentifier;
import org.ros.internal.node.service.ServiceManager;
import org.ros.internal.node.service.ServiceResponseBuilder;
import org.ros.internal.node.topic.PublisherFactory;
import org.ros.internal.node.topic.SubscriberFactory;
import org.ros.internal.node.topic.TopicDefinition;
import org.ros.internal.node.topic.TopicManager;
import org.ros.internal.node.xmlrpc.XmlRpcTimeoutException;
import org.ros.message.MessageDefinition;
import org.ros.message.MessageFactory;
import org.ros.message.MessageSerializationFactory;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.namespace.NameResolver;
import org.ros.namespace.NodeNameResolver;
import org.ros.node.DefaultNodeFactory;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeListener;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceServer;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import org.ros.time.TimeProvider;

import com.google.common.annotations.VisibleForTesting;

/**
 * The default implementation of a {@link Node}.
 * 
 * @author ethan.rublee@gmail.com (Ethan Rublee)
 * @author kwc@willowgarage.com (Ken Conley)
 * @author damonkohler@google.com (Damon Kohler)
 */
public class DefaultNode implements Node {

	private static final boolean DEBUG = false;

	/**
	 * The maximum delay before shutdown will begin even if all
	 * {@link NodeListener}s have not yet returned from their
	 * {@link NodeListener#onShutdown(Node)} callback.
	 */
	private static final int MAX_SHUTDOWN_DELAY_DURATION = 5;
	private static final TimeUnit MAX_SHUTDOWN_DELAY_UNITS = TimeUnit.SECONDS;

	private final GraphName nodeName;
	private final NodeConfiguration nodeConfiguration;
	private final NodeNameResolver resolver;
	private final RosoutLogger log;
	private final MasterClient masterClient;
	private final SlaveServer slaveServer;
	private final TopicManager topicManager;
	private final ServiceManager serviceManager;
	private final ParameterManager parameterManager;
	private final ServiceFactory serviceFactory;
	private final Registrar registrar;
	private final URI masterUri;

	/**
	 * Used for all thread creation.
	 */
	private final ScheduledExecutorService scheduledExecutorService;

	/**
	 * All {@link NodeListener} instances registered with the node.
	 */
	private final ListenerCollection<NodeListener> nodeListeners;

	private MasterPinger masterPinger;

	/**
	 * {@link DefaultNode}s should only be constructed using the
	 * {@link DefaultNodeFactory}.
	 * 
	 * @param nodeConfiguration
	 *            the {@link NodeConfiguration} for this {@link Node}
	 * @param nodeListeners
	 *            a {@link Collection} of {@link NodeListener}s that will be
	 *            added to this {@link Node} before it starts
	 */
	public DefaultNode(NodeConfiguration nodeConfiguration,
			Collection<NodeListener> nodeListeners,
			ScheduledExecutorService scheduledExecutorService,
			MasterPinger masterPinger) {
		this.nodeConfiguration = NodeConfiguration.copyOf(nodeConfiguration);
		this.nodeListeners = new ListenerCollection<NodeListener>(
				nodeListeners, scheduledExecutorService);
		this.scheduledExecutorService = scheduledExecutorService;
		masterUri = nodeConfiguration.getMasterUri();
		masterClient = new MasterClient(masterUri);
		topicManager = new TopicManager();
		serviceManager = new ServiceManager();
		parameterManager = new ParameterManager();

		GraphName basename = nodeConfiguration.getNodeName();
		NameResolver parentResolver = nodeConfiguration.getParentResolver();
		nodeName = parentResolver.getNamespace().join(basename);
		resolver = new NodeNameResolver(nodeName, parentResolver);
		slaveServer = new SlaveServer(nodeName,
				nodeConfiguration.getTcpRosBindAddress(),
				nodeConfiguration.getTcpRosAdvertiseAddress(),
				nodeConfiguration.getXmlRpcBindAddress(),
				nodeConfiguration.getXmlRpcAdvertiseAddress(), masterClient,
				topicManager, serviceManager, parameterManager,
				scheduledExecutorService);
		slaveServer.start();

		NodeIdentifier nodeIdentifier = slaveServer.toSlaveIdentifier();
		topicManager
				.setPublisherFactory(new PublisherFactory(nodeIdentifier,
						nodeConfiguration.getMessageFactory(),
						scheduledExecutorService));
		topicManager.setSubscriberFactory(new SubscriberFactory(nodeIdentifier,
				topicManager, scheduledExecutorService));

		serviceFactory = new ServiceFactory(nodeName, slaveServer,
				serviceManager, scheduledExecutorService);

		registrar = new Registrar(masterClient, scheduledExecutorService,
				nodeConfiguration.getLog());
		topicManager.setListener(registrar);
		serviceManager.setListener(registrar);
		registrar.start(nodeIdentifier);

		// NOTE(damonkohler): This must be created after the Registrar has been
		// initialized with the SlaveServer's NodeIdentifier so that it can
		// register the /rosout Publisher.
		log = new RosoutLogger(this);
		signalOnStart();

		if (masterPinger != null) {
			masterPinger.start(scheduledExecutorService, this, topicManager,
					registrar);
		}
	}

	@VisibleForTesting
	Registrar getRegistrar() {
		return registrar;
	}

	private <T> org.ros.message.MessageSerializer<T> newMessageSerializer(
			String messageType) {
		return nodeConfiguration.getMessageSerializationFactory()
				.newMessageSerializer(messageType);
	}

	@SuppressWarnings("unchecked")
	private <T> MessageDeserializer<T> newMessageDeserializer(String messageType) {
		return (MessageDeserializer<T>) nodeConfiguration
				.getMessageSerializationFactory().newMessageDeserializer(
						messageType);
	}

	@SuppressWarnings("unchecked")
	private <T> MessageSerializer<T> newServiceResponseSerializer(
			String serviceType) {
		return (MessageSerializer<T>) nodeConfiguration
				.getMessageSerializationFactory().newServiceResponseSerializer(
						serviceType);
	}

	@SuppressWarnings("unchecked")
	private <T> MessageDeserializer<T> newServiceResponseDeserializer(
			String serviceType) {
		return (MessageDeserializer<T>) nodeConfiguration
				.getMessageSerializationFactory()
				.newServiceResponseDeserializer(serviceType);
	}

	@SuppressWarnings("unchecked")
	private <T> MessageSerializer<T> newServiceRequestSerializer(
			String serviceType) {
		return (MessageSerializer<T>) nodeConfiguration
				.getMessageSerializationFactory().newServiceRequestSerializer(
						serviceType);
	}

	@SuppressWarnings("unchecked")
	private <T> MessageDeserializer<T> newServiceRequestDeserializer(
			String serviceType) {
		return (MessageDeserializer<T>) nodeConfiguration
				.getMessageSerializationFactory()
				.newServiceRequestDeserializer(serviceType);
	}

	@Override
	public <T> Publisher<T> newPublisher(GraphName topicName, String messageType) {
		GraphName resolvedTopicName = resolveName(topicName);
		MessageDefinition messageDefinition = nodeConfiguration
				.getMessageDefinitionFactory().newFromMessageType(messageType);
		TopicDefinition topicDefinition = TopicDefinition.newFromTopicName(
				resolvedTopicName, messageDefinition);
		org.ros.message.MessageSerializer<T> serializer = newMessageSerializer(messageType);
		return topicManager.newOrExistingPublisher(topicDefinition, serializer);
	}

	@Override
	public <T> Publisher<T> newPublisher(String topicName, String messageType) {
		return newPublisher(new GraphName(topicName), messageType);
	}

	@Override
	public <T> Subscriber<T> newSubscriber(GraphName topicName,
			String messageType) {
		GraphName resolvedTopicName = resolveName(topicName);
		MessageDefinition messageDefinition = nodeConfiguration
				.getMessageDefinitionFactory().newFromMessageType(messageType);
		TopicDefinition topicDefinition = TopicDefinition.newFromTopicName(
				resolvedTopicName, messageDefinition);
		MessageDeserializer<T> deserializer = newMessageDeserializer(messageType);
		Subscriber<T> subscriber = topicManager.newOrExistingSubscriber(
				topicDefinition, deserializer);
		return subscriber;
	}

	@Override
	public <T> Subscriber<T> newSubscriber(String topicName, String messageType) {
		return newSubscriber(new GraphName(topicName), messageType);
	}

	@Override
	public <T, S> ServiceServer<T, S> newServiceServer(GraphName serviceName,
			String serviceType, ServiceResponseBuilder<T, S> responseBuilder) {
		GraphName resolvedServiceName = resolveName(serviceName);
		// TODO(damonkohler): It's rather non-obvious that the URI will be
		// created
		// later on the fly.
		ServiceIdentifier identifier = new ServiceIdentifier(
				resolvedServiceName, null);
		ServiceMessageDefinition messageDefinition = ServiceMessageDefinitionFactory
				.newFromString(serviceType);
		ServiceDefinition definition = new ServiceDefinition(identifier,
				messageDefinition);
		MessageDeserializer<T> requestDeserializer = newServiceRequestDeserializer(serviceType);
		MessageSerializer<S> responseSerializer = newServiceResponseSerializer(serviceType);
		return serviceFactory.newServer(definition, requestDeserializer,
				responseSerializer, responseBuilder);
	}

	@Override
	public <T, S> ServiceServer<T, S> newServiceServer(String serviceName,
			String serviceType, ServiceResponseBuilder<T, S> responseBuilder) {
		return newServiceServer(new GraphName(serviceName), serviceType,
				responseBuilder);
	}

	@Override
	public <T, S> ServiceClient<T, S> newServiceClient(GraphName serviceName,
			String serviceType) throws ServiceNotFoundException {
		GraphName resolvedServiceName = resolveName(serviceName);
		URI uri = lookupService(resolvedServiceName);
		if (uri == null) {
			throw new ServiceNotFoundException("No such service "
					+ resolvedServiceName + " of type " + serviceType);
		}
		ServiceMessageDefinition messageDefinition = ServiceMessageDefinitionFactory
				.newFromString(serviceType);
		ServiceIdentifier serviceIdentifier = new ServiceIdentifier(
				resolvedServiceName, uri);
		ServiceDefinition definition = new ServiceDefinition(serviceIdentifier,
				messageDefinition);
		MessageSerializer<T> requestSerializer = newServiceRequestSerializer(serviceType);
		MessageDeserializer<S> responseDeserializer = newServiceResponseDeserializer(serviceType);
		return serviceFactory.newClient(definition, requestSerializer,
				responseDeserializer);
	}

	@Override
	public <T, S> ServiceClient<T, S> newServiceClient(String serviceName,
			String serviceType) throws ServiceNotFoundException {
		return newServiceClient(new GraphName(serviceName), serviceType);
	}

	@Override
	public URI lookupService(GraphName serviceName) {
		Response<URI> response = masterClient.lookupService(slaveServer
				.toSlaveIdentifier().getNodeName(), resolveName(serviceName)
				.toString());
		if (response.getStatusCode() == StatusCode.SUCCESS) {
			return response.getResult();
		} else {
			return null;
		}
	}

	@Override
	public URI lookupService(String serviceName) {
		return lookupService(new GraphName(serviceName));
	}

	@Override
	public Time getCurrentTime() {
		return nodeConfiguration.getTimeProvider().getCurrentTime();
	}

	@Override
	public TimeProvider getTimeProvider() {
		return nodeConfiguration.getTimeProvider();
	}

	@Override
	public GraphName getName() {
		return nodeName;
	}

	@Override
	public Log getLog() {
		return log;
	}

	@Override
	public GraphName resolveName(GraphName name) {
		return resolver.resolve(name);
	}

	@Override
	public GraphName resolveName(String name) {
		return resolver.resolve(new GraphName(name));
	}

	@Override
	public void shutdown() {
		signalOnShutdown();

		if (masterPinger != null) {
			masterPinger.shutdown();
		}

		// NOTE(damonkohler): We don't want to raise potentially spurious
		// exceptions during shutdown that would interrupt the process. This is
		// simply best effort cleanup.
		for (Publisher<?> publisher : topicManager.getPublishers()) {
			publisher.shutdown();
		}
		for (Subscriber<?> subscriber : topicManager.getSubscribers()) {
			subscriber.shutdown();
		}
		for (ServiceServer<?, ?> serviceServer : serviceManager.getServers()) {
			try {
				Response<Integer> response = masterClient.unregisterService(
						slaveServer.toSlaveIdentifier(), serviceServer);
				if (DEBUG) {
					if (response.getResult() == 0) {
						System.err.println("Failed to unregister service: "
								+ serviceServer.getName());
					}
				}
			} catch (XmlRpcTimeoutException e) {
				log.error(e);
			} catch (RemoteException e) {
				log.error(e);
			}
		}
		for (ServiceClient<?, ?> serviceClient : serviceManager.getClients()) {
			serviceClient.shutdown();
		}
		registrar.shutdown();
		slaveServer.shutdown();
		signalOnShutdownComplete();
	}

	@Override
	public URI getMasterUri() {
		return masterUri;
	}

	@Override
	public NodeNameResolver getResolver() {
		return resolver;
	}

	@Override
	public ParameterTree newParameterTree() {
		return org.ros.internal.node.parameter.DefaultParameterTree
				.newFromSlaveIdentifier(slaveServer.toSlaveIdentifier(),
						masterClient.getRemoteUri(), resolver, parameterManager);
	}

	@Override
	public URI getUri() {
		return slaveServer.getUri();
	}

	@Override
	public MessageSerializationFactory getMessageSerializationFactory() {
		return nodeConfiguration.getMessageSerializationFactory();
	}

	@Override
	public MessageFactory getMessageFactory() {
		return nodeConfiguration.getMessageFactory();
	}

	@Override
	public void addListener(NodeListener listener) {
		nodeListeners.add(listener);
	}

	@Override
	public void removeListener(NodeListener listener) {
		nodeListeners.remove(listener);
	}

	/**
	 * SignalRunnable all {@link NodeListener}s that the {@link Node} has
	 * started.
	 * 
	 * <p>
	 * Each listener is called in a separate thread.
	 */
	private void signalOnStart() {
		final Node node = this;
		nodeListeners.signal(new SignalRunnable<NodeListener>() {
			@Override
			public void run(NodeListener listener) {
				listener.onStart(node);
			}
		});
	}

	/**
	 * SignalRunnable all {@link NodeListener}s that the {@link Node} has
	 * started shutting down.
	 * 
	 * <p>
	 * Each listener is called in a separate thread.
	 */
	private void signalOnShutdown() {
		final Node node = this;
		try {
			nodeListeners.signal(new SignalRunnable<NodeListener>() {
				@Override
				public void run(NodeListener listener) {
					listener.onShutdown(node);
				}
			}, MAX_SHUTDOWN_DELAY_DURATION, MAX_SHUTDOWN_DELAY_UNITS);
		} catch (InterruptedException e) {
			// Ignored since we do not guarantee that all listeners will finish
			// before
			// shutdown begins.
		}
	}

	/**
	 * SignalRunnable all {@link NodeListener}s that the {@link Node} has shut
	 * down.
	 * 
	 * <p>
	 * Each listener is called in a separate thread.
	 */
	private void signalOnShutdownComplete() {
		final Node node = this;
		nodeListeners.signal(new SignalRunnable<NodeListener>() {
			@Override
			public void run(NodeListener listener) {
				try {
					listener.onShutdownComplete(node);
				} catch (Throwable e) {
					System.out.println(listener);
				}
			}
		});
	}

	@VisibleForTesting
	InetSocketAddress getAddress() {
		return slaveServer.getAddress();
	}

	@Override
	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutorService;
	}

	@Override
	public void executeCancellableLoop(final CancellableLoop cancellableLoop) {
		scheduledExecutorService.execute(cancellableLoop);
		addListener(new NodeListener() {
			@Override
			public void onStart(Node node) {
			}

			@Override
			public void onShutdown(Node node) {
				cancellableLoop.cancel();
			}

			@Override
			public void onShutdownComplete(Node node) {
			}
		});
	}
}
