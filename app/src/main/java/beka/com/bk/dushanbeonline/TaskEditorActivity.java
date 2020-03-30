package beka.com.bk.dushanbeonline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yandex.mapkit.MapKitFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import beka.com.bk.dushanbeonline.custom_views.TaskEditorButton;
import beka.com.bk.dushanbeonline.custom_views.TextEditorForTask;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;

import static java.lang.System.gc;


public class TaskEditorActivity extends AppCompatActivity {

    int lang, categoryId, subCategoryId, subSubCategoryId;
    String[][] strings;
    String name = "wait for loading";
    FrameLayout layout;
    Typeface type;
    TopLabel tx;
    TextEditorForTask textEdtName, textEdtDesc; // commentEdit
    int PLACE_PICKER_REQUEST = 1, DATE_TIME_PICKER_REQUEST = 2, PRICE_PICKER_REQUEST = 3;
    TaskEditorButton btnAdress, btnDate, btnPrice;
    View mRootView;
    Window mRootWindow;
    ScrollView scrollView;
    CustomPublishBtn btnPublish;
    FrameLayout layoutMain;
    boolean keyboardShown;
    Intent mapIntent, dateTimeIntent, priceIntent;
    private DatabaseReference mDatabase;
    DisplayMetrics dm;
    TaskData taskData;
    String taskId;
    boolean changingTask = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        taskId = getIntent().getStringExtra("taskId");

        taskData = new TaskData();

        layoutMain = new FrameLayout(this);
        layoutMain.setBackgroundColor(Color.BLACK);
        setContentView(layoutMain);
        scrollView = new ScrollView(this);
        layout = new FrameLayout(this);
        scrollView.addView(layout);
        layout.setLayoutParams(new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, 3000));
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels));
        layoutMain.addView(scrollView);

        dm = getResources().getDisplayMetrics();

        layoutMain.setFocusable(true);
        layoutMain.setFocusableInTouchMode(true);
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        layout.setFocusable(true);
        layout.setFocusableInTouchMode(true);
        layout.setBackgroundColor(Color.rgb(29, 35, 42));
        lang = getIntent().getIntExtra("lang", 1);
        categoryId = getIntent().getIntExtra("category", -1);
        subCategoryId = getIntent().getIntExtra("subCategory", -1);
        subSubCategoryId = getIntent().getIntExtra("subSubCategory", -1);
        strings = new String[14][2];
        strings[0][0] = "Name of task";
        strings[0][1] = "Что нужно сделать?";
        strings[1][0] = "Example: ";
        strings[1][1] = "Например: ";
        strings[2][0] = "Describe the task/problem";
        strings[2][1] = "Опишите задание/проблему";
        strings[3][0] = "Your adress";
        strings[3][1] = "Ваш адрес";
        strings[4][0] = "Additional terms";
        strings[4][1] = "Дополнительные условия";
        strings[5][0] = "Publish";
        strings[5][1] = "Опубликовать";
        strings[6][0] = "Indicate date/time";
        strings[6][1] = "Укажите дату/время";
        strings[7][0] = "I'm ready to pay";
        strings[7][1] = "Я заплачу";
        strings[8][0] = "Price";
        strings[8][1] = "Стоимость";
        strings[9][0] = "I'm ready to pay";
        strings[9][1] = "Я заплачу";
        strings[10][0] = "Please fill required gaps";
        strings[10][1] = "Заполните обязательные поля";
        strings[11][0] = "Please input correct data";
        strings[11][1] = "Пожалуйста введите правильную дату";
        strings[12][0] = "Changes were applied";
        strings[12][1] = "Изменения сохранены";
        strings[13][0] = "Task created";
        strings[13][1] = "Задание создано";

        if (subCategoryId < 0) {
            //start for category
            String langStr = "eng";
            if (lang == 1)
                langStr = "rus";
            FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(langStr + "Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.getValue(String.class);
                    if (name == null) {
                        name = "not set in database";
                    }
                    tx.setLabel(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    name = databaseError.getMessage();
                    tx.setLabel(name);
                }
            });
        }
        if (subSubCategoryId < 0 & subCategoryId >= 0) {
            String langStr = "eng";
            if (lang == 1)
                langStr = "rus";
            FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(subCategoryId + "").child(langStr + "Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.getValue(String.class);
                    if (name == null) {
                        name = "not set in database";
                    }
                    tx.setLabel(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    name = databaseError.getMessage();
                    tx.setLabel(name);
                }
            });
        }
        if (subSubCategoryId >= 0 & subCategoryId >= 0) {
            String langStr = "eng";
            if (lang == 1)
                langStr = "rus";
            FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(subCategoryId + "").child(langStr + "Sub" + subSubCategoryId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.getValue(String.class);
                    if (name == null) {
                        name = "not set in database";
                    }
                    tx.setLabel(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    name = databaseError.getMessage();
                    tx.setLabel(name);
                }
            });
        }

        type = Typeface.createFromAsset(getAssets(), "font.ttf");
        design();


        String getedName = getIntent().getStringExtra("name");
        if(getedName!=null) {
            textEdtName.editText.setText(getedName);
        }
        String getedDesc = getIntent().getStringExtra("desc");
        if(getedDesc!=null) {
            textEdtDesc.editText.setText(getedDesc);
        }
       /* String getedAddDesc = getIntent().getStringExtra("addDesc");
        if(getedAddDesc!=null) {
            commentEdit.editText.setText(getedAddDesc);
        }*/

        mRootWindow = getWindow();
        mRootView = mRootWindow.getDecorView().findViewById(android.R.id.content);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        View view = mRootWindow.getDecorView();
                        view.getWindowVisibleDisplayFrame(r);
                        /*if (commentEdit.editText.isFocused()) {
                            commentEdit.setY(r.bottom - commentEdit.getHeight());
                        }*/
                        keyboardShown = r.bottom < dm.heightPixels - 150 * scale;
                        resizeGlobal();
                    }
                });
        MapKitFactory.setApiKey("NoKeyForGit");
        MapKitFactory.initialize(this);
        mapIntent = new Intent(this, MapActivityYandex.class);
        dateTimeIntent = new Intent(this, DateTime.class);
        priceIntent = new Intent(this, PriceEditorActivity.class);

        mapIntent.putExtra("lang", lang);
        dateTimeIntent.putExtra("lang", lang);
        priceIntent.putExtra("lang", lang);


        if(taskId!=null){
            loadData();
        }

    }

    View space1, space2, whiteboard;

    float scale = 1;

    private void design() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        scale = (float) dm.widthPixels / 1080.0f;

        tx = new TopLabel(this, "wait for loading ...", scale);
        tx.setLabel(name);
        layout.addView(tx);

        tx.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        textEdtName = new TextEditorForTask(TaskEditorActivity.this, strings[0][lang], "loading...", 1, scale);
        textEdtName.editText.setLimit(53);
        layout.addView(textEdtName);
        textEdtName.setY(168 * scale);
        String langStr = "eng";
        if (lang == 1)
            langStr = "rus";
        if(subSubCategoryId==-1) {
            FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(subCategoryId + "").child(langStr + "HintTaskName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String hint = dataSnapshot.getValue(String.class);
                    hint = strings[1][lang] + hint;
                    textEdtName.setHint(hint);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String hint = databaseError.getMessage();
                    textEdtName.setHint(hint);
                }
            });
            textEdtName.editText.heightListener.add(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    resizeGlobal();
                }
            });
        }else{
            FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(subCategoryId + "").child(subSubCategoryId + "").child(langStr + "HintTaskName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String hint = dataSnapshot.getValue(String.class);
                    hint = strings[1][lang] + hint;
                    textEdtName.setHint(hint);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String hint = databaseError.getMessage();
                    textEdtName.setHint(hint);
                }
            });
            textEdtName.editText.heightListener.add(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    resizeGlobal();
                }
            });
        }


        textEdtDesc = new TextEditorForTask(TaskEditorActivity.this, strings[2][lang], "loading...", 5, scale);
        textEdtDesc.editText.setLimit(200);
        layout.addView(textEdtDesc);
        textEdtDesc.setY((183 + 197) * scale);
        textEdtDesc.hasline = false;
        if(subSubCategoryId==-1) {
            FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(subCategoryId + "").child(langStr + "DescExamp").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String hint = dataSnapshot.getValue(String.class);

                    textEdtDesc.setHint(hint);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String hint = databaseError.getMessage();
                    textEdtDesc.setHint(hint);
                }
            });
            textEdtDesc.editText.heightListener.add(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    resizeGlobal();
                }
            });
        }else{
            FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(subCategoryId + "").child(subSubCategoryId + "").child(langStr + "DescExamp").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String hint = dataSnapshot.getValue(String.class);

                    textEdtDesc.setHint(hint);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String hint = databaseError.getMessage();
                    textEdtDesc.setHint(hint);
                }
            });
            textEdtDesc.editText.heightListener.add(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    resizeGlobal();
                }
            });
        }

        space1 = new View(this);
        space1.setBackgroundColor(Color.rgb(230, 230, 230));
        space1.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (93 * scale)));
        space1.setY((183 + 197 * 2) * scale);
        layout.addView(space1);
        space1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnAdress = new TaskEditorButton(this, MainMenuActivity.getBitmapFromAsset(this, "arrow.png"), MainMenuActivity.getBitmapFromAsset(this, "navi_icon.png"), MainMenuActivity.getBitmapFromAsset(this, "navi_icon_set.png"), strings[3][lang]);
        btnAdress.setY((183 + 197 * 2 + 93) * scale);
        btnAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(mapIntent, PLACE_PICKER_REQUEST);
                gc();
            }
        });
        layout.addView(btnAdress);

        btnDate = new TaskEditorButton(this, MainMenuActivity.getBitmapFromAsset(this, "arrow.png"), MainMenuActivity.getBitmapFromAsset(this, "date_icon.png"), MainMenuActivity.getBitmapFromAsset(this, "date_icon_set.png"), strings[6][lang]);
        btnDate.setY((183 + 197 * 2 + 93 + 166) * scale);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(dateTimeIntent, DATE_TIME_PICKER_REQUEST);
            }
        });
        layout.addView(btnDate);

        btnPrice = new TaskEditorButton(this, MainMenuActivity.getBitmapFromAsset(this, "arrow.png"), MainMenuActivity.getBitmapFromAsset(this, "price_icon.png"), MainMenuActivity.getBitmapFromAsset(this, "price_icon_set.png"), strings[8][lang]);
        btnPrice.setY((183 + 197 * 2 + 93 + 166 * 2) * scale);
        btnPrice.hasLine = false;
        btnPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(priceIntent, PRICE_PICKER_REQUEST);
            }
        });
        layout.addView(btnPrice);

        space2 = new View(this);
        space2.setBackgroundColor(Color.rgb(230, 230, 230));
        space2.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (93 * scale)));
        space2.setY((183 + 197 * 2 + 93 + 166 * 3) * scale);
        space2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layout.addView(space2);

        whiteboard = new View(this);
        whiteboard.setBackgroundColor(Color.rgb(255, 255, 255));
        whiteboard.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (1000 * scale)));
        whiteboard.setY((183 + 197 * 2 + 93 * 2 + 166 * 3) * scale);
        layout.addView(whiteboard);
        whiteboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPublish = new CustomPublishBtn(this, ResourseColors.colorGreen, strings[5][lang]);
        btnPublish.setX(0);
        btnPublish.setY(dm.heightPixels - 141 * scale);
        btnPublish.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (141 * scale)));
        layoutMain.addView(btnPublish);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnPublish.setZ(1000);
        }
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ready = textEdtName.editText.getText().length() != 0;
                ready = ready & textEdtDesc.editText.getText().length() != 0;
                ready = ready & !btnAdress.checkWarning();
                ready = ready & !btnDate.checkWarning();
                ready = ready & !btnPrice.checkWarning();
                if (ready) {
                    writeNewTask();
                    //запись в бд
                } else {
                    Toast.makeText(TaskEditorActivity.this, strings[10][lang], Toast.LENGTH_LONG).show();
                    if (textEdtName.editText.getText().length() == 0) {
                        textEdtName.paint.setColor(Color.RED);
                        textEdtName.invalidate();
                    } else {
                        textEdtName.paint.setColor(Color.rgb(100, 100, 100));
                        textEdtName.invalidate();
                    }
                    if (textEdtDesc.editText.getText().length() == 0) {
                        textEdtDesc.paint.setColor(Color.RED);
                        textEdtDesc.invalidate();
                    } else {
                        textEdtDesc.paint.setColor(Color.rgb(100, 100, 100));
                        textEdtDesc.invalidate();
                    }
                    if (btnAdress.checkWarning()) {
                        btnAdress.p.setColor(Color.RED);
                        btnAdress.p2.setColor(Color.RED);
                        btnAdress.invalidate();
                    } else {
                        btnAdress.p2.setColor(Color.rgb(100, 100, 100));
                    }
                    if (btnPrice.checkWarning()) {
                        btnPrice.p.setColor(Color.RED);
                        btnPrice.p2.setColor(Color.RED);
                        btnAdress.invalidate();
                    } else {
                        btnPrice.p2.setColor(Color.rgb(100, 100, 100));
                    }
                    if (btnDate.checkWarning()) {
                        btnDate.p.setColor(Color.RED);
                        btnDate.p2.setColor(Color.RED);
                        btnDate.invalidate();
                    } else {
                        btnDate.p2.setColor(Color.rgb(100, 100, 100));
                    }
                }
                Toast.makeText(TaskEditorActivity.this, strings[13][lang],Toast.LENGTH_LONG).show();
            }
        });
    }

    boolean isChanged = false;

    private boolean writeNewTask() {
        isChanged = false;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(taskId==null) {
            if (!isChanged) {
                mDatabase.child("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long amountOfTasks = 0;
                        String sum = FirebaseAuth.getInstance().getUid().substring(0,8);
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            sum=md5Custom(sum+child.getKey());
                        }
                        if(dataSnapshot!=null) {
                            amountOfTasks = dataSnapshot.getChildrenCount();
                        }

                        DatabaseReference reference = mDatabase.child("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).child(md5Custom(sum).substring(0, 8));
                        writeTaskData(reference);
                        isChanged = true;

                        Intent intent = new Intent(TaskEditorActivity.this,MyTasksActivity.class);
                        intent.putExtra("lang",lang);
                        startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }else{
            Date nowDate = Calendar.getInstance().getTime();
            int nowHour = nowDate.getHours();
            int nowMinute = nowDate.getMinutes();
            int nowYear = Calendar.getInstance().get(Calendar.YEAR);
            int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
            nowDate.setYear(nowYear);
            nowDate.setDate(nowDay);
            nowDate.setMonth(nowMonth);
            nowDate.setMinutes(nowMinute);
            nowDate.setHours(nowHour);
            if(taskData.dateStart.getTime()>=nowDate.getTime()) {
                changeTaskData(FirebaseDatabase.getInstance().getReference().child("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).child(taskId));
                Toast.makeText(this,strings[12][lang],Toast.LENGTH_LONG).show();
                Intent intent = new Intent(TaskEditorActivity.this,MyTasksActivity.class);
                intent.putExtra("lang",lang);
                startActivity(intent);
            }
            else{
                btnDate.p.setColor(Color.RED);
                btnDate.invalidate();
                Toast.makeText(this,strings[11][lang],Toast.LENGTH_LONG).show();
            }
        }

        return false;
    }

    public void changeTaskData(DatabaseReference reference) {
        reference.child("name").setValue(textEdtName.editText.getText().toString());
        reference.child("desc").setValue(textEdtDesc.editText.getText().toString());
        reference.child("adres").setValue(btnAdress.label);
        reference.child("price").setValue(btnPrice.label);
        reference.child("dateTime").setValue(btnDate.label);
        reference.child("userId").setValue(FirebaseAuth.getInstance().getUid());
        int myYear = Calendar.getInstance().get(Calendar.YEAR);
        int myDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int myMonth = Calendar.getInstance().get(Calendar.MONTH);
        Date date = new Date();
        date.setYear(myYear);
        date.setMonth(myMonth);
        date.setDate(myDay);
        reference.child("publicDate").setValue(String.format("%tR", date));
       // reference.child("aditDesc").setValue(commentEdit.editText.getText().toString());
        reference.child("publicDateY").setValue(myYear);
        reference.child("publicDateD").setValue(myDay);
        reference.child("publicDateM").setValue(myMonth);
        reference.child("fresh").setValue(true);
        reference.child("lat").setValue(taskData.lat);
        reference.child("lon").setValue(taskData.lon);

        if(taskData.dateStart!=null) {
            reference.child("startDateY").setValue(taskData.dateStart.getYear());
            reference.child("startDateD").setValue(taskData.dateStart.getDate());
            reference.child("startDateM").setValue(taskData.dateStart.getMonth());
            reference.child("startDateH").setValue(taskData.dateStart.getHours());
            reference.child("startDateMin").setValue(taskData.dateStart.getMinutes());

            reference.child("outOfDate").setValue(false);
        }

        if(taskData.price!=0) {
            reference.child("priceVal").setValue(taskData.price);
            reference.child("paymentType").setValue(taskData.paymentType);
            if (taskData.buget) {
                reference.child("budget").setValue(strings[7][lang]);
            } else {
                reference.child("budget").setValue(strings[9][lang]);
            }
            reference.child("budgetBool").setValue(taskData.buget);
        }

    }

    public void writeTaskData(DatabaseReference reference) {
        reference.child("name").setValue(textEdtName.editText.getText().toString());
        reference.child("desc").setValue(textEdtDesc.editText.getText().toString());
        reference.child("adres").setValue(btnAdress.label);
        reference.child("price").setValue(btnPrice.label);
        reference.child("dateTime").setValue(btnDate.label);
        reference.child("userId").setValue(FirebaseAuth.getInstance().getUid());

        int myYear = Calendar.getInstance().get(Calendar.YEAR);
        int myDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int myMonth = Calendar.getInstance().get(Calendar.MONTH);
        int myHour = Calendar.getInstance().get(Calendar.HOUR);
        int myMin = Calendar.getInstance().get(Calendar.MINUTE);
        Date date = new Date();
        date.setYear(myYear);
        date.setMonth(myMonth);
        date.setDate(myDay);
        date.setHours(myHour);
        date.setHours(myMin);

        reference.child("publicDate").setValue(String.format("%tR", date));
       // reference.child("aditDesc").setValue(commentEdit.editText.getText().toString());
        reference.child("publicDateY").setValue(myYear);
        reference.child("publicDateD").setValue(myDay);
        reference.child("publicDateM").setValue(myMonth);
        reference.child("publicDateH").setValue(myHour);
        reference.child("publicDateMin").setValue(myMin);
        reference.child("fresh").setValue(true);


        reference.child("lat").setValue(taskData.lat);
        reference.child("lon").setValue(taskData.lon);

        reference.child("startDateY").setValue(taskData.dateStart.getYear());
        reference.child("startDateD").setValue(taskData.dateStart.getDate());
        reference.child("startDateM").setValue(taskData.dateStart.getMonth());
        reference.child("startDateH").setValue(taskData.dateStart.getHours());
        reference.child("startDateMin").setValue(taskData.dateStart.getMinutes());


        reference.child("priceVal").setValue(taskData.price);
        reference.child("categoryId").setValue(categoryId);
        reference.child("subCategoryId").setValue(subCategoryId);
        reference.child("subSubCategoryId").setValue(subSubCategoryId);
        reference.child("paymentType").setValue(taskData.paymentType);
        if (taskData.buget) {
            reference.child("budget").setValue(strings[7][lang]);
        } else {
            reference.child("budget").setValue(strings[9][lang]);
        }
        reference.child("budgetBool").setValue(taskData.buget);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onPause() {
        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (data != null) {
                if (data.getStringExtra("adress") != null) {
                    String adres = data.getStringExtra("adress");
                    if (adres.length() != 0) {
                        btnAdress.setLabel(data.getStringExtra("adress"));
                        taskData.adress = data.getStringExtra("adress");
                        taskData.lat = data.getDoubleExtra("lat", 0);
                        taskData.lon = data.getDoubleExtra("lon", 0);
                    }
                }
            }
        }
        if (requestCode == DATE_TIME_PICKER_REQUEST) {
            if (data != null) {
                if (data.getStringExtra("date") != null) {
                    String adres = data.getStringExtra("date");
                    if (adres.length() != 0) {
                        btnDate.setLabel(data.getStringExtra("date"));
                        Date begin = new Date();
                        begin.setYear(data.getIntExtra("sY", 0));
                        begin.setDate(data.getIntExtra("sD", 0));
                        begin.setMonth(data.getIntExtra("sM", 0));
                        begin.setHours(data.getIntExtra("sH", 0));
                        begin.setMinutes(data.getIntExtra("sMin", 0));
                        taskData.dateStart = begin;
                    }
                }
            }
        }
        if (requestCode == PRICE_PICKER_REQUEST) {
            if (data != null) {
                if (data.getStringExtra("price") != null) {
                    String price = data.getStringExtra("price");
                    if (price.length() != 0) {
                        btnPrice.setLabel(data.getStringExtra("price"));
                        taskData.price = data.getIntExtra("priceVal", 0);
                        taskData.paymentType = data.getBooleanExtra("paymentType", true);
                        if (data.getBooleanExtra("type", true)) {
                            btnPrice.mainLabel = strings[7][lang];
                            taskData.buget = true;
                        } else {
                            btnPrice.mainLabel = strings[9][lang];
                            taskData.buget = false;
                        }
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private int resizeGlobal() {
        int nowHeight = (int) (168 * scale) + textEdtName.getLayoutParams().height;
        textEdtDesc.setY(nowHeight);
        nowHeight += textEdtDesc.getLayoutParams().height;
        space1.setY(nowHeight);
        nowHeight += space1.getLayoutParams().height;
        btnAdress.setY(nowHeight);
        nowHeight += btnAdress.getLayoutParams().height;
        btnDate.setY(nowHeight);
        nowHeight += btnDate.getLayoutParams().height;
        btnPrice.setY(nowHeight);
        nowHeight += btnPrice.getLayoutParams().height;
        space2.setY(nowHeight);
        nowHeight += space2.getLayoutParams().height;
        whiteboard.setY(nowHeight);
       /* if (!(commentEdit.editText.isFocused() & keyboardShown)) {
            commentEdit.setY(nowHeight);
        }*/
        //nowHeight += commentEdit.getLayoutParams().height;
        nowHeight += 141 * scale;
        if (scrollView.getHeight() < nowHeight) {
            layout.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), nowHeight));
            layout.setMinimumHeight(nowHeight);
        } else {
            layout.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), scrollView.getHeight()));
            layout.setMinimumHeight(scrollView.getHeight());
        }
        return nowHeight;
    }

    public static String md5Custom(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            // тут можно обработать ошибку
            // возникает она если в передаваемый алгоритм в getInstance(,,,) не существует
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while (md5Hex.length() < 32) {
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }

    public void loadData() {


        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(4)).child(taskId);

        reference.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null)
                    textEdtName.editText.setText(dataSnapshot.getValue(String.class));
                resizeGlobal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("dateTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null)
                    btnDate.setLabel(dataSnapshot.getValue(String.class));
                resizeGlobal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("price").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    btnPrice.setLabel(dataSnapshot.getValue(String.class));
                    resizeGlobal();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        reference.child("desc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null)
                    textEdtDesc.editText.setText(dataSnapshot.getValue(String.class));
                resizeGlobal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*reference.child("aditDesc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null)
                    commentEdit.editText.setText(dataSnapshot.getValue(String.class));
                resizeGlobal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        reference.child("adres").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null)
                    btnAdress.setLabel(dataSnapshot.getValue(String.class));
                resizeGlobal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("startDateY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class) != null)
                {
                    if( taskData.dateStart == null){
                        taskData.dateStart = new Date();
                    }
                    taskData.dateStart.setYear(dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        reference.child("startDateM").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class) != null)
                {
                    if( taskData.dateStart == null){
                        taskData.dateStart = new Date();
                    }
                    taskData.dateStart.setMonth(dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("startDateD").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class) != null)
                {
                    if( taskData.dateStart == null){
                        taskData.dateStart = new Date();
                    }
                    taskData.dateStart.setDate(dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("startDateH").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class) != null)
                {
                    if( taskData.dateStart == null){
                        taskData.dateStart = new Date();
                    }
                    taskData.dateStart.setHours(dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("startDateMin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class) != null)
                {
                    if( taskData.dateStart == null){
                        taskData.dateStart = new Date();
                    }
                    taskData.dateStart.setMinutes(dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        reference.child("lat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Double.class) != null)
                {
                   taskData.lat = dataSnapshot.getValue(Double.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("lon").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Double.class) != null)
                {
                    taskData.lon = dataSnapshot.getValue(Double.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("priceVal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Double.class) != null)
                {
                    taskData.price = dataSnapshot.getValue(Long.class).intValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
    }

}
