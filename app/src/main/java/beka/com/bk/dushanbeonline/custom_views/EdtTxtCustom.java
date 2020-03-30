package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import android.widget.Toast;

import beka.com.bk.dushanbeonline.ResourseColors;

public class EdtTxtCustom extends FrameLayout {
    public TextBox tb;
    float scale;
    public Paint p;
    boolean wasWrong = false;
    private int requestedAmountOfCharsMin = -1;
    String error = "error";

    public EdtTxtCustom(@NonNull Context context, String hint) {
        super(context);
        setWillNotDraw(false);
        scale = context.getResources().getDisplayMetrics().widthPixels / 1080f;
        tb = new TextBox(context, scale);
        tb.setHint(hint);
        addView(tb);
        tb.setSize(1005 * scale, 120 * scale);
        tb.paddingx = 35 * scale;
        tb.paddingy = 20 * scale;
        tb.setTextSize2(44 * scale);
        tb.setHintSize(45 * scale);
        tb.setTextColor(ResourseColors.colorBlack);
        tb.setHintColor(ResourseColors.colorLightGray);
        tb.setTypeface2(Typeface.createFromAsset(context.getAssets(), "font.ttf"));
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
                if (wasWrong)
                    checkRequest();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setLayoutParams(new LayoutParams((int) (1005 * scale), (int) (120 * scale)));

        p = new Paint();
        p.setStrokeWidth(5 * scale);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(ResourseColors.colorStroke);
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setRequestedAmountOfCharsMin(int requestedAmountOfCharsMin) {
        this.requestedAmountOfCharsMin = requestedAmountOfCharsMin;
    }

    public boolean checkRequest() {
        if (requestedAmountOfCharsMin > 0) {
            if (tb.getText().toString().length() < requestedAmountOfCharsMin) {
                if (wasWrong)
                    p.setColor(ResourseColors.colorRed);
                if (!wasWrong) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
                wasWrong = true;
                invalidate();
                return false;
            } else {
                p.setColor(ResourseColors.colorStroke);
                wasWrong = false;
                invalidate();
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean checkRequestFinal() {
        if (requestedAmountOfCharsMin > 0) {
            if (tb.getText().toString().length() < requestedAmountOfCharsMin) {
                p.setColor(ResourseColors.colorRed);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                wasWrong = true;
                invalidate();
                return false;
            } else {
                p.setColor(ResourseColors.colorStroke);
                wasWrong = false;
                invalidate();
                return true;
            }
        } else {
            return true;
        }
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
            canvas.drawRect(1.5f * scale, 1.5f * scale, getWidth() - 1.5f * scale, getHeight() - 1.5f * scale, p);
        }
    }
}
