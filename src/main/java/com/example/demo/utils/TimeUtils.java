package com.example.demo.utils;

import java.util.Calendar;

public class TimeUtils {
    public static String get_timestamp(){
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }
}
