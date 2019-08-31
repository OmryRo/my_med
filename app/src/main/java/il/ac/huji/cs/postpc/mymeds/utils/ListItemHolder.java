package il.ac.huji.cs.postpc.mymeds.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import il.ac.huji.cs.postpc.mymeds.R;

public class ListItemHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView titleView;
    private TextView textView;

    public ListItemHolder(View view) {
        super(view);
        imageView = view.findViewById(R.id.item_image);
        titleView = view.findViewById(R.id.item_title);
        textView = view.findViewById(R.id.item_text);
    }

    public static ListItemHolder createHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ListItemHolder(view);
    }

    public void setData(int imageRes, String title, String text) {
        imageView.setImageResource(imageRes);
        titleView.setText(title);
        textView.setText(text);
    }
}
