package beka.com.bk.dushanbeonline.custom_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.widget.FrameLayout;
import android.widget.ImageView;

import beka.com.bk.dushanbeonline.MainMenuActivity;


@SuppressLint("AppCompatCustomView")
public class CustomPublishBtn extends FrameLayout {

    int color;
    public String btnText;
    public Paint p, pText;
    public ImageView back;
    float scale;


    public CustomPublishBtn(Context context, int color, String btnText) {
        super(context);
        this.color = color;
        this.btnText = btnText;
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;

        p = new Paint();
        p.setColor(color);
        p.setAntiAlias(true);

        pText = new Paint();
        pText.setColor(Color.rgb(255, 255, 255));
        pText.setAntiAlias(true);
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(), "font_medium.ttf"));
        pText.setTextSize((int) (60 * scale));

        Bitmap backbtn = MainMenuActivity.getBitmapFromAsset(context, "back_frw_btn.png");
        backbtn = Bitmap.createScaledBitmap(backbtn, (int) (60 * scale), (int) (60 * scale), false);
        back = new ImageView(context);
        back.setImageBitmap(backbtn);
        back.setX(964 * scale);
        back.setY(80*scale-30*scale);
        back.setScaleType(ImageView.ScaleType.MATRIX);
        addView(back);
        setWillNotDraw(false);


    }

    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);

        Rect bounds = new Rect();
        pText.getTextBounds(btnText, 0, btnText.length(), bounds);
        int height = bounds.height();
        int width = bounds.width();
        canvas.drawText(btnText, getWidth() / 2 - width / 2, getHeight() / 2 + height * 0.35f, pText);

        super.onDraw(canvas);

    }
}
