package il.ac.huji.cs.postpc.mymeds.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "treatments")
public class Treatment {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "t_id")
    public long id;

    @ColumnInfo(name = "m_id")
    public long medicine_id;

    @ColumnInfo(name = "t_when")
    public Date when;

    @ColumnInfo(name = "t_amount")
    public int amount;
}
