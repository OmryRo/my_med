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
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;

public class DoctorInfoActivity extends AppCompatActivity {

    public static final int DOCTOR_INFO_REQ = 0x5000;
    public static final int DOCTOR_INFO_NOTHING_CHANGED = 0;
    public static final int DOCTOR_INFO_DOCTORS_CHANGED = 1;

    public static final String INTENT_INDEX = "INDEX";

    private DoctorManager manager;
    private Doctor doctor;
    private boolean isEditing;

    private Toolbar toolbar;
    private TextView doctorNameTv;
    private TextView doctorNoteTv;
    private TextView doctorPhoneTv;
    private TextView doctorEmailTv;
    private TextView doctorContactInfoTv;
    private EditText doctorNameEt;
    private EditText doctorNoteEt;
    private EditText doctorPhoneEt;
    private EditText doctorEmailEt;
    private View moreInfoView;
    private View afterNotesDividerView;
    private View doctorPhoneContainer;
    private View doctorEmailContainer;
    private View doctorEmailIv;
    private View doctorPhoneIv;

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
        doctorNameEt = findViewById(R.id.doctor_name_et);
        doctorNoteEt = findViewById(R.id.doctor_notes_et);
        doctorPhoneEt = findViewById(R.id.doctor_phone_number_et);
        doctorEmailEt = findViewById(R.id.doctor_email_et);
        moreInfoView = findViewById(R.id.doctor_more_info);
        afterNotesDividerView = findViewById(R.id.doctor_divider_after_notes);
        doctorPhoneContainer = findViewById(R.id.doctor_phone_container);
        doctorEmailContainer = findViewById(R.id.doctor_email_container);
        doctorContactInfoTv = findViewById(R.id.doctor_contact_info_text);
        doctorEmailIv = findViewById(R.id.doctor_email_image);
        doctorPhoneIv = findViewById(R.id.doctor_phone_image);
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

            doctorNoteTv.setVisibility(doctor.note.length() > 0 ? View.VISIBLE : View.GONE);
            if (doctor.email.length() > 0 || doctor.phone.length() > 0) {
                afterNotesDividerView.setVisibility(View.VISIBLE);
                doctorContactInfoTv.setVisibility(View.VISIBLE);
                doctorEmailTv.setVisibility(doctor.email.length() > 0 ? View.VISIBLE : View.GONE);
                doctorPhoneTv.setVisibility(doctor.phone.length() > 0 ? View.VISIBLE : View.GONE);
                doctorEmailIv.setVisibility(doctor.email.length() > 0 ? View.VISIBLE : View.GONE);
                doctorPhoneIv.setVisibility(doctor.phone.length() > 0 ? View.VISIBLE : View.GONE);
            }
            else {
                afterNotesDividerView.setVisibility(View.GONE);
                doctorContactInfoTv.setVisibility(View.GONE);
                doctorEmailTv.setVisibility(View.GONE);
                doctorPhoneTv.setVisibility(View.GONE);
                doctorEmailIv.setVisibility(View.GONE);
                doctorPhoneIv.setVisibility(View.GONE);
            }

            doctorPhoneContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri number = Uri.parse(String.format("tel:%s", doctor.phone));
                    Intent intent = new Intent(Intent.ACTION_DIAL, number);

                    try {
                        startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        // do nothing
                    }
                }
            });
            doctorEmailContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri number = Uri.parse(String.format("mailto:%s", doctor.email));
                    Intent intent = new Intent(Intent.ACTION_SEND, number);

                    try {
                        startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        // do nothing
                    }
                }
            });

        } else {
            doctorNameEt.setText(doctor == null ? "" : doctor.name);
            doctorNoteEt.setText(doctor == null ? "" : doctor.note);
            doctorPhoneEt.setText(doctor == null ? "" : doctor.phone);

            doctorEmailEt.setText(doctor == null ? "" : doctor.email);

            doctorNoteTv.setVisibility(View.GONE);
            doctorEmailTv.setVisibility(View.GONE);
            doctorPhoneTv.setVisibility(View.GONE);
            doctorEmailIv.setVisibility(View.VISIBLE);
            doctorPhoneIv.setVisibility(View.VISIBLE);
            afterNotesDividerView.setVisibility(View.VISIBLE);
            doctorContactInfoTv.setVisibility(View.VISIBLE);

            doctorPhoneContainer.setOnClickListener(null);
            doctorEmailContainer.setOnClickListener(null);
            doctorPhoneContainer.setClickable(false);
            doctorEmailContainer.setClickable(false);

        }

        View[] visibleInEditing = new View[]{
                doctorNameEt, doctorNoteEt, doctorPhoneEt, doctorEmailEt
        };
        View[] visibleInViewing = new View[]{
                doctorNameTv, moreInfoView};

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

        if (doctor != null) {
            doctor.name = doctorName;
            doctor.note = doctorNote;
            doctor.email = doctorEmail;
            doctor.phone = doctorPhone;

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
            manager.add(doctorName, doctorNote, doctorPhone, doctorEmail, new DoctorManager.Listener() {
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
