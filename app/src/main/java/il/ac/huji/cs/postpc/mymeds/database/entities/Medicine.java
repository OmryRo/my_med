package il.ac.huji.cs.postpc.mymeds.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicines")
public class Medicine {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "m_id")
    public long id;

    @ColumnInfo(name = "m_name")
    public String name;

    @ColumnInfo(name = "m_note")
    public String note;

    @ColumnInfo(name = "m_amount")
    public int amount;

    @ColumnInfo(name = "m_each_num")
    public int eachNum;

    @ColumnInfo(name = "m_each_type")
    public int eachType;

    @ColumnInfo(name = "m_repeat")
    public int repeat;

    @ColumnInfo(name = "m_started")
    public long started;

    public Medicine() {}

}
