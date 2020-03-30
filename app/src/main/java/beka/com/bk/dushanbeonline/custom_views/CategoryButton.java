package beka.com.bk.dushanbeonline.custom_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

@SuppressLint("AppCompatCustomView")
public class CategoryButton extends Button {
    Bitmap bmp;
    String str;
    Paint p, p2, ptext;
    float scale;
    int color;
    ArrayList<String> subCategories;
    int categoryId;


    public CategoryButton(Context context, Bitmap bmp, String str, int color, float scale, int categoryId, int lang) {
        super(context);
        this.bmp = bmp;
        this.str = str;
        this.color = color;
        this.scale = scale;
        this.categoryId = categoryId;
        p = new Paint();
        p.setAntiAlias(true);
        p2 = new Paint();
        p2.setColor(Color.argb(150, 0, 0, 0));
        p2.setAntiAlias(true);
        ptext = new Paint();
        ptext.setColor(Color.WHITE);
        ptext.setAntiAlias(true);
        ptext.setTypeface(Typeface.createFromAsset(context.getAssets(), "font_medium.ttf"));
        ptext.setTextSize((int) (60 * scale));
        setOnTouchListener(new touchList());

        //get subcategories from firebase
        String langStr = "eng";
        if (lang == 1)
            langStr = "rus";
        FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(langStr + "Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CategoryButton.this.str = dataSnapshot.getValue(String.class);
                if (CategoryButton.this.str == null) {
                    CategoryButton.this.str = "not set in database";
                }
                invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                CategoryButton.this.str = databaseError.getMessage();
                invalidate();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawRect(0, 0, getWidth(), getHeight(), p);

        canvas.drawBitmap(bmp, 0, 0, new Paint());

        //  canvas.drawRect(0, 400 * scale, getWidth(), 300 * scale, p2);

        Rect bounds = new Rect();
        ptext.getTextBounds(str, 0, str.length(), bounds);
        int height = bounds.height();
        int width = bounds.width();

        canvas.drawText(str, getWidth() / 20, 368 * scale, ptext);
        super.onDraw(canvas);
    }

    private class touchList implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }
}
