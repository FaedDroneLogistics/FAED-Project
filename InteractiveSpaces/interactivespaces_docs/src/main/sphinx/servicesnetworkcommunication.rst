The Interactive Spaces Network Communication Services and Utilities
***************************************

Interactive Spaces comes with a variety of services and classes to help with network communication.
Network communication tools can be used for communicating with software applications that do not run
natively in Interactive Spaces, or can be used for communicating with hardware.

The Interactive Spaces services are easy to use and allow you to let Interactive Spaces take care
of resource management, such as shutting down connections and cleaning up resources when your activity is
done using the service.

TCP Communication
=================

Interactive Spaces has services which simplify the creation and usage of TCP-based network servers and clients.

TCP Clients
-----------

The TCP Client service makes it easy to create a TCP Client for accessing a remote TCP server. A given client
can only communicate with a single TCP server on the network. 

It is necessary to first get the TCP Client Service before creating the TCP Client. The service is
obtained from the Service Registry.

.. code-block:: java

    TcpClientNetworkCommunicationEndpointService tcpClientSevice = getSpaceEnvironment()
        .getServiceRegistry().getRequiredService(
            TcpClientNetworkCommunicationEndpointService.SERVICE_NAME);

This service can now create multiple clients to communicate with multiple servers.

One of the simplest clients to create is a client that communicates by transferring strings back
and forth. Clients are created with the 
``tcpClientService.newStringClient(byte[][] delimiters, Charset charset, InetAddress remoteHost, int remotePort, Log log)`` 
call.

``delimiters`` specifies all groups of characters that can terminate a complete message.
``charset`` defines the character set that the strings will be encoded in. ``remoteHost`` gives the host
for the TCP server to be contacted, and ``remotePort`` gives the port on the remote machine.
``log`` is the logger to use and is typically the activity's log.

The reason that ``delimiters`` is an array of arrays is because it is possible to have multiple
message terminator sequences.

The following call will create a client on the local machine at port 9000. Messages are
terminated with a newline.

.. code-block:: java

    TcpClientNetworkCommunicationEndpoint<String> tcpClient = tcpClientSevice.newStringClient(
        new byte[][] { new byte[] { '\n' } },
        Charsets.UTF_8,
        InetAddress.getByName("localhost"), 9000,
        getLog());

The client typically should be registered as a ManagedResource with the hosting activity so that it
will be automatically shut down and cleaned up when the activity shuts down.

.. code-block:: java

    TcpClientNetworkCommunicationEndpoint<String> tcpClient = tcpClientSevice.newStringClient(
        new byte[][] { new byte[] { '\n' } },
        Charsets.UTF_8,
        InetAddress.getByName("localhost"), 9000,
        getLog());
    addManagedResource(remoteTcpClient);

Sending packets to the remote server
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``write()`` method can be used to send TCP packets to the remote server.

.. code-block:: java

    tcpClient.write(message);

``message`` is a string. For example, if the message ``hello, remote server`` should be sent to
the remote server, you could use the call

.. code-block:: java

    tcpClient.write("hello, remote server\n");

Receiving responses from the remote server
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you want to receive responses back from the server, you must register an instance of 
``TcpClientNetworkCommunicationEndpointListener<String>`` with the client. The listener must implement the method
``onTcpResponse()``, which will be called whenever a new response packet comes in.

The method has the arguments
``onTcpResponse(TcpClientNetworkCommunicationEndpoint<String> endpoint, String response)``.

The ``endpoint`` argument is the TCP client that received the response. This argument is there so
that the listener can be registered with multiple clients. ``response`` is the data from the response
packet minus the delimiters set when the client was created.

Suppose, for example, you merely want to log the responses from the remote server.

.. code-block:: java

    tcpClient.addListener(new TcpClientNetworkCommunicationEndpointListener<String>() {
      @Override
      public void onTcpResponse(TcpClientNetworkCommunicationEndpoint<String> endpoint, 
          String response) {
        getLog().info("Got response " + response);
      }
    });

TCP Servers
-----------

The TCP Server service can be used to easily create TCP servers.

It is necessary to first get the TCP Server Service before creating the TCP server. The service is
obtained from the Service Registry.

.. code-block:: java

    TcpServerNetworkCommunicationEndpointService tcpServerSevice = getSpaceEnvironment()
        .getServiceRegistry().getRequiredService(
            TcpServerNetworkCommunicationEndpointService.SERVICE_NAME);

One of the simplest servers to create is a server that communicates by transferring strings back
and forth. Servers are created with the 
``tcpServerService.newStringServer(byte[][] delimiters, Charset charset, int serverPort, Log log)``
call.

``delimiters`` specifies all groups of characters that can terminate a complete message.
``charset`` defines the character set that the strings will be encoded in. ``remotePort`` gives 
the port on which the server will listen.
``log`` is the logger to use and is typically the activity's log.

The reason that the ``delimiters`` is an array of arrays is because it is possible to have multiple
message terminator sequences.

The following call will create a server on the local machine at port 9000. Messages are
terminated with a newline.

.. code-block:: java

    TcpServerNetworkCommunicationEndpoint<String> tcpClient = tcpClientSevice.newStringClient(
        new byte[][] { new byte[] { '\n' } },
        Charsets.UTF_8,
        9000,
        getLog());

The server typically should be registered as a ManagedResource with the hosting activity so that it
will be automatically shut down and cleaned up when the activity shuts down.

.. code-block:: java

    TcpServerNetworkCommunicationEndpoint<String> tcpServer = tcpServerSevice.newStringServer(
        new byte[][] { new byte[] { '\n' } },
        Charsets.UTF_8,
        9000,
        getLog());
    addManagedResource(remoteTcpServer);

Receiving Requests
~~~~~~~~~~~~~~~~~~

The server needs at least one instance of the ``TcpServerNetworkCommunicationEndpointListener`` class
in order to process client requests. The listener must implement the method
``onTcpRequest()``, which will be called whenever a new request packet comes in.

The method for a string server has the arguments
``onTcpRequest(TcpServerNetworkCommunicationEndpoint<String> endpoint, TcpServerRequest request)``.

The ``endpoint`` argument gives a reference to the UDP server that received the message, so that a
single listener can be used for multiple servers. ``request`` is the actual request that has come into the server.

The ``request.getRemoteAddress()`` will get an instance of the Java ``InetSocketAddress`` class. This
gives information about which client sent the request to the server.

``request.getRequest()`` returns a string containing the contents of the TCP request.

The following shows one way of creating a listener.

.. code-block:: java

    tcpServer.addListener(new TcpServerNetworkCommunicationEndpointListener<String>() {
          @Override
          public void onTcpRequest(
              TcpServerNetworkCommunicationEndpoint<String> endpoint,
              TcpServerRequest request) {
            // ...handle request...
          }
        });

Sending back a response
~~~~~~~~~~~~~~~~~~~~~~~

``request.writeMessage(String message)`` will send back a response to the client that sent the request.
For example, if the server wanted to send back the response "Well, hello to you, too!", it could use the call

.. code-block:: java

    request.writeMessage("Well, hello to you, too!");

UDP Communication
=================

Interactive Spaces has services which simplify the creation and usage of UDP-based network servers and clients,
including UDP Broadcast clients.

UDP Clients
-----------

The UDP Client service makes it easy to create a UDP Client for accessing a remote UDP server. A given client
can communicate with multiple UDP servers on the network, it is not tied to just one server. 

It is necessary to first get the UDP Client Service before creating the UDP Client. The service is
obtained from the Service Registry.

.. code-block:: java

    UdpClientNetworkCommunicationEndpointService udpClientSevice = getSpaceEnvironment()
        .getServiceRegistry().getRequiredService(UdpClientNetworkCommunicationEndpointService.SERVICE_NAME);

It is now possible to create as many clients as are needed. However, since a single client can talk to multiple servers, only one
client is generally needed.

.. code-block:: java

    UdpClientNetworkCommunicationEndpoint remoteUdpClient = udpClientSevice.newClient(getLog());

The client typically should be registered as a ManagedResource with the hosting activity so that it
will be automatically shut down and cleaned up when the activity shuts down.

.. code-block:: java

    UdpClientNetworkCommunicationEndpoint remoteUdpClient = udpClientSevice.newClient(getLog());
    addManagedResource(remoteUdpClient);

A socket address needs to be specified for any remote UDP server to be addressed.

.. code-block:: java

    InetSocketAddress remoteUdpServerAddress =
        new InetSocketAddress(remoteUdpServerHost, remoteUdpServerPort);

``remoteUdpServerHost`` is a string giving either the DNS name or IP address for the remote server,
while ``remoteUdpServerPort`` is an integer giving the network port for the remote server.

Sending simple packets to the remote server
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``write()`` method can be used to send UDP packets to the remote server.

.. code-block:: java

    remoteUdpClient.write(remoteUdpServerAddress, message);

``message`` is a ``byte`` array. For example, if the message ``hello, remote server`` should be sent to
the remote server, you could use the call

.. code-block:: java

    remoteUdpClient.write(remoteUdpServerAddress, "hello, remote server".getBytes());

Receiving responses from the remote server
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you want to receive responses back from the server, you must register an instance of 
``UdpClientNetworkCommunicationEndpointListener`` with the client. The listener must implement the method
``onUdpResponse()``, which will be called whenever a new response packet comes in.

The method has the arguments
``onUdpResponse(UdpClientNetworkCommunicationEndpoint endpoint, byte[] response, InetSocketAddress remoteAddress)``.

The ``endpoint`` argument is the UDP client that received the response. This argument is there so
that the listener can be registered with multiple clients. ``response`` is the raw data from the response
packet. ``remoteAddress`` is the network address of the UDP server that sent the response.

Suppose, for example, you merely want to log the responses from the remote server, and the responses are
known to be strings.

.. code-block:: java

    remoteUdpClient.addListener(new UdpClientNetworkCommunicationEndpointListener() {
      @Override
      public void onUdpResponse(UdpClientNetworkCommunicationEndpoint endpoint, byte[] response,
          InetSocketAddress remoteAddress) {
        getLog().info("Got response " + new String(response));
      }
    });

More complex messaging
~~~~~~~~~~~~~~~~~~~~~~

So far we have only discussed sending raw byte packets. However, it is possible to easily send data packets
made of ints, longs, floats, and byte arrays.

If you are going to use the higher level UDP packets, you must understand the byte order of the remote UDP
server. This byte order is used to specify whether numbers are sent in *big-endian* or *little-endian*
order. The byte order is specified for the client when it is created. For instance, if the remote server
is big-endian, use

.. code-block:: java

    UdpClientNetworkCommunicationEndpoint remoteUdpClient =
        udpClientSevice.newClient(ByteOrder.BIG_ENDIAN, getLog());

The other byte order is ``ByteOrder.LITTLE_ENDIAN``. The default, if you don't specify a byte ordering
when creating the client, is big-endian.

Are the UDP packets your client needs to send of a fixed
length or can they have a variable length? If they are fixed length, you create the packet with the 
following method.

.. code-block:: java

    WriteableUdpPacket packet = remoteUdpClient.newWriteableUdpPacket(int size);

where ``size`` specifies the size of the packet in bytes.

if the packets can be of arbitrary length, use a dynamic packet.

.. code-block:: java

    WriteableUdpPacket packet = remoteUdpClient.newDynamicWriteableUdpPacket();

It is now possible to write data into the packet.

``packet.writeByte(int value)`` will write the lower 8 bits of ``value`` into the packet.

``packet.writeShort(int value)`` will write the lower 16 bits of ``value`` into the packet.

``packet.writeMedium(int value)`` will write the lower 24 bits of ``value`` into the packet.

``packet.writeInt(int value)`` will write all 32 bits of ``value`` into the packet.

``packet.writeLong(long value)`` will write all 64 bits of ``value`` into the packet.

``packet.writeChar(int value)`` will write the lower 16 bits of ``value`` as a UTF-16 character
into the packet.

``packet.writeFloat(float value)`` will write all 32 bits of ``value`` into the packet.

``packet.writeDouble(double value)`` will write all 64 bits of ``value`` into the packet.

``packet.writeBytes(byte[] src))`` will write all bytes from the ``value`` array into the packet.

``packet.writeBytes(byte[] src, int srcIndex, int length)`` will write ``length`` bytes from the ``value``
array into the packet, starting at position ``srcIndex``.

You should call the methods that write data to the packet in the order in which the data should appear in the packet.
Then send the packet to the remote server with the ``write()`` method.

.. code-block:: java

    packet.write(remoteUdpServerAddess);



UDP Servers
-----------

The UDP Server service can be used to easily create UDP servers.

It is necessary to first get the UDP Server Service before creating the UDP server. The service is
obtained from the Service Registry.

.. code-block:: java

    UdpServerNetworkCommunicationEndpointService udpServerSevice = getSpaceEnvironment()
        .getServiceRegistry().getRequiredService(UdpServerNetworkCommunicationEndpointService.SERVICE_NAME);

A UDP server can now be created from the service.

.. code-block:: java
            
    UdpServerNetworkCommunicationEndpoint localUdpServer = udpServerSevice
        .newServer(localPort, getLog());

``localPort`` is an integer saying which port on the local machine the server will listen on.

Typically the server should then be registered as a ManagedResource with the hosting activity so that it
will be automatically shut down and cleaned up when the activity shuts down.

.. code-block:: java
            
    UdpServerNetworkCommunicationEndpoint localUdpServer = udpServerSevice
        .newServer(localPort, getLog());
    addManagedResource(localUdpServer);

Receiving Requests
~~~~~~~~~~~~~~~~~~

The server needs at least one instance of the ``UdpServerNetworkCommunicationEndpointListener`` class
in order to process client requests. The listener must implement the method
``onUdpRequest()``, which will be called whenever a new request packet comes in.

The method has the arguments
``onUdpRequest(UdpServerNetworkCommunicationEndpoint endpoint, UdpServerRequest request)``.

The ``endpoint`` argument gives a reference to the UDP server that received the message, so that a
single listener can be used for multiple servers. ``request`` is the actual request that has come into the server.

The ``request.getRemoteAddress()`` will get an instance of the Java ``InetSocketAddress`` class. This
gives information about which client sent the request to the server.

``request.getRequest()`` returns a ``byte`` array containing the contents of the UDP request. This array
will have to be processed in some way to get the contents of the message.

The following shows one way of creating a listener.

.. code-block:: java

    localUdpServer.addListener(new UdpServerNetworkCommunicationEndpointListener() {
          @Override
          public void onUdpRequest(
              UdpServerNetworkCommunicationEndpoint endpoint,
              UdpServerRequest request) {
            // ...handle request...
          }
        });

Sending back a response
~~~~~~~~~~~~~~~~~~~~~~~

There are two ways of sending a response back to the client.

``request.writeResponse(byte[] message)`` will send back a response to the client that sent the request.
For example, if the server wanted to send back the response "Well, hello to you, too!", it could use the call

.. code-block:: java

    request.writeResponse("Well, hello to you, too!".getBytes());

It is possible to use the ``WriteableUdpPacket`` class with a UDP server request. First of all, you must
decide whether your server should be big-endian or little-endian. For example, a little-endian server is
created with the following call:


.. code-block:: java
            
    UdpServerNetworkCommunicationEndpoint localUdpServer = udpServerSevice
        .newServer(localPort, ByteOrder.LITTLE_ENDIAN, getLog());

The default UDP server is big-endian.

Are the UDP packets your server needs to send back as a response of a fixed
length or can they have a variable length? If they are fixed length, you create the response packet with the 
following method.

.. code-block:: java

    WriteableUdpPacket packet = request.newWriteableUdpPacket(int size);

where ``size`` specifies the size of the packet in bytes.

if the packets can be of arbitrary length, use a dynamic packet.

.. code-block:: java

    WriteableUdpPacket packet = request.newDynamicWriteableUdpPacket();

It is now possible to write data into the packet.

``packet.writeByte(int value)`` will write the lower 8 bits of ``value`` into the packet.

``packet.writeShort(int value)`` will write the lower 16 bits of ``value`` into the packet.

``packet.writeMedium(int value)`` will write the lower 24 bits of ``value`` into the packet.

``packet.writeInt(int value)`` will write all 32 bits of ``value`` into the packet.

``packet.writeLong(long value)`` will write all 64 bits of ``value`` into the packet.

``packet.writeChar(int value)`` will write the lower 16 bits of ``value`` as a UTF-16 character
into the packet.

``packet.writeFloat(float value)`` will write all 32 bits of ``value`` into the packet.

``packet.writeDouble(double value)`` will write all 64 bits of ``value`` into the packet.

``packet.writeBytes(byte[] src))`` will write all bytes from the ``value`` array into the packet.

``packet.writeBytes(byte[] src, int srcIndex, int length)`` will write ``length`` bytes from the ``value``
array into the packet, starting at position ``srcIndex``.

You should call the methods that write data to the packet in the order in which the data should appear in the packet.
Then send the packet to the remote server with the ``write()`` method.

.. code-block:: java

    packet.write(request.getRemoteAddress());

Web Communication
=================

The Web Server Service
----------------------

The Interactive Spaces Web Server service allows you to easily create web servers with little or no configuration.
The Interactive Spaces Web Server supports

* Standard HTTP GET and POST requests
* Easy to use handlers for static and dynamic content
* Web socket server support
* Automatic MIME type resolution for content being served
* HTTPS connections


The Web Socket Client Service
-----------------------------

The Interactive Spaces Web Socket Client Service makes it easy to create Web Socket clients that can communicate
with remote web socket-based services. The service takes care of the connections to the remote server and handles
all message serialization and deserialization.

The HttpContentCopier
---------------------

The ``HttpContentCopier`` interface provides a way of retrieving content from a remote HTTP server into a string
or into a file in the file system. It can also be used to ``POST`` a file to a remote HTTP server.

Instances of the ``HttpContentCopier`` interface are instances of ``ManagedResource`` and their lifecycle can
be controlled by an Activity through the ``addManagedResource()`` call.

The only current implementation of ``HttpContentCopier`` is 
``interactivespaces.util.web.HttpClientHttpContentCopier``, which uses the Apache Commons HttpClient libraries.

.. code-block:: java

  HttpContentCopier copier = new HttpClientHttpContentCopier();

By default this will give you the possibility of 20 simultaneous connections. If you need more you can specify
how many you need in the constructor. The example below gives you 100 connections.


.. code-block:: java

  HttpContentCopier copier = new HttpClientHttpContentCopier(100);

Normally you then register this as a ManagedResource with your activity.


.. code-block:: java

  HttpContentCopier copier = new HttpClientHttpContentCopier();
  addManagedResource(copier);

Copying Content From a Remote Server
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Using the copier to copy content from a remote HTTP server is very simple. The call

.. code-block:: java

  copier.copy("http://www.foo.com/glorg", getActivityFilesystem().getPermanentDataFile("banana")));

will take the content found at ``http://www.foo.com/glorg`` and copy it to the file ``banana`` in the
activity's permanent data folder. If the URL
is malformed, the remote server does not return a code ``200`` for the content, or something bad happens
during the copy, the call will throw an ``InteractiveSpacesException``.

You can get the remote content as a Java string. For this you use the call

.. code-block:: java

  String content = copier.getContentAsString("http://www.foo.com/glorg");

This assumes the content is in UTF-8. If you need to specify the character set of the content, you can 
specify the charset in the call. For example, if the content is in UTF-16, you could use

.. code-block:: java

  String content = copier.getContentAsString("http://www.foo.com/glorg", Charset.forName("UTF-16"));


Copying Content To a Remote Server
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The copier can also copy content to a remote HTTP server. It does this by creating an 
HTTP ``multipart`` ``POST`` that contains the file as part of the post.

There are two ways to do this. The first takes a file as the source of content to copy.

.. code-block:: java

  copier.copyTo("http://www.foo.com/meef", new File("/var/tmp/foo", "bar", null);

This call will post to the destination URL ``http://www.foo.com/meef``. The file to be copied is
``/var/tmp/foo``. The file will be given the ``POST`` parameter name of ``bar``. No other ``POST`` parameters
will be added.

The call

.. code-block:: java

  Map<String, String> parameters = Maps.newHashMap();
  parameters.put("name", "Me");
  parameters.put("quality", "not so bad");
  copier.copyTo("http://www.foo.com/meef", new File("/var/tmp/foo", "bar", parameters);

will do the same thing as above, but will add the contents of the ``parameters`` map as ``POST`` parameters.

You can also send content from an arbitrary ``java.io.InputStream`` by replacing the second argument of the
``copyTo()`` call with the input stream you want to use. The copier will take care of closing the input stream
after the copy succeeds or fails.


.. code-block:: java

  InputStream in = ... get from somewhere ...
  copier.copyTo("http://www.foo.com/meef", in, "bar", null);


The UrlReader
^^^^^^^^^^^^^

The ``UrlReader`` class provides a safe way to read content accessible with a URL. It
makes sure that all resources are properly cleaned up after the reader completes, even
after error conditions. It provides a ``BufferedReader`` for processing the content, making it
easy to process the content one line at a time.

Email
=====

Interactive Spaces has the ability for you to send and receive email from your spaces.

Sending Email
-------------

Interactive Spaces has the ability to send email through the Mail Sender Service.
With this service you can compose an email and then send it.

An example showing how to use the service is given below.

.. code-block:: java

  MailSenderService mailSenderService = 
      getSpaceEnvironment().getServiceRegistry().getService(MailSenderService.SERVICE_NAME);

  ComposableMailMessage message = new SimpleMailMessage();
  message.setSubject("Greetings");
  message.setFromAddress("the-space@interactivespaces.com");
  message.addToAddress("you@you.com");
  message.setBody("Hello World");
  
  mailSenderService.sendMailMessage(message);
  

For more details about what you can do with the Mail Sender Service, see the
:javadoc:`interactivespaces.service.mail.sender.MailSenderService` 
Javadoc.

Configuring the Mail Sender Service
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Mail Sender Service needs to be configured properly if it going to
be able to send mail. Configurations for the mail service should be placed in the
``config/interactivespaces`` directory. These configurations are usually placed in a
file called ``mail.conf``.

The mail sender service needs to know an SMTP server that it can use
to transport the mail to its destination. The SMTP
server host is set with the ``interactivespaces.service.mail.sender.smtp.host`` configuration
property. The port of the SMTP server is set with the
``interactivespaces.service.mail.sender.smtp.port`` configuration property.

An example would be

::

  interactivespaces.service.mail.sender.smtp.host=172.22.58.11
  interactivespaces.service.mail.sender.smtp.port=25


Receiving Email
---------------

Interactive Spaces can also receive email through the Email Receiver Service.
This service sets up a very simple SMTP server that can receive emails when properly
configured. Event listeners are registered with the service that have methods
that are called when an email is received.

n example showing how to use the service is given below.

.. code-block:: java

  MailReceiverService mailReceiverService = 
      getSpaceEnvironment().getServiceRegistry().getService(MailReceiverService.SERVICE_NAME);

  mailReceiverService.addListener(new MailReceiverListener() {
    public void onMailMessageReceive(MailMessage message) {
      getLog().info("Received mail from " + message.getFromAddress();
    }
  });

The example listener merely prints the from address from the received email
and nothing else.

A listener can be removed with the ``removeListener()`` method on the service.

For more details about what you can do with the Mail Receiver Service, see the
:javadoc:`interactivespaces.service.mail.receiver.MailReceiverService` 
Javadoc.


Configuring the Mail Receiver Service
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Mail Receiver Service normally listens for SMTP traffic on port
``9999``. It can be reconfigured. Configurations for mail services should 
be placed in the``config/interactivespaces`` directory. These configurations are usually 
placed in a file called ``mail.conf``, the same file as the Email Sender configurations.

The port of the SMTP receiver is set with the
``interactivespaces.service.mail.receiver.smtp.port`` configuration property.

An example would be

::

  interactivespaces.service.mail.receiver.smtp.port=10000

Misc
====

The following are network based services that don't fit into a particular category.

Chat Service
------------

The Chat Service provides support for both reading from and writing to chat services.
The current implementation only supports XMPP-based chat.

For more details about what you can do with the Chat Service, see the
:javadoc:`interactivespaces.service.comm.chat.ChatService` 
Javadoc.

Twitter Service
---------------

The Twitter Service provides support for both sending Twitter Status updates and
being notified of any tweets containing a specified hashtag.

For more details about what you can do with the Chat Service, see the
:javadoc:`interactivespaces.service.comm.twitter.TwitterService` 
Javadoc.

Open Sound Control
------------------

Interactive Spaces includes support for building both Open Sound Control servers and clients. The current implementations
only support UDP connections.

For more details about what you can do with the Open Sound Control Services, see the
:javadoc:`interactivespaces.service.control.opensoundcontrol` 
Javadoc.
