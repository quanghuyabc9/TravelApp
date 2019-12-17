package com.example.travelguide.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeTool {
    public static String convertMillisToDateTime(long millis){
        DateFormat simple = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date result = new Date(millis);
        return simple.format(result);
    }
}
