package beka.com.bk.dushanbeonline.custom_views;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import beka.com.bk.dushanbeonline.DateTime;
import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class DayPeriod extends View {
    public Date begin, end;

    int DIALOG_DATE_BEGIN = 1;
    int DIALOG_DATE_END = 2;
    int myYear, myMonth, myDay, myHour, myMinute;

    float offset, scale=1;

    Paint pRect, pLabel, pDate;

    Bitmap calendIcon, calendIconGray;

    String[][] strings;
    int lang;

    boolean isActive=false;

    SwitchView control;

    public DayPeriod(Context context, int lang) {
        super(context);

        strings = new String[2][3];
        strings[0][0] = "Wrong date";
        strings[1][0] = "Неверная дата";
        strings[0][1] = "Period of tasks";
        strings[1][1] = "Период проведения работ";

        this.lang = lang;
        scale = getContext().getResources().getDisplayMetrics().widthPixels/1080f;

        Date nowDate = Calendar.getInstance().getTime();
        myHour = nowDate.getHours();
        myMinute = nowDate.getMinutes();
        myYear = Calendar.getInstance().get(Calendar.YEAR);
        myDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        myMonth = Calendar.getInstance().get(Calendar.MONTH);

        offset = (1080-1016)*scale;

        pRect = new Paint();
        pRect.setColor(ResourseColors.colorLightGray);
        pRect.setStyle(Paint.Style.STROKE);
        pRect.setStrokeWidth(3*scale);

        pDate = new Paint();
        pDate.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"font_bold.ttf"));
        pDate.setColor(ResourseColors.colorBlue);
        pDate.setTextSize(45*scale);

        pLabel = new Paint();
        pLabel.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"font.ttf"));
        pLabel.setColor(ResourseColors.colorLightGray);
        pLabel.setTextSize(42*scale);

        calendIcon = MainMenuActivity.getBitmapFromAsset(getContext(),"date_icon_set.png");
        calendIcon = Bitmap.createScaledBitmap(calendIcon,(int)(calendIcon.getWidth()*scale), (int)(calendIcon.getHeight()*scale),false);

        calendIconGray = MainMenuActivity.getBitmapFromAsset(getContext(),"date_icon.png");
        calendIconGray = Bitmap.createScaledBitmap(calendIconGray,(int)(calendIconGray.getWidth()*scale), (int)(calendIconGray.getHeight()*scale),false);

        setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(220*scale)));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(isActive) {
            if (event.getX() < getWidth() / 2 & event.getAction() == MotionEvent.ACTION_UP) {
                onCreateDialog(DIALOG_DATE_BEGIN).show();
                return false;
            }
            if (event.getX() > getWidth() / 2 & event.getAction() == MotionEvent.ACTION_UP) {
                onCreateDialog(DIALOG_DATE_END).show();
                return false;
            }
            return true;
        }else {
              return super.dispatchTouchEvent(event);
        }
    }

    public void pair(SwitchView switchView){
        this.control = switchView;
        control.turnListener = new OnTurnListener(){
            @Override
            public void onTurn(boolean state) {
                isActive = state;
                setEnabled(state);
                invalidate();
                super.onTurn(state);
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float boxTop = 90;
        float boxHeight = 118;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(offset,boxTop*scale,getWidth()/2-offset,(boxTop+boxHeight)*scale,7*scale,7*scale,pRect);
            canvas.drawRoundRect(offset+getWidth()/2,boxTop*scale,getWidth()-offset,(boxTop+boxHeight)*scale,7*scale,7*scale,pRect);
        }

        if(isActive){
            canvas.drawBitmap(calendIcon, getWidth()/2 - offset*2,(boxTop+(boxTop+boxHeight))/2*scale-calendIcon.getHeight()/2,pDate);
            canvas.drawBitmap(calendIcon, getWidth() - offset*2,(boxTop+(boxTop+boxHeight))/2*scale-calendIcon.getHeight()/2,pDate);
            pDate.setColor(ResourseColors.colorBlue);
        }else{
            canvas.drawBitmap(calendIconGray, getWidth()/2 - offset*2,(boxTop+(boxTop+boxHeight))/2*scale-calendIconGray.getHeight()/2,pDate);
            canvas.drawBitmap(calendIconGray, getWidth() - offset*2,(boxTop+(boxTop+boxHeight))/2*scale-calendIconGray.getHeight()/2,pDate);
            pDate.setColor(ResourseColors.colorLightGray);
        }

        if(begin != null) {
            String beginFormated = String.format("%td.%tm.%ty", begin,begin,begin);
            Rect bounds = new Rect();
            pDate.getTextBounds(beginFormated, 0, beginFormated.length(), bounds);
            canvas.drawText(beginFormated, getWidth() / 4 - bounds.width() / 2, 165 * scale, pDate);
        }else{

        }

        if(end != null) {
            String endFormated = String.format("%td.%tm.%ty", end,end,end);
            Rect bounds = new Rect();
            pDate.getTextBounds(endFormated, 0, endFormated.length(), bounds);
            canvas.drawText(endFormated, getWidth()*3 / 4 - bounds.width() / 2 , 165 * scale, pDate);

        }else{

        }

        canvas.drawRect(getWidth()/2-5*scale,(boxTop+(boxTop+boxHeight))/2*scale - 2*scale,getWidth()/2+5*scale,(boxTop+(boxTop+boxHeight))/2*scale + 2*scale, pDate);

        canvas.drawText(strings[lang][1],offset/2,30*scale,pLabel);
    }

    protected DatePickerDialog onCreateDialog(int id) {

        if (id == DIALOG_DATE_BEGIN) {
            @SuppressLint("ResourceType") DatePickerDialog tpd = new DatePickerDialog(getContext(), 2, myDateBeginCallBack, myYear, myMonth, myDay);
            return tpd;
        }

        if (id == DIALOG_DATE_END) {
            @SuppressLint("ResourceType") DatePickerDialog tpd  = new DatePickerDialog(getContext(), 2, myDateEndCallBack, myYear, myMonth, myDay);
            return tpd;
        }

        return  null;
    }


    DatePickerDialog.OnDateSetListener myDateBeginCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            begin = new Date();
            begin.setYear(year);
            begin.setMonth(monthOfYear);
            begin.setDate(dayOfMonth);

            if(end!=null) {
                if (begin.getTime() > end.getTime()) {
                    Toast.makeText(getContext(),strings[lang][0],Toast.LENGTH_LONG).show();
                    begin = null;
                }
            }

            invalidate();
        }
    };

    DatePickerDialog.OnDateSetListener myDateEndCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            end = new Date();
            end.setYear(year);
            end.setMonth(monthOfYear);
            end.setDate(dayOfMonth);

            if(begin!=null) {
                if (begin.getTime() > end.getTime()) {
                    Toast.makeText(getContext(),strings[lang][0],Toast.LENGTH_LONG).show();
                    end = null;
                }
            }

            invalidate();

        }
    };

}
