package il.ac.huji.cs.postpc.mymeds.utils;

import android.util.Log;
import android.util.Pair;

import androidx.recyclerview.widget.SortedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;

public class CalenderMap {

    public class Event {
        public int hours;
        public int minutes;
        public Object event;

        private Event(int hours, int minutes, Object event) {
            this.hours = hours;
            this.minutes = minutes;
            this.event = event;
        }
    }
    public class Day extends SortedList<Event> {
        private Day() {
            super(Event.class, new Callback<Event>() {
                @Override
                public int compare(Event o1, Event o2) {

                    if (o1.hours != o2.hours) {
                        return o1.hours - o2.hours;
                    } else if (o1.minutes!= o2.minutes) {
                        return o1.minutes - o2.minutes;
                    } else if (o1.event instanceof Medicine) {
                        return -1;
                    } else if (o2.event instanceof Medicine) {
                        return 1;
                    } else {
                        return 0;
                    }

                }

                @Override
                public void onChanged(int position, int count) {}

                @Override
                public boolean areContentsTheSame(Event oldItem, Event newItem) {
                    return oldItem.hours == newItem.hours && oldItem.minutes == newItem.minutes && oldItem.event == newItem.event;
                }

                @Override
                public boolean areItemsTheSame(Event item1, Event item2) {
                    return item1.hours == item2.hours && item1.minutes == item2.minutes && item1.event == item2.event;
                }

                @Override
                public void onInserted(int position, int count) {}

                @Override
                public void onRemoved(int position, int count) {}

                @Override
                public void onMoved(int fromPosition, int toPosition) {}
            });
        }
    }
    private class Month extends HashMap<Integer, Day> {}
    private class Year extends HashMap<Integer, Month> {}
    private class YearHolder extends HashMap<Integer, Year> {}

    private final Object LOCK = new Object();
    private YearHolder holder;

    private static CalenderMap instance;

    public synchronized static CalenderMap getInstance() {
        if (instance == null) {
            instance = new CalenderMap();
        }
        return instance;
    }

    private CalenderMap() {
        this.holder = new YearHolder();
    }

    public void clear() {
        synchronized (LOCK) {
            holder.clear();
        }
    }

    public void add(Date date, Object item) {
        int yearIndex = date.getYear() + 1900;
        int monthIndex = date.getMonth() + 1;
        int dayIndex = date.getDate();

        synchronized (LOCK) {
            holder.putIfAbsent(yearIndex, new Year());
            Year year = holder.get(yearIndex);

            year.putIfAbsent(monthIndex, new Month());
            Month month = year.get(monthIndex);

            month.putIfAbsent(dayIndex, new Day());
            Day day = month.get(dayIndex);

            day.add(new Event(date.getHours(), date.getMinutes(), item));
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

    public boolean hasEvents(int year, int month, int day) {
        return get(year, month, day).size() > 0;
    }

}
