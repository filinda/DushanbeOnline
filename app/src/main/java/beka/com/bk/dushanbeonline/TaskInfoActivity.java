package beka.com.bk.dushanbeonline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.runtime.image.ImageProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import beka.com.bk.dushanbeonline.custom_views.CustomRoundButton;
import beka.com.bk.dushanbeonline.custom_views.LoadCircle;
import beka.com.bk.dushanbeonline.custom_views.PersonNameView;
import beka.com.bk.dushanbeonline.custom_views.RatingPopUp;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;
import beka.com.bk.dushanbeonline.custom_views.WorkerShortView;

public class TaskInfoActivity extends AppCompatActivity {

    TaskData taskData;
    FrameLayout layout, sclLayout;
    ScrollView scl;
    String[][] strings, stringsDays;
    int lang;
    TextView publicationTx, adressSmall, publicDateTx, nameTx, priceTx, priceTx2, adressTx, descTx, descLoadTx, startTx, startLoadTx;// addDescTx, addDescLoadTx
    ImageView pick, calIcon;
    String id;
    Typeface typeRegular, typeBold;
    float scale;
    DisplayMetrics dm;
    Date date, startDate;
    String time = "00:00";
    double lat = 38.569962, lon = 68.772682;
    ImageProvider imgProv;
    boolean budget = true;
    long price = 0;
    CustomPublishBtn callBtn;
    Bitmap callAct, callInact;
    Intent intent;
    boolean latSet = false, lonSet = false;
    String taskPhone = "";
    String taskUid = "";
    WorkerShortView personView;
    int category=-1, subCategory=-1, subSubCategory=-1;
    TextView delete;
    ImageView arrow;
    TextView workLine;
    View workLineBackground;
    LoadCircle loading;
    View mask;
    ArrayList<WorkerShortView> workers = new ArrayList<>();
    ImageView editBtn;
    boolean unreterned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        unreterned = getIntent().getBooleanExtra("unreturnable",false);

        layout = new FrameLayout(this);
        setContentView(layout);

        date = new Date();
        imgProv = ImageProvider.fromAsset(this, "navi_icon_set.png");

        MapKitFactory.setApiKey("e7223c7a-7840-41e3-b4c7-7ef8948c63e4");
        MapKitFactory.initialize(this);
        lang = getIntent().getIntExtra("lang", 1);
        id = getIntent().getStringExtra("id");
        System.out.println("ololok get "+id);
        taskPhone = getIntent().getStringExtra("phone");
        System.out.println("ololok get "+taskPhone);

        strings = new String[2][32];
        strings[0][0] = "Publishing date";
        strings[1][0] = "Дата публикации";
        strings[0][1] = "Task";
        strings[1][1] = "Задание";
        strings[0][2] = "Description";
        strings[1][2] = "Описание";
        strings[0][3] = "Deadline";
        strings[1][3] = "Сроки";
        strings[0][4] = "Finish";
        strings[1][5] = "Закончить";
        strings[0][5] = "Through our payment system";
        strings[1][5] = "Через нашу систему";
        strings[0][6] = "Cash";
        strings[1][6] = "Наличными";
        strings[0][7] = "Customer";
        strings[1][7] = "Заказчик";
        strings[0][8] = "Additional comments";
        strings[1][8] = "Дополнительные коментарии";
        strings[0][9] = "tjs.";
        strings[1][9] = "сом.";
        strings[0][10] = "Price";
        strings[1][10] = "Оплата";
        strings[0][11] = "Price";
        strings[1][11] = "Оплата";
        strings[0][12] = "Ready to accept";
        strings[1][12] = "Готов принять";
        strings[0][13] = "Address";
        strings[1][13] = "Адрес";
        strings[0][14] = "Are you sure?";
        strings[1][14] = "Вы уверенны?";
        strings[0][15] = "Yes";
        strings[1][15] = "Да";
        strings[0][16] = "No";
        strings[1][16] = "Нет";
        strings[0][17] = "Remove task";
        strings[1][17] = "Удалить задание";
        strings[0][18] = "Task removed";
        strings[1][18] = "Задание удалено";
        strings[0][19] = "Create same task";
        strings[1][19] = "Создать такое же задание";
        strings[0][20] = "bad pofi";
        strings[1][20] = "bad pofi";
        strings[0][21] = "Request sent";
        strings[1][21] = "Запрос отправлен";
        strings[0][22] = "Applicants";
        strings[1][22] = "Претенденты";
        strings[0][23] = "Customer";
        strings[1][23] = "Заказчик";
        strings[0][24] = "Accept";
        strings[1][24] = "Принять";
        strings[0][25] = "Refuse";
        strings[1][25] = "Отказаться";
        strings[0][26] = "Finish work";
        strings[1][26] = "Завершить работу";
        strings[0][27] = "Hired";
        strings[1][27] = "Нанят";
        strings[0][28] = "Task finished";
        strings[1][28] = "Задание выполнено";
        strings[0][29] = "Call";
        strings[1][29] = "Позвонить";
        strings[0][30] = "Waiting for confirmation";
        strings[1][30] = "Ожидание подтверждения";
        strings[0][31] = "Completing";
        strings[1][31] = "Завершение";


        stringsDays = new String[2][7];
        stringsDays[0][0] = "Sun";
        stringsDays[1][0] = "Воскр";
        stringsDays[0][2] = "Tue";
        stringsDays[1][2] = "Вт";
        stringsDays[0][3] = "Wed";
        stringsDays[1][3] = "Ср";
        stringsDays[0][4] = "Thu";
        stringsDays[1][4] = "Четв";
        stringsDays[0][5] = "Fri";
        stringsDays[1][5] = "Пят";
        stringsDays[0][6] = "Sat";
        stringsDays[1][6] = "Суб";
        stringsDays[0][1] = "Mon";
        stringsDays[1][1] = "Пон";

        typeRegular = Typeface.createFromAsset(getAssets(), "font.ttf");
        typeBold = Typeface.createFromAsset(getAssets(), "font_bold.ttf");

        startDate = new Date();

        design();
    }

    TopLabel label;
    boolean personShown = false;

    public void design() {
        dm = getResources().getDisplayMetrics();
        scale = (float) dm.widthPixels / 1080.0f;

        scl = new ScrollView(this);
        scl.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, dm.heightPixels - (int) (183 * scale)));
        scl.setY(163 * scale);
        scl.setBackgroundColor(ResourseColors.colorWhite);
        sclLayout = new FrameLayout(this);
        sclLayout.setY(40 * scale);
       // sclLayout.setBackgroundColor(Color.YELLOW);
        sclLayout.setMinimumHeight((int) (2500 * scale));
        scl.addView(sclLayout);
        layout.addView(scl);

        publicationTx = new TextView(this);
        publicationTx.setTypeface(typeRegular);
        publicationTx.setText(strings[lang][0] + " ");
        publicationTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * scale);
        publicationTx.setX(45 * scale);
        publicationTx.setY(10 * scale);
        publicationTx.setTextColor(ResourseColors.colorLightGray);
        sclLayout.addView(publicationTx);

        publicDateTx = new TextView(this);
        publicDateTx.setTypeface(typeRegular);
        publicDateTx.setTextColor(ResourseColors.colorBlack);
        publicDateTx.setText("---");
        Rect bounds = new Rect();
        publicationTx.getPaint().getTextBounds(strings[lang][0] + ": ", 0, (strings[lang][0] + ": ").length(), bounds);
        publicDateTx.setX(75 * scale + bounds.width());
        publicDateTx.setY(10 * scale);
        publicDateTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * scale);
        sclLayout.addView(publicDateTx);

        nameTx = new TextView(this);
        nameTx.setTypeface(typeBold);
        nameTx.setText("---");
        nameTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 55 * scale);
        nameTx.setY(80 * scale);
        nameTx.setX(45 * scale);
        nameTx.setTextColor(ResourseColors.colorBlack);
        nameTx.setLayoutParams(new FrameLayout.LayoutParams((int) (1000 * scale), (int) (600 * scale)));
        sclLayout.addView(nameTx);

        priceTx2 = new TextView(this);
        priceTx2.setTypeface(typeRegular);
        priceTx2.setTextColor(ResourseColors.colorLightGray);
        priceTx2.setText("---");
        priceTx2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * scale);
        priceTx2.setY(150 * scale);
        priceTx2.setX(45 * scale);
        sclLayout.addView(priceTx2);

        priceTx = new TextView(this);
        priceTx.setTypeface(typeRegular);
        priceTx.setTextColor(ResourseColors.colorBlue);
        priceTx.setText("---");
        priceTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50 * scale);
        priceTx.setY(180 * scale);
        priceTx.setX(45 * scale);
        sclLayout.addView(priceTx);

        line2 = new View(this);
        //line2.setX(45 * scale);
        line2.setY(340 * scale);
        line2.setBackgroundColor(Color.rgb(200, 200, 200));
        line2.setLayoutParams(new LinearLayout.LayoutParams((int) (1080 * scale), (int) (4 * scale)));
        sclLayout.addView(line2);

        adressSmall = new TextView(this);
        adressSmall.setTypeface(typeRegular);
        adressSmall.setTextColor(ResourseColors.colorLightGray);
        adressSmall.setText(strings[lang][13]);
        adressSmall.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * scale);
        adressSmall.setY(300 * scale);
        adressSmall.setX(45 * scale);
        sclLayout.addView(adressSmall);

        pick = new ImageView(this);
        pick.setScaleType(ImageView.ScaleType.MATRIX);
        pick.setY(342 * scale);
        pick.setX(45 * scale);
        Bitmap pic = MainMenuActivity.getBitmapFromAsset(this, "navi_icon_set.png");
        pick.setImageBitmap(Bitmap.createScaledBitmap(pic, (int) (pic.getWidth() *1.3* scale), (int) (pic.getHeight()  *1.3* scale), false));
        sclLayout.addView(pick);

        adressTx = new TextView(this);
        adressTx.setTypeface(typeRegular);
        adressTx.setTextColor(ResourseColors.colorBlack);
        adressTx.setText(strings[lang][13]);
        adressTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        adressTx.setY(350 * scale);
        adressTx.setX(115 * scale);
        adressTx.setLayoutParams(new FrameLayout.LayoutParams((int) ((1080 - 220) * scale), (int) (150 * scale)));
        adressTx.setWidth((int) ((1080 - 220) * scale));
        sclLayout.addView(adressTx);

        arrow = new ImageView(this);
        Bitmap temp = MainMenuActivity.getBitmapFromAsset(this,"arrow.png");
        temp = Bitmap.createScaledBitmap(temp,(int)(temp.getWidth()*scale),(int)(temp.getHeight()*scale),false);
        arrow.setImageBitmap(temp);
        arrow.setScaleType(ImageView.ScaleType.MATRIX);
        arrow.setY(adressTx.getY()+40*scale);
        arrow.setX(1080*scale-arrow.getWidth()-60*scale);

        line = new View(this);
        line.setY(480 * scale);
        line.setBackgroundColor(Color.rgb(200, 200, 200));
        line.setLayoutParams(new LinearLayout.LayoutParams((int) (1080 * scale), (int) (4 * scale)));
        sclLayout.addView(line);

        descTx = new TextView(this);
        descTx.setTypeface(typeRegular);
        descTx.setTextColor(ResourseColors.colorLightGray);
        descTx.setText(strings[lang][2]);
        descTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * scale);
        descTx.setY(480 * scale);
        descTx.setX(45 * scale);
        sclLayout.addView(descTx);

        descLoadTx = new TextView(this);
        descLoadTx.setTypeface(typeRegular);
        descLoadTx.setTextColor(ResourseColors.colorGray);
        descLoadTx.setText("waitToLoad");
        descLoadTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        descLoadTx.setY(550 * scale);
        descLoadTx.setX(45 * scale);
        descLoadTx.setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale - 90*scale), ViewGroup.LayoutParams.WRAP_CONTENT));
        sclLayout.addView(descLoadTx);

        line3 = new View(this);
        line3.setY(480 * scale);
        line3.setBackgroundColor(Color.rgb(200, 200, 200));
        line3.setLayoutParams(new LinearLayout.LayoutParams((int) (1080 * scale), (int) (4 * scale)));
        sclLayout.addView(line3);

        calIcon = new ImageView(this);
        calIcon.setScaleType(ImageView.ScaleType.MATRIX);
        calIcon.setY(342 * scale);
        calIcon.setX(45 * scale);
        Bitmap calend = MainMenuActivity.getBitmapFromAsset(this, "date_icon_set.png");
        calIcon.setImageBitmap(Bitmap.createScaledBitmap(calend, (int) (calend.getWidth() * scale), (int) (calend.getHeight() * scale), false));
        sclLayout.addView(calIcon);

        startTx = new TextView(this);
        startTx.setTypeface(typeRegular);
        startTx.setTextColor(ResourseColors.colorLightGray);
        startTx.setText(strings[lang][3]);
        startTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * scale);
        startTx.setY(680 * scale);
        startTx.setX(45 * scale);
        sclLayout.addView(startTx);

        startLoadTx = new TextView(this);
        startLoadTx.setTypeface(typeRegular);
        startLoadTx.setTextColor(ResourseColors.colorBlack);
        startLoadTx.setText("begin");
        startLoadTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        startLoadTx.setY(750 * scale);
        startLoadTx.setX(115 * scale);
        sclLayout.addView(startLoadTx);

        line4 = new View(this);
        line4.setY(480 * scale);
        line4.setBackgroundColor(Color.rgb(200, 200, 200));
        line4.setLayoutParams(new LinearLayout.LayoutParams((int) (1080 * scale), (int) (4 * scale)));
        sclLayout.addView(line4);

        callBtn = new CustomPublishBtn(this, Color.rgb(143, 206, 76), "");
        callBtn.setY(dm.heightPixels - 160 * scale);
        callBtn.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (160 * scale)));
        callBtn.setX(0);
        callAct = MainMenuActivity.getBitmapFromAsset(this, "call_icon.png");
        callInact = MainMenuActivity.getBitmapFromAsset(this, "call_inact.png");
        callAct = Bitmap.createScaledBitmap(callAct, (int) (200 * scale), (int) (200 * scale), false);
        callInact = Bitmap.createScaledBitmap(callInact, (int) (200 * scale), (int) (200 * scale), false);

        label = new TopLabel(this, strings[lang][1] + " №" + String.format("%S", id), scale);
        label.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layout.addView(label);

        editBtn = new ImageView(this);
        Bitmap bp = MainMenuActivity.getBitmapFromAsset(this, "editor_icon_white.png");
        editBtn.setImageBitmap(Bitmap.createScaledBitmap(bp,(int)(bp.getWidth()*scale),(int)(bp.getHeight()*scale),false));
        editBtn.setLayoutParams(new FrameLayout.LayoutParams((int)(bp.getWidth()*scale),(int)(bp.getHeight()*scale)));
        editBtn.setScaleType(ImageView.ScaleType.MATRIX);
        editBtn.setY(65*scale);
        editBtn.setX(dm.widthPixels-(130*scale));
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskInfoActivity.this,TaskEditorActivity.class);
                intent.putExtra("taskId",id);
                intent.putExtra("lang",lang);
                intent.putExtra("category",category);
                intent.putExtra("subCategory",subCategory);
                intent.putExtra("subSubCategory",subSubCategory);

                startActivity(intent);
            }
        });

        delete = new TextView(this);
        delete.setText(strings[lang][17]);
        delete.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * scale);
        delete.setTypeface(Typeface.createFromAsset(getAssets(), "font_medium.ttf"));
        SpannableString content = new SpannableString(strings[lang][17]);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        delete.setText(content);
        delete.setTextColor(ResourseColors.colorRed);
        delete.setY(((dm.heightPixels - 420*scale + (dm.heightPixels-150*scale))/2) - 100*scale);
        delete.measure(0, 0);
        delete.setX(dm.widthPixels / 2 - delete.getMeasuredWidth() / 2);
        delete.setLayoutParams(new FrameLayout.LayoutParams(delete.getMeasuredWidth(),delete.getMeasuredHeight()));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                FirebaseDatabase.getInstance().getReference().child("tasks").child(taskPhone).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(TaskInfoActivity.this,strings[lang][18],Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(TaskInfoActivity.this,MyTasksActivity.class);
                                        intent.putExtra("lang",lang);
                                        startActivity(intent);
                                    }
                                });

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
                builder.setMessage(strings[lang][14]).setPositiveButton(strings[lang][15], dialogClickListener)
                        .setNegativeButton(strings[lang][16], dialogClickListener).show();

            }

        });


        workLineBackground = new View(this);
        workLineBackground.setBackgroundColor(ResourseColors.colorBlue);
        workLineBackground.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels,(int)(100*scale)));
        workLineBackground.setY(480*scale);

        workLine = new TextView(TaskInfoActivity.this);
        workLine.setTextColor(ResourseColors.colorWhite);
        workLine.setTypeface(Typeface.createFromAsset(TaskInfoActivity.this.getAssets(), "font.ttf"));
        workLine.setTextSize(TypedValue.COMPLEX_UNIT_PX, 57 * scale);
        workLine.setY(480*scale + 5 * scale);

        sclLayout.addView(workLineBackground);
        sclLayout.addView(workLine);

        loadData();
    }

    @Override
    public void onBackPressed() {
        if(unreterned){
            Intent intent = new Intent(this,MyTasksActivity.class);
            intent.putExtra("lang",lang);
            startActivity(intent);
        }else {
            super.onBackPressed();
        }
    }

    View line, line2, line3, line4;

    private void resize() {
        float totalHeight;// = nameTx.getY();
        totalHeight = nameTx.getLineHeight() * nameTx.getLineCount();
        priceTx2.setY((110 * scale) + totalHeight);
        priceTx.setY((170 * scale) + totalHeight);
        arrow.setY(adressTx.getY()+200*scale);
        line2.setY((270 * scale) + totalHeight);
        totalHeight = priceTx.getY() + priceTx.getLineHeight() * priceTx.getLineCount();
        adressSmall.setY(totalHeight + 60 * scale);
        pick.setY((130 * scale) + totalHeight);
        if(adressTx.getLineCount()==1) {
            adressTx.setY(totalHeight + 130 * scale);
            line.setY(adressTx.getY() + 100 * scale);
        }
        else{
            adressTx.setY(totalHeight + 110 * scale);
            line.setY(adressTx.getY() + 130 * scale);
        }

        totalHeight = line.getY() + 0 * scale;
        calIcon.setY((90 * scale) + totalHeight);
        startTx.setY(totalHeight + 18 * scale);
        startLoadTx.setY(startTx.getY() + 75 * scale);
        totalHeight = startLoadTx.getY() + startLoadTx.getLineCount() * startLoadTx.getLineHeight() + 0 * scale;
        line3.setY(totalHeight + 25 * scale);
        descTx.setY(totalHeight + 40 * scale);
        descLoadTx.setY(descTx.getY() + 70 * scale);
        totalHeight = descLoadTx.getY() + descLoadTx.getLineCount() * descLoadTx.getLineHeight();
        line4.setY(totalHeight + 35 * scale);

        //workers
        if(!personShown) {
            workLine.setY(totalHeight + 43 * scale);
            workLineBackground.setY(totalHeight + 35 * scale);
            totalHeight += 35 * scale + workLineBackground.getHeight();

            for (int i = 0; i < workers.size(); i++) {
                workers.get(i).setY(totalHeight + i * workers.get(i).height);
            }
            if(workers.size()!=0)
            totalHeight += workers.size() * workers.get(0).height;
        }

        if(personShown) {
            workLine.setY(totalHeight + 43 * scale);
            workLineBackground.setY(totalHeight + 35 * scale);
            totalHeight += 35 * scale + workLineBackground.getHeight();
            if(personView!=null) {
                personView.setY(totalHeight + 20 * scale);
                totalHeight = personView.getY() + personView.height;
            }
        }

        delete.setY(totalHeight+50*scale);
        totalHeight = delete.getY()+delete.getMeasuredHeight();
        sclLayout.setMinimumHeight((int) totalHeight);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TaskInfoActivity.this.startActivity(intent);
    }
    DatabaseReference reference;
    boolean taskComplitted = false;
    boolean taskHalfComplitted = false;

    public void loadData() {

        mask = new View(this);
        mask.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels,dm.heightPixels-(int)(163*scale)));
        mask.setY((int)(163*scale));
        mask.setBackgroundColor(ResourseColors.colorWhite);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mask.setZ(10000);
        }
        layout.addView(mask);

        loading = new LoadCircle(this);
        loading.setX(1080/2*scale-loading.bmp.getWidth()/2);
        loading.setY(400*scale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loading.setZ(10010);
        }
        layout.addView(loading);
        loading.start();

        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(taskPhone).child(id);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                taskUid = dataSnapshot.child("userId").getValue(String.class);

                if (!taskUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    personShown = true;
                }

                /*if (!taskUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                   // layout.addView(callBtn);
                }*/

                if(personShown)
                {
                    workLine.setText(strings[lang][23]);
                    workLineBackground.setBackgroundColor(ResourseColors.colorGreen);
                }else{
                    workLine.setText(strings[lang][22]);
                }
                workLine.measure(0, 0);
                workLine.setX(dm.widthPixels / 2 - workLine.getMeasuredWidth() / 2);


                if(personShown) {
                    personView = new WorkerShortView(TaskInfoActivity.this, dataSnapshot.child("userId").getValue(String.class), lang);
                    personView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent  = new Intent(TaskInfoActivity.this,ProfiPage.class);
                            intent.putExtra("profiId",personView.id);
                            intent.putExtra("lang",lang);
                            intent.putExtra("OpenAsProfi",false);
                            TaskInfoActivity.this.startActivity(intent);
                        }
                    });
                    sclLayout.addView(personView);
                }else{
                    sclLayout.removeView(delete);
                    sclLayout.addView(delete);
                }

                if(!taskUid.equals(FirebaseAuth.getInstance().getUid())) {
                    layout.addView(arrow);
                }

                adressTx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isOkProfi&!taskUid.equals(FirebaseAuth.getInstance().getUid())) {
                            Intent intent = new Intent(TaskInfoActivity.this, RouteActivity.class);
                            intent.putExtra("lang", lang);
                            intent.putExtra("aLat", lat);
                            intent.putExtra("aLon", lon);
                            intent.putExtra("uid", taskUid);
                            intent.putExtra("address", adressTx.getText().toString());
                            startActivity(intent);
                        }else{
                            if(!isOkProfi&!taskUid.equals(FirebaseAuth.getInstance().getUid())){
                                Toast.makeText(TaskInfoActivity.this,strings[lang][20],Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

                if(taskUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    layout.addView(editBtn);
                }


                if (dataSnapshot.child("name").getValue(String.class) != null)
                    nameTx.setText(dataSnapshot.child("name").getValue(String.class));

                if (dataSnapshot.child("startDateY").getValue(Long.class) != null & dataSnapshot.child("startDateM").getValue(Long.class) != null & dataSnapshot.child("startDateD").getValue(Long.class) != null) {
                    startDate.setYear((dataSnapshot.child("startDateY").getValue(Long.class)).intValue()-1900);
                    startDate.setMonth((dataSnapshot.child("startDateM").getValue(Long.class)).intValue());
                    startDate.setDate((dataSnapshot.child("startDateD").getValue(Long.class)).intValue());
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                    startLoadTx.setText(stringsDays[lang][startDate.getDay()]+"  "+(format.format(startDate)));
                }

                if (dataSnapshot.child("priceVal").getValue(Long.class) != null) {
                    price = dataSnapshot.child("priceVal").getValue(Long.class);
                    priceTx.setText(price + " " + strings[lang][9]);
                    if ((price > 999)) {
                        priceTx.setText(priceTx.getText().toString().substring(0, priceTx.getText().toString().length() - 8) + "," + priceTx.getText().toString().substring(priceTx.getText().toString().length() - 8));
                    }

                    if (budget) {
                        priceTx2.setText(strings[lang][10]);
                    } else {
                        priceTx2.setText(strings[lang][11]);
                    }
                }

                if (dataSnapshot.child("budgetBool").getValue(Boolean.class) != null) {
                    budget = (dataSnapshot.child("budgetBool").getValue(Boolean.class));

                    priceTx.setText(price + " " + strings[lang][9]);
                    if ((price > 999)) {
                        priceTx.setText(priceTx.getText().toString().substring(0, priceTx.getText().toString().length() - 8) + "," + priceTx.getText().toString().substring(priceTx.getText().toString().length() - 8));
                    }

                    if (budget) {
                        priceTx2.setText(strings[lang][10]);
                    } else {
                        priceTx2.setText(strings[lang][11]);
                    }
                    resize();
                }

                if (dataSnapshot.child("publicDateY").getValue(Long.class) != null & dataSnapshot.child("publicDateM").getValue(Long.class) != null & dataSnapshot.child("publicDateD").getValue(Long.class) != null & dataSnapshot.child("publicDate").getValue(String.class) != null) {
                    date.setYear((dataSnapshot.child("publicDateY").getValue(Long.class)).intValue() - 1900);
                    date.setMonth((dataSnapshot.child("publicDateM").getValue(Long.class)).intValue());
                    date.setDate((dataSnapshot.child("publicDateD").getValue(Long.class)).intValue());
                    time = dataSnapshot.child("publicDate").getValue(String.class);
                    publicDateTx.setText(" " + String.format("%tF", date) + "  " + time);
                    resize();
                }

                if (dataSnapshot.child("desc").getValue(String.class) != null)
                    descLoadTx.setText(dataSnapshot.child("desc").getValue(String.class));

                if (dataSnapshot.child("lat").getValue(Double.class) != null) {
                    lat = dataSnapshot.child("lat").getValue(Double.class);
                    latSet = true;
                }

                if (dataSnapshot.child("lon").getValue(Double.class) != null) {
                    lon = dataSnapshot.child("lon").getValue(Double.class);
                    lonSet = true;
                }

                if (dataSnapshot.child("adres").getValue(String.class) != null)
                    adressTx.setText(dataSnapshot.child("adres").getValue(String.class));

                if (dataSnapshot.child("categoryId").getValue(Integer.class) != null){
                    category = dataSnapshot.child("categoryId").getValue(Integer.class);
                }

                if (dataSnapshot.child("subCategoryId").getValue(Integer.class) != null){
                    subCategory = dataSnapshot.child("subCategoryId").getValue(Integer.class);
                }

                if (dataSnapshot.child("subSubCategoryId").getValue(Integer.class) != null){
                    subSubCategory = dataSnapshot.child("subSubCategoryId").getValue(Integer.class);
                }
                resize();

                reference2 = FirebaseDatabase.getInstance().getReference().child("tasks").child(taskPhone).child(id).child("profies");

                profiListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null)
                        {
                            if (dataSnapshot.child(FirebaseAuth.getInstance().getUid()).getValue() != null) {
                                sentInvite = dataSnapshot.child(FirebaseAuth.getInstance().getUid()).getValue(Long.class).intValue();
                            } else {
                                sentInvite = -1;
                            }
                            if (!personShown) {
                                int i = 0;
                                for (int j = 0; j < workers.size(); j++) {
                                    sclLayout.removeView(workers.get(j));
                                }
                                workers.clear();
                                somebodyHiered = false;
                                for (DataSnapshot worker : dataSnapshot.getChildren()) {
                                    if (worker.getValue(Long.class) == 2L) {
                                        somebodyHiered = true;
                                        WorkerShortView workerView = new WorkerShortView(TaskInfoActivity.this, worker.getKey(), lang);
                                        hiredProfiUid = workerView.id;
                                        workerView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(TaskInfoActivity.this, ProfiPage.class);
                                                intent.putExtra("profiId", ((WorkerShortView) v).id);
                                                intent.putExtra("lang", lang);
                                                intent.putExtra("taskId", id);
                                                intent.putExtra("phone", taskPhone);
                                                intent.putExtra("OpenAsProfi", true);
                                                TaskInfoActivity.this.startActivity(intent);
                                            }
                                        });
                                        System.out.println("abab added profie " + i);
                                        workers.add(workerView);
                                        sclLayout.addView(workers.get(i));
                                        i++;
                                    }

                                    if (worker.getValue(Long.class) == 3L) {
                                        somebodyHiered = true;
                                        taskHalfComplitted = true;
                                        WorkerShortView workerView = new WorkerShortView(TaskInfoActivity.this, worker.getKey(), lang);
                                        hiredProfiUid = workerView.id;
                                        workerView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(TaskInfoActivity.this, ProfiPage.class);
                                                intent.putExtra("profiId", ((WorkerShortView) v).id);
                                                intent.putExtra("lang", lang);
                                                intent.putExtra("taskId", id);
                                                intent.putExtra("phone", taskPhone);
                                                intent.putExtra("OpenAsProfi", true);
                                                TaskInfoActivity.this.startActivity(intent);
                                            }
                                        });
                                        System.out.println("abab added profie " + i);
                                        workers.add(workerView);
                                        sclLayout.addView(workers.get(i));
                                        i++;
                                    }

                                    if (worker.getValue(Long.class) == 4L) {
                                        somebodyHiered = true;
                                        taskHalfComplitted = true;
                                        taskComplitted = true;
                                        workLineBackground.setBackgroundColor(ResourseColors.colorStroke);
                                    }

                                }
                                if (!somebodyHiered) {
                                    workLine.setText(strings[lang][22]);
                                    for (DataSnapshot worker : dataSnapshot.getChildren()) {
                                        if (worker.getValue(Long.class) != -2) {
                                            WorkerShortView workerView = new WorkerShortView(TaskInfoActivity.this, worker.getKey(), lang);
                                            workerView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(TaskInfoActivity.this, ProfiPage.class);
                                                    intent.putExtra("profiId", ((WorkerShortView) v).id);
                                                    intent.putExtra("lang", lang);
                                                    intent.putExtra("taskId", id);
                                                    intent.putExtra("phone", taskPhone);
                                                    intent.putExtra("OpenAsProfi", true);
                                                    TaskInfoActivity.this.startActivity(intent);
                                                }
                                            });
                                            System.out.println("abab added profie " + i);
                                            workers.add(workerView);
                                            sclLayout.addView(workers.get(i));
                                            i += 1;
                                        }
                                    }
                                }else{
                                    if(!taskComplitted&!taskHalfComplitted) {
                                        workLine.setText(strings[lang][27]);
                                    }else{
                                        if(taskHalfComplitted) {
                                            workLine.setText(strings[lang][31]);
                                        }
                                        if(taskComplitted){
                                            workLine.setText(strings[lang][28]);
                                        }
                                    }
                                }
                                Rect bounds = new Rect();
                                workLine.getPaint().getTextBounds(workLine.getText().toString(),0,workLine.getText().length(),bounds);
                                workLine.setX( dm.widthPixels / 2 - bounds.width()/ 2);
                            }
                        } else
                        {
                            System.out.println("abab no profies");
                        }
                        checkProfi();
                        resize();
                        loading.stop();
                        layout.removeView(loading);
                        layout.removeView(mask);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                reference2.addValueEventListener(profiListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    DatabaseReference reference2;
    ValueEventListener profiListener;

    @Override
    protected void onDestroy() {
        reference2.removeEventListener(profiListener);
        if(personView!=null)
        personView.destroy();
        for(int i =0;i< workers.size();i++){
            workers.get(i).destroy();
        }
        super.onDestroy();
    }

    boolean somebodyHiered = false;
    String hiredProfiUid = "";
    int sentInvite = -1;
    boolean isProfi=false;
    boolean isOkProfi=false;
    String clientPhone = "";

    CustomRoundButton refuse, accept;

    void checkProfi(){
        FirebaseDatabase.getInstance().getReference().child("profiInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        isProfi=false;
                        isOkProfi=true;
                        System.out.println("okolokf "+"start");
                        if(dataSnapshot!=null){
                            System.out.println("okolokf "+" notNull "+dataSnapshot.getChildrenCount());
                            for(DataSnapshot  object : dataSnapshot.getChildren()){
                                if(object.getValue() instanceof Boolean){
                                    isProfi = object.getValue(Boolean.class);
                                }
                                if(object.getValue() instanceof List){
                                    HashMap<String, Object> map =  (HashMap<String, Object>)dataSnapshot.getValue();
                                    if(map.get("categories") instanceof  ArrayList){

                                    ArrayList<String> catList = ((ArrayList)map.get("categories"));
                                    for(int i=0;i<catList.size();i++){
                                        System.out.println(catList.get(i).split("-",3)[0]+" "+catList.get(i).split("-",3)[1]+" "+catList.get(i).split("-",3)[2]);
                                        String[] categs =   catList.get(i).split("-",3);
                                        boolean nowOk = true;
                                        if(categs[2].equals("#")){
                                            nowOk = nowOk&((category+"").equals(categs[0]));
                                            nowOk = nowOk&((subCategory+"").equals(categs[1]));
                                        }else{
                                            nowOk = nowOk&((category+"").equals(categs[0]));
                                            nowOk = nowOk&((subCategory+"").equals(categs[1]));
                                            nowOk = nowOk&((subSubCategory+"").equals(categs[2]));
                                        }
                                        if(i==0){isOkProfi = nowOk;}else {
                                            isOkProfi = isOkProfi | nowOk;
                                        }
                                    }
                                    }
                                }
                            }
                        }
                        System.out.println("okolok prof "+isProfi);
                        System.out.println("okolok okProfi "+isOkProfi);

                        layout.removeView(callBtn);
                        layout.addView(callBtn);

                        if(isOkProfi&isProfi){
                            if(sentInvite==-1) {
                                callBtn.btnText = strings[lang][12];
                                callBtn.p.setColor(ResourseColors.colorGreen);
                                callBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        reference2.child(FirebaseAuth.getInstance().getUid()).setValue(0);
                                    }
                                });
                            }else{
                                if(sentInvite==0) {
                                    callBtn.btnText = strings[lang][21];
                                    callBtn.p.setColor(ResourseColors.colorStroke);
                                    callBtn.removeView(callBtn.back);
                                    callBtn.setEnabled(false);
                                }else{
                                    if(sentInvite==1) {
                                        layout.removeView(callBtn);
                                        accept = new CustomRoundButton(TaskInfoActivity.this) {
                                            public void press() {
                                                reference.child("profies").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.child(FirebaseAuth.getInstance().getUid()).getValue(Long.class)==1L) {
                                                            for(DataSnapshot worker : dataSnapshot.getChildren()){
                                                                reference.child("profies").child(worker.getKey()).setValue(-3);
                                                            }
                                                            reference.child("profies").child(FirebaseAuth.getInstance().getUid()).setValue(2);
                                                            layout.removeView(refuse);
                                                            layout.removeView(accept);
                                                            layout.removeView(callBtn);
                                                            Intent intent = new Intent(TaskInfoActivity.this, WorkInProgressActivity.class);
                                                            intent.putExtra("lang", lang);
                                                            intent.putExtra("tab", 1);
                                                            startActivity(intent);
                                                            layout.addView(callBtn);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                        };
                                        accept.question = false;
                                        accept.setLayoutParams(new FrameLayout.LayoutParams((int) (444 * scale), (int) (119 * scale)));
                                        accept.setX(scale * (1080 / 2 + (1080 / 2 - 444) / 2));
                                        accept.setY(dm.heightPixels-200*scale);
                                        accept.blocked = false;
                                        accept.fixed = false;
                                        accept.setText(strings[lang][24],false);
                                        accept.setBackGround1(ResourseColors.colorBlue);
                                        accept.setBackGround2(ResourseColors.colorGreen);
                                        accept.setTextColor(ResourseColors.colorWhite);
                                        layout.addView(accept);

                                        refuse = new CustomRoundButton(TaskInfoActivity.this) {
                                            public void press() {
                                                reference.child("profies").child(FirebaseAuth.getInstance().getUid()).removeValue();
                                                layout.removeView(refuse);
                                                layout.removeView(accept);
                                                layout.removeView(callBtn);
                                                Intent intent = new Intent(TaskInfoActivity.this,WorkInProgressActivity.class);
                                                intent.putExtra("lang",lang);
                                                intent.putExtra("tab",0);
                                                startActivity(intent);
                                                layout.addView(callBtn);
                                            }
                                        };
                                        refuse.question = false;
                                        refuse.setLayoutParams(new FrameLayout.LayoutParams((int) (444 * scale), (int) (119 * scale)));
                                        refuse.setX(scale * (1080 / 2 - 444) / 2);
                                        refuse.setY(dm.heightPixels-200*scale);
                                        refuse.blocked = false;
                                        refuse.fixed = false;
                                        refuse.setText(strings[lang][25],false);
                                        refuse.setBackGround1(ResourseColors.colorRed);
                                        refuse.setBackGround2(ResourseColors.colorGray);
                                        refuse.setTextColor(ResourseColors.colorWhite);
                                        layout.addView(refuse);
                                    }else{
                                        if(sentInvite==2){
                                            layout.removeView(callBtn);
                                            callBtn.p.setColor(ResourseColors.colorGreen);
                                            callBtn.btnText = strings[lang][29];
                                            FirebaseDatabase.getInstance().getReference("usersInfo").child(taskUid).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    clientPhone = dataSnapshot.getValue(String.class);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            callBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(!clientPhone.equals("")) {
                                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + clientPhone));
                                                        if (ActivityCompat.checkSelfPermission(TaskInfoActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                            // TODO: Consider calling
                                                            //    ActivityCompat#requestPermissions
                                                            // here to request the missing permissions, and then overriding
                                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                            //                                          int[] grantResults)
                                                            // to handle the case where the user grants the permission. See the documentation
                                                            // for ActivityCompat#requestPermissions for more details.
                                                            ActivityCompat.requestPermissions(TaskInfoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 5);
                                                            return;
                                                        }
                                                        startActivity(intent);
                                                    }
                                                   /* RatingPopUp popup = new RatingPopUp(TaskInfoActivity.this,taskUid,false,nameTx.getText().toString(),id,taskPhone,lang);
                                                    layout.addView(popup);*/
                                                }
                                            });
                                            callBtn.invalidate();
                                            layout.addView(callBtn);
                                        }else{

                                            if(sentInvite==3){
                                                RatingPopUp popup = new RatingPopUp(TaskInfoActivity.this,taskUid,false,nameTx.getText().toString(),id,taskPhone,lang);
                                                layout.addView(popup);
                                            }

                                            if(sentInvite==4){
                                                layout.removeView(callBtn);
                                                TextView finish = new TextView(TaskInfoActivity.this);
                                                finish.setText(strings[lang][28]);
                                                finish.setTypeface(Typeface.createFromAsset(getAssets(),"font.ttf"));
                                                finish.setTextSize(TypedValue.COMPLEX_UNIT_PX,60*scale);
                                                finish.setTextColor(ResourseColors.colorGray);
                                                finish.measure(0,0);
                                                finish.setY(dm.heightPixels-200*scale);
                                                finish.setX(dm.widthPixels/2-finish.getMeasuredWidth()/2);
                                                layout.addView(finish);
                                            }
                                        }
                                    }
                                }
                            }
                            callBtn.invalidate();
                        }else{
                            if(personShown) {
                                callBtn.btnText = strings[lang][19];
                                callBtn.p.setColor(ResourseColors.colorGreen);
                                callBtn.invalidate();
                                callBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(TaskInfoActivity.this, TaskEditorActivity.class);
                                        intent.putExtra("lang", lang);
                                        intent.putExtra("category", category);
                                        intent.putExtra("subCategory", subCategory);
                                        intent.putExtra("subSubCategory", subSubCategory);
                                        intent.putExtra("name", nameTx.getText().toString());
                                        intent.putExtra("desc", descLoadTx.getText().toString());
                                        // intent.putExtra("addDesc", addDescLoadTx.getText().toString());
                                        startActivity(intent);
                                    }
                                });
                            }else{
                                layout.removeView(callBtn);
                            }

                        }

                        if(!personShown) {
                            if (somebodyHiered&!taskHalfComplitted) {
                                layout.removeView(callBtn);
                                callBtn.btnText = strings[lang][26];
                                callBtn.p.setColor(ResourseColors.colorBlue);
                                callBtn.invalidate();
                                callBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        RatingPopUp popup = new RatingPopUp(TaskInfoActivity.this, hiredProfiUid, true, nameTx.getText().toString(), id, taskPhone, lang);
                                        layout.addView(popup);
                                    }
                                });
                                layout.addView(callBtn);
                            }else{
                                if(taskHalfComplitted){
                                    layout.removeView(callBtn);
                                    callBtn.btnText = strings[lang][30];
                                    callBtn.p.setColor(ResourseColors.colorStroke);
                                    callBtn.removeView(callBtn.back);
                                    callBtn.invalidate();
                                    callBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                                    layout.addView(callBtn);
                                }
                                if(taskComplitted){
                                    layout.removeView(callBtn);
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }





}
