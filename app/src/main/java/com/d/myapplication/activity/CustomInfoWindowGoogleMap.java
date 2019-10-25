package com.d.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.d.myapplication.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Location;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    /**
     * Show custom window in map whn click marker
     * @param marker {@link Marker}
     * @return {@link View}
     */
    @Override
    public View getInfoContents(final Marker marker) {
        View d = ((Activity) context).getLayoutInflater().inflate(R.layout.map_custom_infowindow, null);
        final Business bb = (Business) marker.getTag();
        TextView tvName = d.findViewById(R.id.name);
        TextView rating = d.findViewById(R.id.rating);
        TextView address = d.findViewById(R.id.address);

        tvName.setText(bb.getName());
        rating.setText("Rating : " + bb.getRating());
        Location l = bb.getLocation();
        address.setText(l.getAddress1() + ", \n" + l.getCity());
        return d;
    }
}