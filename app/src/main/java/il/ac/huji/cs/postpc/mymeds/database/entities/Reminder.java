package il.ac.huji.cs.postpc.mymeds.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "reminders")
public class Reminder {

    public static final int TYPE_APPOINTMENT = 0;
    public static final int TYPE_DOCTOR_REVISIT = 1;
    public static final int TYPE_MEDICINE_CONSUME = 2;
    public static final int TYPE_MEDICINE_LOW_STOCK = 3;
    public static final int TYPE_PERCEPTION_IN_EFFECT = 4;
    public static final int TYPE_PERCEPTION_EXPIRE = 5;
    public static final int TYPE_PERCEPTIONS_OVER = 6;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "r_id")
    public long id;

    @ColumnInfo(name = "r_date_start")
    public Date dateStart;

    @ColumnInfo(name = "r_date_reminder")
    public Date dateReminder;

    @ColumnInfo(name = "r_type")
    public int type;

    @ColumnInfo(name = "r_target")
    public long target;


}
