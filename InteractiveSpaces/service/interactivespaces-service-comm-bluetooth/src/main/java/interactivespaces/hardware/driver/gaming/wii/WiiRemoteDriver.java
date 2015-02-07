/*
 * Copyright (C) 2012 Google Inc.
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

package interactivespaces.hardware.driver.gaming.wii;

import com.google.common.collect.Lists;

import interactivespaces.InteractiveSpacesException;
import interactivespaces.hardware.driver.DriverSupport;
import interactivespaces.service.comm.serial.bluetooth.BluetoothCommunicationEndpoint;
import interactivespaces.service.comm.serial.bluetooth.BluetoothCommunicationEndpointService;

import java.util.List;
import java.util.concurrent.Future;

/**
 * A driver for the Wii remote.
 *
 * @author Keith M. Hughes
 */
public class WiiRemoteDriver extends DriverSupport {

  /**
   * The Bluetooth report for reading from the Wii remote.
   */
  public static final int WII_REMOTE_RECEIVE_PORT = 13;

  /**
   * The Bluetooth report for writing to the Wii remote.
   */
  public static final int WII_REMOTE_SEND_PORT = 11;

  /**
   * Command for setting the lights.
   */
  private static final byte COMMAND_LIGHT = 0x11;

  private static final byte[] FULL_COMMAND_LIGHT_0 = { 0x52, COMMAND_LIGHT, 0x10 };
  private static final byte[] FULL_COMMAND_LIGHT_1 = { 0x52, COMMAND_LIGHT, 0x20 };
  private static final byte[] FULL_COMMAND_LIGHT_2 = { 0x52, COMMAND_LIGHT, 0x40 };
  private static final byte[] FULL_COMMAND_LIGHT_3 = { 0x52, COMMAND_LIGHT, (byte) 0x80 };

  private static final byte[][] FULL_COMMAND_LIGHTS = { FULL_COMMAND_LIGHT_0, FULL_COMMAND_LIGHT_1,
      FULL_COMMAND_LIGHT_2, FULL_COMMAND_LIGHT_3 };

  /**
   * The command for telling the remote to calibrate.
   */
  private static byte COMMAND_READ_CALIBRATION = 0x17;

  /**
   * The full command for reading remote calibration.
   */
  private static byte[] FULL_COMMAND_READ_CALIBRATION = new byte[] { 0x52,
      COMMAND_READ_CALIBRATION, 0x00, 0x00, 0x00, 0x16, 0x00, 0x08 };

  /**
   * Command for setting the reporting that the rmeote provides.
   */
  private static final int COMMAND_SET_REPORT = 0x52;

  /**
   * The calibration response report.
   */
  private static final byte REPORT_CALIBRATION_RESPONSE = 0x21;

  /**
   * A button event only is being reported.
   */
  private static final byte REPORT_BUTTON_ONLY = 0x30;

  /**
   * A button and accelerometer event are being reported.
   */
  private static final byte REPORT_BUTTON_ACCELEROMETER = 0x31;

  /**
   * The full command for setting the report to button only.
   */
  private static final byte[] FULL_COMMAND_SET_REPORT_BUTTON = new byte[] { COMMAND_SET_REPORT,
      0x12, 0x00, REPORT_BUTTON_ONLY };

  /**
   * The full command for setting the report to button and accelerometer.
   */
  private static final byte[] FULL_COMMAND_SET_REPORT_BUTTON_ACCELEROMETER = new byte[] {
      COMMAND_SET_REPORT, 0x12, 0x00, REPORT_BUTTON_ACCELEROMETER };

  /**
   * The Bluetooth address for the remote.
   */
  private String address;

  /**
   * The endpoint for speaing with the remote.
   */
  private BluetoothCommunicationEndpoint remoteEndpoint;

  /**
   * The reader future.
   */
  private Future<?> readerFuture;

  /**
   * Denominator for the X calibration for the accelerometer.
   */
  private double calibrationX;

  /**
   * Denominator for the Y calibration for the accelerometer.
   */
  private double calibrationY;

  /**
   * Denominator for the Z calibration for the accelerometer.
   */
  private double calibrationZ;

  /**
   * 0 point for the X calibration for the accelerometer.
   */
  private double calibrationX0;

  /**
   * 0 point for the Y calibration for the accelerometer.
   */
  private double calibrationY0;

  /**
   * 0 point for the Z calibration for the accelerometer.
   */
  private double calibrationZ0;

  /**
   * The list of all listeners for events.
   */
  private List<WiiRemoteEventListener> listeners = Lists.newArrayList();

  /**
   * Construct a driver for the given address.
   *
   * @param address
   *          the bluetooth address of the Wii Remote
   */
  public WiiRemoteDriver(String address) {
    this.address = address;
  }

  @Override
  public void startup() {
    BluetoothCommunicationEndpointService connectionEndpointService =
        spaceEnvironment.getServiceRegistry().getRequiredService(
            BluetoothCommunicationEndpointService.SERVICE_NAME);

    try {
      remoteEndpoint =
          connectionEndpointService.newDualEndpoint(address, WII_REMOTE_RECEIVE_PORT,
              WII_REMOTE_SEND_PORT);
      remoteEndpoint.startup();
    } catch (Exception e) {
      throw new InteractiveSpacesException("Unable to connect to device", e);
    }

    readerFuture = spaceEnvironment.getExecutorService().submit(new RemoteReader());

    readCalibration();
  }

  @Override
  public void shutdown() {
    if (readerFuture != null) {
      readerFuture.cancel(true);
      readerFuture = null;
    }

    if (remoteEndpoint != null) {
      remoteEndpoint.shutdown();
      remoteEndpoint = null;
    }
  }

  /**
   * Add a new event listener.
   *
   * @param listener
   *          the listener to add
   */
  public void addEventListener(WiiRemoteEventListener listener) {
    listeners.add(listener);
  }

  /**
   * Remove an event listener.
   *
   * <p>
   * Does nothing if listener not registered.
   *
   * @param listener
   *          the listener to remove
   */
  public void removeEventListener(WiiRemoteEventListener listener) {
    listeners.add(listener);
  }

  public void setAccelerometerReporting(boolean accelerometerReporting) {
    remoteEndpoint.write(accelerometerReporting ? FULL_COMMAND_SET_REPORT_BUTTON_ACCELEROMETER
        : FULL_COMMAND_SET_REPORT_BUTTON);
  }

  /**
   * Set a light on.
   *
   * @param light
   *          the light to turn on
   */
  public void setLight(int light) {
    if (light >= 0 && light <= 3) {
      try {
        remoteEndpoint.write(FULL_COMMAND_LIGHTS[light]);
      } catch (Exception e) {
        throw new InteractiveSpacesException("Error sending light command", e);
      }
    }
  }

  /**
   * Read the calibration from the remote
   */
  private void readCalibration() {
    try {
      remoteEndpoint.write(FULL_COMMAND_READ_CALIBRATION);
    } catch (Exception e) {
      throw new InteractiveSpacesException("Error sending read calibration command", e);
    }
  }

  /**
   * A button has been pressed. Notify all listeners.
   *
   * @param button
   *          the button which has been pressed
   */
  private void notifyButtonEvent(int button) {
    for (WiiRemoteEventListener listener : listeners) {
      listener.onWiiRemoteButtonEvent(button);
    }
  }

  /**
   * A button has been pressed. Notify all listeners.
   *
   * @param button
   *          the button which has been pressed
   * @param x
   *          the x component of the accelerometer
   * @param y
   *          the y component of the accelerometer
   * @param z
   *          the z component of the accelerometer
   */
  private void notifyButtonAccelerometerEvent(int button, double x, double y, double z) {
    for (WiiRemoteEventListener listener : listeners) {
      listener.onWiiRemoteButtonAccelerometerEvent(button, x, y, z);
    }
  }

  /**
   * The reader for information coming from the Wii
   *
   * @author Keith M. Hughes
   */
  private class RemoteReader implements Runnable {

    /**
     * The read buffer
     */
    private byte[] buffer = new byte[32];

    @Override
    public void run() {
      while (!Thread.interrupted()) {
        try {
          if (remoteEndpoint.read(buffer) == -1) {
            break;
          }

          switch (buffer[1]) {
            case REPORT_CALIBRATION_RESPONSE:
              decodeCalibrationResponse();

              break;

            case REPORT_BUTTON_ONLY:
              handleButtonOnlyEvent();

              break;

            case REPORT_BUTTON_ACCELEROMETER:
              handleButtonAccelerometerEvent();

              break;
          }
        } catch (Exception e) {
          log.error(String.format("Error while reading Wii remote with address %s", address), e);

          // TODO(keith): Decide whether to look at message of string
          // to decide if to break or not. If remote turned off,
          // Bluecove gives "Peer closed connection".
          break;
        }
      }
    }

    /**
     * Decode the calibration response.
     */
    private void decodeCalibrationResponse() {
      byte b10 = buffer[10];
      calibrationX0 = ((buffer[7] & 0xFF) << 2) + (b10 & 3);
      calibrationY0 = ((buffer[8] & 0xFF) << 2) + ((b10 & 0xC) >> 2);
      calibrationZ0 = ((buffer[9] & 0xFF) << 2) + ((b10 & 0x30) >> 4);

      byte b14 = buffer[14];
      double calibrationX1 = ((buffer[11] & 0xFF) << 2) + (b14 & 3);
      double calibrationY1 = ((buffer[12] & 0xFF) << 2) + ((b14 & 0xC) >> 2);
      double calibrationZ1 = ((buffer[13] & 0xFF) << 2) + ((b14 & 0x30) >> 4);

      calibrationX = calibrationX1 - calibrationX0;
      calibrationY = calibrationY1 - calibrationY0;
      calibrationZ = calibrationZ1 - calibrationX0;
    }

    /**
     * Handle a button only event.
     */
    private void handleButtonOnlyEvent() {
      int button = ((buffer[2] << 8) & 0x1f00) | (buffer[3] & 0x9f);
      notifyButtonEvent(button);
    }

    /**
     * Handle the full sensor event.
     */
    private void handleButtonAccelerometerEvent() {
      byte b2 = buffer[2];
      byte b3 = buffer[3];
      int button = ((b2 << 8) & 0x1f00) | (b3 & 0x9f);

      int x = ((buffer[4] & 0xff) << 2) + ((b2 >> 5) & 0x03);

      // Byte 3 has bit 1 of y and z. No bit 0 for y and z
      int y = ((buffer[5] & 0xff) << 2) + ((b3 >> 4) & 0x02);
      int z = ((buffer[6] & 0xff) << 2) + ((b3 >> 5) & 0x02);

      double xaccel = ((double) x - calibrationX0) / (calibrationX);
      double yaccel = ((double) y - calibrationY0) / (calibrationY);
      double zaccel = ((double) z - calibrationZ0) / (calibrationZ);

      notifyButtonAccelerometerEvent(button, xaccel, yaccel, zaccel);
    }

  }
}
