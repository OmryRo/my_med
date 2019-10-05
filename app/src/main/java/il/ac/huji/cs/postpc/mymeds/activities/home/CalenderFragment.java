package il.ac.huji.cs.postpc.mymeds.activities.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.appointments.AppointmentActivity;
import il.ac.huji.cs.postpc.mymeds.activities.medicine.MedicineInfoActivity;
import il.ac.huji.cs.postpc.mymeds.activities.prescriptions.PrescriptionActivity;
import il.ac.huji.cs.postpc.mymeds.database.AppointmentManager;
import il.ac.huji.cs.postpc.mymeds.database.MedicineManager;
import il.ac.huji.cs.postpc.mymeds.database.PrescriptionManager;
import il.ac.huji.cs.postpc.mymeds.database.TreatmentManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Appointment;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.database.entities.Prescription;
import il.ac.huji.cs.postpc.mymeds.database.entities.Treatment;
import il.ac.huji.cs.postpc.mymeds.utils.CalenderMap;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;
import il.ac.huji.cs.postpc.mymeds.calender_view.CalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

public class CalenderFragment extends Fragment {

    private CalenderFragmentListener listener;
    private RecyclerView recyclerView;
    private CalendarView calendarView;
    private View noEventsMessage;
    private EventAdapter eventAdapter;
    private PrescriptionManager perceptionManager;
    private AppointmentManager appointmentManager;
    private TreatmentManager treatmentManager;
    private MedicineManager medicineManager;
    private CalenderMap calenderMap = CalenderMap.getInstance();
    private boolean startedAnotherActivity;
    private final Object LOCK = new Object();
    private int[] lastMarked;

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

        noEventsMessage = view.findViewById(R.id.no_events_message);

        calendarView = view.findViewById(R.id.calender_view);
        calendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                setEvents(date.getYear(), date.getMonth(), date.getDay());
            }
        });
        Date now = new Date();
        setEvents(now.getYear() + 1900, now.getMonth() + 1, now.getDate());

        return view;
    }

    synchronized void setEvents(int year, int month, int day) {
        MarkedDates.getInstance().removeAdd();
        lastMarked = new int[] {year, month, day};
        calendarView.markDate(year, month, day);

        try {
            calendarView.travelTo(new DateData(year, month, day));
        } catch (Exception e) {
            Log.e("CALENDER_FRAGMENT", "initData: ", e);
        }

        CalenderMap.Day events = calenderMap.get(year, month, day);
        eventAdapter.update(events);
        noEventsMessage.setVisibility(events.size() == 0 ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        startedAnotherActivity = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        perceptionManager = ((MyMedApplication) context.getApplicationContext()).getPerceptionManager();
        appointmentManager = ((MyMedApplication) context.getApplicationContext()).getAppointmentManager();
        treatmentManager = ((MyMedApplication) context.getApplicationContext()).getTreatmentManager();
        medicineManager =  ((MyMedApplication) context.getApplicationContext()).getMedicineManager();

        initData();

        if (context instanceof CalenderFragmentListener) {
            listener = (CalenderFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void initData() {
        calenderMap.clear();

        perceptionManager.getPrescriptions(new PrescriptionManager.PerceptionsListener() {
            @Override
            public void callback(List<Prescription> perceptions) {
                for (Prescription perception : perceptions) {
                    calenderMap.add(perception.start, perception.expire, perception);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lastMarked != null) {
                            setEvents(lastMarked[0], lastMarked[1], lastMarked[2]);
                        }
                    }
                });
            }
        });

        appointmentManager.getAppointments(new AppointmentManager.AppointmentsListener() {
            @Override
            public void callback(List<Appointment> appointments) {
                for (Appointment appointment : appointments) {
                    calenderMap.add(appointment.date, appointment);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lastMarked != null) {
                            setEvents(lastMarked[0], lastMarked[1], lastMarked[2]);
                        }
                    }
                });
            }
        });

        treatmentManager.getTreatments(new TreatmentManager.TreatmentsListener() {
            @Override
            public void callback(List<Treatment> treatments) {

                for (Treatment treatment : treatments) {
                    calenderMap.add(treatment.when, treatment);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lastMarked != null) {
                            setEvents(lastMarked[0], lastMarked[1], lastMarked[2]);
                        }
                    }
                });

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<Long, Medicine> medicineMap = medicineManager.getMedicines();
                for (Medicine medicine : medicineMap.values()) {

                    if (medicine.nextTime == null) {
                        continue;
                    }

                    Date nextTime = medicine.nextTime;
                    int maxRepeats = medicine.times == -1 ? 50 : medicine.times;

                    while ((medicine.endsAt == null || medicine.endsAt.after(nextTime)) && maxRepeats >= 0) {
                        calenderMap.add(nextTime, medicine);
                        maxRepeats--;
                        nextTime = medicine.each.addTo(nextTime);
                    }

                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lastMarked != null) {
                            setEvents(lastMarked[0], lastMarked[1], lastMarked[2]);
                        }
                    }
                });

            }
        }).start();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface CalenderFragmentListener {}

    class EventAdapter extends RecyclerView.Adapter<ListItemHolder> {

        ArrayList<CalenderMap.Event> events;
        EventAdapter() {
            this.events = new ArrayList<>();
        }

        void update(CalenderMap.Day events) {
            this.events.clear();

            for (int i = 0; i < events.size(); i++) {
                this.events.add(events.get(i));
            }

            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ListItemHolder.createHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {

            CalenderMap.Event event = events.get(position);
            if (event.event instanceof Appointment) {

                final Appointment appointment = (Appointment) event.event;

                holder.setData(
                        R.drawable.ic_user_md_solid,
                        appointment.title,
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
                        intent.putExtra(AppointmentActivity.ARRIVED_FROM_DOCTOR, false);
                        startActivityForResult(intent, AppointmentActivity.APPOINTMENT_INFO_REQ);

                    }
                });

            } else if (event.event instanceof Prescription) {

                final Prescription perception = (Prescription) event.event;

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

                        Intent intent = new Intent(getContext(), PrescriptionActivity.class);
                        intent.putExtra(PrescriptionActivity.PRESCRIPTION_ID, perception.id);
                        intent.putExtra(PrescriptionActivity.ARRIVED_FROM_DOCTOR, false);
                        startActivityForResult(intent, PrescriptionActivity.PRESCRIPTION_INFO_REQ);

                    }
                });

            } else if (event.event instanceof Treatment) {

                final Treatment treatment = (Treatment) event.event;
                final Medicine medicine = medicineManager.getById(treatment.medicine_id);

                holder.setData(
                        Medicine.medicineTypeToRes(medicine.type),
                        String.format(medicine.name),
                        String.format(
                                "%02d:%02d %s %s were taken.",
                                treatment.when.getHours(), treatment.when.getMinutes(),
                                treatment.amount, (medicine.type == Medicine.TYPE_PILLS ? "pills" : "units")
                        )
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

                        Intent intent = new Intent(getContext(), MedicineInfoActivity.class);
                        intent.putExtra(MedicineInfoActivity.INTENT_INDEX, medicine.id);
                        startActivityForResult(intent, MedicineInfoActivity.MEDICINE_INFO_REQ);

                    }
                });

            } else if (event.event instanceof Medicine) {

                final Medicine medicine = (Medicine) event.event;
                final int hours = event.hours;
                final int minutes = event.minutes;

                holder.setData(
                        Medicine.medicineTypeToRes(medicine.type),
                        String.format(medicine.name),
                        String.format(
                                "%02d:%02d take %s %s.",
                                hours, minutes, medicine.amount,
                                (medicine.type == Medicine.TYPE_PILLS ? "pills" : "units")
                        )
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

                        Intent intent = new Intent(getContext(), MedicineInfoActivity.class);
                        intent.putExtra(MedicineInfoActivity.INTENT_INDEX, medicine.id);
                        startActivityForResult(intent, MedicineInfoActivity.MEDICINE_INFO_REQ);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        initData();
    }
}
