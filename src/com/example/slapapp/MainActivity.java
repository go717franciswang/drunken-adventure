package com.example.slapapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	private Sensor mAccelerometer;
	private SensorManager mSensorManager;
	private float maxSpeed;
	private float minSpeed;
	private float speed;
	private long lastTimestamp;
	private TextView speedText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			speed = savedInstanceState.getFloat("speed");
			maxSpeed = savedInstanceState.getFloat("maxSpeed");
			minSpeed = savedInstanceState.getFloat("minSpeed");
			lastTimestamp = savedInstanceState.getLong("lastTimestamp");
		}
		
		setContentView(R.layout.activity_main);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		speedText = (TextView) findViewById(R.id.swingSpeed);
		
		maxSpeed = 0f;
		minSpeed = 0f;
		speed = 0f;
		lastTimestamp = -1;
	}
	
	protected void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putFloat("speed", speed);
		outState.putFloat("maxSpeed", maxSpeed);
		outState.putFloat("minSpeed", minSpeed);
		outState.putLong("lastTimestamp", lastTimestamp);
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	protected void onPause() {
		super.onPause();
        mSensorManager.unregisterListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		float a = (float) (Math
				.sqrt((double) (Math.pow(x, 2) + Math.pow(y, 2) + Math
						.pow(z, 2))) - SensorManager.GRAVITY_EARTH);
		
		if (lastTimestamp == -1) {
			lastTimestamp = event.timestamp;
			return;
		}
		
		long timeElapsed = event.timestamp - lastTimestamp;
		if (a < 0.1 && a > -0.1) {
			a = 0;
			speed = 0;
		}
		
		speed += a * timeElapsed / 1000000000d;
		if (speed > maxSpeed) {
			maxSpeed = speed;
			speedText.setText(Float.toString(maxSpeed));
			Log.d("MainActivity", "New max speed: " + maxSpeed);
		} else if (speed < minSpeed) {
			minSpeed = speed;
			Log.d("MainActivity", "New min speed: " + minSpeed);
		}
		
		lastTimestamp = event.timestamp;
	}
}
