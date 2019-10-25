package com.d.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.d.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.SphericalUtil;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQ_CODE = 100;
    private GoogleMap mMap;
    private List<Business> businesses = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
        mMap.setInfoWindowAdapter(customInfoWindow);
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                callAPI();
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent mainIntent = new Intent(MapsActivity.this, DetailsActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("SELECTED_RES", (Business) marker.getTag());
                mainIntent.putExtras(b);
                startActivity(mainIntent);
            }
        });
        if (havePermission())
            mMap.setMyLocationEnabled(true);
        else
            askPermission();

        LatLng nw = new LatLng(40.730610, -73.935242);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nw, 14.0f));
    }

    /**
     * Ask permission for location
     */
    private void askPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQ_CODE
        );
    }

    /**
     * get displaying radius
     *
     * @return radius
     */
    public int getRadius() {
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        return (int) SphericalUtil.computeDistanceBetween(
                visibleRegion.farLeft, mMap.getCameraPosition().target);
    }

    /**
     * check if have permission
     *
     * @return if have return true
     */
    private boolean havePermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Call yelp api for get nearest restaurants
     */
    private void callAPI() {
        try {
            LatLng lng = mMap.getCameraPosition().target;
            YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
            YelpFusionApi yelpFusionApi = apiFactory.createAPI("it7OeFverxYHKGr5VL-8bvPVOyKzBNSvt2JzGCUMshxMWiWaNeua6Dwy5tw_FHFgnSKEy3aIkDPV7HyX8TnghW_mAlIhKUWQUuR1yesxT-TGwdghMynnEimNrJixXXYx");
            Map<String, String> params = new HashMap<>();
            params.put("term", "restaurants");
            params.put("latitude", String.valueOf(lng.latitude));
            params.put("longitude", String.valueOf(lng.longitude));
            params.put("radius", String.valueOf(getRadius()));
            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
                    SearchResponse resource = response.body();
                    businesses = resource.getBusinesses();
                    for (Business b : businesses) {
                        setAllMarker(b);
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {

                }
            });
        } catch (IOException e) {
            Log.e("LOGGER", e.getMessage());
        }
    }

    /**
     * Set marker in map position
     *
     * @param b {@link Business}
     */
    private void setAllMarker(Business b) {
        LatLng latLng = new LatLng(b.getCoordinates().getLatitude(), b.getCoordinates().getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(latLng))
                .setTag(b);
    }

    /*Get selected restaurant details from name*/
    private Business getResDetatils(String name) {
        Business wordList = new Business();
        for (int i = 0; i < businesses.size(); i++) {
            if (name.equalsIgnoreCase(businesses.get(i).getName()))
                wordList = businesses.get(i);
        }
        return wordList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean granted = true;
                for (int r : grantResults) {
                    if (r != PackageManager.PERMISSION_GRANTED)
                        granted = false;
                }
                if (granted)
                    mMap.setMyLocationEnabled(true);
            }
        }
    }
}
