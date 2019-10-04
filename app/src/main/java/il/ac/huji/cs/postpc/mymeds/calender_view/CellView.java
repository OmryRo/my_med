package il.ac.huji.cs.postpc.mymeds.calender_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import il.ac.huji.cs.postpc.mymeds.R;
import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.vo.DayData;

public class CellView extends sun.bob.mcalendarview.views.DefaultCellView {

    private static Circle chosen;

    public TextView textView;
    private AbsListView.LayoutParams matchParentParams;
    public CellView(Context context) {
        super(context);
        initLayout();
    }

    public CellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    private void initLayout(){
        matchParentParams = new AbsListView.LayoutParams((int) CellConfig.cellWidth, (int) CellConfig.cellHeight);
        this.setLayoutParams(matchParentParams);
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);
        textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(textView);
    }

    @Override
    public void setDisplayText(DayData day) {
        textView.setText(day.getText());
    }

    @Override
    protected void onMeasure(int measureWidthSpec,int measureHeightSpec){
        super.onMeasure(measureWidthSpec, measureHeightSpec);
    }

    public void setMarked() {
        if (chosen == null) {
            chosen = new Circle(getContext().getColor(R.color.colorAccent));
        }

        setBackgroundDrawable(chosen);
        textView.setTextColor(Color.WHITE);
    }

    public void setDateToday(){
        textView.setTextColor(getContext().getColor(R.color.colorAccent));
    }

    public void setDateNormal() {
        textView.setTextColor(getContext().getColor(R.color.colorTextTextDark));
        setBackgroundDrawable(null);
    }

    public void setDateSomething() {
        textView.setTextColor(getContext().getColor(R.color.colorPrimary));
    }

    public void setDateOtherMonth() {
        textView.setTextColor(getContext().getColor(R.color.browser_actions_divider_color));
        setBackgroundDrawable(null);
    }

    public void setTextColor(String text, int color) {
        textView.setText(text);
        if (color != 0) {
            textView.setTextColor(color);
        }
    }

    public class Circle extends Drawable {
        private Paint paint;

        Circle(int color) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(color);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(canvas.getWidth() / 2,
                    canvas.getHeight() / 2,
                    canvas.getHeight() / 3,
                    paint);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    };
}