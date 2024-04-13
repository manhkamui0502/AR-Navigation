package com.example.arnavigation;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.arnavigation.ble.BLEScanner;
import com.example.arnavigation.ble.BLEService;
import com.example.arnavigation.fullmapdisplay.InternetConnect;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImpl;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.search.ApiType;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchEngine;
import com.mapbox.search.SearchEngineSettings;
import com.mapbox.search.ServiceProvider;
import com.mapbox.search.offline.OfflineResponseInfo;
import com.mapbox.search.offline.OfflineSearchEngine;
import com.mapbox.search.offline.OfflineSearchEngineSettings;
import com.mapbox.search.offline.OfflineSearchResult;
import com.mapbox.search.record.HistoryDataProvider;
import com.mapbox.search.record.HistoryRecord;
import com.mapbox.search.result.SearchResult;
import com.mapbox.search.result.SearchSuggestion;
import com.mapbox.search.ui.adapter.engines.SearchEngineUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.DistanceUnitType;
import com.mapbox.search.ui.view.SearchMode;
import com.mapbox.search.ui.view.SearchResultsView;
import com.mapbox.search.ui.view.place.SearchPlace;
import com.mapbox.search.ui.view.place.SearchPlaceBottomSheetView;

import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    static FloatingActionButton floatingActionButton, freeDriveButton;
    static LocationEngine locationEngine;
    static BLEService bleService;
    private static MapView mapView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SearchView searchView;
    SearchResultsView searchResultsView;
    SearchEngineUiAdapter searchEngineUiAdapter;
    SearchPlaceBottomSheetView searchPlaceView;
    SearchEngine searchEngine;
    OfflineSearchEngine offlineSearchEngine;
    HistoryDataProvider historyDataProvider;
    ApiType apiType;
    LinearLayout infoWindow;
    TextView locationTextView;
    Button navigateButton;
    ImageView bluetoothStatusImg;
    TextView bluetoothStatus;
    LocationManager locationManager;
    private boolean deviceConnected = false;
    private static Point curentpoint;
    private static CircleAnnotationManager circleAnnotationManager;
    private static AnnotationPlugin annotationPlugin;
    private static LocationComponentPlugin locationComponentPlugin;
    private static final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            double zoomLevel = mapView.getMapboxMap().getCameraState().getZoom();
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).zoom(zoomLevel).build());
        }
    };
    private static final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            double zoomLevel = mapView.getMapboxMap().getCameraState().getZoom();
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(zoomLevel).build());
            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
        }
    };
    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            getLocationComponent(mapView).removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            floatingActionButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
        }
    };
    public ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            BLEService.LocalBinder localBinder = (BLEService.LocalBinder) binder;
            bleService = localBinder.getService();
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };
    private final OnMapClickListener onMapClickListener = new OnMapClickListener() {
        @Override
        public boolean onMapClick(@NonNull Point point) {
            double zoomLevel = mapView.getMapboxMap().getCameraState().getZoom();
            locationTextView.setText(String.valueOf(point.longitude() + "\n" + point.latitude()));
            navigateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToNavigationActivity(point);
                }
            });
            infoWindow.setVisibility(View.VISIBLE);
            showMarker(point, zoomLevel);
            return false;
        }
    };

    static void showMarker(Point coordinate, double v) {
        circleAnnotationManager.deleteAll();
        updateCamera(coordinate, v);
        locationComponentPlugin.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        CircleAnnotationOptions circleAnnotationOptions = new CircleAnnotationOptions().withPoint(coordinate).withCircleRadius(8.0).withCircleColor("#BB2A2A").withCircleStrokeWidth(2.0).withCircleStrokeColor("#ffffff");
        CircleAnnotation circleAnnotation = circleAnnotationManager.create(circleAnnotationOptions);
        getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(coordinate));
        floatingActionButton.show();
    }

    private static void updateCamera(Point point, double v) {
        MapAnimationOptions mapAnimationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraAnimationsPlugin cameraAnimationsPlugin = CameraAnimationsUtils.getCamera(mapView);
        cameraAnimationsPlugin.easeTo(new CameraOptions.Builder().center(point).zoom(v).build(), mapAnimationOptions);
    }

    public void navigateToNavigationActivity(Point point) {
        Intent intent = new Intent(MainActivity.this, NavigationViewActivity.class);
        intent.putExtra("destination_lng", point.longitude());
        intent.putExtra("destination_lat", point.latitude());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);
        infoWindow = findViewById(R.id.infoWindow);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        locationTextView = findViewById(R.id.locationInfo);
        navigateButton = findViewById(R.id.navigateButton);
        bluetoothStatus = findViewById(R.id.bluetooth_status);
        searchPlaceView = findViewById(R.id.search_place_view);
        freeDriveButton = findViewById(R.id.freeDrive);
        floatingActionButton = findViewById(R.id.focusLocation);
        searchResultsView = findViewById(R.id.search_results_view);
        bluetoothStatusImg = findViewById(R.id.bluetooth_status_img);

        locationEngine = LocationEngineProvider.getBestLocationEngine(MainActivity.this);
        historyDataProvider = ServiceProvider.getInstance().historyDataProvider();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration(DistanceUnitType.METRIC)));
        searchResultsView.setVisibility(View.GONE);
        infoWindow.setVisibility(View.GONE);
        annotationPlugin = new AnnotationPluginImpl();

        searchPlaceView.initialize(new CommonSearchViewConfiguration(DistanceUnitType.METRIC));
        searchPlaceView.setVisibility(View.GONE);
        searchPlaceView.setShareButtonVisible(false);
        searchPlaceView.setFavoriteButtonVisible(false);
        searchPlaceView.addOnCloseClickListener(new SearchPlaceBottomSheetView.OnCloseClickListener() {
            @Override
            public void onCloseClick() {
                //searchPlaceView.hide();
                circleAnnotationManager.deleteAll();
                searchPlaceView.setVisibility(View.GONE);
            }
        });

        searchPlaceView.addOnNavigateClickListener(new SearchPlaceBottomSheetView.OnNavigateClickListener() {
            @Override
            public void onNavigateClick(@NonNull SearchPlace searchPlace) {
                navigateToNavigationActivity(searchPlace.getCoordinate());
            }
        });

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_scan_device) {
                    startActivity(new Intent(MainActivity.this, BLEScanner.class));
                } else if (item.getItemId() == R.id.nav_access_hotspot) {
                    if (deviceConnected == true) {
                        locationComponentPlugin.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                        startActivity(new Intent(MainActivity.this, InternetConnect.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Connect to BLE device first!", Toast.LENGTH_SHORT).show();
                    }
                } else if ((item.getItemId() == R.id.nav_dis_device) && (deviceConnected == true)) {
                    bleService.disconnect();
                    bluetoothStatus.setText(R.string.glasses_not_connected);
                    bluetoothStatus.setBackground(getDrawable(R.drawable.bluetooth_status_background_2));
                    bluetoothStatusImg.setImageDrawable(getDrawable(R.drawable.baseline_bluetooth));
                    navigationView.getMenu().findItem(R.id.connected_device).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_access_hotspot).setEnabled(false);
                    deviceConnected = false;
                }
                return false;
            }
        });

        mapView.getMapboxMap().loadStyleUri(("mapbox://styles/mapbox/dark-v11"), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(14.0).build());
                locationComponentPlugin = getLocationComponent(mapView);
                locationComponentPlugin.setEnabled(true);

                annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);
                circleAnnotationManager = (CircleAnnotationManager) annotationPlugin.createAnnotationManager(AnnotationType.CircleAnnotation, null);

                LocationPuck2D locationPuck2D = new LocationPuck2D();
                locationPuck2D.setBearingImage(AppCompatResources.getDrawable(MainActivity.this, R.drawable.user_bearing_icon));
                locationPuck2D.setTopImage(AppCompatResources.getDrawable(MainActivity.this, R.drawable.my_location));
                locationPuck2D.setShadowImage(AppCompatResources.getDrawable(MainActivity.this, R.drawable.user_icon_shadow));
                locationComponentPlugin.setLocationPuck(locationPuck2D);

                locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                getGestures(mapView).addOnMoveListener(onMoveListener);
                getGestures(mapView).addOnMapClickListener(onMapClickListener);
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        double zoomLevel = mapView.getMapboxMap().getCameraState().getZoom();
                        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                        getGestures(mapView).addOnMoveListener(onMoveListener);
                        floatingActionButton.hide();
                    }
                });

                freeDriveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (deviceConnected == false) {
                            Toast.makeText(MainActivity.this, "Connect to glasses first", 0).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, FreeDriveActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            BLEService.sendToBLE(2, 17, 0, 0, 0);
                            startActivity(intent);
                        }

                    }
                });
            }
        });
        apiType = ApiType.GEOCODING;
        //apiType = ApiType.SBS;
        searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(apiType, new SearchEngineSettings(getString(R.string.mapbox_access_token)));
        offlineSearchEngine = OfflineSearchEngine.create(new OfflineSearchEngineSettings(getString(R.string.mapbox_access_token)));
        searchEngineUiAdapter = new SearchEngineUiAdapter(searchResultsView, searchEngine, offlineSearchEngine, locationEngine, historyDataProvider);
        searchEngineUiAdapter.setSearchMode(SearchMode.AUTO);
        searchEngineUiAdapter.addSearchListener(new SearchEngineUiAdapter.SearchListener() {
            @Override
            public void onSuggestionsShown(@NonNull List<SearchSuggestion> list, @NonNull ResponseInfo responseInfo) {

            }

            @Override
            public void onSearchResultsShown(@NonNull SearchSuggestion searchSuggestion, @NonNull List<SearchResult> list, @NonNull ResponseInfo responseInfo) {
                closeSearchView();
            }

            @Override
            public void onOfflineSearchResultsShown(@NonNull List<OfflineSearchResult> list, @NonNull OfflineResponseInfo offlineResponseInfo) {

            }

            @Override
            public boolean onSuggestionSelected(@NonNull SearchSuggestion searchSuggestion) {
                return false;
            }

            @Override
            public void onSearchResultSelected(@NonNull SearchResult searchResult, @NonNull ResponseInfo responseInfo) {
                closeSearchView();
                searchPlaceView.open(SearchPlace.createFromSearchResult(searchResult, responseInfo));
                searchPlaceView.setVisibility(View.VISIBLE);
                infoWindow.setVisibility(View.GONE);
                showMarker(searchResult.getCoordinate(), 12);
            }

            @Override
            public void onOfflineSearchResultSelected(@NonNull OfflineSearchResult offlineSearchResult, @NonNull OfflineResponseInfo offlineResponseInfo) {
                closeSearchView();
                searchPlaceView.open(SearchPlace.createFromOfflineSearchResult(offlineSearchResult));
                searchPlaceView.setVisibility(View.VISIBLE);
                showMarker(offlineSearchResult.getCoordinate(), 12);
            }

            @Override
            public void onError(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error happened: $e", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHistoryItemClick(@NonNull HistoryRecord historyRecord) {
                closeSearchView();
                searchPlaceView.open(SearchPlace.createFromIndexableRecord(historyRecord, null));
                searchPlaceView.setVisibility(View.VISIBLE);
                showMarker(historyRecord.getCoordinate(), 12);
            }

            @Override
            public void onPopulateQueryClick(@NonNull SearchSuggestion searchSuggestion, @NonNull ResponseInfo responseInfo) {

            }

            @Override
            public void onFeedbackItemClick(@NonNull ResponseInfo responseInfo) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResultsView.setVisibility(View.VISIBLE);
                searchEngineUiAdapter.search(newText);
                return false;
            }
        });
    }

    private void closeSearchView() {
        toolbar.collapseActionView();
        searchResultsView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (searchResultsView.getVisibility() != View.GONE || infoWindow.getVisibility() != View.GONE || drawerLayout.isDrawerOpen(GravityCompat.START) || searchPlaceView.getVisibility() != View.GONE) {
            searchResultsView.setVisibility(View.GONE);
            searchPlaceView.setVisibility(View.GONE);
            infoWindow.setVisibility(View.GONE);
            drawerLayout.closeDrawer(GravityCompat.START);
            circleAnnotationManager.deleteAll();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Thoát ứng dụng")
                    .setMessage("Bạn có chắc chắn muốn thoát khỏi ứng dụng?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            if (!searchPlaceView.isHidden()) {
                if (!searchPlaceView.isHidden()) {
                    circleAnnotationManager.deleteAll();
                    searchPlaceView.hide();
                } else {
                    circleAnnotationManager.deleteAll();
                }
            } else {
                onBackPressedCallback.setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, BLEService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
        deviceConnected = bleService.connection_state();
        if (deviceConnected == true) {
            bluetoothStatus.setText(R.string.glasses_connected);
            bluetoothStatus.setBackground(getDrawable(R.drawable.bluetooth_status_background));
            bluetoothStatusImg.setImageDrawable(getDrawable(R.drawable.baseline_bluetooth_connected_32));

            navigationView.getMenu().findItem(R.id.connected_device).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_access_hotspot).setEnabled(true);
            navigationView.getMenu().findItem(R.id.connected_device).setTitle(String.valueOf(bleService.getmConnGatt().getDevice().getName()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Enabled GPS on your devide", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        bleService.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }
}