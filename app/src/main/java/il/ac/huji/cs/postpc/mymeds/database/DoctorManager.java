package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import il.ac.huji.cs.postpc.mymeds.BuildConfig;
import il.ac.huji.cs.postpc.mymeds.database.controllers.AppDatabase;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;

public class DoctorManager extends ManagerHelp {
    private static final String TAG = DoctorManager.class.getSimpleName();
    private static final String FILE_NAME = "doctors.json";
    private final AppDatabase db;
    private List<Doctor> doctors;
    private Map<Long, Doctor> dbMap;

    public DoctorManager(Context context) {
        super(context);
        db = AppDatabase.getInstance(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doctors = db.doctorsDao().getAll();

                dbMap = new HashMap<>();
                for (Doctor doctor : doctors) {
                    dbMap.put(doctor.id, doctor);
                }

                if (BuildConfig.DEBUG && doctors.size() == 0) {
                    try {
                        JSONArray jsonArray = new JSONArray(readJSONFromAsset(FILE_NAME));
                        String name, note, phone, email, address;
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            name = jsonArray.getJSONObject(i).getString("name");
                            note = jsonArray.getJSONObject(i).getString("note");
                            phone = jsonArray.getJSONObject(i).getString("phone");
                            email = jsonArray.getJSONObject(i).getString("email");
                            address = jsonArray.getJSONObject(i).getString("address");
                            add(name, note, phone, email, address);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    public Doctor add(String name, String note, String phone, String email, String address) {
        Doctor doctor = new Doctor(name, note, phone, email, address);
        long id = db.doctorsDao().insert(doctor);
        doctor.id = id;

        doctors.add(doctor);
        dbMap.put(id, doctor);
        return doctor;
    }

    public void add(final String name,
                    final String note,
                    final String phone,
                    final String email,
                    final String address,
                    final Listener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Doctor doctor = add(name, note, phone, email, address);

                if (callback != null) {
                    callback.callback(doctor);
                }
            }
        }).start();
    }

    public Map<Long, Doctor> getDoctors() {
        return dbMap;
    }

    public int size() {
        return doctors.size();
    }

    public Doctor getByPos(int position) {
        return doctors.get(position);
    }

    public Doctor getById(long id) {
        return dbMap.get(id);
    }

    public void remove(Doctor doctor) {
        db.doctorsDao().delete(doctor);
        db.perceptionsDao().deleteAllByDoctor(doctor.id);
        db.appointmentsDao().deleteAllByDoctor(doctor.id);
        doctors.remove(doctor);
        dbMap.remove(doctor.id);
    }

    public void remove(final Doctor doctor, final Listener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                remove(doctor);

                if (callback != null) {
                    callback.callback(doctor);
                }
            }
        }).start();
    }

    public void update(Doctor doctor) {
        db.doctorsDao().update(doctor);
        int index = doctors.indexOf(doctor);
        doctors.set(index, doctor);
        dbMap.put(doctor.id, doctor);
    }

    public void update(final Doctor doctor, final Listener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                update(doctor);

                if (callback != null) {
                    callback.callback(doctor);
                }
            }
        }).start();
    }

    public interface Listener {
        void callback(Doctor doctor);
    }
}
