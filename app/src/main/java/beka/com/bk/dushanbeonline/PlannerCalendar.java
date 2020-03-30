package beka.com.bk.dushanbeonline;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import beka.com.bk.dushanbeonline.custom_views.DayView;

public class PlannerCalendar extends AppCompatActivity {

    ConstraintLayout layout;
    int lang;
    String[][] strings;
    static Typeface type;
    static float scale = 0.3f;
    ImageView imgBackBtn;
    ScrollView scrollView;
    ConstraintLayout layoutScroll;
    ArrayList<DayView> days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        lang = getIntent().getIntExtra("lang", 0);
        layout = new ConstraintLayout(this);
        layout.setBackgroundColor(Color.rgb(29, 35, 42));
        setContentView(layout);


        strings = new String[2][15];

        strings[0][0] = "Appointments";
        strings[1][0] = "Назначенные встречи";
        strings[0][1] = "Lost connection.....";
        strings[1][1] = "Пожалуйста подключитесь к интернету...";

        type = Typeface.createFromAsset(getAssets(), "font.ttf");

        design();
    }

    private void design() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        scale = (float) dm.widthPixels / 1080.0f;

        layoutScroll = new ConstraintLayout(this);
        layoutScroll.setBackgroundColor(Color.WHITE);

        scrollView = new ScrollView(this);
        scrollView.setY(185 * scale);
        scrollView.addView(layoutScroll);
        layout.addView(scrollView);

        layoutScroll.setLayoutParams(new FrameLayout.LayoutParams(1080, 1000));

        days = new ArrayList<>();
        int heightOfDays = 0;
        for (int i = 0; i < 5; i++) {
            DayView d1 = new DayView(this, 3, i + ".05.2018", lang);
            d1.setY(heightOfDays);
            heightOfDays += d1.myHeight - 3 * scale;
            layoutScroll.addView(d1);
            days.add(d1);
        }
        layoutScroll.setMinHeight(heightOfDays + (int) (185 * scale));
        layoutScroll.setMaxHeight(heightOfDays + (int) (185 * scale));


        TextView tx = new TextView(this);
        tx.setText(strings[lang][0]);
        tx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60 * scale);
        tx.setTextColor(Color.WHITE);
        tx.setTypeface(type);
        Rect bounds = new Rect();
        Paint textPaint = tx.getPaint();
        textPaint.getTextBounds(strings[lang][0], 0, strings[lang][0].length(), bounds);
        int height = bounds.height();
        int width = bounds.width();
        tx.setX(1080 * scale / 2 - width / 2);
        tx.setY(50 * scale);
        layout.addView(tx);

        Bitmap backbtn = getBitmapFromAsset(this, "back_btn.png");
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

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    AlertDialog noInternet;

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println(intent.getAction());
            Log.d("app", "Network connectivity change" + intent.getAction());
            if (noInternet == null) {
                noInternet = new AlertDialog.Builder(PlannerCalendar.this).create();
                noInternet.setMessage(strings[lang][1]);
                noInternet.setCanceledOnTouchOutside(false);
                noInternet.setCancelable(false);
            }
            if (!isOnline()) {
                //    noInternet.show();
            } else {
                noInternet.dismiss();
            }
        }
    };

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }
}
