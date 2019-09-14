package il.ac.huji.cs.postpc.mymeds.activities.doctors;

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

import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.database.AppointmentManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;

public class AppointmentsActivity extends AppCompatActivity {

    private RecyclerView upcoming_recyclerView;
    private RecyclerView past_recyclerView;
    private AppointmentManager appointmentManager;
    private static final Object LOCK = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        upcoming_recyclerView = findViewById(R.id.upcoming_appointments_container);
        past_recyclerView = findViewById(R.id.past_appointments_container);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.appointment_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationContentDescription(R.string.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        upcoming_recyclerView.setAdapter(new RecyclerView.Adapter<ListItemHolder>() {
            @NonNull
            @Override
            public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return ListItemHolder.createHolder(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull ListItemHolder holder, final int position) {
                if (appointmentManager == null) {
                    return;
                }

                final Doctor doctor = appointmentManager.getByPos(position);
                holder.setData(
                        R.drawable.ic_user_md_solid,
                        doctor.name,
                        doctor.note
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

                        Intent intent = new Intent(getContext(), DoctorInfoActivity.class);
                        intent.putExtra(DoctorInfoActivity.INTENT_INDEX, doctor.id);
                        startActivityForResult(intent, DoctorInfoActivity.DOCTOR_INFO_REQ);

                    }
                });
            }

            @Override
            public int getItemCount() {
                return appointmentManager == null ? 0 : appointmentManager.size();
            }
        });
        upcoming_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        upcoming_recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

    }
}
