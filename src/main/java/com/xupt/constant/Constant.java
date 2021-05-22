package com.xupt.constant;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Constant {
    public static final String FILE_PREFIX = "/data/app/CSVFiles/";
    public static final String HOTSPOT = "hotspot";

    public static String getTodayString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return format.format(date);
    }

    public static String getHotspotKey() {
        return HOTSPOT + ":" + getTodayString();
    }

    public static String date2String(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
