package com.example.CordialCare;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CordialConverters {

    static DateFormat data_frame = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    static {
        data_frame.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @TypeConverter
    public static String fromTimestamp(Date value) {
        if(value != null){
            return data_frame.format(value);
        } else {
            return null;
        }
    }

    @TypeConverter
    public static Date dateToTimestamp(String value) {
        if(value != null){
            try {
                return data_frame.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }
}
