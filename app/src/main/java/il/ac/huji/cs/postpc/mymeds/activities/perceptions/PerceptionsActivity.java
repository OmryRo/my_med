package il.ac.huji.cs.postpc.mymeds.activities.perceptions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.appointments.AppointmentActivity;
import il.ac.huji.cs.postpc.mymeds.database.AppointmentManager;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.PerceptionManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Perception;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;

public class PerceptionsActivity extends AppCompatActivity {

    public static final int PERCEPTIONS_REQ = 0x5001;
    public static final int PERCEPTIONS_RES = 0x5001;
    public static final String INTENT_DOCTOR_ID = "ID";

    private final Object LOCK = new Object();
    private boolean startedAnotherActivity = false;

    private DoctorManager doctorManager;
    private PerceptionManager perceptionManager;
    private Toolbar toolbar;
    private FloatingActionButton newPerceptionFab;
    private RecyclerView pastPerceptionsRv;
    private RecyclerView futurePerceptionsRv;
    private TextView pastPerceptionsTv;
    private long doctorId;
    private Doctor doctor;
    private ArrayList<Perception> pastPerceptions;
    private ArrayList<Perception> futurePerceptions;

    private TextView doctorNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorManager = ((MyMedApplication) getApplicationContext()).getDoctorManager();
        perceptionManager = ((MyMedApplication) getApplicationContext()).getPerceptionManager();

        setResult(PERCEPTIONS_RES);

        setContentView(R.layout.activity_perceptions);
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

        pastPerceptionsRv = findViewById(R.id.past_perceptions);
        futurePerceptionsRv = findViewById(R.id.future_perceptions);
        pastPerceptionsTv = findViewById(R.id.past_perceptions_tv);

        Intent intent = getIntent();
        doctorId = intent.getLongExtra(INTENT_DOCTOR_ID, -1);

        if (doctorId == -1) {
            throw new RuntimeException("bad doctor id");
        }

        newPerceptionFab = findViewById(R.id.perception_add_fab);
        newPerceptionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (LOCK) {
                    if (startedAnotherActivity) {
                        return;
                    }

                    startedAnotherActivity = true;
                }

                Intent intent = new Intent(getApplicationContext(), PerceptionActivity.class);
                intent.putExtra(PerceptionActivity.DOCTOR_ID, doctor.id);
                intent.putExtra(PerceptionActivity.ARRIVED_FROM_DOCTOR, true);
                startActivityForResult(intent, PerceptionActivity.PERCEPTION_INFO_REQ);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        startedAnotherActivity = false;

        doctor = doctorManager.getById(doctorId);
        doctorNameTv.setText(doctor.name);

        perceptionManager.getPerceptions(doctor, new PerceptionManager.PerceptionsListener() {
            @Override
            public void callback(List<Perception> perceptions) {
                futurePerceptions = new ArrayList<>();
                pastPerceptions = new ArrayList<>();

                Date now = new Date(System.currentTimeMillis());
                for (Perception perception : perceptions) {
                    if (perception.expire.after(now)) {
                        futurePerceptions.add(perception);
                    } else {
                        pastPerceptions.add(perception);
                    }
                }

                pastPerceptionsTv.setVisibility(pastPerceptions.size() == 0 ? View.GONE : View.VISIBLE);

                pastPerceptionsRv.setAdapter(new PerceptionAdapter(pastPerceptions));
                pastPerceptionsRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                pastPerceptionsRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

                futurePerceptionsRv.setAdapter(new PerceptionAdapter(futurePerceptions));
                futurePerceptionsRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                futurePerceptionsRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
            }
        });


    }

    private class PerceptionAdapter extends RecyclerView.Adapter<ListItemHolder> {

        private ArrayList<Perception> perceptions;

        PerceptionAdapter(ArrayList<Perception> perceptions) {
            this.perceptions = perceptions;
        }

        @NonNull
        @Override
        public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ListItemHolder.createHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {

            final Perception perception = perceptions.get(position);

            StringJoiner medicines = new StringJoiner(", ");
            for (String name : perception.medicineNames) {
                medicines.add(name);
            }

            String validaty = String.format("%s - %s", perception.start.toString(), perception.expire.toString());

            holder.setData(R.drawable.ic_prescription_bottle_solid, medicines.toString(), validaty);
            holder.setOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    synchronized (LOCK) {
                        if (startedAnotherActivity) {
                            return;
                        }

                        startedAnotherActivity = true;
                    }


                    Intent intent = new Intent(getApplicationContext(), PerceptionActivity.class);
                    intent.putExtra(PerceptionActivity.PERCEPTION_ID, perception.id);
                    intent.putExtra(PerceptionActivity.ARRIVED_FROM_DOCTOR, true);
                    startActivityForResult(intent, PerceptionActivity.PERCEPTION_INFO_REQ);

                }
            });
        }

        @Override
        public int getItemCount() {
            return perceptions.size();
        }
    }
}
