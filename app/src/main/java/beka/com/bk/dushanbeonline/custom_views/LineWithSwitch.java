package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class LineWithSwitch extends FrameLayout {

    public SwitchView swtch;
    TextView label;
    ImageView img;
    float scale=1;
    Paint pLine;

    public LineWithSwitch(@NonNull Context context, String text, String iconPath) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;

        label = new TextView(getContext());
        label.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX,45*scale);
        label.setX(scale*160);
        label.setY(scale*66);
        label.setText(text);
        addView(label);

        img = new ImageView(context);
        img.setScaleType(ImageView.ScaleType.MATRIX);
        Bitmap icon = MainMenuActivity.getBitmapFromAsset(context,iconPath);
        icon = Bitmap.createScaledBitmap(icon,(int)(icon.getWidth()*scale),(int)(icon.getHeight()*scale),false);
        img.setLayoutParams(new LayoutParams(icon.getWidth(),icon.getHeight()));
        img.setImageBitmap(icon);
        img.setY(200*scale/2-icon.getHeight()/2);
        img.setX(42*scale);
        addView(img);

        swtch = new SwitchView(context);
        swtch.setX((1080-168)*scale);
        swtch.setY(200*scale/2-25*scale);
        addView(swtch);

        pLine = new Paint();
        pLine.setColor(ResourseColors.colorLightGray);
        pLine.setAntiAlias(true);

        setLayoutParams(new LayoutParams((int)(1080*scale),(int)(200*scale)));

        setWillNotDraw(false);
        setBackgroundColor(ResourseColors.colorGrayPrice);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
