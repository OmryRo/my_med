package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.controllers.AppDatabase;
import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;

public class AppointmentManager {

    private final AppDatabase db;

    public AppointmentManager(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public List<Appointment> getAppointments(Doctor doctor) {
        return db.appointmentsDao().getAppointmentsByDoctorId(doctor.id);
    }

    public void getAppointments(final Doctor doctor, final AppointmentsListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Appointment> appointments = getAppointments(doctor);
                callback.callback(appointments);
            }
        }).start();
    }

    public interface AppointmentListener {
        void callback(Appointment appointment);
    }

    public interface AppointmentsListener {
        void callback(List<Appointment> appointments);
    }
}
