package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import beka.com.bk.dushanbeonline.ResourseColors;
import beka.com.bk.dushanbeonline.TaskEditorActivity;

public class SubCategoryBtn extends View implements View.OnClickListener {

    int categoryId, subCategoryId, subSubCategoryId;
    Context context;
    String label = "wait for data to load";
    Paint pText, pLine, pRect;
    Bitmap arrow;
    SubCategoryMenu root;
    int lang;
    float scale;
    ValueAnimator animator;
    int ofset = 0;
    int textWidth;

    public SubCategoryBtn(Context context, int categoryId, int subCategoryId, int lang, Bitmap arrow, SubCategoryMenu root) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        textWidth = (int)(900*scale);
        this.context = context;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        subSubCategoryId = -1;
        this.arrow = arrow;
        this.lang = lang;
        this.root = root;
        setOnClickListener(this);

        String langStr = "eng";
        if (lang == 1)
            langStr = "rus";

        FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child("" + subCategoryId).child(langStr + "Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                label = dataSnapshot.getValue(String.class);
                if (label == null) {
                    label = "not set in database";
                }
                setAnimator(label);
                invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                label = databaseError.getMessage();
                invalidate();
            }
        });

        pText = new Paint();
        pText.setColor(ResourseColors.colorTextSubmenu);
        pText.setAntiAlias(true);
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_reg.ttf"));
        pText.setTextSize((int) (48 * scale));

        pLine = new Paint();
        pLine.setColor(ResourseColors.colorLightGray2);
        pLine.setStrokeWidth(3);
        pLine.setAntiAlias(true);
        setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (183 * scale)));

        pRect = new Paint();
        pRect.setColor(ResourseColors.colorWhite);
    }

    public SubCategoryBtn(Context context, int categoryId, final int subCategoryId, final int subSubCategoryId, int lang, Bitmap arrow, SubCategoryMenu root) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        textWidth = (int)(900*scale);
        this.context = context;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.subSubCategoryId = subSubCategoryId;
        this.arrow = arrow;
        this.lang = lang;
        this.root = root;
        setOnClickListener(this);

        String langStr = "eng";
        if (lang == 1)
            langStr = "rus";

        FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child("" + subCategoryId).child(langStr + "Sub" + subSubCategoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                label = dataSnapshot.getValue(String.class);
                if (label == null) {
                    label = "not set in database";
                }
                setAnimator(label);
                invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                label = databaseError.getMessage();
                invalidate();
            }
        });

        pText = new Paint();
        pText.setColor(ResourseColors.colorTextSubmenu);
        pText.setAntiAlias(true);
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(), "roboto_reg.ttf"));
        pText.setTextSize((int) (48 * scale));

        pLine = new Paint();
        pLine.setColor(ResourseColors.colorLightGray2);
        pLine.setStrokeWidth(3);
        pLine.setAntiAlias(true);
        setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (166 * scale)));

        pRect = new Paint();
        pRect.setColor(ResourseColors.colorWhite);
    }

    private void setAnimator(String string){
        Rect bounds=new Rect();
        pText.getTextBounds(string,0,string.length(),bounds);
        if(bounds.width()-(textWidth-76)*scale>100){
            animator = new ValueAnimator();
            animator.setIntValues(0, (int)((textWidth-76)*scale-bounds.width())/3 ,(int)((textWidth-76)*scale-bounds.width())*2/3, (int)((textWidth-76)*scale-bounds.width()),0);
            animator.setDuration((int)(bounds.width()-(textWidth-76)*scale)*30);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ofset = (Integer) animation.getAnimatedValue();
                    invalidate();
            }
            });
        }else{
            animator=null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(animator==null) {
            canvas.drawText(label, (int) (76 * scale), (int) (92 * scale), pText);
        }else{
            canvas.drawText(label, (int) (76 * scale) + ofset, (int) (92 * scale), pText);
            canvas.drawRect(0,0,76*scale, getHeight(), pRect);
            canvas.drawRect(textWidth,0,getWidth(), getHeight(), pRect);
        }
        canvas.drawBitmap(arrow, (int) (1020 * scale), (int) (60 * scale), pText);
        canvas.drawLine(69 * scale, 164 * scale, 1085 * scale, 164 * scale, pLine);
        super.onDraw(canvas);
    }

    @Override
    public void onClick(View v) {
        if (subSubCategoryId < 0) {
            root.categoryChoosen(subSubCategoryId, subCategoryId);
        } else {
            Intent intent = new Intent(context, TaskEditorActivity.class);
            intent.putExtra("lang", lang);
            intent.putExtra("category", categoryId);
            intent.putExtra("subCategory", subCategoryId);
            intent.putExtra("subSubCategory", subSubCategoryId);
            context.startActivity(intent);
        }
        //startCreateTask
        //Intent intent = new Intent()
    }
}
