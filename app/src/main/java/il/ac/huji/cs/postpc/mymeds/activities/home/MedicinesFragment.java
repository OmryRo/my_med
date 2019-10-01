package il.ac.huji.cs.postpc.mymeds.activities.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
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

import il.ac.huji.cs.postpc.mymeds.MyMedApplication;
import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.medicine.MedicineInfoActivity;
import il.ac.huji.cs.postpc.mymeds.activities.search.SearchMedicineActivity;
import il.ac.huji.cs.postpc.mymeds.database.MedicineManager;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;


public class MedicinesFragment extends Fragment {

    private MedicinesFragmentListener listener;
    private FloatingActionButton newMedicineFab;
    private RecyclerView recyclerView;
    private MedicineManager medicineManager;
    private boolean startedAnotherActivity;

    private static final Object LOCK = new Object();

    public MedicinesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicines, container, false);
        newMedicineFab = view.findViewById(R.id.medicines_add_fab);
        recyclerView = view.findViewById(R.id.medicines_container);

        newMedicineFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                synchronized (LOCK) {
                    if (startedAnotherActivity) {
                        return;
                    }

                    startedAnotherActivity = true;
                }

                startActivityForResult(
                        new Intent(getContext(), SearchMedicineActivity.class),
                        SearchMedicineActivity.SEACH_MEDICINE_REQUEST
                );
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
                if (medicineManager == null) {
                    return;
                }

                final Medicine medicine = medicineManager.getByPos(position);
                holder.setData(
                        medicine.type == Medicine.TYPE_PILLS ? R.drawable.ic_pills_solid : R.drawable.ic_syringe_solid,
                        medicine.name,
                        medicine.getDosageString()
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
            }

            @Override
            public int getItemCount() {
                return medicineManager == null ? 0 : medicineManager.size();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

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
        if (context instanceof MedicinesFragmentListener) {
            listener = (MedicinesFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        medicineManager = ((MyMedApplication) context.getApplicationContext()).getMedicineManager();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        medicineManager = null;
    }

    public interface MedicinesFragmentListener {}
}
