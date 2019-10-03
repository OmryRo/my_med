package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import il.ac.huji.cs.postpc.mymeds.database.controllers.AppDatabase;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.database.entities.RepeatingDate;

public class MedicineManager {

    private final AppDatabase db;
    private List<Medicine> medicines;
    private Map<Long, Medicine> dbMap;

    public MedicineManager(Context context) {
        db = AppDatabase.getInstance(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                medicines = db.medicinesDao().getAll();

                dbMap = new HashMap<>();
                for (Medicine medicine : medicines) {
                    dbMap.put(medicine.id, medicine);
                }

                // remove later for debugging propose...
                if (medicines.size() == 0) {
                    add(
                            "Ritalin LA 30mg",
                            "Coffie isn't allowed when taking this medicine.",
                            new Date(System.currentTimeMillis()),
                            null,
                            10,
                            1,
                            new RepeatingDate(RepeatingDate.UNIT_DAYS, 1),
                            30,
                            Medicine.TYPE_PILLS
                    );
                    add(
                            "Something random",
                            "do we really need it?.",
                            new Date(System.currentTimeMillis()),
                            new Date(System.currentTimeMillis() + 10000),
                            -1,
                            2,
                            new RepeatingDate(RepeatingDate.UNIT_DAYS, 1),
                            200,
                            Medicine.TYPE_IV
                    );
                    add(
                            "Something random 2",
                            "do we really need it?.",
                            new Date(System.currentTimeMillis()),
                            null,
                            10,
                            1,
                            new RepeatingDate(RepeatingDate.UNIT_DAYS, 1),
                            30,
                            Medicine.TYPE_PILLS
                    );
                }
            }
        }).start();
    }

    public Medicine add(
            String name,
            String note,
            Date nextTime,
            Date endsAt,
            int times,
            int amount,
            RepeatingDate each,
            int stock,
            int type
    ) {
        Medicine medicine = new Medicine(name, note, nextTime, endsAt, times, amount, each, stock, type);

        long id = db.medicinesDao().insert(medicine);
        medicine.id = id;

        medicines.add(medicine);
        dbMap.put(id, medicine);
        return medicine;
    }

    public void add(
            final String name,
            final String note,
            final Date nextTime,
            final Date endsAt,
            final int times,
            final int amount,
            final RepeatingDate each,
            final int stock,
            final int type,
            final Listener callback
    ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Medicine medicine = add(name, note, nextTime, endsAt, times, amount, each, stock, type);

                if (callback != null) {
                    callback.callback(medicine);
                }
            }
        }).start();
    }

    public Map<Long, Medicine> getMedicines() {
        return dbMap;
    }

    public int size() {
        return medicines.size();
    }

    public Medicine getByPos(int position) {
        return medicines.get(position);
    }

    public Medicine getById(long id) {
        return dbMap.get(id);
    }

    public void remove(Medicine medicine) {
        db.medicinesDao().delete(medicine);
        medicines.remove(medicine);
        dbMap.remove(medicine.id);
    }

    public void remove(final Medicine medicine, final Listener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                remove(medicine);

                if (callback != null) {
                    callback.callback(medicine);
                }
            }
        }).start();
    }

    public void update(Medicine medicine) {
        db.medicinesDao().update(medicine);
        int index = medicines.indexOf(medicine);
        medicines.set(index, medicine);
        dbMap.put(medicine.id, medicine);
    }

    public void update(final Medicine medicine, final Listener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                update(medicine);

                if (callback != null) {
                    callback.callback(medicine);
                }
            }
        }).start();
    }

    public interface Listener {
        void callback(Medicine medicine);
    }
}
