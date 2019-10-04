/**
 * https://github.com/SpongeBobSun/mCalendarView/blob/master/mcalendarview/src/main/java/sun/bob/mcalendarview/adapters/CalendarAdapter.java
 */
package il.ac.huji.cs.postpc.mymeds.calender_view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Observable;

import il.ac.huji.cs.postpc.mymeds.utils.CalenderMap;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.utils.CurrentCalendar;
import sun.bob.mcalendarview.vo.DayData;
import sun.bob.mcalendarview.vo.MarkedDates;
import sun.bob.mcalendarview.vo.MonthData;

public class CalendarAdapter extends sun.bob.mcalendarview.adapters.CalendarAdapter {
    private ArrayList data;
    private int month;
    private int cellView = -1;
    private int markView = -1;
    private CalenderMap calenderMap = CalenderMap.getInstance();

    public CalendarAdapter(Context context, int resource, MonthData data) {
        super(context, resource, data.getData());
        this.data = data.getData();
        this.month = data.getDate().getMonth();
        MarkedDates.getInstance().addObserver(this);
    }

    public CalendarAdapter setCellViews(int cellView, int markView){
        this.cellView = cellView;
        this.markView = markView;
        return this;
    }


    public View getView(int position, View convertView, ViewGroup viewGroup){
        CellView ret = null;
        DayData dayData = (DayData) data.get(position);
        MarkStyle style = MarkedDates.getInstance().check(dayData.getDate());
        boolean marked = style != null;
        boolean special = calenderMap.hasEvents(dayData.getDate().getYear(), dayData.getDate().getMonth(), dayData.getDate().getDay());

        ret = new CellView(getContext());
        ret.setDisplayText(dayData);
        ret.setDate(dayData.getDate());

        if (OnDateClickListener.instance != null) {
            ret.setOnDateClickListener(OnDateClickListener.instance);
        }
        if (marked) {
            ret.setMarked();
        } else if (dayData.getDate().equals(CurrentCalendar.getCurrentDateData())) {
            ret.setDateToday();
        } else if (dayData.getDate().getMonth() != month && position > 6) {
            ret.setDateOtherMonth();
        } else if (special) {
            ret.setDateSomething();
        } else {
            ret.setDateNormal();
        }
        return ret;
    }

    @Override
    public int getCount(){
        return data.size();
    }

    @Override
    public void update(Observable observable, Object data) {
        this.notifyDataSetChanged();
    }
}