package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.controllers.AppDatabase;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.database.entities.Treatment;

public class TreatmentManager {

    private final AppDatabase db;

    public TreatmentManager(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public Treatment getTreatment(long id) {
        return db.treatmentDao().getTreatmentById(id);
    }

    public void getTreatment(final long id, final TreatmentListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Treatment treatment = getTreatment(id);
                callback.callback(treatment);
            }
        }).start();
    }

    public List<Treatment> getTreatments() {
        return db.treatmentDao().getAll();
    }

    public void getTreatments(final TreatmentsListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Treatment> treatments = getTreatments();
                callback.callback(treatments);
            }
        }).start();
    }

    public List<Treatment> getTreatments(Medicine medicine) {
        return db.treatmentDao().getTreatmentsByMedicine(medicine.id);
    }

    public void getTreatments(final Medicine medicine, final TreatmentsListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Treatment> treatments = getTreatments(medicine);
                callback.callback(treatments);
            }
        }).start();
    }

    public Treatment addTreatment(Medicine medicine) {
        Treatment treatment = new Treatment(medicine.id, medicine.nextTime, medicine.amount);
        long id = db.treatmentDao().insert(treatment);
        treatment.id = id;
        return treatment;
    }

    public void addTreatment(
            final Medicine medicine,
            final TreatmentListener callback
    ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Treatment treatment = addTreatment(medicine);
                callback.callback(treatment);
            }
        }).start();
    }

    public void updateTreatment(Treatment treatment) {
        db.treatmentDao().update(treatment);
    }

    public void updateTreatment(final Treatment treatment, final TreatmentListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateTreatment(treatment);
                callback.callback(treatment);
            }
        }).start();
    }

    public void deleteTreatment(Treatment treatment) {
        db.treatmentDao().delete(treatment);
    }

    public void deleteTreatment(final Treatment treatment, final TreatmentListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteTreatment(treatment);
                callback.callback(treatment);
            }
        }).start();
    }

    public void clearTreatmentsByMedicine(Medicine medicine) {
        db.treatmentDao().deleteAllByMedicine(medicine.id);
    }

    public void clearTreatmentsByMedicine(final Medicine medicine, final TreatmentListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearTreatmentsByMedicine(medicine);
                callback.callback(null);
            }
        }).start();
    }

    public interface TreatmentListener {
        void callback(Treatment treatment);
    }

    public interface TreatmentsListener {
        void callback(List<Treatment> treatments);
    }
}
