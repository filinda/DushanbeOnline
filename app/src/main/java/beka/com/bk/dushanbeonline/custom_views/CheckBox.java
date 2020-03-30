package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.widget.FrameLayout;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.MaskWatcher;
import beka.com.bk.dushanbeonline.ResourseColors;

public class CheckBox extends View implements View.OnClickListener {
    Paint pText;
    Bitmap box, check;
    CheckBox dependentBox;
    public boolean isCheck = false;
    float scale=1;
    String label;

    public CheckBox(Context context, String label) {
        super(context);
        this.label = label;
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        setOnClickListener(this);
        pText = new Paint();
        pText.setColor(ResourseColors.colorLightGray);
        pText.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"font.ttf"));
        pText.setTextSize(42*scale);
        pText.setAntiAlias(true);

        box = MainMenuActivity.getBitmapFromAsset(getContext(),"checkbox.png");
        box = Bitmap.createScaledBitmap(box,(int)(box.getWidth()*scale),(int)(box.getHeight()*scale),false);
        check = MainMenuActivity.getBitmapFromAsset(getContext(),"check.png");
        check = Bitmap.createScaledBitmap(check,(int)(check.getWidth()*scale),(int)(check.getHeight()*scale),false);

        setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(120*scale)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(isCheck){
            pText.setColor(ResourseColors.colorBlue);
        }else{
            pText.setColor(ResourseColors.colorLightGray);
        }
        canvas.drawText(label,(1080-1016)*scale/2,getHeight()-45*scale,pText);
        canvas.drawBitmap(box,getWidth()-80*scale,getHeight()/2-box.getHeight()/2,pText);

        if(isCheck){
            canvas.drawBitmap(check,getWidth()-75*scale,getHeight()/2-box.getHeight()/2,pText);
        }

        canvas.drawRect(0,getHeight()-2*scale,1080*scale,getHeight(),pText);
        canvas.drawRect(0,0,1080*scale,2*scale,pText);
    }


    public static void pair(CheckBox b1, CheckBox b2){
        b1.dependentBox = b2;
        b2.dependentBox = b1;
    }





    @Override
    public void onClick(View v) {
        isCheck = !isCheck;
        if(dependentBox!=null){
            dependentBox.isCheck=!isCheck;
            dependentBox.invalidate();
        }
        invalidate();
    }
}
