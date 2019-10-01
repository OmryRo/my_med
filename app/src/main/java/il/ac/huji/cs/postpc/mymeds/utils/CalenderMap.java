package il.ac.huji.cs.postpc.mymeds.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CalenderMap {

    private class Day extends ArrayList<Object> {}
    private class Month extends HashMap<Integer, Day> {}
    private class Year extends HashMap<Integer, Month> {}
    private class YearHolder extends HashMap<Integer, Year> {}

    private final Object LOCK = new Object();
    private YearHolder holder;

    public CalenderMap() {
        this.holder = new YearHolder();
    }

    public void add(Date date, Object item) {
        int yearIndex = date.getYear() + 1900;
        int monthIndex = date.getMonth();
        int dayIndex = date.getDate();

        Log.e("BLABLA", "add: " + yearIndex + " " + monthIndex + " " + dayIndex);

        synchronized (LOCK) {
            holder.putIfAbsent(yearIndex, new Year());
            Year year = holder.get(yearIndex);

            year.putIfAbsent(monthIndex, new Month());
            Month month = year.get(monthIndex);

            month.putIfAbsent(dayIndex, new Day());
            Day day = month.get(dayIndex);

            day.add(item);
        }
    }

    public void add(Date start, Date end, Object item) {
        synchronized (LOCK) {
            Date pointer = start;
            while (pointer.before(end)) {
                add(pointer, item);
                pointer = new Date(pointer.getTime() + 1000 * 60 * 60 * 24);
            }
            add(pointer, item);
        }
    }

    public Day get(int year, int month, int day) {
        Year y;
        Month m;
        Day d;

        synchronized (LOCK) {
            if (
                    ((y = holder.get(year)) == null) ||
                            ((m = y.get(month)) == null) ||
                            ((d = m.get(day)) == null)
            ) {
                return new Day();
            }
        }

        return d;
    }

    public Day get(Date date) {
        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();

        return get(year, month, day);
    }

}
