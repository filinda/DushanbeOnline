package beka.com.bk.dushanbeonline;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import beka.com.bk.dushanbeonline.custom_views.LoadCircle;
import beka.com.bk.dushanbeonline.custom_views.ShortTaskView;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;

public class MyTasksActivity extends AppCompatActivity {

    FrameLayout layout;
    ScrollView scl;
    TopLabel topLabel;
    public int lang = 1;
    ArrayList<ShortTaskView> tasks, naTasks;
    ArrayList<ShortTaskView> waitingTasks = new ArrayList<>(), chooseTasks = new ArrayList<>(), progressTasks = new ArrayList<>(), finishedTasks = new ArrayList<>(), notActiveTasks = new ArrayList<>();
    TextView waiting, choose, progress, finish, notActive;
    float  waitingY, chooseY, progressY, finishY, notActiveY;
    float scale;
    String[][] strings;
    DisplayMetrics dm;
    FrameLayout sclLayout;
    int deleteTask = -1;
    TextView noAct;
    LoadCircle loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        layout = new FrameLayout(this);
        setContentView(layout);
        lang = getIntent().getIntExtra("lang", 1);

        strings = new String[2][12];
        strings[0][0] = "My tasks";
        strings[1][0] = "Мои задания";
        strings[0][1] = "No active tasks";
        strings[1][1] = "Ваши неактивные задания";
        strings[0][2] = "Are you sure?";
        strings[1][2] = "Вы уверенны?";
        strings[0][3] = "Yes";
        strings[1][3] = "Да";
        strings[0][4] = "No";
        strings[1][4] = "Нет";
        strings[0][5] = "Remove task";
        strings[1][5] = "Удалить задание";
        strings[0][6] = "Task removed";
        strings[1][6] = "Задание удалено";
        strings[0][7] = "Waiting for performer";
        strings[1][7] = "В ожидании исполнителя";
        strings[0][8] = "Choose a performer";
        strings[1][8] = "Выбор претендента";
        strings[0][9] = "Performer is hired";
        strings[1][9] = "Нанят исполнитель";
        strings[0][10] = "Tasks finishedTasks";
        strings[1][10] = "Завершённые задания";
        strings[0][11] = "Tasks out of date";
        strings[1][11] = "Неактивные задания";


        tasks = new ArrayList<>();
        naTasks = new ArrayList<>();
        design();
        sclLayout.setBackgroundColor(ResourseColors.colorWhite);
        loading.start();
        getTasks();

    }

    DatabaseReference mDatabase;
    Date nowDate;

    private void getTasks() {
        sclLayout.setBackgroundColor(ResourseColors.colorWhite);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(tasks!=null){
            for(ShortTaskView task:tasks){
                sclLayout.removeView(task);
            }
        }
        tasks.clear();

        if(naTasks!=null){
            for(ShortTaskView task:naTasks){
                sclLayout.removeView(task);
            }
        }
        naTasks.clear();

        FirebaseDatabase.getInstance().getReference().child("tasks").child(""+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                nowDate = Calendar.getInstance().getTime();
                int nowHour = nowDate.getHours();
                int nowMinute = nowDate.getMinutes();
                int nowYear = Calendar.getInstance().get(Calendar.YEAR);
                int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
                nowDate.setYear(nowYear);
                nowDate.setDate(nowDay);
                nowDate.setMonth(nowMonth);
                nowDate.setHours(nowHour);
                nowDate.setMinutes(nowMinute);

                if(dataSnapshot!=null){
                    Map<Object,Object> tasks2 = ((Map<Object,Object>)dataSnapshot.getValue());
                    if(tasks2!=null){
                        for (final Map.Entry<Object, Object> entry : tasks2.entrySet())
                        {
                                if (!(entry.getValue() instanceof Boolean)){
                                    Long categ = (Long) (((Map<Object, Object>) entry.getValue()).get("categoryId"));
                                    String name = (String)(((Map<Object, Object>) entry.getValue()).get("name"));
                                    String dateTime = (String)(((Map<Object, Object>) entry.getValue()).get("dateTime"));
                                    Long price = (Long) (((Map<Object, Object>) entry.getValue()).get("priceVal"));
                                    String typeText = (String)(((Map<Object, Object>) entry.getValue()).get("budget"));

                                    Long Y = (Long) (((Map<Object, Object>) entry.getValue()).get("startDateY"));
                                    Long M = (Long) (((Map<Object, Object>) entry.getValue()).get("startDateM"));
                                    Long D = (Long) (((Map<Object, Object>) entry.getValue()).get("startDateD"));
                                    Long H = (Long) (((Map<Object, Object>) entry.getValue()).get("startDateH"));
                                    Long min = (Long) (((Map<Object, Object>) entry.getValue()).get("startDateMin"));

                                    Date start = new Date();
                                    start.setYear(Y.intValue());
                                    start.setMonth(M.intValue());
                                    start.setDate(D.intValue());
                                    start.setHours(H.intValue());
                                    start.setMinutes(min.intValue());

                                    Long Y1 = (Long) (((Map<Object, Object>) entry.getValue()).get("publicDateY"));
                                    Long M1 = (Long) (((Map<Object, Object>) entry.getValue()).get("publicDateM"));
                                    Long D1 = (Long) (((Map<Object, Object>) entry.getValue()).get("publicDateD"));
                                    Long H1 = (Long) (((Map<Object, Object>) entry.getValue()).get("publicDateH"));
                                    Long min1 = (Long) (((Map<Object, Object>) entry.getValue()).get("publicDateMin"));

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
                                    HashMap<String,Long> profies = (HashMap<String,Long>)(((Map<Object, Object>) entry.getValue()).get("profies"));
                                    ShortTaskView taskView = new ShortTaskView(MyTasksActivity.this, scale, (String) entry.getKey(), name, dateTime, price, typeText, categ, pub, start, true, profies){
                                        @Override
                                        public void onDelete(final ShortTaskView shortTaskView) {
                                            //deleting
                                            deleteTask = tasks.indexOf(shortTaskView);
                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which){
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            //Yes button clicked
                                                            FirebaseDatabase.getInstance().getReference().child("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                    public void onComplete(@NonNull final Task<Void> task) {
                                                                    Toast.makeText(MyTasksActivity.this,strings[lang][6],Toast.LENGTH_LONG).show();
                                                                    ValueAnimator upMoove = new ValueAnimator();
                                                                    upMoove.setFloatValues(0,-200*scale);
                                                                    upMoove.setDuration(400);
                                                                    if(naTasks.size()==0&tasks.size()==1){
                                                                        sclLayout.setBackgroundColor(ResourseColors.colorWhite);
                                                                    }else{
                                                                        sclLayout.setBackgroundColor(ResourseColors.colorIconGray);
                                                                    }
                                                                    upMoove.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                                        @Override
                                                                        public void onAnimationUpdate(ValueAnimator animation) {
                                                                            tasks.get(deleteTask).setAlpha(1+((Float)animation.getAnimatedValue()/ 1));
                                                                            for(int i = deleteTask;i<tasks.size();i++) {
                                                                                tasks.get(i).setMooveY(tasks.get(i).mY + (Float) animation.getAnimatedValue());
                                                                            }
                                                                            if(waitingTasks.contains(tasks.get(deleteTask))){
                                                                                choose.setY(chooseY+(Float) animation.getAnimatedValue());
                                                                                progress.setY(progressY+(Float) animation.getAnimatedValue());
                                                                                finish.setY(finishY+(Float) animation.getAnimatedValue());
                                                                                notActive.setY(notActiveY+(Float) animation.getAnimatedValue());
                                                                            }
                                                                            if(chooseTasks.contains(tasks.get(deleteTask))){
                                                                                progress.setY(progressY+(Float) animation.getAnimatedValue());
                                                                                finish.setY(finishY+(Float) animation.getAnimatedValue());
                                                                                notActive.setY(notActiveY+(Float) animation.getAnimatedValue());
                                                                            }
                                                                            if(progressTasks.contains(tasks.get(deleteTask))){
                                                                                finish.setY(finishY+(Float) animation.getAnimatedValue());
                                                                                notActive.setY(notActiveY+(Float) animation.getAnimatedValue());
                                                                            }
                                                                            if(finishedTasks.contains(tasks.get(deleteTask))){
                                                                                notActive.setY(notActiveY+(Float) animation.getAnimatedValue());
                                                                            }
                                                                            sclLayout.setMinimumHeight((int)(tasks.get(tasks.size()-1).getY()+200*scale));

                                                                        }
                                                                    });
                                                                    upMoove.addListener(new Animator.AnimatorListener() {
                                                                        @Override
                                                                        public void onAnimationStart(Animator animation) {

                                                                        }

                                                                        @Override
                                                                        public void onAnimationEnd(Animator animation) {
                                                                            sclLayout.removeView(tasks.get(deleteTask));
                                                                            if(waitingTasks.contains(tasks.get(deleteTask))){
                                                                                waitingTasks.remove(tasks.get(deleteTask));
                                                                                if(waitingTasks.size()==0){
                                                                                    sclLayout.removeView(waiting);
                                                                                    for(int i = 0; i<tasks.size();i++){
                                                                                        tasks.get(i).setY(tasks.get(i).getY()-waiting.getHeight());
                                                                                    }
                                                                                    choose.setY(choose.getY()-waiting.getHeight());
                                                                                    progress.setY(progress.getY()-waiting.getHeight());
                                                                                    finish.setY(finish.getY()-waiting.getHeight());
                                                                                    notActive.setY(notActive.getY()-waiting.getHeight());
                                                                                }
                                                                            }
                                                                            if(chooseTasks.contains(tasks.get(deleteTask))){
                                                                                chooseTasks.remove(tasks.get(deleteTask));
                                                                                if(chooseTasks.size()==0){
                                                                                    sclLayout.removeView(choose);
                                                                                    for(int i = deleteTask+1; i<tasks.size();i++){
                                                                                        tasks.get(i).setY(tasks.get(i).getY()-choose.getHeight());
                                                                                    }
                                                                                    progress.setY(progress.getY()-choose.getHeight());
                                                                                    finish.setY(finish.getY()-choose.getHeight());
                                                                                    notActive.setY(notActive.getY()-choose.getHeight());
                                                                                }
                                                                            }
                                                                            if(progressTasks.contains(tasks.get(deleteTask))){
                                                                                progressTasks.remove(tasks.get(deleteTask));
                                                                                if(progressTasks.size()==0){
                                                                                    sclLayout.removeView(progress);
                                                                                    for(int i = deleteTask+1; i<tasks.size();i++){
                                                                                        tasks.get(i).setY(tasks.get(i).getY()-progress.getHeight());
                                                                                    }
                                                                                    finish.setY(finish.getY()-progress.getHeight());
                                                                                    notActive.setY(notActive.getY()-progress.getHeight());
                                                                                }
                                                                            }
                                                                            if(finishedTasks.contains(tasks.get(deleteTask))){
                                                                                finishedTasks.remove(tasks.get(deleteTask));
                                                                                if(finishedTasks.size()==0){
                                                                                    sclLayout.removeView(finish);
                                                                                    for(int i = deleteTask+1; i<tasks.size();i++){
                                                                                        tasks.get(i).setY(tasks.get(i).getY()-finish.getHeight());
                                                                                    }
                                                                                    notActive.setY(notActive.getY()-finish.getHeight());
                                                                                }
                                                                            }
                                                                            if(notActiveTasks.contains(tasks.get(deleteTask))){
                                                                                notActiveTasks.remove(tasks.get(deleteTask));
                                                                                if(notActiveTasks.size()==0){
                                                                                    sclLayout.removeView(notActive);
                                                                                }
                                                                            }

                                                                            chooseY = choose.getY();
                                                                            progressY = progress.getY();
                                                                            finishY = finish.getY();
                                                                            notActiveY = notActive.getY();
                                                                            tasks.remove(tasks.get(deleteTask));
                                                                            for(int i=0; i< tasks.size();i++){
                                                                                tasks.get(i).setY(tasks.get(i).getY());
                                                                            }
                                                                            sclLayout.setMinimumHeight((int)(tasks.get(tasks.size()-1).getY()+200*scale));
                                                                        }

                                                                        @Override
                                                                        public void onAnimationCancel(Animator animation) {

                                                                        }

                                                                        @Override
                                                                        public void onAnimationRepeat(Animator animation) {

                                                                        }
                                                                    });
                                                                upMoove.start();
                                                                }
                                                        });
                                                        break;

                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            //No button clicked
                                                            deleteTask = -1;
                                                            break;
                                                    }
                                                }
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(MyTasksActivity.this);
                                            builder.setMessage(strings[lang][2]).setPositiveButton(strings[lang][3], dialogClickListener)
                                                    .setNegativeButton(strings[lang][4], dialogClickListener).show();
                                    }
                                };
                                taskView.setY((tasks.size()) * 200 * scale);
                                taskView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MyTasksActivity.this, TaskInfoActivity.class);
                                        intent.putExtra("lang", lang);
                                        intent.putExtra("id", ((ShortTaskView) v).id);
                                        intent.putExtra("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4));
                                        intent.putExtra("uid", (String) (((Map<Object, Object>) entry.getValue()).get("userId")));
                                        startActivity(intent);
                                    }
                                });
                                //sclLayout.addView(taskView);
                                tasks.add(taskView);
                                }
                        }loading.stop();

                        rightPlace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void rightPlace(){

        for(int i=0;i<tasks.size();i++){
            if(checkFinishedTask(tasks.get(i).profies)){
                finishedTasks.add(tasks.get(i));
                tasks.get(i).setActive(false);
                tasks.get(i).allouDelete = true;
            }else{
                if(checkFailDateTask(tasks.get(i).startDate)){
                    notActiveTasks.add(tasks.get(i));
                    tasks.get(i).setActive(false);
                    tasks.get(i).allouDelete = true;
                }else {
                    if (checkWaitPerformer(tasks.get(i).profies)) {
                        waitingTasks.add(tasks.get(i));
                        tasks.get(i).allouDelete = true;
                    } else {
                        if (checkChoosePerformer(tasks.get(i).profies)) {
                            chooseTasks.add(tasks.get(i));
                            tasks.get(i).allouDelete = true;
                        } else {
                            if (checkHiredPerformer(tasks.get(i).profies)) {
                                progressTasks.add(tasks.get(i));
                            }
                        }
                    }
                }
            }
        }

        System.out.println("abab wait:"+waitingTasks.size()+" choose:"+chooseTasks.size()+" progress:"+progressTasks.size()+" finish:"+finishedTasks.size());

        Collections.sort(waitingTasks, new Comparator<ShortTaskView>() {
            @Override
            public int compare(ShortTaskView o1, ShortTaskView o2) {
                return o2.publication.compareTo(o1.publication);
            }
        });

        Collections.sort(chooseTasks, new Comparator<ShortTaskView>() {
            @Override
            public int compare(ShortTaskView o1, ShortTaskView o2) {
                return o2.publication.compareTo(o1.publication);
            }
        });

        Collections.sort(progressTasks, new Comparator<ShortTaskView>() {
            @Override
            public int compare(ShortTaskView o1, ShortTaskView o2) {
                return o2.publication.compareTo(o1.publication);
            }
        });

        Collections.sort(finishedTasks, new Comparator<ShortTaskView>() {
            @Override
            public int compare(ShortTaskView o1, ShortTaskView o2) {
                return o2.publication.compareTo(o1.publication);
            }
        });

        Collections.sort(notActiveTasks, new Comparator<ShortTaskView>() {
            @Override
            public int compare(ShortTaskView o1, ShortTaskView o2) {
                return o2.publication.compareTo(o1.publication);
            }
        });

        tasks.clear();

        int nowHeight = 0;

        waiting = new TextView(this);
        waiting.setY(0);
        waitingY = 0;
        waiting.setText(strings[lang][7]);
        waiting.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, (int) (120 * scale)));
        waiting.setBackgroundColor(ResourseColors.colorGreen);
        waiting.setTextColor(ResourseColors.colorWhite);
        waiting.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        waiting.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
        waiting.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50 * scale);
        waiting.setPadding(0, (int) (20 * scale), 0, (int) (20 * scale));
        if(waitingTasks.size()>0) {
            sclLayout.addView(waiting);
            nowHeight += (int) (120 * scale);
        }

        for(int i=0;i<waitingTasks.size();i++){
            waitingTasks.get(i).setY(nowHeight);
            tasks.add(waitingTasks.get(i));
            sclLayout.addView(waitingTasks.get(i));
            nowHeight += 200 * scale;
        }

        choose = new TextView(this);
        choose.setY(nowHeight);
        chooseY = nowHeight;
        choose.setText(strings[lang][8]);
        choose.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, (int) (120 * scale)));
        choose.setBackgroundColor(ResourseColors.colorBlue);
        choose.setTextColor(ResourseColors.colorWhite);
        choose.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        choose.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
        choose.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50 * scale);
        choose.setPadding(0, (int) (20 * scale), 0, (int) (20 * scale));
        if(chooseTasks.size()>0) {
            sclLayout.addView(choose);
            nowHeight += (int) (120 * scale);
        }

        for(int i=0;i<chooseTasks.size();i++){
            chooseTasks.get(i).setY(nowHeight);
            tasks.add(chooseTasks.get(i));
            sclLayout.addView(chooseTasks.get(i));
            nowHeight += 200 * scale;
        }

        progress = new TextView(this);
        progress.setY(nowHeight);
        progressY = nowHeight;
        progress.setText(strings[lang][9]);
        progress.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, (int) (120 * scale)));
        progress.setBackgroundColor(ResourseColors.colorYellow);
        progress.setTextColor(ResourseColors.colorWhite);
        progress.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        progress.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
        progress.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50 * scale);
        progress.setPadding(0, (int) (20 * scale), 0, (int) (20 * scale));
        if(progressTasks.size()>0) {
            sclLayout.addView(progress);
            nowHeight += (int) (120 * scale);
        }

        for(int i=0;i<progressTasks.size();i++){
            progressTasks.get(i).setY(nowHeight);
            tasks.add(progressTasks.get(i));
            sclLayout.addView(progressTasks.get(i));
            nowHeight += 200 * scale;
        }


        finish = new TextView(this);
        finish.setY(nowHeight);
        finishY = nowHeight;
        finish.setText(strings[lang][10]);
        finish.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, (int) (120 * scale)));
        finish.setBackgroundColor(ResourseColors.colorLightGray);
        finish.setTextColor(ResourseColors.colorWhite);
        finish.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        finish.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
        finish.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50 * scale);
        finish.setPadding(0, (int) (20 * scale), 0, (int) (20 * scale));
        if(finishedTasks.size()>0) {
            sclLayout.addView(finish);
            nowHeight += (int) (120 * scale);
        }

        for(int i=0;i<finishedTasks.size();i++){
            finishedTasks.get(i).setY(nowHeight);
            tasks.add(finishedTasks.get(i));
            sclLayout.addView(finishedTasks.get(i));
            nowHeight += 200 * scale;
        }

        notActive = new TextView(this);
        notActive.setY(nowHeight);
        notActiveY = nowHeight;
        notActive.setText(strings[lang][11]);
        notActive.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, (int) (120 * scale)));
        notActive.setBackgroundColor(ResourseColors.colorGray);
        notActive.setTextColor(ResourseColors.colorWhite);
        notActive.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        notActive.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
        notActive.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50 * scale);
        notActive.setPadding(0, (int) (20 * scale), 0, (int) (20 * scale));
        if(notActiveTasks.size()>0) {
            sclLayout.addView(notActive);
            nowHeight += (int) (120 * scale);
        }

        for(int i=0;i<notActiveTasks.size();i++){
            notActiveTasks.get(i).setY(nowHeight);
            tasks.add(notActiveTasks.get(i));
            sclLayout.addView(notActiveTasks.get(i));
            nowHeight += 200 * scale;
        }

        sclLayout.setMinimumHeight(nowHeight);
        sclLayout.setBackgroundColor(ResourseColors.colorIconGray);
    }

    boolean checkWaitPerformer(HashMap<String,Long> profies){
        if(profies!=null) {
                if(profies.containsValue(0L)|profies.containsValue(1L)|profies.containsValue(2L)|profies.containsValue(3L)|profies.containsValue(4L)){
                    return false;
                }else{
                    return true;
                }
        }
        System.out.println("abab "+profies);
       return true;
    }

    boolean checkChoosePerformer(HashMap<String,Long> profies){
        if(profies!=null) {
            if(profies.containsValue(0L)|profies.containsValue(1L)&!profies.containsValue(2L)&!profies.containsValue(3L)&!profies.containsValue(4L)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    boolean checkHiredPerformer(HashMap<String,Long> profies){
        if(profies!=null) {
            if((profies.containsValue(2L)|profies.containsValue(3L))&!profies.containsValue(4L)){
                return true;
            }else{
                return false;
            }
        }
        return true;
    }

    boolean checkFinishedTask(HashMap<String,Long> profies){
        if(profies!=null) {
            if(profies.containsValue(4L)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    boolean checkFailDateTask(Date start){
        if(start.before(nowDate)){
            return true;
        }
        return false;
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void design() {
        dm = getResources().getDisplayMetrics();
        scale = dm.widthPixels / 1080.0f;
        topLabel = new TopLabel(this, strings[lang][0], scale);
        topLabel.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layout.addView(topLabel);
        scl = new ScrollView(this);
        scl.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, dm.heightPixels - (int) (168 * scale)));
        scl.setY(168 * scale);
        // scl.setBackgroundColor(Color.YELLOW);
        sclLayout = new FrameLayout(this);
        //  sclLayout.setBackgroundColor(Color.RED);
        scl.addView(sclLayout);

        /**/


        sclLayout.setMinimumHeight((int) (200 * 10 * scale));

        layout.addView(scl);

        loading = new LoadCircle(this);
        loading.setX(1080/2*scale-loading.bmp.getWidth()/2);
        loading.setY(400*scale);
        layout.addView(loading);

    }
}
