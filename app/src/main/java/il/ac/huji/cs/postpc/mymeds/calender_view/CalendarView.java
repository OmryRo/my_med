/**
 * https://github.com/SpongeBobSun/mCalendarView/blob/master/mcalendarview/src/main/java/sun/bob/mcalendarview/MCalendarView.java
 */
package il.ac.huji.cs.postpc.mymeds.calender_view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.utils.CalendarUtil;
import sun.bob.mcalendarview.utils.CurrentCalendar;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

public class CalendarView extends MCalendarView implements MonthFragment.NavigateButtonClickListener {
    private int dateCellViewResId = -1;
    private View dateCellView = null;
    private int markedStyle = -1;
    private int markedCellResId = -1;
    private View markedCellView = null;
    private boolean hasTitle = true;

    private boolean initted = false;

    private DateData currentDate;
    private CalendarViewAdapter adapter;

    private int width, height;
    private int currentIndex;

    public CalendarView(Context context) {
        super(context);
        if (context instanceof FragmentActivity){
            init((FragmentActivity) context);
        }
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof FragmentActivity){
            init((FragmentActivity) context);
        }
    }

    public void init(FragmentActivity activity){
        if (initted){
            return;
        }
        initted = true;

        if (currentDate == null){
            currentDate = CurrentCalendar.getCurrentDateData();
        }
        // TODO: 15/8/28 Will this cause trouble when achieved?
        if (this.getId() == View.NO_ID){
            this.setId(sun.bob.mcalendarview.R.id.calendarViewPager);
        }
        adapter = new CalendarViewAdapter(activity.getSupportFragmentManager(), this).setDate(currentDate);

        this.setAdapter(adapter);
        this.setCurrentItem(500);
//        addBackground();
        float density = getContext().getResources().getSystem().getDisplayMetrics().density;
        float cellWidth = getContext().getResources().getSystem().getDisplayMetrics().widthPixels / 7 / density;

        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //CellConfig.cellHeight = (int) (getContext().getResources().getSystem().getDisplayMetrics().widthPixels / 7 / density * 0.5);
            CellConfig.cellWidth = (int) (cellWidth * 0.5);
            CellConfig.cellHeight = (int) (cellWidth * 0.25);
        } else {
            CellConfig.cellWidth = (int) (cellWidth);
            CellConfig.cellHeight = (int) (cellWidth * 0.60);
        }
    }

    private void addBackground(){
        ShapeDrawable drawable = new ShapeDrawable(new RectShape());
        drawable.getPaint().setColor(Color.LTGRAY);
        drawable.getPaint().setStyle(Paint.Style.STROKE);
        this.setBackground(drawable);
    }

    public CalendarView travelTo(DateData dateData){

        if (dateData == null) {
            dateData = CurrentCalendar.getCurrentDateData();
        }

        this.currentDate = dateData;
        CalendarUtil.date = dateData;
        this.initted = false;
        init((FragmentActivity) getContext());
        return this;
    }

    public CalendarView markDate(int year, int month, int day){
        MarkedDates.getInstance().add(new DateData(year, month, day));
        return this;
    }

    public CalendarView unMarkDate(int year, int month, int day){
        MarkedDates.getInstance().remove(new DateData(year, month, day));
        return this;
    }

    public CalendarView markDate(DateData date){
        MarkedDates.getInstance().add(date);
        return this;
    }

    public CalendarView unMarkDate(DateData date){
        MarkedDates.getInstance().remove(date);
        return this;
    }

    public MarkedDates getMarkedDates(){
        return MarkedDates.getInstance();
    }

    public CalendarView setDateCell(int resId){
        adapter.setDateCellId(resId);
        return this;
    }

    public CalendarView setMarkedStyle(int style, int color){
        MarkStyle.current = style;
        MarkStyle.defaultColor = color;
        return this;
    }

    public CalendarView setMarkedStyle(int style){
        MarkStyle.current = style;
        return this;
    }

    public CalendarView setMarkedCell(int resId) {
        adapter.setMarkCellId(resId);
        return this;
    }

    public CalendarView setOnMonthChangeListener(OnMonthChangeListener listener){
        this.addOnPageChangeListener(listener);
        return this;
    }

    public CalendarView setOnDateClickListener(OnDateClickListener onDateClickListener){
        OnDateClickListener.instance = onDateClickListener;
        return this;
    }

    public CalendarView hasTitle(boolean hasTitle){
        this.hasTitle = hasTitle;
        adapter.setTitle(hasTitle);
        return this;
    }

    @Override
    protected void onMeasure(int measureWidthSpec,int measureHeightSpec){
        width = measureWidth(measureWidthSpec);
        height = measureHeight(measureHeightSpec);
        measureHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(measureWidthSpec, measureHeightSpec);
    }
    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specMode == MeasureSpec.AT_MOST) {
            float destiney = getContext().getResources().getSystem().getDisplayMetrics().density;
            result = (int) (CellConfig.cellWidth * 7 * destiney);
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) CellConfig.cellHeight;
        }
        return result;
    }
    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specMode == MeasureSpec.AT_MOST) {
            int columns = CalendarUtil.getWeekCount(currentIndex) + 1;
            columns = hasTitle ? columns + 1 : columns;
            float density = getContext().getResources().getSystem().getDisplayMetrics().density;
            result = (int) (CellConfig.cellHeight * columns * density);
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) CellConfig.cellHeight;
        }
        return result;
    }


    public void measureCurrentView(int currentIndex) {
        this.currentIndex = currentIndex;
        requestLayout();
    }

    @Override
    public void onForwardPressed() {
        int year = currentDate.getYear();
        int month = currentDate.getMonth();

        if (month == 12) {
            year++;
            month = 1;
        } else {
            month++;
        }

        travelTo(new DateData(year, month, 1));
    }

    @Override
    public void onBackPressed() {
        int year = currentDate.getYear();
        int month = currentDate.getMonth();

        if (month == 1) {
            year--;
            month = 12;
        } else {
            month--;
        }

        travelTo(new DateData(year, month, 1));
    }
}
