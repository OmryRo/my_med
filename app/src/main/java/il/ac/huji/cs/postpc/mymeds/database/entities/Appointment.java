package il.ac.huji.cs.postpc.mymeds.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "appointments")
public class Appointment {

    public static final int TYPE_APPOINTMENT = 0;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "a_id")
    public long id;

    @ColumnInfo(name = "d_id")
    public long doctorId;

    @ColumnInfo(name = "a_title")
    public String title;

    @ColumnInfo(name = "a_notes")
    public String notes;

    @ColumnInfo(name = "a_date")
    public Date date;

    @ColumnInfo(name = "a_duration")
    public int duration; // minutes

    @ColumnInfo(name = "a_address")
    public String address;

    @ColumnInfo(name = "a_notification_date")
    public Date notificationDate;

}
