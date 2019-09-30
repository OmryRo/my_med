package il.ac.huji.cs.postpc.mymeds.activities.perceptions;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.doctors.DoctorInfoActivity;
import il.ac.huji.cs.postpc.mymeds.database.AppointmentManager;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.MedicineManager;
import il.ac.huji.cs.postpc.mymeds.database.PerceptionManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.database.entities.Perception;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;
import il.ac.huji.cs.postpc.mymeds.utils.PerceptionMedicineHolder;

public class PerceptionActivity extends AppCompatActivity {

    public static final int PERCEPTION_INFO_REQ = 0x5000;
    public static final int PERCEPTION_INFO_NOTHING_CHANGED = 0;
    public static final int PERCEPTION_INFO_PERCEPTION_CHANGED = 1;

    public static final String PERCEPTION_ID = "PERCEPTION_ID";
    public static final String DOCTOR_ID = "DOCTOR_ID";
    public static final String ARRIVED_FROM_DOCTOR = "ARRIVED_FROM_DOCTOR";
    private static final Object LOCK = new Object();

    private PerceptionManager perceptionManager;
    private DoctorManager doctorManager;
    private MedicineManager medicineManager;

    private Perception perception;
    private Doctor doctor;
    private boolean isEditing;
    private boolean hasArivedFromDoctor;
    private boolean hasStartedAnotherActivity;
    private Date selectedStartDate;
    private Date selectedEndDate;
    private LinkedHashMap<Long, String> medicines;
    private boolean hasChanged;

    private Toolbar toolbar;
    private View doctorContainer;
    private TextView doctorName;
    private View startDateContainer;
    private TextView startDate;
    private View endDateContainer;
    private TextView endDate;
    private RecyclerView medicinesRv;
    private ImageView addMedicine;
    private AlertDialog addMedicineDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        perceptionManager = ((MyMedApplication) getApplicationContext()).getPerceptionManager();
        doctorManager = ((MyMedApplication) getApplicationContext()).getDoctorManager();
        medicineManager = ((MyMedApplication) getApplicationContext()).getMedicineManager();

        setContentView(R.layout.activity_perception);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        doctorName = findViewById(R.id.perception_doctor_name);
        doctorContainer = findViewById(R.id.perception_doctor_container);
        startDate = findViewById(R.id.perception_start_date);
        startDateContainer = findViewById(R.id.perception_start_date_container);
        endDate = findViewById(R.id.perception_end_date);
        endDateContainer = findViewById(R.id.perception_end_date_container);
        addMedicine = findViewById(R.id.perception_medicines_add);
        medicinesRv = findViewById(R.id.perception_medicines);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hasStartedAnotherActivity = false;
        setData();
    }

    private void setData() {
        Intent intent = getIntent();
        long perceptionId = intent.getLongExtra(PERCEPTION_ID, -1);
        long doctorId = intent.getLongExtra(DOCTOR_ID, -1);
        hasArivedFromDoctor = intent.getBooleanExtra(ARRIVED_FROM_DOCTOR, false);

        if (perceptionId == -1 && doctorId != -1) {
            doctor = doctorManager.getById(doctorId);
            perception = null;
            hasChanged = true;
            setTitle("New Perception");
            setContent();

        } else if (doctorId == -1 && perceptionId != -1) {
            perceptionManager.getPerception(perceptionId, new PerceptionManager.PerceptionListener() {
                @Override
                public void callback(Perception perception) {
                    PerceptionActivity.this.perception = perception;
                    PerceptionActivity.this.doctor = doctorManager.getById(perception.doctorId);

                    setContent();
                    hasChanged = false;
                }
            });
            setTitle("Perception");


        } else {
            throw new RuntimeException("bad parameters");
        }

    }

    private void setContent() {
        updateToolbar();

        doctorContainer.setOnClickListener(hasArivedFromDoctor ? null :
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        synchronized (LOCK) {
                            if (hasStartedAnotherActivity) {
                                return;
                            }

                            hasStartedAnotherActivity = true;
                        }

                        Intent intent = new Intent(getApplicationContext(), DoctorInfoActivity.class);
                        intent.putExtra(DoctorInfoActivity.INTENT_INDEX, doctor.id);
                        startActivityForResult(intent, DoctorInfoActivity.DOCTOR_INFO_REQ);
                    }
        });

        doctorName.setText(doctor.name);

        selectedStartDate = perception != null ? perception.start : new Date(System.currentTimeMillis());
        selectedEndDate = perception != null ? perception.expire : new Date(System.currentTimeMillis());

        startDateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePicker = new DatePickerDialog(PerceptionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {

                        selectedStartDate.setYear(year - 1900);
                        selectedStartDate.setMonth(month);
                        selectedStartDate.setDate(dayOfMonth);

                        setDate(startDate, selectedStartDate, "Valid from");
                        hasChanged = true;
                        updateToolbar();

                    }
                }, selectedStartDate.getYear() + 1900, selectedStartDate.getMonth(), selectedStartDate.getDate());
                datePicker.show();

            }
        });

        endDateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePicker = new DatePickerDialog(PerceptionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {

                        selectedEndDate.setYear(year - 1900);
                        selectedEndDate.setMonth(month);
                        selectedEndDate.setDate(dayOfMonth);

                        setDate(endDate, selectedEndDate, "Expire at");
                        hasChanged = true;
                        updateToolbar();

                    }
                }, selectedEndDate.getYear() + 1900, selectedEndDate.getMonth(), selectedEndDate.getDate());
                datePicker.show();

            }
        });


        if (perception != null) {
            setDate(startDate, selectedStartDate, "Valid from");
            setDate(endDate, selectedEndDate, "Expire at");
        } else {
            startDate.setText("Select starting date.");
            endDate.setText("Select expire date.");
        }

        medicines = new LinkedHashMap<>();
        if (perception != null) {
            for (int i = 0; i < perception.medicineIds.length; i++) {
                medicines.put(perception.medicineIds[i], perception.medicineNames[i]);
            }
        }

        //medicinesRv
        final MedicineInPerceptionAdapter adapter = new MedicineInPerceptionAdapter();
        medicinesRv.setAdapter(adapter);
        medicinesRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        medicinesRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        final LinearLayout medicineDialogView = new LinearLayout(getApplicationContext());
        medicineDialogView.setOrientation(LinearLayout.VERTICAL);
        final Map<Long,Medicine> globalMedicines = medicineManager.getMedicines();
        for (final Map.Entry<Long,Medicine> entry : globalMedicines.entrySet()) {
            TextView tv = new TextView(this);
            tv.setText(entry.getValue().name);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    medicines.put(entry.getKey(), entry.getValue().name);
                    hasChanged = true;
                    adapter.updateList();

                    final AlertDialog dialog = addMedicineDialog;
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            medicineDialogView.addView(tv);
        }

        addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                synchronized (LOCK) {

                    if (addMedicineDialog == null) {
                        addMedicineDialog = new AlertDialog.Builder(PerceptionActivity.this)
                                .setView(medicineDialogView)
                                .create();
                    }

                    if (addMedicineDialog.isShowing()) {
                        return;
                    }

                    addMedicineDialog.show();
                }
            }
        });

        adapter.updateList();

    }

    private void setDate(TextView textView, Date date, String pretext) {
        String dateText = String.format("%s/%s/%s", date.getDate(), date.getMonth(), date.getYear() + 1900);
        textView.setText(String.format("%s: %s", pretext, dateText));
    }


    private void updateToolbar() {
        invalidateOptionsMenu();

        if (perception == null || !hasChanged) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationContentDescription(R.string.back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });
        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_delete:
                        onDeleteClicked();
                        return true;
                }
                return false;
            }
        });
    }

    private void onDeleteClicked() {
        new AlertDialog.Builder(this)
                .setTitle("Delete this Perception?")
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

    private void onSaveSaveChangesClicked() {

        if (selectedEndDate.before(selectedStartDate)) {
            Toast.makeText(getApplicationContext(), "The perception expire before it starts.", Toast.LENGTH_LONG).show();
            return;
        }

        if (medicines.size() == 0) {
            Toast.makeText(getApplicationContext(), "The perception have no medicines.", Toast.LENGTH_LONG).show();
            return;
        }

        long[] medicineIds = new long[medicines.size()];
        String[] medicineNames = new String[medicines.size()];

        int i = 0;
        for (Map.Entry<Long, String> item : medicines.entrySet()) {
            medicineIds[i] = item.getKey();
            medicineNames[i] = item.getValue();
            i++;
        }

        if (perception != null) {
            perception.start = selectedStartDate;
            perception.expire = selectedEndDate;
            perception.medicineIds = medicineIds;
            perception.medicineNames = medicineNames;

            perceptionManager.updatePerception(perception, new PerceptionManager.PerceptionListener() {
                @Override
                public void callback(Perception perception) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setResult(PERCEPTION_INFO_PERCEPTION_CHANGED);
                            finish();
                        }
                    });
                }
            });

        } else {
            perceptionManager.addPerception(doctor.id, medicineIds, medicineNames, selectedStartDate, selectedEndDate,
                    new PerceptionManager.PerceptionListener() {
                @Override
                public void callback(final Perception perception) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PerceptionActivity.this.perception = perception;
                            setResult(PERCEPTION_INFO_PERCEPTION_CHANGED);
                            finish();
                        }
                    });
                }
            });
        }
    }

    private void _delete() {

        perceptionManager.deletePerception(perception, new PerceptionManager.PerceptionListener() {

            @Override
            public void callback(Perception perception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setResult(PERCEPTION_INFO_PERCEPTION_CHANGED);
                        finish();
                    }
                });
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_delete_only_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (hasChanged) {
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to save the changes?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onSaveSaveChangesClicked();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
        } else {
            finish();
        }
    }

    private class MedicineInPerceptionAdapter extends RecyclerView.Adapter<PerceptionMedicineHolder> {

        private long[] indexes;
        private String[] names;

        MedicineInPerceptionAdapter() {
            updateList();
        }

        private void updateList() {

            indexes = new long[medicines.size()];
            names = new String[medicines.size()];

            int i = 0;
            for (Map.Entry<Long, String> entry : medicines.entrySet()) {
                indexes[i] = entry.getKey();
                names[i] = entry.getValue();
                i++;
            }

            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PerceptionMedicineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return PerceptionMedicineHolder.createHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PerceptionMedicineHolder holder, int position) {

            final long id = indexes[position];
            final String name = names[position];

            holder.setData(R.drawable.ic_pills_solid, name, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    medicines.remove(id);
                    hasChanged = true;
                    updateList();
                }
            });
        }

        @Override
        public int getItemCount() {
            return indexes.length;
        }
    }

}
