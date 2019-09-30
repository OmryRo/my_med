package il.ac.huji.cs.postpc.mymeds.database.controllers;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringJoiner;

import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.database.entities.Perception;
import il.ac.huji.cs.postpc.mymeds.database.entities.RepeatingDate;

@Database(entities = {Doctor.class, Medicine.class, Appointment.class, Perception.class}, exportSchema = false, version = 9)
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
        public static String stringArrayToString(String[] strings) {

            JSONArray jsonArray = new JSONArray();

            for (String string : strings) {
                jsonArray.put(string);
            }

            return jsonArray.toString();
        }

        @TypeConverter
        public static String[] stringToStringArray(String string) {

            try {
                JSONArray jsonArray = new JSONArray(string);
                String[] output = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    output[i] = jsonArray.optString(i, "");
                }

                return output;

            } catch (JSONException e) {
                return new String[] {};
            }

        }

        @TypeConverter
        public static String longArrayToString(long[] longs) {
            JSONArray jsonArray = new JSONArray();

            for (long longVal : longs) {
                jsonArray.put(longVal);
            }

            return jsonArray.toString();
        }

        @TypeConverter
        public static long[] stringToLongArray(String string) {
            try {
                JSONArray jsonArray = new JSONArray(string);
                long[] output = new long[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    output[i] = jsonArray.optLong(i, 0);
                }

                return output;

            } catch (JSONException e) {
                return new long[] {};
            }
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
