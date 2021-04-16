package com.example.CordialCare;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CordialHeartRate extends Service {

    private Bundle the_bundle = new Bundle();
    private String root_path = Environment.getExternalStorageDirectory().getPath();
    private int the_windows = 9;


    public class HeartRateWindowSplitting implements Runnable {
        @Override
        public void run() {

            ExecutorService the_executor = Executors.newFixedThreadPool(6);
            List<FrameExtractor> task_list = new ArrayList<>();
            for (int i = 0; i < the_windows; i++) {
                FrameExtractor frame_extractor = new FrameExtractor(i * 5);
                task_list.add(frame_extractor);
            }

            List<Future<ArrayList<Integer>>> result_list = null;
            try {
                result_list = the_executor.invokeAll(task_list);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            the_executor.shutdown();
            System.gc();

            for (int k = 0; k < result_list.size(); k++) {

                Future<ArrayList<Integer>> the_future = result_list.get(k);
                try {
                    the_bundle.putIntegerArrayList("heartData" + k, the_future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    e.getCause();
                }
            }
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intnt, int flgs, int start_id) {

        System.gc();
        Toast.makeText(this, "Your video is being processed..", Toast.LENGTH_LONG).show();

        HeartRateWindowSplitting the_runnable = new HeartRateWindowSplitting();
        Thread thrd = new Thread(the_runnable);
        thrd.start();

        return START_STICKY;
    }


    private int getAverageColor(Bitmap bit_map){

        long red_bucket = 0;
        long pixel_coubt = 0;

        for (int X = 0; X < bit_map.getHeight(); X+=5) {
            for (int Y = 0; Y < bit_map.getWidth(); Y+=5) {
                int C = bit_map.getPixel(Y, X);
                pixel_coubt++;
                red_bucket += Color.red(C);
            }
        }
        return (int)(red_bucket / pixel_coubt);
    }

    public class FrameExtractor implements Callable<ArrayList<Integer>> {
        private int strt_tme;

        FrameExtractor(int strt_tme){
            this.strt_tme = strt_tme;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        private ArrayList<Integer> getFrames(){
            Bitmap bit_map = null;

            try {
                String path = root_path + "/heart_rate.mp4";
                ArrayList<Integer> avg_color_arr = new ArrayList<>();
                FFmpegFrameGrabber the_grabber = new FFmpegFrameGrabber(path);
                AndroidFrameConverter convert_to_bit_map = new AndroidFrameConverter();
                the_grabber.start();
                the_grabber.setTimestamp(strt_tme *1000000);
                double frame_rate = the_grabber.getFrameRate();

                for (int J = 0; J < 5*frame_rate;) {
                    Frame ther_frame = the_grabber.grabFrame();
                    if (ther_frame == null) {
                        break;
                    }
                    if (ther_frame.image == null) {
                        continue;
                    }
                    J++;
                    System.gc();


                    bit_map = convert_to_bit_map.convert(ther_frame);
                    int avgColor = getAverageColor(bit_map);

                    avg_color_arr.add(avgColor);
                }

                return avg_color_arr;

            } catch(Exception e) {
                Log.e("FrameError",e.toString());
                System.out.println(e.toString());
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public ArrayList<Integer> call() {

            ArrayList<Integer> rednessData = new ArrayList<>();
            try {
                rednessData = getFrames();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return rednessData;
        }
    }


    @Override
    public IBinder onBind(Intent intnt) {
        return null;
    }
    @Override
    public void onDestroy() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent("broadcastingHeartData");
                intent.putExtras(the_bundle);
                LocalBroadcastManager.getInstance(CordialHeartRate.this).sendBroadcast(intent);
                the_bundle.clear();
                System.gc();
            }
        });

        thread.start();
    }


}
