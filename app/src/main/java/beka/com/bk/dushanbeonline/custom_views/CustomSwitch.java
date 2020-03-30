package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import beka.com.bk.dushanbeonline.ResourseColors;

public class CustomSwitch extends FrameLayout {


    float scale;
    int height;
    TextView[] items;
    public View[] lines;
    public int selected = 0;

    public CustomSwitch(@NonNull Context context, String[] labels) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        height = (int)(120*scale);
        setLayoutParams(new LayoutParams((int)(1080*scale),height));

        items = new TextView[labels.length];
        lines = new View[labels.length];
        for(int i = 0; i< labels.length;i++){
            items[i] = new TextView(getContext());
            items[i].setText(labels[i]);
            items[i].setTypeface(Typeface.createFromAsset(getContext().getAssets(),"font.ttf"));
            items[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,44*scale);
            items[i].setTextColor(ResourseColors.colorLightGray);
            items[i].measure(0,0);
            items[i].setY(25*scale);
            items[i].setX((1080*scale/labels.length)*i + (1080*scale/labels.length)/2-items[i].getMeasuredWidth()/2);
            final int finalI = i;
            items[i].setOnClickListener(new OnClickListener() {
                int g = finalI;
                @Override
                public void onClick(View v) {
                   select(g);
                }
            });
            addView(items[i]);

            lines[i] = new View(getContext());
            lines[i].setBackgroundColor(Color.TRANSPARENT);
            lines[i].setLayoutParams(new LayoutParams((int)(1080*scale/labels.length),(int)(6*scale)));
            lines[i].setY(height-6*scale);
            lines[i].setX((int)(1080*scale/labels.length)*i);
            addView(lines[i]);
        }

        lines[selected].setBackgroundColor(ResourseColors.colorBlue);
        items[selected].setTextColor(ResourseColors.colorBlue);

        setBackgroundColor(ResourseColors.colorVeryLightGray);
    }

    public void select(int g){
        selected = g;
        onItemSelected(g);
        for(int i = 0; i< lines.length;i++){
            lines[i].setBackgroundColor(Color.TRANSPARENT);
            items[i].setTextColor(ResourseColors.colorLightGray);
        }
        lines[selected].setBackgroundColor(ResourseColors.colorBlue);
        items[selected].setTextColor(ResourseColors.colorBlue);
    }

    public void onItemSelected(int g){

    }

}
