package com.homie.takeanote.Utils;

import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class DateCreated {

    public static String getDate_Created(){
        Date current_date= Calendar.getInstance().getTime();
        SimpleDateFormat Dateformat = new SimpleDateFormat("dd/MM/yy");
        String today_date=Dateformat.format(current_date);
        return today_date;
    }

    public static String getCurrentTimeUsingDate() {
        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);
        return formattedDate;
    }
}
