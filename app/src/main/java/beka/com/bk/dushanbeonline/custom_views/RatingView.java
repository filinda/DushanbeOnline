package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class RatingView extends View {

    Bitmap starFill, starEmpty;
    private int stars;
    float scale;
    public int width, heiight;

    public RatingView(Context context) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        width = (int)(250*scale);
        heiight = (int)(40*scale);
        setLayoutParams(new FrameLayout.LayoutParams(width,heiight));
        starFill = MainMenuActivity.getBitmapFromAsset(context,"star_fill.png");
        starFill = Bitmap.createScaledBitmap(starFill,(int)(width*4f/25f),(int)(width*4f/25f),false);
        starEmpty = MainMenuActivity.getBitmapFromAsset(context,"star_empty.png");
        starEmpty = Bitmap.createScaledBitmap(starEmpty,(int)(width*4f/25f),(int)(width*4f/25f),false);
    }

    public void setWidth(int width){
        this.width = width;
        heiight = ((int)(width*4f/25f));
        setLayoutParams(new FrameLayout.LayoutParams(this.width,heiight));
        starFill = MainMenuActivity.getBitmapFromAsset(getContext(),"star_fill.png");
        starFill = Bitmap.createScaledBitmap(starFill,(int)(width*4f/25f),(int)(width*4f/25f),false);
        starEmpty = MainMenuActivity.getBitmapFromAsset(getContext(),"star_empty.png");
        starEmpty = Bitmap.createScaledBitmap(starEmpty,(int)(width*4f/25f),(int)(width*4f/25f),false);
        invalidate();
    }

    public void setStars(int stars) {
        this.stars = stars;
        invalidate();
    }
    public int getStars() {
        return stars;
    }

    Paint p =new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        p.setColor(ResourseColors.colorStroke);
        for(int i = 0; i<5; i++){
            if(stars >= i){
                canvas.drawBitmap(starFill,i*(width/5),0,p);
            }else{
                canvas.drawBitmap(starEmpty,i*(width/5),0,p);
            }
        }
        super.onDraw(canvas);
    }

}
