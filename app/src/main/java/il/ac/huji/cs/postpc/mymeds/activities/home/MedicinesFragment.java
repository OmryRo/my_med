package il.ac.huji.cs.postpc.mymeds.activities.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;


public class MedicinesFragment extends Fragment {

    private MedicinesFragmentListener listener;
    private FloatingActionButton newMedicineFab;
    private RecyclerView recyclerView;
    private ReminderFragment reminderFragment;

    public MedicinesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicines, container, false);
        newMedicineFab = view.findViewById(R.id.medicines_add_fab);
        recyclerView = view.findViewById(R.id.medicines_container);

        reminderFragment = new ReminderFragment();
        reminderFragment.init(
                "You run out of something in few days.\nDon't forget to go to the phamacy.",
                new ReminderFragment.ReminderFragmentListener() {
                    @Override
                    public void onRemindMeLaterClicked() {
                        hide();
                    }

                    @Override
                    public void onDismissClicked() {
                        hide();
                    }

                    private void hide() {
                        reminderFragment.animateHide(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                getFragmentManager()
                                        .beginTransaction()
                                        .remove(reminderFragment)
                                        .commit();
                            }
                        });
                    }
                }
        );

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.medicines_reminder_container, reminderFragment)
                .commit();

        newMedicineFab.setOnClickListener(new View.OnClickListener() {
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
                        R.drawable.ic_tablets_solid,
                        "Some Medicine",
                        "3 times a day.\nNext time: 8 hours."
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
        if (context instanceof MedicinesFragmentListener) {
            listener = (MedicinesFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface MedicinesFragmentListener {}
}
