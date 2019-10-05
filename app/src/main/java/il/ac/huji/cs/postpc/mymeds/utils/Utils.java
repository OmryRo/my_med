package il.ac.huji.cs.postpc.mymeds.utils;

import java.util.Date;

public class Utils {

    public static String dateToHumanReadabily(Date date, boolean showTime) {
        String dateString = String.format("%02d/%02d/%04d", date.getDate(), date.getMonth() + 1, date.getYear() + 1900);

        if (!showTime) {
            return dateString;
        }

        return String.format("%s, %02d:%02d", dateString, date.getHours(), date.getMinutes());
    }

}
