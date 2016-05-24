package com.gaiay.base.widget.photoview;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.DONUT) public final class VersionedGestureDetector {

    public static GestureDetector newInstance(Context context,
                                              OnGestureListener listener,boolean isForbidDoubleFingersZoom) {
        final int sdkVersion = Build.VERSION.SDK_INT;
        GestureDetector detector;

        if (sdkVersion < Build.VERSION_CODES.ECLAIR) {
            detector = new CupcakeGestureDetector(context);
        } else if (sdkVersion < Build.VERSION_CODES.FROYO) {
            detector = new EclairGestureDetector(context);
        } else {
            if(isForbidDoubleFingersZoom){
                detector = new ForbidDoubleFingersZoomDetector(context);
            }else{
                detector = new FroyoGestureDetector(context);
            }
        }

        detector.setOnGestureListener(listener);

        return detector;
    }
}