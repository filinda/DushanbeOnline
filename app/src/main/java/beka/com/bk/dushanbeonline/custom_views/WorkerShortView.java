package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.ProfiPage;
import beka.com.bk.dushanbeonline.ResourseColors;
import de.hdodenhof.circleimageview.CircleImageView;

public class WorkerShortView extends FrameLayout {

    CircleImageView avatar;
    TextView name, surname;
    public float stars;
    float scale;
    public float height;
    public String id;
    Bitmap starFill, starEmpty;
    int starX, starY;
    DisplayMetrics dm;
    int lang = 1;

    public WorkerShortView(final Context context, final String wuid, int lan) {
        super(context);
        lang = lan;
        dm = context.getResources().getDisplayMetrics();
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        id = wuid;
        height = 200*scale;
        starX =  dm.widthPixels - (int)(300*scale);
        starY = (int)(height/2) - (int)(20*scale);
        setBackgroundColor(ResourseColors.colorWhite);
        setLayoutParams(new LayoutParams((int)(1080*scale),(int)(height)));

        avatar = new CircleImageView(context);
        avatar.setLayoutParams(new LayoutParams((int)(150*scale),(int)(150*scale)));
        avatar.setX(50*scale);
        avatar.setY(height/2-75*scale);
        addView(avatar);

        surname = new TextView(context);
        surname.setTextSize(TypedValue.COMPLEX_UNIT_PX,50*scale);
        surname.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        surname.setTextColor(ResourseColors.colorBlack);
        surname.setX(240*scale);
        surname.setY(100*scale);
        addView(surname);

        name = new TextView(context);
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX,50*scale);
        name.setTypeface(Typeface.createFromAsset(context.getAssets(),"font.ttf"));
        name.setTextColor(ResourseColors.colorBlack);
        name.setX(240*scale);
        name.setY(30*scale);
        addView(name);

        starFill = MainMenuActivity.getBitmapFromAsset(context,"star_fill.png");
        starFill = Bitmap.createScaledBitmap(starFill,(int)(40*scale),(int)(40*scale),false);
        starEmpty = MainMenuActivity.getBitmapFromAsset(context,"star_empty.png");
        starEmpty = Bitmap.createScaledBitmap(starEmpty,(int)(40*scale),(int)(40*scale),false);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(context,ProfiPage.class);
                intent.putExtra("profiId",wuid);
                intent.putExtra("lang",lang);
                intent.putExtra("OpenAsProfi",true);
                context.startActivity(intent);
            }
        });

        setWillNotDraw(false);
        uploadInfo();
    }

    public void destroy(){
        Glide.clear(avatar);
    }

    public void uploadInfo(){
        FirebaseDatabase.getInstance().getReference("usersInfo").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue(String.class));
                surname.setText(dataSnapshot.child("surname").getValue(String.class));
                String imgurl = dataSnapshot.child("photoURL").getValue(String.class);

                Glide.with(getContext()).load(imgurl).into(avatar);
                stars = 2;
                try {
                    stars = dataSnapshot.child("stars").getValue(Float.class);
                    invalidate();
                }catch (Exception e){
                    System.out.println("abab no stars foe user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    Paint p =new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        p.setColor(ResourseColors.colorStroke);
        for(int i = 0; i<5; i++){
            if(stars >= i){
                canvas.drawBitmap(starFill,starX+i*50*scale,starY,p);
            }else{
                canvas.drawBitmap(starEmpty,starX+i*50*scale,starY,p);
            }
        }
        canvas.drawRect(0,getHeight()-2*scale,dm.widthPixels,getHeight(),p);
        super.onDraw(canvas);
    }
}
