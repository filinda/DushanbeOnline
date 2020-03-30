package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.widget.FrameLayout;

import java.util.ArrayList;

import beka.com.bk.dushanbeonline.PlannerCalendar;

public class DayView extends ConstraintLayout {

    String[][] strings;
    int day, lang;
    String date;
    String resultForDrawing;
    Paint p;
    Paint ptext;
    ArrayList<TaskView> tasks;
    public int myHeight = 0;
    float scale;

    public DayView(Context context, int day, String date, int lang) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        this.day = day;
        this.date = date;
        this.lang = lang;
        strings = new String[2][7];
        strings[0][0] = "Monday";
        strings[1][0] = "Понедельник";
        strings[0][1] = "Tuesday";
        strings[1][1] = "Вторник";
        strings[0][2] = "Wednesday";
        strings[1][2] = "Среда";
        strings[0][3] = "Thursday";
        strings[1][3] = "Четверг";
        strings[0][4] = "Friday";
        strings[1][4] = "Пятница";
        strings[0][5] = "Saturday";
        strings[1][5] = "Суббота";
        strings[0][6] = "Sunday";
        strings[1][6] = "Воскресение";

        resultForDrawing = strings[lang][day] + " " + date;
        p = new Paint();
        p.setColor(Color.rgb(58, 203, 120));
        ptext = new Paint();
        ptext.setColor(Color.WHITE);
        ptext.setTextSize(50 * scale);
        ptext.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        setWillNotDraw(false);

        tasks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tasks.add(new TaskView(context, "" + 2, lang));
            tasks.get(i).setY(100 * scale + 504 * i * scale);
            addView(tasks.get(i));
        }

        setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) ((100 + tasks.size() * 504) * scale)));
        myHeight = (int) ((100 + tasks.size() * 504) * scale);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), (int) (100 * scale), p);
        Rect bounds = new Rect();
        ptext.getTextBounds(resultForDrawing, 0, resultForDrawing.length(), bounds);
        canvas.drawText(resultForDrawing, getWidth() / 2 - bounds.width() / 2, 70 * scale, ptext);

        super.onDraw(canvas);
    }
}
