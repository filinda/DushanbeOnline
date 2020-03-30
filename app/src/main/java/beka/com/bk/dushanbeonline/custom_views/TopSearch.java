package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class TopSearch extends FrameLayout {

    public ImageView back, mapBtn, filterBtn;
    public EditText edt;
    float scale;
    private ImageView background;
    public String[][] strings;
    FrameLayout globalLay;
    public TextView tv;

    public TopSearch(Context context, int lang, FrameLayout globalLay) {
        super(context);
        this.globalLay = globalLay;
        strings = new String[2][2];
        strings[0][0] = "All tasks";
        strings[1][0] = "Все задания";
        strings[0][1] = "Map of tasks";
        strings[1][1] = "Карта заданий";
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        setBackgroundColor(ResourseColors.colorBlue);

        setLayoutParams(new LayoutParams((int)(1080*scale),(int)(168*scale)));

        tv = new TextView(getContext());
        tv.setText(strings[lang][0]);
        tv.setTextColor(ResourseColors.colorWhite);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,60*scale);
        tv.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        tv.setY(40*scale);
        tv.setTypeface(Typeface.createFromAsset(context.getAssets(),"font_medium.ttf"));
        addView(tv);

        Bitmap backbtn = MainMenuActivity.getBitmapFromAsset(context, "back_btn.png");
        backbtn = Bitmap.createScaledBitmap(backbtn, (int) (60 * scale), (int) (60 * scale), false);
        back = new ImageView(context);
        back.setImageBitmap(backbtn);
        back.setX(52 * scale);
        back.setY(56 * scale);
        back.setScaleType(ImageView.ScaleType.MATRIX);
        back.setLayoutParams(new LayoutParams((int) (60 * scale), (int) (60 * scale)));
        addView(back);
        Rect rect = new Rect();
        back.getHitRect(rect);
        rect.top -= 100;    // increase top hit area
        rect.left -= 100;   // increase left hit area
        rect.bottom += 100; // increase bottom hit area
        rect.right += 100;
        setTouchDelegate(new TouchDelegate(rect, back));
        setLayoutParams(new LayoutParams((int) ((1080) * scale), (int) (168 * scale)));
        setBackgroundColor(ResourseColors.colorBlue);

        Bitmap filter = MainMenuActivity.getBitmapFromAsset(context, "filter_icon.png");
        filterBtn = new ImageView(getContext());
        filterBtn.setImageBitmap(Bitmap.createScaledBitmap(filter,(int)(filter.getWidth()*scale),(int)(filter.getHeight()*scale),false));
        filterBtn.setScaleType(ImageView.ScaleType.MATRIX);
        filterBtn.setY(168*scale/2-filter.getHeight()*scale/2);
        filterBtn.setX(853*scale);
        addView(filterBtn);

        Bitmap map = MainMenuActivity.getBitmapFromAsset(context, "map_icon.png");
        mapBtn = new ImageView(getContext());
        mapBtn.setImageBitmap(Bitmap.createScaledBitmap(map,(int)(map.getWidth()*scale),(int)(map.getHeight()*scale),false));
        mapBtn.setScaleType(ImageView.ScaleType.MATRIX);
        mapBtn.setY(168*scale/2-map.getHeight()*scale/2);
        mapBtn.setX(965*scale);
        addView(mapBtn);

    }


}
