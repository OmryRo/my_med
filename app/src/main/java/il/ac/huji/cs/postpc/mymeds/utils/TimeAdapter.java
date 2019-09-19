package il.ac.huji.cs.postpc.mymeds.utils;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import il.ac.huji.cs.postpc.mymeds.R;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {
    private ArrayList<TimeItem> data ;
    private Listener listener;
    private Context context;

    public  TimeAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setContext (ArrayList<TimeItem> context1) {
        data.clear();
        data.addAll(context1);
    }

    @NonNull
    @Override
    public TimeAdapter.TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_item, parent, false);

        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        holder.etAmount.setText(data.get(position).getAmount());
        holder.etHoure.setText(data.get(position).getHoure());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    static class TimeViewHolder extends RecyclerView.ViewHolder {
        private EditText etAmount;
        private EditText etHoure;
        private ImageButton deleteButtun;

        TimeViewHolder(@NonNull View v) {
            super(v);
            etAmount = (EditText) v.findViewById(R.id.et_amount_to_take);
            etHoure = (EditText) v.findViewById(R.id.et_hour_to_take_dose);
            deleteButtun = v.findViewById(R.id.bt_delete_time);
        }
    }

    public void onButtunPressed(TimeItem item) {
        if (listener != null) {
            listener.onTimeItemClick(item);
        }
    }

    public void setListener(Listener otherListener) {
        listener = otherListener;
    }

    interface Listener {
        void onTimeItemClick(TimeItem item);
    }
}
