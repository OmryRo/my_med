package il.ac.huji.cs.postpc.mymeds.activities.medicine;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.MedicineManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;

public class MedicineInfoActivity extends AppCompatActivity {

    public static final int MEDICINE_INFO_REQ = 0x5001;
    public static final int MEDICINE_INFO_NOTHING_CHANGED = 0;
    public static final int MEDICINE_INFO_MEDICINE_CHANGED = 1;

    public static final String INTENT_INDEX = "INDEX";
    public static final String INTENT_MED_NAME = "MED_NAME";
    public static final String INTENT_MED_TYPE = "MED_TYPE";

    private MedicineManager manager;
    private Medicine medicine;

    private ImageView medicineTypeIv;
    private TextView medicineNameTv;
    private TextView medicineTypeTv;
    private TextView medicineNotesTv;
    private TextView medicineDoseAmountTv;
    private TextView medicineDoseNextTv;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = ((MyMedApplication) getApplicationContext()).getMedicineManager();

        setContentView(R.layout.activity_medicine);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        medicineNameTv = findViewById(R.id.medicine_name);
        medicineTypeIv = findViewById(R.id.medicine_type_icon);
        medicineTypeTv = findViewById(R.id.medicine_type_text);
        medicineNotesTv = findViewById(R.id.medicine_notes);
        medicineDoseAmountTv = findViewById(R.id.medicine_dose_amount);
        medicineDoseNextTv = findViewById(R.id.medicine_dose_next);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setData();
    }

    private void setData() {
        Intent intent = getIntent();
        long index = intent.getLongExtra(INTENT_INDEX, -1);
        medicine = manager.getById(index);
        setContent();
    }

    private void setContent() {
        updateToolbar();

        medicineNameTv.setText(medicine.name);
        medicineTypeTv.setText(medicine.type == Medicine.TYPE_PILLS ? "pills" : "IV");
        medicineTypeIv.setImageResource(medicine.type == Medicine.TYPE_PILLS ? R.drawable.ic_pills_solid : R.drawable.ic_syringe_solid);
        medicineNotesTv.setText(medicine.note);
        medicineDoseAmountTv.setText(medicine.getDosageString());
        medicineDoseNextTv.setText(medicine.getNextTimeString());
    }


    private void updateToolbar() {
        setTitle(""); // we need it empty...
        invalidateOptionsMenu();

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationContentDescription(R.string.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_view_menu, menu);
        return true;
    }

}
