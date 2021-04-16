package com.example.CordialCare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

import static java.lang.Math.abs;

public class Home extends AppCompatActivity {

    private static final int video_captured = 101;
    private Uri file_uri;
    private int windows = 9;
    long start_exe_time;
    private boolean upload_signs_clicked = false;
    private boolean heart_rate_process = false;
    private boolean breathing_rate_process = false;
    private TextView heart_rate_text_view;
    private TextView breathing_rate_text_view;

    private CordialDB DataBase;

    private String root_path = Environment.getExternalStorageDirectory().getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signs_screen);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Button record_button = findViewById(R.id.RecordButton);
        Button measure_breathing_rate = findViewById(R.id.RespiratoryRateMeasurementButton);
        Button measure_heart_rate_button = findViewById(R.id.HeartRateMeasurementButton);
        Button upload_signs_button =  findViewById(R.id.UploadSignsButton);
        Button upload_symptoms_button =  findViewById(R.id.UploadSymptomsButton);

        breathing_rate_text_view =  findViewById(R.id.breathing_rate);
        heart_rate_text_view =  findViewById(R.id.heart_rate);


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                DataBase = CordialDB.getInstance(getApplicationContext());

            }
        });
        thread.start();
        if(!has_cam()){
            record_button.setEnabled(false);
        }
        handlePermissions(Home.this);
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(heart_rate_process == true) {
                    Toast.makeText(Home.this, "Already processing a video!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    start_rec();
                }
            }
        });

        measure_heart_rate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File videoFile = new File(root_path + "/HeartRateVideo.mp4");
                file_uri = Uri.fromFile(videoFile);

                if(heart_rate_process == true) {
                    Toast.makeText(Home.this, "Process is currently running!",
                            Toast.LENGTH_SHORT).show();
                } else if (videoFile.exists()) {
                    heart_rate_process = true;
                    heart_rate_text_view.setText("Calculating...");

                    start_exe_time = System.currentTimeMillis();
                    System.gc();
                    Intent heartIntent = new Intent(Home.this, CordialHeartRate.class);
                    startService(heartIntent);

                } else {
                    Toast.makeText(Home.this, "You have not recorded any videos yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        measure_breathing_rate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(breathing_rate_process == true) {
                    Toast.makeText(Home.this, "Already in progress!",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Home.this, "Please place your phone on your abdomen \nfor 45s", Toast.LENGTH_LONG).show();
                    breathing_rate_process = true;
                    Intent accel_intent = new Intent(Home.this, CordialAccelerometer.class);
                    startService(accel_intent);
                }
            }
        });

        upload_symptoms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intnt = new Intent(Home.this, CordialSymptoms.class);
                intnt.putExtra("UploadSignsClicked", upload_signs_clicked);
                startActivity(intnt);
            }
        });

        upload_signs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upload_signs_clicked = true;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserDetails data = new UserDetails();
                        data.heartRate = Float.parseFloat(heart_rate_text_view.getText().toString());
                        data.breathingRate = Float.parseFloat(breathing_rate_text_view.getText().toString());
                        data.timestamp = new Date(System.currentTimeMillis());
                        DataBase.userInfoDao().insert(data);
                    }
                });
                thread.start();
            }

        });

        LocalBroadcastManager.getInstance(Home.this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle b = intent.getExtras();
                BreathingRateDetector runnable = new BreathingRateDetector(b.getIntegerArrayList("accel_value_X"));

                Thread thread = new Thread(runnable);
                thread.start();

                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                breathing_rate_text_view.setText(runnable.breathing_rate + "");
                breathing_rate_process = false;
                b.clear();
                System.gc();

            }
        }, new IntentFilter("broadcastingAccelData"));


        LocalBroadcastManager.getInstance(Home.this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bndle = intent.getExtras();
                float heart_rate = 0;
                int FAIL = 0;
                for (int i = 0; i < windows; i++) {

                    ArrayList<Integer> heart_data = null;
                    heart_data = bndle.getIntegerArrayList("heart_data"+i);

                    ArrayList<Integer> denoised_reddness = denoise(heart_data, 5);

                    float zero_crossing = peakFinding(denoised_reddness);
                    heart_rate += zero_crossing/2;

                    String cse_file_path = root_path + "/heart_rate" + i + ".csv";
                    saveToCSV(heart_data, cse_file_path);

                    cse_file_path = root_path + "/heart_rate_denoised" + i + ".csv";
                    saveToCSV(denoised_reddness, cse_file_path);
                }

                heart_rate = (heart_rate*12)/ windows;
                heart_rate_text_view.setText(heart_rate + "");
                heart_rate_process = false;
                System.gc();
                bndle.clear();

            }
        }, new IntentFilter("broadcastingHeartData"));

    }

    @Override
    protected void onStart() {
        super.onStart();
        upload_signs_clicked = false;
    }

    public class BreathingRateDetector implements Runnable{

        public float breathing_rate;
        ArrayList<Integer> accel_value_X;

        BreathingRateDetector(ArrayList<Integer> accel_value_X){
            this.accel_value_X = accel_value_X;
        }

        @Override
        public void run() {

            String csv_file_path = root_path + "/XValues.csv";
            saveToCSV(accel_value_X, csv_file_path);

            ArrayList<Integer> accel_values_denoised = denoise(accel_value_X, 10);

            csv_file_path = root_path + "/x_values_denoised.csv";
            saveToCSV(accel_values_denoised, csv_file_path);

            int  zero_crossing = peakFinding(accel_values_denoised);
            breathing_rate = (zero_crossing*60)/90;
        }
    }

    public ArrayList<Integer> denoise(ArrayList<Integer> data, int filter){

        ArrayList<Integer> mvng_avg_arr = new ArrayList<>();
        int moving_avg = 0;

        for(int i=0; i< data.size(); i++){
            moving_avg += data.get(i);
            if(i+1 < filter) {
                continue;
            }
            mvng_avg_arr.add((moving_avg)/filter);
            moving_avg -= data.get(i+1 - filter);
        }

        return mvng_avg_arr;

    }

    public int peakFinding(ArrayList<Integer> data) {

        int difference, prev, slp = 0, zero_crossing = 0;
        int k = 0;
        prev = data.get(0);

        while(slp == 0 && k + 1 < data.size()){
            difference = data.get(k + 1) - data.get(k);
            if(difference != 0){
                slp = difference/abs(difference);
            }
            k++;
        }

        for(int j = 1; j<data.size(); j++) {

            difference = data.get(j) - prev;
            prev = data.get(j);

            if(difference == 0) continue;

            int currSlope = difference/abs(difference);

            if(currSlope == -1* slp){
                slp *= -1;
                zero_crossing++;
            }
        }

        return zero_crossing;
    }

    public void saveToCSV(ArrayList<Integer> the_data, String the_path){

        File file = new File(the_path);

        try {
            FileWriter op_file = new FileWriter(file);
            CSVWriter the_writer = new CSVWriter(op_file);
            String[] hdr = { "Index", "Data"};
            the_writer.writeNext(hdr);
            int k = 0;
            for (int d : the_data) {
                String data_row[] = {k + "", d + ""};
                the_writer.writeNext(data_row);
                k++;
            }
            the_writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int req_code, int result_code, Intent dta) {

        boolean del_file = false;
        super.onActivityResult(req_code, result_code, dta);
        if (req_code == video_captured) {
            if (result_code == RESULT_OK) {

                MediaMetadataRetriever video_retriever = new MediaMetadataRetriever();
                FileInputStream ip = null;
                try {
                    ip = new FileInputStream(file_uri.getPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    video_retriever.setDataSource(ip.getFD());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String time_string = video_retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long tme = Long.parseLong(time_string)/1000;
                if(tme<45) {

                    del_file = true;
                }

            } else if (result_code == RESULT_CANCELED) {

                del_file = true;
            }
            if(del_file) {
                File fdelete = new File(file_uri.getPath());
            }
        }
        file_uri = null;
    }

    public static void handlePermissions(Activity activity) {

        int storage_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int req_extrnl_storage = 1;

        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA

        };



        ActivityCompat.requestPermissions(
                activity,
                permissions,
                req_extrnl_storage
        );



    }

    private boolean has_cam() {

        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)){
            return true;
        } else {
            return false;
        }
    }


    public void start_rec() {

        File media_file = new File( root_path + "/heart_rate.mp4");
        Intent intnt = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intnt, video_captured);


        file_uri = Uri.fromFile(media_file);
        intnt.putExtra(MediaStore.EXTRA_DURATION_LIMIT,45);
        intnt.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);

    }

    }

