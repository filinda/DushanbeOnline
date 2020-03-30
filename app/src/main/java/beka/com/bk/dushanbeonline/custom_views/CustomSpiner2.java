package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class CustomSpiner2 extends FrameLayout {
    boolean closed = true;
    public ValueAnimator openAnimator;
    public ValueAnimator closeAnimator;
    public int leftOffset, delta, heightItem;
    public int itemSellected;
    public String[] strings;
    public Bitmap arrowD, arrowU;
    public Rect[] touchareas;
    public Paint p, pMain;
    public Paint pText;
    float scale;
    int fixedDrop = -1;

    public CustomSpiner2(@NonNull final Context context, String[] items) {
        super(context);
        setWillNotDraw(false);
        scale = context.getResources().getDisplayMetrics().widthPixels / 1080f;
        heightItem = (int) (120 * scale);
        delta = (int) (17 * scale);
        strings = items;
        itemSellected = 0;
        touchareas = new Rect[strings.length];
        setLayoutParams(new LayoutParams((int) (1005 * scale), (int) (120 * scale)));

        openAnimator = new ValueAnimator();
        openAnimator.setIntValues((int) (120 * scale), heightItem + delta + heightItem * items.length);
        openAnimator.setDuration(500);
        openAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setLayoutParams(new LayoutParams((int) (getWidth()), (int) (animation.getAnimatedValue())));
            }
        });
        closeAnimator = new ValueAnimator();
        closeAnimator.setIntValues(delta + heightItem * items.length, (int) (120 * scale));
        closeAnimator.setDuration(500);
        closeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setLayoutParams(new LayoutParams((int) (getWidth()), (int) (animation.getAnimatedValue())));
            }
        });

        p = new Paint();
        p.setStrokeWidth(5 * scale);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(ResourseColors.colorStroke);

        pMain = new Paint();
        pMain.setStrokeWidth(5 * scale);
        pMain.setStyle(Paint.Style.STROKE);
        pMain.setColor(ResourseColors.colorStroke);

        pText = new Paint();
        pText.setColor(ResourseColors.colorBlack);
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(), "font.ttf"));
        pText.setTextSize(44 * scale);

        arrowD = MainMenuActivity.getBitmapFromAsset(context, "arrow_down_black.png");
        arrowD = Bitmap.createScaledBitmap(arrowD, (int) (37 * scale), (int) (23 * scale), false);
        arrowU = MainMenuActivity.getBitmapFromAsset(context, "arrow_up_black.png");
        arrowU = Bitmap.createScaledBitmap(arrowU, (int) (37 * scale), (int) (23 * scale), false);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (closed) {
                        open();
                    } else {
                        if (event.getY() < heightItem) {
                            close();
                        } else {
                            for (int i = 0; i < strings.length; i++) {
                                if (heightItem + delta + i * heightItem < event.getY() & event.getY() < heightItem + delta + (i + 1) * heightItem) {
                                    itemSellected = i;
                                    close();
                                }
                            }
                        }

                    }
                    return false;
                }
                return true;
            }
        });

        setBackgroundColor(ResourseColors.colorWhite);
    }

    public void close() {
        if (!closed) {
            openAnimator.cancel();
            closeAnimator.start();
            closed = true;
        }
        pMain.setColor(ResourseColors.colorStroke);
        invalidate();
    }

    public void open() {
        if (closed) {
            closeAnimator.cancel();
            openAnimator.start();
            closed = false;
        }
        pMain.setColor(ResourseColors.colorBlue);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(2.5f * scale, 2.5f * scale, getWidth() - 2.5f * scale, heightItem - 2.5f * scale, 15 * scale, 15 * scale, pMain);
            canvas.drawRoundRect(2.5f * scale, heightItem + delta, getWidth() - 2.5f * scale, heightItem + delta + heightItem * strings.length - 2.5f * scale, 15 * scale, 15 * scale, p);
        } else {
            canvas.drawRect(1.5f * scale, 1.5f * scale, getWidth() - 1.5f * scale, getHeight() - 1.5f * scale, pMain);
            canvas.drawRect(2.5f * scale, heightItem + delta, getWidth() - 2.5f * scale, heightItem + delta + heightItem * strings.length - 2.5f * scale, p);
        }

        if (closed) {
            canvas.drawBitmap(arrowD, getWidth() - 70 * scale, 52 * scale, p);
        } else {
            canvas.drawBitmap(arrowU, getWidth() - 70 * scale, 52 * scale, p);
        }

        canvas.drawText(strings[itemSellected], 35 * scale, 77 * scale, pText);

        for (int i = 0; i < strings.length; i++) {
            canvas.drawText(strings[i], 35 * scale, delta + heightItem + heightItem * i + 77 * scale, pText);
        }

    }
}
