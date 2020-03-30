package beka.com.bk.dushanbeonline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import beka.com.bk.dushanbeonline.custom_views.CategoryButton;
import beka.com.bk.dushanbeonline.custom_views.SubCategoryMenu;

public class CreateTaskActivity extends Activity {
    ConstraintLayout layout;
    SubCategoryMenu subMenu;
    String[][] strings;
    Typeface type;
    ImageView imgBackBtn;
    int lang;
    float scale;
    boolean isCatAct = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        layout = new ConstraintLayout(this);
        setContentView(layout);
        lang = getIntent().getIntExtra("lang", 0);
        strings = new String[2][3];

        strings[0][0] = "Create task";
        strings[1][0] = "Создать задание";
        strings[0][1] = "For which specialist is your task?";
        strings[1][1] = "Для какого специалиста ваше задание?";
        strings[0][2] = "Lost connection.....";
        strings[1][2] = "Пожалуйста подключитесь к интернету...";

        layout.setBackgroundColor(Color.rgb(29, 35, 42));

        type = Typeface.createFromAsset(getAssets(), "font.ttf");
        design();
    }

    private void design() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        scale = (float) dm.widthPixels / 1080.0f;

        ScrollView scl = new ScrollView(this);
        scl.setY(360 * scale);
        ConstraintLayout layoutScroll = new ConstraintLayout(this);
        layoutScroll.setY(0);
        layoutScroll.setMinHeight((int) (9 * 384 * scale - 20 * scale));
        scl.addView(layoutScroll);
        layout.addView(scl);

        subMenu = new SubCategoryMenu(this, lang, (int) (dm.heightPixels - 362 * scale), 1);
        subMenu.setX(1080 * scale);
        subMenu.setY(362 * scale);
        layout.addView(subMenu);

        subMenu.setOpenCloseListener(new OpenCloseListener() {
            @Override
            public void onClose() {
                isCatAct = true;
            }

            @Override
            public void onOpen() {
                isCatAct = false;
            }
        });

        for (int i = 1; i < 9; i++) {
            Bitmap tutorImg = MainMenuActivity.getBitmapFromAsset(this, "MainMenu/" + (i + 1) + ".png");
            tutorImg = Bitmap.createScaledBitmap(tutorImg, (int) (1080 * scale), (int) (384 * scale), false);
            final CategoryButton btnTutor2 = new CategoryButton(this, tutorImg, MainMenuActivity.strings[lang][i + 1], (int) (20 * scale), scale, i, lang);
            btnTutor2.setX(0);
            btnTutor2.setY((384 * (i - 1)) * scale);
            btnTutor2.setLayoutParams(new LinearLayout.LayoutParams((int) (1080 * scale), (int) (384 * scale)));
            btnTutor2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // openpage(i);
                }
            });
            btnTutor2.setId(350 + i);
            layoutScroll.addView(btnTutor2);
            btnTutor2.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    if (isCatAct) {
                        int catId = v.getId() - 350;
                        subMenu.changeCategory(catId, false);
                    }
                }
            });
        }


        View greenLine = new View(this);
        greenLine.setLayoutParams(new FrameLayout.LayoutParams((int) (scale * 1080), (int) (scale * 175)));
        greenLine.setBackgroundColor(Color.rgb(135, 91, 213));
        greenLine.setY(187 * scale);
        layout.addView(greenLine);

        TextView tx2 = new TextView(this);
        tx2.setText(strings[lang][1]);
        tx2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 53 * scale);
        tx2.setTextColor(Color.WHITE);
        tx2.setTypeface(type);
        Rect bounds = new Rect();
        Paint textPaint = tx2.getPaint();
        textPaint.getTextBounds(strings[lang][1], 0, strings[lang][1].length(), bounds);
        int height = bounds.height();
        int width = bounds.width();
        tx2.setX(1080 * scale / 2 - width / 2);
        tx2.setY(240 * scale);
        layout.addView(tx2);

        TextView tx = new TextView(this);
        tx.setText(strings[lang][0]);
        tx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60 * scale);
        tx.setTextColor(Color.WHITE);
        tx.setTypeface(type);
        bounds = new Rect();
        textPaint = tx.getPaint();
        textPaint.getTextBounds(strings[lang][0], 0, strings[lang][0].length(), bounds);
        width = bounds.width();
        tx.setX(1080 * scale / 2 - width / 2);
        tx.setY(50 * scale);
        layout.addView(tx);

        Bitmap backbtn = MainMenuActivity.getBitmapFromAsset(this, "back_btn.png");
        backbtn = Bitmap.createScaledBitmap(backbtn, (int) (30 * scale), (int) (64 * scale), false);
        imgBackBtn = new ImageView(this);
        imgBackBtn.setImageBitmap(backbtn);
        imgBackBtn.setX(70 * scale);
        imgBackBtn.setY(60 * scale);
        imgBackBtn.setScaleType(ImageView.ScaleType.MATRIX);
        layout.addView(imgBackBtn);
        imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Rect rect = new Rect();
        imgBackBtn.getHitRect(rect);
        rect.top -= 100;    // increase top hit area
        rect.left -= 100;   // increase left hit area
        rect.bottom += 100; // increase bottom hit area
        rect.right += 100;
        layout.setTouchDelegate(new TouchDelegate(rect, imgBackBtn));

    }
}
