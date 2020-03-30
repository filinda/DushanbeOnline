package beka.com.bk.dushanbeonline.custom_views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.MyTasksActivity;
import beka.com.bk.dushanbeonline.ResourseColors;
import beka.com.bk.dushanbeonline.WorkInProgressActivity;


public class RatingPopUp extends FrameLayout {
    float scale;
    FrameLayout whiteBack;
    EditText edt;
    CustomRoundButton btnY;
    TextView label, label2;
    DisplayMetrics dm;
    int backWidth, backHeight;
    String ratingUid;
    String[][] strings;
    ClicableRatingView ratingView;
    View header;
    TextView topText;
    View[] lines;
    CustomRoundButton publish;
    int lang;
    ImageView closeBtn;
    String taskName, taskPhone, taskId;
    boolean isProfi;

    public RatingPopUp(@NonNull Context context, final String ratingUid, boolean isProfi, String taskName, String taskId, String taskPhone, int lang) {
        super(context);
        this.lang = lang;
        this.taskName = taskName;
        this.taskId = taskId;
        this.taskPhone = taskPhone;
        this.isProfi = isProfi;
        dm = context.getResources().getDisplayMetrics();
        scale = dm.widthPixels/1080f;
        this.ratingUid = ratingUid;
        backHeight = (int)(1400*scale);
        backWidth = (int)(800*scale);

        strings = new String[2][8];
        strings[0][0] = "Thanks for answer. This worker wont be abel to see your this task anymore";
        strings[1][0] = "Спасибо за ответ. Данный исполнитель больше не увидет это задание";
        strings[0][1] = "ok";
        strings[1][1] = "ok";
        if(isProfi) {
            strings[0][2] = "Rate performer";
            strings[1][2] = "Оцените исполнителя";
        }else{
            strings[0][2] = "Rate customer";
            strings[1][2] = "Оцените заказчика";
        }
        strings[0][3] = "Task complete";
        strings[1][3] = "Задание завершено";
        strings[0][4] = "Leave a comment";
        strings[1][4] = "Оставьте отзыв";
        strings[0][5] = "Complete";
        strings[1][5] = "Завершить";
        strings[0][6] = "Please, leave comment longer than 10 characters";
        strings[1][6] = "Пожалуйста, оставьте коментарий больше чем на 10 символов";
        strings[0][7] = "Please, rate customer";
        strings[1][7] = "Пожалуйста, оцените заказчика";

        setLayoutParams(new LayoutParams(dm.widthPixels,dm.heightPixels));
        setBackgroundColor(Color.argb(200,100,100,100));

        whiteBack = new FrameLayout(context);
        whiteBack.setLayoutParams(new LayoutParams(backWidth,backHeight));
        whiteBack.setY(dm.heightPixels/2-backHeight/2- 60*scale);
        whiteBack.setX(dm.widthPixels/2-backWidth/2);
        whiteBack.setBackgroundColor(ResourseColors.colorWhite);
        addView(whiteBack);

        header = new View(getContext());
        header.setLayoutParams(new LayoutParams(backWidth,(int)(100*scale)));
        header.setBackgroundColor(ResourseColors.colorBlue);
        whiteBack.addView(header);
        
        topText = new TextView(context);
        topText.setText(strings[lang][3]);
        topText.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        topText.setTextSize(TypedValue.COMPLEX_UNIT_PX,57*scale);
        topText.setGravity(Gravity.CENTER_HORIZONTAL);
        topText.setTextColor(ResourseColors.colorWhite);
        topText.measure(0,0);
        topText.setY(40*scale-topText.getMeasuredHeight()/2);
        whiteBack.addView(topText);

        label = new TextView(context);
        label.setText(strings[lang][2]);
        label.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX,40*scale);
        label.setGravity(Gravity.CENTER_HORIZONTAL);
        label.setTextColor(ResourseColors.colorBlack);
        label.setY(170*scale);
        label.measure(0,0);
        whiteBack.addView(label);

        lines = new View[4];
        lines[0] = new View(getContext());
        lines[0].setBackgroundColor(ResourseColors.colorVeryLightGray);
        lines[0].setLayoutParams(new LayoutParams((int)(backWidth*0.75f-label.getMeasuredWidth()*1.25f)/2,(int)(3*scale)));
        lines[0].setX(backWidth*0.125f);
        lines[0].setY(label.getY()+label.getMeasuredHeight()/2);
        whiteBack.addView(lines[0]);

        lines[1] = new View(getContext());
        lines[1].setBackgroundColor(ResourseColors.colorVeryLightGray);
        lines[1].setLayoutParams(new LayoutParams((int)(backWidth*0.75f-label.getMeasuredWidth()*1.25f)/2,(int)(3*scale)));
        lines[1].setY(label.getY()+label.getMeasuredHeight()/2);
        lines[1].setX(backWidth*(1-0.175f)-(int)(backWidth*0.55f-label.getMeasuredWidth())/2);
        whiteBack.addView(lines[1]);

        ratingView = new ClicableRatingView(context);
        ratingView.setStars(-1);
        ratingView.setY(label.getY()+label.getMeasuredHeight()+80*scale);
        ratingView.setX(backWidth/2-ratingView.width/2);
        whiteBack.addView(ratingView);

        label2 = new TextView(context);
        label2.setText(strings[lang][4]);
        label2.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        label2.setTextSize(TypedValue.COMPLEX_UNIT_PX,40*scale);
        label2.setGravity(Gravity.CENTER_HORIZONTAL);
        label2.setTextColor(ResourseColors.colorBlack);
        label2.setY(514*scale);
        label2.measure(0,0);
        whiteBack.addView(label2);

        lines = new View[4];
        lines[2] = new View(getContext());
        lines[2].setBackgroundColor(ResourseColors.colorVeryLightGray);
        lines[2].setLayoutParams(new LayoutParams((int)(backWidth*0.75f-label2.getMeasuredWidth()*1.25f)/2,(int)(3*scale)));
        lines[2].setX(backWidth*0.125f);
        lines[2].setY(label2.getY()+label2.getMeasuredHeight()/2);
        whiteBack.addView(lines[2]);

        lines[3] = new View(getContext());
        lines[3].setBackgroundColor(ResourseColors.colorVeryLightGray);
        lines[3].setLayoutParams(new LayoutParams((int)(backWidth*0.75f-label2.getMeasuredWidth()*1.25f)/2,(int)(3*scale)));
        lines[3].setY(label2.getY()+label2.getMeasuredHeight()/2);
        lines[3].setX(backWidth*(1-0.175f)-(int)(backWidth*0.55f-label2.getMeasuredWidth())/2);
        whiteBack.addView(lines[3]);

        ShapeDrawable shape = new ShapeDrawable();
        float[] rad = new float[8];
        rad[0] = 20*scale;
        rad[1] = 20*scale;
        rad[2] = 20*scale;
        rad[3] = 20*scale;
        rad[4] = 20*scale;
        rad[5] = 20*scale;
        rad[6] = 20*scale;
        rad[7] = 20*scale;

        RectF rec = new RectF();
        rec.left = 2*scale;
        rec.top = 2*scale;
        rec.right = (int)(backWidth - 100*scale)-4*scale;
        rec.top = (int)(300*scale)-4*scale;
        RoundRectShape rectShape = new RoundRectShape(rad,null,null);
        shape.setShape(rectShape);
        shape.getPaint().setColor(ResourseColors.colorVeryVeryLightGray);
        //shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);

        edt = new EditText(context);
        edt.setY(650*scale);
        edt.setX(50*scale);
        edt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40*scale);
        edt.setTextColor(ResourseColors.colorGray);
        edt.setBackground(shape);
        edt.setLayoutParams(new LayoutParams((int)(backWidth - 100*scale),(int)(300*scale)));
        edt.setGravity(Gravity.TOP);
        edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edt.setPadding((int)(25*scale),(int)(25*scale),(int)(25*scale),(int)(25*scale));
        edt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(200) });
        whiteBack.addView(edt);

        publish = new CustomRoundButton(getContext()){
            @Override
            public void press() {
                super.press();
                publishComent();
            }

            @Override
            public void unPress() {
                super.unPress();
                publishComent();
            }
        };
        publish.setText(strings[lang][5],false);
        publish.setLayoutParams(new LayoutParams((int)(450*scale),(int)(150*scale)));
        publish.setX(backWidth/2-225*scale);
        publish.setY(edt.getY()+500*scale);
        publish.setBackGround1(ResourseColors.colorBlue);
        publish.setBackGround2(ResourseColors.colorBlue);
        publish.setTextColor(ResourseColors.colorWhite);
        publish.pText.setTextSize(50*scale);
        whiteBack.addView(publish);

        closeBtn = new ImageView(getContext());
        Bitmap bp = MainMenuActivity.getBitmapFromAsset(getContext(),"cross.png");
        bp = Bitmap.createScaledBitmap(bp,(int)(57*scale),(int)(57*scale),false);
        closeBtn.setImageBitmap(bp);
        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FrameLayout)RatingPopUp.this.getParent()).removeView(RatingPopUp.this);
            }
        });
        closeBtn.setScaleType(ImageView.ScaleType.MATRIX);
        closeBtn.setX(whiteBack.getX()+backWidth+bp.getWidth()/2);
        closeBtn.setY(whiteBack.getY()-bp.getHeight()/2);
        closeBtn.setLayoutParams(new LayoutParams((int)(57*scale),(int)(57*scale)));
        addView(closeBtn);
    }

    public void publishComent(){
        if(edt.getText().toString().length()<10){
            Toast.makeText(getContext(),strings[lang][6],Toast.LENGTH_LONG).show();
        }else {
            if (ratingView.getStars() < 0) {
                Toast.makeText(getContext(), strings[lang][7], Toast.LENGTH_LONG).show();
            }else{
                if(isProfi) {
                    FirebaseDatabase.getInstance().getReference("usersInfo").child(ratingUid).child("comments").child(taskName).child("rate").setValue(ratingView.getStars());
                    FirebaseDatabase.getInstance().getReference("usersInfo").child(ratingUid).child("comments").child(taskName).child("comment").setValue(edt.getText().toString());
                    FirebaseDatabase.getInstance().getReference("usersInfo").child(ratingUid).child("comments").child(taskName).child("commentatorUid").setValue(FirebaseAuth.getInstance().getUid());
                    FirebaseDatabase.getInstance().getReference("tasks").child(taskPhone).child(taskId).child("profies").child(ratingUid).setValue(3);
                }else{
                    FirebaseDatabase.getInstance().getReference("usersInfo").child(ratingUid).child("comments").child(taskName).child("rate").setValue(ratingView.getStars());
                    FirebaseDatabase.getInstance().getReference("usersInfo").child(ratingUid).child("comments").child(taskName).child("comment").setValue(edt.getText().toString());
                    FirebaseDatabase.getInstance().getReference("usersInfo").child(ratingUid).child("comments").child(taskName).child("commentatorUid").setValue(FirebaseAuth.getInstance().getUid());
                    FirebaseDatabase.getInstance().getReference("tasks").child(taskPhone).child(taskId).child("profies").child(FirebaseAuth.getInstance().getUid()).setValue(4);
                }
                if(!isProfi) {
                    Intent intent = new Intent(getContext(), WorkInProgressActivity.class);
                    intent.putExtra("lng", lang);
                    intent.putExtra("tab", 2);
                    getContext().startActivity(intent);
                }else{
                    Intent intent = new Intent(getContext(), MyTasksActivity.class);
                    intent.putExtra("lng", lang);
                    getContext().startActivity(intent);
                }
            }
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;//super.onTouchEvent(event);
    }
}
