package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import beka.com.bk.dushanbeonline.ResourseColors;


public class CustomRoundButton extends View {

    public int backGround1 = Color.RED, backGround2 = Color.BLUE, textColor = Color.BLACK;
    Paint pBack, pText, pBorder;
    public boolean fixed = false;
    public boolean presed = false;
    public boolean blocked = false;
    String text = "button";
    ValueAnimator animClose = new ValueAnimator();
    float scale;
    float rounding = 0;

    public CustomRoundButton(Context context) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;

        pBack = new Paint();
        pBack.setColor(backGround1);

        pText = new Paint();
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        pText.setTextSize(45*scale);
        pText.setColor(textColor);

        pBorder = new Paint();
        pBorder.setColor(ResourseColors.colorStroke);
        pBorder.setStyle(Paint.Style.STROKE);
        pBorder.setStrokeWidth(2*scale);
    }

    ValueAnimator press;
    float colorBalance = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(!blocked) {
            if (!presed) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tap();
                }
            } else {
                if (!fixed) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        unTap();
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


    public boolean question = false;

    public void tap(){
        if(!question){
            changeColor1();
        }
        press();
    }

    public void changeColor1(){
        if (press != null)
            press.cancel();
        press = new ValueAnimator().setDuration(200);
        press.setFloatValues(colorBalance, 1f);
        press.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                colorBalance = (float) animation.getAnimatedValue();
                float a = Color.alpha(backGround1) * (1 - colorBalance) + Color.alpha(backGround2) * (colorBalance);
                float r = Color.red(backGround1) * (1 - colorBalance) + Color.red(backGround2) * (colorBalance);
                float g = Color.green(backGround1) * (1 - colorBalance) + Color.green(backGround2) * (colorBalance);
                float b = Color.blue(backGround1) * (1 - colorBalance) + Color.blue(backGround2) * (colorBalance);
                pBack.setColor(Color.argb((int)a, (int) r, (int) g, (int) b));
                invalidate();
            }
        });
        press.start();
        presed = true;
    }

    public void changeColor2(){
        if (press != null)
            press.cancel();
        press = new ValueAnimator().setDuration(300);
        press.setFloatValues(colorBalance, 0f);
        press.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                colorBalance = (float) animation.getAnimatedValue();
                float a = Color.alpha(backGround1) * (1 - colorBalance) + Color.alpha(backGround2) * (colorBalance);
                float r = Color.red(backGround1) * (1 - colorBalance) + Color.red(backGround2) * (colorBalance);
                float g = Color.green(backGround1) * (1 - colorBalance) + Color.green(backGround2) * (colorBalance);
                float b = Color.blue(backGround1) * (1 - colorBalance) + Color.blue(backGround2) * (colorBalance);
                pBack.setColor(Color.argb((int)a, (int) r, (int) g, (int) b));
                invalidate();
            }
        });
        press.start();
    }

    public void unTap(){
        if(!question){
            changeColor2();
        }
        presed = false;
        unPress();
    }

    public void setBackGround1(int backGround1) {
        this.backGround1 = backGround1;
        float a = Color.alpha(backGround1) * (1 - colorBalance) + Color.alpha(backGround2) * (colorBalance);
        float r = Color.red(backGround1) * (1 - colorBalance) + Color.red(backGround2) * (colorBalance);
        float g = Color.green(backGround1) * (1 - colorBalance) + Color.green(backGround2) * (colorBalance);
        float b = Color.blue(backGround1) * (1 - colorBalance) + Color.blue(backGround2) * (colorBalance);
        pBack.setColor(Color.argb((int)a,(int) r, (int) g, (int) b));
        invalidate();
    }

    public void setBackGround2(int backGround2) {
        this.backGround2 = backGround2;
        float a = Color.alpha(backGround1) * (1 - colorBalance) + Color.alpha(backGround2) * (colorBalance);
        float r = Color.red(backGround1) * (1 - colorBalance) + Color.red(backGround2) * (colorBalance);
        float g = Color.green(backGround1) * (1 - colorBalance) + Color.green(backGround2) * (colorBalance);
        float b = Color.blue(backGround1) * (1 - colorBalance) + Color.blue(backGround2) * (colorBalance);
        pBack.setColor(Color.argb((int)a, (int) r, (int) g, (int) b));
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        pText.setColor(textColor);
        invalidate();
    }

    public void setText(String text, boolean fit) {
        this.text = text;
        if(fit) {
            Rect r = new Rect();
            pText.getTextBounds(text,0,text.length(),r);
            setLayoutParams(new FrameLayout.LayoutParams((int)(r.width()+40*scale),(int)(r.height()+70*scale)));
        }
        invalidate();
    }

    public void press(){

    }

    public void unPress(){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(rounding == 0) {
                canvas.drawRoundRect(pBorder.getStrokeWidth() / 2, pBorder.getStrokeWidth() / 2, getWidth() - pBorder.getStrokeWidth() / 2, getHeight() - pBorder.getStrokeWidth() / 2, getHeight() / 2 - pBorder.getStrokeWidth() / 2, getHeight() / 2 - pBorder.getStrokeWidth() / 2, pBorder);
                canvas.drawRoundRect(0, 0, getWidth(), getHeight(), getHeight() / 2, getHeight() / 2, pBack);
            }else{
                canvas.drawRoundRect(pBorder.getStrokeWidth() / 2, pBorder.getStrokeWidth() / 2, getWidth() - pBorder.getStrokeWidth() / 2, getHeight() - pBorder.getStrokeWidth() / 2, rounding, rounding, pBorder);
                canvas.drawRoundRect(0, 0, getWidth(), getHeight(), rounding, rounding, pBack);
            }
        }else{
            canvas.drawRect(pBorder.getStrokeWidth()/2,pBorder.getStrokeWidth()/2,getWidth()-pBorder.getStrokeWidth()/2,getHeight()-pBorder.getStrokeWidth()/2,pBorder);
            canvas.drawRect(0,0,getWidth(),getHeight(),pBack);
        }

        Rect bounds = new Rect();
        pText.getTextBounds(text,0,text.length(),bounds);
        canvas.drawText(text,getWidth()/2 - bounds.width()/2,getHeight()/2 + bounds.height()/2 - 6*scale,pText);

    }
}
