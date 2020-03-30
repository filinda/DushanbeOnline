package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class TopLabel extends FrameLayout {

    HorizontalScrollView scl;
    public ImageView back;
    float scale;
    textClass layScl;
    String label;
    Typeface type;
    ValueAnimator animator;

    public TopLabel(@NonNull Context context, String label, float scale) {
        super(context);
        this.scale = scale;
        this.type = Typeface.createFromAsset(context.getAssets(), "font_medium.ttf");
        scl = new HorizontalScrollView(context);
        scl.setLayoutParams(new LayoutParams((int) ((1080 - 300) * scale), (int) (168 * scale)));
        scl.setX(150 * scale);
        layScl = new textClass(context);
        scl.addView(layScl);
        addView(scl);
        layScl.setBackgroundColor(Color.TRANSPARENT);
        scl.setHorizontalScrollBarEnabled(false);
        scl.setVerticalScrollBarEnabled(false);

        Bitmap backbtn = MainMenuActivity.getBitmapFromAsset(context, "back_btn.png");
        backbtn = Bitmap.createScaledBitmap(backbtn, (int) (60 * scale), (int) (60 * scale), false);
        back = new ImageView(context);
        back.setImageBitmap(backbtn);
        back.setX(52 * scale);
        back.setY(56 * scale);
        back.setScaleType(ImageView.ScaleType.MATRIX);
        back.setLayoutParams(new LayoutParams((int) (60 * scale), (int) (60 * scale)));
        addView(back);
        Rect rect = new Rect();
        back.getHitRect(rect);
        rect.top -= 100;    // increase top hit area
        rect.left -= 100;   // increase left hit area
        rect.bottom += 100; // increase bottom hit area
        rect.right += 100;
        setTouchDelegate(new TouchDelegate(rect, back));
        setLayoutParams(new LayoutParams((int) ((1080) * scale), (int) (168 * scale)));
        setBackgroundColor(ResourseColors.colorBlue);
        setLabel(label);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void setLabel(String label) {
        this.label = label;
        Rect bounds = new Rect();
        Paint textPaint = layScl.p;
        textPaint.getTextBounds(label, 0, label.length(), bounds);
        int width2 = bounds.width();
        layScl.setMinimumWidth((int) (bounds.width() + 20 * scale));
        layScl.setLayoutParams(new LayoutParams(bounds.width() + (int) (20 * scale), (int) (185 * scale)));
        layScl.invalidate();
        scl.invalidate();

        scl.setX(150 * scale);
        animator = new ValueAnimator();
        animator.setIntValues(0, bounds.width() - scl.getWidth() + (int) (20 * scale), 0);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(10000);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scl.setScrollX((int) animation.getAnimatedValue());
                layScl.invalidate();
                scl.invalidate();
            }
        });
        animator.start();
        if (bounds.width() < 1080 * scale - 300 * scale) {
            scl.setX(1080 / 2 * scale - bounds.width() / 2);
        }
        scl.setHorizontalScrollBarEnabled(false);
        scl.setVerticalScrollBarEnabled(false);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    public void setBackOnClickListener(OnClickListener listener) {
        back.setOnClickListener(listener);

    }

    class textClass extends FrameLayout {

        Paint p;

        public textClass(@NonNull Context context) {
            super(context);
            p = new Paint();
            p.setColor(Color.WHITE);
            p.setTextSize(57 * scale);
            p.setTypeface(type);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawText(label, 0, 106 * scale, p);
        }
    }
}
