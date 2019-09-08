package il.ac.huji.cs.postpc.mymeds.database.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "doctors")
public class Doctor {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "d_id")
    public long id;

    @ColumnInfo(name = "d_name")
    public String name;

    @ColumnInfo(name = "d_note")
    public String note;

    @ColumnInfo(name = "d_phone")
    public String phone;

    @ColumnInfo(name = "d_email")
    public String email;

    @ColumnInfo(name = "d_last_visit")
    public Date lastVisit;

    @ColumnInfo(name = "d_repeating")
    public RepeatingDate repeating;

    @Ignore
    public Doctor(String name, String note, String phone, String email) {
        this(0, name, note, phone, email);
    }

    public Doctor(long id, String name, String note, String phone, String email) {
        this.id = id;
        this.name = name;
        this.note = note;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Doctor && ((Doctor) obj).id == id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
