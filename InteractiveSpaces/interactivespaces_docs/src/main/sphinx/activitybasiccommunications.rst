Basic Interactive Spaces Communications
***************************************

Bringing Live Activities up and down is nice and all, but if you really
want interesting behaviors in your space, you need your Live Activities
to communicate with each other.

Interactive Spaces uses ROS to provide communication. Using ROS directly gives the
most power, but it is somewhat complex to use. So Interactive Spaces
provides a simpler-to-use mechanism called *routes* based on the popular communication
format JSON which is used in web applications for transferring name/value
pairs between Live Activities. Routes use ROS under the covers,
but other than a few configuration parameters that are ROS specific,
you need never think about ROS.

Direct use of ROS will be covered in another chapter.

Route Basics
==========

The message format for route communication is very simple,
a collection of name/value pairs.
The collection is anything serialization as a JSON message, in fact the message is transmitted
on the network as a JSON-encoded string.

The basic Interactive Spaces communication system uses ROS for Activity to Activity and Master
to Controller communication. ROS uses the concept of Global Topics, which you can think of
as a globally defined mailbox that anyone can write to and anyone can read from, as long as they
have the name of the topic.
``/example/routable/channel1`` is the global ROS Topic that many of the example routable
Activities in the Workbench can write to or read from. Every Activity that wants to use
the route to communicate must use the same name for the global topic.

Routes can have multiple activities writing information to the route and multiple activities
reading from that route. A given activity can write to the channel and never read from it
and visa versa. In the examples found in the Workbench you can see one example which only writes
on the route and another which only reads from the route.

Routes are used in Activities by giving them a name which is local to the Activity. If the
Activity wants to write to a route, it uses this local name, also called a Channel. This name
is different than the global topic name.

A lot of words... what does it all mean???

Configuring Routes
------------------

The example Workbench Activity ``interactivespaces.example.activity.routable.input``
reads from a route whose global topic name is ``/example/routable/channel1``.
The Activity has the following configuration parameters:

+-----------------------------------+----------------------------+
| space.activity.routes.inputs      | input1                     |
+-----------------------------------+----------------------------+
| space.activity.route.input.input1 | /example/routable/channel1 |
+-----------------------------------+----------------------------+

The example Workbench Activity ``interactivespaces.example.activity.routable.output``
writes to the same route. The Activity has the following configuration parameters:

+------------------------------------+----------------------------+
| space.activity.routes.outputs      | output1                    |
+------------------------------------+----------------------------+
| space.activity.route.input.output1 | /example/routable/channel1 |
+------------------------------------+----------------------------+

Notice that the configuration property ``space.activity.route.input.input1`` has the same
value as the configuration property ``space.activity.route.output.output1``. This means that
writing to channel ``output1`` in Activity
``interactivespaces.example.activity.routable.output``
will show up on channel ``input1`` in Activity
``interactivespaces.example.activity.routable.input``.

``input1`` and ``output1`` are examples of the local name part of a route. These names, once
again, are local to an Activity, and can be anything the activity wants it to be. Even names
like ``saxophone``, ``foo``, and ``television`` would be fine. Of course you should pick names
that actually mean something for what the route is used for.

The property ``space.activity.routes.inputs`` would contain the local names of all route channels
the activity will read from. Every channel to be read from will be listed in this property,
with each name separated by a ``:``. For example


+------------------------------+----------------+
| space.activity.routes.inputs | foo:bar:bletch |
+------------------------------+----------------+


would create input channels named ``foo``, ``bar``, and ``bletch``.

For each named input channel, there must be a corresponding property whose name
starts with ``space.activity.route.input``. For example, in the example above, we have an
input route named ``input1``, so we must have a property with the name
``space.activity.route.input.input1``. The value of this property would be the name of the
global topic for the route.

Multiple global topics can be listed as the value for the ``space.activity.route.input`` property, once
again separated by a ``:``. This means the channel will listen on all topics listed at the same
time.

Similarly

+-------------------------------+----------------+
| space.activity.routes.outputs | foo:bar:bletch |
+-------------------------------+----------------+

would create output channels named ``foo``, ``bar``, and ``bletch``.

For each named output channel, there must be a corresponding property whose name
starts with ``space.activity.route.output``. For example, in the example above, we have an
output route named ``output1``, so we must have a property with the name
``space.activity.route.output.output1``. The value of this property would be the name of the
global topic for the route.

Multiple global topics can be listed as the value for the ``space.activity.route.output`` property, once
again separated by a ``:``. This means the channel will write to all topics listed at the same
time.

Any Activity which uses ROS communication, remember that routes are implemented using ROS
communication, must have the ``space.activity.ros.node.name`` configuration property defined.
This name should be unique for your space, and one way to do that is to make it
a relative name, meaning don't start it with a ``/``. Names which start with a ``/`` are
absolute names, and should only be used if you know what you are doing and have a good reason
for it.

Using Routes In Code
====================


Routes with ``BaseRoutableRosActivity``
--------------------

The simplest way to use a route is to base your Activity on the ``BaseRoutableRosActivity``
Supported Activity class.

To read from the route, implement the ``onNewInputJson`` method. This method has two arguments,
one which gives the local name of the channel which received the message, and the second
which gives the map of name/value pairs from the message.

This method will be called for any incoming route messages, regardless of which route it came
from. Use the first argument to decide which route the message came from.

.. code-block:: java

    public class SimpleJavaRoutableInputActivity  extends BaseRoutableRosActivity {

        @Override
        public void onNewInputJson(String channelName, Map<String, Object> message) {
            getLog().info("Got message on input channel " + channelName);
            getLog().info(message);
        }
    }

To write to a route, create a map of name/value pairs and call the ``sendOutputJson`` method.
The first argument will be the name of the output channel you want to write to, the second argument
will be the map of name/value pairs to send.

.. code-block:: java

    public class SimpleJavaRoutableOutputActivity extends BaseRoutableRosActivity {

        @Override
        public void onActivityActivate() {
            Map<String, Object> message = Maps.newHashMap();
            message.put("message", "yipee! activated!");
            sendOutputJson("output1", message);
        }

        @Override
        public void onActivityDeactivate() {
            Map<String, Object> message = Maps.newHashMap();
            message.put("message", "bummer! deactivated!");
            sendOutputJson("output1", message);
        }
    }

An Advanced Example of Using Routes
-----------------------------------

It would be good to look at two example projects in the Workbench which
demonstrate a very common setup, a web browser Activity which is used to
control a remote Activity.

Look at the following two Activity examples in the workbench:

#. examples/basics/comm/interactivespaces.example.activity.routable.output.web
#. examples/basics/comm/interactivespaces.example.activity.routable.input.speech

The first example is a browser-based activity which will start up a web
server which serves a webapp and starts up a web browser which displays
the webapp to the user. This example shows how to create a web page
which can communicate or obtain information in a browser-based
interface. The browser speaks to the IS side via a web socket
connection.

The second example creates an instance of a speech synthesizer that
can speak text supplied to it.

The two activities talk to each other over a route.

The communication flow is as follows:

#. The user types in text to be spoken into a textbox in the web browser.
#. The user clicks the send button in the browser.
#. The contents of the text box is sent over a websocket connection to Activity 1.
#. Activity 1 packages up the information sent from the browser and sends it over a route to Activity 2.
#. Activity 2 receives the route message sent from Activity 1 and gives it to the speech synthesizer.

