package beka.com.bk.dushanbeonline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import beka.com.bk.dushanbeonline.custom_views.ChooserPrice;
import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import beka.com.bk.dushanbeonline.custom_views.CustomSpiner2;
import beka.com.bk.dushanbeonline.custom_views.TextBox;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;
import beka.com.bk.dushanbeonline.custom_views.changeListener;

public class PriceEditorActivity extends AppCompatActivity {

    FrameLayout layout;
    TopLabel lbl;
    String[][] strings;
    int lang = 0;
    float scale;
    Typeface type;
    TextBox edt;
    DisplayMetrics dm;
    CustomSpiner2 systemSpiner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        lang = getIntent().getIntExtra("lang", 1);

        strings = new String[2][12];
        strings[0][0] = "Indicate your price";
        strings[1][0] = "Укажите вашу цену";
        strings[0][1] = "How much are you ready to pay?";
        strings[1][1] = "Сколько вы готовы заплатить?";
        strings[0][2] = "Your budget";
        strings[1][2] = "Ваш бюджет";
        strings[0][3] = "Exactly price";
        strings[1][3] = "Точная цена";
        strings[0][4] = "Please choose payment option";
        strings[1][4] = "Выберите способ оплаты";
        strings[0][5] = "Through our payment system";
        strings[1][5] = "Через нашу систему";
        strings[0][6] = "Cash";
        strings[1][6] = "Наличными";
        strings[0][7] = "I'm ready to pay";
        strings[1][7] = "Я заплачу";
        strings[0][8] = "Somoni";
        strings[1][8] = "Сомони";
        strings[0][9] = "Input price";
        strings[1][9] = "Введите сумму";
        strings[0][10] = "READY";
        strings[1][10] = "ГОТОВО";
        strings[0][11] = "0 TJS";
        strings[1][11] = "0 COM";

        dm = getResources().getDisplayMetrics();
        scale = dm.widthPixels / 1080.0f;
        layout = new FrameLayout(this);
        layout.setBackgroundColor(Color.rgb(255, 255, 255));
        type = Typeface.createFromAsset(getAssets(), "font.ttf");

        lbl = new TopLabel(this, strings[lang][0], scale);
        layout.addView(lbl);
        lbl.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setContentView(layout);


        design();
    }

    TextView tx3;

    @SuppressLint("ClickableViewAccessibility")
    private void design() {

        TextView tx = new TextView(this);
        tx.setText(strings[lang][1]);
        tx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48 * scale);
        tx.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        tx.setTextColor(Color.BLACK);
        tx.setTypeface(type);
        Rect bounds = new Rect();
        Paint textPaint = tx.getPaint();
        textPaint.getTextBounds(strings[lang][1], 0, strings[lang][1].length(), bounds);
        int width = bounds.width();
        //tx.setX(70 * scale);
        tx.setY(250 * scale);
        layout.addView(tx);

        tx3 = new TextView(this);
        tx3.setText(strings[lang][7]);
        tx3.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48 * scale);
        tx3.setTextColor(ResourseColors.colorBlue);
        tx3.setTypeface(type);
        Rect bounds2 = new Rect();
        Paint textPaint2 = tx3.getPaint();
        textPaint2.getTextBounds(strings[lang][7], 0, strings[lang][7].length(), bounds2);
        tx3.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        //tx3.setX((1080 / 2) * scale - bounds2.width() / 2);
        tx3.setY(450 * scale);
        layout.addView(tx3);




        edt = new TextBox(this, scale);
        edt.setY(520 * scale);
        edt.setX((1080 / 2 - 200) * scale);
        edt.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                    edt.setX((1080 / 2) * scale - bounds.width() / 2);
                } else {
                    Rect bounds = new Rect();
                    edt.pHint.getTextBounds(edt.hint, 0, edt.hint.length(), bounds);
                    edt.setX((1080 / 2) * scale - bounds.width() / 2);
                }
            }
        });
        edt.paddingx = 0;
        edt.setSize(1080 * scale, 100 * scale);
        edt.setLimit(6);
        edt.setHint(strings[lang][11]);
        edt.hintInFocus = true;
        edt.setHintSize(72 * scale);
        edt.setTextSize2(75 * scale);
        edt.paddingy = 0 * scale;
        bounds = new Rect();
        edt.pHint.getTextBounds(edt.hint, 0, edt.hint.length(), bounds);
        edt.setX((1080 / 2) * scale - bounds.width() / 2);
        edt.setTypeface2(Typeface.createFromAsset(this.getAssets(),"font_bold.ttf"));
        edt.setHintColor(ResourseColors.colorStroke);
        layout.addView(edt);

        edt.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            edt.imm.hideSoftInputFromInputMethod(edt.getWindowToken(), 0);
                        }
                        return false;
                    }

                });

        TextView tx2 = new TextView(this);
        tx2.setText(strings[lang][4]);
        tx2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 48 * scale);
        tx2.setTextColor(ResourseColors.colorBlue);
        tx2.setTypeface(type);
        tx2.setX(70 * scale);
        tx2.setY(850 * scale);
        layout.addView(tx2);

        String[] cityArray = new String[2];
        cityArray[0] = strings[lang][5];
        cityArray[1] = strings[lang][6];
        // cityArray[2] = strings[lang][10];

        systemSpiner = new CustomSpiner2(this, cityArray);
        systemSpiner.setY(tx2.getY() + 85 * scale);
        systemSpiner.setX(70*scale);
        systemSpiner.setLayoutParams(new FrameLayout.LayoutParams((int)((1080-140)*scale),(int)(120*scale)));
        layout.addView(systemSpiner);

        CustomPublishBtn readybtn = new CustomPublishBtn(this, ResourseColors.colorGreen, strings[lang][10]);
        readybtn.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (141 * scale)));
        readybtn.setY(dm.heightPixels - 141 * scale);
        layout.addView(readybtn);

        readybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt.getText().toString().length() != 0) {
                    Intent intent = new Intent();
                   // if (chooser.isOn) {
                        intent.putExtra("price", edt.getText().toString() + " " + strings[lang][8]);
                        intent.putExtra("priceVal", Integer.parseInt(edt.getText().toString()));
                   /* } else {
                        intent.putExtra("price", edt.getText().toString() + " " + strings[lang][8]);
                        intent.putExtra("priceVal", Integer.parseInt(edt.getText().toString()));
                    }*/
                    intent.putExtra("type", false);
                    intent.putExtra("paymentType", systemSpiner.itemSellected==0);
                    setResult(3, intent);
                    onBackPressed();
                } else {
                    Toast.makeText(PriceEditorActivity.this, strings[lang][9], Toast.LENGTH_LONG).show();
                    edt.pHint.setColor(Color.RED);
                }
            }
        });

    }

}
