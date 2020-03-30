package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class CustomSpiner extends FrameLayout {

    public ArrayList<SpinerButton> items = new ArrayList<>();
    ValueAnimator animtorOpen, animatorClose;
    public boolean closed = true;
    public changeListener changeListener;

    public CustomSpiner(@NonNull Context context) {
        super(context);
        animtorOpen = new ValueAnimator();
        animatorClose = new ValueAnimator();
        changeListener = new changeListener() {
            @Override
            public void onChange(boolean isOn) {

            }

            @Override
            public void onChange(int isOn) {

            }
        };
    }

    public void addItem(SpinerButton item) {
        if (items.size() == 0) {
            item.isChoosen = true;
            setLayoutParams(new LayoutParams(item.getWidth2(), item.getHeight2()));
        }

        item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((SpinerButton) v).isChoosen) {
                    if (closed) {
                        expand();
                    } else {
                        animatorClose.start();
                        closed = true;
                    }
                } else {
                    chooseItem(items.indexOf(v));
                }
            }
        });

        addView(item);
        item.index = items.size();

        int endPx1 = 0;

        for (SpinerButton itemInAr : items) {
            endPx1 += itemInAr.getHeight2();
        }
        item.setY(endPx1);
        items.add(item);


        int startPx = 0, endPx = 0;

        for (SpinerButton itemInAr : items) {
            if (itemInAr.isChoosen) {
                startPx = itemInAr.getHeight2();
            }
            endPx += itemInAr.getHeight2();

        }

        animtorOpen = new ValueAnimator();
        animtorOpen.setIntValues(startPx, startPx * items.size());
        animtorOpen.setDuration(500);
        animtorOpen.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setLayoutParams(new FrameLayout.LayoutParams(getWidth(), (Integer) animation.getAnimatedValue()));
            }
        });
        animatorClose = new ValueAnimator();
        animatorClose.setIntValues(endPx, startPx);
        animatorClose.setDuration(500);
        animatorClose.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setLayoutParams(new FrameLayout.LayoutParams(getWidth(), (Integer) animation.getAnimatedValue()));
            }
        });

    }

    private void expand() {
        animtorOpen.start();
        closed = false;
    }

    private void chooseItem(int i) {
        for (SpinerButton item : items)
            item.isChoosen = false;

        items.get(i).isChoosen = true;
        items.get(i).setY(0);
        int k = items.get(i).getHeight();
        for (SpinerButton item : items) {
            if (item != items.get(i)) {
                item.setY(k);
                k += item.getHeight2();
            }
        }

        int startPx = 0, endPx = 0;

        for (SpinerButton itemInAr : items) {
            if (itemInAr.isChoosen) {
                startPx = itemInAr.getHeight2();
            }
            endPx += itemInAr.getHeight2();
            itemInAr.invalidate();
        }

        animtorOpen = new ValueAnimator();
        animtorOpen.setIntValues(startPx, endPx);
        animtorOpen.setDuration(500);
        animtorOpen.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setLayoutParams(new FrameLayout.LayoutParams(getWidth(), (Integer) animation.getAnimatedValue()));
            }
        });
        animatorClose = new ValueAnimator();
        animatorClose.setIntValues(endPx, startPx);
        animatorClose.setDuration(500);
        animatorClose.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setLayoutParams(new FrameLayout.LayoutParams(getWidth(), (Integer) animation.getAnimatedValue()));
            }
        });

        if (!closed)
            animatorClose.start();
        closed = true;
        changeListener.onChange(items.get(0).isChoosen);
    }

}




