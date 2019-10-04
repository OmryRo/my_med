package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import java.util.Date;
import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.controllers.AppDatabase;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Prescription;

public class PrescriptionManager {

    private final AppDatabase db;

    public PrescriptionManager(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public Prescription getPrescription(long id) {
        return db.perceptionsDao().getPerceptionById(id);
    }

    public void getPrescription(final long id, final PerceptionListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Prescription perception = getPrescription(id);
                callback.callback(perception);
            }
        }).start();
    }

    public List<Prescription> getPrescriptions() {
        return db.perceptionsDao().getAll();
    }

    public void getPrescriptions(final PerceptionsListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Prescription> perceptions = getPrescriptions();
                callback.callback(perceptions);
            }
        }).start();
    }

    public List<Prescription> getPrescriptions(Doctor doctor) {
        return db.perceptionsDao().getPerceptionsByDoctorId(doctor.id);
    }

    public void getPrescriptions(final Doctor doctor, final PerceptionsListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Prescription> perceptions = getPrescriptions(doctor);
                callback.callback(perceptions);
            }
        }).start();
    }

    public Prescription addPerception(long doctorId, long[] medicineIds, String[] medicineNames, Date start, Date expire) {
        Prescription perception = new Prescription(doctorId, medicineIds, medicineNames, start, expire);
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
                Prescription perception = addPerception(doctorId, medicineIds, medicineNames, start, expire);
                callback.callback(perception);
            }
        }).start();
    }

    public void updatePerception(Prescription perception) {
        db.perceptionsDao().update(perception);
    }

    public void updatePerception(final Prescription perception, final PerceptionListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updatePerception(perception);
                callback.callback(perception);
            }
        }).start();
    }

    public void deletePerception(Prescription perception) {
        db.perceptionsDao().delete(perception);
    }

    public void deletePerception(final Prescription perception, final PerceptionListener callback) {
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
        void callback(Prescription perception);
    }

    public interface PerceptionsListener {
        void callback(List<Prescription> perceptions);
    }
}
