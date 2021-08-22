package com.example.slider_biometry_prototype;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

public class MainActivity extends AppCompatActivity implements OnTouchListener{

    LinearLayout llMain;
    SeekBar seekBar1;
    SeekBar seekBar2;
    SeekBar seekBar3;
    TextView tv;

    SensorManager sensorManager;
    Sensor sensorAccel;
    Sensor sensorLinAccel;
    Sensor sensorGravity;
    Sensor pressureSensor;
    float x;
    float[] mGravity = new float[3];
    float[] mGeomagnetic = new float[3];
    float azimuth = 0f;
    float pressure;
    VelocityTracker vt;
    int k = 0;
    StringBuilder sb = new StringBuilder();

    Timer timer;

    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1],
                values[2]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llMain = (LinearLayout) findViewById(R.id.llMain);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        seekBar3 = (SeekBar) findViewById(R.id.seekBar3);
        tv = (TextView) findViewById(R.id.textView1);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLinAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        seekBar1.animate().alpha(0).setDuration(0);
        seekBar2.animate().alpha(0).setDuration(0);
        seekBar3.animate().alpha(0).setDuration(0);

        seekBar1.setProgress(0);
        seekBar2.setProgress(0);
        seekBar3.setProgress(0);

        seekBar1.animate().alpha(1).setDuration(2000);
        int seekRand = (int) (Math.random()*360);
        seekBar1.animate().rotation(seekRand);

        seekBar1.setOnSeekBarChangeListener(seekBarChangeListener);

        seekBar2.animate().alpha(1).setDuration(2000);
        seekRand = (int) (Math.random()*360);
        seekBar2.animate().rotation(seekRand);
        seekBar2.setOnSeekBarChangeListener(seekBarChangeListener);

        seekBar3.animate().alpha(1).setDuration(2000);
        seekRand = (int) (Math.random()*360);
        seekBar3.animate().rotation(seekRand);
        seekBar3.setOnSeekBarChangeListener(seekBarChangeListener);
    }
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            x = seekBar.getProgress();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            x = seekBar.getProgress();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            x = seekBar.getProgress();
            if (seekBar.getProgress() == seekBar.getMax()) {
                k+=1;
                if (k == 3){
                    tv.setText("Спасибо!");
                }
                seekBar.animate().alpha(0).setDuration(2000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        llMain.removeView(seekBar);
                    }
                });
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (vt==null){
            vt = VelocityTracker.obtain();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие
                vt.clear();
                vt.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE: // движение
                vt.addMovement(event);
                vt.computeCurrentVelocity(100);
                vt.addMovement(event);
                break;
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                vt.computeCurrentVelocity(100);
                vt.recycle();
                vt = null;
                break;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener, sensorAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorLinAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorGravity,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, pressureSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfo();
                    }
                });
            }
        };
        timer.schedule(task, 0, 400);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
        timer.cancel();
    }

    void showInfo() {
        sb.setLength(100);
        Log.d("MyApp", "ACCELEROMETER:" + format(valuesAccel));
        Log.d("MyApp", "ACCEL_MOTION:" + format(valuesAccelMotion));
        Log.d("MyApp", "ACCEL_GRAVITY:" + format(valuesAccelGravity));
        Log.d("MyApp", "LIN_ACCEL:" + format(valuesLinAccel));
        Log.d("MyApp", "GRAVITY:" + format(valuesGravity));
        Log.d("MyApp", "SEEKBAR_PROGRESS: "+ x);
        Log.d("MyApp", "PRESSURE_VALUE: "+ pressure);
        /* + "\n" +"SPEED_Y: "+ vy + "\n" + "ROTATION FROM NORTH: " + azimuth);*/
    }

    float[] valuesAccel = new float[3];
    float[] valuesAccelMotion = new float[3];
    float[] valuesAccelGravity = new float[3];
    float[] valuesLinAccel = new float[3];
    float[] valuesGravity = new float[3];
    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float alpha = 0.97f;
            synchronized (this){
                if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    mGravity[0]=alpha*mGravity[0]+(1-alpha)*event.values[0];
                    mGravity[1]=alpha*mGravity[1]+(1-alpha)*event.values[1];
                    mGravity[2]=alpha*mGravity[1]+(1-alpha)*event.values[2];
                }
                if(event.sensor.getType() == Sensor.TYPE_PRESSURE){
                    float[] values = event.values;
                    pressure = values[0];
                }
                if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                    mGeomagnetic[0]=alpha*mGeomagnetic[0]+(1-alpha)*event.values[0];
                    mGeomagnetic[1]=alpha*mGeomagnetic[1]+(1-alpha)*event.values[1];
                    mGeomagnetic[2]=alpha*mGeomagnetic[1]+(1-alpha)*event.values[2];
                }
                float R[] = new float[9];
                float T[] = new float[9];
                boolean success = sensorManager.getRotationMatrix(R,T,mGravity,mGeomagnetic);
                if (success){
                    float orientation[]=new float[3];
                    sensorManager.getOrientation(R, orientation);
                    azimuth = (float)Math.toDegrees(orientation[0]);
                    azimuth = (azimuth+360)%360;
                }
            }
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = event.values[i];
                        valuesAccelGravity[i] = (float) (0.1 * event.values[i] + 0.9 * valuesAccelGravity[i]);
                        valuesAccelMotion[i] = event.values[i]
                                - valuesAccelGravity[i];
                    }
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    for (int i = 0; i < 3; i++) {
                        valuesLinAccel[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    for (int i = 0; i < 3; i++) {
                        valuesGravity[i] = event.values[i];
                    }
                    break;
            }

        }

    };
}
