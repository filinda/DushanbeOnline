package beka.com.bk.dushanbeonline;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.Date;

import beka.com.bk.dushanbeonline.custom_views.CustomPopUp;
import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import beka.com.bk.dushanbeonline.custom_views.CustomRoundButton;
import beka.com.bk.dushanbeonline.custom_views.CustomSwitch;

import beka.com.bk.dushanbeonline.custom_views.RatingView;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfiPage extends Activity {


    FrameLayout layout;

    CircleImageView avatar;
    TextView profiName, ageExp, languages, categories;
    TopLabel label;
    CustomPublishBtn callBtn;
    CustomSwitch swt;

    String profiId;

    ArrayList<View> comments;

    int lang;
    String[][] strings;
    float scale;
    DisplayMetrics dm;
    CustomRoundButton cancel, hire;
    RatingView rating;
    boolean openedAsProfi;
    //int halfComplitted =
    String phone=null, taskId=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        phone = getIntent().getStringExtra("phone");
        taskId = getIntent().getStringExtra("taskId");

        openedAsProfi = getIntent().getBooleanExtra("OpenAsProfi",false);

        dm = getResources().getDisplayMetrics();
        scale = dm.widthPixels/1080f;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        strings = new String[2][28];
        strings[0][0] = "User profile";
        strings[1][0] = "Профиль пользователя";
        strings[0][1] = "Call";
        strings[1][1] = "Позвонить";
        strings[0][2] = "Age";
        strings[1][2] = "Воз.";
        strings[0][3] = "Exp.";
        strings[1][3] = "Стаж";
        strings[0][4] = "years";
        strings[1][4] = "лет";
        strings[0][5] = "Cancel";
        strings[1][5] = "Отменить";
        strings[0][6] = "Invite";
        strings[1][6] = "Пригласить";
        strings[0][7] = "Fire";
        strings[1][7] = "Уволить";
        strings[0][8] = "In process...";
        strings[1][8] = "В процессе...";
        strings[0][9] = "Hired";
        strings[1][9] = "Нанят";
        strings[0][10] = "Share";
        strings[1][10] = "Поделиться";
        strings[0][11] = "Statistics";
        strings[1][11] = "Статистика";
        strings[0][12] = "Revues";
        strings[1][12] = "Отзывы";
        strings[0][13] = "Skills";
        strings[1][13] = "Компетенции";
        strings[0][14] = "Your profile";
        strings[1][14] = "Ваш профиль";
        strings[0][15] = "No";
        strings[1][15] = "Нет";
        strings[0][16] = "With confirmation, you send an invitation to this performer. After the performer accepts your invitation, he will begin work on your assignment.\n \nAttention! After confirmation of your invitation by the performer, all your subsequent actions will be taken into account in your statistics.\n \nAre you sure, that you want to hire this performer? ";
        strings[1][16] = "При подтверждении, вы отправляете приглашение данному исполнителю. После того как исполнитель примет ваше приглашение, он начнет работу над вашим заданием. \n \nВнимание! После подтверждения вашего приглашения исполнителем, все ваши последующие действия будут учитываться в вашей статистике.\n \nВы уверенны что собираетесь нанять данного исполнителя?";
        strings[0][17] = "Yes";
        strings[1][17] = "Да";
        strings[0][18] = "Decline";
        strings[1][18] = "Отклонить";
        strings[0][19] = "You are going to refuse help from this worker. Please tell us why:";
        strings[1][19] = "Вы собираетесь отказаться от помощи этого специалиста. Расскажите нам почему:";
        strings[0][20] = "Answer 1";
        strings[1][20] = "Не понравилось качество работы";
        strings[0][21] = "Answer 2";
        strings[1][21] = "Ответ 2";
        strings[0][22] = "Answer 3";
        strings[1][22] = "Ответ 3";
        strings[0][23] = "Answer 1";
        strings[1][23] = "Не понравилось качество работы";
        strings[0][24] = "Answer 2";
        strings[1][24] = "Ответ 2";
        strings[0][25] = "Answer 3";
        strings[1][25] = "Ответ 3";
        strings[0][26] = "Answer 1";
        strings[1][26] = "Не понравилось качество работы";
        strings[0][27] = "Answer 2";
        strings[1][27] = "Ответ 2";

        layout = new FrameLayout(this);

        profiId = getIntent().getStringExtra("profiId");
        lang = getIntent().getIntExtra("lang", 1);

        design();

        uploadProfiInfo();

    }
String clientPhone = "";
    private void uploadProfiInfo() {

        FirebaseDatabase.getInstance().getReference("usersInfo").child(profiId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(openedAsProfi) {
                    clientPhone = dataSnapshot.child("phone").getValue(String.class);
                    callBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + clientPhone));
                            if (ActivityCompat.checkSelfPermission(ProfiPage.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                ActivityCompat.requestPermissions(ProfiPage.this, new String[]{Manifest.permission.CALL_PHONE}, 5);
                                return;
                            }
                            ProfiPage.this.startActivity(intent);
                        }
                    });
                }

                profiName.setText(dataSnapshot.child("name").getValue(String.class)+" "+dataSnapshot.child("surname").getValue(String.class));
                String imgurl = dataSnapshot.child("photoURL").getValue(String.class);
                Glide.with(ProfiPage.this).load(imgurl).into(avatar);

                Date now = new Date();
                now.setTime(System.currentTimeMillis());

                if(openedAsProfi) {
                    Date birth = new Date();
                    birth.setTime(dataSnapshot.child("birthDate").getValue(Long.class));

                    Date exp = new Date();
                    exp.setTime(dataSnapshot.child("expDate").getValue(Long.class));

                    int work = (1900 + now.getYear() - exp.getYear());
                    int age = (1900 + now.getYear() - birth.getYear());

                    String russianYearBirth = "лет";
                    if (age % 10 <= 4) {
                        russianYearBirth = "года";
                    }
                    if ((age % 10) == 1) {
                        russianYearBirth = "год";
                    }
                    if ((age % 100) == 11 | (age % 100) == 12 | (age % 100) == 13 | (age % 100) == 14) {
                        russianYearBirth = "лет";
                    }

                    String russianYearExp = "лет";
                    if (work % 10 <= 4) {
                        russianYearExp = "года";
                    }
                    if (work % 10 == 1) {
                        russianYearExp = "год";
                    }
                    if ((work % 100) == 11 | (work % 100) == 12 | (work % 100) == 13 | (work % 100) == 14) {
                        russianYearExp = "лет";
                    }


                    if (work != 0) {
                        if (lang == 0)
                            ageExp.setText(strings[lang][2] + ": " + (age) + (" " + strings[lang][4] + "  |  " + strings[lang][3] + ": ") + (work) + " " + strings[lang][4]);

                        if (lang == 1)
                            ageExp.setText(strings[lang][2] + ": " + (age) + (" " + russianYearBirth + "  |  " + strings[lang][3] + ": ") + (work) + " " + russianYearExp);
                    } else {
                        if (lang == 0)
                            ageExp.setText(strings[lang][2] + ": " + (age) + (" " + strings[lang][4] + "  |  " + strings[lang][3] + ": ") + "less then a year");

                        if (lang == 1)
                            ageExp.setText(strings[lang][2] + ": " + (age) + (" " + russianYearBirth + "  |  " + strings[lang][3] + ": ") + "меньше года");
                    }
                }

                if(dataSnapshot.child("profiInfo").child("isProfi").getValue(Boolean.class)){
                    //upload skils
                }else{
                    float y = swt.getY();

                    String[] labels = new String[2];
                    labels[0] = strings[lang][11];
                    labels[1] = strings[lang][12];
                    layout.removeView(swt);
                    swt = new CustomSwitch(ProfiPage.this,labels);
                    swt.setY(y);
                    layout.addView(swt);
                }

                if(FirebaseAuth.getInstance().getUid().equals(profiId)){
                    label.setLabel(strings[lang][14]);
                    label.invalidate();
                }

                int stars = 2;
                try {
                    stars = (int)((float)dataSnapshot.child("stars").getValue(Float.class));
                }catch (Exception e){
                    System.out.println("abab no stars foe user");
                }
                rating.setStars(stars);

                setContentView(layout);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                setContentView(layout);
            }
        });
        if(openedAsProfi) {
            FirebaseDatabase.getInstance().getReference("tasks").child(phone).child(taskId).child("profies").child(profiId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hireStage = dataSnapshot.getValue(Long.class);
                    switch(hireStage.intValue()) {
                        case 0:
                            cancel.setText(strings[lang][18],false);
                            cancel.setBackGround1(ResourseColors.colorRed);
                            cancel.blocked = false;
                            hire.setText(strings[lang][6],false);
                            hire.setBackGround1(Color.TRANSPARENT);
                            hire.setTextColor(ResourseColors.colorStroke);
                            hire.invalidate();
                            hire.blocked = false;
                            break;
                        case 1:
                            cancel.setText(strings[lang][5],false);
                            cancel.setBackGround1(ResourseColors.colorRed);
                            cancel.blocked = false;
                            hire.setText(strings[lang][8],false);
                            hire.setBackGround1(ResourseColors.colorYellow);
                            hire.setTextColor(ResourseColors.colorWhite);
                            hire.blocked = false;
                            break;
                        case 2:
                            cancel.setText(strings[lang][7],false);
                            cancel.setBackGround1(ResourseColors.colorRed);
                            cancel.blocked = false;
                            cancel.invalidate();
                            hire.setText(strings[lang][9],false);
                            hire.setBackGround1(ResourseColors.colorGreen);
                            hire.setBackGround2(ResourseColors.colorGreen);
                            hire.setTextColor(ResourseColors.colorWhite);
                            hire.invalidate();
                            hire.blocked = true;
                            break;
                        case 3:
                            layout.removeView(cancel);
                            layout.removeView(hire);
                            swt.setY(profiName.getY() + 140* scale);
                            break;
                        case 4:
                            layout.removeView(cancel);
                            layout.removeView(hire);
                            swt.setY(profiName.getY() + 140* scale);
                            break;

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    Long hireStage = 0L; //0-not hired 1-in process 2-hired

    private void design() {
        label = new TopLabel(this, strings[lang][0],scale);
        label.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layout.addView(label);

        int wAvatar = (int)(250*scale),hAratar = (int)(250*scale);
        avatar = new CircleImageView(this);
        avatar.setLayoutParams(new FrameLayout.LayoutParams(wAvatar,hAratar));
        avatar.setX(dm.widthPixels/2 - wAvatar/2);
        avatar.setY(320*scale);
        layout.addView(avatar);

        rating = new RatingView(this);
        rating.setX(dm.widthPixels/2-rating.width/2);
        rating.setY(avatar.getY()+300*scale);
        layout.addView(rating);

        profiName = new TextView(this);
        profiName.setTypeface(Typeface.createFromAsset(getAssets(),"font_bold.ttf"));
        profiName.setTextColor(ResourseColors.colorBlack);
        profiName.setTextSize(TypedValue.COMPLEX_UNIT_PX,55*scale);
        profiName.setGravity(Gravity.CENTER_HORIZONTAL);
        profiName.setY(rating.getY()+rating.heiight+20*scale);
        layout.addView(profiName);

        ageExp = new TextView(this);
        ageExp.setTypeface(Typeface.createFromAsset(getAssets(),"font.ttf"));
        ageExp.setTextColor(ResourseColors.colorGray);
        ageExp.setTextSize(TypedValue.COMPLEX_UNIT_PX,40*scale);
        ageExp.setGravity(Gravity.CENTER_HORIZONTAL);
        ageExp.setY(profiName.getY()+70*scale);
        layout.addView(ageExp);

        if(openedAsProfi) {

            cancel = new CustomRoundButton(this) {
                public void press() {

                    if (hireStage == 0) {
                        String[] answers = new String[8];
                        answers[0] = strings[lang][20];
                        answers[1] = strings[lang][21];
                        answers[2] = strings[lang][22];
                        answers[3] = strings[lang][23];
                        answers[4] = strings[lang][24];
                        answers[5] = strings[lang][25];
                        answers[6] = strings[lang][26];
                        answers[7] = strings[lang][27];
                        CustomPopUp popUp = new CustomPopUp(ProfiPage.this, strings[lang][19],answers,profiId,taskId,phone,lang);
                        layout.addView(popUp);
                    }

                    if (hireStage == 1) {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        if(phone!=null&taskId!=null) {
                                            FirebaseDatabase.getInstance().getReference("tasks").child(phone).child(taskId).child("profies").child(profiId).setValue(0);
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        cancel.unTap();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfiPage.this);
                        builder.setMessage(strings[lang][16]).setPositiveButton(strings[lang][17], dialogClickListener)
                                .setNegativeButton(strings[lang][15], dialogClickListener).show();

                    }




                }
            };
            cancel.question = true;
            cancel.setLayoutParams(new FrameLayout.LayoutParams((int) (444 * scale), (int) (119 * scale)));
            cancel.setX(scale * (1080 / 2 - 444) / 2);
            cancel.setY(ageExp.getY() + 123 * scale);
            cancel.blocked = false;
            cancel.setText(strings[lang][18],false);
            cancel.setBackGround1(ResourseColors.colorRed);
            cancel.setTextColor(ResourseColors.colorWhite);
            layout.addView(cancel);

            hire = new CustomRoundButton(this) {
                public void press() {
                    if (hireStage == 0) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        if(phone!=null&taskId!=null) {
                                            FirebaseDatabase.getInstance().getReference("tasks").child(phone).child(taskId).child("profies").child(profiId).setValue(1);
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        hire.unTap();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfiPage.this);
                        builder.setMessage(strings[lang][16]).setPositiveButton(strings[lang][17], dialogClickListener)
                                .setNegativeButton(strings[lang][15], dialogClickListener).show();

                    }
                }
            };
            hire.question = true;
            hire.setLayoutParams(new FrameLayout.LayoutParams((int) (444 * scale), (int) (119 * scale)));
            hire.setX(scale * (1080 / 2 + (1080 / 2 - 444) / 2));
            hire.setY(ageExp.getY() + 123 * scale);
            hire.blocked = false;
            hire.fixed = true;
            hire.setText(strings[lang][6],false);
            hire.setBackGround1(Color.TRANSPARENT);
            hire.setBackGround2(ResourseColors.colorYellow);
            hire.setTextColor(ResourseColors.colorGray);
            layout.addView(hire);

            callBtn = new CustomPublishBtn(this, ResourseColors.colorGreen, strings[lang][1]);
            callBtn.setY(dm.heightPixels - 160 * scale);
            callBtn.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (160 * scale)));
            layout.addView(callBtn);
        }
            String[] labels = new String[3];
            labels[0] = strings[lang][11];
            labels[1] = strings[lang][12];
            labels[2] = strings[lang][13];

            swt = new CustomSwitch(this, labels);
            if(openedAsProfi) {
                swt.setY(hire.getY() + 140 * scale);
            }else{
                swt.setY(profiName.getY() + 100* scale);
            }
            layout.addView(swt);


    }

}
