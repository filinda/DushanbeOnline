package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class CustomSpiner3 extends View {

    private int selectedItem = 0;
    float scale = 1;
    public String[] items;
    float scrollOffset = 0;
    public float rounding = 15;
    ValueAnimator open, close;
    float itemHeight = 120;
    float textSize = 44;
    float gapSize = 30;
    Paint pText, pChosenText, pRect, pMain;
    int visibleAmount;
    boolean isOpened = false;
    Bitmap arrowD, arrowU;
    Paint pScroll;

    public CustomSpiner3(Context context, String[] items, int visibleAmount, int width) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        this.items = items;
        this.visibleAmount = visibleAmount;
        pText = new Paint();
        pText.setColor(ResourseColors.colorBlack);
        pText.setTextSize(textSize*scale);
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));

        pChosenText = new Paint();
        pChosenText.setColor(ResourseColors.colorBlack);
        pChosenText.setTextSize(textSize*scale);
        pChosenText.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));

        pRect = new Paint();
        pRect.setColor(Color.WHITE);

        pMain = new Paint();
        pMain.setStrokeWidth(5 * scale);
        pMain.setStyle(Paint.Style.STROKE);
        pMain.setColor(ResourseColors.colorStroke);

        pScroll = new Paint();
        pScroll.setColor(ResourseColors.colorBlue);
        pScroll.setAntiAlias(true);

        open = new ValueAnimator();
        open.setIntValues((int)(itemHeight*scale),(int)((visibleAmount+1)*itemHeight*scale + gapSize*scale));
        open.setDuration(200*visibleAmount);
        open.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setLayoutParams(new FrameLayout.LayoutParams(getWidth(),(Integer) animation.getAnimatedValue()));
            }
        });

        close = new ValueAnimator();
        close.setIntValues((int)((visibleAmount+1)*itemHeight*scale + gapSize*scale),(int)(itemHeight*scale));
        close.setDuration(200*visibleAmount);
        close.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setLayoutParams(new FrameLayout.LayoutParams(getWidth(),(Integer) animation.getAnimatedValue()));
            }
        });

        setLayoutParams(new FrameLayout.LayoutParams(width,(int)(itemHeight*scale)));

        arrowD = MainMenuActivity.getBitmapFromAsset(context, "arrow_down_black.png");
        arrowD = Bitmap.createScaledBitmap(arrowD, (int) (37 * scale), (int) (23 * scale), false);
        arrowU = MainMenuActivity.getBitmapFromAsset(context, "arrow_up_black.png");
        arrowU = Bitmap.createScaledBitmap(arrowU, (int) (37 * scale), (int) (23 * scale), false);
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(2.5f * scale,2.5f * scale,getWidth()-5f * scale,(itemHeight-5f) * scale, rounding*scale,rounding*scale,pRect);
            canvas.drawRoundRect(2.5f * scale,2.5f * scale,getWidth()-5f * scale,(itemHeight-5f) * scale, rounding*scale,rounding*scale,pMain);
        }
        else{
            canvas.drawRect(2.5f * scale,2.5f * scale,getWidth()-5f * scale,(itemHeight-5f) * scale,pRect);
            canvas.drawRect(2.5f * scale,2.5f * scale,getWidth()-5f * scale,(itemHeight-5f) * scale,pMain);
        }

        canvas.drawText(items[selectedItem],35*scale,77*scale,pChosenText);

        if (!isOpened) {
            canvas.drawBitmap(arrowD, getWidth() - 70 * scale, 52 * scale, pMain);
        } else {
            canvas.drawBitmap(arrowU, getWidth() - 70 * scale, 52 * scale, pMain);
        }

        Bitmap temp = Bitmap.createBitmap(getWidth(),(int)(visibleAmount*itemHeight*scale),Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(temp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(2.5f * scale,itemHeight*scale+gapSize*scale+2.5f * scale,temp.getWidth()-5f * scale,itemHeight*scale+gapSize*scale+temp.getHeight()-5f * scale,rounding*scale,rounding*scale,pRect);
            canvas.drawRoundRect(2.5f * scale,itemHeight*scale+gapSize*scale+2.5f * scale,temp.getWidth()-5f * scale,itemHeight*scale+gapSize*scale+temp.getHeight()-5f * scale,rounding*scale,rounding*scale,pMain);
        }
        else{
            canvas.drawRect(2.5f * scale,itemHeight*scale+gapSize*scale+2.5f * scale,temp.getWidth()-5f * scale,itemHeight*scale+gapSize*scale+temp.getHeight()-5f * scale,pRect);
            canvas.drawRect(2.5f * scale,itemHeight*scale+gapSize*scale+2.5f * scale,temp.getWidth()-5f * scale,itemHeight*scale+gapSize*scale+temp.getHeight()-5f * scale,pMain);
        }

        tempCanvas.drawRect(getWidth()-30*scale,40*scale, getWidth()-26*scale,temp.getHeight()-40*scale,pScroll);
        tempCanvas.drawRect(getWidth()-33*scale,40*scale+(scrollOffset/((items.length-2)*itemHeight*scale))*(temp.getHeight()-80*scale)-10*scale, getWidth()-23*scale,40*scale+(scrollOffset/((items.length-2)*itemHeight*scale))*(temp.getHeight()-80*scale)+10*scale,pScroll);

        for(int i=0; i< items.length; i++) {
            tempCanvas.drawText(items[i],35*scale, 77*scale+scale*itemHeight*i - scrollOffset, pText);
        }
        Rect scr = new Rect();
        scr.left = (int)(0*scale);
        scr.top = (int)(7*scale);
        scr.right = (int)(temp.getWidth());
        scr.bottom = (int)(temp.getHeight()-14*scale);

        Rect dst = new Rect();
        dst.left = (int)(0*scale);
        dst.top = (int)(itemHeight*scale+gapSize*scale+7*scale);
        dst.right = (int)(temp.getWidth());
        dst.bottom = (int)(itemHeight*scale+gapSize*scale+temp.getHeight()-14*scale);


        canvas.drawBitmap(temp,scr,dst,pRect);
    }

    public void open(){
        isOpened = true;
        close.cancel();
        open.setIntValues(getHeight(),(int)((visibleAmount+1)*itemHeight*scale + gapSize*scale));
        open.start();
        pMain.setColor(ResourseColors.colorBlue);
        invalidate();
    }

    public void close(){
        isOpened = false;
        open.cancel();
        close.setIntValues(getHeight(),(int)(itemHeight*scale));
        close.start();
        pMain.setColor(ResourseColors.colorStroke);
        invalidate();
    }

    float lastY = 0;
    float lastOffset = 0;

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_UP){
            if(Math.abs(lastOffset-scrollOffset)<itemHeight/6){
                if(!isOpened) {
                    open();
                }else{
                    selectedItem = (int)(event.getRawY()-getY()-(itemHeight*scale+gapSize*scale)+scrollOffset)/(int)(itemHeight*scale);
                    if(selectedItem<0){
                        selectedItem=0;
                    }
                    if(selectedItem>=items.length){
                        selectedItem=items.length-1;
                    }
                    close();
                    invalidate();
                }

            }
            else{

            }
            lastOffset = scrollOffset;
        }

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            lastY = event.getRawY();
        }

        if(event.getAction()==MotionEvent.ACTION_MOVE){
            scrollOffset -= event.getRawY() - lastY;
            if(scrollOffset<0){
                scrollOffset=0;
            }
            if(scrollOffset>(items.length-visibleAmount)*itemHeight*scale){
                scrollOffset = (items.length-visibleAmount)*itemHeight*scale;
            }
            lastY = event.getRawY();
            invalidate();
        }

        return true;// super.dispatchTouchEvent(event);
    }
}
