package com.example.smarthome;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;


public class GestureActivity extends AppCompatActivity {
    public String videoname;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1996;
    public static final int STORAGE_PERMISSION_CODE = 1;
    public static final String DIR = "/DCIM/";
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 112;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        Intent intent = getIntent();
        String gesture = intent.getStringExtra("gestures");
            if (gesture.equals("Turn on lights")) {
                videoname="hlighton";
            } else if (gesture.equals("Turn off lights")) {
                videoname = "hlightoff";
            } else if (gesture.equals("Turn on fan")) {
                videoname= "hfanon";;
            } else if (gesture.equals("Turn off fan")) {
                videoname= "hfanoff";
            } else if (gesture.equals("Increase fan speed")) {
                videoname= "hincreasefanspeed";
            } else if (gesture.equals("Decrease fan speed")) {
                videoname= "hdecreasefanspeed";
            } else if (gesture.equals("Set Thermostat to temperature")) {
                videoname="hsetthermo";
            } else if (gesture.equals("Number 0")) {
                videoname= "h0";
            } else if (gesture.equals("Number 1")) {
                videoname= "h1";
            } else if (gesture.equals("Number 2")) {
                videoname= "h2";
            } else if (gesture.equals("Number 3")) {
                 videoname= "h3";
            } else if (gesture.equals("Number 4")) {
                 videoname= "h4";
            } else if (gesture.equals("Number 5")) {
                 videoname= "h5";
            } else if (gesture.equals("Number 6")) {
                videoname= "h6";
            } else if (gesture.equals("Number 7")) {
                videoname= "h7";
            } else if (gesture.equals("Number 8")) {
                videoname= "h8";
            } else if (gesture.equals("Number 9")) {
                videoname= "h9";
            }

        this.requestPermission();

        Log.d("VideoName", "" + videoname);
        String path = System.getProperty("user.dir");

        Button btn2 = (Button) findViewById(R.id.button11);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent int3 = new Intent(getApplicationContext(), RecordActivity.class);
                int3.putExtra("gestureitem",gesture);
                startActivity(int3);
            }
        });

    }

    private void requestPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            this.showPreview( videoname);
        } else {
            String[] permissionReq = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissionReq, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.showPreview(videoname);
            } else {
                Toast.makeText(this, "External Storage permission required.", Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void showPreview(String s) {
        File videoPath = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File mediaFile = new File(videoPath, getFileName());    // Set video in the media player
        Uri videoFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", mediaFile);
        Log.d("Video URL", "" + videoPath);
        MediaController m = new MediaController(this);
        VideoView videoView = findViewById(R.id.videoView);
        videoView.setMediaController(m);
        videoView.setVideoURI(videoFileUri);
        videoView.start();
    }
    @Override
    protected void onDestroy() {
        VideoView videoView = findViewById(R.id.videoView);
        videoView.stopPlayback();
        super.onDestroy();
    }
    public String getFileName() {

        return videoname +".mp4";
    }
}