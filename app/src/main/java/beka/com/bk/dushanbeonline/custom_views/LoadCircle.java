package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.FrameLayout;

import java.util.concurrent.TimeUnit;

import beka.com.bk.dushanbeonline.MainMenuActivity;

public class LoadCircle extends View {

    ValueAnimator animator;
    Paint p;
    public Bitmap bmp;
    int angle;
    float scale;
    boolean isShown = false;

    public LoadCircle(Context context) {
        super(context);
        p=new Paint();
        animator = new ValueAnimator();
        animator.setIntValues(360,0);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angle = (int)(animation.getAnimatedValue());
                invalidate();
            }
        });
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        bmp = MainMenuActivity.getBitmapFromAsset(getContext(),"rfre.png");
        bmp = Bitmap.createScaledBitmap(bmp,(int)(bmp.getWidth()*scale),(int)(bmp.getHeight()*scale),false);
        setLayoutParams(new FrameLayout.LayoutParams(bmp.getWidth(),bmp.getHeight()));
    }

    public void start(){
        isShown = true;
        animator.start();
    }

    public void stop(){
        isShown = false;
        animator.cancel();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(isShown) {
            canvas.rotate(angle, getWidth() / 2, getHeight() / 2);
            canvas.drawBitmap(bmp, 0, 0, p);
            canvas.rotate(-angle, getWidth() / 2, getHeight() / 2);
        }
    }


}
