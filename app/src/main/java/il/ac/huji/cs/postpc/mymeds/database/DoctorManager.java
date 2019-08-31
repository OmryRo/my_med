package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.controllers.AppDatabase;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;

public class DoctorManager {

    private AppDatabase db;
    private List<Doctor> doctors;

    public DoctorManager(Context context) {
        db = AppDatabase.getInstance(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doctors = db.doctorsDao().getAll();

                // remove later for debugging propose...
                if (doctors.size() == 0) {
                    add("Palony Almony the first", "Do something", "+9722222222", "first@docdocdoc.com");
                    add("Palony Almony the second", "Do something", "+9722222222", "second@docdocdoc.com");
                    add("Palony Almony the third", "Do something", "+9722222222", "third@docdocdoc.com");
                }
            }
        }).start();
    }

    public void add(String name, String note, String phone, String email) {
        Doctor doctor = new Doctor(name, note, phone, email);
        long id = db.doctorsDao().insert(doctor);
        doctor.id = id;

        doctors.add(doctor);
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public int size() {
        return doctors.size();
    }

    public Doctor get(int index) {
        return doctors.get(index);
    }
}
