package il.ac.huji.cs.postpc.mymeds;

import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class ReminderFragment extends Fragment implements View.OnClickListener {

    private ReminderFragmentListener listener;
    private String message;
    private View view;

    public ReminderFragment() {}

    public void init(String message, ReminderFragmentListener listener) {
        this.message = message;
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reminder, container, false);
        TextView messageTv = view.findViewById(R.id.reminder_message_tv);
        Button laterBtn = view.findViewById(R.id.reminder_later_btn);
        Button dismissBtn = view.findViewById(R.id.reminder_dismiss_btn);

        messageTv.setText(message);
        laterBtn.setOnClickListener(this);
        dismissBtn.setOnClickListener(this);

        return view;
    }

    public void animateHide(AnimatorListenerAdapter listenerAdapter) {
        view.animate()
                .scaleX(0.0f)
                .scaleY(0.0f)
                .translationY(0.0f)
                .alpha(0.0f)
                .setListener(listenerAdapter);
    }

    public interface ReminderFragmentListener {
        void onRemindMeLaterClicked();
        void onDismissClicked();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reminder_later_btn:
                listener.onRemindMeLaterClicked();
                break;
            case R.id.reminder_dismiss_btn:
                listener.onDismissClicked();
                break;
        }
    }
}
