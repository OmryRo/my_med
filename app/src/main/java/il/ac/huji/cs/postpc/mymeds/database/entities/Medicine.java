package il.ac.huji.cs.postpc.mymeds.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "medicines")
public class Medicine {

    public static final int TYPE_PILLS = 0;
    public static final int TYPE_IV = 1;

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

    @Ignore
    public String getDosageString() {

        String amountUnits = type == TYPE_PILLS ? "pill" : "bottle";
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
