package beka.com.bk.dushanbeonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import beka.com.bk.dushanbeonline.custom_views.EdtTxtCustom;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;

public class SignInActivity extends AppCompatActivity {

    FrameLayout layout;
    Typeface type;
    int lang = 1;
    DisplayMetrics dm;
    float scale;
    TextView textInfo, phoneNumb;
    String[][] strings;
    EdtTxtCustom codeEdt;
    TopLabel label;
    CustomPublishBtn readybtn;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String TAG = "phoneRegist ";
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth mAuth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        lang = getIntent().getIntExtra("lang", 1);

        layout = new FrameLayout(this);
        setContentView(layout);

        layout.setBackgroundColor(ResourseColors.colorWhite);
        type = Typeface.createFromAsset(getAssets(), "font.ttf");

        strings = new String[2][7];
        strings[0][0] = "Login";
        strings[1][0] = "Вход";
        strings[0][1] = "Phone number";
        strings[1][1] = "Номер телефона";
        strings[0][2] = "Please enter the phone number,\nby which you registered\nin our system";
        strings[1][2] = "Пожалуйста введите в поле\nтелефонный номер, по которому вы\nзарегистрированы в нашей системе";
        strings[0][3] = "Login";
        strings[1][3] = "Войти";
        strings[0][4] = "Please wait...";
        strings[1][4] = "Пожалуйста подождите...";
        strings[0][5] = "This number is not registered";
        strings[1][5] = "Этот номер не зарегистрирован";
        strings[0][6] = "Number is not correct";
        strings[1][6] = "Номер введён не правильно";

        dialog = new ProgressDialog(this);
        dialog.setMessage(strings[lang][4]);

        makeCallback();

        design();
    }

    private void design() {
        dm = getResources().getDisplayMetrics();
        scale = (float) dm.widthPixels / 1080.0f;

        label = new TopLabel(this, strings[lang][0], scale);
        layout.addView(label);
        label.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        textInfo = new TextView(this);
        textInfo.setText(strings[lang][2]);
        textInfo.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        textInfo.setTextColor(ResourseColors.colorBlack);
        textInfo.setTypeface(type);
        textInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        textInfo.setY(269 * scale);
        layout.addView(textInfo);


        phoneNumb = new TextView(this);
        phoneNumb.setText(strings[lang][1]);
        phoneNumb.setTextColor(ResourseColors.colorBlue);
        phoneNumb.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40 * scale);
        phoneNumb.setTypeface(type);
        phoneNumb.measure(0, 0);
        phoneNumb.setX(dm.widthPixels / 2 - phoneNumb.getMeasuredWidth() / 2);
        phoneNumb.setY(507 * scale);
        layout.addView(phoneNumb);

        TextView countryCode = new TextView(this);
        countryCode.setText("+992");
        countryCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        countryCode.setTypeface(type);
        countryCode.setX(310 * scale);
        countryCode.setY(601 * scale);
        countryCode.setTextColor(ResourseColors.colorBlue);
        layout.addView(countryCode);

        codeEdt = new EdtTxtCustom(this, "");
        codeEdt.setY(573 * scale);
        codeEdt.setX(dm.widthPixels / 2 - 270.5f * scale);
        codeEdt.tb.setLimit(9);
        codeEdt.tb.paddingy = 21 * scale;
        codeEdt.tb.setTextSize2(45 * scale);
        codeEdt.tb.setInputType(InputType.TYPE_CLASS_NUMBER);
        codeEdt.setLayoutParams(new FrameLayout.LayoutParams((int) (541 * scale), (int) (120 * scale)));
        codeEdt.tb.setX(120 * scale);
        codeEdt.setError(strings[lang][6]);
        codeEdt.setRequestedAmountOfCharsMin(9);
        layout.addView(codeEdt);

        readybtn = new CustomPublishBtn(this, ResourseColors.colorGreen, strings[lang][3]);
        readybtn.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (150 * scale)));
        readybtn.setY(dm.heightPixels - 150 * scale);
        layout.addView(readybtn);

        readybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });

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
                    Toast.makeText(SignInActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(SignInActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
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

                Intent intent = new Intent(SignInActivity.this, PhCodeActivity.class);
                intent.putExtra("lang", lang);
                intent.putExtra("verif", verificationId);
                intent.putExtra("SignIn", true);

                SignInActivity.this.startActivity(intent);

            }
        };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent startServiceIntent = new Intent(SignInActivity.this, NotificationService.class);
                            startServiceIntent.putExtra("lang",lang);
                            startService(startServiceIntent);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            Intent intent = new Intent(SignInActivity.this, MainMenuActivity.class);
                            intent.putExtra("lang", lang);
                            startActivity(intent);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void checkNumber(String phoneNumber) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.orderByChild("telNum").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //it means user already registered
                    //Add code to show your prompt
                    dialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+992" + codeEdt.tb.getText().toString(),        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            SignInActivity.this,               // Activity (for callback binding)
                            mCallbacks);
                } else {
                    Toast.makeText(SignInActivity.this, strings[lang][5], Toast.LENGTH_LONG).show();
                    codeEdt.p.setColor(ResourseColors.colorRed);
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

    private void tryLogin() {
        if (codeEdt.checkRequestFinal()) {
            {
                checkNumber("+992" + codeEdt.tb.getText().toString());
            }
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

}
