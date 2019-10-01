package il.ac.huji.cs.postpc.mymeds.activities.home;

import android.content.Context;
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

                Appointment appointment = (Appointment) event;

                holder.setData(
                        R.drawable.ic_user_md_solid,
                        ((Appointment) event).title,
                        String.format("%s:%s", appointment.date.getHours(), appointment.date.getMinutes())
                );
            } else if (event instanceof Perception) {
                StringJoiner joiner = new StringJoiner(", ");
                for (String medName : ((Perception) event).medicineNames) {
                    joiner.add(medName);
                }

                holder.setData(
                        R.drawable.ic_prescription_bottle_solid,
                        "Prescription",
                        joiner.toString()
                );
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
