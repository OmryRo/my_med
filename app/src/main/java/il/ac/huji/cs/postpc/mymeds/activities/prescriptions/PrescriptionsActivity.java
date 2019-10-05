package il.ac.huji.cs.postpc.mymeds.activities.prescriptions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.database.PrescriptionManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.entities.Prescription;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;
import il.ac.huji.cs.postpc.mymeds.utils.Utils;

public class PrescriptionsActivity extends AppCompatActivity {

    public static final int PERCEPTIONS_REQ = 0x5001;
    public static final int PERCEPTIONS_RES = 0x5001;
    public static final int PERCEPTIONS_DOCTOR_DELECTED = 0x5002;
    public static final String INTENT_DOCTOR_ID = "ID";

    private final Object LOCK = new Object();
    private boolean startedAnotherActivity = false;

    private DoctorManager doctorManager;
    private PrescriptionManager perceptionManager;
    private Toolbar toolbar;
    private View noPrescriptionsMessage;
    private FloatingActionButton newPerceptionFab;
    private RecyclerView pastPerceptionsRv;
    private RecyclerView futurePerceptionsRv;
    private TextView pastPerceptionsTv;
    private long doctorId;
    private Doctor doctor;
    private PerceptionAdapter pastAdapter;
    private PerceptionAdapter futureAdapter;

    private TextView doctorNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorManager = ((MyMedApplication) getApplicationContext()).getDoctorManager();
        perceptionManager = ((MyMedApplication) getApplicationContext()).getPerceptionManager();

        setResult(PERCEPTIONS_RES);

        setContentView(R.layout.activity_prescriptions);
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

        pastPerceptionsRv = findViewById(R.id.past_prescriptions);
        futurePerceptionsRv = findViewById(R.id.future_prescriptions);
        pastPerceptionsTv = findViewById(R.id.past_prescriptions_tv);

        noPrescriptionsMessage = findViewById(R.id.no_prescriptions_message);

        Intent intent = getIntent();
        doctorId = intent.getLongExtra(INTENT_DOCTOR_ID, -1);

        if (doctorId == -1) {
            finish();
            return;
        }

        newPerceptionFab = findViewById(R.id.prescription_add_fab);
        newPerceptionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (LOCK) {
                    if (startedAnotherActivity) {
                        return;
                    }

                    startedAnotherActivity = true;
                }

                Intent intent = new Intent(getApplicationContext(), PrescriptionActivity.class);
                intent.putExtra(PrescriptionActivity.DOCTOR_ID, doctor.id);
                intent.putExtra(PrescriptionActivity.ARRIVED_FROM_DOCTOR, true);
                startActivityForResult(intent, PrescriptionActivity.PRESCRIPTION_INFO_REQ);

            }
        });

        pastAdapter = new PerceptionAdapter();
        pastPerceptionsRv.setAdapter(pastAdapter);
        pastPerceptionsRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        pastPerceptionsRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        futureAdapter = new PerceptionAdapter();
        futurePerceptionsRv.setAdapter(futureAdapter);
        futurePerceptionsRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        futurePerceptionsRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

    }

    @Override
    protected void onResume() {
        super.onResume();
        startedAnotherActivity = false;

        doctor = doctorManager.getById(doctorId);

        if (doctor == null) {
            finish();
            return;
        }

        doctorNameTv.setText(doctor.name);

        perceptionManager.getPrescriptions(doctor, new PrescriptionManager.PerceptionsListener() {
            @Override
            public void callback(final List<Prescription> perceptions) {
                final ArrayList<Prescription> futurePerceptions = new ArrayList<>();
                final ArrayList<Prescription> pastPerceptions = new ArrayList<>();

                Date now = new Date(System.currentTimeMillis());
                for (Prescription perception : perceptions) {
                    if (perception.expire.after(now)) {
                        futurePerceptions.add(perception);
                    } else {
                        pastPerceptions.add(perception);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pastPerceptionsTv.setVisibility(pastPerceptions.size() == 0 ? View.GONE : View.VISIBLE);
                        pastAdapter.update(pastPerceptions);
                        futureAdapter.update(futurePerceptions);
                        noPrescriptionsMessage.setVisibility(perceptions.size() == 0 ? View.VISIBLE : View.GONE);
                    }
                });

            }
        });


    }

    private class PerceptionAdapter extends RecyclerView.Adapter<ListItemHolder> {

        private ArrayList<Prescription> perceptions;

        PerceptionAdapter() {
            this.perceptions = new ArrayList<>();
        }

        public synchronized void update(ArrayList<Prescription> perceptions) {
            this.perceptions.clear();
            this.perceptions.addAll(perceptions);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ListItemHolder.createHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {

            final Prescription perception = perceptions.get(position);

            StringJoiner medicines = new StringJoiner(", ");
            for (String name : perception.medicineNames) {
                medicines.add(name);
            }

            String validaty = String.format(
                    "%s - %s",
                    Utils.dateToHumanReadabily(perception.start, false),
                    Utils.dateToHumanReadabily(perception.expire, false));

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


                    Intent intent = new Intent(getApplicationContext(), PrescriptionActivity.class);
                    intent.putExtra(PrescriptionActivity.PRESCRIPTION_ID, perception.id);
                    intent.putExtra(PrescriptionActivity.ARRIVED_FROM_DOCTOR, true);
                    startActivityForResult(intent, PrescriptionActivity.PRESCRIPTION_INFO_REQ);

                }
            });
        }

        @Override
        public int getItemCount() {
            return perceptions.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == PrescriptionActivity.PRESCRIPTION_INFO_DOCTOR_DELETED) {
            setResult(PERCEPTIONS_DOCTOR_DELECTED);
            finish();
        }
    }
}
