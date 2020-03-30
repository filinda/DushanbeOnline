package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import beka.com.bk.dushanbeonline.ResourseColors;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonNameView extends FrameLayout {

    CircleImageView photo;
    Paint pText;
    String name;
    ValueAnimator animator;
    String userId = "";
    int wigth, height;
    int offset = 0;
    float scale = 1;
    Bitmap buffer;
    View gradientView;

    public PersonNameView(@NonNull Context context, float scale) {
        super(context);
        this.scale = scale;
        height = (int) (200 * scale);
        wigth = (int) (1080 * scale);

        setWillNotDraw(false);
        setLayoutParams(new FrameLayout.LayoutParams(wigth, height));

        photo = new CircleImageView(context);
        photo.setX(30*scale);
        photo.setY(height / 2 - 20 * scale);
        photo.setLayoutParams(new FrameLayout.LayoutParams((int) (150 * scale), (int) (150 * scale)));
        addView(photo);

        pText = new Paint();
        pText.setColor(ResourseColors.colorBlack);
        pText.setTextSize(50 * scale);
        pText.setTypeface(Typeface.createFromAsset(context.getAssets(), "font.ttf"));

        name = "name";
        buffer = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    public void destroy(){
        Glide.clear(photo);
    }

    public void setUserId(String userId) {
        this.userId = userId;
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("usersInfo");
        userReference.child(userId).child("photoURL").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(getContext().getApplicationContext()).load(dataSnapshot.getValue(String.class)).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(photo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userReference.child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setName(dataSnapshot.getValue(String.class));
                invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userReference.child(userId).child("surname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setSurName(dataSnapshot.getValue(String.class));
                invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void setName(String name) {
        this.name = name;
        invalidate();
    }

    String surname = "";

    public void setSurName(String surname) {
        this.surname = surname;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(name, 220*scale, 192*scale, pText);
        canvas.drawText(surname, 220*scale, 262*scale, pText);
    }
}
