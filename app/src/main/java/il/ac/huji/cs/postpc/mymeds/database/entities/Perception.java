package il.ac.huji.cs.postpc.mymeds.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "perceptions")
public class Perception {

    public final int STATE_NOT_USED = 0;
    public final int STATE_USED = 1;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "p_id")
    public long id;

    @ColumnInfo(name = "d_id")
    public long doctorId;

    @ColumnInfo(name = "m_ids")
    public long[] medicineIds;

    @ColumnInfo(name = "p_date_start")
    public Date start;

    @ColumnInfo(name = "p_date_expire")
    public Date expire;

    @ColumnInfo(name = "p_expire_notify_date")
    public Date expire_notify_date;

}
