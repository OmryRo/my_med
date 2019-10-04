/**
 * https://github.com/SpongeBobSun/mCalendarView/blob/master/mcalendarview/src/main/java/sun/bob/mcalendarview/fragments/MonthFragment.java
 */
package il.ac.huji.cs.postpc.mymeds.calender_view;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import il.ac.huji.cs.postpc.mymeds.R;
import sun.bob.mcalendarview.views.MonthView;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MonthData;

public class MonthFragment extends sun.bob.mcalendarview.fragments.MonthFragment {

    public interface NavigateButtonClickListener {
        void onBackPressed();
        void onForwardPressed();
    }

    private MonthData monthData;
    private int cellView = -1;
    private int markView = -1;
    private boolean hasTitle = true;
    private NavigateButtonClickListener listener;

    MonthFragment(NavigateButtonClickListener listener) {
        this.listener = listener;
    }

    public void setData(MonthData monthData, int cellView, int markView) {
        this.monthData = monthData;
        this.cellView = cellView;
        this.markView = markView;
    }

    public void setTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        LinearLayout ret = new LinearLayout(getContext());
        ret.setOrientation(LinearLayout.VERTICAL);
        ret.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ret.setGravity(Gravity.TOP);

        DateData dateData = null;
        if ((monthData != null) && ((dateData = monthData.getDate()) != null)) {

            if (hasTitle) {

                LinearLayout title = new LinearLayout(getContext());
                title.setOrientation(LinearLayout.HORIZONTAL);
                title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                title.setGravity(Gravity.CENTER);


                Date date = new Date(dateData.getYear() - 1900, dateData.getMonth() - 1, dateData.getDay());

                TextView textView = new TextView(getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                textView.setText(
                        String.format("%s %s",
                                DateFormat.format("MMMM", date),
                                DateFormat.format("yyyy", date)
                        )
                );

                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);

                ImageView back = new ImageView(getContext());
                back.setLayoutParams(new LinearLayout.LayoutParams(85, 85));
                back.setImageResource(R.drawable.ic_chevron_left_black_24dp);
                back.setColorFilter(getContext().getColor(R.color.colorTextTextDark), PorterDuff.Mode.SRC_IN);
                back.setBackground(getContext().getDrawable(outValue.resourceId));
                back.setClickable(true);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onBackPressed();
                    }
                });

                ImageView forward = new ImageView(getContext());
                forward.setLayoutParams(new LinearLayout.LayoutParams(85, 85));
                forward.setImageResource(R.drawable.ic_chevron_right_black_24dp);
                forward.setColorFilter(getContext().getColor(R.color.colorTextTextDark), PorterDuff.Mode.SRC_IN);
                forward.setBackground(getContext().getDrawable(outValue.resourceId));
                forward.setClickable(true);
                forward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onForwardPressed();
                    }
                });

                title.addView(back);
                title.addView(textView);
                title.addView(forward);
                ret.addView(title);
            }
            MonthView monthView = new MonthView(getContext());
            monthView.setAdapter(new CalendarAdapter(getContext(), 1, monthData).setCellViews(cellView, markView));
            ret.addView(monthView);
        }
        return ret;
    }
}
