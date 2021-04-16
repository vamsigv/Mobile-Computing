package com.example.CordialCare;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

public class CordialAccelerometer extends Service implements SensorEventListener {

    private SensorManager accel_manager;
    private Sensor sense_accel;
    private ArrayList<Integer> accel_values_X = new ArrayList<>();
    private ArrayList<Integer> accel_values_Y = new ArrayList<>();
    private ArrayList<Integer> accel_values_Z = new ArrayList<>();

    @Override
    public void onCreate(){

        accel_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sense_accel = accel_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accel_manager.registerListener(this, sense_accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intnt, int flg, int strt_id) {
        accel_values_X.clear();
        accel_values_Y.clear();
        accel_values_Z.clear();
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensor_event) {

        Sensor genericSensor = sensor_event.sensor;
        if (genericSensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            accel_values_X.add((int)(sensor_event.values[0] * 100));
            accel_values_Y.add((int)(sensor_event.values[1] * 100));
            accel_values_Z.add((int)(sensor_event.values[2] * 100));

            if(accel_values_X.size() >= 230){
                stopSelf();
            }
        }
    }

    @Override
    public void onDestroy(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                accel_manager.unregisterListener(CordialAccelerometer.this);

                Intent intent = new Intent("broadcastingAccelData");
                Bundle b = new Bundle();
                b.putIntegerArrayList("accel_value_X", accel_values_X);
                intent.putExtras(b);
                LocalBroadcastManager.getInstance(CordialAccelerometer.this).sendBroadcast(intent);
            }
        });
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intnt) { return null; }

    @Override
    public void onAccuracyChanged(Sensor the_sensor, int the_accuracy) {
    }
}
