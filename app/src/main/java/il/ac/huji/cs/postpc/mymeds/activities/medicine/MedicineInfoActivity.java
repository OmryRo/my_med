package il.ac.huji.cs.postpc.mymeds.activities.medicine;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.appointments.AppointmentActivity;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.MedicineManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.database.entities.RepeatingDate;
import il.ac.huji.cs.postpc.mymeds.utils.Utils;

public class MedicineInfoActivity extends AppCompatActivity {

    public static final int MEDICINE_INFO_REQ = 0x5001;
    public static final int MEDICINE_INFO_NOTHING_CHANGED = 0;
    public static final int MEDICINE_INFO_MEDICINE_CHANGED = 1;

    public static final String INTENT_INDEX = "INDEX";
    public static final String INTENT_MED_NAME = "MED_NAME";
    public static final String INTENT_MED_TYPE = "MED_TYPE";

    private static final String[] OPTIONS_NEXT_TIME = new String[] {
            "No next time.",
            "Next time at"
    };

    private static final String[] OPTIONS_DATE_UNITS = new String[] {
            "Minutes",
            "Hours",
            "Days",
            "Weeks",
            "Months"
    };

    private static final String[] OPTIONS_ENDS_AT = new String[] {
            "Continues",
            "Ends at ",
            "Ends after ",
            "Not repeating"
    };

    private MedicineManager manager;
    private Medicine medicine;
    private String medicineName;
    private int medicineType;
    private boolean isEditing;
    private Date selectedNextTimeDate;
    private Date selectedEndsAtDate;

    private ImageView typeIcon;
    private TextView nameTv;
    private TextView typeTv;

    private TextView notesTv;
    private EditText notesEt;

    private View nextTimeContainer;
    private TextView nextTimeTv;
    private View nextTimeEditContainer;
    private Spinner nextTimeSp;
    private EditText nextTimeEv;

    private View dosageContainer;
    private TextView dosageTv;
    private View dosageEditContainer;
    private EditText dosageEditAmountEt;
    private TextView dosageEditMiddleTv;
    private EditText dosageEditTimeAmountEt;
    private Spinner dosageEditTimeUnitsSp;

    private View endsAtContainer;
    private TextView endsAtTv;
    private View endsAtEditContainer;
    private Spinner endsAtEditSelectorSp;
    private EditText endsAtEditDateEt;
    private EditText endsAtEditTimesEt;
    private TextView endsAtEditSuffixTv;

    private View inventoryContainer;
    private TextView inventoryTv;
    private View inventoryEditContainer;
    private EditText inventoryEditAmountEt;
    private TextView inventoryEditSuffixTv;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = ((MyMedApplication) getApplicationContext()).getMedicineManager();

        setContentView(R.layout.activity_medicine);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        typeIcon = findViewById(R.id.medicine_type_icon);
        nameTv = findViewById(R.id.medicine_name);
        typeTv = findViewById(R.id.medicine_type_text);
        notesTv = findViewById(R.id.medicine_notes);
        notesEt = findViewById(R.id.medicine_notes_et);
        nextTimeContainer = findViewById(R.id.medicine_next_time_container);
        nextTimeTv = findViewById(R.id.medicine_next_time);
        nextTimeEditContainer = findViewById(R.id.medicine_next_time_edit_container);
        nextTimeSp = findViewById(R.id.medicine_next_time_edit_selector);
        nextTimeEv = findViewById(R.id.medicine_next_time_edit_select_date);
        dosageContainer = findViewById(R.id.medicine_dosage_container);
        dosageTv = findViewById(R.id.medicine_dosage);
        dosageEditContainer = findViewById(R.id.medicine_dosage_edit_container);
        dosageEditAmountEt = findViewById(R.id.medicine_dosage_edit_amount);
        dosageEditMiddleTv = findViewById(R.id.medicine_dosage_edit_middle);
        dosageEditTimeAmountEt = findViewById(R.id.medicine_dosage_edit_time_amount);
        dosageEditTimeUnitsSp = findViewById(R.id.medicine_dosage_edit_time_units);
        endsAtContainer = findViewById(R.id.medicine_ends_at_container);
        endsAtTv = findViewById(R.id.medicine_ends_at_text);
        endsAtEditContainer = findViewById(R.id.medicine_ends_at_edit_container);
        endsAtEditSelectorSp = findViewById(R.id.medicine_ends_at_edit_selector);
        endsAtEditDateEt = findViewById(R.id.medicine_ends_at_edit_select_date);
        endsAtEditTimesEt = findViewById(R.id.medicine_ends_at_edit_times);
        endsAtEditSuffixTv = findViewById(R.id.medicine_ends_at_edit_times_suffix);
        inventoryContainer = findViewById(R.id.medicine_inventory_container);
        inventoryTv = findViewById(R.id.medicine_inventory);
        inventoryEditContainer = findViewById(R.id.medicine_inventory_edit_container);
        inventoryEditAmountEt = findViewById(R.id.medicine_inventory_edit_amount);
        inventoryEditSuffixTv = findViewById(R.id.medicine_inventory_edit_suffix);

        nextTimeSp.setAdapter(new StaticAdapter(OPTIONS_NEXT_TIME));
        dosageEditTimeUnitsSp.setAdapter(new StaticAdapter(OPTIONS_DATE_UNITS));
        endsAtEditSelectorSp.setAdapter(new StaticAdapter(OPTIONS_ENDS_AT));

    }

    @Override
    protected void onStart() {
        super.onStart();
        setData();
    }

    private void setData() {
        Intent intent = getIntent();
        long index = intent.getLongExtra(INTENT_INDEX, -1);

        if (index != -1) {
            medicine = manager.getById(index);

            if (medicine == null) {
                finish();
                return;
            }

        } else {
            medicineName = intent.getStringExtra(INTENT_MED_NAME);
            medicineType = intent.getIntExtra(INTENT_MED_TYPE, -1);

            if (medicineName == null || medicineType == -1) {
                finish();
                return;
            }

            isEditing = true;
        }

        setContent();
    }

    private void setContent() {
        updateToolbar();
        setName();
        setNotes();
        setNextTime();
        setDosage();
        setEndsAt();
        setInventory();
    }

    private void setName() {
        String name = medicine != null ? medicine.name : medicineName;
        int type = medicine != null ? medicine.type : medicineType;

        nameTv.setText(name);
        typeIcon.setImageResource(Medicine.medicineTypeToRes(type));
        typeTv.setText(Medicine.medicineTypeToString(type).toLowerCase());
    }

    private void setNotes() {
        String notes = medicine != null ? medicine.note : "";
        notesTv.setText(notes);
        notesEt.setText(notes);

        notesTv.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        notesEt.setVisibility(isEditing ? View.VISIBLE : View.GONE);
    }

    private void setNextTime() {
        if (isEditing) {

            nextTimeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    nextTimeEv.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    nextTimeSp.setSelection(0);
                }
            });

            if (medicine != null) {
                selectedNextTimeDate = medicine.nextTime;
            } else {
                nextTimeSp.setSelection(1);
            }

            nextTimeEv.setText(selectedNextTimeDate == null ? "Select date." : Utils.dateToHumanReadabily(selectedNextTimeDate, true));

            nextTimeEv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isEditing) {
                        return;
                    }

                    final Date selectedDate = selectedNextTimeDate == null ? new Date(System.currentTimeMillis()) : selectedNextTimeDate;

                    DatePickerDialog datePicker = new DatePickerDialog(MedicineInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {

                            TimePickerDialog timePicker = new TimePickerDialog(MedicineInfoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                    selectedDate.setHours(hourOfDay);
                                    selectedDate.setMinutes(minute);
                                    selectedDate.setYear(year - 1900);
                                    selectedDate.setMonth(month);
                                    selectedDate.setDate(dayOfMonth);
                                    nextTimeEv.setText(Utils.dateToHumanReadabily(selectedDate, true));
                                    selectedNextTimeDate = selectedDate;

                                }
                            }, selectedDate.getHours(), selectedDate.getMinutes(), true);
                            timePicker.show();

                        }
                    }, selectedDate.getYear() + 1900, selectedDate.getMonth(), selectedDate.getDate());
                    datePicker.show();

                }
            });
            nextTimeEv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        v.callOnClick();
                    }
                }
            });

            if (selectedNextTimeDate != null) {
                nextTimeSp.setSelection(1);
            }

        } else {
            String infoText = (
                    medicine.nextTime == null ?
                    "No more treatments." :
                    String.format("Next time at %s", Utils.dateToHumanReadabily(medicine.nextTime, true)));

            nextTimeTv.setText(infoText);
        }

        nextTimeTv.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        nextTimeEditContainer.setVisibility(isEditing ? View.VISIBLE : View.GONE);
    }

    private void setDosage() {

        if (isEditing) {
            dosageEditAmountEt.setText(medicine != null ? String.valueOf(medicine.amount) : "1");


            int type = medicine != null ? medicine.type : medicineType;
            String amountUnits;

            switch (type) {
                case Medicine.TYPE_PILLS:
                    amountUnits = "pills";
                    break;
                default:
                    amountUnits = "units";
                    break;
            }

            dosageEditMiddleTv.setText(String.format("%s each", amountUnits));

            if (medicine != null && medicine.each != null) {
                dosageEditTimeAmountEt.setText(String.valueOf(medicine.each.amount));

                int unitsIndex = 0;
                switch (medicine.each.unit) {
                    case (RepeatingDate.UNIT_MINUTES):
                        unitsIndex = 0;
                        break;
                    case (RepeatingDate.UNIT_HOURS):
                        unitsIndex = 1;
                        break;
                    case (RepeatingDate.UNIT_DAYS):
                        unitsIndex = 2;
                        break;
                    case (RepeatingDate.UNIT_WEEKS):
                        unitsIndex = 3;
                        break;
                    case (RepeatingDate.UNIT_MONTHS):
                        unitsIndex = 4;
                        break;
                }
                dosageEditTimeUnitsSp.setSelection(unitsIndex);

            } else {
                dosageEditTimeAmountEt.setText("1");
                dosageEditTimeUnitsSp.setSelection(2);
            }


        } else {
            dosageTv.setText(medicine.getDosageString());
        }

        dosageTv.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        dosageEditContainer.setVisibility(isEditing ? View.VISIBLE : View.GONE);
    }

    private void setEndsAt() {

        if (isEditing) {

            endsAtContainer.setVisibility(View.VISIBLE);

            endsAtEditSelectorSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    endsAtEditDateEt.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
                    endsAtEditTimesEt.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
                    endsAtEditSuffixTv.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    endsAtEditSelectorSp.setSelection(0);
                }
            });

            if (medicine != null) {

                if (medicine.endsAt == null) {

                    if (medicine.times == -1) {
                        endsAtEditSelectorSp.setSelection(0);
                    } else if (medicine.times == 0) {
                        endsAtEditSelectorSp.setSelection(3);
                    } else {
                        endsAtEditSelectorSp.setSelection(2);
                        endsAtEditTimesEt.setText(String.valueOf(medicine.times));
                    }

                } else {
                    endsAtEditSelectorSp.setSelection(1);
                    selectedEndsAtDate = medicine.endsAt;
                    endsAtEditDateEt.setText(Utils.dateToHumanReadabily(selectedEndsAtDate, false));
                }

            } else {

                endsAtEditSelectorSp.setSelection(1);
                endsAtEditDateEt.setText("Select date.");

            }

            endsAtEditDateEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isEditing) {
                        return;
                    }

                    final Date selectedDate = selectedEndsAtDate == null ? new Date(System.currentTimeMillis()) : selectedEndsAtDate;

                    DatePickerDialog datePicker = new DatePickerDialog(MedicineInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {

                            selectedDate.setYear(year - 1900);
                            selectedDate.setMonth(month);
                            selectedDate.setDate(dayOfMonth);
                            selectedDate.setHours(23);
                            selectedDate.setMinutes(59);
                            endsAtEditDateEt.setText(Utils.dateToHumanReadabily(selectedDate, false));
                            selectedEndsAtDate = selectedDate;

                        }
                    }, selectedDate.getYear() + 1900, selectedDate.getMonth(), selectedDate.getDate());
                    datePicker.show();

                }
            });
            endsAtEditDateEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        v.callOnClick();
                    }
                }
            });


        } else {

            if (medicine.endsAt == null && medicine.times == -1) {
                endsAtContainer.setVisibility(View.GONE);

            } else {
                endsAtContainer.setVisibility(View.VISIBLE);

                if (medicine.endsAt != null) {
                    endsAtTv.setText(String.format("Ends at %s", Utils.dateToHumanReadabily(medicine.endsAt, false)));
                } else {
                    endsAtTv.setText(String.format("Ends in %s times", medicine.times));
                }

            }

        }

        endsAtTv.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        endsAtEditContainer.setVisibility(isEditing ? View.VISIBLE : View.GONE);
    }

    private void setInventory() {

        int type = medicine != null ? medicine.type : medicineType;
        String amountUnits;

        switch (type) {
            case Medicine.TYPE_PILLS:
                amountUnits = "pill";
                break;
            default:
                amountUnits = "unit";
                break;
        }

        if (isEditing) {
            inventoryEditSuffixTv.setText(String.format("%ss left.", amountUnits));
            inventoryEditAmountEt.setText(medicine != null ? String.valueOf(medicine.stock) : "0");

        } else {
            if (medicine.stock != 1) {
                amountUnits += "s";
            }

            inventoryTv.setText(String.format("%s %s left.", medicine.stock, amountUnits));
        }

        inventoryTv.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        inventoryEditContainer.setVisibility(isEditing ? View.VISIBLE : View.GONE);
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

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_edit:
                        onEditClicked();
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

    public void onEditClicked() {
        isEditing = true;
        setContent();
    }

    public void onDeleteClicked() {
        new AlertDialog.Builder(this)
                .setTitle("Delete this medicine?")
                .setMessage("This will remove all the medicine data on this phone, including the times which when you took the medicine.")
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

    public void onDoneClicked() {

        String note = notesEt.getText().toString();
        Date nextTime = (nextTimeSp.getSelectedItemPosition() == 1 ? selectedNextTimeDate : null);

        int selectedEndsAtOption = endsAtEditSelectorSp.getSelectedItemPosition();
        Date endsAt = (selectedEndsAtOption == 1 ? selectedEndsAtDate : null);

        int times = -1;
        if (selectedEndsAtOption == 2) {
            times = Integer.valueOf(endsAtEditTimesEt.getText().toString());
        } else if (selectedEndsAtOption == 3) {
            times = 0;
        }

        int dosageAmount = Integer.valueOf(dosageEditAmountEt.getText().toString());
        int eachAmount = Integer.valueOf(dosageEditTimeAmountEt.getText().toString());
        int eachUnits = RepeatingDate.UNIT_DAYS;

        int selectedDosageUnitsOption = dosageEditTimeUnitsSp.getSelectedItemPosition();
        switch (selectedDosageUnitsOption) {
            case 0:
                eachUnits = RepeatingDate.UNIT_MINUTES;
                break;
            case 1:
                eachUnits = RepeatingDate.UNIT_HOURS;
                break;
            case 2:
                eachUnits = RepeatingDate.UNIT_DAYS;
                break;
            case 3:
                eachUnits = RepeatingDate.UNIT_WEEKS;
                break;
            case 4:
                eachUnits = RepeatingDate.UNIT_MONTHS;
                break;
        }

        RepeatingDate each = new RepeatingDate(eachUnits, eachAmount);
        int stock = Integer.valueOf(inventoryEditAmountEt.getText().toString());

        if (medicine != null) {
            medicine.note = note;
            medicine.nextTime = nextTime;
            medicine.endsAt = endsAt;
            medicine.times = times;
            medicine.amount = dosageAmount;
            medicine.each = each;
            medicine.stock = stock;

            manager.update(medicine, new MedicineManager.Listener() {
                @Override
                public void callback(Medicine medicine) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setResult(MEDICINE_INFO_MEDICINE_CHANGED);
                            isEditing = false;
                            setContent();
                        }
                    });
                }
            });
        } else {

            manager.add(medicineName, note, nextTime, endsAt, times, dosageAmount, each, stock, medicineType, new MedicineManager.Listener() {
                @Override
                public void callback(final Medicine medicine) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MedicineInfoActivity.this.medicine = medicine;
                            setResult(MEDICINE_INFO_MEDICINE_CHANGED);
                            isEditing = false;
                            setContent();
                        }
                    });
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(isEditing ? R.menu.info_edit_menu : R.menu.info_view_menu, menu);
        return true;
    }

    public class StaticAdapter extends BaseAdapter {

        private final String[] options;

        public StaticAdapter(String[] options) {
            this.options = options;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(MedicineInfoActivity.this);
            tv.setText(options[position]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(MedicineInfoActivity.this);
            tv.setText(options[position]);
            tv.setPadding(50, 50, 50, 50);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            return tv;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return options[position];
        }

        @Override
        public int getCount() {
            return options.length;
        }

    }

}
