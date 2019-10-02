package il.ac.huji.cs.postpc.mymeds.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

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

    @ColumnInfo(name = "m_amount")
    public int amount;

    @ColumnInfo(name = "m_each")
    public RepeatingDate each;

    @ColumnInfo(name = "m_stock")
    public int stock;

    @ColumnInfo(name = "m_last")
    public Date lastTaken;

    @ColumnInfo(name = "m_type")
    public int type;

    @ColumnInfo(name  = "m_has_image")
    public boolean hasImage;

    @ColumnInfo(name = "m_notification_next_date")
    public Date notificationNextDate;

    @ColumnInfo(name = "m_notified_low_stock")
    public boolean notifiedLowStock;

    @Ignore
    public String getDosageString() {

        String amountUnits = null;
        switch (type) {
            case TYPE_PILLS:
                amountUnits = "pills";
                break;
            default:
                amountUnits = "units";
                break;
        }

        if (amount > 1) {
            amountUnits += "s";
        }

        return String.format("%d %s each %s.", amount, amountUnits, each.toHumanReadable());
    }

    @Ignore
    public String getNextTimeString() {
        return each.nextOccurianceHumanReadabily(lastTaken, new Date());
    }

    @Ignore
    public Medicine(String name, String note, int amount, RepeatingDate each, int stock, Date lastTaken, int type, boolean hasImage) {
        this(0, name, note, amount, each, stock, lastTaken, type, hasImage);
    }

    public Medicine(long id, String name, String note, int amount, RepeatingDate each, int stock, Date lastTaken, int type, boolean hasImage) {
        this.id = id;
        this.name = name;
        this.note = note;
        this.amount = amount;
        this.each = each;
        this.stock = stock;
        this.lastTaken = lastTaken;
        this.type = type;
        this.hasImage = hasImage;
    }

}
