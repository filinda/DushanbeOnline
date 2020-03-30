package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;
import android.widget.FrameLayout;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;


public class RouteHeader extends View {

    private int lang = 0;
    private String address = "";
    public float range = 0;
    public float timeByWalk = 0;
    public float timeByCar = 0;
    String[][] strings;
    Paint pTextLight, pTextDark, pTextBold, pRects;
    float scale;
    Bitmap men, car;
    ValueAnimator animator;
    boolean animated = false;
    float adressOfset = 0;

    public RouteHeader(Context context, String address, int lang) {
        super(context);

        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;

        this.address = address;
        this.range = range;
        this.timeByWalk = timeByWalk;
        this.timeByCar = timeByCar;
        this.lang = lang;

        strings = new String[2][5];
        strings[0][0] = "Address:";
        strings[1][0] = "Адрес:";
        strings[0][1] = "Distance: ";
        strings[1][1] = "Расстояние: ";
        strings[0][2] = "km";
        strings[1][2] = "км";
        strings[0][3] = "min.";
        strings[1][3] = "мин.";
        strings[0][4] = "h.";
        strings[1][4] = "ч.";

        setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(280*scale)));
        setBackgroundColor(ResourseColors.colorWhite);

        pTextLight = new Paint();
        pTextLight.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        pTextLight.setTextSize(39*scale);
        pTextLight.setColor(ResourseColors.colorLightGray);

        pTextDark = new Paint();
        pTextDark.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        pTextDark.setTextSize(39*scale);
        pTextDark.setColor(ResourseColors.colorBlack);

        pTextBold = new Paint();
        pTextBold.setTypeface(Typeface.createFromAsset(context.getAssets(),"font_bold.ttf"));
        pTextBold.setTextSize(45*scale);
        pTextBold.setColor(ResourseColors.colorBlack);

        pRects = new Paint();
        pRects.setColor(ResourseColors.colorWhite);

        men = MainMenuActivity.getBitmapFromAsset(getContext(),"human.png");
        men = Bitmap.createScaledBitmap(men,(int)(men.getWidth()*scale),(int)(men.getHeight()*scale),false);

        car = MainMenuActivity.getBitmapFromAsset(getContext(),"car_icon.png");
        car = Bitmap.createScaledBitmap(car,(int)(car.getWidth()*scale),(int)(car.getHeight()*scale),false);

        Rect boundAddress  = new Rect();
        pTextDark.getTextBounds(address,0,address.length(),boundAddress);
        if(boundAddress.width()>(1080-267)*scale){
            animated = true;
            animator = new ValueAnimator();
            animator.setFloatValues(0,((1080-267)*scale)-boundAddress.width(),((1080-267)*scale)-boundAddress.width(),0,0);
            animator.setDuration((int)-(((1080-267)*scale)-boundAddress.width())*30);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    adressOfset = (float)animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float ofset = 0;
        float length = (403-168)*scale;

        Rect bounds3 = new Rect();

        pTextLight.getTextBounds(strings[lang][1],0,strings[lang][1].length(),bounds3);
        length+=bounds3.width();
        pTextBold.getTextBounds(String.format("%.1f",range)+" "+strings[lang][2],0,(String.format("%.1f",range)+" "+strings[lang][2]).length(),bounds3);
        length+=bounds3.width();

        Rect walkbound = new Rect();
        if(timeByWalk<60){
            pTextDark.getTextBounds((String.format("%d",(int)timeByWalk)+" "+strings[lang][3]),0,(String.format("%d",(int)timeByWalk)+" "+strings[lang][3]).length(),walkbound);
            length += walkbound.width();
        }else{
            pTextDark.getTextBounds((String.format("%d", (int)timeByWalk/60)+" "+strings[lang][4] +" "+String.format("%d", (int)timeByWalk%60)+" "+strings[lang][3]),0,(String.format("%d", (int)timeByWalk/60)+" "+strings[lang][4] +" "+String.format("%d", (int)timeByWalk%60)+" "+strings[lang][3]).length(),walkbound);
            length += walkbound.width();
        }


        if(timeByCar<60){
            Rect bound = new Rect();
            pTextDark.getTextBounds((String.format("%d",(int)timeByCar)+" "+strings[lang][3]),0,(String.format("%d",(int)timeByCar)+" "+strings[lang][3]).length(),bound);
            length += bound.width();
            ofset = (1080*scale)-length/2 - 675*scale;
            canvas.drawText(String.format("%d",(int)timeByCar)+" "+strings[lang][3],ofset+850*scale- 168*scale + walkbound.width(),219*scale,pTextDark);
        }else{
            Rect bound = new Rect();
            pTextDark.getTextBounds((String.format("%d", (int)timeByCar/60)+" "+strings[lang][4] +" "+String.format("%d", (int)timeByCar%60)+" "+strings[lang][3]),0,(String.format("%d", (int)timeByCar/60)+" "+strings[lang][4] +" "+String.format("%d", (int)timeByCar%60)+" "+strings[lang][3]).length(),bound);
            length += bound.width();
            ofset = (1080*scale)-length/2- 675*scale;
            canvas.drawText(String.format("%d", (int)timeByCar/60)+" "+strings[lang][4] +" "+String.format("%d", (int)timeByCar%60)+" "+strings[lang][3],ofset+850*scale- 168*scale + walkbound.width(),219*scale,pTextDark);
        }

        canvas.drawBitmap(men,ofset+479*scale,177*scale,pTextBold);
        if(timeByWalk<60){
            canvas.drawText(String.format("%d",(int)timeByWalk)+" "+strings[lang][3],ofset+541*scale,219*scale,pTextDark);
        }else{
            canvas.drawText(String.format("%d", (int)timeByWalk/60)+" "+strings[lang][4] +" "+String.format("%d", (int)timeByWalk%60)+" "+strings[lang][3],ofset+541*scale,219*scale,pTextDark);
        }

        canvas.drawBitmap(car,ofset+750*scale - 168*scale + walkbound.width(),180*scale,pTextBold);


      //  length +=

        Rect bounds = new Rect(),bounds2=new Rect();
        pTextLight.getTextBounds(strings[lang][0],0,strings[lang][0].length(),bounds);
        pTextDark.getTextBounds(address,0,address.length(),bounds2);
        if(!animated) {
            canvas.drawText(strings[lang][0], getWidth() / 2 - (bounds.width() + bounds2.width() + 33 * scale) / 2, 86 * scale, pTextLight);
            canvas.drawText(address, getWidth() / 2 - (bounds.width() + bounds2.width() + 33 * scale) / 2 + bounds.width() + 33 * scale, 86 * scale, pTextDark);
        }else{
            canvas.drawText(address, getWidth() / 2 - (bounds.width() +(1080-267)*scale + 33 * scale) / 2 + bounds.width() + 33 * scale +adressOfset, 86 * scale, pTextDark);
            canvas.drawRect(0,0,getWidth() / 2 - (bounds.width() +(1080-267)*scale + 33 * scale) / 2 + bounds.width() + 33 * scale,getHeight()/2-20*scale,pRects);
            canvas.drawRect(getWidth() / 2 - (bounds.width() +(1080-267)*scale + 33 * scale) / 2 + bounds.width() + 70 * scale+(1080-267)*scale,0,getWidth(),getHeight()/2-20*scale,pRects);
            canvas.drawText(strings[lang][0], getWidth() / 2 - (bounds.width() + (1080-267)*scale + 33 * scale) / 2, 86 * scale, pTextLight);
        }

        pTextLight.getTextBounds(strings[lang][1],0,strings[lang][1].length(),bounds);
        pTextBold.getTextBounds(String.format("%.1f",range)+" "+strings[lang][2],0,(String.format("%.1f",range)+" "+strings[lang][2]).length(),bounds2);

        canvas.drawRect(0,getHeight()/2,getWidth(),getHeight()/2+2*scale,pTextLight);
        canvas.drawText(strings[lang][1],ofset+(426)*scale - bounds.width()- bounds2.width(),219*scale,pTextLight);
        canvas.drawText(String.format("%.1f",range)+" "+strings[lang][2],ofset+(448)*scale - bounds2.width(),219*scale,pTextBold);




    }
}
