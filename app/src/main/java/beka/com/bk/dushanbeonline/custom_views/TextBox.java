package beka.com.bk.dushanbeonline.custom_views;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import java.util.ArrayList;

import beka.com.bk.dushanbeonline.R;


@SuppressLint("AppCompatCustomView")
public class TextBox extends EditText implements View.OnTouchListener {

    private Editable text;
    private TextWatcher textWatcher;
    public String hint;
    private Typeface typeface;
    public Paint pText, pLines, pHint;
    private ValueAnimator animator;
    public float paddingx, paddingy;
    float width, height;
    float scale;
    public final int AUTOSIZE = 1;
    public final int SCROLL = 2;
    private int touchX, touchY;
    private boolean isNewCursor = false;
    public InputMethodManager imm;
    public ArrayList<NumberPicker.OnValueChangeListener> heightListener;
    int limit = 0;
    public boolean hintInFocus = false, isCentered = false;

    @SuppressLint("ResourceAsColor")
    public TextBox(Context context, float scale) {
        super(context);
        this.scale = scale;
        //  inputConnection = new TestInputConnection(this, true);
        text = getText();
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        hint = "";
        setFocusableInTouchMode(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.showSoftInput(v, 0);
            }
        });
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (limit > 0 & s.toString().length() > limit) {
                    setText(s.toString().substring(0, limit));
                    setSelection(limit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        };

        addTextChangedListener(textWatcher);
        heightListener = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setFocusable(FOCUSABLE);
        } else {
            setFocusable(true);
        }
        setOnTouchListener(this);

        hint = "...";
        pText = new Paint();
        pText.setColor(Color.BLACK);
        pText.setTextSize(40 * scale);
        pHint = new Paint();
        pHint.setTextSize(35 * scale);
        pHint.setColor(Color.GRAY);
        pLines = new Paint();
        pLines.setStrokeWidth(5 * scale);
        pLines.setColor(R.color.colorPrimary);
        paddingx = scale * 20;
        paddingy = scale * 10;
        animator = new ValueAnimator();
        animator.setIntValues(255, 50, 255);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(1000);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        setBackgroundColor(Color.TRANSPARENT);
        setSingleLine(true);
        setMovementMethod(null);
        setCursorVisible(false);
        setTextSize(1);
        invalidate();
    }


    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }


    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setSize(float width, float height) {
        setLayoutParams(new FrameLayout.LayoutParams((int) width, (int) height));
        this.width = width;
        this.height = height;
        for (int i = 0; i < heightListener.size(); i++) {
            heightListener.get(i).onValueChange(null, (int) this.height, (int) height);
        }
    }

    public void setPaddingx(float paddingx) {
        this.paddingx = paddingx;
    }

    public void setPaddingy(float paddingy) {
        this.paddingy = paddingy;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.argb(30,40,40,220));

        text = getText();
        int selectionStart = getSelectionStart();//Selection.getSelectionStart(text);

        Rect bounds = new Rect();
        int startline = 0;
        int lineAmount = 1;
        pText.getTextBounds("5", 0, 1, bounds);
        int nextlineHeight = (int) (bounds.height() * 1.7);


        if ((selectionStart == -1 | selectionStart == 0) & isFocused()) {
            pLines.setAlpha((int) animator.getAnimatedValue());
            canvas.drawLine(paddingx, paddingy + 1.2f * nextlineHeight * (lineAmount), paddingx, paddingy + nextlineHeight * 0.35f, pLines);
        }

        Rect recI = new Rect();
        Rect rec2 = new Rect();
        pText.getTextBounds("|", 0, 1, recI);

        for (int i = 0; i < text.toString().length(); i++) {
            Rect rec = new Rect();
            pText.getTextBounds(text.toString().substring(startline, i + 1) + "|", 0, text.toString().substring(startline, i + 1).length() + 1, rec);
            pText.getTextBounds(text.toString().substring(startline, i) + "|", 0, text.toString().substring(startline, i).length() + 1, rec2);
            pText.getTextBounds((text.toString() + "|").substring(startline, i + 2), 0, (text.toString() + "|").substring(startline, i + 2).length(), bounds);

            if (isNewCursor & touchX < paddingx & touchY > (lineAmount - 1) * nextlineHeight & touchY - recI.right < (lineAmount) * nextlineHeight) {
                setSelection(startline);
                //  imm.updateSelection(this,startline,startline,0,startline);
                isNewCursor = false;
            }

            if (isNewCursor & touchX - paddingx < rec.right - recI.right & touchX - paddingx > rec2.right - recI.right - nextlineHeight * 0.1 & touchY > (lineAmount - 1) * nextlineHeight & touchY - recI.right < (lineAmount) * nextlineHeight) {
                setSelection(i + 1);
                // imm.updateSelection(this,i+1,i+1,0,i+1);
                isNewCursor = false;
            }

            if (bounds.right > width - paddingx * 2) {
                if (paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2 > height) {
                    height = paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2;
                    setSize(width, height);
                }
                if (height - (paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2) > nextlineHeight * 0.7f) {
                    height = paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2;
                    setSize(width, height);
                }
                canvas.drawText(text.toString().substring(startline, i + 1), paddingx, paddingy + nextlineHeight * lineAmount, pText);
                startline = i + 1;
                lineAmount += 1;
                if (i + 1 == text.toString().length()) {
                    rec.right = 0;
                }
            } else {
                if (i + 1 == text.toString().length()) {
                    canvas.drawText(text.toString().substring(startline, i + 1), paddingx, paddingy + nextlineHeight * lineAmount, pText);
                    if (height - (paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2) > nextlineHeight * 0.7f) {
                        height = paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2;
                        setSize(width, height);
                    }
                }
            }
            if (i == selectionStart - 1 & isFocused()) {
                pLines.setAlpha((int) animator.getAnimatedValue());
                if (i != startline - 1) {
                    canvas.drawLine(paddingx + rec.right - recI.right, paddingy + nextlineHeight * (lineAmount) + nextlineHeight * 0.2f, paddingx + rec.right - recI.right, paddingy + nextlineHeight * (lineAmount - 1) + nextlineHeight * 0.3f, pLines);
                } else {
                    canvas.drawLine(paddingx, paddingy + nextlineHeight * (lineAmount) + nextlineHeight * 0.2f, paddingx, paddingy + nextlineHeight * (lineAmount - 1) + nextlineHeight * 0.3f, pLines);
                }
            }
        }

        if (isNewCursor) {
            setSelection(text.toString().length());
            //  imm.updateSelection(this,text.toString().length(),text.toString().length(),0,text.toString().length());
            isNewCursor = false;
        }

        if ((!isFocused() | hintInFocus) & text.toString().length() == 0) {
            pHint.getTextBounds("5", 0, 1, bounds);
            nextlineHeight = (int) (bounds.height() * 1.7);
            startline = 0;
            lineAmount = 1;
            for (int i = 0; i < hint.length(); i++) {

                Rect rec = new Rect();
                pHint.getTextBounds(hint.substring(startline, i + 1) + "|", 0, hint.substring(startline, i + 1).length() + 1, rec);
                pHint.getTextBounds((hint + "i").substring(startline, i + 2), 0, (hint + "i").substring(startline, i + 2).length(), bounds);

                if (bounds.right > width - paddingx * 2) {
                    if (paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2 > height) {
                        height = paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2;
                        setSize(width, height);
                    }
                    if (height - (paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2) > nextlineHeight * 0.7f) {
                        height = paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2;
                        setSize(width, height);
                    }
                    canvas.drawText(hint.substring(startline, i + 1), paddingx, paddingy + nextlineHeight * lineAmount, pHint);
                    startline = i + 1;
                    lineAmount += 1;
                    if (i + 1 == hint.length()) {
                        rec.right = 0;
                    }
                } else {
                    if (i + 1 == hint.length()) {
                        canvas.drawText(hint.substring(startline, i + 1), paddingx, paddingy + nextlineHeight * lineAmount, pHint);
                        if (height - (paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2) > nextlineHeight * 0.7f) {
                            height = paddingy + nextlineHeight * (lineAmount + 1) + paddingy * 2;
                            setSize(width, height);
                        }
                    }
                }

            }
        }

    }


    @Override
    public void setY(float y) {
        super.setY(y);
    }

    public void setHint(String hint) {
        if(hint!=null)
        this.hint = hint;
    }


    public String getStringText() {
        return text.toString();
    }

    public void setTextColor(int color) {
        pText.setColor(color);
        invalidate();
    }

    public void setHintColor(int color) {
        pHint.setColor(color);
        invalidate();
    }

    public void setLinesColor(int color) {
        pLines.setColor(color);
        invalidate();
    }

    public void setTypeface2(Typeface typeface) {
        this.typeface = typeface;
        pText.setTypeface(typeface);
        pHint.setTypeface(typeface);
        invalidate();
    }

    public void setTextSize2(float pxSize) {
        pText.setTextSize(pxSize);
        invalidate();
    }

    public void setHintSize(float pxSize) {
        pHint.setTextSize(pxSize);
        invalidate();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            touchX = (int) event.getX();
            touchY = (int) event.getY();
            isNewCursor = true;
        }
        return false;
    }
}

class TestInputConnection extends BaseInputConnection {

    TextBox mCustomView;
    private int mBatchEditNesting;

    public TestInputConnection(TextBox targetView, boolean fullEditor) {
        super(targetView, fullEditor);
        mCustomView = targetView;

        //super.
    }

   /* @Override
    public Editable getEditable() {
        return mCustomView.getEditable();
    }*/

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        return super.commitText(text, newCursorPosition);
    }

}