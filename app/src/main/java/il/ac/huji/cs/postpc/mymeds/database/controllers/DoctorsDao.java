package il.ac.huji.cs.postpc.mymeds.database.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;

@Dao
public interface DoctorsDao {

    @Query("SELECT * FROM doctors")
    List<Doctor> getAll();

    @Query("SELECT * FROM doctors WHERE d_id = :id LIMIT 1")
    Doctor getDoctorById(int id);

    @Insert
    long insert(Doctor doctor);

    @Update
    void update(Doctor doctor);

    @Delete
    void delete(Doctor doctor);

}
