package beka.com.bk.dushanbeonline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import beka.com.bk.dushanbeonline.custom_views.ListCategoryView;
import beka.com.bk.dushanbeonline.custom_views.MuliLayerList;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;

public class AddRemoveCategories extends Activity {

    FrameLayout layout, sclLayout;
    ScrollView scrollView;
    TopLabel label;
    float scale=1;
    String[][] strings;
    int lang = 1;
    DisplayMetrics dm;
    TextView underlabel;
    CustomPublishBtn saveBtn;
    MuliLayerList k;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        layout = new FrameLayout(this);
        setContentView(layout);

        lang = getIntent().getIntExtra("lang", 1);

        dm = getResources().getDisplayMetrics();
        scale = dm.widthPixels/1080f;

        strings = new String[2][9];
        strings[0][0] = "Choose categories";
        strings[1][0] = "Выберите категории";
        strings[0][1] = "List of categories";
        strings[1][1] = "Список категорий";
        strings[0][2] = "Add";
        strings[1][2] = "Добавить";
        strings[0][3] = " tjs per mounth";
        strings[1][3] = " сом в месяц";
        strings[0][4] = "It is necessary to replenish the balance";
        strings[1][4] = "Необходимо пополнить баланс";
        strings[0][5] = "Every month, your balance will be withdrawn ";
        strings[1][5] = "Каждый месяц с вашего баланса будет сниматься ";
        strings[0][6] = "Accept";
        strings[1][6] = "Принять";
        strings[0][7] = "Cancel";
        strings[1][7] = "Отклонить";
        strings[0][8] = "tjk";
        strings[1][8] = "сом";

        label = new TopLabel(this,strings[lang][0],scale);
        label.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layout.addView(label);

        saveBtn = new CustomPublishBtn(this,ResourseColors.colorGreen,strings[lang][2]);
        saveBtn.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (141 * scale)));
        saveBtn.setY(dm.heightPixels-141*scale);
        layout.addView(saveBtn);
        saveBtn.invalidate();

        scrollView = new ScrollView(this);
        scrollView.setX(0);
        scrollView.setY(122*scale+168*scale);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(dm.heightPixels-168*scale-122*scale-141*scale)));

        sclLayout = new FrameLayout(this);

        System.out.println("abab create");
        k = new MuliLayerList(this,(int)(1080*scale),getResources().getDisplayMetrics().heightPixels,lang);
        k.setY(0*scale);
        sclLayout.addView(k);
        k.all.baseLayout=sclLayout;
        k.all.postBaseLayout=k;
        k.all.saveBtn = saveBtn;
        k.all.payPreInfo = strings[lang][3];

        underlabel = new TextView(this);
        underlabel.setTypeface(Typeface.createFromAsset(getAssets(),"font.ttf"));
        underlabel.setGravity(Gravity.CENTER_HORIZONTAL);
        underlabel.setTextColor(ResourseColors.colorLightGray);
        underlabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,40*scale);
        underlabel.setY((168+50)*scale);
        underlabel.setText(strings[lang][1]);
        layout.addView(underlabel);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("abab click");
                FirebaseDatabase.getInstance().getReference("paymentInfo").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long sum = 0L;
                        if(dataSnapshot!=null){
                            sum = dataSnapshot.getValue(Long.class);
                        }
                        if(sum.intValue()<k.all.price* k.all.checkedViews){
                            Toast.makeText(AddRemoveCategories.this,strings[lang][4],Toast.LENGTH_LONG).show();
                        }else{
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            int checkedViews = 0;
                                            ArrayList<String> categories = new ArrayList<>();
                                            for(int i = 0;i<k.all.childs.size();i++){
                                                if(k.all.childs.get(i).isChecked) {
                                                    checkedViews++;
                                                    categories.add(i+"-*-*");
                                                }
                                                for(int j = 0;j<k.all.childs.get(i).childs.size();j++){
                                                    if(k.all.childs.get(i).childs.get(j).isChecked) {
                                                        checkedViews++;
                                                        categories.add(i+"-"+j+"-*");
                                                    }
                                                    for(int p = 0;p<k.all.childs.get(i).childs.get(j).childs.size();p++){
                                                        if(k.all.childs.get(i).childs.get(j).childs.get(p).isChecked) {
                                                            checkedViews++;
                                                            categories.add(i+"-"+j+"-"+p);
                                                        }
                                                    }
                                                }
                                            }

                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("serverTasks").child("newCvalifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            FirebaseDatabase.getInstance().getReference().child("serverTasks").child("newCvalifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                            reference.setValue(categories);

                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:

                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(AddRemoveCategories.this);
                            builder.setMessage(strings[lang][5]+sum+" "+strings[lang][8]).setPositiveButton(strings[lang][6], dialogClickListener)
                                    .setNegativeButton(strings[lang][7], dialogClickListener).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        layout.addView(scrollView);
        scrollView.addView(sclLayout);
    }
}

