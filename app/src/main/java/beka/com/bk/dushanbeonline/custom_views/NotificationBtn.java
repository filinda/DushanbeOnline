package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class NotificationBtn extends View {
    String label;
    Date date;
    String id;
    String type;
    int lang =0;
    static Bitmap arrow = null;
    Bitmap[] icons = null;
    float scale = 1;
    int iconType = 0;
    static Paint p = new Paint();
    static Paint pText = new Paint(), pDate = new Paint(),pLine = new Paint();
    static String[][] stringsDays = null;

    String dat;
    public NotificationBtn(Context context, String label, Date date, String id, String type, int lang) {
        super(context);

        this.lang = lang;

        if(stringsDays==null) {
            stringsDays = new String[2][7];
            stringsDays[0][0] = "Sun";
            stringsDays[1][0] = "Воскр";
            stringsDays[0][2] = "Tue";
            stringsDays[1][2] = "Вт";
            stringsDays[0][3] = "Wed";
            stringsDays[1][3] = "Ср";
            stringsDays[0][4] = "Thu";
            stringsDays[1][4] = "Четв";
            stringsDays[0][5] = "Fri";
            stringsDays[1][5] = "Пят";
            stringsDays[0][6] = "Sat";
            stringsDays[1][6] = "Суб";
            stringsDays[0][1] = "Mon";
            stringsDays[1][1] = "Пон";
        }

        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        pText.setColor(ResourseColors.colorBlack);
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        pText.setTextSize(45 * scale);

        pDate.setTextSize(37 * scale);
        pDate.setColor(Color.rgb(150, 150, 150));
        pDate.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));

        pLine.setColor(ResourseColors.colorLightGray2);
        pLine.setAntiAlias(true);

        if(arrow==null|icons==null){
            arrow = MainMenuActivity.getBitmapFromAsset(context, "arrow.png");
            arrow = Bitmap.createScaledBitmap(arrow,(int)(arrow.getWidth()*scale),(int)(arrow.getHeight()*scale), false);
            icons = new Bitmap[2];
            icons[0] = MainMenuActivity.getBitmapFromAsset(context, "icon_notif_system.png");
            icons[0] = Bitmap.createScaledBitmap(icons[0],(int)(icons[0].getWidth()*scale),(int)(icons[0].getHeight()*scale), false);
            icons[1] = MainMenuActivity.getBitmapFromAsset(context, "icon_notif_user.png");
            icons[1] = Bitmap.createScaledBitmap(icons[1],(int)(icons[1].getWidth()*scale),(int)(icons[1].getHeight()*scale), false);
        }
        this.date = date;
        this.id = id;
        this.label = label;
        this.type = type;
        setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(200*scale)));
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
        dat = stringsDays[lang][date.getDay()]+"  "+(format.format(date));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ResourseColors.colorWhite);
        canvas.drawBitmap(icons[iconType],83*scale-icons[iconType].getWidth()/2,getHeight()/2-icons[iconType].getHeight()/2,p);
        canvas.drawText(label,160*scale,90*scale,pText);
        canvas.drawText(dat,160*scale,153*scale,pDate);
        if(type!=null) {
            if (type.equals("adm"))
                canvas.drawBitmap(arrow, 1020 * scale - arrow.getWidth() / 2, getHeight() / 2 - arrow.getHeight() / 2, p);
        }
        canvas.drawRect(0,getHeight()-3*scale,getWidth(),getHeight(),pLine);
    }

}
