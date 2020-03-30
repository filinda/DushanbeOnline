package beka.com.bk.dushanbeonline;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;

import beka.com.bk.dushanbeonline.custom_views.CategoryButton;
import beka.com.bk.dushanbeonline.custom_views.ProfileMenu;
import beka.com.bk.dushanbeonline.custom_views.SubCategoryMenu;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;

public class MainMenuActivity extends AppCompatActivity {
    FrameLayout layout;
    public static int lang;
    public static String[][] strings;
    Typeface type;
    ProfileMenu profMenu;
    static float scale;
    SubCategoryMenu subMenu;
    TopLabel label;
    View topTransparentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        topTransparentLayout = new View(this);
        topTransparentLayout.setBackgroundColor(Color.argb(100,0,200,50));
        topTransparentLayout.setLayoutParams(new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels));

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("abab", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        System.out.println("abab token: "+token);
                        FirebaseDatabase.getInstance().getReference("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("token").setValue(token);
                    }
                });

        lang = getIntent().getIntExtra("lang", 1);

        strings = new String[2][15];
        strings[0][0] = "Choose Area";
        strings[1][0] = "Выберите область";
        strings[0][1] = "Medicine";
        strings[1][1] = "Медицина";
        strings[0][2] = "Lawyers";
        strings[1][2] = "Юристы";
        strings[0][3] = "Repairers";
        strings[1][3] = "Ремонтники";
        strings[0][4] = "Tutors";
        strings[1][4] = "Обучение";
        strings[0][5] = "Information Technology";
        strings[1][5] = "Информационные Технологии";
        strings[0][6] = "Moving";
        strings[1][6] = "Переезды, Грузчики";
        strings[0][7] = "Transportation";
        strings[1][7] = "Транспортировка";
        strings[0][8] = "Cleaning";
        strings[1][8] = "Уборка";
        strings[0][9] = "Lost connection.....";
        strings[1][9] = "Пожалуйста подключитесь к интернету...";
        strings[0][10] = "Uploaded";
        strings[1][10] = "Загружено";
        strings[0][11] = "Failed";
        strings[1][11] = "Загрузка провалена";
        strings[0][12] = "Uploading...";
        strings[1][12] = "Загрузка подождите...";
        strings[0][13] = "User profile updated.";
        strings[1][13] = "Ваш профиль обнавлен.";
        strings[0][14] = "Create task";
        strings[1][14] = "Создать задание";

        layout = new FrameLayout(this);

        setContentView(layout);


        type = Typeface.createFromAsset(getAssets(), "font.ttf");

        design();


        profMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        swipeDetector = new SwipeListener(this){
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                swiped = true;
                System.out.println("ababtouch right");
                profMenu.animate().translationX(0).setDuration(500).start();
                isProfileShown = true;
            }
            public void onSwipeLeft() {
                swiped = true;
                profMenu.animate().translationX((-935 * scale)).setDuration(300).start();
                isProfileShown = false;
            }
            public void onSwipeBottom() {
            }
        };

    }
    SwipeListener swipeDetector;

    boolean swiped = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            swiped = false;
        }
        swipeDetector.onTouch(topTransparentLayout,ev);
        System.out.println("ababtouch enddispatch "+ev.getAction());
        if(swiped){
            return true;
        }else{
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    boolean isCatActiv = true;

    private void design() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        scale = (float) dm.widthPixels / 1080.0f;

        // GAMBURGER BUTTON

        label = new TopLabel(this, strings[lang][14], scale);
        layout.addView(label);


        Bitmap gambIcon = getBitmapFromAsset(this, "MainMenu/gamb.png");
        gambIcon = Bitmap.createScaledBitmap(gambIcon, (int) (70 * scale), (int) (50 * scale), false);
        label.back.setImageBitmap(gambIcon);
        label.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profMenu.animate().translationX(0).setDuration(500).start();
                isProfileShown = true;

            }
        });


        // CHOOSE CATEGORY TEXT BLOCK
        TextView tx = new TextView(this);
        tx.setText(strings[lang][0]);
        tx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60 * scale);
        tx.setTextColor(ResourseColors.colorBlue);
        tx.setTypeface(type);
        tx.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        Rect bounds = new Rect();
        Paint textPaint = tx.getPaint();
        textPaint.getTextBounds(strings[lang][0], 0, strings[lang][0].length(), bounds);
        tx.setY(265 * scale);
        layout.addView(tx);

        // CATEGORY SCROLL BUTTONS BLOCK

        ScrollView scl = new ScrollView(this);
        scl.setY(414 * scale);
        scl.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, dm.heightPixels - (int) (414 * scale)));
        FrameLayout layoutScroll = new FrameLayout(this);
        layoutScroll.setY(0);
        layoutScroll.setMinimumHeight((int) (12 * 427 * scale));
        scl.addView(layoutScroll);
        layout.addView(scl);

        for (int i = 0; i < 12; i++) {
            Bitmap tutorImg = getBitmapFromAsset(this, "MainMenu/" + (i + 1) + ".png");
            tutorImg = Bitmap.createScaledBitmap(tutorImg, (int) (1032 * scale), (int) (405 * scale), false);
            final CategoryButton btnTutor2 = new CategoryButton(this, tutorImg, strings[lang][i + 1], (int) (20 * scale), scale, i, lang);
            btnTutor2.setX(dm.widthPixels / 2 - ((int) (1032 * scale) / 2));
            btnTutor2.setY((427 * i) * scale);
            btnTutor2.setLayoutParams(new LinearLayout.LayoutParams((int) (1032 * scale), (int) (405 * scale)));
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
                    if (isCatActiv) {
                        int catId = v.getId() - 350;
                        subMenu.changeCategory(catId, false);
                    }
                }
            });
        }

        subMenu = new SubCategoryMenu(this, lang, (int) (dm.heightPixels), 0);
        subMenu.setX(1080 * scale);
        subMenu.setY(0 * scale);
        layout.addView(subMenu);
        subMenu.setOpenCloseListener(new OpenCloseListener() {
            @Override
            public void onClose() {
                isCatActiv = true;
            }

            @Override
            public void onOpen() {
                isCatActiv = false;
            }
        });

        // PROFILE MENU
        profMenu = new ProfileMenu(this, scale, lang);
        profMenu.setX(-935 * scale);
        profMenu.setY(0 * scale);
        profMenu.setLayoutParams(new FrameLayout.LayoutParams((int) (935 * scale),  dm.heightPixels));
        layout.addView(profMenu);

       // layout.addView(topTransparentLayout);
    }

    //SETTING USER PHOTO FROM PROFILEVIEW
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 73 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Uri filePath) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(strings[lang][12]);
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storageReference;
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    FirebaseUser[] user = new FirebaseUser[1];
                                    user[0] = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(task.getResult())
                                            .build();

                                    user[0].updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("firebase", strings[lang][13]);
                                                    }
                                                }
                                            });
                                }
                            });

                            Toast.makeText(MainMenuActivity.this, strings[lang][10], Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainMenuActivity.this, strings[lang][11] + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage(strings[lang][10] + (int) progress + "%");
                        }
                    });
        }
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


    boolean isProfileShown = false;
    float firsX = 0;

   /* @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isProfileShown) {


            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (profMenu.getX() < -200 * scale) {
                    profMenu.animate().translationX((-935 * scale)).setDuration(300).start();
                    isProfileShown = false;
                }
                if (profMenu.getX() >= -200 * scale) {
                    profMenu.animate().translationX(0).setDuration(300).start();
                    isProfileShown = true;
                }
                return false;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                firsX = event.getRawX() + profMenu.getX();
            }

            if (event.getRawX() - (firsX) <= 0)
                profMenu.setX(event.getRawX() - (firsX));

            return true;

        }

        return false;
    }*/

    AlertDialog noInternet;

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println(intent.getAction());
            Log.d("app", "Network connectivity change" + intent.getAction());
            if (noInternet == null) {
                noInternet = new AlertDialog.Builder(MainMenuActivity.this).create();
                noInternet.setMessage(strings[lang][9]);
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


