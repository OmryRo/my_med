package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import java.util.Date;
import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.controllers.AppDatabase;
import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Perception;

public class PerceptionManager {

    private final AppDatabase db;

    public PerceptionManager(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public Perception getPerception(long id) {
        return db.perceptionsDao().getPerceptionById(id);
    }

    public void getPerception(final long id, final PerceptionListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Perception perception = getPerception(id);
                callback.callback(perception);
            }
        }).start();
    }

    public List<Perception> getPerceptions() {
        return db.perceptionsDao().getAll();
    }

    public void getPerceptions(final PerceptionsListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Perception> perceptions = getPerceptions();
                callback.callback(perceptions);
            }
        }).start();
    }

    public List<Perception> getPerceptions(Doctor doctor) {
        return db.perceptionsDao().getPerceptionsByDoctorId(doctor.id);
    }

    public void getPerceptions(final Doctor doctor, final PerceptionsListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Perception> perceptions = getPerceptions(doctor);
                callback.callback(perceptions);
            }
        }).start();
    }

    public Perception addPerception(long doctorId, long[] medicineIds, String[] medicineNames, Date start, Date expire) {
        Perception perception = new Perception(doctorId, medicineIds, medicineNames, start, expire);
        long id = db.perceptionsDao().insert(perception);
        perception.id = id;
        return perception;
    }

    public void addPerception(
            final long doctorId,
            final long[] medicineIds,
            final String[] medicineNames,
            final Date start,
            final Date expire,
            final PerceptionListener callback
    ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Perception perception = addPerception(doctorId, medicineIds, medicineNames, start, expire);
                callback.callback(perception);
            }
        }).start();
    }

    public void updatePerception(Perception perception) {
        db.perceptionsDao().update(perception);
    }

    public void updatePerception(final Perception perception, final PerceptionListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updatePerception(perception);
                callback.callback(perception);
            }
        }).start();
    }

    public void deletePerception(Perception perception) {
        db.perceptionsDao().delete(perception);
    }

    public void deletePerception(final Perception perception, final PerceptionListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                deletePerception(perception);
                callback.callback(perception);
            }
        }).start();
    }

    public void clearPerceptionsByDoctor(Doctor doctor) {
        db.perceptionsDao().deleteAllByDoctor(doctor.id);
    }

    public void clearPerceptionsByDoctor(final Doctor doctor, final PerceptionsListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearPerceptionsByDoctor(doctor);
                callback.callback(null);
            }
        }).start();
    }

    public interface PerceptionListener {
        void callback(Perception perception);
    }

    public interface PerceptionsListener {
        void callback(List<Perception> perceptions);
    }
}
