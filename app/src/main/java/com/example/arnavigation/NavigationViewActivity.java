package com.example.arnavigation;

import static com.example.arnavigation.Utils.followingPadding;
import static com.example.arnavigation.Utils.overviewPadding;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;

import com.example.arnavigation.ble.BLEService;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.api.directions.v5.models.StepManeuver;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation;
import com.mapbox.bindgen.Expected;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;
import com.mapbox.navigation.base.TimeFormat;
import com.mapbox.navigation.base.extensions.RouteOptionsExtensions;
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions;
import com.mapbox.navigation.base.formatter.UnitType;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.base.trip.model.RouteProgress;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.core.trip.session.RouteProgressObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi;
import com.mapbox.navigation.ui.maneuver.model.Maneuver;
import com.mapbox.navigation.ui.maneuver.model.ManeuverError;
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView;
import com.mapbox.navigation.ui.maps.camera.NavigationCamera;
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource;
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler;
import com.mapbox.navigation.ui.maps.camera.transition.MapboxNavigationCameraStateTransition;
import com.mapbox.navigation.ui.maps.camera.transition.MapboxNavigationCameraTransition;
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions;
import com.mapbox.navigation.ui.maps.camera.view.MapboxRecenterButton;
import com.mapbox.navigation.ui.maps.camera.view.MapboxRouteOverviewButton;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.RouteLayerConstants;
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi;
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView;
import com.mapbox.navigation.ui.maps.route.arrow.model.InvalidPointError;
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions;
import com.mapbox.navigation.ui.maps.route.arrow.model.UpdateManeuverArrowValue;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineClearValue;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi;
import com.mapbox.navigation.ui.tripprogress.model.DistanceRemainingFormatter;
import com.mapbox.navigation.ui.tripprogress.model.EstimatedTimeToArrivalFormatter;
import com.mapbox.navigation.ui.tripprogress.model.PercentDistanceTraveledFormatter;
import com.mapbox.navigation.ui.tripprogress.model.TimeRemainingFormatter;
import com.mapbox.navigation.ui.tripprogress.model.TripProgressUpdateFormatter;
import com.mapbox.navigation.ui.tripprogress.view.MapboxTripProgressView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kotlin.collections.CollectionsKt;

public class NavigationViewActivity extends AppCompatActivity {
    static Location lStart;
    static Location lEnd;
    static Location mCurrentLocation;
    private final MapboxRouteArrowApi mapboxRouteArrowApi = new MapboxRouteArrowApi();
    ImageView imageView;
    long mLastDataSentTime = 0;
    private MapView mapView_nav;
    private Double des_lng, des_lat;
    private ImageView stopNavigation;
    private CardView tripProgressCard;
    private MapboxRecenterButton mapboxRecenterButton;
    private MapboxRouteOverviewButton mapboxRouteOverviewButton;
    private boolean firstLocationUpdateReceived = false;
    private LocationComponentPlugin locationComponentPlugin;
    private NavigationLocationProvider navigationLocationProvider;
    private MapboxNavigationViewportDataSource mapboxNavigationViewportDataSource;
    private NavigationCamera navigationCamera;
    private CameraAnimationsPlugin cameraAnimationsPlugin;
    private MapboxManeuverApi mapboxManeuverApi;
    private MapboxRouteLineApi mapboxRouteLineApi;
    private MapboxRouteLineView mapboxRouteLineView;
    private MapboxManeuverView maneuverView;
    private MapboxTripProgressApi mapboxTripProgressApi;
    private MapboxTripProgressView mapboxTripProgressView;
    private boolean onRoute = false;
    static MapboxStaticMap mapboxStaticMap;
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

            mapboxStaticMap = MapboxStaticMap.builder().accessToken(getString(R.string.mapbox_access_token))
                    .cameraPoint(Point.fromLngLat(navigationLocationProvider.getLastLocation().getLongitude(), navigationLocationProvider.getLastLocation().getLatitude()))
                    .staticMarkerAnnotations(markers).logo(false).attribution(false).user("manhkamui0502").styleId("cltgjsc1c009i01qp24nyd3or").precision(5)
                    .height(70).width(110).cameraZoom(12)
                    .cameraBearing(navigationLocationProvider.getLastLocation().getBearing())
                    .build();

            if (!onRoute) {
                if(!BLEService.getMapMode()){
                    BLEService.sendToBLE(1, 17, getSpeed(navigationLocationProvider.getLastLocation()), 0, 0);
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
        }
    };
    private MapboxRouteArrowView mapboxRouteArrowView;
    RoutesObserver routesObserver = routesUpdatedResult -> {
        if (!routesUpdatedResult.getNavigationRoutes().isEmpty()) {
            mapboxRouteLineApi.setNavigationRoutes(routesUpdatedResult.getNavigationRoutes(), new MapboxNavigationConsumer<Expected<RouteLineError, RouteSetValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) {
                    Style style = mapView_nav.getMapboxMap().getStyle();
                    if (style != null) {
                        mapboxRouteLineView.renderRouteDrawData(style, routeLineErrorRouteSetValueExpected);
                    }
                }
            });
            mapboxNavigationViewportDataSource.onRouteChanged(CollectionsKt.first(routesUpdatedResult.getNavigationRoutes()));
            mapboxNavigationViewportDataSource.evaluate();
        } else {
            final Style style = mapView_nav.getMapboxMap().getStyle();
            if (style != null) {
                mapboxRouteLineApi.clearRouteLine(new MapboxNavigationConsumer<Expected<RouteLineError, RouteLineClearValue>>() {
                    @Override
                    public void accept(Expected<RouteLineError, RouteLineClearValue> routeLineErrorRouteLineClearValueExpected) {
                        mapboxRouteLineView.renderClearRouteLineValue(style, routeLineErrorRouteLineClearValueExpected);
                    }
                });
                mapboxRouteArrowView.render(style, mapboxRouteArrowApi.clearArrows());
            }
            mapboxNavigationViewportDataSource.clearRouteData();
            mapboxNavigationViewportDataSource.evaluate();
        }
    };
    RouteProgressObserver routeProgressObserver = new RouteProgressObserver() {
        @Override
        public void onRouteProgressChanged(@NonNull RouteProgress routeProgress) {
            onRoute = true;
            mapboxNavigationViewportDataSource.onRouteProgressChanged(routeProgress);
            mapboxNavigationViewportDataSource.evaluate();
            Style style = mapView_nav.getMapboxMap().getStyle();
            if (style != null) {
                Expected<InvalidPointError, UpdateManeuverArrowValue> maneuverArrowResult = mapboxRouteArrowApi.addUpcomingManeuverArrow(routeProgress);
                mapboxRouteArrowView.renderManeuverUpdate(style, maneuverArrowResult);
            }
            Expected<ManeuverError, List<Maneuver>> maneuvers = mapboxManeuverApi.getManeuvers(routeProgress);
            maneuvers.fold(error -> {
                Toast.makeText(NavigationViewActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                return null;
            }, input -> {
                maneuverView.setVisibility(View.VISIBLE);
                maneuverView.renderManeuvers(maneuvers);
                return null;
            });
            mapboxTripProgressView.render(mapboxTripProgressApi.getTripProgress(routeProgress));
            List<StaticMarkerAnnotation> markers = new ArrayList<>();
            markers.add(StaticMarkerAnnotation.builder().iconUrl(Utils.userLocationPuck)
                    .lnglat(Point.fromLngLat(navigationLocationProvider.getLastLocation().getLongitude(), navigationLocationProvider.getLastLocation().getLatitude()))
                    .build());

            markers.add(StaticMarkerAnnotation.builder().iconUrl(Utils.destination)
                    .lnglat(Point.fromLngLat(des_lng, des_lat))
                    .build());

            if (routeProgress.getRoute().geometry() != null) {
                mapboxStaticMap = MapboxStaticMap.builder().accessToken(getString(R.string.mapbox_access_token))
                        .cameraPoint(Point.fromLngLat(navigationLocationProvider.getLastLocation().getLongitude(), navigationLocationProvider.getLastLocation().getLatitude()))
                        .staticMarkerAnnotations(markers).logo(false).attribution(false).user("manhkamui0502").styleId("cltgjsc1c009i01qp24nyd3or").precision(5)
                        .height(70).width(110).cameraZoom(12)
                        .cameraBearing(navigationLocationProvider.getLastLocation().getBearing())
                        .build();
            }
            if (onRoute) {
                if(!BLEService.getMapMode()){
                    extractInfo(routeProgress);
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
        }
    };
    MapboxNavigationObserver mapboxNavigationObserver = new MapboxNavigationObserver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onAttached(@NonNull MapboxNavigation mapboxNavigation) {
            mapboxNavigation.registerRoutesObserver(routesObserver);
            mapboxNavigation.registerLocationObserver(locationObserver);
            mapboxNavigation.registerRouteProgressObserver(routeProgressObserver);
            mapboxNavigation.startTripSession();
        }

        @Override
        public void onDetached(@NonNull MapboxNavigation mapboxNavigation) {
            mapboxNavigation.unregisterRoutesObserver(routesObserver);
            mapboxNavigation.unregisterLocationObserver(locationObserver);
            mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver);
        }
    };
    private Runnable task = new Runnable() {
        public void run() {
            findRoute(Point.fromLngLat(des_lng, des_lat));
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

    private static String obtainManeuverResource(LegStep step) {
        ManeuverMap maneuverMap = new ManeuverMap();
        Log.d("navigating", step.maneuver().type() + step.maneuver().modifier());
        if (step != null) {
            StepManeuver maneuver = step.maneuver();
            if (maneuver.modifier() != null) {
                return maneuverMap.getManeuverResource(maneuver.type() + maneuver.modifier());
            } else {
                return maneuverMap.getManeuverResource(maneuver.type());
            }
        }
        return "starting";
    }

    private String getLegStep(RouteProgress progress) {
        LegStep upComingStep = progress.getCurrentLegProgress().getUpcomingStep();
        if (upComingStep != null) {
            return obtainManeuverResource(upComingStep);
        }
        return "starting";
    }

    private void extractInfo(RouteProgress progress) {
        double currentSpeed = getSpeed(navigationLocationProvider.getLastLocation());
        currentSpeed = (double) Math.round(currentSpeed * 10) / 10;
        double legStepDis = progress.getCurrentLegProgress().getCurrentStepProgress().getDistanceRemaining();
        legStepDis = (double) Math.round(legStepDis * 10) / 10;
        double totalRemainDistance = progress.getDistanceRemaining();
        totalRemainDistance = (double) Math.round(totalRemainDistance * 10) / 10;
        String maneuver = getLegStep(progress);

        int maneuver_id = 0;
        switch (maneuver) {
            case "direction_continue_straight":
                maneuver_id = 1;
                break;
            case "direction_end_left":
                maneuver_id = 2;
                break;
            case "direction_slight_left":
                maneuver_id = 3;
                break;
            case "direction_continue_left":
                maneuver_id = 4;
                break;
            case "direction_off_ramp_slight_left":
                maneuver_id = 5;
                break;
            case "direction_end_right":
                maneuver_id = 6;
                break;
            case "direction_slight_right":
                maneuver_id = 7;
                break;
            case "direction_continue_right":
                maneuver_id = 8;
                break;
            case "direction_off_ramp_slight_right":
                maneuver_id = 9;
                break;
            case "direction_uturn":
                maneuver_id = 10;
                break;
            case "direction_right_uturn":
                maneuver_id = 11;
                break;
            case "direction_roundabout_straight":
                maneuver_id = 12;
                break;
            case "direction_roundabout_left":
                maneuver_id = 13;
                break;
            case "direction_roundabout_right":
                maneuver_id = 14;
                break;
            case "direction_arrive":
                if (totalRemainDistance > 3) {
                    maneuver_id = 1;
                } else {
                    maneuver_id = 15;
                    onRoute = false;
                }
                break;
            case "start":
                maneuver_id = 16;
                break;
        }
        BLEService.sendToBLE(1, maneuver_id, currentSpeed, legStepDis, totalRemainDistance);
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
        imageView = findViewById(R.id.imageView);
        stopNavigation = findViewById(R.id.stop);
        mapView_nav = findViewById(R.id.mapView_nav);
        maneuverView = findViewById(R.id.maneuverView);
        tripProgressCard = findViewById(R.id.tripProgressCard);
        mapboxRecenterButton = findViewById(R.id.recenterButton);
        mapboxTripProgressView = findViewById(R.id.tripProgressView);
        mapboxRouteOverviewButton = findViewById(R.id.routeOverview);

        mapboxNavigationViewportDataSource = new MapboxNavigationViewportDataSource(mapView_nav.getMapboxMap());
        cameraAnimationsPlugin = CameraAnimationsUtils.getCamera(mapView_nav);
        navigationCamera = new NavigationCamera(
                mapView_nav.getMapboxMap(),
                cameraAnimationsPlugin,
                mapboxNavigationViewportDataSource,
                new MapboxNavigationCameraStateTransition(mapView_nav.getMapboxMap(), cameraAnimationsPlugin,
                        new MapboxNavigationCameraTransition(mapView_nav.getMapboxMap(), cameraAnimationsPlugin)));

        cameraAnimationsPlugin.addCameraAnimationsLifecycleListener(new NavigationBasicGesturesHandler(navigationCamera));
        navigationCamera.registerNavigationCameraStateChangeObserver(navigationCameraState -> {
            switch (navigationCameraState) {
                case TRANSITION_TO_FOLLOWING:
                    mapboxRecenterButton.setVisibility(View.INVISIBLE);
                    break;
                case TRANSITION_TO_OVERVIEW:
                    mapboxRecenterButton.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        });

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mapboxNavigationViewportDataSource.setOverviewPadding(overviewPadding);
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mapboxNavigationViewportDataSource.setFollowingPadding(followingPadding);
        }

        DistanceFormatterOptions distanceFormatterOptions = new DistanceFormatterOptions.Builder(NavigationViewActivity.this).unitType(UnitType.METRIC).build();
        mapboxManeuverApi = new MapboxManeuverApi(new MapboxDistanceFormatter(distanceFormatterOptions));

        mapboxTripProgressApi = new MapboxTripProgressApi(new TripProgressUpdateFormatter.Builder(this)
                .distanceRemainingFormatter(new DistanceRemainingFormatter(distanceFormatterOptions))
                .timeRemainingFormatter(new TimeRemainingFormatter(this, Locale.ENGLISH))
                .percentRouteTraveledFormatter(new PercentDistanceTraveledFormatter())
                .estimatedTimeToArrivalFormatter(new EstimatedTimeToArrivalFormatter(this, TimeFormat.TWELVE_HOURS))
                .build());

        MapboxRouteLineOptions mapboxRouteLineOptions = new MapboxRouteLineOptions.Builder(this)
                .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER)
                .build();

        mapboxRouteLineApi = new MapboxRouteLineApi(mapboxRouteLineOptions);
        mapboxRouteLineView = new MapboxRouteLineView(mapboxRouteLineOptions);

        RouteArrowOptions routeArrowOptions = new RouteArrowOptions.Builder(this).withAboveLayerId(RouteLayerConstants.TOP_LEVEL_ROUTE_LINE_LAYER_ID).build();
        mapboxRouteArrowView = new MapboxRouteArrowView(routeArrowOptions);

        initNavigation();
        mapView_nav.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/dark-v11");
        stopNavigation.setOnClickListener(view -> clearRouteAndStopNavigation());
        mapboxRecenterButton.setOnClickListener(view -> {
            navigationCamera.requestNavigationCameraToFollowing();
            mapboxRouteOverviewButton.showTextAndExtend(1500L);
        });
        mapboxRouteOverviewButton.setOnClickListener(view -> {
            navigationCamera.requestNavigationCameraToOverview();
            mapboxRecenterButton.showTextAndExtend(1500L);
        });
        Handler handler = new Handler();
        handler.postDelayed(task, 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BLEService.sendToBLE(1, 16, 0, 0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapboxManeuverApi.cancel();
        mapboxRouteLineApi.cancel();
        mapboxRouteLineView.cancel();
        MapboxNavigationApp.unregisterObserver(mapboxNavigationObserver);
        BLEService.sendToBLE(0, 16, 0, 0, 0);
        finish();
    }

    private void initNavigation() {
        MapboxNavigationApp.setup(
                new NavigationOptions.Builder(NavigationViewActivity.this)
                        .accessToken(getString(R.string.mapbox_access_token))
                        .build()
        );
        MapboxNavigationApp.attach(this);
        MapboxNavigationApp.registerObserver(mapboxNavigationObserver);
        locationComponentPlugin = LocationComponentUtils.getLocationComponent(mapView_nav);
        locationComponentPlugin.setLocationProvider(navigationLocationProvider);
        LocationPuck2D locationPuck2D = new LocationPuck2D();
        locationPuck2D.setTopImage(AppCompatResources.getDrawable(NavigationViewActivity.this, R.drawable.user_puck_icon));
        locationPuck2D.setShadowImage(AppCompatResources.getDrawable(NavigationViewActivity.this, R.drawable.user_icon_shadow));
        locationComponentPlugin.setLocationPuck(locationPuck2D);
        locationComponentPlugin.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void findRoute(Point destination) {
        Location originLocation = navigationLocationProvider.getLastLocation();
        assert originLocation != null;
        Point originPoint = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());

        RouteOptions routeOptions = RouteOptionsExtensions.applyDefaultNavigationOptions(
                RouteOptions.builder()
                        .coordinatesList(CollectionsKt.listOf(originPoint, destination))
                        .bearingsList(CollectionsKt.listOf(Bearing.builder()
                                .angle(originLocation.getBearing())
                                .degrees(45.0)
                                .build(), null))
                        .layersList(CollectionsKt.listOf(Objects.requireNonNull(MapboxNavigationApp.current()).getZLevel(), null))
                , DirectionsCriteria.PROFILE_DRIVING).build();

        Objects.requireNonNull(MapboxNavigationApp.current()).requestRoutes(routeOptions, new NavigationRouterCallback() {
            @Override
            public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull RouterOrigin routerOrigin) {
                setRouteAndStartNavigation(list);
            }

            @Override
            public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
            }

            @Override
            public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {

            }
        });
    }

    private void setRouteAndStartNavigation(List<NavigationRoute> routes) {
        Objects.requireNonNull(MapboxNavigationApp.current()).setNavigationRoutes(routes);
        mapboxRouteOverviewButton.setVisibility(View.VISIBLE);
        tripProgressCard.setVisibility(View.VISIBLE);
        navigationCamera.requestNavigationCameraToOverview();
    }

    private void clearRouteAndStopNavigation() {
        Objects.requireNonNull(MapboxNavigationApp.current()).setNavigationRoutes(new ArrayList<>());
        maneuverView.setVisibility(View.INVISIBLE);
        mapboxRouteOverviewButton.setVisibility(View.INVISIBLE);
        tripProgressCard.setVisibility(View.INVISIBLE);
        finish();
    }
}
