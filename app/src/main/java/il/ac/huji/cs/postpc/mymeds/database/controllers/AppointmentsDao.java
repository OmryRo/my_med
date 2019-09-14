package il.ac.huji.cs.postpc.mymeds.database.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;

@Dao
public interface AppointmentsDao {

    @Query("SELECT * FROM appointments")
    List<Appointment> getAll();

    @Query("SELECT * FROM appointments WHERE d_id = :id")
    List<Appointment> getAppointmentsByDoctorId(long id);

    @Query("SELECT * FROM appointments WHERE a_id = :id LIMIT 1")
    Appointment getAppointmentById(long id);

    @Insert
    long insert(Appointment appointment);

    @Update
    void update(Appointment appointment);

    @Delete
    void delete(Appointment appointment);

    @Query("DELETE FROM appointments WHERE d_id = :doctorId")
    void deleteAllByDoctor(long doctorId);

}
