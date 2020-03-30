package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.widget.FrameLayout;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.PlannerCalendar;
import de.hdodenhof.circleimageview.CircleImageView;


public class TaskView extends ConstraintLayout {

    Bitmap phoneAct, phoneDis, worker, rangeIcon, calendarIcon;
    String cat, subcat, name, time, range, company, address;
    String[][] onlineStat;
    Typeface type;
    Paint usualText, onlineText, smallText, nameText;
    boolean isOnline;
    CircleImageView workerPhoto, phoneCall, calendar;
    int lang;
    float scale;

    public TaskView(Context context, String id, int lang) {
        super(context);

        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;

        this.lang = lang;
        setWillNotDraw(false);

        onlineStat = new String[2][2];
        onlineStat[0][0] = "Online";
        onlineStat[0][1] = "Offline";
        onlineStat[1][0] = "В сети";
        onlineStat[1][1] = "Не в сети";

        isOnline = true;
        name = "Иван Иванов";
        cat = MainMenuActivity.strings[lang][1];
        subcat = "Терапевт";
        time = "9:00";
        range = "3.00 км";
        company = "Больница Москвы №1";
        address = "Шаховский переулок д 30";

        phoneAct = MainMenuActivity.getBitmapFromAsset(context, "call_icon.png");
        phoneDis = MainMenuActivity.getBitmapFromAsset(context, "noactiv_call_icon.png");
        worker = MainMenuActivity.getBitmapFromAsset(context, "worker_icon.png");
        rangeIcon = MainMenuActivity.getBitmapFromAsset(context, "navigation_icon.png");
        calendarIcon = MainMenuActivity.getBitmapFromAsset(context, "calendar_icon.png");

        type = Typeface.createFromAsset(getContext().getAssets(),"font.ttf");

        usualText = new Paint();
        usualText.setTypeface(type);
        usualText.setTextSize(40 * scale);
        usualText.setColor(Color.rgb(100, 100, 100));
        usualText.setStrokeWidth(2);

        smallText = new Paint();
        smallText.setTypeface(type);
        smallText.setTextSize(30 * scale);
        smallText.setColor(Color.rgb(125, 125, 125));

        nameText = new Paint();
        nameText.setTypeface(type);
        nameText.setTextSize(45 * scale);
        nameText.setColor(Color.rgb(0, 0, 0));

        onlineText = new Paint();
        onlineText.setTypeface(type);
        onlineText.setTextSize(40 * scale);
        onlineText.setColor(Color.rgb(255, 0, 0));
        if (isOnline)
            onlineText.setColor(Color.rgb(62, 211, 123));

        workerPhoto = new CircleImageView(context);
        workerPhoto.setImageBitmap(worker);
        workerPhoto.setX(37 * scale);
        workerPhoto.setY(206 * scale);
        workerPhoto.setLayoutParams(new FrameLayout.LayoutParams((int) (163 * scale), (int) (163 * scale)));
        addView(workerPhoto);

        phoneCall = new CircleImageView(context);
        phoneCall.setImageBitmap(phoneDis);
        if (isOnline)
            phoneCall.setImageBitmap(phoneAct);
        phoneCall.setX(908 * scale);
        phoneCall.setY(300 * scale);
        phoneCall.setLayoutParams(new FrameLayout.LayoutParams((int) (131 * scale), (int) (131 * scale)));
        addView(phoneCall);

        calendar = new CircleImageView(context);
        calendar.setImageBitmap(calendarIcon);
        calendar.setX(908 * scale);
        calendar.setY(130 * scale);
        calendar.setLayoutParams(new FrameLayout.LayoutParams((int) (131 * scale), (int) (131 * scale)));
        addView(calendar);

        setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (504 * scale)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect bounds = new Rect();
        usualText.getTextBounds(cat + " - " + subcat, 0, (cat + " - " + subcat).length(), bounds);
        canvas.drawText(cat + " - " + subcat, getWidth() / 2 - bounds.width() / 2, 75 * scale, usualText);
        canvas.drawText(time, 37 * scale, 75 * scale, usualText);
        canvas.drawText(subcat, 240 * scale, 241 * scale, usualText);
        canvas.drawText(range, 282 * scale, 410 * scale, usualText);

        canvas.drawText(name, 37 * scale, 162 * scale, nameText);

        canvas.drawText(company, 240 * scale, 281 * scale, smallText);
        canvas.drawText(address, 240 * scale, 311 * scale, smallText);

        float xOnline, yOnline;
        xOnline = workerPhoto.getX() + workerPhoto.getMeasuredWidth() / 2;
        onlineText.getTextBounds(onlineStat[lang][0], 0, onlineStat[lang][0].length(), bounds);
        xOnline -= bounds.width() / 2;
        yOnline = workerPhoto.getY() + workerPhoto.getMeasuredHeight() + bounds.height() + 13 * scale;

        if (isOnline)
            canvas.drawText(onlineStat[lang][0], xOnline, yOnline, onlineText);
        else
            canvas.drawText(onlineStat[lang][1], xOnline, yOnline, onlineText);

        canvas.drawBitmap(rangeIcon, 240 * scale, 360 * scale, usualText);

        canvas.drawLine(-1, 502 * scale, 1081 * scale, 502 * scale, usualText);

        super.onDraw(canvas);
    }
}
