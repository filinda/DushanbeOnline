package beka.com.bk.dushanbeonline;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Map;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import beka.com.bk.dushanbeonline.custom_views.CustomPublishBtn;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import yandex_map_2.MapView2;

public class MapActivityYandex extends AppCompatActivity {

    FrameLayout layout;
    MapView2 mapView;
    float scale;
    double lat, lon;
    String url1, resp = "null";
    TextView adressTx;
    ImageView pick;
    int lang = 0;
    String locale = "";
    String[][] strings;
    ValueAnimator animator;
    ImageView positionButton;
    DisplayMetrics dm;
    LocationManager lm;
    private static final int PERMISSIONS_REQUEST = 100;

    boolean mooved = false;
    ImageView picker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        scale = getResources().getDisplayMetrics().widthPixels/1080f;
        
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startTracker();
        }
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTracker();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

        layout = new FrameLayout(this);
        setContentView(layout);

        lang = getIntent().getIntExtra("lang", 0);
        if (lang == 0) {
            locale = "&lang=en_US";
        }
        if (lang == 1) {
            locale = "&lang=ru_RU";
        }

        strings = new String[2][4];
        strings[0][0] = "By coordinates";
        strings[1][0] = "По координатам";
        strings[0][1] = "Enter your address within Tajikistan";
        strings[1][1] = "Укажите адрес в пределах Таджикистана";
        strings[0][2] = "READY";
        strings[1][2] = "ГОТОВО";

        design();
    }

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
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
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
                        if (!mooved) {
                            Animation animation = new Animation(Animation.Type.LINEAR,1);
                            mapView.getMap().move(new CameraPosition(new Point(location.getLatitude(), location.getLongitude()), 16, 0, 0), animation, new Map.CameraCallback() {
                                @Override
                                public void onMoveFinished(boolean b) {
                                    getAdress();
                                    mooved = true;
                                }
                            });

                        }
                    }
                }
            }, null);
        }
    }

    public void design() {
        dm = getResources().getDisplayMetrics();
        scale = dm.widthPixels / 1080f;


        mapView = new MapView2(this);
        mapView.getMap().move(
                new CameraPosition(new Point(38.569962, 68.772682), 12.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        layout.addView(mapView);
        mapView.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, dm.heightPixels - (int) (141 * scale)));

        mapView.addOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                }
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    getAdress();
                }
                return false;
            }
        });

        adressTx = new TextView(this);
        adressTx.setTypeface(Typeface.createFromAsset(getAssets(), "font.ttf"));
        adressTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45 * scale);
        adressTx.setX(0 * scale);
        adressTx.setPadding((int) (130 * scale), (int) (40 * scale), 0, 0);
        adressTx.setY(1 * scale);
        adressTx.setBackgroundColor(ResourseColors.colorWhite);
        adressTx.setTextColor(ResourseColors.colorBlack);
        adressTx.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (150 * scale)));
        layout.addView(adressTx);

        pick = new ImageView(this);
        pick.setScaleType(ImageView.ScaleType.MATRIX);
        pick.setY(35 * scale);
        pick.setX(45 * scale);
        pick.setImageBitmap(Bitmap.createScaledBitmap(MainMenuActivity.getBitmapFromAsset(this, "navi_icon_set.png"), (int) (45 * scale), (int) (70 * scale), false));
        layout.addView(pick);
        readySet();
        posBtnSet();

        picker = new ImageView(this);
        picker.setScaleType(ImageView.ScaleType.MATRIX);
        picker.setX(1080 / 2 * scale - 25.5f * scale);
        picker.setY((dm.heightPixels - 141 * scale) / 2 - 70 * scale);
        picker.setImageBitmap(Bitmap.createScaledBitmap(MainMenuActivity.getBitmapFromAsset(this, "navi_icon_set.png"), (int) (45 * scale), (int) (70 * scale), false));
        layout.addView(picker);

        animator = new ValueAnimator();
        animator.setFloatValues(0, 1, 0);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(1000);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mooved) {
                    positionButton.setAlpha(1f);
                    animation.cancel();
                } else {
                    positionButton.setAlpha((float) animation.getAnimatedValue());
                }
            }
        });

    }


    private void readySet() {
        CustomPublishBtn readybtn = new CustomPublishBtn(this, ResourseColors.colorGreen, strings[lang][2]);
        readybtn.setLayoutParams(new FrameLayout.LayoutParams((int) (1080 * scale), (int) (150 * scale)));
        readybtn.setY(dm.heightPixels - 150 * scale);
        layout.addView(readybtn);

        readybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("adress", adressTx.getText().toString());
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                setResult(1, intent);
                onBackPressed();
            }
        });
    }

    private void posBtnSet() {
        Bitmap posButton = MainMenuActivity.getBitmapFromAsset(this, "posButton.png");
        posButton = Bitmap.createScaledBitmap(posButton, (int) (180 * scale), (int) (180 * scale), false);
        positionButton = new ImageView(this);
        positionButton.setImageBitmap(posButton);
        positionButton.setX(860 * scale);
        positionButton.setY(dm.heightPixels - 450 * scale);
        positionButton.setScaleType(ImageView.ScaleType.MATRIX);
        layout.addView(positionButton);
        positionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mooved = false;
                animator.start();
            }
        });
    }

    private void getAdress() {
        Point point = mapView.getMap().getCameraPosition().getTarget();
        lon = point.getLongitude();
        lat = point.getLatitude();
        url1 = "https://geocode-maps.yandex.ru/1.x/?apikey=e7223c7a-7840-41e3-b4c7-7ef8948c63e4&format=json&geocode=" + lon + "," + lat + locale;
        new RequestTask().execute();
    }


    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            try {
                resp = post(url1);
            } catch (IOException e) {
                e.printStackTrace();
                resp = "error connection";
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(resp);
                String country = jsonObject.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember").getJSONObject(0).getJSONObject("GeoObject").getJSONObject("metaDataProperty").getJSONObject("GeocoderMetaData").getJSONObject("Address").getJSONArray("Components").getJSONObject(0).getString("name");
                if (country.equals("Таджикистан") | country.equals("Tajikistan")) {
                    String adress = jsonObject.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember").getJSONObject(0).getJSONObject("GeoObject").getJSONObject("metaDataProperty").getJSONObject("GeocoderMetaData").getJSONObject("Address").getString("formatted");
                    adressTx.setText(adress);
                    if (jsonObject.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember").length() < 4) {
                        adressTx.setText(strings[lang][0]);
                    }
                } else {
                    adressTx.setText("");
                    Toast.makeText(MapActivityYandex.this, strings[lang][1], Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    OkHttpClient client = new OkHttpClient();

    String post(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
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
    }

    //https://geocode-maps.yandex.ru/1.x/?apikey=e7223c7a-7840-41e3-b4c7-7ef8948c63e4&format=json&geocode=68.772070,38.559624&lang=en_US
}
