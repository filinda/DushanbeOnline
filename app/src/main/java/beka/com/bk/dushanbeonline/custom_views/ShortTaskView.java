package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

import beka.com.bk.dushanbeonline.AllTasksActivity;
import beka.com.bk.dushanbeonline.AllTasksMapActivity;
import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.MyTasksActivity;
import beka.com.bk.dushanbeonline.OnSwipeTouchListener;
import beka.com.bk.dushanbeonline.ResourseColors;

public class ShortTaskView extends View {

    String name = "Not set in dataBase", price = "------", typeText = "--------";
    public String date = "Not set in dataBase";
    Paint pName, pDate, pPrice, pLine, pRect, pType, pDelete;
    Bitmap bp;
    Typeface type;
    float scale = 1;
    float offset = 0;
    boolean doubleString = false;
    public String id = "";
    DatabaseReference reference;
    String[][] strings;
    int lang;
    public Date publication;
    static Bitmap cross;
    Bitmap temp;
    OnSwipeTouchListener listener;
    boolean isOpened = false;
    public boolean allouDelete = false;
    public float mY = 0;
    Long category;
    public HashMap<String,Long> profies;
    public Date startDate;

    public ShortTaskView(MyTasksActivity context, float scale, String id, String name, String date, Long price, String typeText, Long category, Date publication, Date startDate, boolean isActive, HashMap<String,Long> profies) {
        super(context);

        this.publication=publication;
        this.category = category;
        this.profies = profies;
        this.startDate = startDate;

        strings = new String[2][4];
        strings[0][0] = "tjs";
        strings[1][0] = "сом";
        strings[0][1] = "Remove";
        strings[1][1] = "Удалить";

        this.scale = scale;
        this.id = id;

        lang = context.lang;
        this.name=name;
        this.date = date;
        this.price = (price) + " " + strings[lang][0];
        if ((price > 999)) {
            this.price = this.price.substring(0, this.price.length() - 7) + "," + this.price.substring(this.price.length() - 7);
        }
        this.typeText = typeText;

        bp = MainMenuActivity.getBitmapFromAsset(context, "editor_icon_white.png");
        bp = Bitmap.createScaledBitmap(bp, 70, 70, true);

        cross = MainMenuActivity.getBitmapFromAsset(context, "cross.png");
        cross = Bitmap.createScaledBitmap(cross, 50, 50, true);

        if(isActive)
        {bp = MainMenuActivity.getBitmapFromAsset(getContext(), "MainMenu/icons/" + (category+1) + ".png");}
        else
            {bp = MainMenuActivity.getBitmapFromAsset(getContext(), "MainMenu/icons/icons_grey/" + (category+1) + ".png");}

        bp = Bitmap.createScaledBitmap(bp, (int)(bp.getWidth()*ShortTaskView.this.scale*0.8), (int)(bp.getHeight()*ShortTaskView.this.scale*0.8), true);

        type = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        pName = new Paint();
        pName.setTextSize(45 * scale);
        pName.setColor(Color.BLACK);
        pName.setTypeface(type);

        pDelete = new Paint();
        pDelete.setTextSize(30 * scale);
        pDelete.setColor(ResourseColors.colorWhite);
        pDelete.setTypeface(type);

        pDate = new Paint();
        pDate.setTextSize(37 * scale);
        pDate.setColor(Color.rgb(150, 150, 150));
        pDate.setTypeface(type);

        pPrice = new Paint();
        pPrice.setTextSize(51 * scale);
        pPrice.setColor(ResourseColors.colorBlue);
        if(!isActive)
            pPrice.setColor(ResourseColors.colorIconGray);
        pPrice.setTypeface(Typeface.createFromAsset(context.getAssets(), "font.ttf"));

        pLine = new Paint();
        pLine.setColor(Color.rgb(225, 225, 225));

        pRect = new Paint();
        pRect.setColor(Color.WHITE);

        pType = new Paint();
        pType.setTextSize(39 * scale);
        pType.setColor(ResourseColors.colorLightGray);
        pType.setTypeface(type);

        setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (200 * scale)));
        setName(name);

        listener = new OnSwipeTouchListener(getContext()){
            public void onDoubleTouch() {
                if(allouDelete) {
                    if (sprin == null) {
                        sprin = new ValueAnimator();
                        sprin.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                offset = (Float) animation.getAnimatedValue();
                                invalidate();
                            }
                        });
                    }

                    if (isOpened) {
                        isOpened = false;
                        sprin.setFloatValues(offset, 0 * ShortTaskView.this.scale);
                        sprin.setDuration(200);
                        sprin.start();
                    } else {
                        isOpened = true;
                        sprin.setFloatValues(offset, -200 * ShortTaskView.this.scale);
                        sprin.setDuration(200);
                        sprin.start();
                    }
                }

            }

            public void onClick() {
                if(!isOpened) {
                    onl.onClick(ShortTaskView.this);
                }
            }
        };

        setOnTouchListener(listener);

    }

    OnClickListener onl;


    public void setActive(boolean active){
        if(active)
            pPrice.setColor(ResourseColors.colorBlue);
        else
            pPrice.setColor(ResourseColors.colorIconGray);

        if(active)
        {bp = MainMenuActivity.getBitmapFromAsset(getContext(), "MainMenu/icons/" + (category+1) + ".png");}
        else
        {bp = MainMenuActivity.getBitmapFromAsset(getContext(), "MainMenu/icons/icons_grey/" + (category+1) + ".png");}

        invalidate();
    }

    public void setMooveY(float y) {
        super.setY(y);
    }

    @Override
    public void setY(float y) {
        mY = y;
        super.setY(y);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        onl = l;
    }

    public String phone;

    public ShortTaskView(AllTasksActivity context, float scale, String id, String name, String date, Long price, String typeText, Long category, Date publication) {
        super(context);

        this.publication=publication;

        strings = new String[2][4];
        strings[0][0] = "tjs";
        strings[1][0] = "сом";
        strings[0][1] = "Remove";
        strings[1][1] = "Удалить";

        this.scale = scale;
        this.id = id;

        lang = context.lang;
        this.name=name;
        this.date = date;
        this.price = (price) + " " + strings[lang][0];
        if ((price > 999)) {
            this.price = this.price.substring(0, this.price.length() - 7) + "," + this.price.substring(this.price.length() - 7, this.price.length());
        }
        this.typeText = typeText;



        bp = MainMenuActivity.getBitmapFromAsset(context, "editor_icon_white.png");
        bp = Bitmap.createScaledBitmap(bp, 70, 70, true);

        bp = MainMenuActivity.getBitmapFromAsset(getContext(), "MainMenu/icons/" + (category+1) + ".png");
        bp = Bitmap.createScaledBitmap(bp, (int)(bp.getWidth()*ShortTaskView.this.scale*0.8), (int)(bp.getHeight()*ShortTaskView.this.scale*0.8), true);

        cross = MainMenuActivity.getBitmapFromAsset(context, "cross.png");
        cross = Bitmap.createScaledBitmap(cross, 50, 50, true);

        type = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        pName = new Paint();
        pName.setTextSize(45 * scale);
        pName.setColor(Color.BLACK);
        pName.setTypeface(type);


        pDelete = new Paint();
        pDelete.setTextSize(30 * scale);
        pDelete.setColor(ResourseColors.colorWhite);
        pDelete.setTypeface(type);

        pDate = new Paint();
        pDate.setTextSize(37 * scale);
        pDate.setColor(Color.rgb(150, 150, 150));
        pDate.setTypeface(type);

        pPrice = new Paint();
        pPrice.setTextSize(51 * scale);
        pPrice.setColor(ResourseColors.colorBlue);
        //pPrice.setTypeface(type);
        pPrice.setTypeface(Typeface.createFromAsset(context.getAssets(), "font.ttf"));

        pLine = new Paint();
        pLine.setColor(Color.rgb(225, 225, 225));

        pRect = new Paint();
        pRect.setColor(Color.WHITE);

        pType = new Paint();
        pType.setTextSize(39 * scale);
        pType.setColor(ResourseColors.colorLightGray);
        pType.setTypeface(type);

        setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (200 * scale)));
        setName(name);

        listener = new OnSwipeTouchListener(getContext()){

            public void onDoubleTouch() {
                if(allouDelete) {
                    if (sprin == null) {
                        sprin = new ValueAnimator();
                        sprin.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                offset = (Float) animation.getAnimatedValue();
                                invalidate();
                            }
                        });
                    }

                    if (isOpened) {
                        isOpened = false;
                        sprin.setFloatValues(offset, 0 * ShortTaskView.this.scale);
                        sprin.setDuration(200);
                        sprin.start();
                    } else {
                        isOpened = true;
                        sprin.setFloatValues(offset, -200 * ShortTaskView.this.scale);
                        sprin.setDuration(200);
                        sprin.start();
                    }
                }
            }

            public void onClick() {
                if(!isOpened) {
                    onl.onClick(ShortTaskView.this);
                }
            }
        };

        setOnTouchListener(listener);

    }

    public ShortTaskView(Context context, int lang, float scale, String id, String name, String date, Long price, String typeText, Long category, Date publication) {
        super(context);

        this.publication=publication;

        strings = new String[2][4];
        strings[0][0] = "tjs";
        strings[1][0] = "сом";
        strings[0][1] = "Remove";
        strings[1][1] = "Удалить";

        this.scale = scale;
        this.id = id;

        this.lang = lang;
        this.name=name;
        this.date = date;
        this.price = (price) + " " + strings[lang][0];
        if ((price > 999)) {
            this.price = this.price.substring(0, this.price.length() - 7) + "," + this.price.substring(this.price.length() - 7, this.price.length());
        }
        this.typeText = typeText;



        bp = MainMenuActivity.getBitmapFromAsset(context, "editor_icon_white.png");
        bp = Bitmap.createScaledBitmap(bp, 70, 70, true);

        bp = MainMenuActivity.getBitmapFromAsset(getContext(), "MainMenu/icons/" + (category+1) + ".png");
        bp = Bitmap.createScaledBitmap(bp, (int)(bp.getWidth()*ShortTaskView.this.scale*0.8), (int)(bp.getHeight()*ShortTaskView.this.scale*0.8), true);

        cross = MainMenuActivity.getBitmapFromAsset(context, "cross.png");
        cross = Bitmap.createScaledBitmap(cross, 50, 50, true);

        type = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        pName = new Paint();
        pName.setTextSize(45 * scale);
        pName.setColor(Color.BLACK);
        pName.setTypeface(type);


        pDelete = new Paint();
        pDelete.setTextSize(30 * scale);
        pDelete.setColor(ResourseColors.colorWhite);
        pDelete.setTypeface(type);

        pDate = new Paint();
        pDate.setTextSize(37 * scale);
        pDate.setColor(Color.rgb(150, 150, 150));
        pDate.setTypeface(type);

        pPrice = new Paint();
        pPrice.setTextSize(51 * scale);
        pPrice.setColor(ResourseColors.colorBlue);
        //pPrice.setTypeface(type);
        pPrice.setTypeface(Typeface.createFromAsset(context.getAssets(), "font.ttf"));

        pLine = new Paint();
        pLine.setColor(Color.rgb(225, 225, 225));

        pRect = new Paint();
        pRect.setColor(Color.WHITE);

        pType = new Paint();
        pType.setTextSize(39 * scale);
        pType.setColor(ResourseColors.colorLightGray);
        pType.setTypeface(type);

        setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (200 * scale)));
        setName(name);

        listener = new OnSwipeTouchListener(getContext()){

            public void onDoubleTouch() {
                if(allouDelete) {
                    if (sprin == null) {
                        sprin = new ValueAnimator();
                        sprin.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                offset = (Float) animation.getAnimatedValue();
                                invalidate();
                            }
                        });
                    }

                    if (isOpened) {
                        isOpened = false;
                        sprin.setFloatValues(offset, 0 * ShortTaskView.this.scale);
                        sprin.setDuration(200);
                        sprin.start();
                    } else {
                        isOpened = true;
                        sprin.setFloatValues(offset, -200 * ShortTaskView.this.scale);
                        sprin.setDuration(200);
                        sprin.start();
                    }
                }
            }

            public void onClick() {
                if(!isOpened) {
                    onl.onClick(ShortTaskView.this);
                }
            }
        };

        setOnTouchListener(listener);

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent e)
    {

        if(e.getAction()==MotionEvent.ACTION_UP){
            if(isOpened&e.getX()>getWidth()-200*scale){
                onDelete(this);
            }
        }

        boolean res = super.dispatchTouchEvent(e);
        if(listener!=null) {
            return listener.getGestureDetector().onTouchEvent(e);
        }else{
            return res;
        }
    }

    public void setName(String name) {
        this.name = name;
        if (name.length() > 24) {
            doubleString = true;
        } else {
            doubleString = false;
        }
        if (name.length() > 48) {
            this.name = name.substring(0, 48) + "...";
        }
    }

    float startX = 0;
    ValueAnimator sprin = null;
    ValueAnimator.AnimatorUpdateListener list = null;




    @Override
    protected void onDraw(Canvas canvasR) {
        canvasR.drawColor(ResourseColors.colorRed);
        canvasR.drawBitmap(cross,getWidth()-100*scale-cross.getWidth()/2, getHeight()/2-cross.getHeight()/2-20*scale,pLine);
        Rect bound = new Rect();
        pDelete.getTextBounds(strings[lang][1],0,strings[lang][1].length(),bound);
        canvasR.drawText(strings[lang][1],getWidth()-100*scale-bound.width()/2,getHeight()-55*scale,pDelete);

        if(temp==null)
            temp = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        canvas.drawColor(ResourseColors.colorWhite);

        if (doubleString) {
            canvas.drawText(name.substring(0, 24), 154 * scale, 71 * scale, pName);
            canvas.drawText(name.substring(24, name.length()), 154 * scale, 121 * scale, pName);
        } else {
            canvas.drawText(name, 154 * scale, 71 * scale, pName);
        }
        canvas.drawText(date, 150 * scale, 175 * scale, pDate);
        Rect bounds = new Rect();
        pPrice.getTextBounds(price, 0, price.length(), bounds);
        canvas.drawText(price, getWidth()-30*scale - bounds.width(), 130 * scale, pPrice);
        pType.getTextBounds(typeText, 0, typeText.length(), bounds);
        canvas.drawText(typeText, getWidth()-30*scale - bounds.width(),  68* scale, pType);
        canvas.drawRect(0, getHeight() - 2 * scale, getWidth(), getHeight(), pLine);
        canvas.drawBitmap(bp, 73 * scale - bp.getWidth()/2, getHeight()/2-bp.getHeight()/2, pLine);

        canvasR.drawBitmap(temp,offset,0,pLine);
        super.onDraw(canvas);
    }

    public void onDelete(ShortTaskView shortTaskView){

    }
}
