package com.example.sensors;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends AppCompatActivity  implements SensorEventListener {
    private ImageView arrowImg;
    private TextView degView;
    private int deg;
    private SensorManager sm;
    private Vibrator v;
    private Sensor rotSensor, accSensor, magnSensor;
    private boolean haveSensor1 = false;
    private boolean haveSensor2 = false;
    private float[] lastAccelerometers = new float[3];
    private float[] lastMagnetometers = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    float[] rotMatrix = new float[9];
    float[] orientation = new float[3];
    boolean north;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        arrowImg = (ImageView) findViewById(R.id.arrowView);
        degView = (TextView) findViewById(R.id.degView);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        start();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotMatrix, event.values);
            deg = (int) (Math.toDegrees(SensorManager.getOrientation(rotMatrix, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometers, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometers, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotMatrix, null, lastAccelerometers, lastMagnetometers);
            SensorManager.getOrientation(rotMatrix, orientation);
            deg = (int) (Math.toDegrees(SensorManager.getOrientation(rotMatrix, orientation)[0]) + 360) % 360;
        }

        deg = Math.round(deg);
        arrowImg.setRotation(-deg);

        String location = "NW";

        if (deg >= 350 || deg <= 10) {
            location = "N";
            if (!north) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(100);
                }
            }
            north = true;
        }
        if (deg < 350 && deg > 280)  {
            location = "NW";
            north = false;
        }
        if (deg <= 280 && deg > 260) {
            location = "W";
            north = false;
        }
        if (deg <= 260 && deg > 190) {
            location = "SW";
            north = false;
        }
        if (deg <= 190 && deg > 170) {
            location = "S";
            north = false;
        }
        if (deg <= 170 && deg > 100) {
            location = "SE";
            north = false;
        }
        if (deg <= 100 && deg > 80) {
            location = "E";
            north = false;
        }
        if (deg <= 80 && deg > 10) {
            location = "NE";
            north = false;
        }

        degView.setText(deg + "° " + location);

    }


    public void start() {
        if (sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                magnSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor1 = sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = sm.registerListener(this, magnSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            rotSensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor1 = sm.registerListener(this, rotSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void stop() {
        sm.unregisterListener(this, rotSensor);
        sm.unregisterListener(this, accSensor);
        sm.unregisterListener(this, magnSensor);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
