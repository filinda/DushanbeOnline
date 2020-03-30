package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import beka.com.bk.dushanbeonline.ResourseColors;

public class ChooserPrice extends View {

    public String on, off;
    public Paint pOn, pOff, pOnText, pOffText;
    public int onColor, offColor;
    public boolean isOn;
    public changeListener changeListener;
    public boolean orentHoriz = false;

    public ChooserPrice(Context context, String on, String off, Typeface type, float textSize) {
        super(context);
        this.on = on;
        this.off = off;
        onColor = ResourseColors.colorBlue;
        offColor = ResourseColors.colorGrayPrice;
        pOn = new Paint();
        pOn.setColor(onColor);
        pOff = new Paint();
        pOff.setColor(offColor);
        isOn = true;
        pOnText = new Paint();
        pOnText.setColor(ResourseColors.colorWhite);
        pOffText = new Paint();
        pOffText.setColor(ResourseColors.colorBlue);
        pOffText.setTextSize(textSize);
        pOnText.setTextSize(textSize);
        pOffText.setTypeface(type);
        pOnText.setTypeface(type);
        changeListener = new changeListener() {
            @Override
            public void onChange(boolean isOn) {

            }

            @Override
            public void onChange(int lang) {

            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!orentHoriz) {
                if (event.getX() < getWidth() / 2) {
                    if (!isOn)
                        changeListener.onChange(isOn);

                    isOn = true;
                    pOn.setColor(onColor);
                    pOff.setColor(offColor);
                    pOnText.setColor(Color.WHITE);
                    pOffText.setColor(Color.BLACK);
                    invalidate();
                } else {
                    if (isOn)
                        changeListener.onChange(isOn);
                    isOn = false;
                    pOff.setColor(onColor);
                    pOn.setColor(offColor);
                    pOnText.setColor(Color.BLACK);
                    pOffText.setColor(Color.WHITE);
                    invalidate();
                }
            } else {
                if (event.getY() < getHeight() / 2) {
                    if (!isOn)
                        changeListener.onChange(isOn);

                    isOn = true;
                    pOn.setColor(onColor);
                    pOff.setColor(offColor);
                    pOnText.setColor(Color.WHITE);
                    pOffText.setColor(Color.BLACK);
                    invalidate();
                } else {
                    if (isOn)
                        changeListener.onChange(isOn);
                    isOn = false;
                    pOff.setColor(onColor);
                    pOn.setColor(offColor);
                    pOnText.setColor(Color.BLACK);
                    pOffText.setColor(Color.WHITE);
                    invalidate();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!orentHoriz) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(0, 0, getWidth() / 2, getHeight(), getHeight() / 12, getHeight() / 12, pOn);
                canvas.drawRect(getWidth() / 2 - getWidth() / 4, 0, getWidth() / 2, getHeight(), pOn);
                canvas.drawRoundRect(getWidth() / 2, 0, getWidth(), getHeight(), getHeight() / 12, getHeight() / 12, pOff);
                canvas.drawRect(getWidth() / 2, 0, getWidth() * 3 / 4, getHeight(), pOff);
            } else {
                canvas.drawRect(0, 0, getWidth() / 2, getHeight(), pOn);
                canvas.drawRect(getWidth() / 2, 0, getWidth(), getHeight(), pOff);
            }

            Rect bounds = new Rect();
            pOnText.getTextBounds(on, 0, on.length(), bounds);
            canvas.drawText(on, getWidth() / 4 - bounds.width() / 2, getHeight() * 3 / 5, pOnText);
            pOffText.getTextBounds(off, 0, off.length(), bounds);
            canvas.drawText(off, getWidth() * 3 / 4 - bounds.width() / 2, getHeight() * 3 / 5, pOffText);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(0, 0, getWidth(), getHeight() / 2 - getHeight() / 50, getWidth() / 50, getWidth() / 50, pOn);
                //   canvas.drawRect(0, getHeight()/4, getWidth(), getHeight()/2, pOn);
                canvas.drawRoundRect(0, getHeight() / 50 + getHeight() / 2, getWidth(), getHeight(), getWidth() / 50, getWidth() / 50, pOff);
                //   canvas.drawRect(0, getHeight()/2, getWidth(), getHeight()*3/4, pOff);
            } else {
                canvas.drawRect(0, 0, getWidth(), getHeight() / 2, pOn);
                canvas.drawRect(0, getHeight() / 2, getWidth(), getHeight(), pOff);
            }

            Rect bounds = new Rect();
            pOnText.getTextBounds(on, 0, on.length(), bounds);
            canvas.drawText(on, getWidth() / 20, bounds.height() * 1.5f, pOnText);
            pOffText.getTextBounds(off, 0, off.length(), bounds);
            canvas.drawText(off, getWidth() / 20, getHeight() / 2 + getHeight() / 25 + bounds.height() * 1.5f, pOffText);
        }

    }
}

