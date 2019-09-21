package il.ac.huji.cs.postpc.mymeds.activities.doctors;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.appointments.AppointmentsActivity;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;

public class DoctorInfoActivity extends AppCompatActivity {

    public static final int DOCTOR_INFO_REQ = 0x5000;
    public static final int DOCTOR_INFO_NOTHING_CHANGED = 0;
    public static final int DOCTOR_INFO_DOCTORS_CHANGED = 1;
    public static final String INTENT_INDEX = "INDEX";

    private static final Object LOCK = new Object();

    private DoctorManager manager;
    private Doctor doctor;
    private boolean isEditing;
    private boolean hasStartedAnotherActivity;

    private Toolbar toolbar;
    private TextView doctorNameTv;
    private TextView doctorNoteTv;
    private TextView doctorPhoneTv;
    private TextView doctorEmailTv;
    private TextView doctorAddressTv;
    private TextView doctorContactInfoTv;
    private EditText doctorNameEt;
    private EditText doctorNoteEt;
    private EditText doctorPhoneEt;
    private EditText doctorEmailEt;
    private EditText doctorAddressEt;
    private View moreInfoView;
    private View afterNotesDividerView;
    private View doctorPhoneContainer;
    private View doctorEmailContainer;
    private View doctorAddressContainer;
    private View doctorEmailIv;
    private View doctorPhoneIv;
    private View doctorPerceptionsView;
    private TextView doctorPerceptionTv;
    private View doctorAppointmentsView;
    private TextView doctorAppointmentsTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = ((MyMedApplication) getApplicationContext()).getDoctorManager();

        setContentView(R.layout.activity_doctor);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        doctorNameTv = findViewById(R.id.doctor_name);
        doctorNoteTv = findViewById(R.id.doctor_notes);
        doctorPhoneTv = findViewById(R.id.doctor_phone_number);
        doctorEmailTv = findViewById(R.id.doctor_email);
        doctorAddressTv = findViewById(R.id.doctor_address);
        doctorNameEt = findViewById(R.id.doctor_name_et);
        doctorNoteEt = findViewById(R.id.doctor_notes_et);
        doctorPhoneEt = findViewById(R.id.doctor_phone_number_et);
        doctorEmailEt = findViewById(R.id.doctor_email_et);
        doctorAddressEt = findViewById(R.id.doctor_address_et);
        moreInfoView = findViewById(R.id.doctor_more_info);
        afterNotesDividerView = findViewById(R.id.doctor_divider_after_notes);
        doctorPhoneContainer = findViewById(R.id.doctor_phone_container);
        doctorEmailContainer = findViewById(R.id.doctor_email_container);
        doctorAddressContainer = findViewById(R.id.doctor_address_container);
        doctorContactInfoTv = findViewById(R.id.doctor_contact_info_text);
        doctorEmailIv = findViewById(R.id.doctor_email_image);
        doctorPhoneIv = findViewById(R.id.doctor_phone_image);
        doctorPerceptionTv = findViewById(R.id.doctor_perceptions_more_info);
        doctorAppointmentsTv = findViewById(R.id.doctor_appointment_more_info);
        doctorAppointmentsView = findViewById(R.id.doctor_appointments);
        doctorPerceptionsView = findViewById(R.id.doctor_perceptions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hasStartedAnotherActivity = false;
        setData();
    }

    private void setData() {
        Intent intent = getIntent();
        long index = intent.getLongExtra(INTENT_INDEX, -1);

        if (index != -1) {
            doctor = manager.getById(index);
        }

        setContent(doctor == null);

    }

    private void setContent(boolean isEditing) {
        this.isEditing = isEditing;
        updateToolbar();

        if (!isEditing) {
            doctorNameTv.setText(doctor.name);
            doctorNoteTv.setText(doctor.note);
            doctorPhoneTv.setText(doctor.phone);
            doctorEmailTv.setText(doctor.email);
            doctorAddressTv.setText(doctor.address);

            doctorNoteTv.setVisibility(doctor.note.length() > 0 ? View.VISIBLE : View.GONE);

            boolean shouldShowEmail = doctor.email != null && doctor.email.length() != 0;
            boolean shouldShowPhone = doctor.phone != null && doctor.phone.length() != 0;
            boolean shouldShowAddress = doctor.address != null && doctor.address.length() != 0;
            boolean shouldShowContentTitle = shouldShowAddress || shouldShowEmail || shouldShowPhone;

            doctorEmailContainer.setVisibility(shouldShowEmail ? View.VISIBLE : View.GONE);
            doctorPhoneContainer.setVisibility(shouldShowPhone ? View.VISIBLE : View.GONE);
            doctorAddressContainer.setVisibility(shouldShowAddress ? View.VISIBLE : View.GONE);
            afterNotesDividerView.setVisibility(shouldShowContentTitle ? View.VISIBLE : View.GONE);
            doctorContactInfoTv.setVisibility(shouldShowContentTitle ? View.VISIBLE : View.GONE);

            doctorPhoneContainer.setClickable(true);
            doctorEmailContainer.setClickable(true);
            doctorAddressContainer.setClickable(true);

            doctorPhoneContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    synchronized (LOCK) {
                        if (hasStartedAnotherActivity) {
                            return;
                        }

                        hasStartedAnotherActivity = true;
                    }

                    Uri uri = Uri.parse(String.format("tel:%s", doctor.phone));
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);

                    try {
                        startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        hasStartedAnotherActivity = false;
                    }
                }
            });

            doctorEmailContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    synchronized (LOCK) {
                        if (hasStartedAnotherActivity) {
                            return;
                        }

                        hasStartedAnotherActivity = true;
                    }

                    Uri uri = Uri.parse(String.format("mailto:%s", doctor.email));
                    Intent intent = new Intent(Intent.ACTION_SEND, uri);

                    try {
                        startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        hasStartedAnotherActivity = false;
                    }
                }
            });

            doctorAddressContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    synchronized (LOCK) {
                        if (hasStartedAnotherActivity) {
                            return;
                        }

                        hasStartedAnotherActivity = true;
                    }

                    Uri uri = Uri.parse(String.format("geo:?q=%s", doctor.address));
                    Intent intent = new Intent(Intent.ACTION_SEND, uri);

                    try {
                        startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        hasStartedAnotherActivity = false;
                    }
                }
            });

            doctorAppointmentsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (doctor == null) {
                        return;
                    }

                    synchronized (LOCK) {
                        if (hasStartedAnotherActivity) {
                            return;
                        }

                        hasStartedAnotherActivity = true;
                    }

                    Intent intent = new Intent(getApplicationContext(), AppointmentsActivity.class);
                    intent.putExtra(AppointmentsActivity.INTENT_DOCTOR_ID, doctor.id);
                    startActivityForResult(intent, AppointmentsActivity.APPOINTMENT_INFO_REQ);
                }
            });

            doctorPerceptionsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (doctor == null) {
                        return;
                    }

                    synchronized (LOCK) {
                        if (hasStartedAnotherActivity) {
                            return;
                        }

                        hasStartedAnotherActivity = true;
                    }

                    Intent intent = new Intent(getApplicationContext(), AppointmentsActivity.class);
                    intent.putExtra(AppointmentsActivity.INTENT_DOCTOR_ID, doctor.id);
                    startActivityForResult(intent, AppointmentsActivity.APPOINTMENT_INFO_REQ);
                }
            });

        } else {
            doctorNameEt.setText(doctor == null ? "" : doctor.name);
            doctorNoteEt.setText(doctor == null ? "" : doctor.note);
            doctorPhoneEt.setText(doctor == null ? "" : doctor.phone);
            doctorEmailEt.setText(doctor == null ? "" : doctor.email);
            doctorAddressEt.setText(doctor == null ? "" : doctor.address);

            doctorNoteTv.setVisibility(View.GONE);
            doctorEmailTv.setVisibility(View.GONE);
            doctorPhoneTv.setVisibility(View.GONE);
            afterNotesDividerView.setVisibility(View.VISIBLE);
            doctorContactInfoTv.setVisibility(View.VISIBLE);

            doctorAddressContainer.setVisibility(View.VISIBLE);
            doctorPhoneContainer.setVisibility(View.VISIBLE);
            doctorEmailContainer.setVisibility(View.VISIBLE);

            doctorAddressContainer.setOnClickListener(null);
            doctorPhoneContainer.setOnClickListener(null);
            doctorEmailContainer.setOnClickListener(null);
            doctorPhoneContainer.setClickable(false);
            doctorEmailContainer.setClickable(false);
            doctorAddressContainer.setClickable(false);

        }

        View[] visibleInEditing = new View[]{
                doctorNameEt, doctorNoteEt, doctorPhoneEt, doctorEmailEt, doctorAddressEt
        };
        View[] visibleInViewing = new View[]{
                doctorNameTv, doctorPhoneTv, doctorEmailTv, doctorAddressTv, moreInfoView
        };

        for (View view : visibleInEditing) {
            view.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        }

        for (View view : visibleInViewing) {
            view.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        }
    }


    private void updateToolbar() {
        setTitle(""); // we need it empty...
        invalidateOptionsMenu();

        if (doctor == null || !isEditing) {
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
                .setTitle("Delete this therapist?")
                .setMessage("This will remove all the therapist data on this phone, including appointment dates and perceptions.")
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

        String doctorName = doctorNameEt.getText().toString();

        if (doctorName.length() == 0) {
            doctorNameEt.setError("Name can't be empty.");
            return;
        }

        String doctorNote = doctorNoteEt.getText().toString().trim();
        doctorNoteEt.setText(doctorNote);
        String doctorEmail = doctorEmailEt.getText().toString();
        String doctorPhone = doctorPhoneEt.getText().toString();
        String doctorAddress  =doctorAddressEt.getText().toString();

        if (doctor != null) {
            doctor.name = doctorName;
            doctor.note = doctorNote;
            doctor.email = doctorEmail;
            doctor.phone = doctorPhone;
            doctor.address = doctorAddress;

            manager.update(doctor, new DoctorManager.Listener() {
                @Override
                public void callback(Doctor doctor) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setResult(DOCTOR_INFO_DOCTORS_CHANGED);
                            setContent(false);
                        }
                    });
                }
            });

        } else {
            manager.add(doctorName, doctorNote, doctorPhone, doctorEmail, doctorAddress, new DoctorManager.Listener() {
                @Override
                public void callback(final Doctor doctor) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DoctorInfoActivity.this.doctor = doctor;
                            setResult(DOCTOR_INFO_DOCTORS_CHANGED);
                            setContent(false);
                        }
                    });
                }
            });
        }
    }

    private void _delete() {

        manager.remove(doctor, new DoctorManager.Listener() {
            @Override
            public void callback(Doctor doctor) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setResult(DOCTOR_INFO_DOCTORS_CHANGED);
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
        if (doctor != null && isEditing) {
            setContent(false);
        } else {
            super.onBackPressed();
        }
    }
}
