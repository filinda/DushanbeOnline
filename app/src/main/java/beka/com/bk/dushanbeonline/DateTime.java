package beka.com.bk.dushanbeonline;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import beka.com.bk.dushanbeonline.custom_views.TaskEditorButton;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;


public class DateTime extends AppCompatActivity {

    FrameLayout layout;
    TextView timeTv, dateTv;
    Calendar dateAndTime;
    int myYear, myMonth, myDay, myHour, myMinute, startYear, startDay, startMonth, startHour, startMinute, endYear, endDay, endMonth, endHour, endMinute;
    int DIALOG_DATE_BEGIN = 1;
    int DIALOG_TIME_BEGIN = 2;
    int DIALOG_DATE_END = 3;
    int DIALOG_TIME_END = 4;
    String[][] strings, stringsDays;
    int lang = 1;
    float scale;
    Typeface type;
    TaskEditorButton btnDateBegin, btnTimeBegin, btnDateEnd, btnTimeEnd;
    TopLabel label;
    View space1, space2;
    CustomPublishBtn btnPublish;
    DisplayMetrics dm;
    TextView dtBegin, dtEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        layout = new FrameLayout(this);
        layout.setBackgroundColor(ResourseColors.colorWhite);
        setContentView(layout);

        if (lang == 1) {
            Locale myLocale = new Locale("ru", "RU");
            Locale.setDefault(myLocale);
        } else {
            Locale.setDefault(Locale.ENGLISH);
        }

        dm = getResources().getDisplayMetrics();

        strings = new String[2][10];
        strings[1][0] = "Назначьте время";
        strings[1][1] = "Когда задание должно быть выполнено?";
        strings[1][2] = "Дата завершения";
        strings[1][3] = "Дата";
        strings[1][4] = "Время";
        strings[1][5] = "c";
        strings[1][6] = "до";
        strings[1][7] = "Заполните все поля";
        strings[1][8] = "Введите корректную дату";
        strings[1][9] = "Готово";

        strings[0][0] = "Indicate date and time";
        strings[0][1] = "When the task must be completed?";
        strings[0][2] = "Date of finish";
        strings[0][3] = "Date";
        strings[0][4] = "Time";
        strings[0][5] = "from";
        strings[0][6] = "to";
        strings[0][7] = "Please fill all gaps";
        strings[0][8] = "Please input correct date";
        strings[0][9] = "Finish";


        stringsDays = new String[2][10];
        stringsDays[0][1] = "Sun";
        stringsDays[1][1] = "Воскр";
        stringsDays[0][3] = "Tue";
        stringsDays[1][3] = "Вт";
        stringsDays[0][4] = "Wed";
        stringsDays[1][4] = "Ср";
        stringsDays[0][5] = "Thu";
        stringsDays[1][5] = "Четв";
        stringsDays[0][6] = "Fri";
        stringsDays[1][6] = "Пят";
        stringsDays[0][0] = "Sat";
        stringsDays[1][0] = "Суб";
        stringsDays[0][2] = "Mon";
        stringsDays[1][2] = "Пон";

        scale = getResources().getDisplayMetrics().widthPixels / 1080f;
        design();

        timeTv = new TextView(this);
        timeTv.setY(100);
        layout.addView(timeTv);
        dateTv = new TextView(this);
        dateTv.setY(200);
        layout.addView(dateTv);
        Date nowDate = Calendar.getInstance().getTime();
        myHour = nowDate.getHours();
        myMinute = nowDate.getMinutes();
        myYear = Calendar.getInstance().get(Calendar.YEAR);
        myDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        myMonth = Calendar.getInstance().get(Calendar.MONTH);
    }

    private void design() {
        type = Typeface.createFromAsset(getAssets(), "font.ttf");

        label = new TopLabel(this, strings[lang][0], scale);
        layout.addView(label);

        label.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        space1 = new View(this);
        space1.setBackgroundColor(ResourseColors.colorGrayPrice);
        space1.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (93 * scale)));
        space1.setY(168 * scale);
        layout.addView(space1);

        dtBegin = new TextView(this);
        dtBegin.setText(strings[lang][1]);
        dtBegin.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        dtBegin.setTypeface(type);
        dtBegin.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        dtBegin.setTextColor(ResourseColors.colorDateText);
        dtBegin.setY(180*scale);
        layout.addView(dtBegin);


        btnDateBegin = new TaskEditorButton(this, MainMenuActivity.getBitmapFromAsset(this, "null.png"), MainMenuActivity.getBitmapFromAsset(this, "date_icon.png"), MainMenuActivity.getBitmapFromAsset(this, "date_icon_set.png"), strings[lang][3]);
        btnDateBegin.setY(285 * scale);
        layout.addView(btnDateBegin);
        btnDateBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog(DIALOG_DATE_BEGIN).show();
            }
        });

        btnTimeBegin = new TaskEditorButton(this, MainMenuActivity.getBitmapFromAsset(this, "null.png"), MainMenuActivity.getBitmapFromAsset(this, "date_icon.png"), MainMenuActivity.getBitmapFromAsset(this, "date_icon_set.png"), strings[lang][4]);
        btnTimeBegin.setY((285 + 166) * scale);
        btnTimeBegin.hasLine = false;
        layout.addView(btnTimeBegin);
        btnTimeBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnDateBegin.checkWarning()) {
                    onCreateDialog(DIALOG_TIME_BEGIN).show();
                }
            }
        });


        space2 = new View(this);
        space2.setBackgroundColor(ResourseColors.colorGrayPrice);
        space2.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (93 * scale)));
        space2.setY(630 * scale);
        layout.addView(space2);

        dtEnd = new TextView(this);
        //dtEnd.setText(strings[lang][2]);
        dtEnd.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        dtEnd.setTypeface(type);
        dtEnd.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        dtEnd.setTextColor(ResourseColors.colorDateText);
        dtEnd.setY(642*scale);
        layout.addView(dtEnd);


        btnPublish = new CustomPublishBtn(this, ResourseColors.colorGreen, strings[lang][9]);
        btnPublish.setX(0);
        btnPublish.setY(dm.heightPixels - 141 * scale);
        btnPublish.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (141 * scale)));
        layout.addView(btnPublish);
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(btnDateBegin.checkWarning() | btnTimeBegin.checkWarning())) {

                    Intent intent = new Intent();
                    intent.putExtra("date", btnDateBegin.label /*+ "  -  " + btnDateEnd.label*/);
                    intent.putExtra("sY", startYear);
                    intent.putExtra("sD", startDay);
                    intent.putExtra("sM", startMonth);
                    intent.putExtra("sH", startHour);
                    intent.putExtra("sMin", startMinute);
                    /*intent.putExtra("eY", endYear);
                    intent.putExtra("eD", endDay);
                    intent.putExtra("eM", endMonth);
                    intent.putExtra("eH", endHour);
                    intent.putExtra("eMin", endMinute);*/
                    setResult(2, intent);
                    onBackPressed();
                } else {
                    Toast.makeText(DateTime.this, strings[lang][7], Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE_BEGIN) {
            @SuppressLint("ResourceType") DatePickerDialog tpd = new DatePickerDialog(this, 2, myDateCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        if (id == DIALOG_TIME_BEGIN) {
            TimePickerDialog tpd = new TimePickerDialog(this, 2, myTimeCallBack, myHour, myMinute, true);
            return tpd;
        }
        /*if (id == DIALOG_DATE_END) {
            @SuppressLint("ResourceType") DatePickerDialog tpd = new DatePickerDialog(this, 2, myDateCallBackEnd, myYear, myMonth, myDay);
            return tpd;
        }
        if (id == DIALOG_TIME_END) {
            TimePickerDialog tpd = new TimePickerDialog(this, 2, myTimeCallBackEnd, myHour, myMinute, true);
            return tpd;
        }*/
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myTimeCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            Date nowDate = Calendar.getInstance().getTime();
            int nowHour = nowDate.getHours();
            int nowMinute = nowDate.getMinutes();
            int nowYear = Calendar.getInstance().get(Calendar.YEAR);
            int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
            nowDate.setYear(nowYear);
            nowDate.setDate(nowDay);
            nowDate.setMonth(nowMonth);
            nowDate.setHours(nowHour);
            nowDate.setMinutes(nowMinute);
            Date date = new Date();
            date.setYear(startYear);
            date.setMonth(startMonth);
            date.setDate(startDay);
            date.setHours(hourOfDay);
            date.setMinutes(minute);

            if (date.getTime() > nowDate.getTime()) {
                btnTimeBegin.setLabel(String.format("%tR", date));
                startHour = myHour;
                startMinute = myMinute;
            } else {
                Toast.makeText(DateTime.this, strings[lang][8], Toast.LENGTH_LONG).show();
                btnTimeBegin.p.setColor(Color.RED);
                btnTimeBegin.invalidate();
            }
        }
    };

    DatePickerDialog.OnDateSetListener myDateCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;

            Date date = new Date();
            date.setYear(year);
            date.setMonth(monthOfYear);
            date.setDate(dayOfMonth);
            date.setHours(23);
            date.setMinutes(59);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            Date nowDate = Calendar.getInstance().getTime();
            int nowHour = nowDate.getHours();
            int nowMinute = nowDate.getMinutes();
            int nowYear = Calendar.getInstance().get(Calendar.YEAR);
            int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
            nowDate.setYear(nowYear);
            nowDate.setDate(nowDay);
            nowDate.setMonth(nowMonth);
            nowDate.setMinutes(nowMinute);
            nowDate.setHours(nowHour);
            startDay = dayOfMonth;
            startYear = year;
            startMonth = monthOfYear;

            if (date.getTime() >= nowDate.getTime()) {
                btnDateBegin.setLabel(stringsDays[lang][dayOfWeek - 1] + "." + "  " + String.format("%td.%tm.%ty", date, date, date));
            } else {
                Toast.makeText(DateTime.this, strings[lang][8], Toast.LENGTH_LONG).show();
                btnDateBegin.p.setColor(Color.RED);
                btnDateBegin.invalidate();
            }
        }
    };

    TimePickerDialog.OnTimeSetListener myTimeCallBackEnd = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            Date nowDate = Calendar.getInstance().getTime();
            nowDate.setYear(startYear);
            nowDate.setDate(startDay);
            nowDate.setMonth(startMonth);
            nowDate.setHours(startHour);
            nowDate.setMinutes(startMinute);
            Date date = new Date();
            date.setYear(endYear);
            date.setMonth(endMonth);
            date.setDate(endDay);
            date.setHours(hourOfDay);
            date.setMinutes(minute);
            endHour = myHour;
            endMinute = myMinute;

            if (date.getTime() > nowDate.getTime()) {
                btnTimeEnd.setLabel(String.format("%tR", date));
                startHour = myHour;
                startMinute = myMinute;
            } else {
                Toast.makeText(DateTime.this, strings[lang][8], Toast.LENGTH_LONG).show();
                btnTimeEnd.p.setColor(Color.RED);
                btnTimeEnd.invalidate();
            }
        }
    };


    DatePickerDialog.OnDateSetListener myDateCallBackEnd = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;
            Date date = new Date();
            date.setYear(year);
            date.setMonth(monthOfYear);
            date.setDate(dayOfMonth);
            date.setHours(23);
            date.setMinutes(59);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            Date nowDate = Calendar.getInstance().getTime();
            nowDate.setYear(startYear);
            nowDate.setDate(startDay);
            nowDate.setMonth(startMonth);

            if (date.getTime() >= nowDate.getTime()) {
                btnDateEnd.setLabel(stringsDays[lang][dayOfWeek - 1] + "." + "  " + String.format("%td.%tm.%ty", date, date, date));
                endDay = dayOfMonth;
                endMonth = monthOfYear;
                endYear = year;
            } else {
                Toast.makeText(DateTime.this, strings[lang][8], Toast.LENGTH_LONG).show();
                btnDateEnd.p.setColor(Color.RED);
                btnDateEnd.invalidate();
            }
        }
    };
}

