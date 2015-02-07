/*
 * Copyright (C) 2013 Google Inc.
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

package interactivespaces.service.comm.network.server.internal.netty;

import interactivespaces.service.comm.network.WriteableUdpPacket;
import interactivespaces.service.comm.network.internal.netty.NettyWriteableUdpPacket;
import interactivespaces.service.comm.network.server.UdpServerNetworkCommunicationEndpoint;
import interactivespaces.service.comm.network.server.UdpServerNetworkCommunicationEndpointListener;
import interactivespaces.service.comm.network.server.UdpServerRequest;

import com.google.common.collect.Lists;

import org.apache.commons.logging.Log;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * A Netty-based {@link UdpServerNetworkCommunicationEndpoint}.
 *
 * @author Keith M. Hughes
 */
public class NettyUdpServerNetworkCommunicationEndpoint implements UdpServerNetworkCommunicationEndpoint {

  /**
   * The buffer size for UDP packets.
   */
  public static final int BUFFER_SIZE = 1024;

  /**
   * The port the server is listening to.
   */
  private final int serverPort;

  /**
   * Byte order for packets.
   */
  private final ByteOrder byteOrder;

  /**
   * The bootstrap for the UDP client.
   */
  private ConnectionlessBootstrap bootstrap;

  /**
   * The listeners to endpoint events.
   */
  private final List<UdpServerNetworkCommunicationEndpointListener> listeners = Lists.newCopyOnWriteArrayList();

  /**
   * Executor service for this endpoint.
   */
  private final ExecutorService executorService;

  /**
   * Logger for this endpoint.
   */
  private final Log log;

  /**
   * Construct a new endpoint.
   *
   * @param serverPort
   *          the server port to listen to
   * @param byteOrder
   *          byte order for packets
   * @param executorService
   *          the executor service to use
   * @param log
   *          the logger to use
   */
  public NettyUdpServerNetworkCommunicationEndpoint(int serverPort, ByteOrder byteOrder,
      ExecutorService executorService, Log log) {
    this.serverPort = serverPort;
    this.byteOrder = byteOrder;
    this.executorService = executorService;
    this.log = log;
  }

  @Override
  public void startup() {
    DatagramChannelFactory channelFactory = new NioDatagramChannelFactory(executorService);
    bootstrap = new ConnectionlessBootstrap(channelFactory);

    // Configure the pipeline factory.
    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      @Override
      public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(new NettyUdpServerHandler());
      }
    });

    // Enable broadcast
    bootstrap.setOption("broadcast", "false");

    // Allow packets as large as up to 1024 bytes (default is 768).
    // You could increase or decrease this value to avoid truncated packets
    // or to improve memory footprint respectively.
    //
    // Please also note that a large UDP packet might be truncated or
    // dropped by your router no matter how you configured this option.
    // In UDP, a packet is truncated or dropped if it is larger than a
    // certain size, depending on router configuration. IPv4 routers
    // truncate and IPv6 routers drop a large packet. That's why it is
    // safe to send small packets in UDP.
    bootstrap.setOption("receiveBufferSizePredictorFactory", new FixedReceiveBufferSizePredictorFactory(BUFFER_SIZE));

    // Bind to the port and start the service.
    bootstrap.bind(new InetSocketAddress(serverPort));
  }

  @Override
  public void shutdown() {
    if (bootstrap != null) {
      bootstrap.shutdown();
      bootstrap = null;
    }
  }

  @Override
  public ByteOrder getByteOrder() {
    return byteOrder;
  }

  @Override
  public void addListener(UdpServerNetworkCommunicationEndpointListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(UdpServerNetworkCommunicationEndpointListener listener) {
    listeners.remove(listener);
  }

  @Override
  public int getServerPort() {
    return serverPort;
  }

  @Override
  public String toString() {
    return "NettyUdpServerNetworkCommunicationEndpoint [serverPort=" + serverPort + "]";
  }

  /**
   * Handle the message received by the handler.
   *
   * @param event
   *          the event which happened
   */
  private void handleMessageReceived(MessageEvent event) {
    NettyUdpServerRequest request = new NettyUdpServerRequest(byteOrder, event);

    for (UdpServerNetworkCommunicationEndpointListener listener : listeners) {
      listener.onUdpRequest(this, request);
    }
  }

  /**
   * Netty handler for incoming UDP requests.
   *
   * @author Keith M. Hughes
   */
  public class NettyUdpServerHandler extends SimpleChannelUpstreamHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
      handleMessageReceived(e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
      log.error("Error during netty UDP server handler processing", e.getCause());
    }
  }

  /**
   * Netty-based version of the {@link UdpServerRequest}.
   *
   * @author Keith M. Hughes
   */
  private static class NettyUdpServerRequest implements UdpServerRequest {

    /**
     * Byte order for writeable packets.
     */
    private final ByteOrder byteOrder;

    /**
     * The message event from the request.
     */
    private final MessageEvent event;

    /**
     * Construct a new server request.
     *
     * @param byteOrder
     *          byte order for packets
     * @param event
     *          the Netty message event which came in
     */
    public NettyUdpServerRequest(ByteOrder byteOrder, MessageEvent event) {
      this.byteOrder = byteOrder;
      this.event = event;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
      return (InetSocketAddress) event.getRemoteAddress();
    }

    @Override
    public byte[] getRequest() {
      return ((ChannelBuffer) event.getMessage()).array();
    }

    @Override
    public ByteBuffer newRequestByteBuffer() {
      return ByteBuffer.wrap(getRequest()).order(byteOrder);
    }

    @Override
    public void writeResponse(byte[] response) {
      ChannelBuffer responseBuffer = ChannelBuffers.copiedBuffer(response);
      event.getChannel().write(responseBuffer, event.getRemoteAddress());
    }

    @Override
    public WriteableUdpPacket newDynamicWriteableUdpPacket() {
      return new NettyWriteableUdpPacket((DatagramChannel) event.getChannel(), ChannelBuffers.dynamicBuffer(byteOrder,
          NettyWriteableUdpPacket.DYNAMIC_BUFFER_INITIAL_SIZE));
    }

    @Override
    public WriteableUdpPacket newWriteableUdpPacket(int size) {
      return new NettyWriteableUdpPacket((DatagramChannel) event.getChannel(), ChannelBuffers.buffer(byteOrder, size));
    }
  }
}
