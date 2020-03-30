package beka.com.bk.dushanbeonline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import beka.com.bk.dushanbeonline.custom_views.CheckBox;
import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import beka.com.bk.dushanbeonline.custom_views.CustomSpiner3;
import beka.com.bk.dushanbeonline.custom_views.LineWithSwitch;
import beka.com.bk.dushanbeonline.custom_views.OnTurnListener;
import beka.com.bk.dushanbeonline.custom_views.ProfileMenu;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChngProfile extends AppCompatActivity {

    FrameLayout layout;
    int lang;
    String[][] strings;
    Typeface type;
    CircleImageView photo;
    float scale;
    private final int PICK_IMAGE_REQUEST = 71;
    Bitmap newPhoto;
    StorageReference storageReference;
    String filePath;
    EditText   nameEdt, surnameEdt;
    TopLabel label;
    View  space2,space3;
    CustomPublishBtn saveChanges;
    ProgressDialog dialog;
    TextView delete;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ProgressDialog dialogDel;
    LineWithSwitch wory, sound, access;
    ScrollView sclView;
    FrameLayout mainLayout;
    String nameWas, surnameWas;
    boolean changedName = false, changedSurname = false, changedPhoto = false;
    ProgressDialog dialog2;
    DisplayMetrics dm;

    // TextView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chng_profile);

        mainLayout = new FrameLayout(this);
        setContentView(mainLayout);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lang = getIntent().getIntExtra("lang", 1);

        strings = new String[2][23];
        strings[0][0] = "Edit profile";
        strings[1][0] = "Редактировать профиль";
        strings[0][1] = "Change avatar";
        strings[1][1] = "Сменить аватар";
        strings[0][2] = "Name";
        strings[1][2] = "Имя";
        strings[0][3] = "Phone number";
        strings[1][3] = "Номер телефона";
        strings[0][4] = "Save changes";
        strings[1][4] = "Сохранить изменения";
        strings[0][5] = "User profile updated.";
        strings[1][5] = "Профиль пользователя обновлен";
        strings[0][6] = "Image not selected";
        strings[1][6] = "Изображение не выбрано";
        strings[0][7] = "Uploading...";
        strings[1][7] = "Загрузка...";
        strings[0][8] = "Uploaded";
        strings[1][8] = "Загружено";
        strings[0][9] = "Failed";
        strings[1][9] = "Сбой";
        strings[0][10] = "Changes saved";
        strings[1][10] = "Изменения сохранены";
        strings[0][11] = "Lost connection.....";
        strings[1][11] = "Пожалуйста подключитесь к интернету...";
        strings[0][12] = "Changes failed";
        strings[1][12] = "Сохранение провалено";
        strings[0][13] = "Surname";
        strings[1][13] = "Фамилия";
        strings[0][14] = "Are you sure? Deleting your account will remove all your information and records.";
        strings[1][14] = "Вы уверены? Удаление вашего аккаунта приведёт к потери всех ваших записей и информации о вас";
        strings[0][15] = "Yes";
        strings[1][15] = "Да";
        strings[0][16] = "No";
        strings[1][16] = "Нет";
        strings[0][17] = "Delete account";
        strings[1][17] = "Удалить аккаунт";
        strings[0][18] = "Your account deleted";
        strings[1][18] = "Ваша учётная запись удалена";
        strings[0][19] = "Deleting...";
        strings[1][19] = "Удаление...";
        strings[0][20] = "Hide my tasks";
        strings[1][20] = "Скрыть мои задания";
        strings[0][21] = "Sound of notifications";
        strings[1][21] = "Звук уведомлений";
        strings[0][22] = "Available for work";
        strings[1][22] = "Доступен для работы";

        dialog = new ProgressDialog(this);
        dialog.setMessage(strings[lang][8]);

        dialogDel = new ProgressDialog(this);
        dialogDel.setMessage(strings[lang][19]);

        type = Typeface.createFromAsset(getAssets(), "font.ttf");


        storageReference = FirebaseStorage.getInstance().getReference();

        design();
    }

    Intent intent;

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(EditText edt){
        edt.setCursorVisible(true);
        edt.setEnabled(true);
        edt.requestFocus();
        // edt.callOnClick();
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //assert inputMethodManager != null;
        inputMethodManager.showSoftInput(edt,InputMethodManager.SHOW_FORCED);
    }

    public void closeKeyboard(EditText edt){
        edt.setEnabled(false);
        edt.setCursorVisible(false);
    }

    private void design() {
        makeCallback();

        dm = getResources().getDisplayMetrics();
        scale = (float) dm.widthPixels / 1080.0f;

        label = new TopLabel(this, strings[lang][0], scale);
        mainLayout.addView(label);

        label.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                closeKeyboard(nameEdt);
            }
        });


        sclView = new ScrollView(this);
        sclView.setY(600*scale);
        sclView.setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(dm.heightPixels-600*scale)));
        mainLayout.addView(sclView);

        layout = new FrameLayout(this);
        layout.setFocusableInTouchMode(true);
        layout.setBackgroundColor(ResourseColors.colorWhite);
        layout.setMinimumHeight((int)(6*214*scale));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(layout);
                closeKeyboard(nameEdt);
                closeKeyboard(surnameEdt);
            }
        });

        sclView.addView(layout);

        dialog2 = new ProgressDialog(this);
        dialog2.setMessage(strings[lang][19]);

        delete = new TextView(this);
        delete.setText(strings[lang][17]);
        delete.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * scale);
        delete.setTypeface(Typeface.createFromAsset(getAssets(), "font_medium.ttf"));
        SpannableString content = new SpannableString(strings[lang][17]);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        delete.setText(content);
        delete.setTextColor(ResourseColors.colorRed);
        delete.measure(0, 0);
        delete.setX(dm.widthPixels / 2 - delete.getMeasuredWidth() / 2);
        layout.addView(delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                dialog2.show();
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),        // Phone number to verify
                                        60,                 // Timeout duration
                                        TimeUnit.SECONDS,   // Unit of timeout
                                        ChngProfile.this,               // Activity (for callback binding)
                                        mCallbacks);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ChngProfile.this);
                builder.setMessage(strings[lang][14]).setPositiveButton(strings[lang][15], dialogClickListener)
                        .setNegativeButton(strings[lang][16], dialogClickListener).show();

            }
        });
        delete.setLayoutParams(new FrameLayout.LayoutParams( (delete.getMeasuredWidth()), (int) (100 * scale)));


        TextView chngAvatar = new TextView(this);
        chngAvatar.setText(strings[lang][1]);
        chngAvatar.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        chngAvatar.setTypeface(type);
        chngAvatar.setTextColor(ResourseColors.colorBlue);
        final Rect ibounds = new Rect();
        Paint textP = chngAvatar.getPaint();
        textP.getTextBounds(strings[lang][1], 0, strings[lang][1].length(), ibounds);
        int height = ibounds.height();
        int width = ibounds.width();
        chngAvatar.setX(1080 * scale / 2 - width / 2);
        chngAvatar.setY(545 * scale);
        mainLayout.addView(chngAvatar);

        Bitmap bgAvatar = getBitmapFromAsset(this, "avatar_bg_cng.png");
        bgAvatar = Bitmap.createScaledBitmap(bgAvatar, (int) (308 * scale), (int) (308 * scale), false);
        ImageView avatarBg = new ImageView(this);
        avatarBg.setImageBitmap(bgAvatar);
        avatarBg.setX(dm.widthPixels/2 - 154 * scale);
        avatarBg.setY(240 * scale);
        avatarBg.setScaleType(ImageView.ScaleType.MATRIX);
        mainLayout.addView(avatarBg);


        photo = new CircleImageView(this.getBaseContext());
        photo.setX(dm.widthPixels / 2 - 144 * scale);
        photo.setY(250 * scale);
        photo.setLayoutParams(new FrameLayout.LayoutParams((int) (scale * 288), (int) (scale * 288)));
        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
        if (account != null) {
            FirebaseDatabase.getInstance().getReference("usersInfo").child(account.getUid()).child("photoURL").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imgurl = dataSnapshot.getValue(String.class);
                    Glide.with(ChngProfile.this).load(imgurl).into(photo);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        mainLayout.addView(photo);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(ImagePicker.getPickImageIntent(ChngProfile.this), PICK_IMAGE_REQUEST);
            }
        });

        //NAME LINE 2

        space2 = new View(this);
        space2.setBackgroundColor(ResourseColors.colorGrayPrice);
        space2.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (200 * scale)));
        space2.setY(14 * scale);
        layout.addView(space2);

        Bitmap chngIconName = getBitmapFromAsset(this, "editor_icon_blue.png");
        chngIconName = Bitmap.createScaledBitmap(chngIconName, (int) (54 * scale), (int) (54 * scale), false);
        ImageView iconChngName = new ImageView(this);
        iconChngName.setImageBitmap(chngIconName);
        iconChngName.setX(950 * scale);
        iconChngName.setY(space2.getY()+72 * scale);
        iconChngName.setScaleType(ImageView.ScaleType.MATRIX);
        layout.addView(iconChngName);
        iconChngName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showKeyboard(nameEdt);

            }
        });
        Rect rect = new Rect();
        iconChngName.getHitRect(rect);
        rect.top -= 100;    // increase top hit area
        rect.left -= 100;   // increase left hit area
        rect.bottom += 100; // increase bottom hit area
        rect.right += 100;
        layout.setTouchDelegate(new TouchDelegate(rect, iconChngName));

        final TextView NameTxt = new TextView(this);
        NameTxt.setText(strings[lang][2]);
        NameTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        NameTxt.setTypeface(type);
        NameTxt.setTextColor(ResourseColors.colorBlue);
        NameTxt.setX(42 * scale);
        NameTxt.setY(space2.getY()+20 * scale);
        layout.addView(NameTxt);

        nameEdt = new EditText(this);
        nameEdt.setCursorVisible(false);
        nameEdt.setEnabled(false);
        layout.addView(nameEdt);
        nameEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    closeKeyboard(nameEdt);
                    handled = true;
                }
                return handled;
            }
        });
        nameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changedName = !s.toString().equals(nameWas);
                checkSaveBtn();
            }
        });
        FirebaseDatabase.getInstance().getReference("usersInfo").child(account.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameWas = dataSnapshot.getValue(String.class);
                nameEdt.setText(dataSnapshot.getValue(String.class));
                nameEdt.setInputType(InputType.TYPE_CLASS_TEXT);
                nameEdt.setBackgroundColor(Color.TRANSPARENT);



                nameEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
                nameEdt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 51 * scale);
                nameEdt.setTextColor(ResourseColors.colorTextSubmenu);
                nameEdt.setTypeface(type);
                // phoneNumber.measure(0, 0);       //must call measure!
                // width = phoneNumber.getMeasuredWidth();
                nameEdt.setX(42 * scale);
                nameEdt.setY(NameTxt.getY()+45 * scale);
                nameEdt.setLayoutParams(new FrameLayout.LayoutParams((int)(250*scale),(int)(120* scale)));
                //nameEdt.setBackgroundColor(ResourseColors.colorBlack);
                nameEdt.setSelection(nameEdt.getText().toString().length());
                nameEdt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.toString().length()>2){
                            nameEdt.setTextColor(ResourseColors.colorTextSubmenu);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //LINE 3

        space3 = new View(this);
        space3.setBackgroundColor(ResourseColors.colorGrayPrice);
        space3.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (200 * scale)));
        space3.setY(space2.getY()+215 * scale);
        layout.addView(space3);

        Bitmap chngIconSurname = getBitmapFromAsset(this, "editor_icon_blue.png");
        chngIconSurname = Bitmap.createScaledBitmap(chngIconSurname, (int) (54 * scale), (int) (54 * scale), false);
        ImageView iconChngSurname = new ImageView(this);
        iconChngSurname.setImageBitmap(chngIconSurname);
        iconChngSurname.setX(950 * scale);
        iconChngSurname.setY(space3.getY()+72 * scale);
        iconChngSurname.setScaleType(ImageView.ScaleType.MATRIX);
        layout.addView(iconChngSurname);
        iconChngSurname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(surnameEdt);

            }
        });
        Rect rect2 = new Rect();
        iconChngSurname.getHitRect(rect2);
        rect2.top -= 100;    // increase top hit area
        rect2.left -= 100;   // increase left hit area
        rect2.bottom += 100; // increase bottom hit area
        rect2.right += 100;
        layout.setTouchDelegate(new TouchDelegate(rect, iconChngSurname));

        final TextView SurNameTxt = new TextView(this);
        SurNameTxt.setText(strings[lang][13]);
        SurNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        SurNameTxt.setTypeface(type);
        SurNameTxt.setTextColor(ResourseColors.colorBlue);
        SurNameTxt.setX(42 * scale);
        SurNameTxt.setY(space3.getY()+20 * scale);
        layout.addView(SurNameTxt);

        wory = new LineWithSwitch(this,strings[lang][20],"dnd.png");
        wory.setY(space3.getY()+214*scale);
        wory.swtch.off();
        wory.swtch.turnListener = new OnTurnListener(){
            @Override
            public void onTurn(boolean state) {
                FirebaseDatabase.getInstance().getReference("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).child("visible").setValue(!state);
                super.onTurn(state);
            }
        };
        FirebaseDatabase.getInstance().getReference("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).child("visible").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    if(dataSnapshot.getValue(Boolean.class)){
                        wory.swtch.off();
                    }else{
                        wory.swtch.on();
                    }
                }else{
                    FirebaseDatabase.getInstance().getReference("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).child("visible").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sound = new LineWithSwitch(this,strings[lang][21],"nvc.png");
        sound.setY(wory.getY()+214*scale);
        sound.swtch.off();
        sound.swtch.turnListener = new OnTurnListener(){
            @Override
            public void onTurn(boolean state) {
                FirebaseDatabase.getInstance().getReference("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("sound").setValue(state);
                super.onTurn(state);
            }
        };
        FirebaseDatabase.getInstance().getReference("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("sound").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    if(dataSnapshot.getValue(Boolean.class)){
                        sound.swtch.on();
                    }else{
                        sound.swtch.off();
                    }
                }else{
                    FirebaseDatabase.getInstance().getReference("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("sound").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseDatabase.getInstance().getReference("profiInfo").child(FirebaseAuth.getInstance().getUid()).child("isProfi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.getValue(Boolean.class)) {
                            if (access == null) {
                                access = new LineWithSwitch(ChngProfile.this, strings[lang][22], "dost.png");
                                access.setY(sound.getY() + 214 * scale);
                                access.swtch.on();
                                access.swtch.turnListener = new OnTurnListener() {
                                    @Override
                                    public void onTurn(boolean state) {
                                        FirebaseDatabase.getInstance().getReference("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("access").setValue(state);
                                        super.onTurn(state);
                                    }
                                };
                                FirebaseDatabase.getInstance().getReference("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("access").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            if (dataSnapshot.getValue(Boolean.class)) {
                                                access.swtch.on();
                                            } else {
                                                access.swtch.off();
                                            }
                                        } else {
                                            FirebaseDatabase.getInstance().getReference("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("access").setValue(true);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            layout.addView(access);
                            delete.setY(access.getY() + 274 * scale);
                        } else {
                            if (access != null) {
                                layout.removeView(access);
                                delete.setY(sound.getY() + 274 * scale);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        layout.addView(wory);
        layout.addView(sound);
        delete.setY(sound.getY() + 274*scale);

        surnameEdt = new EditText(this);
        surnameEdt.setCursorVisible(false);
        surnameEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    closeKeyboard(surnameEdt);
                    handled = true;
                }
                return handled;
            }
        });
        surnameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changedSurname = !s.toString().equals(surnameWas);
                checkSaveBtn();
            }

        });
        layout.addView(surnameEdt);
        FirebaseDatabase.getInstance().getReference("usersInfo").child(account.getUid()).child("surname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                surnameWas = dataSnapshot.getValue(String.class);
                surnameEdt.setText(dataSnapshot.getValue(String.class));

                surnameEdt.setInputType(InputType.TYPE_CLASS_TEXT);
                surnameEdt.setBackgroundColor(Color.TRANSPARENT);



                surnameEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
                surnameEdt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 51 * scale);
                surnameEdt.setTextColor(ResourseColors.colorTextSubmenu);
                surnameEdt.setTypeface(type);
                // phoneNumber.measure(0, 0);       //must call measure!
                // width = phoneNumber.getMeasuredWidth();
                surnameEdt.setX(42 * scale);
                surnameEdt.setY(SurNameTxt.getY()+45 * scale);
                surnameEdt.setLayoutParams(new FrameLayout.LayoutParams((int)(250*scale),(int)(120* scale)));
                //nameEdt.setBackgroundColor(ResourseColors.colorBlack);
                surnameEdt.setSelection(surnameEdt.getText().toString().length());
                surnameEdt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.toString().length()>2){
                            surnameEdt.setTextColor(ResourseColors.colorTextSubmenu);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("city").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> towns = new ArrayList<>();
                ArrayList<Long> numbers = new ArrayList<>();
                if(dataSnapshot!=null){
                    Map<Object,Object> cities = ((Map<Object,Object>)dataSnapshot.getValue());
                    for (final Map.Entry<Object, Object> entry : cities.entrySet())
                    {
                        String town = null;
                        if(lang==0) {
                            town = (String) (((Map<Object, Object>) entry.getValue()).get("nameEng"));
                        }
                        if(lang==1) {
                            town = (String) (((Map<Object, Object>) entry.getValue()).get("nameRus"));
                        }
                        if(town!=null){
                            towns.add(town);
                            numbers.add(Long.parseLong((String) entry.getKey()));
                        }
                    }
                }

                String[] cityArray = new String[towns.size()];
                for(int i=0;i<cityArray.length;i++){
                    cityArray[(numbers.get(i).intValue()-1)] = towns.get(i);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        saveChanges = new CustomPublishBtn(this, ResourseColors.colorLightGray, strings[lang][4]);
        saveChanges.setX(0);
        saveChanges.setY(dm.heightPixels - 141 * scale);
        saveChanges.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (141 * scale)));
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveChanges.p.getColor()==ResourseColors.colorGreen) {
                    if (nameEdt.length() <= 2) {
                        nameEdt.setTextColor(Color.RED);
                    }
                    if (surnameEdt.length() <= 2) {
                        surnameEdt.setTextColor(Color.RED);
                    }
                    if (surnameEdt.length() > 2 & nameEdt.length() > 2) {
                        dialog.show();
                        FirebaseDatabase.getInstance().getReference().child("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("name").setValue(nameEdt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                nameReady = true;
                                if (photoReady) {
                                    dialog.dismiss();
                                    onBackPressed();
                                }
                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("surname").setValue(surnameEdt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                surnamenReady = true;
                                if (photoReady) {
                                    dialog.dismiss();
                                    onBackPressed();
                                }
                            }
                        });

                        uploadImage(newPhoto);
                    }
                }
            }
        });


    }

    private void checkSaveBtn() {
        if(changedName|changedSurname|changedPhoto){
            saveChanges.p.setColor(ResourseColors.colorGreen);
            mainLayout.removeView(saveChanges);
            mainLayout.addView(saveChanges);
            sclView.setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(dm.heightPixels-600*scale-141*scale)));
            saveChanges.invalidate();
        }else{
            saveChanges.p.setColor(ResourseColors.colorLightGray);
            mainLayout.removeView(saveChanges);
            sclView.setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(dm.heightPixels-600*scale)));
            saveChanges.invalidate();
        }
    }

    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    private void makeCallback() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("ok", "onVerificationCompleted:" + credential);
                dialogDel.show();
                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference().child("usersInfo").child(FirebaseAuth.getInstance().getUid()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).removeValue();
                                    FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("del", "User account deleted.");
                                                Toast.makeText(ChngProfile.this, strings[lang][18], Toast.LENGTH_LONG).show();
                                                dialogDel.dismiss();
                                                Intent intent = new Intent(ChngProfile.this, SplashScreenActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }
                                dialog2.dismiss();
                            }
                        });
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("fail", "onVerificationFailed", e);
                Toast.makeText(ChngProfile.this,"no",Toast.LENGTH_LONG).show();
                dialog.hide();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(ChngProfile.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(ChngProfile.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    // The SMS quota for the project has been exceeded
                    // ...
                }
                dialog2.dismiss();
                // Show a message and update the UIва
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                Intent intent = new Intent(ChngProfile.this, PhCodeActivity.class);
                intent.putExtra("lang", lang);
                intent.putExtra("verif", verificationId);
                intent.putExtra("isDelete", true);
                dialog2.dismiss();
                ChngProfile.this.startActivity(intent);

                // ...
            }
        };
    }

    boolean nameReady=false, surnamenReady=false, townReady=false, photoReady=false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (ImagePicker.getImageFromResult(ChngProfile.this, resultCode, data) != null) {
                newPhoto = ImagePicker.getImageFromResult(ChngProfile.this, resultCode, data);
                Bitmap resizedPhoto;
                if(newPhoto.getWidth()>newPhoto.getHeight()) {
                    newPhoto = RotateBitmap(newPhoto,-90);
                }
                if(newPhoto.getWidth()<newPhoto.getHeight()) {
                    resizedPhoto = Bitmap.createBitmap(newPhoto, 3, 3, newPhoto.getWidth() - 3, newPhoto.getWidth() - 3);
                }else{
                    resizedPhoto = Bitmap.createBitmap(newPhoto, 3, 3, newPhoto.getHeight() - 3, newPhoto.getHeight() - 3);
                }
                newPhoto = Bitmap.createScaledBitmap(resizedPhoto, 220, 220, false);
                photo.setImageBitmap(newPhoto);
                changedPhoto = true;
                checkSaveBtn();
                //  photo.setImageBitmap(ImagePicker.getImageFromResult(ChngProfile.this, resultCode, data));
                // photo.setImageBitmap(resizedPhoto);
            } else {
                Toast.makeText(this, strings[lang][6], Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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

    final FirebaseUser[] user = new FirebaseUser[1];

    private void uploadImage(Bitmap picture) {

        if (picture != null) {

            filePath = FirebaseAuth.getInstance().getCurrentUser().getUid() + "avatar";
            final StorageReference ref = storageReference.child("images/" + filePath);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ref.putBytes(byteArray)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.child("images/" + filePath).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    FirebaseDatabase.getInstance().getReference().child("usersInfo").child(FirebaseAuth.getInstance().getUid()).child("photoURL").setValue(task.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            photoReady = true;
                                            dialog.dismiss();
                                            if(nameReady&surnamenReady&townReady&photoReady){
                                                dialog.dismiss();
                                                onBackPressed();
                                            }
                                        }
                                    });
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(ChngProfile.this, strings[lang][9] + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            dialog.setMessage(strings[lang][8] + (int) progress + "%");
                        }
                    });

            picture.recycle();
        } else {
            // Toast.makeText(this, "null", Toast.LENGTH_LONG).show();
            photoReady = true;
            if(nameReady&surnamenReady&townReady&photoReady){
                dialog.dismiss();
                onBackPressed();
            }
        }
    }

    AlertDialog noInternet;

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println(intent.getAction());
            Log.d("app", "Network connectivity change" + intent.getAction());
            if (noInternet == null) {
                noInternet = new AlertDialog.Builder(ChngProfile.this).create();
                noInternet.setMessage(strings[lang][11]);
                noInternet.setCanceledOnTouchOutside(false);
                noInternet.setCancelable(false);
            }
            if (!isOnline()) {
                noInternet.show();
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
