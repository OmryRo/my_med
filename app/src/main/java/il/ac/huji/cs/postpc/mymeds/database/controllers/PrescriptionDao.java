package il.ac.huji.cs.postpc.mymeds.database.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.entities.Prescription;

@Dao
public interface PrescriptionDao {

    @Query("SELECT * FROM prescription")
    List<Prescription> getAll();

    @Query("SELECT * FROM prescription WHERE p_id = :id LIMIT 1")
    Prescription getPerceptionById(long id);

    @Query("SELECT * FROM prescription WHERE d_id = :id")
    List<Prescription> getPerceptionsByDoctorId(long id);

    @Insert
    long insert(Prescription perception);

    @Update
    void update(Prescription perception);

    @Delete
    void delete(Prescription perception);

    @Query("DELETE FROM prescription WHERE d_id = :doctorId")
    void deleteAllByDoctor(long doctorId);

}
