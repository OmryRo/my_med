package il.ac.huji.cs.postpc.mymeds.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "perceptions")
public class Perception {

    public final static int STATE_NOT_USED = 0;
    public final static int STATE_USED = 1;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "p_id")
    public long id;

    @ColumnInfo(name = "d_id")
    public long doctorId;

    @ColumnInfo(name = "m_ids")
    public long[] medicineIds;

    @ColumnInfo(name = "m_names")
    public String[] medicineNames;

    @ColumnInfo(name = "p_date_start")
    public Date start;

    @ColumnInfo(name = "p_date_expire")
    public Date expire;

    @ColumnInfo(name = "p_notified")
    public boolean hasNotified = false;

    public Perception(long id, long doctorId, long[] medicineIds, String[] medicineNames, Date start, Date expire) {
        this.id = id;
        this.doctorId = doctorId;
        this.medicineIds = medicineIds;
        this.medicineNames = medicineNames;
        this.start = start;
        this.expire = expire;
    }

    @Ignore
    public Perception(long doctorId, long[] medicineIds, String[] medicineNames, Date start, Date expire) {
        this(0, doctorId, medicineIds, medicineNames, start, expire);
    }

}
