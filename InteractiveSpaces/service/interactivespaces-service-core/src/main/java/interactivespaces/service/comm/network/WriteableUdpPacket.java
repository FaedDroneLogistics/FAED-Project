/*
 * Copyright (C) 2014 Google Inc.
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

package interactivespaces.service.comm.network;

import java.net.InetSocketAddress;

/**
 * A UDP packet that will be written out.
 *
 * @author Keith M. Hughes
 */
public interface WriteableUdpPacket {

  /**
   * Write out the packet to the remote address.
   *
   * @param remoteAddress
   *          address where the packet will be sent
   */
  void write(InetSocketAddress remoteAddress);

  /**
   * Write out the lowest byte of the int into the packet.
   *
   * @param value
   *          the value to write
   *
   * @return this packet
   *
   * @throws IndexOutOfBoundsException
   *           if there is not enough room in the packet for the data
   */
  WriteableUdpPacket writeByte(int value);

  /**
   * Write the lower 16 bits of the int into the packet.
   *
   * @param value
   *          the value to write
   *
   * @return this packet
   *
   * @throws IndexOutOfBoundsException
   *           if there is not enough room in the packet for the data
   */
  WriteableUdpPacket writeShort(int value);

  /**
   * Write the lower 24 bits of the int into the packet.
   *
   * @param value
   *          the value to write
   *
   * @return this packet
   *
   * @throws IndexOutOfBoundsException
   *           if there is not enough room in the packet for the data
   */
  WriteableUdpPacket writeMedium(int value);

  /**
   * Write the int into the packet.
   *
   * @param value
   *          the value to write
   *
   * @return this packet
   *
   * @throws IndexOutOfBoundsException
   *           if there is not enough room in the packet for the data
   */
  WriteableUdpPacket writeInt(int value);

  /**
   * Write the long into the packet.
   *
   * @param value
   *          the value to write
   *
   * @return this packet
   *
   * @throws IndexOutOfBoundsException
   *           if there is not enough room in the packet for the data
   */
  WriteableUdpPacket writeLong(long value);

  /**
   * Write the lower 16 bits of the int as a UTF-16 character in the packet.
   *
   * @param value
   *          the value to write
   *
   * @return this packet
   *
   * @throws IndexOutOfBoundsException
   *           if there is not enough room in the packet for the data
   */
  WriteableUdpPacket writeChar(int value);

  /**
   * Write the floating point number in the packet.
   *
   * @param value
   *          the value to write
   *
   * @return this packet
   *
   * @throws IndexOutOfBoundsException
   *           if there is not enough room in the packet for the data
   */
  WriteableUdpPacket writeFloat(float value);

  /**
   * Write the double in the packet.
   *
   * @param value
   *          the value to write
   *
   * @return this packet
   *
   * @throws IndexOutOfBoundsException
   *           if there is not enough room in the packet for the data
   */
  WriteableUdpPacket writeDouble(double value);

  /**
   * Write the entire byte array into the packet.
   *
   * @param src
   *          the byte array
   *
   * @return this packet
   *
   * @throws IndexOutOfBoundsException
   *           if there is not enough room in the packet for the data
   */
  WriteableUdpPacket writeBytes(byte[] src);

  /**
   * Write part of a byte array into the packet.
   *
   * @param src
   *          the source bytes
   * @param srcIndex
   *          the first index of the source
   * @param length
   *          the number of bytes to transfer
   *
   * @return this packet
   *
   * @throws IndexOutOfBoundsException
   *           if there is not enough room in the packet for the data
   */
  WriteableUdpPacket writeBytes(byte[] src, int srcIndex, int length);

  /**
   * Get the current size of the packet.
   *
   * @return current size in bytes
   */
  int getPacketSize();
}
