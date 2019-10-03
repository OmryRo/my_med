package il.ac.huji.cs.postpc.mymeds.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

import il.ac.huji.cs.postpc.mymeds.R;

@Entity(tableName = "medicines")
public class Medicine {

    @Ignore
    public static final int TYPE_PILLS = 0;

    @Ignore
    public static final int TYPE_IV = 1;

    @Ignore
    public static final int TYPE_INHALE = 2;

    @Ignore
    public static final int TYPE_INJECTION = 3;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "m_id")
    public long id;

    @ColumnInfo(name = "m_name")
    public String name;

    @ColumnInfo(name = "m_note")
    public String note;

    @ColumnInfo(name = "m_next")
    public Date nextTime;

    @ColumnInfo(name = "m_ends_at")
    public Date endsAt;

    @ColumnInfo(name = "m_times")
    public int times;

    @ColumnInfo(name = "m_amount")
    public int amount;

    @ColumnInfo(name = "m_each")
    public RepeatingDate each;

    @ColumnInfo(name = "m_stock")
    public int stock;

    @ColumnInfo(name = "m_type")
    public int type;

    @ColumnInfo(name = "m_notified_low_stock")
    public boolean notifiedLowStock;

    @Ignore
    public String getDosageString() {

        String amountUnits = null;
        switch (type) {
            case TYPE_PILLS:
                amountUnits = "pill";
                break;
            default:
                amountUnits = "unit";
                break;
        }

        if (amount > 1) {
            amountUnits += "s";
        }

        return String.format("%d %s each %s.", amount, amountUnits, each.toHumanReadable());
    }

    @Ignore
    public String getNextTimeString() {
        return nextTime.toString();//each.nextOccurianceHumanReadabily(lastTaken, new Date());
    }

    @Ignore
    public Medicine(String name, String note, Date nextTime, Date endsAt, int times, int amount, RepeatingDate each, int stock, int type) {
        this(0, name, note, nextTime, endsAt, times, amount, each, stock, type);
    }

    public Medicine(long id, String name, String note, Date nextTime, Date endsAt, int times, int amount, RepeatingDate each, int stock, int type) {
        this.id = id;
        this.name = name;
        this.note = note;
        this.nextTime = nextTime;
        this.endsAt = endsAt;
        this.times = times;
        this.amount = amount;
        this.each = each;
        this.stock = stock;
        this.type = type;
    }

    @Ignore
    public static String medicineTypeToString(int type) {
        switch (type) {
            case TYPE_PILLS:
                return "Pills";
            case TYPE_IV:
                return "IV";
            case TYPE_INJECTION:
                return "Injection";
            case TYPE_INHALE:
                return "Inhalation";
            default:
                return "Pills";
        }
    }

    @Ignore
    public static int medicineTypeToRes(int type) {
        switch (type) {
            case TYPE_PILLS:
                return R.drawable.ic_tablets_solid;
            case TYPE_IV:
                return R.drawable.ic_syringe_solid;
            case TYPE_INJECTION:
                return R.drawable.ic_syringe_solid;
            case TYPE_INHALE:
                return R.drawable.ic_prescription_bottle_solid;
            default:
                return R.drawable.ic_pills_solid;
        }
    }

}
