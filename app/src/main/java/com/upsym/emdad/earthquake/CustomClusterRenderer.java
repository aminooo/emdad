package com.upsym.emdad.earthquake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.upsym.emdad.earthquake.main.data.Item;

/**
 * Created by Karo on 11/17/2017.
 */

public class CustomClusterRenderer extends DefaultClusterRenderer<Item> {

    public CustomClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<Item> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(Item item,
                                               MarkerOptions markerOptions) {
        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
        if ("waiting".equals(item.getStatus())){
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        } else if("on_the_way".equals(item.getStatus())){
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        } else if("delivered".equals(item.getStatus())){
            markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        }

        markerOptions.icon(markerDescriptor);
    }

    @Override
    protected void onClusterItemRendered(Item clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }

}