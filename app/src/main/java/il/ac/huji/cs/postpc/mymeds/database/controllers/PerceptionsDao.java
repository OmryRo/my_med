package il.ac.huji.cs.postpc.mymeds.database.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.entities.Perception;

@Dao
public interface PerceptionsDao {

    @Query("SELECT * FROM perceptions")
    List<Perception> getAll();

    @Query("SELECT * FROM perceptions WHERE p_id = :id LIMIT 1")
    Perception getPerceptionById(long id);

    @Query("SELECT * FROM perceptions WHERE d_id = :id")
    List<Perception> getPerceptionsByDoctorId(long id);

    @Insert
    long insert(Perception perception);

    @Update
    void update(Perception perception);

    @Delete
    void delete(Perception perception);

    @Query("DELETE FROM perceptions WHERE d_id = :doctorId")
    void deleteAllByDoctor(long doctorId);

}
