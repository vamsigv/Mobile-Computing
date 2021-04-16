package com.example.CordialCare;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class UserDetails {
    @PrimaryKey (autoGenerate = true)
    public int id;
    public Date timestamp;
    public float heartRate;
    public float breathingRate;
    public float Nausea;
    public float Headache;
    public float diarrhea;
    public float SoarThroat;
    public float Fever;
    public float MuscleAche;
    public float Loss_of_smell_or_taste;
    public float Cough;
    public float Shortness_of_breath;
    public float Feeling_tired;

    public UserDetails() {
        Nausea = 0;
        Headache = 0;
        diarrhea = 0;
        SoarThroat = 0;
        Fever = 0;
        MuscleAche = 0;
        Loss_of_smell_or_taste = 0;
        Cough = 0;
        Shortness_of_breath = 0;
        Feeling_tired = 0;
    }
}
