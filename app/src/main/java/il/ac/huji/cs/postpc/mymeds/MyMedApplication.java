package il.ac.huji.cs.postpc.mymeds;

import android.app.Application;

import il.ac.huji.cs.postpc.mymeds.database.AppointmentManager;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.MedicineManager;
import il.ac.huji.cs.postpc.mymeds.database.PerceptionManager;

public class MyMedApplication extends Application {

    private DoctorManager doctorManager;
    private MedicineManager medicineManager;
    private AppointmentManager appointmentManager;
    private PerceptionManager perceptionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        doctorManager = new DoctorManager(this);
        medicineManager = new MedicineManager(this);
        appointmentManager = new AppointmentManager(this);
        perceptionManager = new PerceptionManager(this);
    }

    public DoctorManager getDoctorManager() {
        return doctorManager;
    }

    public MedicineManager getMedicineManager() {
        return medicineManager;
    }

    public AppointmentManager getAppointmentManager() {
        return appointmentManager;
    }
}
