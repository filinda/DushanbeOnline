package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import beka.com.bk.dushanbeonline.MainMenuActivity;
import beka.com.bk.dushanbeonline.OpenCloseListener;
import beka.com.bk.dushanbeonline.ResourseColors;
import beka.com.bk.dushanbeonline.TaskEditorActivity;

public class SubCategoryMenu extends ConstraintLayout {

    int categoryId = -1;
    Paint pBack;
    ArrayList<SubCategoryBtn> subs;
    Context context;
    int lang = 0;

    Bitmap arrow;
    int minHeightParent = 0;
    ConstraintLayout scrollLayout;
    ScrollView scrollView;
    TopLabel title;
    private OpenCloseListener openCloseListener;
    int mode = 0;
    ImageView icon;
    TextView txCategory;
    float scale;
    String[][] strings;

    public SubCategoryMenu(Context context, final int lang, int minHeightParent, int mode) {
        super(context);

        scale = context.getResources().getDisplayMetrics().widthPixels/1080.0f;

        this.context = context;
        this.lang = lang;
        this.minHeightParent = minHeightParent;
        this.mode = mode;

        setWillNotDraw(false);
        setBackgroundColor(ResourseColors.colorWhite);

        strings = new String[2][6];
        strings[0][0] = "Choose area";
        strings[1][0] = "Выберите область";
        strings[0][1] = "not set in Database";
        strings[1][1] = "нет в Базе данных";

        openCloseListener = new OpenCloseListener() {
            @Override
            public void onClose() {

            }

            @Override
            public void onOpen() {

            }
        };

        title = new TopLabel(context, strings[lang][0], scale);
        addView(title);

        String langStr = "eng";
        if (lang == 1)
            langStr = "rus";

        FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(langStr + "Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    title.setLabel(dataSnapshot.getValue(String.class));
                    if (dataSnapshot.getValue(String.class) == null) {
                        title.setLabel(strings[lang][1]);
                        title.setX(-title.getMeasuredWidth() / 2 + 540 * scale);
                    }
                    title.setX(title.getMeasuredWidth() / 2 + 540 * scale);
                    invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                title.setLabel(databaseError.getMessage());
                invalidate();
            }
        });

        scrollView = new ScrollView(context);
        scrollView.setY(398 * scale);

        addView(scrollView);

        scrollLayout = new ConstraintLayout(context);
        scrollView.addView(scrollLayout);

        title.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subCategoryId < 0) {
                    back();
                } else {
                    changeCategory(categoryId, true);
                }
            }
        });
        setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), minHeightParent));
        arrow = MainMenuActivity.getBitmapFromAsset(context, "arrow.png");
        arrow = Bitmap.createScaledBitmap(arrow,(int)(arrow.getWidth()*scale),(int)(arrow.getHeight()*scale),false);

        icon = new ImageView(context);
        icon.setY(245 * scale);
        icon.setX(58 * scale);
        addView(icon);

        txCategory = new TextView(context);
        txCategory.setTextColor(ResourseColors.colorBlue);
        txCategory.setTextSize(TypedValue.COMPLEX_UNIT_PX,57*scale);
        txCategory.setY(260*scale);
        txCategory.setX(183*scale);
        txCategory.setText("...");
        addView(txCategory);

    }

    void back() {
        openCloseListener.onClose();
        animate().translationX(1080 * scale).setDuration(500).start();
    }

    void open() {
        openCloseListener.onOpen();
        animate().translationX(0).setDuration(500).start();
    }

    public void setOpenCloseListener(OpenCloseListener opl) {
        openCloseListener = opl;
    }

    boolean isFromSubG;

    public void changeCategory(final int categoryId, boolean isFromSub) {
        Bitmap temp = MainMenuActivity.getBitmapFromAsset(context, "MainMenu/icons/" + (categoryId + 1) + ".png");
        temp = Bitmap.createScaledBitmap(temp,(int)(temp.getWidth()*scale),(int)(temp.getHeight()*scale),false);
        icon.setImageBitmap(temp);
        icon.setY(300*scale-temp.getHeight()/2);
        this.categoryId = categoryId;
        subCategoryId = -1;
        isFromSubG = isFromSub;
        scrollLayout.removeAllViews();
        FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child("AmountOfSubs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class) != null) {
                    int amount = dataSnapshot.getValue(Integer.class);
                    subs = new ArrayList<>();
                    int startI = 0;
                    if(categoryId==0){
                        startI = 5;
                    }
                    for (int i = startI; i < amount; i++) {
                        subs.add(new SubCategoryBtn(context, SubCategoryMenu.this.categoryId, i, lang, arrow, SubCategoryMenu.this));
                        subs.get(i-startI).setY(((i-startI) * 166) * scale);
                        if (isFromSubG) {
                            subs.get(i-startI).setX(-1080 * scale);
                        }
                        scrollLayout.addView(subs.get(i-startI));
                    }
                    scrollLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) ((amount-startI) * 188 * scale)));
                    scrollLayout.setMaxHeight((int) ((150 + (amount-startI) * 188) * scale));
                    scrollLayout.setMinHeight((int) ((150 + (amount-startI) * 188) * scale));
                    invalidate();
                    if (isFromSubG) {
                        for (int i = 0; i < subs.size(); i++) {
                            subs.get(i).animate().translationX(0 * scale).setDuration(400).start();
                        }
                    } else {
                        open();
                    }
                } else {
                    switch (mode) {
                        case 0:
                            Intent intent = new Intent(context, TaskEditorActivity.class);
                            intent.putExtra("lang", lang);
                            intent.putExtra("category", SubCategoryMenu.this.categoryId);
                            context.startActivity(intent);
                            break;
                        case 1:
                            Intent intent2 = new Intent(context, TaskEditorActivity.class);
                            intent2.putExtra("lang", lang);
                            intent2.putExtra("category", SubCategoryMenu.this.categoryId);
                            context.startActivity(intent2);
                            break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                invalidate();
            }
        });


        String langStr = "eng";
        if (lang == 1)
            langStr = "rus";

        FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(langStr + "Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txCategory.setText(dataSnapshot.getValue(String.class));
                if (dataSnapshot.getValue(String.class) == null) {
                    txCategory.setText(strings[lang][1]);
                }
                invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                txCategory.setText(databaseError.getMessage());
                invalidate();
            }
        });

    }

    int subCategoryId = -1;
    int amount=0;

    public void changeSubCategory(final int categoryId, final int subCategoryId) {
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(subCategoryId + "").child("amountOfSubs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class) != null) {
                    amount = dataSnapshot.getValue(Integer.class);
                    if(amount>0) {
                        scrollLayout.removeAllViews();
                        subs = new ArrayList<>();
                        for (int i = 0; i < amount; i++) {
                            subs.add(new SubCategoryBtn(context, SubCategoryMenu.this.categoryId, SubCategoryMenu.this.subCategoryId, i, lang, arrow, SubCategoryMenu.this));
                            subs.get(i).setY((i * 166) * scale);
                            subs.get(i).setX(1080 * scale);
                            scrollLayout.addView(subs.get(i));
                        }
                        scrollLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (amount * 188 * scale)));
                        scrollLayout.setMaxHeight((int) ((150 + amount * 188) * scale));
                        scrollLayout.setMinHeight((int) ((150 + amount * 188) * scale));
                        invalidate();
                        for (int i = 0; i < subs.size(); i++) {
                            subs.get(i).animate().translationX(0 * scale).setDuration(400).start();
                        }
                    }else{
                        Intent intent = new Intent(context, TaskEditorActivity.class);
                        intent.putExtra("lang", lang);
                        intent.putExtra("category", categoryId);
                        intent.putExtra("subCategory", SubCategoryMenu.this.subCategoryId);
                       // intent.putExtra("subSubCategory", subSubCategoryId);
                        SubCategoryMenu.this.subCategoryId = -1;
                        context.startActivity(intent);
                    }
                } else {
                    switch (mode) {
                        case 0:
                            Intent intent = new Intent(context, TaskEditorActivity.class);
                            intent.putExtra("lang", lang);
                            intent.putExtra("category", SubCategoryMenu.this.categoryId);
                            intent.putExtra("subCategory", SubCategoryMenu.this.subCategoryId);
                            context.startActivity(intent);
                            break;
                        case 1:
                            Intent intent2 = new Intent(context, TaskEditorActivity.class);
                            intent2.putExtra("lang", lang);
                            intent2.putExtra("category", SubCategoryMenu.this.categoryId);
                            intent2.putExtra("subCategory", SubCategoryMenu.this.subCategoryId);
                            context.startActivity(intent2);
                            break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                invalidate();
            }
        });


        String langStr = "eng";
        if (lang == 1)
            langStr = "rus";

        FirebaseDatabase.getInstance().getReference().child("cvalifications").child(categoryId + "").child(subCategoryId + "").child(langStr + "Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(amount>0)
                txCategory.setText(dataSnapshot.getValue(String.class));
                if (dataSnapshot.getValue(String.class) == null) {
                    txCategory.setText(strings[lang][1]);
                }
                invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                txCategory.setText(databaseError.getMessage());
                invalidate();
            }
        });

    }

    public void categoryChoosen(int subSubCategoryId, int subCategoryId) {
        if (subSubCategoryId < 0) {
            if (categoryId == 0 & subCategoryId == 0) {
                //startQuest

            } else {
                changeSubCategory(categoryId, subCategoryId);
            }
        }
    }

}