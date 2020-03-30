package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import beka.com.bk.dushanbeonline.MainMenuActivity;

public class TaskEditorButton extends View {

    Bitmap arrow, icon, actIcon;
    public String label;
    public Paint p, p2, pline, pWhite;
    public String mainLabel;
    int color;
    public boolean isActiv = false;
    public boolean hasLine = true;
    ValueAnimator valueAnimator, textAnimator;
    int offset = 0;
    float scale;

    public TaskEditorButton(Context context, Bitmap arrow, final Bitmap icon, final Bitmap actIcon, String label) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        this.arrow = arrow;
        this.icon = Bitmap.createScaledBitmap(icon,(int)(icon.getWidth()*scale),(int)(icon.getHeight()*scale),false);
        this.label = label;
        this.mainLabel = label;
        this.actIcon = Bitmap.createScaledBitmap(actIcon,(int)(actIcon.getWidth()*scale),(int)(actIcon.getHeight()*scale),false);
        color = Color.WHITE;
        p = new Paint();
        p.setColor(Color.rgb(100, 100, 100));
        p.setTypeface(Typeface.createFromAsset(context.getAssets(), "font.ttf"));
        p.setTextSize((int) (45 * scale));

        p2 = new Paint();
        p2.setColor(Color.rgb(100, 100, 100));
        p2.setTypeface(Typeface.createFromAsset(context.getAssets(), "font.ttf"));
        p2.setTextSize((int) (35 * scale));

        pline = new Paint();
        pline.setColor(Color.rgb(215, 215, 215));
        pline.setStrokeWidth(3 * scale);

        pWhite = new Paint();
        pWhite.setColor(color);

        setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (166 * scale)));

        valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(500).setIntValues(255, 230, 255);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                color = Color.rgb((int) animation.getAnimatedValue(), (int) animation.getAnimatedValue(), (int) animation.getAnimatedValue());
                pWhite.setColor(color);
                invalidate();
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    valueAnimator.start();
                    invalidate();
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    invalidate();
                    callOnClick();
                }
                return false;
            }
        });

    }

    public void setLabel(String label) {
        this.label = label;
        p.setColor(Color.rgb(56, 175, 243));
        isActiv = true;
        p2.setColor(Color.rgb(100, 100, 100));

        Rect bounds = new Rect();
        p.getTextBounds(label, 0, label.length(), bounds);

        if (bounds.width() > (1000 - 168) * scale) {
            textAnimator = new ValueAnimator();
            textAnimator.setDuration(100 * (int) (bounds.width() - (1000 - 168) * scale)).setIntValues(0, (int) (bounds.width() - (1000 - 168) * scale), 0);
            textAnimator.setRepeatCount(ValueAnimator.INFINITE);
            textAnimator.start();
            textAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    offset = (int) (animation.getAnimatedValue());
                    invalidate();
                }
            });
        }

        invalidate();
    }

    public boolean checkWarning() {
        if (label.equals(mainLabel)) {
            p.setColor(Color.RED);
            p2.setColor(Color.RED);
            invalidate();
        }
        return label.equals(mainLabel);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(color);
        if (!label.equals(mainLabel)) {
            canvas.drawText(mainLabel, (int) (168 * scale), (int) (50 * scale), p2);
        }
        canvas.drawText(label, (int) (168 * scale) - offset, (int) (113 * scale), p);
        canvas.drawRect(0, 0, (int) (168 * scale), getHeight(), pWhite);
        canvas.drawRect(1000, 0, (int) (getWidth()), getHeight(), pWhite);
        canvas.drawBitmap(arrow, (int) (1020 * scale), (int) (75 * scale), p);
        if (isActiv) {
            canvas.drawBitmap(actIcon, (int) (75 * scale), (int) (65 * scale), p);

        } else {
            canvas.drawBitmap(icon, (int) (75 * scale), (int) (65 * scale), p);
        }
        if (hasLine)
            canvas.drawLine(69 * scale, 162 * scale, 1085 * scale, 162 * scale, pline);
    }
}
