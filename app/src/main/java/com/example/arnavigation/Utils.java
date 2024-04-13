package com.example.arnavigation;

import android.content.res.Resources;

import com.mapbox.maps.EdgeInsets;

public class Utils {
    public static final String userLocationPuck = "https://i.postimg.cc/Y9yMFxKy/oroginal-g.png";
    public static final String destination = "https://i.postimg.cc/Njb8RyFr/destination.png";
    private static final float pixelDensity = Resources.getSystem().getDisplayMetrics().density;
    public static EdgeInsets overviewPadding = calculateEdgeInsets(140.0f, 40.0f, 120.0f, 40.0f);
    public static EdgeInsets followingPadding = calculateEdgeInsets(180.0f, 40.0f, 150.0f, 40.0f);

    public static EdgeInsets calculateEdgeInsets(float left, float top, float right, float bottom) {
        return new EdgeInsets(
                (int) (left * pixelDensity),
                (int) (top * pixelDensity),
                (int) (right * pixelDensity),
                (int) (bottom * pixelDensity)
        );
    }
}
