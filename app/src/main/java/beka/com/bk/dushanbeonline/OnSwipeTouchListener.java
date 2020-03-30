package beka.com.bk.dushanbeonline;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;
    private GestureListener gestureListener;
    final GestureDetector.OnDoubleTapListener doubleTapListener;
    Context context;
    boolean isDoubleTap = false;

    public OnSwipeTouchListener (Context ctx){
        context = ctx;
        doubleTapListener = new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                System.out.println("folok sing");
                if(!isDoubleTap)
                onClick();
                isDoubleTap=false;
                return false;
            }


            @Override
            public boolean onDoubleTap(MotionEvent e) {
                System.out.println("folok double");
                isDoubleTap=true;
                onDoubleTouch();
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                System.out.println("folok doubleEvent");
                return true;
            }
        };
        gestureListener = new GestureListener();
        gestureDetector = new GestureDetector(ctx, gestureListener);
        gestureDetector.setOnDoubleTapListener(doubleTapListener);
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public GestureListener getGestureListener() {
        return gestureListener;
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 0;
        private static final int SWIPE_VELOCITY_THRESHOLD = 0;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            System.out.println("folok singUp");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
          //  onDoubleTouch();
           // isDoubleTap = true;
            return super.onDoubleTap(e);

        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
          //  if(!isDoubleTap)
          //  onClick();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {

                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onClick() {
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onDoubleTouch() {
    }

    public void onSwipeBottom() {
    }
}