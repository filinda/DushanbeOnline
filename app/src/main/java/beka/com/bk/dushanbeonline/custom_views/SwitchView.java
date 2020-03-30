package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;

import beka.com.bk.dushanbeonline.MainMenuActivity;


public class SwitchView extends View implements View.OnClickListener {
    Bitmap on, off, oval;
    Paint p =new Paint();
    ValueAnimator onAnim, ofAnim;
    float scale=1, prescale = 0.7f;
    float offset = 0;
    public boolean isOn = false;
    public OnTurnListener turnListener;

    public SwitchView(Context context) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        on = MainMenuActivity.getBitmapFromAsset(getContext(),"on_switch.png");
        on = Bitmap.createScaledBitmap(on,(int)(on.getWidth()*scale*prescale),(int)(on.getHeight()*scale*prescale),false);

        off = MainMenuActivity.getBitmapFromAsset(getContext(),"off_switch.png");
        off = Bitmap.createScaledBitmap(off,(int)(off.getWidth()*scale*prescale),(int)(off.getHeight()*scale*prescale),false);

        oval = MainMenuActivity.getBitmapFromAsset(getContext(),"oval.png");
        oval = Bitmap.createScaledBitmap(oval,(int)(1.5*oval.getWidth()*scale*prescale),(int)(1.5*oval.getHeight()*scale*prescale),false);

        setLayoutParams(new FrameLayout.LayoutParams((int)(off.getWidth()*1.5f),(int)(off.getHeight()*1.5f)));
        setOnClickListener(this);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        p.setAlpha((int)(255*(offset/(off.getWidth()-oval.getWidth()))));
        canvas.drawBitmap(on,0,(oval.getHeight()-off.getHeight())/2,p);
        p.setAlpha((int)(255*(1 - offset/(off.getWidth()-oval.getWidth()))));
        canvas.drawBitmap(off,0,(oval.getHeight()-off.getHeight())/2,p);
        p.setAlpha(255);
        canvas.drawBitmap(oval,offset,0,p);
    }

    public void on(){
        isOn = true;
        if(turnListener!=null){
            turnListener.onTurn(isOn);
        }
        if(ofAnim!=null)
        ofAnim.cancel();
        onAnim = new ValueAnimator();
        onAnim.setDuration(250);
        onAnim.setFloatValues(offset,off.getWidth()-oval.getWidth());
        onAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (float)animation.getAnimatedValue();
                invalidate();
            }
        });
        onAnim.start();

    }

    public void off(){
        isOn = false;
        if(turnListener!=null){
            turnListener.onTurn(isOn);
        }
        if(onAnim!=null)
            onAnim.cancel();
        ofAnim = new ValueAnimator();
        ofAnim.setDuration(250);
        ofAnim.setFloatValues(offset,0);
        ofAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (float)animation.getAnimatedValue();
                invalidate();
            }
        });
        ofAnim.start();
    }



    @Override
    public void onClick(View v) {
        if(isOn){
            off();
        }else{
            on();
        }
    }
}


