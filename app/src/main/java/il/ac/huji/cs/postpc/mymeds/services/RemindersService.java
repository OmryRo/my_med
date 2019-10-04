package il.ac.huji.cs.postpc.mymeds.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.loading.LoadingActivity;
import il.ac.huji.cs.postpc.mymeds.database.AppointmentManager;
import il.ac.huji.cs.postpc.mymeds.database.MedicineManager;
import il.ac.huji.cs.postpc.mymeds.database.PerceptionManager;
import il.ac.huji.cs.postpc.mymeds.database.TreatmentManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.database.entities.Perception;
import il.ac.huji.cs.postpc.mymeds.database.entities.Treatment;

public class RemindersService extends Service {

    final Thread worker;

    public RemindersService() {
        worker = new Thread(new ReminderWorker());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        worker.start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        worker.stop();
        return super.stopService(name);
    }

    private void showNotification(int id, String title, String message) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), MyMedApplication.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);

        Intent resultIntent = new Intent(this, LoadingActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LoadingActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());

    }

    public static void startService(Context context) {
        context.startService(new Intent(context, RemindersService.class));
    }

    class ReminderWorker implements Runnable {

        MedicineManager medicineManager;
        AppointmentManager appointmentManager;
        PerceptionManager perceptionManager;
        TreatmentManager treatmentManager;

        @Override
        public void run() {
            medicineManager = ((MyMedApplication) getApplicationContext()).getMedicineManager();
            appointmentManager = ((MyMedApplication) getApplicationContext()).getAppointmentManager();
            perceptionManager = ((MyMedApplication) getApplicationContext()).getPerceptionManager();
            treatmentManager = ((MyMedApplication) getApplicationContext()).getTreatmentManager();

            while (true) {
                checkForAppointmentReminders();
                checkForPerceptionsReminders();
                checkForMedicineReminders();
                SystemClock.sleep(10000);
            }
        }

        void checkForAppointmentReminders() {
            List<Appointment> appointments = appointmentManager.getAppointments();
            for (Appointment appointment : appointments) {
                long minutesBefore = appointment.notifyMinutesBefore;

                if (minutesBefore < 0) {
                    continue;
                }

                if (appointment.date.getTime() + minutesBefore * 60000 >= System.currentTimeMillis()) {
                    appointment.notifyMinutesBefore = -1;
                    appointmentManager.updateAppointment(appointment);

                    showNotification((int) appointment.id + 10000, "You have an appoitnemt", appointment.title);
                }
            }
        }

        void checkForPerceptionsReminders() {
            List<Perception> perceptions = perceptionManager.getPerceptions();
            for (Perception perception : perceptions) {

                if (perception.hasNotified) {
                    continue;
                }

                long dayBefore = 24 * 60 * 1000;

                if (perception.expire.getTime() + dayBefore >= System.currentTimeMillis()) {
                    perception.hasNotified = true;
                    perceptionManager.updatePerception(perception);

                    StringJoiner medicineNames = new StringJoiner(", ");
                    for (String medicineName : perception.medicineNames) {
                        medicineNames.add(medicineName);
                    }

                    showNotification((int) perception.id + 100000, "Perception is going to expire today.", medicineNames.toString());
                }
            }
        }

        void checkForMedicineReminders() {
            Map<Long, Medicine> medicines = medicineManager.getMedicines();
            Date now = new Date();

            for (Medicine medicine : medicines.values()) {

                if (medicine.nextTime != null && medicine.nextTime.before(now)) {

                    String title = String.format("It's time to take %s", medicine.name);
                    String message = medicine.getAmountString();
                    showNotification((int) medicine.id, title, message);

                    treatmentManager.addTreatment(medicine);

                    Date nextTime = medicine.each.addTo(medicine.nextTime);
                    if (medicine.endsAt != null) {

                        if (medicine.endsAt.after(nextTime)) {
                            medicine.nextTime = nextTime;
                        } else {
                            medicine.nextTime = null;
                        }

                    } else if (medicine.times > 0) {
                        medicine.times--;
                        medicine.nextTime = nextTime;

                    } else {
                        medicine.nextTime = null;
                    }

                    if (medicine.stock > 0) {
                        medicine.stock--;
                    }

                    medicineManager.update(medicine);
                }
            }

        }
    }
}
