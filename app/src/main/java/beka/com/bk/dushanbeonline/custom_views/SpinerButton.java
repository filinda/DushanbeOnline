package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class SpinerButton extends View {

    public Bitmap icon;
    Bitmap arrow;
    String label;
    public int colorChoosen = Color.argb(50, 255, 255, 255);
    //   int colorNotChoosen;
    Paint pBackground, pText;
    public  boolean isChoosen;
    float scale;
    public int index, height2, width2;
    public int border;

    public SpinerButton(Context context, String label, Bitmap icon) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels / 1080f;
        this.label = label;
        this.icon = icon;
        //  setBackgroundColor(colorNotChoosen);
        pBackground = new Paint();
        pBackground.setColor(colorChoosen);
        pText = new Paint();
        pText.setColor(ResourseColors.colorWhite);
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_reg.ttf"));
        pText.setTextSize(60 * scale);
        arrow = MainMenuActivity.getBitmapFromAsset(context, "arrow_down.png");
        arrow = Bitmap.createScaledBitmap(arrow, (int) (46 * scale), (int) (29 * scale), false);
        border = (int) (5 * scale);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if (!isChoosen) {
            canvas.drawRect(border, border, getWidth2() - border, getHeight() - border, pBackground);
        }
        Rect bounds = new Rect();
        pText.getTextBounds(label, 0, label.length(), bounds);
        canvas.drawText(label, getWidth() / 2 - (446 * scale) / 2 + icon.getWidth() + 30 * scale, getHeight() / 2 + bounds.height() / 4, pText);
        canvas.drawBitmap(icon, getWidth() / 2 - (446 * scale) / 2, getHeight() / 2 - icon.getHeight() / 2, pText);
        if (isChoosen) {
            canvas.drawBitmap(arrow, getWidth() / 2 - (446 * scale) / 2 + icon.getWidth() + 60 * scale + bounds.width(), getHeight() / 2 - arrow.getHeight() / 2, pText);
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        width2 = params.width;
        height2 = params.height;
        super.setLayoutParams(params);
    }

    public int getWidth2() {
        return width2;
    }

    public int getHeight2() {
        return height2;
    }

}