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

package org.ros.rosjava_geometry;

import org.ros.message.Time;
import org.ros.namespace.GraphName;

/**
 * A transformation in terms of translation and rotation.
 * 
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
public class Transform {

  private Vector3 translation;
  private Quaternion rotation;

  public Transform(Vector3 translation, Quaternion rotation) {
    this.setTranslation(translation);
    this.setRotation(rotation);
  }

  public Transform multiply(Transform other) {
    return new Transform(transformVector(other.getTranslation()),
        transformQuaternion(other.getRotation()));
  }

  public Transform invert() {
    Quaternion inverseRotation = rotation.invert();
    return new Transform(inverseRotation.rotateVector(translation.invert()), inverseRotation);
  }

  public Vector3 transformVector(Vector3 vector) {
    return translation.add(rotation.rotateVector(vector));
  }

  public Quaternion transformQuaternion(Quaternion quaternion) {
    return rotation.multiply(quaternion);
  }

  public org.ros.message.geometry_msgs.Transform toTransformMessage() {
    org.ros.message.geometry_msgs.Transform result = new org.ros.message.geometry_msgs.Transform();
    result.translation = translation.toVector3Message();
    result.rotation = rotation.toQuaternionMessage();
    return result;
  }

  public org.ros.message.geometry_msgs.TransformStamped toTransformStampedMessage(GraphName frame,
      GraphName childFrame, Time stamp) {
    org.ros.message.geometry_msgs.TransformStamped result =
        new org.ros.message.geometry_msgs.TransformStamped();
    result.header.frame_id = frame.toString();
    result.header.stamp = stamp;
    result.child_frame_id = childFrame.toString();
    result.transform.translation = translation.toVector3Message();
    result.transform.rotation = rotation.toQuaternionMessage();
    return result;
  }

  public org.ros.message.geometry_msgs.Pose toPoseMessage() {
    org.ros.message.geometry_msgs.Pose result = new org.ros.message.geometry_msgs.Pose();
    result.position = translation.toPointMessage();
    result.orientation = rotation.toQuaternionMessage();
    return result;
  }

  public org.ros.message.geometry_msgs.PoseStamped
      toPoseStampedMessage(GraphName frame, Time stamp) {
    org.ros.message.geometry_msgs.PoseStamped result =
        new org.ros.message.geometry_msgs.PoseStamped();
    result.header.frame_id = frame.toString();
    result.header.stamp = stamp;
    result.pose.position = translation.toPointMessage();
    result.pose.orientation = rotation.toQuaternionMessage();
    return result;
  }

  public Vector3 getTranslation() {
    return translation;
  }

  public void setTranslation(Vector3 translation) {
    this.translation = translation;
  }

  public Quaternion getRotation() {
    return rotation;
  }

  public void setRotation(Quaternion rotation) {
    this.rotation = rotation;
  }

  public static Transform newFromTransformMessage(org.ros.message.geometry_msgs.Transform message) {
    return new Transform(Vector3.newFromVector3Message(message.translation),
        Quaternion.newFromQuaternionMessage(message.rotation));
  }

  public static Transform newFromPoseMessage(org.ros.message.geometry_msgs.Pose message) {
    return new Transform(Vector3.newFromPointMessage(message.position),
        Quaternion.newFromQuaternionMessage(message.orientation));
  }

  public static Transform newIdentityTransform() {
    return new Transform(Vector3.newIdentityVector3(), Quaternion.newIdentityQuaternion());
  }

  @Override
  public String toString() {
    return String.format("Transform<%s, %s>", translation, rotation);
  }
}
