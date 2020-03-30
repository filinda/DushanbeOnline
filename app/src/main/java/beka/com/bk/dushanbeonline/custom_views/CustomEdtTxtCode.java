package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import beka.com.bk.dushanbeonline.ResourseColors;

public class CustomEdtTxtCode extends FrameLayout {
    public TextBox tb;
    float scale;
    Paint p;

    public CustomEdtTxtCode(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
        scale = context.getResources().getDisplayMetrics().widthPixels / 1080f;
        tb = new TextBox(context, scale);

        addView(tb);
        tb.setSize(541 * scale, 120 * scale);
        tb.paddingx = 0 * scale;
        //tb.paddingy = 6*scale;
        tb.setHint("");
        tb.setX(270.5f * scale);
        //tb.setLimit(6);
        //tb.setTextSize2(57*scale);
        tb.setTextColor(ResourseColors.colorBlack);
        tb.setTypeface2(Typeface.createFromAsset(context.getAssets(), "font_bold.ttf"));
        tb.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            tb.imm.hideSoftInputFromInputMethod(tb.getWindowToken(), 0);
                        }
                        return false;
                    }

                });

        tb.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Rect bounds = new Rect();
                tb.pText.getTextBounds(s.toString(), 0, s.toString().length(), bounds);
                tb.setX(getWidth() / 2 - bounds.width() / 2);
            }
        });

        setLayoutParams(new LayoutParams((int) (541 * scale), (int) (120 * scale)));

        p = new Paint();
        p.setStrokeWidth(5 * scale);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(ResourseColors.colorStroke);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        tb.setLayoutParams(params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(2.5f * scale, 2.5f * scale, getWidth() - 2.5f * scale, getHeight() - 2.5f * scale, 15 * scale, 15 * scale, p);
        } else {
            canvas.drawRect(1.5f * scale, 1.5f * scale, getWidth() - 1.5f * scale, getHeight() - 3.5f * scale, p);
        }
    }
}