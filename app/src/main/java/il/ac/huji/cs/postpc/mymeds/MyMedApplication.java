package il.ac.huji.cs.postpc.mymeds;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;

import il.ac.huji.cs.postpc.mymeds.database.AppointmentManager;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.MedicineManager;
import il.ac.huji.cs.postpc.mymeds.database.PrescriptionManager;
import il.ac.huji.cs.postpc.mymeds.database.TreatmentManager;
import il.ac.huji.cs.postpc.mymeds.services.RemindersService;

public class MyMedApplication extends Application {

    public static final String NOTIFICATION_CHANNEL_ID = "MYMED";
    public static final String NOTIFICATION_CHANNEL_NAME = "MyMeds Notifications";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Remind about taking medicines and appointments";

    private DoctorManager doctorManager;
    private MedicineManager medicineManager;
    private AppointmentManager appointmentManager;
    private PrescriptionManager perceptionManager;
    private TreatmentManager treatmentManager;

    @Override
    public void onCreate() {
        super.onCreate();

        doctorManager = new DoctorManager(this);
        medicineManager = new MedicineManager(this);
        appointmentManager = new AppointmentManager(this);
        perceptionManager = new PrescriptionManager(this);
        treatmentManager = new TreatmentManager(this);

        setNotificationChannel();
        RemindersService.startService(this);
    }

    private void setNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            mChannel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

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

    public PrescriptionManager getPerceptionManager() {
        return perceptionManager;
    }

    public TreatmentManager getTreatmentManager() {
        return treatmentManager;
    }
}
