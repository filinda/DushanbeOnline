package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.FrameLayout;

public class EditTextCustom extends FrameLayout {

    Paint p, ptext;
    EditText edt;
    int width, height;
    int r;
    String str;
    float scale;
    Bitmap bitmap;
    private int isVis = 0;

    public EditTextCustom(@NonNull Context context, int width, int height, Bitmap bitmap, float scale, String str) {
        super(context);
        this.width = width;
        this.height = height;
        this.bitmap = bitmap;
        this.str = str;
        this.scale = scale;

        setLayoutParams(new LayoutParams(width, height));
        p = new Paint();
        p.setColor(Color.rgb(255, 255, 255));
        edt = new EditText(context);
        edt.setTypeface(Typeface.createFromAsset(context.getAssets(), "font.ttf"));
        edt.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (40 * scale));
        edt.setX(100 * scale);
        edt.setY(6 * scale);
        edt.setLayoutParams(new LayoutParams(width - (int) (100 * scale), height));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edt.setBackground(null);
        }
        addView(edt);
        setWillNotDraw(false);
        r = (int) (20 * scale);

        ptext = new Paint();
        ptext.setColor(Color.rgb(150, 150, 150));
        ptext.setAntiAlias(true);
        ptext.setTypeface(Typeface.createFromAsset(context.getAssets(), "font.ttf"));
        ptext.setTextSize((int) (40 * scale));

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                invalidate();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                invalidate();
            }

            @Override
            public void afterTextChanged(Editable s) {
                invalidate();
            }
        });
    }

    public void setType(int vis) {
        isVis = vis;
        switch (isVis) {
            case 1:
                edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case 2:
                edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(0, 0, getWidth(), getHeight(), r, r, p);
        } else {
            canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        }

        if (edt.getText().toString().length() < 1) {
            canvas.drawText(str, 110 * scale, height - 60 * scale, ptext);
        }

        canvas.drawBitmap(bitmap, 55 * scale - bitmap.getWidth() / 2, getHeight() / 2 - bitmap.getHeight() / 2, new Paint());

        super.onDraw(canvas);
    }
}
