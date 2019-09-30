package il.ac.huji.cs.postpc.mymeds.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import il.ac.huji.cs.postpc.mymeds.R;

public class PerceptionMedicineHolder extends RecyclerView.ViewHolder {

    private View view;
    private ImageView imageView;
    private TextView titleView;
    private ImageView deleteView;

    public PerceptionMedicineHolder(View view) {
        super(view);
        this.view = view;
        this.imageView = view.findViewById(R.id.item_image);
        this.titleView = view.findViewById(R.id.item_title);
        this.deleteView = view.findViewById(R.id.item_remove);
    }

    public static PerceptionMedicineHolder createHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine_in_perception, parent, false);
        return new PerceptionMedicineHolder(view);
    }

    public void setData(int imageRes, String title, View.OnClickListener deleteClickListener) {
        imageView.setImageResource(imageRes);
        titleView.setText(title);
        deleteView.setOnClickListener(deleteClickListener);
    }

    public void setOnClick(View.OnClickListener listener) {
        view.setOnClickListener(listener);
    }
}
