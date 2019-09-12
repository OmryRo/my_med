package il.ac.huji.cs.postpc.mymeds.database.controllers;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.StringJoiner;

import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.database.entities.Perception;
import il.ac.huji.cs.postpc.mymeds.database.entities.RepeatingDate;

@Database(entities = {Doctor.class, Medicine.class, Appointment.class, Perception.class}, version = 3)
@TypeConverters({AppDatabase.Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract DoctorsDao doctorsDao();
    public abstract MedicinesDao medicinesDao();
    public abstract AppointmentsDao appointmentsDao();
    public abstract PerceptionsDao perceptionsDao();

    private static volatile AppDatabase instance;

    public synchronized static AppDatabase getInstance(final Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(), AppDatabase.class, "local.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;

    }

    public static class Converters {
        @TypeConverter
        public static String longArrayToString(long[] array) {
            if (array == null) {
                return "";
            }

            StringJoiner sj = new StringJoiner(",");
            for (long item : array) {
                sj.add(String.valueOf(item));
            }
            return sj.toString();
        }

        @TypeConverter
        public static long[] stringToLongArray(String string) {
            if (string == null) {
                return new long[] {};
            }

            String[] splitted = string.split(",");
            long[] longs = new long[splitted.length];
            for (int i = 0; i < longs.length; i++) {
                longs[i] = Long.getLong(splitted[i]);
            }
            return longs;
        }

        @TypeConverter
        public static Date timestampToDate(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }

        @TypeConverter
        public static RepeatingDate stringToRepeatingDate(String string) {
            if (string == null) {
                return null;
            }
            return new RepeatingDate(string);
        }

        @TypeConverter
        public static String repeatingDateToString(RepeatingDate repeatingDate) {
            if (repeatingDate == null) {
                return null;
            }
            return repeatingDate.stringify();
        }

    }

}
