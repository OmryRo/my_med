package il.ac.huji.cs.postpc.mymeds;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class AppointmentsFragment extends Fragment {

    private AppointmentsFragmentListener listener;
    private FloatingActionButton newDoctorFab;
    private RecyclerView recyclerView;
    private ReminderFragment reminderFragment;

    public AppointmentsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        newDoctorFab = view.findViewById(R.id.doctors_add_fab);
        recyclerView = view.findViewById(R.id.doctors_container);

        reminderFragment = new ReminderFragment();
        reminderFragment.init(
                "You didn't visit Dr. Palony in the last 3 months.",
                new ReminderFragment.ReminderFragmentListener() {
                    @Override
                    public void onRemindMeLaterClicked() {

                    }

                    @Override
                    public void onDismissClicked() {

                    }
                }
        );

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.appointments_reminder_container, reminderFragment)
                .commit();

        newDoctorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        recyclerView.setAdapter(new RecyclerView.Adapter<ListItemHolder>() {
            @NonNull
            @Override
            public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return ListItemHolder.createHolder(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {
                holder.setData(
                        R.drawable.ic_user_md_solid,
                        "Dr. Palony Almony",
                        "Appointment at Friday 10:00."
                );
            }

            @Override
            public int getItemCount() {
                return 10;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        return view;
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface AppointmentsFragmentListener {}
}
