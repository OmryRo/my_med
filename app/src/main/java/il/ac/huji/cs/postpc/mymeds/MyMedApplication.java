package il.ac.huji.cs.postpc.mymeds;

import android.app.Application;

import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;

public class MyMedApplication extends Application {

    private DoctorManager doctorManager;

    @Override
    public void onCreate() {
        super.onCreate();

        doctorManager = new DoctorManager(this);
    }

    public DoctorManager getDoctorManager() {
        return doctorManager;
    }
}
