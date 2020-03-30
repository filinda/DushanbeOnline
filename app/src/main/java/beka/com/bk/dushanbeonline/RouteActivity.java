package beka.com.bk.dushanbeonline;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingArrivalPoint;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.RequestPoint;
import com.yandex.mapkit.directions.driving.RequestPointType;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import beka.com.bk.dushanbeonline.custom_views.RouteHeader;
import beka.com.bk.dushanbeonline.custom_views.TopLabel;

public class RouteActivity extends AppCompatActivity implements DrivingSession.DrivingRouteListener {

    private final String MAPKIT_API_KEY = "e7223c7a-7840-41e3-b4c7-7ef8948c63e4";
    private Point ROUTE_START_LOCATION = new Point(59.959194, 30.407094);
    private Point ROUTE_END_LOCATION = new Point(55.733330, 37.587649);
    private Point SCREEN_CENTER = new Point(
            (ROUTE_START_LOCATION.getLatitude() + ROUTE_END_LOCATION.getLatitude()) / 2,
            (ROUTE_START_LOCATION.getLongitude() + ROUTE_END_LOCATION.getLongitude()) / 2);

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    FrameLayout layout;
    double westPoint, eastPoint, northPoint, southPoint;
    Location userLocation;
    double aLat, bLat, aLon, bLon;
    Bitmap userLocal, userDest;
    String uidLocal, uidDest;
    String nameLocal, surnameLocal, surnameDest, nameDest;
    Paint textNamePaint, underPaint;
    Bitmap backGround;
    boolean localPhotoPlaced = false;
    boolean localPhotoReady = false;
    String[][] strings;
    int lang = 0;
    RouteHeader header;

    float scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        strings = new String[2][3];

        strings[0][0] = "Me";
        strings[1][0] = "Я";
        strings[0][1] = "Client";
        strings[1][1] = "Клиент";
        strings[0][2] = "Route";
        strings[1][2] = "Маршрут";

        scale = getResources().getDisplayMetrics().widthPixels/1080f;

        backGround = MainMenuActivity.getBitmapFromAsset(this,"icon_bg_map.png");
        backGround = Bitmap.createScaledBitmap(backGround, (int)(backGround.getWidth()*scale),(int)(backGround.getHeight()*scale),false);

        textNamePaint = new Paint();
        textNamePaint.setTypeface(Typeface.createFromAsset(getAssets(),"font.ttf"));
        textNamePaint.setColor(ResourseColors.colorBlack);
        textNamePaint.setTextSize(39*scale);

        underPaint = new Paint();
        underPaint.setColor(ResourseColors.colorWhite);

        aLat = getIntent().getDoubleExtra("aLat", 0);
        aLon = getIntent().getDoubleExtra("aLon", 0);
        lang = getIntent().getIntExtra("lang", 0);

        uidLocal = FirebaseAuth.getInstance().getUid();
        uidDest = getIntent().getStringExtra("uid");


        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        DirectionsFactory.initialize(this);
        layout = new FrameLayout(this);
        setContentView(layout);


        mapView = new MapView(this);
        mapView.setY(448*scale);
        mapView.setLayoutParams(new FrameLayout.LayoutParams((int)(1080*scale),(int)(getResources().getDisplayMetrics().heightPixels-448*scale)));
        layout.addView(mapView);

        TopLabel label = new TopLabel(this,strings[lang][2],scale);
        layout.addView(label);
        label.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String address = getIntent().getStringExtra("address");
        header = new RouteHeader(this,address,lang);
        header.setY(168*scale);
        layout.addView(header);

        FirebaseDatabase.getInstance().getReference("usersInfo").child(uidDest).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    Map<Object, Object> tasks2 = ((Map<Object, Object>) dataSnapshot.getValue());
                    String name = strings[lang][1];
                    String surname = "";//(String)tasks2.get("surname");
                    String photoURL = (String)tasks2.get("photoURL");
                    System.out.println("abab local name-"+name+" surname-"+surname+" photo-"+photoURL);

                    nameDest = name;
                    surnameDest = surname;

                    Glide.with(RouteActivity.this)
                            .load(photoURL)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    Rect bounds = new Rect();
                                    textNamePaint.getTextBounds(nameDest+" "+surnameDest,0, (nameDest+" "+surnameDest).length(),bounds);
                                    userDest = Bitmap.createBitmap((int)(bounds.width()*2+backGround.getWidth()+150*scale),backGround.getHeight()*2,Bitmap.Config.ARGB_8888);
                                    Canvas canvas = new Canvas(userDest);
                                    canvas.drawRect(userDest.getWidth()/2,31*scale,userDest.getWidth()/2+backGround.getWidth()/2+25*scale+bounds.width(),111*scale,underPaint);
                                    canvas.drawCircle(userDest.getWidth()/2+backGround.getWidth()/2+25*scale+bounds.width(),(31+111)*scale/2,(31+111)*scale/2-31*scale,underPaint);
                                    canvas.drawBitmap(backGround,userDest.getWidth()/2-backGround.getWidth()/2,0,underPaint);
                                    resource = Bitmap.createScaledBitmap(resource,(int)(103*scale),(int)(103*scale),false);
                                    canvas.drawBitmap(getCroppedBitmap(resource),userDest.getWidth()/2-resource.getWidth()/2,20*scale,underPaint);
                                    canvas.drawText(nameDest+" "+surnameDest,userDest.getWidth()/2+backGround.getWidth()/2+20*scale,86*scale,textNamePaint);

                                    Point point = new Point(aLat, aLon);
                                    ImageProvider provider = ImageProvider.fromBitmap(addShadow(userDest,(int)(userDest.getWidth()),(int)(userDest.getHeight()),ResourseColors.colorBlack,2,1*scale,1*scale));
                                    mapView.getMap().getMapObjects().addPlacemark(point, provider);
                                }
                            });

                }else{
                    System.out.println("abab null local");
                    Toast.makeText(RouteActivity.this,"произошла ошибка, сообщите в поддержку",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("usersInfo").child(uidLocal).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    Map<Object, Object> tasks2 = ((Map<Object, Object>) dataSnapshot.getValue());
                    String name = strings[lang][0];
                    String surname = "";
                    String photoURL = (String)tasks2.get("photoURL");
                    System.out.println("abab local name-"+name+" surname-"+surname+" photo-"+photoURL);

                    nameLocal = name;
                    surnameLocal = surname;

                    Glide.with(RouteActivity.this)
                            .load(photoURL)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    Rect bounds = new Rect();
                                    textNamePaint.getTextBounds(nameLocal+" "+surnameLocal,0, (nameLocal+" "+surnameLocal).length(),bounds);
                                    userLocal = Bitmap.createBitmap((int)(bounds.width()*2+backGround.getWidth()+150*scale),backGround.getHeight()*2,Bitmap.Config.ARGB_8888);
                                    Canvas canvas = new Canvas(userLocal);
                                    canvas.drawRect(userLocal.getWidth()/2,31*scale,userLocal.getWidth()/2+backGround.getWidth()/2+25*scale+bounds.width(),111*scale,underPaint);
                                    canvas.drawCircle(userLocal.getWidth()/2+backGround.getWidth()/2+25*scale+bounds.width(),(31+111)*scale/2,(31+111)*scale/2-31*scale,underPaint);
                                    canvas.drawBitmap(backGround,userLocal.getWidth()/2-backGround.getWidth()/2,0,underPaint);
                                    resource = Bitmap.createScaledBitmap(resource,(int)(103*scale),(int)(103*scale),false);
                                    canvas.drawBitmap(getCroppedBitmap(resource),userLocal.getWidth()/2-resource.getWidth()/2,20*scale,underPaint);
                                    canvas.drawText(nameLocal+" "+surnameLocal,userLocal.getWidth()/2+backGround.getWidth()/2+20*scale,86*scale,textNamePaint);
                                    localPhotoReady = true;
                                    if(userLocation !=null) {
                                        Point point = new Point(userLocation.getLatitude(), userLocation.getLongitude());
                                        ImageProvider provider = ImageProvider.fromBitmap(addShadow(userLocal,(int)(userLocal.getWidth()),(int)(userLocal.getHeight()),ResourseColors.colorBlack,2,1*scale,1*scale));
                                        mapView.getMap().getMapObjects().addPlacemark(point, provider);
                                        localPhotoPlaced = true;
                                    }else{
                                        localPhotoPlaced = false;
                                    }
                                }
                            });

                }else{
                    System.out.println("abab null local");
                    Toast.makeText(RouteActivity.this,"произошла ошибка, сообщите в поддержку",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onDrivingRoutes(List<DrivingRoute> routes) {
        //  for (DrivingRoute route : routes) {
        mapObjects.addPolyline(routes.get(0).getGeometry()).setStrokeColor(ResourseColors.colorBlue);
        List<Point> route = routes.get(0).getGeometry().getPoints();
        Point previous = routes.get(0).getGeometry().getPoints().get(0);
        float range = 0;
        for(Point point : route){
            Location a = new Location("");
            a.setLatitude(previous.getLatitude());
            a.setLongitude(previous.getLongitude());
            Location b = new Location("");
            b.setLatitude(point.getLatitude());
            b.setLongitude(point.getLongitude());
            range+=a.distanceTo(b);
            previous = point;
        }
        System.out.println("abab range-"+range);
        header.range = range/1000f;
        header.timeByCar = 1+range/1000f/60f*60;
        header.timeByWalk = range/1000f/5f*60;
        header.invalidate();

    }

    @Override
    public void onDrivingRoutesError(Error error) {
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
    }

    private void submitRequest() {
        DrivingOptions options = new DrivingOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(
                ROUTE_START_LOCATION,
                new ArrayList<Point>(),
                new ArrayList<DrivingArrivalPoint>(),
                RequestPointType.WAYPOINT));
        requestPoints.add(new RequestPoint(
                ROUTE_END_LOCATION,
                new ArrayList<Point>(),
                new ArrayList<DrivingArrivalPoint>(),
                RequestPointType.WAYPOINT));
        drivingSession = drivingRouter.requestRoutes(requestPoints, options, this);

    }

    private boolean moved = false;

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
                    bLon = location.getLongitude();
                    bLat = location.getLatitude();
                    userLocation = location;
                    if (location != null) {
                        if(!moved) {
                            if (aLon < bLon) {
                                westPoint = aLon;
                                eastPoint = bLon;
                            } else {
                                westPoint = bLon;
                                eastPoint = aLon;
                            }

                            if (aLat > bLat) {
                                northPoint = aLat;
                                southPoint = bLat;
                            } else {
                                northPoint = bLat;
                                southPoint = aLat;
                            }

                            ROUTE_END_LOCATION = new Point(aLat, aLon);
                            ROUTE_START_LOCATION = new Point(bLat, bLon);
                            SCREEN_CENTER = new Point(
                                    (ROUTE_START_LOCATION.getLatitude() + ROUTE_END_LOCATION.getLatitude()) / 2,
                                    (ROUTE_START_LOCATION.getLongitude() + ROUTE_END_LOCATION.getLongitude()) / 2);


                            Point westNorth = new Point(northPoint + 0.01, westPoint - 0.01);
                            Point eastSouth = new Point(southPoint - 0.01, eastPoint + 0.01);
                            BoundingBox box = new BoundingBox(westNorth, eastSouth);
                            CameraPosition camPos = mapView.getMap().cameraPosition(box);


                            mapView.getMap().move(new CameraPosition(
                                    SCREEN_CENTER, 12, 0, 0));
                            drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
                            mapObjects = mapView.getMap().getMapObjects().addCollection();

                            mapView.getMap().move(camPos);
                            if(localPhotoReady) {
                                Point point = new Point(userLocation.getLatitude(), userLocation.getLongitude());
                                ImageProvider provider = ImageProvider.fromBitmap(userLocal);
                                mapView.getMap().getMapObjects().addPlacemark(point, provider);
                                localPhotoPlaced = true;
                            }
                            moved = true;
                            submitRequest();
                        }


                    }
                }
            }, null);
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public Bitmap addShadow(Bitmap bm, int dstWidth, int dstHeight, int color, int size, float dx, float dy) {
        Bitmap mask = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ALPHA_8);

        Matrix scaleToFit = new Matrix();
        RectF src = new RectF(0, 0, bm.getWidth(), bm.getHeight());
        RectF dst = new RectF(0, 0, dstWidth - dx, dstHeight - dy);
        scaleToFit.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        Matrix dropShadow = new Matrix(scaleToFit);
        dropShadow.postTranslate(dx, dy);

        Canvas maskCanvas = new Canvas(mask);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskCanvas.drawBitmap(bm, scaleToFit, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        maskCanvas.drawBitmap(bm, dropShadow, paint);

        BlurMaskFilter filter = new BlurMaskFilter(size, BlurMaskFilter.Blur.OUTER);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setMaskFilter(filter);
        paint.setFilterBitmap(true);

        Bitmap ret = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        Canvas retCanvas = new Canvas(ret);
        retCanvas.drawBitmap(mask, 0,  0, paint);
        retCanvas.drawBitmap(bm, scaleToFit, null);
        mask.recycle();
        return ret;
    }

}
