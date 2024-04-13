package com.example.arnavigation;

import static com.example.arnavigation.Utils.followingPadding;
import static com.example.arnavigation.Utils.overviewPadding;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.arnavigation.ble.BLEService;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.ui.maps.camera.NavigationCamera;
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource;
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler;
import com.mapbox.navigation.ui.maps.camera.transition.MapboxNavigationCameraStateTransition;
import com.mapbox.navigation.ui.maps.camera.transition.MapboxNavigationCameraTransition;
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions;
import com.mapbox.navigation.ui.maps.camera.view.MapboxRecenterButton;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FreeDriveActivity extends AppCompatActivity {
    static Location lStart;
    static Location lEnd;
    static Location mCurrentLocation;
    long mLastDataSentTime = 0;
    boolean fullMapMode = false;
    private MapView mapView_nav;
    private Double des_lng, des_lat;
    private MapboxRecenterButton mapboxRecenterButton;
    private boolean firstLocationUpdateReceived = false;
    private LocationComponentPlugin locationComponentPlugin;
    private NavigationLocationProvider navigationLocationProvider;
    private MapboxNavigationViewportDataSource mapboxNavigationViewportDataSource;
    private NavigationCamera navigationCamera;
    private CameraAnimationsPlugin cameraAnimationsPlugin;
    private Calendar calendar;
    LocationObserver locationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location location) {
        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            Location enhancedLocation = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(enhancedLocation, locationMatcherResult.getKeyPoints(), null, null);
            mapboxNavigationViewportDataSource.onLocationChanged(enhancedLocation);
            mapboxNavigationViewportDataSource.evaluate();
            if (!firstLocationUpdateReceived) {
                firstLocationUpdateReceived = true;
                navigationCamera.requestNavigationCameraToOverview(new NavigationCameraTransitionOptions
                        .Builder()
                        .maxDuration(0L)
                        .build());
            }
            List<StaticMarkerAnnotation> markers = new ArrayList<>();
            markers.add(StaticMarkerAnnotation.builder().iconUrl(Utils.userLocationPuck)
                    .lnglat(Point.fromLngLat(navigationLocationProvider.getLastLocation().getLongitude(), navigationLocationProvider.getLastLocation().getLatitude()))
                    .build());

            MapboxStaticMap mapboxStaticMap = MapboxStaticMap.builder().accessToken(getString(R.string.mapbox_access_token))
                    .cameraPoint(Point.fromLngLat(navigationLocationProvider.getLastLocation().getLongitude(), navigationLocationProvider.getLastLocation().getLatitude()))
                    .staticMarkerAnnotations(markers).logo(false).attribution(false).user("manhkamui0502").styleId("cltgjsc1c009i01qp24nyd3or").precision(5)
                    .height(70).width(110).cameraZoom(12)
                    .cameraBearing(navigationLocationProvider.getLastLocation().getBearing())
                    .build();

            Log.d("OOk", String.valueOf(BLEService.getMapMode()));
            if(!BLEService.getMapMode()){
                calendar = Calendar.getInstance();
                BLEService.sendToBLE_freeDrive(2, (double) Math.round(getSpeed(enhancedLocation) * 10) / 10, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            } else {
                long currentTime = System.currentTimeMillis();
                if (currentTime - mLastDataSentTime > 700) {
                    String senToBle = mapboxStaticMap.url().toString().substring(136);
                    senToBle = senToBle.substring(0, senToBle.length() - 139);
                    BLEService.writeCharacteristic(senToBle);
                    mLastDataSentTime = currentTime;
                }
            }
        }
    };
    MapboxNavigationObserver mapboxNavigationObserver = new MapboxNavigationObserver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onAttached(@NonNull MapboxNavigation mapboxNavigation) {
            mapboxNavigation.registerLocationObserver(locationObserver);
            mapboxNavigation.startTripSession();
        }

        @Override
        public void onDetached(@NonNull MapboxNavigation mapboxNavigation) {
            mapboxNavigation.stopTripSession();
            mapboxNavigation.unregisterLocationObserver(locationObserver);
        }
    };

    public static double getSpeed(Location location) {
        mCurrentLocation = location;
        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
        } else {
            lEnd = mCurrentLocation;
        }
        return location.getSpeed() * 18 / 5;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            des_lng = getIntent().getDoubleExtra("destination_lng", 0.0);
            des_lat = getIntent().getDoubleExtra("destination_lat", 0.0);
        }
        navigationLocationProvider = new NavigationLocationProvider();
        setContentView(R.layout.activity_navigation);
        mapView_nav = findViewById(R.id.mapView_nav);
        mapboxRecenterButton = findViewById(R.id.recenterButton);
        mapboxNavigationViewportDataSource = new MapboxNavigationViewportDataSource(mapView_nav.getMapboxMap());
        cameraAnimationsPlugin = CameraAnimationsUtils.getCamera(mapView_nav);
        navigationCamera = new NavigationCamera(
                mapView_nav.getMapboxMap(),
                cameraAnimationsPlugin,
                mapboxNavigationViewportDataSource,
                new MapboxNavigationCameraStateTransition(mapView_nav.getMapboxMap(), cameraAnimationsPlugin,
                        new MapboxNavigationCameraTransition(mapView_nav.getMapboxMap(), cameraAnimationsPlugin)));

        cameraAnimationsPlugin.addCameraAnimationsLifecycleListener(new NavigationBasicGesturesHandler(navigationCamera));
        mapboxRecenterButton.setOnClickListener(view -> {
            navigationCamera.requestNavigationCameraToFollowing();
        });
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mapboxNavigationViewportDataSource.setOverviewPadding(overviewPadding);
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mapboxNavigationViewportDataSource.setFollowingPadding(followingPadding);
        }
        initNavigation();
        mapView_nav.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/dark-v11");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapboxNavigationApp.unregisterObserver(mapboxNavigationObserver);
        BLEService.sendToBLE_freeDrive(0, 0, 0, 0, 0);
        finish();
    }

    private void initNavigation() {
        MapboxNavigationApp.setup(
                new NavigationOptions.Builder(FreeDriveActivity.this)
                        .accessToken(getString(R.string.mapbox_access_token))
                        .build()
        );
        MapboxNavigationApp.attach(this);
        MapboxNavigationApp.registerObserver(mapboxNavigationObserver);
        locationComponentPlugin = LocationComponentUtils.getLocationComponent(mapView_nav);
        locationComponentPlugin.setLocationProvider(navigationLocationProvider);
        LocationPuck2D locationPuck2D = new LocationPuck2D();
        locationPuck2D.setTopImage(AppCompatResources.getDrawable(FreeDriveActivity.this, R.drawable.user_puck_icon));
        locationPuck2D.setShadowImage(AppCompatResources.getDrawable(FreeDriveActivity.this, R.drawable.user_icon_shadow));
        locationComponentPlugin.setLocationPuck(locationPuck2D);
        locationComponentPlugin.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
