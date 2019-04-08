package com.example.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sm;
    private Sensor accSensor;
    private float accSensorX;
    private float accSensorY;
    private float accSensorZ;
    private TextView valueView;
    private TextView posView;
    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
        setContentView(R.layout.activity_accelerometer);
        valueView = (TextView) findViewById(R.id.accValuesView);
        posView = (TextView) findViewById(R.id.accPosView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long time = System.currentTimeMillis();

            if ((time - lastUpdate) > 100) {
                lastUpdate = time;
                accSensorX = event.values[0];
                accSensorY = event.values[1];
                accSensorZ = event.values[2];
                updateTextView(accSensorX, accSensorY, accSensorZ);
                updatePosView(accSensorX, accSensorY, accSensorZ);
            }
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void updateTextView(float xValue, float yValue, float zValue){
        String xText = "X:    " + xValue;
        String yText = "Y:    " + yValue;
        String zText = "Z:    " + zValue;
        valueView.setText(xText + "\n" + yText + "\n" + zText);
    }

    public void updatePosView(float xValue, float yValue, float zValue){
        if (zValue > 0 && zValue < 9.8){
            if (xValue < -1) {
                posView.setText("Right");
            } else if (xValue > 1){
                posView.setText("Left");
            } else if (yValue > 1){
                posView.setText("Up");
            } else if (yValue < -1){
                posView.setText("Down");
            } else {
                posView.setText("");
            }
        } else if (zValue < 0 && zValue > -9.8){
                if (xValue < -1) {
                    posView.setText("Vänster");
                } else if (xValue > 1){
                    posView.setText("Right");
                } else if (yValue > 1){
                    posView.setText("Down");
                } else if (yValue < -1){
                    posView.setText("Up");
                } else {
                    posView.setText("");
                }
        } else {
                posView.setText("");
        }
    }
}