package il.ac.huji.cs.postpc.mymeds.database.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.entities.Perception;
import il.ac.huji.cs.postpc.mymeds.database.entities.Treatment;

@Dao
public interface TreatmentDao {

    @Query("SELECT * FROM treatments")
    List<Treatment> getAll();

    @Query("SELECT * FROM treatments WHERE t_id = :id LIMIT 1")
    Treatment getTreatmentById(long id);

    @Query("SELECT * FROM treatments WHERE m_id = :medicineId")
    List<Treatment> getTreatmentsByMedicine(long medicineId);

    @Insert
    long insert(Treatment treatment);

    @Update
    void update(Treatment treatment);

    @Delete
    void delete(Treatment treatment);

    @Query("DELETE FROM treatments WHERE m_id = :medicineId")
    void deleteAllByMedicine(long medicineId);

}
