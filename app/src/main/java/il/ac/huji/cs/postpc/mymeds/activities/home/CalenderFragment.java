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

import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;


public class CalenderFragment extends Fragment {

    private CalenderFragmentListener listener;
    private RecyclerView recyclerView;

    public CalenderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        recyclerView = view.findViewById(R.id.event_container);

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
                        "Appointment to Dr. Palony Almony",
                        "18:00-19:00"
                );
            }

            @Override
            public int getItemCount() {
                return 4;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    }

    public interface CalenderFragmentListener {}
}
