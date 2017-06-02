package com.pinkal.todolist.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Pinkal Daliya on 14-Oct-16.
 */

public class Utils {

    //date format
    public String getFormatDate(String inputDate) {

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, d MMM yyyy");

        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputDate = outputFormat.format(date);
        return outputDate;
    }

    //date format header
    public String getFormatDateHeader(String inputDate) {

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMM yyyy");

        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputDate = outputFormat.format(date);
        return outputDate;
    }

    //date format header id
    public String getFormatDateHeaderId(String inputDate) {

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMM yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");

        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputDate = outputFormat.format(date);
        return outputDate;
    }

    //time format
    public String getFormatTime(String inputTime) {

        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a");

        Date date = null;
        try {
            date = inputFormat.parse(inputTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputTime = outputFormat.format(date);
        return outputTime;
    }

    public String getDaysDate(String inputDate) {

//        String input = "Sat Feb 17 2012";
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        long milliseconds = date.getTime();
//        long millisecondsFromNow = milliseconds - (new Date()).getTime();
//        Toast.makeText(context, "Milliseconds to future date=" + millisecondsFromNow, Toast.LENGTH_SHORT).show();


        long smsTimeInMilis = date.getTime();
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        Log.e("cal : ", now.get(Calendar.DATE) + "");

        final long HOURS = 24 * 60 * 60 * 1000;
        Log.e("now.get(Calendar.DATE)", "" + now.get(Calendar.DATE));
        Log.e("yes : ", now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) + " , " + smsTime.get(Calendar.DATE));

        if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 8) {
            return "7 days ago";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 7) {
            return "6 days ago";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 6) {
            return "5 days ago";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 5) {
            return "4 days ago";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 4) {
            return "3 days ago";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 3) {
            return "2 days ago";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 2) {
            return "1 day ago";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Today";
        } else if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) - 1) {
            return "Tomorrow";
        } else {
            return DateFormat.format("d MMM yyyy", smsTime).toString();
        }
    }
}
