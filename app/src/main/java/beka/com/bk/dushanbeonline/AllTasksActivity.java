package beka.com.bk.dushanbeonline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yandex.mapkit.MapKitFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beka.com.bk.dushanbeonline.custom_views.FilterBigView;
import beka.com.bk.dushanbeonline.custom_views.LoadCircle;
import beka.com.bk.dushanbeonline.custom_views.ShortTaskView;
import beka.com.bk.dushanbeonline.custom_views.TopSearch;

public class AllTasksActivity extends AppCompatActivity {

    FrameLayout layout;
    ScrollView scl;
    TopSearch topLabel;
    public int lang = 1;
    ArrayList<ShortTaskView> tasks;
    float scale;
    String[][] strings, stringsDays;
    DisplayMetrics dm;
    FrameLayout sclLayout;
    FilterBigView filter;
    Location userLocation;
    LoadCircle loading;

    public void ancienOnCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        layout = new FrameLayout(this);
        setContentView(layout);
        lang = getIntent().getIntExtra("lang", 1);

        strings = new String[2][4];
        strings[1][0] = "Мои задания";
        strings[0][0] = "My tasks";
        strings[1][1] = "Отмена";
        strings[0][1] = "Cancel";
        strings[1][2] = "Открыть настройки геоданных";
        strings[0][2] = "Open GPS settings";
        strings[1][3] = "Для поиска заданий необходимы геоданные";
        strings[0][3] = "For task serach location is required";

        stringsDays = new String[2][7];
        stringsDays[0][0] = "Sun";
        stringsDays[1][0] = "Воскр";
        stringsDays[0][2] = "Tue";
        stringsDays[1][2] = "Вт";
        stringsDays[0][3] = "Wed";
        stringsDays[1][3] = "Ср";
        stringsDays[0][4] = "Thu";
        stringsDays[1][4] = "Четв";
        stringsDays[0][5] = "Fri";
        stringsDays[1][5] = "Пят";
        stringsDays[0][6] = "Sat";
        stringsDays[1][6] = "Суб";
        stringsDays[0][1] = "Mon";
        stringsDays[1][1] = "Пон";

        tasks = new ArrayList<>();
        design();

        topLabel.filterBtn.setAlpha(0f);
        topLabel.filterBtn.invalidate();
        topLabel.filterBtn.setEnabled(false);


        FirebaseDatabase.getInstance().getReference().child("profiInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("okolokf "+"start");
                if(dataSnapshot!=null){
                    System.out.println("okolokf "+" notNull "+dataSnapshot.getChildrenCount());
                    for(DataSnapshot  object : dataSnapshot.getChildren()){
                        if(object.getValue() instanceof Boolean){
                            isProfi = object.getValue(Boolean.class);
                            if(isProfi){
                                topLabel.filterBtn.setAlpha(1f);
                                topLabel.filterBtn.invalidate();
                                topLabel.filterBtn.setEnabled(true);
                            }
                        }
                        if(object.getValue() instanceof List){
                            HashMap<String, Object> map =  (HashMap<String, Object>)dataSnapshot.getValue();
                            if(map.get("categories") instanceof  ArrayList){
                                 catList = ((ArrayList)map.get("categories"));
                            }
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loading.start();
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            System.out.println("abab starting locationRequest "+permission);
            startTracker();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    ArrayList<String> catList;
    boolean isProfi = false;

  // Сделать так чтобы исполнитель выдел кнопку позвонить только в своих категориях а в не своих категориях видеел кнопку создать такое же задание


    DatabaseReference mDatabase;

    public void getTasks() {

        for (int i = 0; i < tasks.size(); i++) {
        sclLayout.removeView(tasks.get(i));
        }
        tasks.clear();

    mDatabase = FirebaseDatabase.getInstance().getReference();


                        FirebaseDatabase.getInstance().getReference().child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            System.out.println("abab load tasks"+dataSnapshot);
            Date nowDate = Calendar.getInstance().getTime();
            int nowHour = nowDate.getHours();
            int nowMinute = nowDate.getMinutes();
            int nowYear = Calendar.getInstance().get(Calendar.YEAR);
            int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
            nowDate.setYear(nowYear-1900);
            nowDate.setDate(nowDay);
            nowDate.setMonth(nowMonth);
            nowDate.setHours(nowHour);
            nowDate.setMinutes(nowMinute);
            if(dataSnapshot!=null){
                Map<Object,Object> tasks2 = ((Map<Object,Object>)dataSnapshot.getValue());
                for (final Map.Entry<Object, Object> entry : tasks2.entrySet())
                {
                    if(!(entry.getValue() instanceof String)) {
                        Boolean visible = (Boolean) (((Map<Object, Object>) (entry.getValue())).get("visible"));
                        if(visible==null){
                            visible = true;
                        }
                        if (visible){
                            for (final Map.Entry<Object, Object> entry2 : ((Map<Object, Object>) (entry.getValue())).entrySet()) {
                                if (!(entry2.getValue() instanceof Boolean)) {
                                    System.out.println("LOLOLO123: " + (((Map<Object, Object>) entry2.getValue()).get("lat")));
                                    double lat = 0, lon = 0;
                                    try {
                                        lat = (Double) ((Map<Object, Object>) entry2.getValue()).get("lat");
                                    } catch (Exception e) {
                                    }
                                    try {
                                        lon = (Double) ((Map<Object, Object>) entry2.getValue()).get("lon");
                                    } catch (Exception e) {
                                    }
                                    try {
                                        lat = (Long) ((Map<Object, Object>) entry2.getValue()).get("lat");
                                    } catch (Exception e) {
                                    }
                                    try {
                                        lon = (Long) ((Map<Object, Object>) entry2.getValue()).get("lon");
                                    } catch (Exception e) {
                                    }

                                    System.out.println("messageInfo lat " + lat);
                                    System.out.println("messageInfo lon " + lon);
                                    System.out.println("messageInfo ");
                                              /*  System.out.println("messageInfo lon "+(lat<latLU));
                                                System.out.println("messageInfo lon "+(lat>latRD));
                                                System.out.println("messageInfo lon "+(lat<latLU));
                                                System.out.println("messageInfo lon "+(lat<latLU));*/

                                    if (true) {
                                        System.out.println("messageInfo ok");
                                        Long Y = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateY"));
                                        Long M = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateM"));
                                        Long D = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateD"));
                                        Long H = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateH"));
                                        Long min = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateMin"));

                                        Date start = new Date();
                                        start.setYear(Y.intValue() - 1900);
                                        start.setMonth(M.intValue());
                                        start.setDate(D.intValue());
                                        start.setHours(H.intValue());
                                        start.setMinutes(min.intValue());

                                        Date start2 = new Date();
                                        start2.setYear(Y.intValue() - 1900);
                                        start2.setMonth(M.intValue());
                                        start2.setDate(D.intValue());


                                        if (checkfilter(entry2, lat, lon, start2)) {
                                            if (nowDate.getTime() < start.getTime()) {

                                                Long categ = (Long) (((Map<Object, Object>) entry2.getValue()).get("categoryId"));
                                                String name = (String) (((Map<Object, Object>) entry2.getValue()).get("name"));
                                                String dateTime = (String) (((Map<Object, Object>) entry2.getValue()).get("dateTime"));
                                                String typeText = (String) (((Map<Object, Object>) entry2.getValue()).get("budget"));

                                                Long Y1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateY"));
                                                Long M1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateM"));
                                                Long D1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateD"));
                                                Long H1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateH"));
                                                Long min1 = (Long) (((Map<Object, Object>) entry2.getValue()).get("publicDateMin"));

                                                Long price = (Long) (((Map<Object, Object>) entry2.getValue()).get("priceVal"));

                                                Date pub = new Date();
                                                pub.setYear(Y1.intValue());
                                                pub.setMonth(M1.intValue());
                                                pub.setDate(D1.intValue());
                                                if (H1 != null) {
                                                    pub.setHours(H1.intValue());
                                                } else {
                                                    pub.setHours(0);
                                                }
                                                if (min1 != null) {
                                                    pub.setMinutes(min1.intValue());
                                                } else {
                                                    pub.setMinutes(0);
                                                }

                                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                                                String dat = stringsDays[lang][start.getDay()] + "  " + (format.format(start));

                                                ShortTaskView taskView = new ShortTaskView(AllTasksActivity.this, scale, (String) entry2.getKey(), name, dat, price, typeText, categ, pub);
                                                System.out.println("olok " + tasks.size() + " " + entry2.getKey());
                                                taskView.setY(tasks.size() * 200 * scale);
                                                taskView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        FirebaseDatabase.getInstance().getReference().child("usersInfo").child((String) (((Map<Object, Object>) entry2.getValue()).get("userId"))).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                Intent intent = new Intent(AllTasksActivity.this, TaskInfoActivity.class);
                                                                intent.putExtra("lang", lang);
                                                                intent.putExtra("id", (String) entry2.getKey());
                                                                intent.putExtra("phone", dataSnapshot.getValue(String.class).substring(4));
                                                                startActivity(intent);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                });
                                                taskView.setBackgroundColor(ResourseColors.colorWhite);
                                                sclLayout.setBackgroundColor(ResourseColors.colorWhite);
                                                sclLayout.addView(taskView);
                                                tasks.add(taskView);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Collections.sort(tasks, new Comparator<ShortTaskView>() {
                            @Override
                            public int compare(ShortTaskView o1, ShortTaskView o2) {
                                return o2.publication.compareTo(o1.publication);
                            }
                        });

                        for(int i=0;i<tasks.size();i++){
                            tasks.get(i).setY(i * 200 * scale);
                        }

                    }
                }loading.stop();
            }
            sclLayout.setMinimumHeight((int) (200 * tasks.size() * scale));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}


    public void design() {
        dm = getResources().getDisplayMetrics();
        scale = dm.widthPixels / 1080.0f;
        sclLayout = new FrameLayout(this);
        topLabel = new TopSearch(this, lang, sclLayout);
        topLabel.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        topLabel.mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapKitFactory.setApiKey("e7223c7a-7840-41e3-b4c7-7ef8948c63e4");
                MapKitFactory.initialize(AllTasksActivity.this);
                Intent intent = new Intent(AllTasksActivity.this, AllTasksMapActivity.class);
                startActivity(intent);
            }
        });
        layout.addView(topLabel);
        scl = new ScrollView(this);
        scl.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, dm.heightPixels - (int) (168 * scale)));
        scl.setY(168 * scale);
        scl.addView(sclLayout);

        sclLayout.setMinimumHeight((int) (200 * 10 * scale));

        layout.addView(scl);

        filter = new FilterBigView(this,lang, this){
            @Override
            public void close() {
                loading.start();
                getTasks();
                super.close();
            }
        };
        filter.switchViewRange.on();
        layout.addView(filter);

        topLabel.filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter.isOpened){
                    filter.close();
                }else{
                    filter.open();
                }
            }
        });
        loading = new LoadCircle(this);
        loading.setX(1080/2*scale-loading.bmp.getWidth()/2);
        loading.setY(400*scale);
        layout.addView(loading);
    }

    boolean moved = false;
    private boolean checkfilter(Map.Entry<Object, Object> entry2, double lat, double lon, Date start2){
        boolean okRange = true, okMoney = true,okCateg = true, okDate = true, okWork = true;
        okRange = !filter.switchViewRange.isOn;
        Location taskLocation = new Location("");
        taskLocation.setLatitude(lat);
        taskLocation.setLongitude(lon);
        float distance = 0;
        if(userLocation!=null){
            distance = userLocation.distanceTo(taskLocation);
        }
        okRange = okRange|(distance/1000<filter.railRange.val);

        Long price = (Long) (((Map<Object, Object>) entry2.getValue()).get("priceVal"));

        okMoney = !filter.switchViewMoney.isOn;
        int predel = 0;
        if(filter.edt.getText().toString().length()>0){
            predel = Integer.parseInt(filter.edt.getText().toString());
        }

        Long category = (Long) (((Map<Object, Object>) entry2.getValue()).get("categoryId"));
        Long subCategory = (Long) (((Map<Object, Object>) entry2.getValue()).get("subCategoryId"));
        Long subSubCategory = (Long) (((Map<Object, Object>) entry2.getValue()).get("subSubCategoryId"));

        okMoney = okMoney|(price>=predel);

        okDate = !filter.switchViewTime.isOn;

        if(filter.dateView.begin!=null&filter.dateView.end!=null) {
            Date begin1900 = new Date();
            begin1900.setYear(filter.dateView.begin.getYear()-1900);
            begin1900.setMonth(filter.dateView.begin.getMonth());
            begin1900.setDate(filter.dateView.begin.getDate());


            Date end1900 = new Date();
            end1900.setYear(filter.dateView.end.getYear()-1900);
            end1900.setMonth(filter.dateView.end.getMonth());
            end1900.setDate(filter.dateView.end.getDate());

            System.out.println("abab  "+String.format("begin %td.%tm.%ty   ", begin1900,begin1900,begin1900)+String.format("start %td.%tm.%ty", start2,start2,start2)+String.format("  end %td.%tm.%ty   ", end1900,end1900,end1900));
            System.out.println("abab  "+begin1900.getTime()+" "+start2.getTime()+" "+end1900.getTime());


            okDate = okDate | (begin1900.getTime() <= start2.getTime() & end1900.getTime() >= start2.getTime());
        }else{
            if(filter.dateView.begin!=null){
                Date begin1900 = new Date();
                begin1900.setYear(filter.dateView.begin.getYear()-1900);
                begin1900.setMonth(filter.dateView.begin.getMonth());
                begin1900.setDate(filter.dateView.begin.getDate());
                okDate = okDate | (begin1900.getTime() <= start2.getTime());
            }else {
                if (filter.dateView.end != null) {
                    Date end1900 = new Date();
                    end1900.setYear(filter.dateView.end.getYear()-1900);
                    end1900.setMonth(filter.dateView.end.getMonth());
                    end1900.setDate(filter.dateView.end.getDate());
                    okDate = okDate | (end1900.getTime() >= start2.getTime());
                }else{
                    okDate = true;
                }
            }
        }

        okCateg = filter.allBox.isCheck;
        System.out.println("abab allBoxIsCheck-"+okCateg);
        boolean isOkProfi=false;
        if(!okCateg){
            for(int i=0;i<catList.size();i++){
                String[] categs =   catList.get(i).split("-",3);
                boolean nowOk = true;
                if(categs[2].equals("#")){
                    nowOk = nowOk&((category+"").equals(categs[0]));
                    nowOk = nowOk&((subCategory+"").equals(categs[1]));
                }else{
                    nowOk = nowOk&((category+"").equals(categs[0]));
                    nowOk = nowOk&((subCategory+"").equals(categs[1]));
                    nowOk = nowOk&((subSubCategory+"").equals(categs[2]));
                }
                if(i==0){isOkProfi = nowOk;}else {
                    isOkProfi = isOkProfi | nowOk;
                }
            }
            okCateg = isOkProfi;
        }

        System.out.println("abab prof "+(((Map<Object, Object>) entry2.getValue()).get("profies")));
        if((((Map<Object, Object>) entry2.getValue()).get("profies"))!=null) {
            Map<String, Long> profies = (Map<String, Long>) (((Map<Object, Object>) entry2.getValue()).get("profies"));
            okWork = !(profies.containsValue(3L))&!(profies.containsValue(4L))&!(profies.containsValue(2L));

            if(profies.get(FirebaseAuth.getInstance().getUid())!=null)
            okWork = okWork & (profies.get(FirebaseAuth.getInstance().getUid())!=-2);

        }

        //System.out.println("abab "+"name: "++"range:"+okRange);

        return okRange&okMoney&okCateg&okDate&okWork;
    }

    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTracker();
        } else {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTracker() {
        LocationManager lm = (LocationManager)AllTasksActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        System.out.println("abab gps:"+gps_enabled+" network:"+network_enabled);

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(AllTasksActivity.this)
                    .setMessage(strings[lang][3])
                    .setPositiveButton(strings[lang][2], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            AllTasksActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(strings[lang][1],null)
                    .show();
        }
        LocationRequest request = new LocationRequest();
        request.setInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        System.out.println("permis "+permission);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        if(!moved) {
                            getTasks();
                            moved = true;
                        }
                        userLocation = location;
                    }
                }
            }, null);
        }
    }

}
