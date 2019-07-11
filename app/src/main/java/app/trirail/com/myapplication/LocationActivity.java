package app.trirail.com.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import app.trirail.com.myapplication.database.DBHelper;
import app.trirail.com.myapplication.model.location.LocationList;
import app.trirail.com.myapplication.model.location.LocationResponseModel;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public GoogleMap mMap;
    private Context mContext;
    private Toolbar toolbar;
    private DBHelper dbHelper;
    private List<LocationList> locationLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mContext = this;
        dbHelper = DBHelper.getInstance(mContext);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Location");
        //--
        int count = dbHelper.dataItemDao().locationCount();
        if (count == 0) {
            locationLists = getList();
        } else {
            locationLists = dbHelper.dataItemDao().locationList();
        }

        //--
        if (isServiceOk()) {
            initializeMap();
        } else {
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        //--

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private List<LocationList> getList() {
        //--
        List<LocationList> locationLists = new ArrayList<>();
        AssetManager assetManager = this.getAssets();
        try {
            InputStream ims = assetManager.open("data.json");
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(ims);
            LocationResponseModel gsonObj = gson.fromJson(reader, LocationResponseModel.class);
            locationLists = gsonObj.getData();
            Log.i("--->", "onCreate: " + locationLists);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locationLists;
    }

    private boolean isServiceOk() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        110).show();
            }
            return false;
        }
        return true;
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            getGoogleMapSetting(googleMap);
            moveToDefaultLocation(googleMap);
            addStopMarker(locationLists);
            // -- Show all in service train Detail

            googleMap.setOnMarkerClickListener(this);
        } else {
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

    }

    private void addStopMarker(List<LocationList> locationLists) {
        MarkerOptions markerOptions = new MarkerOptions();
        if (locationLists != null && !locationLists.isEmpty()) {
            for (LocationList data : locationLists) {
                markerOptions.position(new LatLng(data.getLatitude(), data.getLongitude()));
                markerOptions.title(data.getName());
                markerOptions.snippet(data.getAddress() + "," + data.getDistrict());
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                Marker m = mMap.addMarker(markerOptions);
            }
        }
    }

    private void getGoogleMapSetting(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(false);
        googleMap.setMinZoomPreference(0);
        googleMap.setMaxZoomPreference(15);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false); //-- To hide navigation icon ( map toolbar)
        googleMap.isBuildingsEnabled();
        googleMap.setBuildingsEnabled(false);
        googleMap.isIndoorEnabled();
        googleMap.setIndoorEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    private void moveToDefaultLocation(GoogleMap googleMap) {
        LatLng florida = new LatLng(20.5937, 78.9629);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(florida));
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}