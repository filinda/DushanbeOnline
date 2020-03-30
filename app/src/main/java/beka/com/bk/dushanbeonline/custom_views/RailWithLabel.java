package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class RailWithLabel extends View {

    Paint pText, pLine, pActLine, pMessage, pInact;
    Bitmap circle, triangle, inactCircle, inactTriangle;


    String label = "";
    public int val = 0;
    int minVal, maxVal;
    float scale;
    float offset;
    float lineWigth;
    String unit = "";

    SwitchView control;

    public RailWithLabel(Context context, int minVal, int maxVal, String unit, String label) {
        super(context);
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.unit = unit;
        this.label = label;
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(235*scale)));
        offset = (1080-1016)*scale;
        lineWigth = 10*scale;
        pLine = new Paint();
        pLine.setColor(ResourseColors.colorLightGray);
        pActLine = new Paint();
        pActLine.setColor(ResourseColors.colorBlue);
        circle = MainMenuActivity.getBitmapFromAsset(getContext(),"slider_circle.png");
        circle = Bitmap.createScaledBitmap(circle,(int)(70*scale),(int)(70*scale),false);
        triangle = MainMenuActivity.getBitmapFromAsset(getContext(),"triangle_slider.png");
        triangle = Bitmap.createScaledBitmap(triangle,(int)(32*scale),(int)(16*scale),false);
        inactCircle = MainMenuActivity.getBitmapFromAsset(getContext(),"slider_grey.png");
        inactCircle = Bitmap.createScaledBitmap(inactCircle,(int)(70*scale),(int)(70*scale),false);
        inactTriangle = MainMenuActivity.getBitmapFromAsset(getContext(),"triangle_grey.png");
        inactTriangle = Bitmap.createScaledBitmap(inactTriangle,(int)(32*scale),(int)(16*scale),false);

        pMessage = new Paint();
        pMessage.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"font_bold.ttf"));
        pMessage.setTextSize(47*scale);
        pMessage.setColor(ResourseColors.colorWhite);
        pText = new Paint();
        pText.setColor(ResourseColors.colorLightGray);
        pText.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"font.ttf"));
        pText.setTextSize(42*scale);

        pInact = new Paint();
        pInact.setColor(ResourseColors.colorIconGrayInact);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if(control!=null) {
            if(control.isOn) {
                if (event.getX() > offset & event.getX() < getWidth() - offset) {
                    float activeX = event.getX() - offset;
                    val = (int) (minVal + (maxVal - minVal) * (activeX / (getWidth() - offset * 2)));
                    invalidate();
                }
            }
        }else{
            if (event.getX() > offset & event.getX() < getWidth() - offset) {
                float activeX = event.getX() - offset;
                val = (int) (minVal + (maxVal - minVal) * (activeX / (getWidth() - offset * 2)));
                invalidate();
            }
        }

        return super.dispatchTouchEvent(event);
    }

    public void pair(SwitchView switchView){
        this.control = switchView;
        control.turnListener = new OnTurnListener(){
            @Override
            public void onTurn(boolean state) {
                super.onTurn(state);
                invalidate();
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect bounds = new Rect();

        float valCentY = getHeight()-35*scale;
        float valCentX = offset + (getWidth()-2*offset)*((val-minVal)/(float)(maxVal-minVal));
        float offsetMessage = 0;

        pMessage.getTextBounds(val+" "+unit,0,(val+" "+unit).length(),bounds);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(offset,getHeight()-35*scale - lineWigth/2,getWidth()-offset,getHeight()-35*scale + lineWigth/2,lineWigth/2,lineWigth/2, pLine);
            if(control!=null) {
                if (control.isOn)
                    canvas.drawRoundRect(offset, getHeight() - 35 * scale - lineWigth / 2, valCentX, getHeight() - 35 * scale + lineWigth / 2, lineWigth / 2, lineWigth / 2, pActLine);
            }else{
                canvas.drawRoundRect(offset, getHeight() - 35 * scale - lineWigth / 2, valCentX, getHeight() - 35 * scale + lineWigth / 2, lineWigth / 2, lineWigth / 2, pActLine);
            }
            if (valCentX - bounds.width() / 2 - 34 * scale < offset / 2) {
                    offsetMessage = -(valCentX - bounds.width() / 2 - 34 * scale) + offset / 2;
                }
            if(valCentX+bounds.width()/2+34*scale>getWidth()-offset/2){
                offsetMessage = -(valCentX+bounds.width()/2+34*scale-(getWidth()-offset/2));
            }
            if(control!=null) {
                if (control.isOn) {
                    canvas.drawRoundRect(valCentX - bounds.width() / 2 - 34 * scale + offsetMessage, getHeight() - 35 * scale - circle.getHeight() / 2 - triangle.getHeight() - 84 * scale, valCentX + bounds.width() / 2 + 34 * scale + offsetMessage, getHeight() - 35 * scale - circle.getHeight() / 2 - triangle.getHeight(), lineWigth, lineWigth, pActLine);
                } else {
                    canvas.drawRoundRect(valCentX - bounds.width() / 2 - 34 * scale + offsetMessage, getHeight() - 35 * scale - circle.getHeight() / 2 - triangle.getHeight() - 84 * scale, valCentX + bounds.width() / 2 + 34 * scale + offsetMessage, getHeight() - 35 * scale - circle.getHeight() / 2 - triangle.getHeight(), lineWigth, lineWigth, pInact);
                }
            }else{
                canvas.drawRoundRect(valCentX - bounds.width() / 2 - 34 * scale + offsetMessage, getHeight() - 35 * scale - circle.getHeight() / 2 - triangle.getHeight() - 84 * scale, valCentX + bounds.width() / 2 + 34 * scale + offsetMessage, getHeight() - 35 * scale - circle.getHeight() / 2 - triangle.getHeight(), lineWigth, lineWigth, pActLine);
            }
        } ///need remake
        if(control!=null) {
            if (control.isOn) {
                canvas.drawBitmap(circle, valCentX - circle.getWidth() / 2, getHeight() - 35 * scale - circle.getHeight() / 2, pLine);
                canvas.drawBitmap(triangle, valCentX - triangle.getWidth() / 2, getHeight() - 35 * scale - circle.getHeight() / 2 - triangle.getHeight(), pLine);
            } else {
                canvas.drawBitmap(inactCircle, valCentX - circle.getWidth() / 2, getHeight() - 35 * scale - circle.getHeight() / 2, pLine);
                canvas.drawBitmap(inactTriangle, valCentX - triangle.getWidth() / 2, getHeight() - 35 * scale - circle.getHeight() / 2 - triangle.getHeight(), pLine);
            }
        }else{
            canvas.drawBitmap(circle, valCentX - circle.getWidth() / 2, getHeight() - 35 * scale - circle.getHeight() / 2, pLine);
            canvas.drawBitmap(triangle, valCentX - triangle.getWidth() / 2, getHeight() - 35 * scale - circle.getHeight() / 2 - triangle.getHeight(), pLine);
        }
        canvas.drawText(val+" "+unit,valCentX - bounds.width()/2 + offsetMessage,getHeight()-35*scale-circle.getHeight()/2-triangle.getHeight()-27*scale, pMessage);
        canvas.drawText(label,offset/2,getHeight()-35*scale - lineWigth/2 - 150*scale,pText);

        super.onDraw(canvas);
    }
}
