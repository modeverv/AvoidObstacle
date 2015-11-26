package com.example.avoidobstacle;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private AvoidObstacleView mSurfaceView;
    private SensorManager mSensorManager;
    private Sensor mMagField;
    private Sensor mAccelerometer;

    private static final int MATRIX_SIZE = 16;
    private float[] mgValues = new float[3];
    private float[] acValues = new float[3];

    public static int pitch = 0;
    public static int role = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags((WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON));
        mSurfaceView = new AvoidObstacleView(this);
        setContentView(mSurfaceView);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,mMagField,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this,mMagField);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] inR = new float[MATRIX_SIZE];
        float[] outR = new float[MATRIX_SIZE];
        float[] I = new float[MATRIX_SIZE];
        float[] orValues = new float[3];

        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                acValues = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mgValues = event.values.clone();
                break;
        }

        if(mgValues != null && acValues != null){
            SensorManager.getRotationMatrix(inR,I,acValues,mgValues);

            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
            SensorManager.getOrientation(outR,orValues);

            pitch = rad2Deg(orValues[1]);
            role = rad2Deg(orValues[2]);
        }
    }

    private int rad2Deg(float rad){
        return (int) Math.floor(Math.toDegrees(rad));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
