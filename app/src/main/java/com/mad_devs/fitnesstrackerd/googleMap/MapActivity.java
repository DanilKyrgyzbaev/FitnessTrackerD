package com.mad_devs.fitnesstrackerd.googleMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mad_devs.fitnesstrackerd.App;
import com.mad_devs.fitnesstrackerd.R;
import com.mad_devs.fitnesstrackerd.googleMap.data.DataRoom;
import com.mad_devs.fitnesstrackerd.googleMap.history.ResultActivity;
import com.mad_devs.fitnesstrackerd.googleMap.service.BackgroundLocationUpdateService;
import com.mad_devs.fitnesstrackerd.uttils.LocationUpdateService;
import com.mad_devs.fitnesstrackerd.uttils.PermissionUtils;
import com.mad_devs.fitnesstrackerd.view.MainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.realm.Realm;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, View.OnClickListener {

    private GoogleMap map;
    private CameraPosition cameraPosition;
    private static final int overview = 0;
    private LocationUpdateService mService;
    private Marker mMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mLatestLocation;
    public static ArrayList<Location> mPathLocations = new ArrayList<>();
    public static final String EXTRA_REPLY = "com.example.android.DataRoomlistsql.REPLY";
    private boolean mIsLoaded = false;
    private final LatLng defaultLocation = new LatLng(42.873098, 74.586718);
    private DecimalFormat dfFormat = new DecimalFormat("#,##0.0");
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private Polyline polyline;
    private long lastPause;
    private TextView mDistance;
    private TextView pulse;
    private TextView avg;
    private Button start;
    private Button reset;
    private Button stop;
    private Button save;
    double myDistance = 0;
    private Realm mRealm;
    private Chronometer chronometer;
    private DataRoomDatabase dataRoomDatabase;
    private boolean record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initButton();
        initMap();
        initLocationRequest();

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    public void initButton() {
        mDistance = findViewById(R.id.distance);
        pulse = findViewById(R.id.pulse);
        avg = findViewById(R.id.avg);
        start = findViewById(R.id.start);
        reset = findViewById(R.id.reset);
        stop = findViewById(R.id.stop);
        save = findViewById(R.id.save);
        chronometer = findViewById(R.id.chronometer);
        save.setEnabled(false);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        reset.setOnClickListener(this);
        save.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                record = true;
                locationCallback();
                start.setText("start");
                save.setEnabled(false);
                startService(new Intent(this, BackgroundLocationUpdateService.class));
                if (lastPause != 0) {
                    chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
                    start.setEnabled(false);

                } else {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                }
                start.setEnabled(false);
                stop.setEnabled(true);
                chronometer.start();
                break;
            case R.id.reset:
                save.setEnabled(false);
                start.setText("start");
                myDistance = 0;
                mPathLocations.clear();
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                lastPause = 0;
                mDistance.setText("Distance:  0,00");
                start.setEnabled(true);
                stop.setEnabled(false);
                renderMapData();
                break;
            case R.id.stop:
                locationCallback();
                removeLocationUpdates();
                stopService(new Intent(this, BackgroundLocationUpdateService.class));
                lastPause = SystemClock.elapsedRealtime();
                chronometer.stop();
                chronometer.setEnabled(false);
                chronometer.setEnabled(true);
                myDistance = 0;
                start.setEnabled(true);
                stop.setEnabled(false);
                save.setEnabled(true);
                start.setText("continue");
                record = false;
                break;
            case R.id.save:
                App.dataRoomDao.insert(new DataRoom(0, myDistance, System.currentTimeMillis(),chronometer.getText().toString(), ""));
                save.setEnabled(true);
                start.setText("start");
                Toast.makeText(this, "Save results ", Toast.LENGTH_LONG).show();
                myDistance = 0;
                mPathLocations.clear();
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                lastPause = 0;
                mDistance.setText("Distance:  0,00");
                renderMapData();
                break;
        }
    }

    private void removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private void locationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();

                if (isLocationChanged(location)) {
                    if (record)
                        mPathLocations.add(location);
                    mLatestLocation = location;
                    renderMapData();
                }
            }
        };
        update();
    }

    private boolean isLocationChanged(Location location) {
        if (mLatestLocation == null) return true;

        LatLng latLng = new LatLng(
                mLatestLocation.getLatitude(),
                mLatestLocation.getLongitude()
        );

        LatLng latLngTo = new LatLng(
                location.getLatitude(),
                location.getLongitude()
        );

        double distance = SphericalUtil.computeDistanceBetween(latLng, latLngTo);

        return distance > 1.0;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        if (item.getItemId() == R.id.history) {
            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }


    @Override
    public void onPolylineClick(Polyline polyline) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        enableMyLocation();
        startLocationService();
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        MapsInitializer.initialize(MapActivity.this);
        try {
            //Настройте стиль базовой карты, используя определенный объект JSON
            // в файле raw ресурсов.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e(String.valueOf(map), "Не удалось проанализировать стиль.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(String.valueOf(map), "Не могу найти стиль. Ошибка:", e);
        }

        setupGoogleMapScreenSettings(map);

    }

    private void getLocationPermission() {
        /*
         * Запрос разрешения местоположения, чтобы мы могли получить местоположение устройства
         * Результат запроса на разрешение обрабатывается обратным вызовом ,
         * onRequestPermissionsResult.
         */


        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d("ololo", "Permission granted");
                        update();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Log.d("ololo", "Permission denied");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Log.d("ololo", "Permission RationaleShouldBeShown");
                    }
                }).check();

        //


        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void startLocationService() {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mService = ((LocationUpdateService.LocalBinder) iBinder).getService();
                mService.getLastLocation(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng
                                (task.getResult().getLatitude(), task.getResult().getLongitude())));
                    }
                });

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mService = null;
            }
        };
        bindService(new Intent(this, LocationUpdateService.class), serviceConnection, BIND_AUTO_CREATE);
    }


    private void getDeviceLocation() {
        /*
         * Получить лучшее и самое последнее местоположение устройства, которое может быть нулевым в редких
         * случаи, когда местоположение недоступно.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Установите положение камеры на карте в текущее местоположение устройства.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
//                            Log.d(TAG, "Current location is null. Using defaults.");
//                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void enableMyLocation() {
        if (PermissionUtils.checkLocationPermission(this) && map != null) {
            map.setMyLocationEnabled(true);
            map.setMaxZoomPreference(18);
            map.setMinZoomPreference(15);

        }

    }

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000L);
        mLocationRequest.setFastestInterval(5000L);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void renderMapData() {
        if (mLatestLocation != null) {
            LatLng latLng = new LatLng(
                    mLatestLocation.getLatitude(),
                    mLatestLocation.getLongitude()
            );

            map.clear();
            map.addMarker(new MarkerOptions().position(latLng).title("Current location"));
            renderPath(mPathLocations);
            if (!mIsLoaded) {
                mIsLoaded = true;

                getLocationPermission();
            }
        }
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    public void update() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // здесь, чтобы запросить отсутствующие разрешения, а затем переопределить
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // обрабатывать случай, когда пользователь предоставляет разрешение. Смотри документацию
                // для ActivityCompat # requestPermissions для более подробной информации.
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.getMainLooper()
            );
        } catch (Exception e) {

        }
    }

    private void renderPath(ArrayList<Location> path) {
        if (map == null) return;
        if (path.size() < 2) return;

        PolylineOptions options = new PolylineOptions();

        options.color(Color.BLUE);
        options.width(5f);


        //start i = 0
        //loop: i++, i = 1
        //      i++, i = 2
        //end: loop ++, i = 3

        //start i = 3
        //loop: i++, i = 4
        //      i++, i = 5
        //end: loop ++, i = 6

        //------------------------------------

        //start i = 0
        //loop: i + 1 = 0 + 1
        //      i + 1 = 0 + 1
        //end: loop++, i = 1

        //start i = 10 // From
        //loop: i + 1 = 10 + 1 // To
        //      i + 1 = 10 + 1 // To
        //end: loop++, i = 11

        double distance = 0.0;

        for (int i = 0; i < path.size(); i++) {
            LatLng latLng = new LatLng(
                    path.get(i).getLatitude(),
                    path.get(i).getLongitude()
            );

            if (i < path.size() - 1) {
                LatLng latLngTo = new LatLng(
                        path.get(i + 1).getLatitude(),
                        path.get(i + 1).getLongitude()
                );

                distance += SphericalUtil.computeDistanceBetween(latLng, latLngTo);
            }

            options.add(latLng);
        }

        myDistance = distance;
        mDistance.setText("Distance: " + dfFormat.format(distance) + "m");
        map.addPolyline(options);
    }

}