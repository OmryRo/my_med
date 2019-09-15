package il.ac.huji.cs.postpc.mymeds.activities.medicine;

import android.app.Activity;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.database.MedicineManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;

public class MedicineInfoActivity extends AppCompatActivity {

    public static final int MEDICINE_INFO_REQ = 0x5001;
    public static final int MEDICINE_INFO_NOTHING_CHANGED = 0;
    public static final int MEDICINE_INFO_MEDICINE_CHANGED = 1;
    public static final int MEDICINE_INFO_MEDICINE_REQUEST = 11;

    public static final String INTENT_INDEX = "INDEX";

    private MedicineManager manager;
    private Medicine medicine;
    private boolean isEditing;

    private ImageView medicineIv;
    private ImageView medicineTypeIv;
    private TextView medicineNameTv;
    private TextView medicineTypeTv;
    private TextView medicineNotesTv;
    private TextView medicineDoseAmountTv;
    private TextView medicineDoseNextTv;

    private EditText medicineNameEt;
    private EditText medicineNotesEt;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = ((MyMedApplication) getApplicationContext()).getMedicineManager();

        setContentView(R.layout.activity_medicine);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        medicineIv = findViewById(R.id.medicine_image_iv);
        medicineTypeIv = findViewById(R.id.medicine_type_icon);

        medicineNameTv = findViewById(R.id.medicine_name);
        medicineTypeTv = findViewById(R.id.medicine_type_text);
        medicineNotesTv = findViewById(R.id.medicine_notes);
        medicineDoseAmountTv = findViewById(R.id.medicine_dose_amount);
        medicineDoseNextTv = findViewById(R.id.medicine_dose_next);

        medicineNameEt = findViewById(R.id.et_medicine_name);
        medicineNotesEt = findViewById(R.id.et_medicine_notes);
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
        setContent(medicine == null);
    }

    private void setContent(boolean isEditing) {
        this.isEditing = isEditing;
        updateToolbar();

        if (isEditing) {
            Intent intent = new Intent(this, MedicineEditorActivity.class);
            startActivityForResult(intent, MEDICINE_INFO_MEDICINE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MEDICINE_INFO_MEDICINE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
            }
        }
    }

    private void updateToolbar() {
        setTitle(""); // we need it empty...
        invalidateOptionsMenu();

        if (medicine == null || !isEditing) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationContentDescription(R.string.back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_close);
            toolbar.setNavigationContentDescription(R.string.cancel);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContent(false);
                }
            });
        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_edit:
                        setContent(true);
                        return true;
                    case R.id.navigation_delete:
                        onDeleteClicked();
                        return true;
                    case R.id.navigation_done:
                        onDoneClicked();
                        return true;
                }
                return false;
            }
        });
    }

    private void onDoneClicked() {

        String medicineNotes = medicineNotesEt.getText().toString().trim();

        if (medicine != null) {
            medicine.note = medicineNotes;

            manager.update(medicine, new MedicineManager.Listener() {
                @Override
                public void callback(Medicine medicine) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setResult(MEDICINE_INFO_MEDICINE_CHANGED);
                            setContent(false);
                        }
                    });
                }
            });

        }
    }

    private void _delete() {

        manager.remove(medicine, new MedicineManager.Listener() {
            @Override
            public void callback(Medicine medicine) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setResult(MEDICINE_INFO_MEDICINE_CHANGED);
                        finish();
                    }
                });
            }
        });
    }

    private void onDeleteClicked() {

        new AlertDialog.Builder(this)
                .setTitle("Delete this medicine?")
                .setMessage("This will remove medicine")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _delete();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing, dismiss (default)
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(isEditing ? R.menu.info_edit_menu : R.menu.info_view_menu, menu);
        return true;
    }

}
