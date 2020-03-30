package beka.com.bk.dushanbeonline.custom_views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ProfiPage;
import beka.com.bk.dushanbeonline.ResourseColors;
import beka.com.bk.dushanbeonline.TaskInfoActivity;

public class CustomPopUp extends FrameLayout {

    float scale;
    FrameLayout whiteBack;
    EditText edt;
    CustomRoundButton btnY;
    TextView label;
    String[] shortAnswers;
    CustomRoundButton[] answersBtns;
    DisplayMetrics dm;
    int backWidth, backHeight;
    String profiUid;
    String taskId;
    String taskPhone;
    View line;
    String[][] strings;
    ImageView closeBtn;

    public CustomPopUp(@NonNull Context context, String message, String[] answers, final String profiUid, final String taskId, final String taskPhone, final int lang) {
        super(context);
        dm = context.getResources().getDisplayMetrics();
        scale = dm.widthPixels/1080f;
        this.shortAnswers=answers;
        this.profiUid = profiUid;
        this.taskId = taskId;
        this.taskPhone = taskPhone;
        backHeight = (int)(1400*scale);
        backWidth = (int)(800*scale);

        strings = new String[2][2];
        strings[0][0] = "Thanks for answer. This worker wont be abel to see your this task anymore";
        strings[1][0] = "Спасибо за ответ. Данный исполнитель больше не увидет это задание";
        strings[0][1] = "ok";
        strings[1][1] = "ok";

        setLayoutParams(new LayoutParams(dm.widthPixels,dm.heightPixels));
        setBackgroundColor(Color.argb(200,100,100,100));

        whiteBack = new FrameLayout(context);
        whiteBack.setLayoutParams(new LayoutParams(backWidth,backHeight));
        whiteBack.setY(dm.heightPixels/2-backHeight/2);
        whiteBack.setX(dm.widthPixels/2-backWidth/2);
        whiteBack.setBackgroundColor(ResourseColors.colorWhite);
        addView(whiteBack);

        label = new TextView(context);
        label.setText(message);
        label.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX,40*scale);
        label.setGravity(Gravity.CENTER_HORIZONTAL);
        label.setTextColor(ResourseColors.colorBlack);
        label.setY(100*scale);
        whiteBack.addView(label);

        answersBtns = new CustomRoundButton[answers.length];
        for(int i = 0; i<answersBtns.length;i++){
            answersBtns[i] = new CustomRoundButton(context){
                public void press(){
                    FirebaseDatabase.getInstance().getReference("tasks").child(taskPhone).child(taskId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            FirebaseDatabase.getInstance().getReference("usersInfo")
                                    .child(profiUid)
                                    .child("shortAnswers")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .child(dataSnapshot.getValue(String.class))
                                    .setValue(text);

                            FirebaseDatabase.getInstance().getReference("tasks").child(taskPhone).child(taskId).child("profies").child(profiUid).setValue(-2);

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Yes button clicked
                                            Intent intent = new Intent(getContext(), TaskInfoActivity.class);
                                            intent.putExtra("lang", lang);
                                            intent.putExtra("id", taskId);
                                            intent.putExtra("phone", taskPhone);
                                            intent.putExtra("unreturnable", true);
                                            getContext().startActivity(intent);
                                            ((FrameLayout)CustomPopUp.this.getParent()).removeView(CustomPopUp.this);

                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage(strings[lang][0]).setPositiveButton(strings[lang][1], dialogClickListener).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            };
            answersBtns[i].setBackGround1(ResourseColors.colorBlue);
            answersBtns[i].setBackGround2(ResourseColors.colorGreen);
            answersBtns[i].pText.setColor(ResourseColors.colorWhite);
            answersBtns[i].rounding = 30*scale;
            answersBtns[i].setText(answers[i],true);
            answersBtns[i].setX(backWidth/2-answersBtns[i].getLayoutParams().width/2);
            answersBtns[i].setY(300*scale+i*130*scale);
            whiteBack.addView(answersBtns[i]);

            closeBtn = new ImageView(getContext());
            Bitmap bp = MainMenuActivity.getBitmapFromAsset(getContext(),"icon_close.png");
            bp = Bitmap.createScaledBitmap(bp,(int)(57*scale),(int)(57*scale),false);
            closeBtn.setImageBitmap(bp);
            closeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FrameLayout)CustomPopUp.this.getParent()).removeView(CustomPopUp.this);
                }
            });
            closeBtn.setScaleType(ImageView.ScaleType.MATRIX);
            closeBtn.setX(backWidth-bp.getWidth()-bp.getWidth()/3);
            closeBtn.setY(bp.getWidth()/3);
            closeBtn.setLayoutParams(new LayoutParams((int)(57*scale),(int)(57*scale)));
            whiteBack.addView(closeBtn);
        }
/*
        line = new View(context);
        line.setBackgroundColor(ResourseColors.colorVeryLightGray);
        line.setLayoutParams(new LayoutParams(backWidth*5/6,(int)(2*scale)));
        line.setY(720*scale);
        line.setX(backWidth/2 - backWidth*5/12);
        whiteBack.addView(line);

        edt = new EditText(context);
        edt.setY(800*scale);
        edt.setTextSize(40*scale);
        edt.setTextColor(ResourseColors.colorGray);
        edt.setLayoutParams(new LayoutParams(backWidth,(int)(300*scale)));
        whiteBack.addView(edt);*/
    }


/*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;//super.dispatchTouchEvent(ev);
    }
*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;//super.onTouchEvent(event);
    }
}
