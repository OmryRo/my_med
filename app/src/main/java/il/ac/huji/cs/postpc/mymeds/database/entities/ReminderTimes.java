package il.ac.huji.cs.postpc.mymeds.database.entities;

import java.util.Date;

public class ReminderTimes {

    public class Hour {
        int hour;
        int minute;
        float amount;
    }

    interface Schedule {}

    public class EachDays implements Schedule {
        int days;
    }

    public class EachDaysInWeek implements Schedule {
        int[] days;  //o sunday 1 monday ...
    }

    public class EachPriodicly implements Schedule {
        int daysContinues;
        int daysBreak;
    }

    Hour[] hours;
    int duration; // in days, -1 for ongoing
    Date startDate;
    Schedule schedule;
}
