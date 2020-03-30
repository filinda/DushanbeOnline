package beka.com.bk.dushanbeonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import beka.com.bk.dushanbeonline.custom_views.CustomSpiner2;
import beka.com.bk.dushanbeonline.custom_views.CustomSpiner3;
import beka.com.bk.dushanbeonline.custom_views.EdtTxtCustom;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;

public class RegistrationActivity extends AppCompatActivity {

    FrameLayout layout;
    DisplayMetrics dm;
    Typeface type;
    TopLabel label;
    TextView[] tx;
    String[][] strings;
    int lang = 1;
    float scale;
    EdtTxtCustom phoneEdt, nameEdt, surnameEdt;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String TAG = "phoneRegist ";
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth mAuth;
    ProgressDialog dialog;
    CustomSpiner3 genderSpiner;
    TextView login;
    float left2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        lang = getIntent().getIntExtra("lang", 1);

        mAuth = FirebaseAuth.getInstance();

        layout = new FrameLayout(this);
        setContentView(layout);

        strings = new String[2][20];
        strings[0][0] = "Registration";
        strings[1][0] = "Регистрация";
        strings[0][1] = "Phone number";
        strings[1][1] = "Номер телефона";
        strings[0][2] = "Name";
        strings[1][2] = "Имя";
        strings[0][3] = "Surname";
        strings[1][3] = "Фамилия";
        strings[0][4] = "Gender";
        strings[1][4] = "Пол";
        strings[0][5] = "City";
        strings[1][5] = "Город";
        strings[0][6] = "Male";
        strings[1][6] = "Мужской";
        strings[0][7] = "Female";
        strings[1][7] = "Женский";
        strings[0][8] = "Dushanbe";
        strings[1][8] = "Душанбе";
        strings[0][9] = "Khudzhand";
        strings[1][9] = "Худжанд";
        strings[0][10] = "Kulyab";
        strings[1][10] = "Куляб";
        strings[0][11] = "Register";
        strings[1][11] = "Зарегистрировать";
        strings[0][12] = "Invalid phone number";
        strings[1][12] = "Неверный телефонный номер";
        strings[0][13] = "The name must consist of at least 3 letters";
        strings[1][13] = "Имя должно состоять минимум из 3 букв";
        strings[0][14] = "Input phone number";
        strings[1][14] = "Введите номер телефона";
        strings[0][15] = "This phone number already registered";
        strings[1][15] = "Этот телефонный номер уже зарегистрирован";
        strings[0][16] = "Please wait";
        strings[1][16] = "Пожалуйста подождите";
        strings[0][17] = "Login";
        strings[1][17] = "Войти";
        strings[0][18] = "if you have an account";
        strings[1][18] = "если есть уже аккаунт";
        strings[0][19] = "The surname must consist of at least 2 letters";
        strings[1][19] = "Фамилия должна состоять минимум из 2 букв";

        dialog = new ProgressDialog(this);
        dialog.setMessage(strings[lang][16]);

        dm = getResources().getDisplayMetrics();
        scale = dm.widthPixels / 1080f;
        type = Typeface.createFromAsset(getAssets(), "font.ttf");

        makeCallback();

        design();
    }

    private void design() {

        label = new TopLabel(this, strings[lang][0], scale);
        layout.addView(label);
        label.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tx = new TextView[4];
        float top = 226 * scale, left = 48 * scale, delta = 255 * scale;
        left2 = 38 * scale;
        for (int i = 0; i < tx.length; i++) {
            tx[i] = new TextView(this);
            tx[i].setTypeface(type);
            tx[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * scale);
            tx[i].setText(strings[lang][i + 1]);
            tx[i].setX(left);
            tx[i].setY(top + i * delta);
            tx[i].setTextColor(ResourseColors.colorBlue);
            layout.addView(tx[i]);
        }

        phoneEdt = new EdtTxtCustom(this, "");
        phoneEdt.setY(tx[0].getY() + 60 * scale);
        phoneEdt.setX(left2);
        phoneEdt.tb.setInputType(InputType.TYPE_CLASS_NUMBER);
        phoneEdt.tb.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        phoneEdt.tb.setX(120 * scale);
        TextView countryCode = new TextView(this);
        countryCode.setText("+992");
        countryCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        countryCode.setTypeface(type);
        countryCode.setX(left);
        countryCode.setY(26 * scale);
        countryCode.setTextColor(ResourseColors.colorBlue);
        phoneEdt.addView(countryCode);
        phoneEdt.setRequestedAmountOfCharsMin(9);
        phoneEdt.setError(strings[lang][12]);
        layout.addView(phoneEdt);

        nameEdt = new EdtTxtCustom(this, strings[lang][2]);
        nameEdt.setY(tx[1].getY() + 60 * scale);
        nameEdt.setX(left2);
        nameEdt.tb.setFilters(new InputFilter[]{new InputFilter.LengthFilter(36)});
        nameEdt.setError(strings[lang][12]);
        nameEdt.setRequestedAmountOfCharsMin(3);
        layout.addView(nameEdt);

        surnameEdt = new EdtTxtCustom(this, strings[lang][3]);
        surnameEdt.setY(tx[2].getY() + 60 * scale);
        surnameEdt.setX(left2);
        surnameEdt.tb.setFilters(new InputFilter[]{new InputFilter.LengthFilter(36)});
        surnameEdt.setError(strings[lang][19]);
        surnameEdt.setRequestedAmountOfCharsMin(2);
        layout.addView(surnameEdt);

        String[] genderArray = new String[2];
        genderArray[0] = strings[lang][6];
        genderArray[1] = strings[lang][7];

        genderSpiner = new CustomSpiner3(this, genderArray,2,(int)(1005*scale));
        genderSpiner.setY(tx[3].getY() + 56 * scale);
        genderSpiner.setX(left2);


        CustomPublishBtn readybtn = new CustomPublishBtn(this, ResourseColors.colorGreen, strings[lang][11]);
        readybtn.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (150 * scale)));
        readybtn.setY(dm.heightPixels - 150 * scale);

        readybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryRegister();
            }
        });


        login = new TextView(this);
        login.setText(strings[lang][17]);
        login.setTextSize(TypedValue.COMPLEX_UNIT_PX, 55 * scale);
        login.setTypeface(Typeface.createFromAsset(getAssets(), "font_medium.ttf"));
        SpannableString content = new SpannableString(strings[lang][17]);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        login.setText(content);
        login.setTextColor(ResourseColors.colorTextSubmenu);
        login.setY(((genderSpiner.getY() + 120*scale + (dm.heightPixels-150*scale))/2) - 100*scale);
        login.measure(0, 0);
        login.setX(dm.widthPixels / 2 - login.getMeasuredWidth() / 2);
        layout.addView(login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(RegistrationActivity.this, SignInActivity.class);
                intent1.putExtra("lang", lang);
                startActivity(intent1);
            }
        });
        login.setLayoutParams(new FrameLayout.LayoutParams( (login.getMeasuredWidth()), (int) (100 * scale)));

        TextView loginInfo = new TextView(this);
        loginInfo.setText(strings[lang][18]);
        loginInfo.setTypeface(type);
        loginInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35 * scale);
        loginInfo.measure(0, 0);
        loginInfo.setX(dm.widthPixels / 2 - loginInfo.getMeasuredWidth() / 2);
        loginInfo.setY(login.getY()+90*scale);
        loginInfo.setTextColor(ResourseColors.colorBlue);
        layout.addView(loginInfo);
        layout.addView(genderSpiner);
        layout.addView(readybtn);
    }


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
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                dialog.hide();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(RegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(RegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UIва
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                Intent intent = new Intent(RegistrationActivity.this, PhCodeActivity.class);
                intent.putExtra("lang", lang);
                intent.putExtra("verif", verificationId);
                intent.putExtra("name", "" + nameEdt.tb.getText().toString());
                intent.putExtra("surname", "" + surnameEdt.tb.getText().toString());
                intent.putExtra("gender", "" + genderSpiner.items[genderSpiner.getSelectedItem()]);

                RegistrationActivity.this.startActivity(intent);

                // ...
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            Intent startServiceIntent = new Intent(RegistrationActivity.this, NotificationService.class);
                            startServiceIntent.putExtra("lang",lang);
                            startService(startServiceIntent);

                            FirebaseUser user = task.getResult().getUser();

                            FirebaseDatabase.getInstance().getReference("users").child("" + user.getUid()).child("telNum").setValue(user.getPhoneNumber());
                            FirebaseDatabase.getInstance().getReference("usersInfo").child("" + user.getUid()).child("phone").setValue(user.getPhoneNumber());
                            FirebaseDatabase.getInstance().getReference("usersInfo").child("" + user.getUid()).child("name").setValue(nameEdt.tb.getText().toString());
                            FirebaseDatabase.getInstance().getReference("usersInfo").child("" + user.getUid()).child("gender").setValue(genderSpiner.items[genderSpiner.getSelectedItem()]);
                            FirebaseDatabase.getInstance().getReference("usersInfo").child("" + user.getUid()).child("surname").setValue(surnameEdt.tb.getText().toString());
                            FirebaseDatabase.getInstance().getReference("tasks").child("" + user.getPhoneNumber().substring(4)).child("visible").setValue(true);
                            FirebaseDatabase.getInstance().getReference("usersInfo").child("" + user.getUid()).child("profiInfo").child("isProfi").setValue(false);

                            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("usersInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            userReference.child("eMail").setValue(user.getEmail());
                            if (user.getPhotoUrl() != null) {
                                userReference.child("photoURL").setValue(user.getPhotoUrl().toString());
                            } else {
                                userReference.child("photoURL").setValue("https://firebasestorage.googleapis.com/v0/b/dushanbeonline-fc7ed.appspot.com/o/defaultphoto.png?alt=media&token=60acef6f-70bc-4835-bd28-ba77d169f802");
                            }

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/dushanbeonline-fc7ed.appspot.com/o/defaultphoto.png?alt=media&token=60acef6f-70bc-4835-bd28-ba77d169f802"))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("firebase", "User profile updated.");
                                            }
                                        }
                                    });

                            Intent intent = new Intent(RegistrationActivity.this, MainMenuActivity.class);
                            intent.putExtra("lang", lang);
                            startActivity(intent);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void tryRegister() {
        if (phoneEdt.checkRequestFinal() & nameEdt.checkRequestFinal() & surnameEdt.checkRequestFinal()) {
            {
                checkNumber("+992" + phoneEdt.tb.getText().toString());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {

            Intent intent = new Intent(RegistrationActivity.this, MainMenuActivity.class);
            intent.putExtra("lang", lang);
            startActivity(intent);
        }
    }

    private void checkNumber(String phoneNumber) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.orderByChild("telNum").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //it means user already registered
                    //Add code to show your prompt
                    Toast.makeText(RegistrationActivity.this, strings[lang][14], Toast.LENGTH_LONG).show();
                    phoneEdt.tb.setTextColor(ResourseColors.colorRed);
                } else {
                    dialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+992" + phoneEdt.tb.getText().toString(),        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            RegistrationActivity.this,               // Activity (for callback binding)
                            mCallbacks);
                    //It is new users
                    //write an entry to your user table
                    //writeUserEntryToDB();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
