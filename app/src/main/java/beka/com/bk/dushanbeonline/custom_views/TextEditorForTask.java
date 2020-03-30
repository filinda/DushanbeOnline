package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

public class TextEditorForTask extends FrameLayout {
    public TextBox editText;
    Typeface type;
    float scale;
    public Paint paint, pLine;
    String title;
    int amountOfRows;
    public int charLimit = 0;
    int textHeight, pureHeigth = 0;
    boolean keyShow = true;
    String hint;
    public boolean hasline = true, isFoc = false;


    public TextEditorForTask(Context context, String title, String hint, int amountOfRows, float scale) {
        super(context);
        setWillNotDraw(false);
        this.scale = scale;
        this.title = title;
        this.hint = hint;
        this.amountOfRows = amountOfRows;
        setBackgroundColor(Color.WHITE);
        paint = new Paint();
        type = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        paint.setTypeface(type);
        paint.setColor(Color.rgb(100, 100, 100));
        paint.setStrokeWidth(2 * scale);
        paint.setTextSize(50 * scale);

        pLine = new Paint();
        pLine.setColor(Color.rgb(215, 215, 215));
        pLine.setStrokeWidth(3 * scale);


        editText = new TextBox(context, scale);
        editText.setTextSize(40 * scale);
        editText.setTextColor(Color.rgb(100, 100, 100));
        editText.setX(60 * scale);
        editText.setY(90 * scale);
        editText.setHint(hint);
        editText.setHintSize(35 * scale);
        editText.setHintColor(Color.rgb(200, 200, 200));
        editText.setTextColor(Color.rgb(100, 100, 100));
        editText.setSize(980 * scale, 107 * scale);
        editText.setPaddingx(0);
        editText.setPaddingy(20 * scale);

        editText.heightListener.add(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * TextEditorForTask.this.scale), (int) (90 * TextEditorForTask.this.scale + newVal)));
            }
        });

        setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (197 * scale)));

        editText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            editText.imm.hideSoftInputFromInputMethod(editText.getWindowToken(), 0);
                        }
                        return false;
                    }

                });

        addView(editText);

    }

    public void setHint(String hint) {
        editText.setHint(hint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TextEditorForTask.this.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * TextEditorForTask.this.scale), (int) (500 + 54 * TextEditorForTask.this.scale)));
        super.onDraw(canvas);
        canvas.drawText(title, 60 * scale, 75 * scale, paint);
        if (hasline)
            canvas.drawLine(60 * scale, getLayoutParams().height - 4 * scale, 1081 * scale, getLayoutParams().height - 4 * scale, pLine);
    }


}
