package beka.com.bk.dushanbeonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import beka.com.bk.dushanbeonline.custom_views.CustomEdtTxtCode;
import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;

public class PhCodeActivity extends AppCompatActivity {

    FrameLayout layout;
    DisplayMetrics dm;
    Typeface type;
    TopLabel label;
    String[][] strings;
    int lang = 1;
    float scale;
    TextView textInfo, txtEdtInfo;
    CustomEdtTxtCode codeEdt;
    String TAG = "phone";
    String verif, code, name, gender, surname;
    int town = 1;
    PhoneAuthCredential credential;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    CustomPublishBtn readybtn;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        lang = getIntent().getIntExtra("lang", 1);
        verif = getIntent().getStringExtra("verif");
        name = getIntent().getStringExtra("name");
        town = getIntent().getIntExtra("town", 1);
        gender = getIntent().getStringExtra("gender");
        code = getIntent().getStringExtra("code");
        surname = getIntent().getStringExtra("surname");
        // credential = getIntent().getExtra();


        layout = new FrameLayout(this);
        setContentView(layout);

        strings = new String[2][10];
        strings[0][0] = "Сode Confirmation";
        strings[1][0] = "Подтверждение";
        strings[0][1] = "Next";
        strings[1][1] = "Продолжить";
        strings[0][2] = "Confirmation code";
        strings[1][2] = "Код подтверждения";
        strings[0][3] = "To complete the registration, enter\nthe confirmation code that we sent\nto the number ";
        strings[1][3] = "Чтобы завершить регистрацию, введите\nкод подтверждения, который мы выслали\nна номер ";
        strings[0][4] = "<line>To complete account<line/> <font color='#EE0000'>deleting</font><line>, enter\nthe confirmation code that we sent\nto the number<line/>";
        strings[1][4] = "<line>Чтобы завершить <line/> <font color='#EE0000'>удаление вашего аккаунта</font><line>, введите\nкод подтверждения, который мы выслали\nна номер <line/>";
        strings[0][5] = "Your account deleted";
        strings[1][5] = "Ваша учётная запись удалена";
        strings[0][6] = "<line>To complete authentication, enter\nthe confirmation code that we sent\nto the number<line/>";
        strings[1][6] = "<line>Чтобы завершить аутентификацию, введите\nкод подтверждения, который мы выслали\nна номер <line/>";
        strings[0][7] = "Deleting...";
        strings[1][7] = "Удаление...";
        strings[0][8] = "Wrong code";
        strings[1][8] = "Неправильный код";
        strings[0][9] = "Try later";
        strings[1][9] = "Попробуйте позже";


        dm = getResources().getDisplayMetrics();
        scale = dm.widthPixels / 1080f;
        type = Typeface.createFromAsset(getAssets(), "font.ttf");

        design();

      /*  if(code!=null){
           codeEdt.tb.setText(code);
        }*/
        readybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verif != null) {
                    credential = PhoneAuthProvider.getCredential(verif, codeEdt.tb.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                } else {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        Intent intent = new Intent(PhCodeActivity.this, MainMenuActivity.class);
                        intent.putExtra("lang", lang);
                        startActivity(intent);
                    }
                }
            }
        });

        if(getIntent().getBooleanExtra("isDelete",false)){
            dialog = new ProgressDialog(this);
            dialog.setMessage(strings[lang][7]);
            textInfo.setText(Html.fromHtml(strings[lang][4]));
            readybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (verif != null) {
                        credential = PhoneAuthProvider.getCredential(verif, codeEdt.tb.getText().toString());
                        deleteWithPhoneAuthCredential(credential);
                    } else {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            Intent intent = new Intent(PhCodeActivity.this, MainMenuActivity.class);
                            intent.putExtra("lang", lang);
                            startActivity(intent);
                        }
                    }
                }
            });
        }

        if(getIntent().getBooleanExtra("SignIn",false)){
            textInfo.setText(Html.fromHtml(strings[lang][6]));
        }

    }

    private void deleteWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if(credential!=null) {
            FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                dialog.show();
                                System.out.println("abab DELETE "+task.isSuccessful());
                                FirebaseDatabase.getInstance().getReference().child("usersInfo").child(FirebaseAuth.getInstance().getUid()).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("tasks").child("AmountOfTasks"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).removeValue();
                                FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("del", "User account deleted.");
                                            dialog.dismiss();
                                            Toast.makeText(PhCodeActivity.this, strings[lang][5], Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(PhCodeActivity.this, SplashScreenActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }else{
                                System.out.println("abab DELETE "+task.isSuccessful());
                                Toast.makeText(PhCodeActivity.this,strings[lang][8],Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }
        else{
            Toast.makeText(PhCodeActivity.this, strings[lang][8], Toast.LENGTH_LONG).show();
        }
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            Intent startServiceIntent = new Intent(PhCodeActivity.this, NotificationService.class);
                            startServiceIntent.putExtra("lang",lang);
                            startService(startServiceIntent);

                            FirebaseUser user = task.getResult().getUser();

                            if (name != null) {

                                FirebaseDatabase.getInstance().getReference("users").child("" + user.getUid()).child("telNum").setValue(user.getPhoneNumber());
                                FirebaseDatabase.getInstance().getReference("usersInfo").child("" + user.getUid()).child("phone").setValue(user.getPhoneNumber());
                                FirebaseDatabase.getInstance().getReference("usersInfo").child("" + user.getUid()).child("name").setValue(name);
                                FirebaseDatabase.getInstance().getReference("usersInfo").child("" + user.getUid()).child("town").setValue(town);
                                FirebaseDatabase.getInstance().getReference("usersInfo").child("" + user.getUid()).child("gender").setValue(gender);
                                FirebaseDatabase.getInstance().getReference("usersInfo").child("" + user.getUid()).child("surname").setValue(surname);
                                FirebaseDatabase.getInstance().getReference("tasks").child("" + user.getPhoneNumber().substring(4)).child("visible").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue()==null){
                                            FirebaseDatabase.getInstance().getReference("tasks").child("" + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).child("visible").setValue(true);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                FirebaseDatabase.getInstance().getReference("profiInfo").child("" + user.getUid()).child("isProfi").setValue(false);

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
                            }

                            Intent intent = new Intent(PhCodeActivity.this, MainMenuActivity.class);
                            intent.putExtra("lang", lang);
                            startActivity(intent);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(PhCodeActivity.this, strings[lang][9], Toast.LENGTH_LONG).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
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

        textInfo = new TextView(this);
        textInfo.setText(strings[lang][3]);
        textInfo.setWidth((int)(900*scale));
        textInfo.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        textInfo.setTextColor(ResourseColors.colorBlack);
        textInfo.setTypeface(type);
        textInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        textInfo.setY(269 * scale);
        layout.addView(textInfo);

        txtEdtInfo = new TextView(this);
        txtEdtInfo.setText(strings[lang][2]);
        txtEdtInfo.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        txtEdtInfo.setTextColor(ResourseColors.colorBlue);
        txtEdtInfo.setTypeface(type);
        txtEdtInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, 39 * scale);
        txtEdtInfo.setY(507 * scale);
        layout.addView(txtEdtInfo);

        codeEdt = new CustomEdtTxtCode(this);
        codeEdt.setY(573 * scale);
        codeEdt.tb.setLimit(6);
        codeEdt.tb.setTextSize2(57 * scale);
        codeEdt.setX(dm.widthPixels / 2 - 270.5f * scale);
        codeEdt.tb.paddingy = 6 * scale;
        //codeEdt.tb.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        codeEdt.tb.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(codeEdt);
        final View parent = (View) codeEdt.getParent();  // button: the view you want to enlarge hit area
        parent.post( new Runnable() {
            public void run() {
                final Rect rect = new Rect();
                codeEdt.getHitRect(rect);
                rect.top -= 100*scale;    // increase top hit area
                rect.left -= 500*scale;   // increase left hit area
                rect.bottom += 100*scale; // increase bottom hit area
                rect.right += 100*scale;  // increase right hit area
                parent.setTouchDelegate( new TouchDelegate( rect , codeEdt));
            }
        });


        readybtn = new CustomPublishBtn(this, ResourseColors.colorGreen, strings[lang][1]);
        readybtn.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (150 * scale)));
        readybtn.setY(dm.heightPixels - 150 * scale);
        layout.addView(readybtn);

        readybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhCodeActivity.this, MainMenuActivity.class);
                intent.putExtra("lang",lang);
                startActivity(intent);
            }
        });

    }


}
