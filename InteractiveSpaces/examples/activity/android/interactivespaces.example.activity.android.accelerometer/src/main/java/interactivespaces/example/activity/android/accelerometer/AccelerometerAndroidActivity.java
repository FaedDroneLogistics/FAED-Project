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

package interactivespaces.example.activity.android.accelerometer;

import interactivespaces.activity.impl.ros.BaseRoutableRosActivity;
import interactivespaces.service.androidos.AndroidOsService;

import java.util.Map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.common.collect.Maps;

/**
 * A Interactive Spaces Android-based activity which reads the accelerometer and
 * publishes the accelerations on the sample route used for all the examples.
 * 
 * @author Keith M. Hughes
 */
public class AccelerometerAndroidActivity extends BaseRoutableRosActivity {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private SensorEventListener accelerometerEventListener;

	@Override
	public void onActivitySetup() {
		getLog().info(
				"Activity interactivespaces.example.activity.android.accelerometer setup");

		AndroidOsService androidService = getSpaceEnvironment()
				.getServiceRegistry().getService(AndroidOsService.SERVICE_NAME);
		sensorManager = (SensorManager) androidService
				.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		accelerometerEventListener = new SensorEventListener() {

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// Nothing yet...
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				onAccelerometerEvent(event);
			}
		};

		sensorManager.registerListener(accelerometerEventListener,
				accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onActivityStartup() {
		getLog().info(
				"Activity interactivespaces.example.activity.android.accelerometer startup");
	}

	@Override
	public void onActivityActivate() {
		getLog().info(
				"Activity interactivespaces.example.activity.android.accelerometer activate");
	}

	@Override
	public void onActivityDeactivate() {
		getLog().info(
				"Activity interactivespaces.example.activity.android.accelerometer deactivate");
	}

	@Override
	public void onActivityShutdown() {
		getLog().info(
				"Activity interactivespaces.example.activity.android.accelerometer shutdown");
	}

	@Override
	public void onActivityCleanup() {
		getLog().info(
				"Activity interactivespaces.example.activity.android.accelerometer cleanup");
		sensorManager.unregisterListener(accelerometerEventListener);
	}

	/**
	 * An accelerometer event has happened.
	 * 
	 * @param event
	 *            the accelerometer event
	 */
	private void onAccelerometerEvent(SensorEvent event) {
		if (isActivated()) {
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			Map<String, Object> message = Maps.newHashMap();
			message.put("x", x);
			message.put("y", y);
			message.put("z", z);

			sendOutputJson("output1", message);
		}
	}
}
