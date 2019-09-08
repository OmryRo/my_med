package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import java.util.List;
import java.util.Map;

import il.ac.huji.cs.postpc.mymeds.database.controllers.AppDatabase;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;

public class MedicineManager {

    private AppDatabase db;
    private List<Medicine> medicines;
    private Map<Integer, Medicine> dbMap;

    public MedicineManager(Context context) {
        db = AppDatabase.getInstance(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                medicines = db.medicinesDao().getAll();
            }
        }).start();
    }

    public void add() {
        Medicine medicine = new Medicine();
        db.medicinesDao().insert(medicine);
        medicines.add(medicine);
    }

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public int size() {
        return medicines.size();
    }

    public Medicine get(int index) {
        return medicines.get(index);
    }
}
