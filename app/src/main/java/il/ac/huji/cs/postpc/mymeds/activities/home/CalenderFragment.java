package il.ac.huji.cs.postpc.mymeds.activities.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.appointments.AppointmentActivity;
import il.ac.huji.cs.postpc.mymeds.activities.perceptions.PerceptionActivity;
import il.ac.huji.cs.postpc.mymeds.database.AppointmentManager;
import il.ac.huji.cs.postpc.mymeds.database.PerceptionManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Perception;
import il.ac.huji.cs.postpc.mymeds.utils.CalenderMap;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;


public class CalenderFragment extends Fragment {

    private CalenderFragmentListener listener;
    private RecyclerView recyclerView;
    private CalendarView calendarView;
    private EventAdapter eventAdapter;
    private PerceptionManager perceptionManager;
    private AppointmentManager appointmentManager;
    private CalenderMap calenderMap = new CalenderMap();
    private boolean startedAnotherActivity;
    private final Object LOCK = new Object();

    public CalenderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        recyclerView = view.findViewById(R.id.event_container);

        eventAdapter = new EventAdapter();
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        calendarView = view.findViewById(R.id.calender_view);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                List<Object> events = calenderMap.get(year, month, dayOfMonth);
                eventAdapter.update(events);
            }
        });

        calendarView.setDate(System.currentTimeMillis());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startedAnotherActivity = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        calenderMap = new CalenderMap();

        perceptionManager = ((MyMedApplication) context.getApplicationContext()).getPerceptionManager();
        appointmentManager = ((MyMedApplication) context.getApplicationContext()).getAppointmentManager();

        perceptionManager.getPerceptions(new PerceptionManager.PerceptionsListener() {
            @Override
            public void callback(List<Perception> perceptions) {
                for (Perception perception : perceptions) {
                    calenderMap.add(perception.start, perception.expire, perception);
                }
            }
        });

        appointmentManager.getAppointments(new AppointmentManager.AppointmentsListener() {
            @Override
            public void callback(List<Appointment> appointments) {
                for (Appointment appointment : appointments) {
                    calenderMap.add(appointment.date, appointment);
                }
            }
        });

        if (context instanceof CalenderFragmentListener) {
            listener = (CalenderFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        perceptionManager = null;
        appointmentManager = null;
    }

    public interface CalenderFragmentListener {}

    class EventAdapter extends RecyclerView.Adapter<ListItemHolder> {

        ArrayList<Object> events;
        EventAdapter() {
            this.events = new ArrayList<>();
        }

        void update(List<Object> events) {
            this.events.clear();
            this.events.addAll(events);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ListItemHolder.createHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {

            Object event = events.get(position);
            if (event instanceof Appointment) {

                final Appointment appointment = (Appointment) event;

                holder.setData(
                        R.drawable.ic_user_md_solid,
                        ((Appointment) event).title,
                        String.format("%s:%s", appointment.date.getHours(), appointment.date.getMinutes())
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

                        Intent intent = new Intent(getContext(), AppointmentActivity.class);
                        intent.putExtra(AppointmentActivity.APPOINTMENT_ID, appointment.id);
                        intent.putExtra(AppointmentActivity.ARRIVED_FROM_DOCTOR, true);
                        startActivityForResult(intent, AppointmentActivity.APPOINTMENT_INFO_REQ);

                    }
                });

            } else if (event instanceof Perception) {

                final Perception perception = (Perception) event;

                StringJoiner joiner = new StringJoiner(", ");
                for (String medName : perception.medicineNames) {
                    joiner.add(medName);
                }

                holder.setData(
                        R.drawable.ic_prescription_bottle_solid,
                        "Prescription",
                        joiner.toString()
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

                        Intent intent = new Intent(getContext(), PerceptionActivity.class);
                        intent.putExtra(PerceptionActivity.PERCEPTION_ID, perception.id);
                        intent.putExtra(PerceptionActivity.ARRIVED_FROM_DOCTOR, true);
                        startActivityForResult(intent, PerceptionActivity.PERCEPTION_INFO_REQ);

                    }
                });

            } else {
                holder.setData(
                        R.drawable.ic_event_black_24dp,
                        "Undefined type of event.",
                        ""
                );
            }


        }

        @Override
        public int getItemCount() {
            return events.size();
        }
    }
}
