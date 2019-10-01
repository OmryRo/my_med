package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import java.util.Date;
import java.util.List;

import il.ac.huji.cs.postpc.mymeds.database.controllers.AppDatabase;
import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;

public class AppointmentManager {

    private final AppDatabase db;

    public AppointmentManager(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public List<Appointment> getAppointments() {
        return db.appointmentsDao().getAll();
    }

    public Appointment getAppointment(long id) {
        return db.appointmentsDao().getAppointmentById(id);
    }

    public void getAppointment(final long id, final AppointmentListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Appointment appointment = getAppointment(id);
                callback.callback(appointment);
            }
        }).start();
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

    public Appointment addAppointment(
            long doctorId,
            String title,
            String notes,
            Date date,
            int duration,
            String address,
            int notifyMinutesBefore
    ) {
        Appointment appointment = new Appointment(doctorId, title, notes, date, duration, address, notifyMinutesBefore);
        long id = db.appointmentsDao().insert(appointment);
        appointment.id = id;
        return appointment;
    }

    public void addAppointment(
            final long doctorId,
            final String title,
            final String notes,
            final Date date,
            final int duration,
            final String address,
            final int notifyMinutesBefore,
            final AppointmentListener callback
    ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Appointment appointment = addAppointment(doctorId, title, notes, date, duration, address, notifyMinutesBefore);
                callback.callback(appointment);
            }
        }).start();
    }

    public void updateAppointment(Appointment appointment) {
        db.appointmentsDao().update(appointment);
    }

    public void updateAppointment(final Appointment appointment, final AppointmentListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateAppointment(appointment);
                callback.callback(appointment);
            }
        }).start();
    }

    public void deleteAppointment(Appointment appointment) {
        db.appointmentsDao().delete(appointment);
    }

    public void deleteAppointment(final Appointment appointment, final AppointmentListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteAppointment(appointment);
                callback.callback(appointment);
            }
        }).start();
    }

    public void clearAppointmentsByDoctor(Doctor doctor) {
        db.appointmentsDao().deleteAllByDoctor(doctor.id);
    }

    public void clearAppointmentsByDoctor(final Doctor doctor, final AppointmentListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearAppointmentsByDoctor(doctor);
                callback.callback(null);
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
