package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RecordActivity extends AppCompatActivity {
    private Intent mainActivity;
    public String videoname;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1996;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 112;
    private static final int VIDEO_CAPTURE = 101;
    private Uri uriForFile;
    public int practicenum = 0;

    public String gestureitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Intent intent = getIntent();
        gestureitem = intent.getStringExtra("gestureitem");
        if (gestureitem.equals("Turn on lights")) {
            videoname="LightOn";
        } else if (gestureitem.equals("Turn off lights")) {
            videoname = "LightOff";
        } else if (gestureitem.equals("Turn on fan")) {
            videoname= "FanOn";;
        } else if (gestureitem.equals("Turn off fan")) {
            videoname= "FanOff";
        } else if (gestureitem.equals("Increase fan speed")) {
            videoname= "FanUp";
        } else if (gestureitem.equals("Decrease fan speed")) {
            videoname= "FanDown";
        } else if (gestureitem.equals("Set Thermostat to temperature")) {
            videoname="SetThermo";
        } else if (gestureitem.equals("Number 0")) {
            videoname= "Num0";
        } else if (gestureitem.equals("Number 1")) {
            videoname= "Num1";
        } else if (gestureitem.equals("Number 2")) {
            videoname= "Num2";
        } else if (gestureitem.equals("Number 3")) {
            videoname= "Num3";
        } else if (gestureitem.equals("Number 4")) {
            videoname= "Num4";
        } else if (gestureitem.equals("Number 5")) {
            videoname= "Num5";
        } else if (gestureitem.equals("Number 6")) {
            videoname= "Num6";
        } else if (gestureitem.equals("Number 7")) {
            videoname= "Num7";
        } else if (gestureitem.equals("Number 8")) {
            videoname= "Num8";
        } else if (gestureitem.equals("Number 9")) {
            videoname= "Num9";
        }
        mainActivity = new Intent(this, MainActivity.class);
        Button btn2 = (Button) findViewById(R.id.button2);

        btn2.setOnClickListener((v) -> {
           this.preInvokeCamera();
            });
        Button btn3 = (Button) findViewById(R.id.button3);

        btn3.setOnClickListener((v) -> {
            this.uploadMultipleFiles();
        });
    }

    private void preInvokeCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            this.startRecording();
        } else {
            String[] permissionReq = {Manifest.permission.CAMERA};
            requestPermissions(permissionReq, CAMERA_PERMISSION_REQUEST_CODE);

            String[] permissionReq2 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionReq2, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.startRecording();
            } else {
                Toast.makeText(this, "Camera permission required.", Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void startRecording() {
        File vidPath = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File vidFile = new File(vidPath, getFileName(practicenum));
        uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", vidFile);
        //Toast.makeText(this,  getFileName(practicenum), Toast.LENGTH_LONG).show();
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        takeVideoIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, VIDEO_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, getFileName(practicenum) + " Saved !!", Toast.LENGTH_LONG).show();
            practicenum = (practicenum + 1) % 3;
            this.showPreview();
        } else {
            Toast.makeText(this, "Recording Cancel", Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    public String getFileName(int num) {

        return videoname + "_PRACTICE_"+ (num + 1) +".mp4";
    }
    private void showPreview() {
        VideoView videodisplay = findViewById(R.id.videoView2);
        videodisplay.setVideoURI(uriForFile);
        videodisplay.start();
        videodisplay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }
    private void uploadMultipleFiles() {
        File videoPath = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File video1 = new File(videoPath, getFileName(0));
        File video2 = new File(videoPath, getFileName(1));
        File video3 = new File(videoPath, getFileName(2));

        MultipartBody.Part videoUpload1 = MultipartBody.Part.createFormData(video1.getName(),
                video1.getName(), RequestBody.create(MediaType.parse("*/*"), video1));
        MultipartBody.Part videoUpload2 = MultipartBody.Part.createFormData(video2.getName(),
                video2.getName(), RequestBody.create(MediaType.parse("*/*"), video2));
        MultipartBody.Part videoUpload3 = MultipartBody.Part.createFormData(video3.getName(),
                video3.getName(), RequestBody.create(MediaType.parse("*/*"), video3));
        ConfigApi getResponse = ConfigFile.getRetrofit().create(ConfigApi.class);
        Call<FlaskResponse> apicall = getResponse.uploadMulFile(videoUpload1, videoUpload2, videoUpload3);
        apicall.enqueue(new Callback<FlaskResponse>() {
            @Override
            public void onResponse(Call<FlaskResponse> call, Response<FlaskResponse> response) {
                FlaskResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    assert serverResponse != null;
                    Log.v("Response", serverResponse.toString());
                }
                startActivity(mainActivity);


            }

            @Override
            public void onFailure(Call<FlaskResponse> call, Throwable t) {
                startActivity(mainActivity);
            }
        });
    }


}