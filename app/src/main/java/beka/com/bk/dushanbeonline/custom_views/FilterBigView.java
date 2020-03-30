package beka.com.bk.dushanbeonline.custom_views;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.MaskWatcher;
import beka.com.bk.dushanbeonline.RegistrationActivity;
import beka.com.bk.dushanbeonline.ResourseColors;

public class FilterBigView extends FrameLayout {
    private float scale = 1;
    private float offsetLeftScreen = 0;
    private ValueAnimator open, close;
    private int screenWidth, screenHeight;
    public boolean isOpened  =false;
    private int lang;
    private String[][] strings;
    private int durOpenClose = 500;
    ImageView closeBtn;
    TextView mainLabel;
    public RailWithLabel railRange, railPayment;
    public TextBox edt;
    TextView tx3, tx4;
    Rect bounds2;
    MaskWatcher mask;
    public CheckBox allBox, mineBox;
    public DayPeriod dateView;
    public SwitchView switchViewRange, switchViewMoney, switchViewTime;

    View line;
    Activity root;

    public FilterBigView(@NonNull Context context, int lang, final Activity root) {
        super(context);
        this.lang = lang;
        this.root = root;

        strings = new String[2][10];
        strings[0][0] = "Filters";
        strings[1][0] = "Фильтры";
        strings[0][1] = "Distance to";
        strings[1][1] = "Расстояние, до";
        strings[0][2] = "Payment from";
        strings[1][2] = "Оплата, от";
        strings[0][3] = "km";
        strings[1][3] = "км";
        strings[0][4] = "TJS";
        strings[1][4] = "СОМ";
        strings[0][5] = "0";
        strings[1][5] = "0";
        strings[0][6] = "Show all categories";
        strings[1][6] = "Показать все категории";
        strings[0][7] = "Show categories of mine profession";
        strings[1][7] = "Показать категории моего профиля";

        setFocusable(true);
        setFocusableInTouchMode(true);


        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        close = new ValueAnimator();
        close.setDuration(durOpenClose);
        close.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetLeftScreen = (float)animation.getAnimatedValue();
                setX(-screenWidth+offsetLeftScreen);
            }
        });

        open = new ValueAnimator();
        open.setDuration(durOpenClose);
        open.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetLeftScreen = (float)animation.getAnimatedValue();
                setX(-screenWidth+offsetLeftScreen);
            }
        });

        setLayoutParams(new LayoutParams(screenWidth,screenHeight));
        setX(-screenWidth+offsetLeftScreen);

        setBackgroundColor(ResourseColors.colorWhite);

        closeBtn = new ImageView(getContext());
        Bitmap bp = MainMenuActivity.getBitmapFromAsset(getContext(),"icon_close.png");
        bp = Bitmap.createScaledBitmap(bp,(int)(57*scale),(int)(57*scale),false);
        closeBtn.setImageBitmap(bp);
        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        closeBtn.setScaleType(ImageView.ScaleType.MATRIX);
        closeBtn.setX(52 * scale);
        closeBtn.setY(168/2*scale-bp.getHeight()/2);
        closeBtn.setLayoutParams(new LayoutParams((int)(57*scale),(int)(57*scale)));
        addView(closeBtn);

        mainLabel=new TextView(getContext());
        mainLabel.setTypeface(Typeface.createFromAsset(context.getAssets(), "font_medium.ttf"));
        mainLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,57*scale);
        mainLabel.setTextColor(ResourseColors.colorGray);
        mainLabel.setText(strings[lang][0]);
        mainLabel.measure(0,0);
        mainLabel.setX(screenWidth/2-mainLabel.getMeasuredWidth()/2);
        mainLabel.setY(168/2*scale-mainLabel.getMeasuredHeight()/2);
        addView(mainLabel);

        railRange = new RailWithLabel(getContext(),1,51,strings[lang][3], strings[lang][1]);
        railRange.val = 50;
        railRange.invalidate();
        railRange.setX(0);
        railRange.setY(closeBtn.getY()+280*scale);
        addView(railRange);


        tx3 = new TextView(getContext());
        tx3.setText(strings[lang][2]);
        tx3.setTextSize(TypedValue.COMPLEX_UNIT_PX, 42 * scale);
        tx3.setTextColor(ResourseColors.colorLightGray);
        tx3.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font.ttf"));
        tx3.setX((1080-1016)*scale/2);
        tx3.setY(railRange.getY()+ railRange.getHeight() + 400*scale);
        addView(tx3);

        tx4 = new TextView(getContext());
        tx4.setText(strings[lang][4]);
        tx4.setTextSize(TypedValue.COMPLEX_UNIT_PX, 55 * scale);
        tx4.setTextColor(ResourseColors.colorLightGray);
        tx4.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font_bold.ttf"));
        bounds2 = new Rect();
        Paint textPaint2 = tx4.getPaint();
        textPaint2.getTextBounds(strings[lang][4], 0, strings[lang][4].length(), bounds2);
        tx4.setX(1080*scale - tx3.getX()*2-bounds2.width());
        tx4.setY(tx3.getY()-10*scale);
        addView(tx4);


        edt = new TextBox(getContext(), scale);
        edt.setY(tx3.getY()-22*scale);
        edt.setX(1080*scale - tx3.getX()-bounds2.width()-70*scale);
        edt.setInputType(InputType.TYPE_CLASS_NUMBER);


        mask = new MaskWatcher("###-###");
        edt.addTextChangedListener(mask);

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    Rect bounds = new Rect();
                    edt.pText.getTextBounds(edt.getText().toString(), 0, edt.getText().toString().length(), bounds);
                    edt.setX((1080*scale - tx3.getX()-bounds2.width()-60*scale) - bounds.width());

                } else {
                    Rect bounds = new Rect();
                    edt.pHint.getTextBounds(edt.hint, 0, edt.hint.length(), bounds);
                    edt.setX((1080*scale - tx3.getX()-bounds2.width()-60*scale) - bounds.width());

                }
            }
        });
        edt.paddingx = 0;
        edt.setSize(1080 * scale, 100 * scale);
        edt.setLimit(6);
        edt.setHint(strings[lang][5]);
        edt.hintInFocus = true;
        edt.setHintSize(58 * scale);
        edt.setTextSize2(58 * scale);
        edt.paddingy = 0 * scale;
        Rect bounds = new Rect();
        edt.pHint.getTextBounds(edt.hint, 0, edt.hint.length(), bounds);
        edt.setTypeface2(Typeface.createFromAsset(getContext().getAssets(),"font_bold.ttf"));
        edt.setTextColor(ResourseColors.colorLightGray);
        edt.setHintColor(ResourseColors.colorStroke);
        edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    hideSoftKeyboard(root);
                }
                return false;
            }
        });
        addView(edt);

        allBox = new CheckBox(getContext(),strings[lang][6]);
        allBox.setY(tx3.getY()+120*scale);
        allBox.isCheck = true;
        allBox.invalidate();
        addView(allBox);

        mineBox = new CheckBox(getContext(),strings[lang][7]);
        mineBox.setY(allBox.getY()+150*scale);
        addView(mineBox);

        CheckBox.pair(allBox,mineBox);

        dateView = new DayPeriod(getContext(),lang);
        dateView.setY(mineBox.getY()+250*scale);
        addView(dateView);

        switchViewRange = new SwitchView(getContext());
        switchViewRange.setY(railRange.getY() - 60*scale);
        switchViewRange.setX(railRange.offset/2);

        railRange.pair(switchViewRange);

        switchViewMoney = new SwitchView(getContext());
        switchViewMoney.setY(tx3.getY()-60*scale);
        switchViewMoney.setX(railRange.offset/2);
        switchViewMoney.turnListener = new OnTurnListener(){
            @Override
            public void onTurn(boolean state) {
                if(state){
                    edt.setTextColor(ResourseColors.colorBlue);
                    edt.invalidate();
                    tx4.setTextColor(ResourseColors.colorBlue);
                    edt.setEnabled(true);
                }else{
                    edt.setTextColor(ResourseColors.colorLightGray);
                    edt.invalidate();
                    tx4.setTextColor(ResourseColors.colorLightGray);
                    edt.setEnabled(false);
                    edt.clearFocus();
                }
                super.onTurn(state);
            }
        };
        addView(switchViewMoney);

        switchViewTime = new SwitchView(getContext());
        switchViewTime.setY(dateView.getY()- 85*scale);
        switchViewTime.setX(railRange.offset/2);
        addView(switchViewTime);
        dateView.pair(switchViewTime);
        dateView.isActive = false;

        line = new View(getContext());
        line.setBackgroundColor(ResourseColors.colorLightGray);
        line.setLayoutParams(new LayoutParams((int)(1080*scale),(int)(2*scale)));
        line.setY(switchViewMoney.getY()-70*scale);
        line.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        addView(line);


        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(root);
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void close(){
        hideSoftKeyboard(root);
        open.cancel();
        close.setFloatValues(offsetLeftScreen,0);
        close.start();
        isOpened=false;
    }

    public void open(){
        close.cancel();
        open.setFloatValues(offsetLeftScreen,screenWidth);
        open.start();
        isOpened = true;
    }

}
