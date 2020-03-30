package beka.com.bk.dushanbeonline.custom_views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;

import beka.com.bk.dushanbeonline.AddRemoveCategories;
import beka.com.bk.dushanbeonline.AllTasksActivity;
import beka.com.bk.dushanbeonline.ChngProfile;
import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.MyTasksActivity;
import beka.com.bk.dushanbeonline.NotificationActivity;
import beka.com.bk.dushanbeonline.ProfiPage;
import beka.com.bk.dushanbeonline.ResourseColors;
import beka.com.bk.dushanbeonline.SplashScreenActivity;
import beka.com.bk.dushanbeonline.WorkInProgressActivity;
import de.hdodenhof.circleimageview.CircleImageView;


@SuppressLint("AppCompatCustomView")
public class ProfileMenu extends FrameLayout {

    int lang = 0;
    String[][] strings;
    Paint p;
    float scale;
    CircleImageView photo;
    Typeface type;
    MainMenuActivity context2;
    TextView nameTv, phoneTv, surnameTv;
    int textSize = 50;
    ProfileMenuBtn btn;
    TextView profTv;
    boolean isProffi = false;
    float offset = 0;
    ProfileMenuBtn[] btns;
    TextView balance;
    ImageView phoneIcon;


    public ProfileMenu(MainMenuActivity context, final float scale, final int lang) {
        super(context);
        context2 = context;

        this.lang = lang;

        strings = new String[2][22];
        strings[0][0] = "Edit profile";
        strings[1][0] = "Редактировать профиль";
        strings[0][1] = "Balance";
        strings[1][1] = "Баланс";
        strings[0][2] = "Work proposal";
        strings[1][2] = "Предлагаемая работа";
        strings[0][3] = "My tasks";
        strings[1][3] = "Мои задания";
        strings[0][4] = "My job";
        strings[1][4] = "Моя работа";
        strings[0][5] = "Notifications";
        strings[1][5] = "Уведомления";
        strings[0][6] = "Favourite";
        strings[1][6] = "Избранное";
        strings[0][7] = "Logout" ;
        strings[1][7] = "Выйти" ;
        strings[0][8] = "Become a performer";
        strings[1][8] = "Стать исполнителем";
        strings[0][9] = "My page";
        strings[1][9] = "Моя страница";
        strings[0][18] = "Add/remove category";
        strings[1][18] = "Добавить/отключить категории";
        strings[0][10] = "UnNamed";
        strings[1][10] = "Нет имени";
        strings[0][11] = "Not registered";
        strings[1][11] = "Не зарегистрирован";
        strings[0][12] = "Performer";
        strings[1][12] = "Исполнитель";
        strings[0][13] = "User";
        strings[1][13] = "Пользователь";
        strings[0][14] = "Are you sure, that you want to quit?";
        strings[1][14] = "Вы уверены, что хотите выйти?";
        strings[0][15] = "Yes";
        strings[1][15] = "Да";
        strings[0][16] = "No";
        strings[1][16] = "Нет";
        strings[0][17] = "tjs";
        strings[1][17] = "сом";
        strings[0][19] = "My statistics";
        strings[1][19] = "Моя статистика";
        strings[0][20] = "Are you sure that you want to be a performer? If yes than administrator will contact you soon.";
        strings[1][20] = "Вы уверены, что хотите стать исполнителем? Если да, то наш администратор свяжется с вами.";
        strings[0][21] = "ATTENTION!!! When adding new categories, you will be charged a monthly fee.";
        strings[1][21] = "ВНИМАНИЕ!!! При добавлении новых категорий с вас будет взыматься ежемесечная плата.";

        type = Typeface.createFromAsset(context.getAssets(), "font.ttf");

        this.scale = scale;
        p = new Paint();
        p.setColor(ResourseColors.colorProfileMenuBg);

        setWillNotDraw(false);

        Bitmap arPhoto = getBitmapFromAsset(context, "ar_defaultphoto.png");
        arPhoto = Bitmap.createScaledBitmap(arPhoto, (int) (202 * scale), (int) (202 * scale), false);
        ImageView arPhotoIcon = new ImageView(context);
        arPhotoIcon.setImageBitmap(arPhoto);
        arPhotoIcon.setX(52 * scale);
        arPhotoIcon.setY(58 * scale);
        arPhotoIcon.setScaleType(ImageView.ScaleType.MATRIX);
        addView(arPhotoIcon);

        Bitmap phonePhoto = getBitmapFromAsset(context, "phone_icon.png");
        phonePhoto = Bitmap.createScaledBitmap(phonePhoto, (int) (42 * scale), (int) (42 * scale), false);
        phoneIcon = new ImageView(context);
        phoneIcon.setImageBitmap(phonePhoto);
        phoneIcon.setX(48 * scale);
        phoneIcon.setY(307* scale);
        phoneIcon.setScaleType(ImageView.ScaleType.MATRIX);
        addView(phoneIcon);

        Bitmap locPhoto = getBitmapFromAsset(context, "place_icon.png");
        locPhoto = Bitmap.createScaledBitmap(locPhoto, (int) (33 * scale), (int) (48 * scale), false);
        ImageView locIcon = new ImageView(context);
        locIcon.setImageBitmap(locPhoto);
        locIcon.setX(450 * scale);
        locIcon.setY(302* scale);
        locIcon.setScaleType(ImageView.ScaleType.MATRIX);
        addView(locIcon);

        nameTv = new TextView(getContext());
        surnameTv = new TextView(getContext());

        photo = new CircleImageView(context.getBaseContext());
        photo.setX(61* scale);
        photo.setY(67 * scale);
        photo.setLayoutParams(new FrameLayout.LayoutParams((int) (scale * 184), (int) (scale * 184)));
        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
        if (account != null) {

            FirebaseDatabase.getInstance().getReference("usersInfo").child(account.getUid()).child("photoURL").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imgurl = dataSnapshot.getValue(String.class);
                    Glide.with(getContext()).load(imgurl).into(photo);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        addView(photo);

        photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context2, ChngProfile.class);
                context2.startActivity(intent);
            }
        });


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            FirebaseDatabase.getInstance().getReference("profiInfo").child(account.getUid()).child("isProfi").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(Boolean.class)!=null) {
                        if (dataSnapshot.getValue(Boolean.class)) {
                            isProffi = true;
                            offset = 0*scale;
                            if(profTv!=null)
                                removeView(profTv);

                            removeView(btns[9]);
                            addView(btns[9]);
                            btns[9].invalidate();

                            removeView(btns[4]);
                            addView(btns[4]);

                            btns[8].label = strings[lang][18];
                            btns[8].invalidate();

                            btns[8].setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                        Intent intent = new Intent(getContext(),AddRemoveCategories.class);
                                                        intent.putExtra("lang",lang);
                                                        getContext().startActivity(intent);
                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:

                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage(strings[lang][21]).setPositiveButton(strings[lang][15], dialogClickListener)
                                            .setNegativeButton(strings[lang][16], dialogClickListener).show();
                                }
                            });
                            profTv = new TextView(getContext());
                            profTv.setTypeface(type);
                            profTv.setTextColor(ResourseColors.colorStroke);
                            profTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize*scale);
                            Rect ibounds = new Rect();
                            Paint textNamePaint = profTv.getPaint();
                            String name = strings[lang][12];
                            if (name != null) {
                                textNamePaint.getTextBounds(name, 0, name.length(), ibounds);
                            } else {
                                textNamePaint.getTextBounds("", 0, "".length(), ibounds);
                            }
                            profTv.setX(518 * ProfileMenu.this.scale);
                            profTv.setY(290 * ProfileMenu.this.scale);
                            profTv.setText(name);
                            addView(profTv);

                            nameTv.setY(88 * ProfileMenu.this.scale-offset);
                            surnameTv.setY(160 * ProfileMenu.this.scale-offset*1.1f);


                            FirebaseDatabase.getInstance().getReference().child("paymentInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    removeView(balance);
                                    removeView(btns[1]);
                                    addView(btns[1]);
                                    addView(balance);
                                    if(btns[2].getY()<=btns[1].getY()){
                                        for (int i = 2; i < 7; i++) {
                                            btns[i].setY(btns[i].getY() + 130*scale);
                                        }
                                    }
                                    String unformated, formated = "";
                                    if(dataSnapshot.getValue()!=null){
                                        unformated = dataSnapshot.getValue(Long.class)+"";
                                    }else{
                                        unformated = "0";
                                    }
                                    for(int i = 0; i< unformated.length();i++){
                                        if((unformated.length()-i)%3==0&(unformated.length()-i)!=0){
                                            formated+=",";
                                        }
                                        formated+=unformated.toCharArray()[i];
                                    }
                                    balance.setText(formated+" "+strings[lang][17]);
                                    balance.measure(0,0);
                                    balance.setX((935-16)*scale - balance.getMeasuredWidth()-30*scale);
                                    balance.setY(btns[1].getY()+125*scale/2-balance.getMeasuredHeight()/2);

                                    for(int i=0; i< btns.length;i++){
                                        btns[i].setY((phoneIcon.getY()+ 85 * scale + (130 * i) * scale));
                                    }
                                    btns[8].setY(getHeight()-130*2*scale);
                                    btns[9].setY(getHeight()-130*1*scale);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }else{
                            btns[9].label = strings[lang][19];
                            btns[9].invalidate();
                            btns[9].setY(getHeight()-130*1*scale);
                            isProffi = false;
                            if(profTv!=null)
                            removeView(profTv);
                            btns[8].label = strings[lang][8];
                            btns[8].setY(getHeight()-130*2*scale);
                            btns[8].invalidate();
                            offset = 0*scale;
                            profTv = new TextView(getContext());
                            profTv.setTypeface(type);
                            profTv.setTextColor(ResourseColors.colorStroke);
                            profTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize*scale);
                            removeView(btns[8]);
                            addView(btns[8]);
                            Rect ibounds = new Rect();
                            Paint textNamePaint = profTv.getPaint();
                            String name = strings[lang][13];
                            if (name != null) {
                                textNamePaint.getTextBounds(name, 0, name.length(), ibounds);
                            } else {
                                textNamePaint.getTextBounds("", 0, "".length(), ibounds);
                            }
                            profTv.setX(518 * ProfileMenu.this.scale);
                            profTv.setY(290 * ProfileMenu.this.scale);
                            profTv.setText(name);
                            addView(profTv);
                            nameTv.setY(88 * ProfileMenu.this.scale-offset);
                            surnameTv.setY(160 * ProfileMenu.this.scale-offset*1.1f);
                            removeView(balance);
                            removeView(btns[1]);
                            removeView(btns[4]);
                            if(btns[2].getY()>btns[1].getY()) {
                                for (int i = 2; i < 8; i++) {
                                    btns[i].setY(btns[i].getY() - 130*scale);
                                }
                                for (int i = 5; i < 8; i++) {
                                    btns[i].setY(btns[i].getY() - 130*scale);
                                }
                            }

                        }
                    }else{

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            nameTv.setTypeface(type);
            nameTv.setTextColor(ResourseColors.colorStroke);
            nameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,55*scale);
            nameTv.setX(308 * ProfileMenu.this.scale);
            nameTv.setY(88 * ProfileMenu.this.scale-offset);
            addView(nameTv);

            FirebaseDatabase.getInstance().getReference("usersInfo").child(account.getUid()).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    if (name != null) {
                        nameTv.setText(name);
                    } else {

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        surnameTv = new TextView(getContext());
        surnameTv.setTypeface(type);
        surnameTv.setTextColor(ResourseColors.colorStroke);
        surnameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,55*scale);
        surnameTv.setX(308 * ProfileMenu.this.scale);
        surnameTv.setY(160 * ProfileMenu.this.scale-offset*1.1f);
        addView(surnameTv);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference("usersInfo").child(account.getUid()).child("surname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    surnameTv.setText(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        phoneTv = new TextView(context);
        phoneTv.setTypeface(type);
        phoneTv.setTextColor(ResourseColors.colorStroke);
        phoneTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize*scale);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != "") {
            phoneTv.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4));
        }
        Rect phbounds = new Rect();
        Paint textPhonePaint = phoneTv.getPaint();
        if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != "") {
            textPhonePaint.getTextBounds(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4), 0, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4).length(), phbounds);
        } else {
            textPhonePaint.getTextBounds("", 0, "".length(), phbounds);
        }
        int width = phbounds.width();
        phoneTv.setX(125 * scale);
        phoneTv.setY(290 * scale);
        addView(phoneTv);

        btns = new ProfileMenuBtn[10];
        for (int i = 0; i < 10; i++) {
            Bitmap temp = MainMenuActivity.getBitmapFromAsset(getContext(),"ProfileMenu/"+(i)+".png");
            temp = Bitmap.createScaledBitmap(temp,(int)(temp.getWidth()*scale),(int)(temp.getHeight()*scale),false);
            btn = new ProfileMenuBtn(context, temp,strings[lang][i]);
            btn.setX(8 * scale);
            btn.setId(100 + i);
            btn.setY((phoneIcon.getY()+ 85 * scale + (130 * i) * scale));
            btn.setOnClickListener(new OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {

                    if (v.getId() == 100) {
                        Intent intent = new Intent(context2, ChngProfile.class);
                        context2.startActivity(intent);
                    }
                    if (v.getId() == 102) {
                        Intent intent = new Intent(context2, AllTasksActivity.class);
                        intent.putExtra("lang", ProfileMenu.this.lang);
                        context2.startActivity(intent);
                    }
                    if (v.getId() == 103) {
                        Intent intent = new Intent(context2, MyTasksActivity.class);
                        intent.putExtra("lang", ProfileMenu.this.lang);
                        context2.startActivity(intent);
                    }
                    if (v.getId() == 105) {
                        Intent intent = new Intent(context2, NotificationActivity.class);
                        intent.putExtra("lang", ProfileMenu.this.lang);
                        context2.startActivity(intent);
                    }



                    if (v.getId() == 107) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(strings[lang][14]).setPositiveButton(strings[lang][15], new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(context2, SplashScreenActivity.class);
                                context2.startActivity(intent);
                            }
                        })
                                .setNegativeButton(strings[lang][16], new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();

                    }

                    if (v.getId() == 108) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(strings[lang][20]).setPositiveButton(strings[lang][15], new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("wanaBeProfi").setValue(true);
                            }
                        })
                                .setNegativeButton(strings[lang][16], new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }

                    if (v.getId() == 104) {
                        Intent intent = new Intent(context2, WorkInProgressActivity.class);
                        intent.putExtra("lang", ProfileMenu.this.lang);
                        context2.startActivity(intent);
                    }

                    if (v.getId() == 109) {
                        Intent intent = new Intent(context2, ProfiPage.class);
                        intent.putExtra("lang", ProfileMenu.this.lang);
                        intent.putExtra("profiId", FirebaseAuth.getInstance().getUid());
                        context2.startActivity(intent);
                    }
                }
            });
            if(i>7){
                btn.setY(context.getResources().getDisplayMetrics().heightPixels-390*scale+(i-6)*130*scale);
            }
            addView(btn);
            btns[i]=btn;
        }

        balance = new TextView(getContext());
        balance.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"font.ttf"));
        balance.setTextSize(TypedValue.COMPLEX_UNIT_PX,45*scale);
        balance.setTextColor(ResourseColors.colorLightGray);
        addView(balance);
        balance.setText("0 "+strings[lang][17]);
        balance.measure(0,0);
        balance.setX((935-16)*scale - balance.getMeasuredWidth()-30*scale);
        balance.setY(btns[1].getY()+125*scale/2-balance.getMeasuredHeight()/2);



    }


    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        super.onDraw(canvas);
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

}

