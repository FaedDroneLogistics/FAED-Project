// Copyright 2011 Google Inc. All Rights Reserved.

package org.ros.internal.node;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ros.address.AdvertiseAddress;
import org.ros.address.BindAddress;
import org.ros.internal.message.old_style.MessageSerializer;
import org.ros.internal.node.client.MasterClient;
import org.ros.internal.node.client.Registrar;
import org.ros.internal.node.parameter.ParameterManager;
import org.ros.internal.node.server.NodeIdentifier;
import org.ros.internal.node.server.SlaveServer;
import org.ros.internal.node.server.master.MasterServer;
import org.ros.internal.node.service.ServiceManager;
import org.ros.internal.node.topic.DefaultPublisher;
import org.ros.internal.node.topic.TopicDefinition;
import org.ros.internal.node.topic.TopicManager;
import org.ros.message.MessageDefinition;
import org.ros.message.MessageFactory;
import org.ros.namespace.GraphName;
import org.ros.node.topic.CountDownPublisherListener;

/**
 * Tests for the {@link Registrar} class.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 */
public class RegistrarTest {

  private final TopicDefinition topicDefinition;
  private final MessageSerializer<org.ros.message.std_msgs.String> messageSerializer;

  private ScheduledExecutorService executorService;
  private MasterServer masterServer;
  private MasterClient masterClient;
  private Registrar registrar;
  private TopicManager topicManager;
  private ServiceManager serviceManager;
  private ParameterManager parameterManager;
  private SlaveServer slaveServer;
  private DefaultPublisher<org.ros.message.std_msgs.String> publisher;
  private CountDownPublisherListener<org.ros.message.std_msgs.String> publisherListener;
  private MessageFactory messageFactory;

  public RegistrarTest() {
    topicDefinition =
        TopicDefinition.newFromTopicName(new GraphName("/topic"), MessageDefinition.newFromStrings(
            org.ros.message.std_msgs.String.__s_getDataType(),
            org.ros.message.std_msgs.String.__s_getMessageDefinition(),
            org.ros.message.std_msgs.String.__s_getMD5Sum()));
    messageSerializer = new MessageSerializer<org.ros.message.std_msgs.String>();
  }

  @Before
  public void setup() {
	Log log = Mockito.mock(Log.class);
    executorService = Executors.newScheduledThreadPool(Integer.MAX_VALUE);
    masterServer = new MasterServer(BindAddress.newPrivate(), AdvertiseAddress.newPrivate());
    masterServer.start();
    masterClient = new MasterClient(masterServer.getUri());
    registrar = new Registrar(masterClient, executorService, log);
    topicManager = new TopicManager();
    serviceManager = new ServiceManager();
    parameterManager = new ParameterManager();
    slaveServer =
        new SlaveServer(new GraphName("/node"), BindAddress.newPrivate(),
            AdvertiseAddress.newPrivate(), BindAddress.newPrivate(), AdvertiseAddress.newPrivate(),
            masterClient, topicManager, serviceManager, parameterManager, executorService);
    slaveServer.start();
    NodeIdentifier nodeIdentifier = slaveServer.toSlaveIdentifier();
    registrar.start(nodeIdentifier);
    messageFactory = mock(MessageFactory.class);
    publisher =
        new DefaultPublisher<org.ros.message.std_msgs.String>(nodeIdentifier, topicDefinition,
            messageSerializer, messageFactory, executorService);
    publisherListener = CountDownPublisherListener.newDefault();
    publisher.addListener(publisherListener);
  }

  @After
  public void tearDown() {
    registrar.shutdown();
    masterServer.shutdown();
    executorService.shutdown();
  }

  @Test
  public void testRegisterAndUnregisterPublisher() throws InterruptedException {
    registrar.onPublisherAdded(publisher);
    assertTrue(publisherListener.awaitMasterRegistrationSuccess(1, TimeUnit.SECONDS));
    registrar.onPublisherRemoved(publisher);
    assertTrue(publisherListener.awaitMasterUnregistrationSuccess(1, TimeUnit.SECONDS));
    registrar.shutdown();
    registrar.onPublisherAdded(publisher);
    assertTrue(publisherListener.awaitMasterRegistrationFailure(1, TimeUnit.SECONDS));
    registrar.onPublisherRemoved(publisher);
    assertTrue(publisherListener.awaitMasterUnregistrationFailure(1, TimeUnit.SECONDS));
  }

  @Test
  public void testRegisterPublisherRetries() throws InterruptedException {
    masterServer.shutdown();
    registrar.setRetryDelay(100, TimeUnit.MILLISECONDS);
    registrar.onPublisherAdded(publisher);
    assertTrue(publisherListener.awaitMasterRegistrationFailure(1, TimeUnit.SECONDS));
    // Restart the MasterServer on the same port (hopefully still available).
    masterServer =
        new MasterServer(BindAddress.newPrivate(masterServer.getAdvertiseAddress().getPort()),
            AdvertiseAddress.newPrivate());
    masterServer.start();
    assertTrue(publisherListener.awaitMasterRegistrationSuccess(1, TimeUnit.SECONDS));
  }

  @Test
  public void testRegisterAfterShutdownIgnored() throws InterruptedException {
    registrar.shutdown();
    registrar.onPublisherAdded(publisher);
    assertTrue(publisherListener.awaitMasterRegistrationFailure(1, TimeUnit.SECONDS));
  }
}
