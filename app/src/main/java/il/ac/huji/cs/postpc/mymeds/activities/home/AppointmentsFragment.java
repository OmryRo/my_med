package il.ac.huji.cs.postpc.mymeds.activities.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.doctors.DoctorInfoActivity;
import il.ac.huji.cs.postpc.mymeds.database.entities.Doctor;
import il.ac.huji.cs.postpc.mymeds.database.DoctorManager;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;

public class AppointmentsFragment extends Fragment {

    private static final Object LOCK = new Object();

    private AppointmentsFragmentListener listener;
    private FloatingActionButton newDoctorFab;
    private RecyclerView recyclerView;
    private DoctorManager doctorManager;
    private View noDoctorMessage;

    private boolean startedAnotherActivity = false;

    public AppointmentsFragment() {}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        newDoctorFab = view.findViewById(R.id.doctors_add_fab);
        recyclerView = view.findViewById(R.id.doctors_container);
        noDoctorMessage = view.findViewById(R.id.no_doctors_message);

        newDoctorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (LOCK) {
                    if (startedAnotherActivity) {
                        return;
                    }

                    startedAnotherActivity = true;
                }
                startActivityForResult(new Intent(getContext(), DoctorInfoActivity.class), DoctorInfoActivity.DOCTOR_INFO_REQ);
            }
        });

        recyclerView.setAdapter(new RecyclerView.Adapter<ListItemHolder>() {
            @NonNull
            @Override
            public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return ListItemHolder.createHolder(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull ListItemHolder holder, final int position) {
                if (doctorManager == null) {
                    return;
                }

                final Doctor doctor = doctorManager.getByPos(position);
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
                return doctorManager == null ? 0 : doctorManager.size();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getAdapter().notifyDataSetChanged();
        startedAnotherActivity = false;
        toggleNoDoctorMessage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentsFragmentListener) {
            listener = (AppointmentsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        doctorManager = ((MyMedApplication) context.getApplicationContext()).getDoctorManager();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        doctorManager = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DoctorInfoActivity.DOCTOR_INFO_REQ && resultCode == DoctorInfoActivity.DOCTOR_INFO_DOCTORS_CHANGED) {
            toggleNoDoctorMessage();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void toggleNoDoctorMessage() {
        noDoctorMessage.setVisibility(doctorManager.size() == 0 ? View.VISIBLE : View.GONE);
    }

    public interface AppointmentsFragmentListener {}
}
