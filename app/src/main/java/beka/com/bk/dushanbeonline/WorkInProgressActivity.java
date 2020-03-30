package beka.com.bk.dushanbeonline;



import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import beka.com.bk.dushanbeonline.custom_views.CustomSwitch;
import beka.com.bk.dushanbeonline.custom_views.LoadCircle;
import beka.com.bk.dushanbeonline.custom_views.ShortTaskView;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;


public class WorkInProgressActivity extends Activity {

    FrameLayout layout;
    TopLabel label;
    ScrollView sclProgress, sclDeal, sclComplete;
    FrameLayout progLay, dealLay, compLay;
    LoadCircle loading;

    int lang = 1;
    String[][] strings, stringsDays;
    float scale = 1;
    boolean firstTimeUpload = true;

    CustomSwitch swt;

    ArrayList<ShortTaskView> tasksProgress = new ArrayList<>();
    ArrayList<ShortTaskView> tasksDeal = new ArrayList<>();
    ArrayList<ShortTaskView> tasksComplete = new ArrayList<>();
    DisplayMetrics dm;
    int tab = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tab = getIntent().getIntExtra("tab",0);

        layout = new FrameLayout(this);
        setContentView(layout);

        dm = getResources().getDisplayMetrics();
        scale = dm.widthPixels/1080f;
        lang = getIntent().getIntExtra("lang", 1);

        strings = new String[2][10];
        strings[0][0] = "Work in progress";
        strings[1][0] = "Работа в процессе";
        strings[0][1] = "Not confirmed";
        strings[1][1] = "Запрошено";
        strings[0][2] = "Hired";
        strings[1][2] = "Нанят";
        strings[0][3] = "Completed";
        strings[1][3] = "Завершено";

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

        design();

      /*  uploadProgressTasks();
        uploadDealTasks();
        uploadCompleteTasks();*/
    }


    private void design() {
        label = new TopLabel(this,strings[lang][0], scale);
        label.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layout.addView(label);

        String[] labels = new String[3];
        labels[0] = strings[lang][1];
        labels[1] = strings[lang][2];
        labels[2] = strings[lang][3];
        swt = new CustomSwitch(WorkInProgressActivity.this, labels){
            @Override
            public void onItemSelected(int g) {
                super.onItemSelected(g);
                if(g == 0){
                    openLeft();
                }else{
                    if(g == 1) {
                        openCenter();
                    }else {
                        openRight();
                    }
                }
            }
        };
        swt.setY(168*scale);
        layout.addView(swt);

        progLay = new FrameLayout(this);
        sclProgress = new ScrollView(this);
        sclProgress.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, dm.heightPixels - (int) (168 * scale)));
        sclProgress.setY(288 * scale);
        sclProgress.setX(0);
        sclProgress.addView(progLay);
        progLay.setMinimumHeight((int) (200 * 10 * scale));
        layout.addView(sclProgress);

        dealLay = new FrameLayout(this);
        sclDeal = new ScrollView(this);
        sclDeal.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, dm.heightPixels - (int) (168 * scale)));
        sclDeal.setY(288 * scale);
        sclDeal.setX(dm.widthPixels);
        sclDeal.addView(dealLay);
        dealLay.setMinimumHeight((int) (200 * 10 * scale));
        layout.addView(sclDeal);

        compLay = new FrameLayout(this);
        sclComplete = new ScrollView(this);
        sclComplete.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, dm.heightPixels - (int) (168 * scale)));
        sclComplete.setY(288 * scale);
        sclComplete.setX(dm.widthPixels);
        sclComplete.addView(compLay);
        compLay.setMinimumHeight((int) (200 * 10 * scale));
        layout.addView(sclComplete);

        loading = new LoadCircle(this);
        loading.setX(1080/2*scale-loading.bmp.getWidth()/2);
        loading.setY(400*scale);
        layout.addView(loading);

        if(tab >= 0){
            firstTimeUpload = false;
            swt.select(tab);
        }

    }

    ValueAnimator mooveAnimator;

    public void openLeft(){
        if(mooveAnimator!=null)
            mooveAnimator.cancel();
        mooveAnimator = new ValueAnimator().setDuration(300);
        mooveAnimator.setFloatValues(sclProgress.getX(),0);
        mooveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sclProgress.setX((float)animation.getAnimatedValue());
                sclDeal.setX((float)animation.getAnimatedValue()+dm.widthPixels);
                sclComplete.setX((float)animation.getAnimatedValue()+dm.widthPixels*2);
            }
        });
        mooveAnimator.start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainMenuActivity.class);
        intent.putExtra("lang",lang);
        startActivity(intent);
    }

    public void openCenter(){
        if(mooveAnimator!=null)
            mooveAnimator.cancel();
        mooveAnimator = new ValueAnimator().setDuration(300);
        mooveAnimator.setFloatValues(sclProgress.getX(),-dm.widthPixels);
        mooveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sclProgress.setX((float)animation.getAnimatedValue());
                sclDeal.setX((float)animation.getAnimatedValue()+dm.widthPixels);
                sclComplete.setX((float)animation.getAnimatedValue()+dm.widthPixels*2);
            }
        });
        mooveAnimator.start();
    }

    public void openRight(){
        if(mooveAnimator!=null)
            mooveAnimator.cancel();
        mooveAnimator = new ValueAnimator().setDuration(300);
        mooveAnimator.setFloatValues(sclProgress.getX(),-dm.widthPixels*2);
        mooveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sclProgress.setX((float)animation.getAnimatedValue());
                sclDeal.setX((float)animation.getAnimatedValue()+dm.widthPixels);
                sclComplete.setX((float)animation.getAnimatedValue()+dm.widthPixels*2);
            }
        });
        mooveAnimator.start();
    }

    public void uploadProgressTasks(){

        for (int i = 0; i < tasksProgress.size(); i++) {
            progLay.removeView(tasksProgress.get(i));
        }
        tasksProgress.clear();


        FirebaseDatabase.getInstance().getReference().child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Date nowDate = Calendar.getInstance().getTime();
                int nowHour = nowDate.getHours();
                int nowMinute = nowDate.getMinutes();
                int nowYear = Calendar.getInstance().get(Calendar.YEAR);
                int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
                nowDate.setYear(nowYear-1900);
                nowDate.setDate(nowDay);
                nowDate.setMonth(nowMonth);
                nowDate.setHours(nowHour);
                nowDate.setMinutes(nowMinute);
                if(dataSnapshot!=null){
                    Map<Object,Object> tasks2 = ((Map<Object,Object>)dataSnapshot.getValue());
                    for (final Map.Entry<Object, Object> entry : tasks2.entrySet())
                    {
                        if(!(entry.getValue() instanceof String)) {
                                for (final Map.Entry<Object, Object> entry2 : ((Map<Object, Object>) (entry.getValue())).entrySet()) {
                                    if (!(entry2.getValue() instanceof Boolean)) {

                                            System.out.println("messageInfo ok");
                                            Long Y = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateY"));
                                            Long M = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateM"));
                                            Long D = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateD"));
                                            Long H = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateH"));
                                            Long min = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateMin"));

                                            Date start = new Date();
                                            start.setYear(Y.intValue() - 1900);
                                            start.setMonth(M.intValue());
                                            start.setDate(D.intValue());
                                            start.setHours(H.intValue());
                                            start.setMinutes(min.intValue());

                                            Date start2 = new Date();
                                            start2.setYear(Y.intValue() - 1900);
                                            start2.setMonth(M.intValue());
                                            start2.setDate(D.intValue());

                                           // System.out.println("abab "+entry2+" "+checkInviteProgress(entry2));

                                            if (checkInviteProgress(entry2)) {
                                                    Long categ = (Long) (((Map<Object, Object>) entry2.getValue()).get("categoryId"));
                                                    String name = (String) (((Map<Object, Object>) entry2.getValue()).get("name"));
                                                    String dateTime = (String) (((Map<Object, Object>) entry2.getValue()).get("dateTime"));
                                                    String typeText = (String) (((Map<Object, Object>) entry2.getValue()).get("budget"));

                                                    Long Y1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateY"));
                                                    Long M1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateM"));
                                                    Long D1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateD"));
                                                    Long H1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateH"));
                                                    Long min1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateMin"));

                                                    Long price = (Long) (((Map<Object, Object>) entry2.getValue()).get("priceVal"));

                                                    Date pub = new Date();
                                                    pub.setYear(Y1.intValue());
                                                    pub.setMonth(M1.intValue());
                                                    pub.setDate(D1.intValue());
                                                    if (H1 != null) {
                                                        pub.setHours(H1.intValue());
                                                    } else {
                                                        pub.setHours(0);
                                                    }
                                                    if (min1 != null) {
                                                        pub.setMinutes(min1.intValue());
                                                    } else {
                                                        pub.setMinutes(0);
                                                    }

                                                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                                                    String dat = stringsDays[lang][start.getDay()] + "  " + (format.format(start));

                                                    ShortTaskView taskView = new ShortTaskView(WorkInProgressActivity.this, lang, scale, (String) entry2.getKey(), name, dat, price, typeText, categ, pub);
                                                    System.out.println("olok " + tasksProgress.size() + " " + entry2.getKey());
                                                    taskView.setY(tasksProgress.size() * 200 * scale);
                                                    taskView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            FirebaseDatabase.getInstance().getReference().child("usersInfo").child((String) (((Map<Object, Object>) entry2.getValue()).get("userId"))).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    Intent intent = new Intent(WorkInProgressActivity.this, TaskInfoActivity.class);
                                                                    intent.putExtra("lang", lang);
                                                                    intent.putExtra("id", (String) entry2.getKey());
                                                                    intent.putExtra("phone", dataSnapshot.getValue(String.class).substring(4));
                                                                    intent.putExtra("uid", (String) (((Map<Object, Object>) entry2.getValue()).get("userId")));
                                                                    startActivity(intent);
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    });
                                                    taskView.setBackgroundColor(ResourseColors.colorWhite);
                                                progLay.setBackgroundColor(ResourseColors.colorWhite);
                                                progLay.addView(taskView);
                                                tasksProgress.add(taskView);

                                            }

                                    }
                                }

                            Collections.sort(tasksProgress, new Comparator<ShortTaskView>() {
                                @Override
                                public int compare(ShortTaskView o1, ShortTaskView o2) {
                                    return o2.publication.compareTo(o1.publication);
                                }
                            });

                            for(int i=0;i<tasksProgress.size();i++){
                                tasksProgress.get(i).setY(i * 200 * scale);
                            }

                        }
                    }loading.stop();
                }
                progLay.setMinimumHeight((int) (200 * tasksProgress.size() * scale));
                progAmount = tasksProgress.size();
                if(dealAmount>=0&progAmount>=0&firstTimeUpload){
                    if(dealAmount>progAmount){
                        swt.select(1);
                        firstTimeUpload = false;
                    }
                    if(dealAmount<progAmount){
                        swt.select(0);
                        firstTimeUpload = false;
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    int progAmount=-1, dealAmount=-1, complAmount=-1;

    public void uploadDealTasks(){

        for (int i = 0; i < tasksDeal.size(); i++) {
            dealLay.removeView(tasksDeal.get(i));
        }
        tasksDeal.clear();


        FirebaseDatabase.getInstance().getReference().child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Date nowDate = Calendar.getInstance().getTime();
                int nowHour = nowDate.getHours();
                int nowMinute = nowDate.getMinutes();
                int nowYear = Calendar.getInstance().get(Calendar.YEAR);
                int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
                nowDate.setYear(nowYear-1900);
                nowDate.setDate(nowDay);
                nowDate.setMonth(nowMonth);
                nowDate.setHours(nowHour);
                nowDate.setMinutes(nowMinute);
                if(dataSnapshot!=null){
                    Map<Object,Object> tasks2 = ((Map<Object,Object>)dataSnapshot.getValue());
                    for (final Map.Entry<Object, Object> entry : tasks2.entrySet())
                    {
                        if(!(entry.getValue() instanceof String)) {
                                for (final Map.Entry<Object, Object> entry2 : ((Map<Object, Object>) (entry.getValue())).entrySet()) {
                                    if (!(entry2.getValue() instanceof Boolean)) {

                                        System.out.println("messageInfo ok");
                                        Long Y = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateY"));
                                        Long M = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateM"));
                                        Long D = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateD"));
                                        Long H = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateH"));
                                        Long min = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateMin"));

                                        Date start = new Date();
                                        start.setYear(Y.intValue() - 1900);
                                        start.setMonth(M.intValue());
                                        start.setDate(D.intValue());
                                        start.setHours(H.intValue());
                                        start.setMinutes(min.intValue());

                                        Date start2 = new Date();
                                        start2.setYear(Y.intValue() - 1900);
                                        start2.setMonth(M.intValue());
                                        start2.setDate(D.intValue());


                                        if (checkInviteDeal(entry2)) {
                                            Long categ = (Long) (((Map<Object, Object>) entry2.getValue()).get("categoryId"));
                                            String name = (String) (((Map<Object, Object>) entry2.getValue()).get("name"));
                                            String dateTime = (String) (((Map<Object, Object>) entry2.getValue()).get("dateTime"));
                                            String typeText = (String) (((Map<Object, Object>) entry2.getValue()).get("budget"));

                                            Long Y1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateY"));
                                            Long M1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateM"));
                                            Long D1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateD"));
                                            Long H1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateH"));
                                            Long min1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateMin"));

                                            Long price = (Long) (((Map<Object, Object>) entry2.getValue()).get("priceVal"));

                                            Date pub = new Date();
                                            pub.setYear(Y1.intValue());
                                            pub.setMonth(M1.intValue());
                                            pub.setDate(D1.intValue());
                                            if (H1 != null) {
                                                pub.setHours(H1.intValue());
                                            } else {
                                                pub.setHours(0);
                                            }
                                            if (min1 != null) {
                                                pub.setMinutes(min1.intValue());
                                            } else {
                                                pub.setMinutes(0);
                                            }

                                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                                            String dat = stringsDays[lang][start.getDay()] + "  " + (format.format(start));

                                            ShortTaskView taskView = new ShortTaskView(WorkInProgressActivity.this, lang, scale, (String) entry2.getKey(), name, dat, price, typeText, categ, pub);
                                            taskView.setY(tasksDeal.size() * 200 * scale);
                                            taskView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    FirebaseDatabase.getInstance().getReference().child("usersInfo").child((String) (((Map<Object, Object>) entry2.getValue()).get("userId"))).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Intent intent = new Intent(WorkInProgressActivity.this, TaskInfoActivity.class);
                                                            intent.putExtra("lang", lang);
                                                            intent.putExtra("id", (String) entry2.getKey());
                                                            intent.putExtra("phone", dataSnapshot.getValue(String.class).substring(4));
                                                            intent.putExtra("uid", (String) (((Map<Object, Object>) entry2.getValue()).get("userId")));
                                                            startActivity(intent);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            });
                                            taskView.setBackgroundColor(ResourseColors.colorWhite);
                                            dealLay.setBackgroundColor(ResourseColors.colorWhite);
                                            dealLay.addView(taskView);
                                            tasksDeal.add(taskView);

                                        }

                                    }
                            }

                            Collections.sort(tasksDeal, new Comparator<ShortTaskView>() {
                                @Override
                                public int compare(ShortTaskView o1, ShortTaskView o2) {
                                    return o2.publication.compareTo(o1.publication);
                                }
                            });

                            for(int i=0;i<tasksDeal.size();i++){
                                tasksDeal.get(i).setY(i * 200 * scale);
                            }

                        }
                    }loading.stop();
                }
                dealLay.setMinimumHeight((int) (200 * tasksDeal.size() * scale));
                dealAmount = tasksDeal.size();
                if(dealAmount>=0&progAmount>=0&firstTimeUpload){
                    if(dealAmount>progAmount){
                        swt.select(1);
                        firstTimeUpload = false;
                    }
                    if(dealAmount<progAmount){
                        swt.select(0);
                        firstTimeUpload = false;
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void uploadCompleteTasks(){

        for (int i = 0; i < tasksComplete.size(); i++) {
            compLay.removeView(tasksComplete.get(i));
        }
        tasksComplete.clear();


        FirebaseDatabase.getInstance().getReference().child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Date nowDate = Calendar.getInstance().getTime();
                int nowHour = nowDate.getHours();
                int nowMinute = nowDate.getMinutes();
                int nowYear = Calendar.getInstance().get(Calendar.YEAR);
                int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
                nowDate.setYear(nowYear-1900);
                nowDate.setDate(nowDay);
                nowDate.setMonth(nowMonth);
                nowDate.setHours(nowHour);
                nowDate.setMinutes(nowMinute);
                if(dataSnapshot!=null){
                    Map<Object,Object> tasks2 = ((Map<Object,Object>)dataSnapshot.getValue());
                    for (final Map.Entry<Object, Object> entry : tasks2.entrySet())
                    {
                        if(!(entry.getValue() instanceof String)) {
                            Boolean visible = true;
                                for (final Map.Entry<Object, Object> entry2 : ((Map<Object, Object>) (entry.getValue())).entrySet()) {
                                    if (!(entry2.getValue() instanceof Boolean)) {

                                        System.out.println("messageInfo ok");
                                        Long Y = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateY"));
                                        Long M = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateM"));
                                        Long D = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateD"));
                                        Long H = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateH"));
                                        Long min = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateMin"));

                                        Date start = new Date();
                                        start.setYear(Y.intValue() - 1900);
                                        start.setMonth(M.intValue());
                                        start.setDate(D.intValue());
                                        start.setHours(H.intValue());
                                        start.setMinutes(min.intValue());

                                        Date start2 = new Date();
                                        start2.setYear(Y.intValue() - 1900);
                                        start2.setMonth(M.intValue());
                                        start2.setDate(D.intValue());


                                        if (checkInviteComplete(entry2)) {
                                            Long categ = (Long) (((Map<Object, Object>) entry2.getValue()).get("categoryId"));
                                            String name = (String) (((Map<Object, Object>) entry2.getValue()).get("name"));
                                            String dateTime = (String) (((Map<Object, Object>) entry2.getValue()).get("dateTime"));
                                            String typeText = (String) (((Map<Object, Object>) entry2.getValue()).get("budget"));

                                            Long Y1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateY"));
                                            Long M1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateM"));
                                            Long D1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateD"));
                                            Long H1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateH"));
                                            Long min1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateMin"));

                                            Long price = (Long) (((Map<Object, Object>) entry2.getValue()).get("priceVal"));

                                            Date pub = new Date();
                                            pub.setYear(Y1.intValue());
                                            pub.setMonth(M1.intValue());
                                            pub.setDate(D1.intValue());
                                            if (H1 != null) {
                                                pub.setHours(H1.intValue());
                                            } else {
                                                pub.setHours(0);
                                            }
                                            if (min1 != null) {
                                                pub.setMinutes(min1.intValue());
                                            } else {
                                                pub.setMinutes(0);
                                            }

                                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                                            String dat = stringsDays[lang][start.getDay()] + "  " + (format.format(start));

                                            ShortTaskView taskView = new ShortTaskView(WorkInProgressActivity.this, lang, scale, (String) entry2.getKey(), name, dat, price, typeText, categ, pub);
                                            System.out.println("olok " + tasksComplete.size() + " " + entry2.getKey());
                                            taskView.setY(tasksComplete.size() * 200 * scale);
                                            taskView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    FirebaseDatabase.getInstance().getReference().child("usersInfo").child((String) (((Map<Object, Object>) entry2.getValue()).get("userId"))).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Intent intent = new Intent(WorkInProgressActivity.this, TaskInfoActivity.class);
                                                            intent.putExtra("lang", lang);
                                                            intent.putExtra("id", (String) entry2.getKey());
                                                            intent.putExtra("phone", dataSnapshot.getValue(String.class).substring(4));
                                                            intent.putExtra("uid", (String) (((Map<Object, Object>) entry2.getValue()).get("userId")));
                                                            startActivity(intent);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            });
                                            taskView.setBackgroundColor(ResourseColors.colorWhite);
                                            compLay.setBackgroundColor(ResourseColors.colorWhite);
                                            compLay.addView(taskView);
                                            tasksComplete.add(taskView);
                                        }
                                    }
                                }


                            Collections.sort(tasksComplete, new Comparator<ShortTaskView>() {
                                @Override
                                public int compare(ShortTaskView o1, ShortTaskView o2) {
                                    return o2.publication.compareTo(o1.publication);
                                }
                            });

                            for(int i=0;i<tasksComplete.size();i++){
                                tasksComplete.get(i).setY(i * 200 * scale);
                            }
                        }
                    }loading.stop();
                }
                compLay.setMinimumHeight((int) (200 * tasksComplete.size() * scale));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkInviteProgress(Map.Entry<Object, Object> entry2) {
        HashMap<String,Long> profies = (HashMap<String,Long>)(((Map<Object, Object>) entry2.getValue()).get("profies"));
        if(profies!=null) {
            if(profies.get(FirebaseAuth.getInstance().getUid())!=null) {
                boolean isHiered = profies.containsValue(2);
                Long invitedStage = profies.get(FirebaseAuth.getInstance().getUid());
                boolean show = (!isHiered & invitedStage == 0)|(invitedStage == 1);
                System.out.println("abab" + profies);
                System.out.println("abab" + show);
                return show;
            }else{
                System.out.println("abab "+entry2+" progress no uid");
                return false;
            }
        }else {
            System.out.println("abab "+entry2+" progress no profies");
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadProgressTasks();
        uploadDealTasks();
        uploadCompleteTasks();
    }

    private boolean checkInviteDeal(Map.Entry<Object, Object> entry2) {
        HashMap<String,Long> profies = (HashMap<String,Long>)(((Map<Object, Object>) entry2.getValue()).get("profies"));
        if(profies!=null) {
            if(profies.get(FirebaseAuth.getInstance().getUid())!=null) {
                Long invitedStage = profies.get(FirebaseAuth.getInstance().getUid());
                boolean show = (invitedStage==2);
                return show;
            }
        }
        return false;
    }

    private boolean checkInviteComplete(Map.Entry<Object, Object> entry2) {
        HashMap<String,Long> profies = (HashMap<String,Long>)(((Map<Object, Object>) entry2.getValue()).get("profies"));
        if(profies!=null) {
            if(profies.get(FirebaseAuth.getInstance().getUid())!=null) {
                Long invitedStage = profies.get(FirebaseAuth.getInstance().getUid());
                boolean show = (invitedStage==4)|(invitedStage==3);
                return show;
            }
        }
        return false;
    }

}

