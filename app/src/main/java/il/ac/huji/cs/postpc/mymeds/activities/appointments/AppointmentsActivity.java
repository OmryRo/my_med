package il.ac.huji.cs.postpc.mymeds.activities.appointments;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.doctors.DoctorInfoActivity;
import il.ac.huji.cs.postpc.mymeds.activities.medicine.MedicineInfoActivity;
import il.ac.huji.cs.postpc.mymeds.database.AppointmentManager;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;

public class AppointmentsActivity extends AppCompatActivity {

    public static final int APPOINTMENT_INFO_REQ = 0x5001;
    public static final int APPOINTMENT_INFO_RES = 0x5001;
    public static final String INTENT_DOCTOR_ID = "ID";

    private final Object LOCK = new Object();
    private boolean startedAnotherActivity = false;

    private DoctorManager doctorManager;
    private AppointmentManager appointmentManager;
    private Toolbar toolbar;
    private FloatingActionButton newAppointmentFab;
    private RecyclerView pastAppointmentsRv;
    private RecyclerView futureAppointmentsRv;
    private TextView pastAppointmentsTv;
    private TextView futureAppointmentsTv;
    private long doctorId;
    private Doctor doctor;
    private List<Appointment> pastAppointments;
    private List<Appointment> futureAppointments;

    private TextView doctorNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorManager = ((MyMedApplication) getApplicationContext()).getDoctorManager();
        appointmentManager = ((MyMedApplication) getApplicationContext()).getAppointmentManager();

        setResult(APPOINTMENT_INFO_RES);

        setContentView(R.layout.activity_appointments);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        doctorNameTv = findViewById(R.id.doctor_name);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationContentDescription(R.string.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pastAppointmentsRv = findViewById(R.id.past_appointments);
        futureAppointmentsRv = findViewById(R.id.future_appointments);
        pastAppointmentsTv = findViewById(R.id.past_appointments_tv);
        futureAppointmentsTv = findViewById(R.id.future_appointments_tv);

        Intent intent = getIntent();
        doctorId = intent.getLongExtra(INTENT_DOCTOR_ID, -1);

        if (doctorId == -1) {
            throw new RuntimeException("bad doctor id");
        }

        newAppointmentFab = findViewById(R.id.appointment_add_fab);
        newAppointmentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (LOCK) {
                    if (startedAnotherActivity) {
                        return;
                    }

                    startedAnotherActivity = true;
                }

                Intent intent = new Intent(getApplicationContext(), AppointmentActivity.class);
                intent.putExtra(AppointmentActivity.DOCTOR_ID, doctor.id);
                intent.putExtra(AppointmentActivity.ARRIVED_FROM_DOCTOR, true);
                startActivityForResult(intent, AppointmentActivity.APPOINTMENT_INFO_REQ);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        startedAnotherActivity = false;

        doctor = doctorManager.getById(doctorId);
        doctorNameTv.setText(doctor.name);

        appointmentManager.getAppointments(doctor, new AppointmentManager.AppointmentsListener() {
            @Override
            public void callback(List<Appointment> appointments) {
                futureAppointments = new ArrayList<>();
                pastAppointments = new ArrayList<>();

                Date now = new Date(System.currentTimeMillis());
                for (Appointment appointment : appointments) {
                    if (appointment.date.after(now)) {
                        futureAppointments.add(appointment);
                    } else {
                        pastAppointments.add(appointment);
                    }
                }

                futureAppointmentsTv.setVisibility(futureAppointments.size() == 0 ? View.GONE : View.VISIBLE);
                pastAppointmentsTv.setVisibility(pastAppointments.size() == 0 ? View.GONE : View.VISIBLE);

                pastAppointmentsRv.setAdapter(new RecyclerView.Adapter<ListItemHolder>() {
                    @NonNull
                    @Override
                    public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return ListItemHolder.createHolder(parent);
                    }

                    @Override
                    public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {

                        final Appointment appointment = pastAppointments.get(position);
                        holder.setData(
                                R.drawable.ic_event_black_24dp,
                                appointment.title,
                                appointment.date.toString()
                        );
                        holder.setOnClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                synchronized (LOCK) {
                                    if (startedAnotherActivity) {
                                        return;
                                    }

                                    startedAnotherActivity = true;
                                }

                                Intent intent = new Intent(getApplicationContext(), AppointmentActivity.class);
                                intent.putExtra(AppointmentActivity.APPOINTMENT_ID, appointment.id);
                                intent.putExtra(AppointmentActivity.ARRIVED_FROM_DOCTOR, true);
                                startActivityForResult(intent, AppointmentActivity.APPOINTMENT_INFO_REQ);
                            }
                        });
                    }

                    @Override
                    public int getItemCount() {
                        return pastAppointments.size();
                    }
                });
                pastAppointmentsRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                pastAppointmentsRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

                futureAppointmentsRv.setAdapter(new RecyclerView.Adapter<ListItemHolder>() {
                    @NonNull
                    @Override
                    public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return ListItemHolder.createHolder(parent);
                    }

                    @Override
                    public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {

                        final Appointment appointment = futureAppointments.get(position);
                        holder.setData(
                                R.drawable.ic_event_black_24dp,
                                appointment.title,
                                appointment.date.toString()
                        );
                        holder.setOnClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                synchronized (LOCK) {
                                    if (startedAnotherActivity) {
                                        return;
                                    }

                                    startedAnotherActivity = true;
                                }

                                Intent intent = new Intent(getApplicationContext(), AppointmentActivity.class);
                                intent.putExtra(AppointmentActivity.APPOINTMENT_ID, appointment.id);
                                intent.putExtra(AppointmentActivity.ARRIVED_FROM_DOCTOR, true);
                                startActivityForResult(intent, AppointmentActivity.APPOINTMENT_INFO_REQ);
                            }
                        });
                    }

                    @Override
                    public int getItemCount() {
                        return futureAppointments.size();
                    }
                });
                futureAppointmentsRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                futureAppointmentsRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
            }
        });


    }
}
