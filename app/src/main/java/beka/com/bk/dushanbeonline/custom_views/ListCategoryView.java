package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class ListCategoryView extends FrameLayout {

    String label;
    Bitmap box, check, defaul, payed;
    Paint pText;
    Paint pBack;
    public ArrayList<ListCategoryView> childs = new ArrayList<>();
    public ListCategoryView root;
    public boolean isChecked = false;
    public boolean isDefault = false;
    public boolean isPayed = false;
    float scale;
    float offset;
    ImageView arrow;
    boolean opened = false;
    public FrameLayout baseLayout,postBaseLayout;
    View touchArea;
    public int checkedViews=0;
    public int price = 30;
    public CustomPublishBtn saveBtn;
    public String payPreInfo;

    public ListCategoryView(Context context, String text) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        rightTextStopper = 144*scale;
        label = text;
        setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(122*scale)));

        pBack = new Paint();
        pStoper = new Paint();
        pStoper.setColor(ResourseColors.colorWhite);

        pText = new Paint();
        pText.setColor(ResourseColors.colorGray);
        pText.setTextSize(scale*45);
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));

        offset = 100*scale;

        box = MainMenuActivity.getBitmapFromAsset(context,"checkbox.png");
        box = Bitmap.createScaledBitmap(box,(int)(box.getWidth()*scale),(int)(box.getHeight()*scale),false);

        check = MainMenuActivity.getBitmapFromAsset(context,"check.png");
        check = Bitmap.createScaledBitmap(check,(int)(check.getWidth()*scale),(int)(check.getHeight()*scale),false);

        defaul = MainMenuActivity.getBitmapFromAsset(context,"check_def.png");
        defaul = Bitmap.createScaledBitmap(defaul,(int)(defaul.getWidth()*scale),(int)(defaul.getHeight()*scale),false);

        payed = MainMenuActivity.getBitmapFromAsset(context,"check_pay.png");
        payed = Bitmap.createScaledBitmap(payed,(int)(payed.getWidth()*scale),(int)(payed.getHeight()*scale),false);


        touchArea = new View(context);
        touchArea.setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(122*scale)));
        touchArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDefault) {
                    isChecked = !isChecked;
                }
                if(root!=null)
                    root.reflect();
                invalidate();
            }
        });
        addView(touchArea);

        setWillNotDraw(false);
    }

    public void reflect(){
        checkedViews = 0;
        for(int i = 0;i<childs.size();i++){
            if(childs.get(i).isChecked) checkedViews++;
            for(int j = 0;j<childs.get(i).childs.size();j++){
                if(childs.get(i).childs.get(j).isChecked) checkedViews++;
                for(int k = 0;k<childs.get(i).childs.get(j).childs.size();k++){
                    if(childs.get(i).childs.get(j).childs.get(k).isChecked) checkedViews++;
                }
            }
        }

        if(saveBtn!=null){
            if(checkedViews>=1){
                saveBtn.btnText =(price*(checkedViews-1))+payPreInfo;
            }else{
                saveBtn.btnText = (price*(0))+payPreInfo;
            }

            saveBtn.invalidate();
        }

    }


    public void addChild(ListCategoryView child, boolean visible){

        if (root==null) root = this;
        child.root = root;

        if(childs.size()==0){
            removeView(touchArea);
            Bitmap arrowb = MainMenuActivity.getBitmapFromAsset(getContext(),"arrow_down_black.png");
            arrowb = Bitmap.createScaledBitmap(arrowb,(int)(arrowb.getWidth()*scale),(int)(arrowb.getHeight()*scale),false);
            arrow = new ImageView(getContext());
            arrow.setImageBitmap(arrowb);
            arrow.setX(offset);
            arrow.setY(61*scale-arrowb.getHeight()/2);
            arrow.setLayoutParams(new LayoutParams((int)(arrowb.getWidth()),(int)(arrowb.getHeight())));
            addView(arrow);
            Rect rect = new Rect();
            arrow.getHitRect(rect);
            rect.top = (int)(0*scale);    // increase top hit area
            rect.left -= (int)(0*scale);   // increase left hit area
            rect.bottom += (int)(110*scale); // increase bottom hit area
            rect.right +=(int)(500*scale);  // increase right hit area
            this.setTouchDelegate( new TouchDelegate( rect , arrow));
            arrow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(opened){
                        close();
                    }else {
                        open();
                    }
                }
            });

        }

        float height = 122*scale;
        for(int i = 0;i<childs.size();i++){
            height +=122*scale;
        }
        child.setY(height);
        if(visible) {
            addView(child);
        }
        childs.add(child);

    }

    private float getSizeHeight(){
        float result = 121*scale;
        if(opened) {
            for (int i = 0; i < childs.size(); i++) {
                result += childs.get(i).getSizeHeight();
            }
        }
        return result;
    }

    private void reorganizeChilds(){
        for(int i =0; i< childs.size()-1;i++){
            childs.get(i+1).setY(childs.get(i).getY()+childs.get(i).getSizeHeight());
        }
        for(int i =0; i< childs.size();i++){
            childs.get(i).reorganizeChilds();
        }
        float height = getSizeHeight();
        if(baseLayout!=null){
            baseLayout.setMinimumHeight((int)height);
            postBaseLayout.setLayoutParams(new LayoutParams((int)(1080*scale),(int)height));
        }
        setLayoutParams(new LayoutParams(getWidth(),(int)height));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        root.reorganizeChilds();
    }

    ValueAnimator textAnimator;
    float textOffset = 0;
    float rightTextStopper;
    Paint pStoper;

    @Override
    protected void onDraw(Canvas canvas) {
        if(childs.size()==0){
            //with checkBox
            if(textAnimator == null){
                Rect bounds = new Rect();
                pText.getTextBounds(label,0,label.length(),bounds);
                if((getWidth()-offset - rightTextStopper)<bounds.width()){
                    textAnimator = new ValueAnimator();
                    textAnimator.setDuration(5000);
                    textAnimator.setFloatValues(-40*scale,(getWidth()-offset - rightTextStopper)-bounds.width()-40*scale,-40*scale);
                    textAnimator.setRepeatMode(ValueAnimator.RESTART);
                    textAnimator.setRepeatCount(ValueAnimator.INFINITE);
                    textAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            textOffset = (float)animation.getAnimatedValue();
                            invalidate();
                        }
                    });
                    textAnimator.start();
                }
            }

            pBack.setColor(ResourseColors.colorWhite);
            Rect bounds =new Rect();
            pText.getTextBounds(label,0,label.length(),bounds);
            canvas.drawRect(0,0,getWidth(),122*scale,pBack);
            canvas.drawText(label,offset + textOffset,122*scale/2+bounds.height()/2,pText);
            canvas.drawRect(0,5*scale,offset,getHeight()-10*scale,pStoper);
            canvas.drawRect(getWidth()-rightTextStopper,5*scale,getWidth(),getHeight()-10*scale,pStoper);

            canvas.drawBitmap(box,getWidth()-70*scale-box.getWidth(),122*scale/2-box.getHeight()/2,pText);

            if(isChecked){
                if(isDefault) {
                    canvas.drawBitmap(defaul, getWidth() - 65 * scale - box.getWidth(), 132 * scale / 2 - box.getHeight() / 2 - 10 * scale, pText);
                }else{
                    if(isPayed){
                        canvas.drawBitmap(payed, getWidth() - 65 * scale - box.getWidth(), 132 * scale / 2 - box.getHeight() / 2 - 10 * scale, pText);
                    }else{
                        canvas.drawBitmap(check, getWidth() - 65 * scale - box.getWidth(), 132 * scale / 2 - box.getHeight() / 2 - 10 * scale, pText);
                    }
                }
            }
        }else{
            //without checkBox
            pBack.setColor(ResourseColors.colorGrayPrice);
            Rect bounds =new Rect();
            pText.getTextBounds(label,0,label.length(),bounds);
            canvas.drawRect(0,0,getWidth(),getHeight(),pBack);
            canvas.drawText(label,offset+arrow.getWidth()*1.5f,122*scale/2+bounds.height()/2,pText);
        }

        canvas.drawRect(0,0,getWidth(),2*scale,pText);
        canvas.drawRect(0,120*scale,getWidth(),122*scale,pText);

        super.onDraw(canvas);
    }

    public void open(){
        float height = 122*scale;
        for(int i = 0;i<childs.size();i++){
            height +=childs.get(i).getHeight();
        }
        arrow.setRotation(180);
        opened = true;
        reorganizeChilds();//setLayoutParams(new LayoutParams(getWidth(),(int)height));
    }


    public void close(){
        float height = 122*scale;
        opened = false;
        reorganizeChilds();//setLayoutParams(new LayoutParams(getWidth(),(int)height));
        arrow.setRotation(0);
    }

}
