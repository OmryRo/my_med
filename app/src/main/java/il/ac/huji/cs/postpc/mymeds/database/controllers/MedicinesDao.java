package il.ac.huji.cs.postpc.mymeds.database.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;

@Dao
public interface MedicinesDao {

    @Query("SELECT * FROM medicines")
    List<Medicine> getAll();

    @Query("SELECT * FROM medicines WHERE m_id = :id LIMIT 1")
    Medicine getDoctorById(long id);

    @Insert
    long insert(Medicine medicine);

    @Update
    void update(Medicine medicine);

    @Delete
    void delete(Medicine medicine);

}
