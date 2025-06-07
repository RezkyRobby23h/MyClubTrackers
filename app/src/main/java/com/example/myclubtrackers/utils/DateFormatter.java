package com.example.myclubtrackers.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    public static String formatApiDate(String apiDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(apiDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            return apiDate;
        }
    }

    public static String formatApiTime(String apiTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date time = inputFormat.parse(apiTime);
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return outputFormat.format(time);
        } catch (ParseException e) {
            return apiTime;
        }
    }
}