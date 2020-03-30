package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import beka.com.bk.dushanbeonline.ResourseColors;

public class ClicableRatingView extends RatingView {
    public ClicableRatingView(Context context) {
        super(context);
        setWidth((int)(550*scale));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            float touchX = event.getX();
            int position = (int)Math.floor(touchX/(getWidth()/5));
            setStars(position);
        }
        return super.onTouchEvent(event);
    }
}
