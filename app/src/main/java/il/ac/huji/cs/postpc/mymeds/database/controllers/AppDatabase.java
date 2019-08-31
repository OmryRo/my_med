package il.ac.huji.cs.postpc.mymeds.database.controllers;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;

@androidx.room.Database(entities = {Doctor.class, Medicine.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DoctorsDao doctorsDao();
    public abstract MedicinesDao medicinesDao();

    private static volatile AppDatabase instance;

    public synchronized static AppDatabase getInstance(final Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(), AppDatabase.class, "local.db")
                    .build();
        }

        return instance;

    }

}
