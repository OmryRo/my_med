package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import il.ac.huji.cs.postpc.mymeds.database.controllers.AppDatabase;

public class PerceptionManager {

    private final AppDatabase db;

    public PerceptionManager(Context context) {
        db = AppDatabase.getInstance(context);
    }

}
