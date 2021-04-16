package com.example.CordialCare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Date;

import static android.R.layout.simple_spinner_item;

public class CordialSymptoms extends AppCompatActivity {

    private Spinner the_spinner;
    RatingBar symptoms_ratings_bar;
    private CordialDB db;
    private UserDetails data = new UserDetails();
    float[] cachedRatings = new float[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_screen);

        symptoms_ratings_bar = (RatingBar) findViewById(R.id.ratingBar);
        Button update_button = (Button) findViewById(R.id.button2);

        the_spinner = (Spinner) findViewById(R.id.symptoms_spinner);

        ArrayAdapter<CharSequence> adptr = ArrayAdapter.createFromResource(this, R.array.symptoms_array, simple_spinner_item);
        adptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        the_spinner.setAdapter(adptr);

        Thread thrd = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    db = CordialDB.getInstance(getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thrd.start();

        symptoms_ratings_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float G, boolean b) {
                int k = the_spinner.getSelectedItemPosition();
                cachedRatings[k] = G;
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                data.Nausea = cachedRatings[0];
                data.Headache = cachedRatings[1];
                data.diarrhea = cachedRatings[2];
                data.SoarThroat = cachedRatings[3];
                data.Fever = cachedRatings[4];
                data.MuscleAche = cachedRatings[5];
                data.Loss_of_smell_or_taste = cachedRatings[6];
                data.Cough = cachedRatings[7];
                data.Shortness_of_breath = cachedRatings[8];
                data.Feeling_tired = cachedRatings[9];
                data.timestamp = new Date(System.currentTimeMillis());

                boolean uploadSignsClicked = getIntent().getExtras().getBoolean("uploadSignsClicked");

                if(uploadSignsClicked == true) {
                    Thread thrd = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            UserDetails latst_date = db.userInfoDao().getLatestData();
                            data.heartRate = latst_date.heartRate;
                            data.breathingRate = latst_date.breathingRate;
                            data.id = latst_date.id;
                            db.userInfoDao().update(data);
                        }
                    });
                    thrd.start();

                } else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.userInfoDao().insert(data);
                        }
                    });
                    thread.start();
                }
            }
        });


        the_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adptr_view, View the_view, int m, long n) {
                symptoms_ratings_bar.setRating(cachedRatings[m]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adptr_view) {
            }
        });
    }

}