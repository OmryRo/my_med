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

    public Medicine() {}

}
