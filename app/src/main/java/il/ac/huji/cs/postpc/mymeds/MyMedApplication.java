package il.ac.huji.cs.postpc.mymeds;

import android.app.Application;

import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.MedicineManager;

public class MyMedApplication extends Application {

    private DoctorManager doctorManager;
    private MedicineManager medicineManager;

    @Override
    public void onCreate() {
        super.onCreate();

        doctorManager = new DoctorManager(this);
        medicineManager = new MedicineManager(this);
    }

    public DoctorManager getDoctorManager() {
        return doctorManager;
    }

    public MedicineManager getMedicineManager() {
        return medicineManager;
    }
}
