package il.ac.huji.cs.postpc.mymeds.activities.appointments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.doctors.DoctorInfoActivity;
import il.ac.huji.cs.postpc.mymeds.database.AppointmentManager;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;

public class AppointmentActivity extends AppCompatActivity {

    public static final int APPOINTMENT_INFO_REQ = 0x5000;
    public static final int APPOINTMENT_INFO_NOTHING_CHANGED = 0;
    public static final int APPOINTMENT_INFO_APPOINTMENTS_CHANGED = 1;

    public static final String APPOINTMENT_ID = "APPOINTMENT_ID";
    public static final String DOCTOR_ID = "DOCTOR_ID";
    public static final String ARRIVED_FROM_DOCTOR = "ARRIVED_FROM_DOCTOR";
    private static final Object LOCK = new Object();

    private static final String[] NOTIFICATION_OPTIONS_TEXTS = new String[] {
            "Don't remind me", "At the time it starts", "5 minutes before", "10 minutes before",
            "15 minutes before", "30 minutes before", "An hour before",
            "2 hours before", "12 hours before", "A day before",
            "2 days before", "A week before", "A month before"
    };
    private static final int[] NOTIFICATION_OPTIONS_IN_MINUTES = new int[] {
            -1, 0, 5, 10, 15, 30, 60, 120, 720, 1440, 2880, 10080, 43200
    };

    private AppointmentManager appointmentManager;
    private DoctorManager doctorManager;
    private Appointment appointment;
    private Doctor doctor;
    private boolean isEditing;
    private boolean hasArivedFromDoctor;
    private boolean hasStartedAnotherActivity;
    private Date selectedDate;

    private Toolbar toolbar;
    private TextView titleTv;
    private EditText titleEt;
    private TextView notesTv;
    private EditText notesEt;
    private View doctorContainer;
    private TextView doctorTv;
    private View addressContainer;
    private TextView addressTv;
    private EditText addressEt;
    private View dateContainer;
    private TextView dateTv;
    private View notificationContainer;
    private TextView notificationTv;
    private Spinner notificationSp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appointmentManager = ((MyMedApplication) getApplicationContext()).getAppointmentManager();
        doctorManager = ((MyMedApplication) getApplicationContext()).getDoctorManager();

        setContentView(R.layout.activity_appointment);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleTv = findViewById(R.id.appointment_title);
        titleEt = findViewById(R.id.appointment_title_et);
        notesTv = findViewById(R.id.appointment_notes);
        notesEt = findViewById(R.id.appointment_notes_et);
        doctorContainer = findViewById(R.id.appointment_doctor_container);
        doctorTv = findViewById(R.id.appointment_doctor_name);
        addressContainer = findViewById(R.id.appointment_address_container);
        addressTv = findViewById(R.id.appointment_address);
        addressEt = findViewById(R.id.appointment_address_et);
        dateContainer = findViewById(R.id.appointment_date_container);
        dateTv = findViewById(R.id.appointment_date);
        notificationContainer = findViewById(R.id.appointment_notify_container);
        notificationTv = findViewById(R.id.appointment_notify_before);
        notificationSp = findViewById(R.id.appointment_notify_before_spinner);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hasStartedAnotherActivity = false;
        setData();
    }

    private void setData() {
        Intent intent = getIntent();
        long appointmentId = intent.getLongExtra(APPOINTMENT_ID, -1);
        long doctorId = intent.getLongExtra(DOCTOR_ID, -1);
        hasArivedFromDoctor = intent.getBooleanExtra(ARRIVED_FROM_DOCTOR, false);

        if (appointmentId == -1 && doctorId != -1) {
            doctor = doctorManager.getById(doctorId);
            appointment = null;
            setContent(true);

        } else if (doctorId == -1 && appointmentId != -1) {
            appointmentManager.getAppointment(appointmentId, new AppointmentManager.AppointmentListener() {
                @Override
                public void callback(Appointment appointment) {
                    AppointmentActivity.this.appointment = appointment;
                    AppointmentActivity.this.doctor = doctorManager.getById(appointment.doctorId);

                    setContent(false);
                }
            });


        } else {
            throw new RuntimeException("bad parameters");
        }

    }

    private void setContent(boolean isEditing) {
        this.isEditing = isEditing;
        updateToolbar();

        titleTv.setText(appointment != null ? appointment.title : "");
        titleTv.setVisibility(isEditing ? View.GONE : View.VISIBLE);

        titleEt.setText(appointment != null ? appointment.title : String.format("Appointment for %s", doctor.name));
        titleEt.setVisibility(isEditing ? View.VISIBLE : View.GONE);

        notesTv.setText(appointment != null ? appointment.notes : "");
        notesTv.setVisibility(isEditing || appointment.notes == null || appointment.notes.length() == 0 ? View.GONE : View.VISIBLE);

        notesEt.setText(appointment != null ? appointment.notes : "");
        notesEt.setVisibility(isEditing ? View.VISIBLE : View.GONE);

        doctorContainer.setOnClickListener(isEditing || hasArivedFromDoctor ? null :
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

        doctorTv.setText(doctor.name);

        addressContainer.setVisibility(!isEditing && (appointment.address == null || appointment.address.length() == 0) ? View.GONE : View.VISIBLE);

        addressTv.setText(appointment != null ? appointment.address : "");
        addressTv.setVisibility(isEditing ? View.GONE : View.VISIBLE);

        addressEt.setText(appointment != null ? appointment.address : doctor.address);
        addressEt.setVisibility(isEditing ? View.VISIBLE : View.GONE);

        addressContainer.setOnClickListener(isEditing || addressTv.length() == 0 ? null :
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        synchronized (LOCK) {
                            if (hasStartedAnotherActivity) {
                                return;
                            }

                            hasStartedAnotherActivity = true;
                        }

                        Uri uri = Uri.parse(String.format("geo:?q=%s", appointment.address));
                        Intent intent = new Intent(Intent.ACTION_SEND, uri);

                        try {
                            startActivity(intent);

                        } catch (ActivityNotFoundException e) {
                            // do nothing
                        }
                    }
                });

        selectedDate = appointment != null ? appointment.date : new Date(System.currentTimeMillis());

        dateContainer.setOnClickListener(!isEditing ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePicker = new DatePickerDialog(AppointmentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {

                        TimePickerDialog timePicker = new TimePickerDialog(AppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                selectedDate.setHours(hourOfDay);
                                selectedDate.setMinutes(minute);
                                selectedDate.setYear(year - 1900);
                                selectedDate.setMonth(month);
                                selectedDate.setDate(dayOfMonth);
                                dateTv.setText(selectedDate.toString());

                            }
                        }, selectedDate.getHours(), selectedDate.getMinutes(), true);
                        timePicker.show();

                    }
                }, selectedDate.getYear() + 1900, selectedDate.getMonth(), selectedDate.getDay());
                datePicker.show();

            }
        });

        dateTv.setText(selectedDate.toString());

        notificationContainer.setVisibility(isEditing || appointment.notifyMinutesBefore >= 0 ? View.VISIBLE : View.GONE);

        //notificationTv
        int notificationIndex = appointment == null ? 0 : notificationMinsToOptionIndex(appointment.notifyMinutesBefore);

        notificationTv.setText(NOTIFICATION_OPTIONS_TEXTS[notificationIndex]);
        notificationTv.setVisibility(isEditing ? View.GONE : View.VISIBLE);

        notificationSp.setAdapter(new NotificationAdapter());
        notificationSp.setSelection(notificationIndex);
        notificationSp.setVisibility(isEditing ? View.VISIBLE : View.GONE);


    }


    private void updateToolbar() {
        setTitle(""); // we need it empty...
        invalidateOptionsMenu();

        if (appointment == null || !isEditing) {
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

    private void onDeleteClicked() {
        new AlertDialog.Builder(this)
                .setTitle("Delete this Appointment?")
                .setMessage("This will also remove the reminder.")
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

    private void onDoneClicked() {
        String title = titleEt.getText().toString().trim();

        if (title.length() == 0) {
            titleEt.setError("Title can't be empty.");
            return;
        }

        String note = notesEt.getText().toString().trim();
        String address = addressEt.getText().toString();
        Date date = selectedDate;
        int reminder = NOTIFICATION_OPTIONS_IN_MINUTES[notificationSp.getSelectedItemPosition()];

        if (appointment != null) {
            appointment.title = title;
            appointment.notes = note;
            appointment.date = date;
            appointment.address = address;
            appointment.notifyMinutesBefore = reminder;

            appointmentManager.updateAppointment(appointment, new AppointmentManager.AppointmentListener() {
                @Override
                public void callback(Appointment appointment) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setResult(APPOINTMENT_INFO_APPOINTMENTS_CHANGED);
                            setContent(false);
                        }
                    });
                }
            });

        } else {
            appointmentManager.addAppointment(doctor.id, title, note, date, 15, address, reminder,
                    new AppointmentManager.AppointmentListener() {
                @Override
                public void callback(final Appointment appointment) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppointmentActivity.this.appointment = appointment;
                            setResult(APPOINTMENT_INFO_APPOINTMENTS_CHANGED);
                            setContent(false);
                        }
                    });
                }
            });
        }
    }

    private void _delete() {

        appointmentManager.deleteAppointment(appointment, new AppointmentManager.AppointmentListener() {

            @Override
            public void callback(Appointment appointment) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setResult(APPOINTMENT_INFO_APPOINTMENTS_CHANGED);
                        finish();
                    }
                });
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(isEditing ? R.menu.info_edit_menu : R.menu.info_view_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (appointment != null && isEditing) {
            setContent(false);
        } else {
            super.onBackPressed();
        }
    }

    private int notificationMinsToOptionIndex(int time) {
        for (int i = 0; i < NOTIFICATION_OPTIONS_IN_MINUTES.length; i++) {
            if (i <= NOTIFICATION_OPTIONS_IN_MINUTES[i]) {
                return i;
            }
        }

        return NOTIFICATION_OPTIONS_IN_MINUTES.length - 1;
    }

    public class NotificationAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(AppointmentActivity.this);
            tv.setText(NOTIFICATION_OPTIONS_TEXTS[position]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            return tv;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return NOTIFICATION_OPTIONS_IN_MINUTES[position];
        }

        @Override
        public int getCount() {
            return NOTIFICATION_OPTIONS_IN_MINUTES.length;
        }

    }
}
