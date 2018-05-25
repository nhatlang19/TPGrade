package com.tpgrade.Lib;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String formatCreated(Date created) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        String date = sdf.format(new Date());
        return date;
    }
}
