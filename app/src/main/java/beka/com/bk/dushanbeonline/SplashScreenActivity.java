package beka.com.bk.dushanbeonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.io.InputStream;

import beka.com.bk.dushanbeonline.custom_views.CustomSpiner;
import beka.com.bk.dushanbeonline.custom_views.SpinerButton;
import beka.com.bk.dushanbeonline.custom_views.changeListener;

public class SplashScreenActivity extends AppCompatActivity {

    FrameLayout layout;
    Typeface type;
    ConstraintLayout internetError;
    ProgressDialog noInternet;
    int lang = 1;
    TextView next;
    DisplayMetrics dm;
    float scale;
    ImageView arrow;
    public CustomSpiner spiner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = new FrameLayout(this);
        setContentView(layout);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        layout.setBackgroundColor(ResourseColors.colorBlue);
        type = Typeface.createFromAsset(getAssets(), "font.ttf");
        noInternet = new ProgressDialog(this);
        noInternet.setMessage("Lost connection.....");
        noInternet.setCanceledOnTouchOutside(false);
        noInternet.setCancelable(false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);


        design();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(SplashScreenActivity.this, MainMenuActivity.class);
            intent.putExtra("lang", lang);
            startActivity(intent);
        }
    }

    private void design() {

        dm = getResources().getDisplayMetrics();
        scale = (float) dm.widthPixels / 1080.0f;

        Bitmap doIcon = getBitmapFromAsset(this, "texlogo.png");
        doIcon = Bitmap.createScaledBitmap(doIcon, (int) (446 * scale), (int) (662 * scale), false);
        ImageView imgDoIcon = new ImageView(this);
        imgDoIcon.setImageBitmap(doIcon);
        imgDoIcon.setX(dm.widthPixels / 2 - 223 * scale);
        imgDoIcon.setY(348 * scale);
        imgDoIcon.setScaleType(ImageView.ScaleType.MATRIX);
        layout.addView(imgDoIcon);


        Bitmap rusFlag = getBitmapFromAsset(this, "russian_lang.png");
        rusFlag = Bitmap.createScaledBitmap(rusFlag, (int) (120 * scale), (int) (120 * scale), false);

        Bitmap engFlag = getBitmapFromAsset(this, "eng_lang.png");
        engFlag = Bitmap.createScaledBitmap(engFlag, (int) (120 * scale), (int) (120 * scale), false);


        spiner = new CustomSpiner(this);
        spiner.setY(dm.heightPixels - 465 * scale);
        spiner.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, 0));
        SpinerButton rus = new SpinerButton(this, "русский", rusFlag);
        rus.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, (int) (465 * scale / 3)));
        spiner.addItem(rus);
        SpinerButton eng = new SpinerButton(this, "english", engFlag);
        eng.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, (int) (465 * scale / 3)));
        spiner.addItem(eng);

        spiner.changeListener = new changeListener() {
            @Override
            public void onChange(boolean isOn) {
                if(isOn){
                    next.setText("Далее");
                }else{
                    next.setText("Next");
                }
                next.measure(0, 0);
                next.setX(dm.widthPixels / 2 - next.getMeasuredWidth() / 2);
            }

            @Override
            public void onChange(int lang) {

            }
        };


        layout.addView(spiner);


        internetError = new ConstraintLayout(this);

        next = new TextView(this);
        next.setText("Далее");
        next.setTextSize(TypedValue.COMPLEX_UNIT_PX, 55 * scale);
        next.setTypeface(Typeface.createFromAsset(getAssets(), "font_medium.ttf"));
        SpannableString content = new SpannableString("Далее");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        next.setText(content);
        next.setTextColor(ResourseColors.colorWhite);
        next.setY(dm.heightPixels - 130 * scale);
        next.measure(0, 0);
        next.setX(dm.widthPixels / 2 - next.getMeasuredWidth() / 2);
        layout.addView(next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreenActivity.this, RegistrationActivity.class);
                if (spiner.items.get(0).isChoosen)
                    intent.putExtra("lang", 1);
                if (spiner.items.get(1).isChoosen)
                    intent.putExtra("lang", 0);

                startActivity(intent);
            }
        });

    }

/*
* Intent intent = new Intent(SplashScreenActivity.this, SignUpActivity.class);
                intent.putExtra("lang", 1);
                startActivity(intent);*/

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

  /*  @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        dm = getResources().getDisplayMetrics();
        scale = (float) dm.widthPixels / 1080.0f;

        Bitmap doIcon = getBitmapFromAsset(this, "texlogo.png");
        doIcon = Bitmap.createScaledBitmap(doIcon, (int) (446 * scale), (int) (662 * scale), false);
        ImageView imgDoIcon = new ImageView(this);
        imgDoIcon.setImageBitmap(doIcon);
        imgDoIcon.setX(dm.widthPixels / 2 - 223 * scale);
        imgDoIcon.setY(348 * scale);
        imgDoIcon.setScaleType(ImageView.ScaleType.MATRIX);
        layout.addView(imgDoIcon);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                SharedPreferences prefs = getSharedPreferences("lang", MODE_PRIVATE);
                int lang  = prefs.getInt("lang", -1);
                if(lang != -1) {
                    updateUI(currentUser);
                }
            }
        }, 1000);

    }*/



   /* private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null) {
            if(currentUser.getPhoneNumber() == ""){
                Intent intent = new Intent(SplashScreenActivity.this, PhoneTaskActivity.class);
                SharedPreferences prefs = getSharedPreferences("lang", MODE_PRIVATE);
                int lang  = prefs.getInt("lang", 1);
                intent.putExtra("lang", lang);
                startActivity(intent);
            }else {
                Intent intent =new Intent(SplashScreenActivity.this, MainMenuActivity.class);
                SharedPreferences prefs = getSharedPreferences("lang", MODE_PRIVATE);
                int lang  = prefs.getInt("lang", 1);
                intent.putExtra("lang", lang);
                startActivity(intent);
            }
        }else{
            design();
        }
    }*/


}
