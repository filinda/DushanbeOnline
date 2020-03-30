package beka.com.bk.dushanbeonline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beka.com.bk.dushanbeonline.custom_views.FilterBigView;
import beka.com.bk.dushanbeonline.custom_views.ShortTaskView;
import beka.com.bk.dushanbeonline.custom_views.TopSearch;
import yandex_map_2.MapView2;

public class AllTasksMapActivity extends AppCompatActivity {


    FrameLayout layout;
    MapView2 mapView;
    Bitmap[] bitmaps;
    int lang = 1;
    float scale;
    TopSearch topLabel;
    double latLU;
    double lonLU;
    double latRD;
    double lonRD;
    FilterBigView filter;
    ArrayList<String> catList;
    boolean isProfi = false;

    ArrayList<MapObjectTapListener> tapListeners = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scale = getResources().getDisplayMetrics().widthPixels/1080f;

        lang =getIntent().getIntExtra("lang",1);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        layout = new FrameLayout(this);
        setContentView(layout);
        mapView = new MapView2(this);
        layout.addView(mapView);

        topLabel = new TopSearch(this, lang, layout);
        topLabel.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Bitmap list = MainMenuActivity.getBitmapFromAsset(this,"tasks_list.png");
        list = Bitmap.createScaledBitmap(list,(int)(list.getWidth()*scale),(int)(list.getHeight()*scale),false);
        topLabel.mapBtn.setImageBitmap(list);
        topLabel.mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllTasksMapActivity.this, AllTasksActivity.class);
                startActivity(intent);
            }
        });
        layout.addView(topLabel);
        topLabel.tv.setText(topLabel.strings[lang][1]);

        Paint p = new Paint();

        Bitmap bg = MainMenuActivity.getBitmapFromAsset(this,"icon_bg_map.png");
        bg = Bitmap.createScaledBitmap(bg,(int)(140*scale),(int)(174*scale),false);

        bitmaps = new Bitmap[12];
        for(int i = 0; i<bitmaps.length; i++){
            bitmaps[i] = Bitmap.createBitmap((int)(140*scale),(int)(2*174*scale),Bitmap.Config.ARGB_8888);
            Canvas temp = new Canvas(bitmaps[i]);
            temp.drawBitmap(bg,0,0,p);
            Bitmap categ = MainMenuActivity.getBitmapFromAsset(this,"MainMenu/icons/"+(i+1)+".png");
            categ = Bitmap.createScaledBitmap(categ,(int)(categ.getWidth()*scale*0.7),(int)(categ.getHeight()*scale*0.7),false);
            temp.drawBitmap(categ,bitmaps[i].getWidth()/2-categ.getWidth()/2,70*scale-categ.getHeight()/2,p);
        }

        filter = new FilterBigView(this,lang, this){
            @Override
            public void close() {
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

      //  mapView.getMap().move(new CameraPosition());

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
    }




    private void getTasks() {

                        FirebaseDatabase.getInstance().getReference().child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mapView.getMap().getMapObjects().clear();
                                Date nowDate = Calendar.getInstance().getTime();
                                int nowHour = nowDate.getHours();
                                int nowMinute = nowDate.getMinutes();
                                int nowYear = Calendar.getInstance().get(Calendar.YEAR);
                                int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                                int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
                                nowDate.setYear(nowYear);
                                nowDate.setDate(nowDay);
                                nowDate.setMonth(nowMonth);
                                nowDate.setHours(nowHour);
                                nowDate.setMinutes(nowMinute);

                                ArrayList<Point> points = new ArrayList<>();

                                if (dataSnapshot != null) {
                                    Map<Object, Object> tasks2 = ((Map<Object, Object>) dataSnapshot.getValue());
                                    for (final Map.Entry<Object, Object> entry : tasks2.entrySet()) {
                                        if (!(entry.getValue() instanceof String)) {

                                            Boolean visible = (Boolean) (((Map<Object, Object>) (entry.getValue())).get("visible"));
                                            if(visible==null){
                                                visible = true;
                                            }
                                            if (visible) {

                                                for (final Map.Entry<Object, Object> entry2 : ((Map<Object, Object>) (entry.getValue())).entrySet()) {

                                                    if (!(entry2.getValue() instanceof Boolean)) {
                                                    System.out.println("LOLOLO123: " + (((Map<Object, Object>) entry2.getValue()).get("lat")));

                                                    double lat = (Double) ((Map<Object, Object>) entry2.getValue()).get("lat");
                                                    double lon = (Double) ((Map<Object, Object>) entry2.getValue()).get("lon");

                                                    System.out.println("messageInfo lat " + lat);
                                                    System.out.println("messageInfo lon " + lon);
                                                    System.out.println("messageInfo ");

                                                    System.out.println("messageInfo ok");
                                                    Long Y = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateY"));
                                                    Long M = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateM"));
                                                    Long D = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateD"));
                                                    Long H = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateH"));
                                                    Long min = (Long) (((Map<Object, Object>) entry2.getValue()).get("startDateMin"));

                                                    Date start = new Date();
                                                    start.setYear(Y.intValue());
                                                    start.setMonth(M.intValue());
                                                    start.setDate(D.intValue());
                                                    start.setHours(H.intValue());
                                                    start.setMinutes(min.intValue());

                                                    Date start2 = new Date();
                                                    start2.setYear(Y.intValue() - 1900);
                                                    start2.setMonth(M.intValue());
                                                    start2.setDate(D.intValue());

                                                    if ((nowDate.getTime() < start.getTime()) & checkfilter(entry2, lat, lon, start2)) {

                                                        Long categ = (Long) (((Map<Object, Object>) entry2.getValue()).get("categoryId"));
                                                        for (Point pointNow : points) {
                                                            if (Math.pow(pointNow.getLatitude() - lat, 2) + Math.pow(pointNow.getLongitude() - lon, 2) < 0.000001) {
                                                                lat -= 0.0004;
                                                            }
                                                        }
                                                        Point point = new Point(lat, lon);
                                                        points.add(point);
                                                        ImageProvider provider = ImageProvider.fromBitmap(bitmaps[categ.intValue()]);

                                                        tapListeners.add(new MapObjectTapListener() {
                                                            @Override
                                                            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                                                                Intent intent = new Intent(AllTasksMapActivity.this, TaskInfoActivity.class);
                                                                intent.putExtra("lang", lang);
                                                                intent.putExtra("id", (String) entry2.getKey());
                                                                intent.putExtra("phone", (String) entry.getKey());
                                                                intent.putExtra("uid", (String) (((Map<Object, Object>) entry2.getValue()).get("userId")));
                                                                startActivity(intent);
                                                                return true;
                                                            }
                                                        });

                                                        mapView.getMap().getMapObjects().addPlacemark(point, provider).addTapListener(tapListeners.get(tapListeners.size() - 1));
                                                    }
                                                }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

    }


    Location userLocation;
    boolean moved = false;

    private void startTracker() {
        LocationRequest request = new LocationRequest();

        request.setInterval(1000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        if(!moved) {
                            getTasks();
                            Point townPoint = new Point(location.getLatitude(), location.getLongitude());
                            CameraPosition campos = new CameraPosition(townPoint, (float) 12, 0, 0);
                            mapView.getMap().move(campos);
                            moved = true;
                        }
                        userLocation = location;
                    }
                }
            }, null);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTracker();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
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

    private boolean checkfilter(Map.Entry<Object, Object> entry2, double lat, double lon, Date start2){
        boolean okRange = true, okMoney = true,okCateg = true, okDate = true;
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
        boolean isOkProfi=false;
        if(!okCateg){
            System.out.println("abab checking for category");
            for(int i=0;i<catList.size();i++){
                System.out.println("abab "+catList.get(i).split("-",3)[0]+" "+catList.get(i).split("-",3)[1]+" "+catList.get(i).split("-",3)[2]);
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

        System.out.println("abab range-"+okRange+" money-"+okMoney+" cat-"+okCateg+" date-"+okDate);

        return okRange&okMoney&okCateg&okDate;
    }


}
