package beka.com.bk.dushanbeonline;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import beka.com.bk.dushanbeonline.custom_views.NotificationBtn;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;

public class NotificationActivity extends AppCompatActivity {

    int lang = 0;
    FrameLayout layout;
    TopLabel label;
    ScrollView scl;
    FrameLayout sclLayout;
    ArrayList<NotificationBtn> notifications = new ArrayList<>();
    float scale = 1;
    String[][] strings;
    DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lang = getIntent().getIntExtra("lang",1);
        layout = new FrameLayout(this);
        setContentView(layout);
        strings = new String[2][10];
        strings[0][0] = "Notifications";
        strings[1][0] = "Уведомления";
        dm = getResources().getDisplayMetrics();
        scale = getResources().getDisplayMetrics().widthPixels/1080f;
        design();
    }

    private void design() {
        label = new TopLabel(this,strings[lang][0],scale);
        layout.addView(label);
        label.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        scl = new ScrollView(this);
        scl.setY(168*scale);
        scl.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, (int)(dm.heightPixels-168*scale)));
        sclLayout = new FrameLayout(this);
        scl.addView(sclLayout);
        layout.addView(scl);
        FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    for(NotificationBtn not : notifications){
                        sclLayout.removeView(not);
                    }
                    notifications.clear();
                  /*  System.out.println("okolok "+dataSnapshot.getValue().getClass());
                    ArrayList<Map<String,Object>> notificatArray =  (ArrayList<Map<String,Object>>) dataSnapshot.getValue();
                    for(int i = 0;i<notificatArray.size();i++){
                        String text = (String) notificatArray.get(i).get("text");
                        Long Y = (Long) notificatArray.get(i).get("dateY");
                        Long M = (Long) notificatArray.get(i).get("dateM");
                        Long D = (Long) notificatArray.get(i).get("dateD");
                        Long H = (Long) notificatArray.get(i).get("dateH");
                        Date date = new Date();
                        date.setYear(Y.intValue()-1900);
                        date.setMonth(M.intValue());
                        date.setDate(D.intValue());
                        date.setHours(H.intValue());
                        String type = (String)notificatArray.get(i).get("type");
                        String id = (String)notificatArray.get(i).get("type");
                        NotificationBtn notBtn = new NotificationBtn(NotificationActivity.this,text,date,id,type,lang);
                        notifications.add(notBtn);
                        sclLayout.addView(notBtn);
                    }*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
