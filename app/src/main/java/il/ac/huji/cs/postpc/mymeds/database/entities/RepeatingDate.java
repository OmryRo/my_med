package il.ac.huji.cs.postpc.mymeds.database.entities;

import java.util.Calendar;
import java.util.Date;

public class RepeatingDate {

    public static final int UNIT_MINUTES = Calendar.MINUTE;
    public static final int UNIT_HOURS = Calendar.HOUR;
    public static final int UNIT_DAYS = Calendar.DAY_OF_MONTH;
    public static final int UNIT_WEEKS = Calendar.WEEK_OF_MONTH;
    public static final int UNIT_MONTHS = Calendar.MONTH;
    public static final int UNIT_YEARS = Calendar.YEAR;

    public int unit;
    public int amount;

    public RepeatingDate(int unit, int amount) {
        this.unit = unit;
        this.amount = amount;
    }

    public RepeatingDate(String stringify) {
        int splitIndex = stringify.indexOf(":");
        this.unit = Integer.parseInt(stringify.substring(0, splitIndex));
        this.amount = Integer.parseInt(stringify.substring(splitIndex + 1));
    }

    public Date addTo(Date date) {
        return addTo(date, 1);
    }

    public Date addTo(Date date, int repeat) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(unit, amount * repeat);
        return c.getTime();
    }

    public String stringify() {
        return String.format("%s:%s", unit, amount);
    }

    public String nextOccurianceHumanReadabily(Date lastTime, Date now) {
        return addTo(lastTime).toString();
    }

    public String toHumanReadable() {
        String timeUnits = "";
        switch (unit) {
            case RepeatingDate.UNIT_DAYS:
                timeUnits = "day";
                break;
            case RepeatingDate.UNIT_HOURS:
                timeUnits = "hour";
                break;
            case RepeatingDate.UNIT_MINUTES:
                timeUnits = "minute";
                break;
            case RepeatingDate.UNIT_MONTHS:
                timeUnits = "month";
                break;
            case RepeatingDate.UNIT_WEEKS:
                timeUnits = "week";
                break;
            case RepeatingDate.UNIT_YEARS:
                timeUnits = "year";
                break;
        }

        if (amount > 1) {
            timeUnits += "s";
        }

        return String.format("%d %s", amount, timeUnits);
    }

}
