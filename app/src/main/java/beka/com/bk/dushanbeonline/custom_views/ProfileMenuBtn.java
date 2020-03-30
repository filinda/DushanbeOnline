package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.util.Strings;

import beka.com.bk.dushanbeonline.ResourseColors;


public class ProfileMenuBtn extends View {

    Paint p, pText;
    String label;
    Bitmap icon;
    float scale;
    ValueAnimator animatorDown, animatorUp;

    public ProfileMenuBtn(Context context, Bitmap icon, String label) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        this.icon = icon;
        this.label = label;

        p = new Paint();
        p.setColor(ResourseColors.colorGrayProfLine);

        pText = new Paint();
        pText.setColor(ResourseColors.colorStroke);
        pText.setTextSize(50*scale);
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));

        setBackgroundColor(ResourseColors.colorGrayProfBack);

        setLayoutParams(new FrameLayout.LayoutParams((int)((935-16)*scale),(int)(125*scale)));

        int colorFrom = ResourseColors.colorGrayProfBack;
        int colorTo = ResourseColors.colorGrayProfLine;
        animatorDown = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        animatorDown.setDuration(250); // milliseconds
        animatorDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        animatorUp = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
        animatorUp.setDuration(250); // milliseconds
        animatorUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                setBackgroundColor((int) animator.getAnimatedValue());
            }

        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            animatorDown.start();
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            animatorUp.start();
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,getWidth(),3*scale,p);
        canvas.drawText(label,119*scale,(125-46)*scale,pText);
        canvas.drawBitmap(icon,60*scale-icon.getWidth()/2,getHeight()/2-icon.getHeight()/2,p);
    }
}
