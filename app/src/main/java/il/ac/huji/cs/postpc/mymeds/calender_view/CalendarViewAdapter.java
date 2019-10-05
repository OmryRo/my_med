/**
 * https://github.com/SpongeBobSun/mCalendarView/blob/master/mcalendarview/src/main/java/sun/bob/mcalendarview/adapters/CalendarViewAdapter.java
 */
package il.ac.huji.cs.postpc.mymeds.calender_view;

import android.content.Context;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import sun.bob.mcalendarview.utils.CalendarUtil;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MonthData;

public class CalendarViewAdapter extends sun.bob.mcalendarview.adapters.CalendarViewAdapter {

    private DateData date;

    private int dateCellId;
    private int markCellId;
    private boolean hasTitle = true;

    private Context context;
    private int mCurrentPosition = -1;
    private MonthFragment.NavigateButtonClickListener navigateButtonClickListener;

    public CalendarViewAdapter(FragmentManager fm, MonthFragment.NavigateButtonClickListener navigateButtonClickListener) {
        super(fm);
        this.navigateButtonClickListener = navigateButtonClickListener;
    }

    public CalendarViewAdapter setDate(DateData date){
        this.date = date;
        return this;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public CalendarViewAdapter setDateCellId(int dateCellRes){
        this.dateCellId =  dateCellRes;
        return this;
    }

    public int getDateCellId() {
        return dateCellId;
    }

    public CalendarViewAdapter setMarkCellId(int markCellId){
        this.markCellId = markCellId;
        return this;
    }

    public int getMarkCellId() {
        return markCellId;
    }

    @Override
    public Fragment getItem(int position) {
        int year = CalendarUtil.position2Year(position);
        int month = CalendarUtil.position2Month(position);

        MonthFragment fragment = new MonthFragment();
        fragment.setTitle(hasTitle);
        fragment.setListener(navigateButtonClickListener);
        MonthData monthData = new MonthData(new DateData(year, month, 1), hasTitle);
        fragment.setData(monthData, dateCellId, markCellId);
        return fragment;
    }
    @Override
    public int getCount() {
        return 1000;
    }

    public CalendarViewAdapter setTitle(boolean hasTitle){
        this.hasTitle = hasTitle;
        return this;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        ((CalendarView) container).measureCurrentView(position);
    }

}