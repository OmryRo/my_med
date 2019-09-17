package il.ac.huji.cs.postpc.mymeds.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import il.ac.huji.cs.postpc.mymeds.R;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {
    ArrayList<TimeItem> data = new ArrayList<>();
    private Listener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeItem timeItem = data.get(position);
        holder.bind(timeItem);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    void addTime(TimeItem item) {
        data.add(item);
    }

    void removeTime(TimeItem item) {
        data.remove(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private EditText etAmount;
        private EditText etHoure;
        View view;

        public ViewHolder(@NonNull View v) {
            super(v);

            etAmount = (EditText) v.findViewById(R.id.et_amount_to_take);
            etHoure = (EditText) v.findViewById(R.id.et_hour_to_take_dose);
        }

        public void bind(final TimeItem item) {
            etHoure.setText(item.getHoure());
            etAmount.setText(item.getAmount());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemPressed(item);
                }
            });
        }
    }

    void onItemPressed(TimeItem item) {
        if (listener != null) {
            listener.onTimeItemClick(item);
        }
    }

    void setListener(Listener otherListener) {
        listener = otherListener;
    }

    interface Listener {
        void onTimeItemClick(TimeItem item);
    }
}
