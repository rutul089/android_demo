package app.trirail.com.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

import app.trirail.com.myapplication.database.DBHelper;
import app.trirail.com.myapplication.model.location.LocationList;

public class AddLocationActivity extends AppCompatActivity implements LocationListener {

    private Context mContext;
    private Toolbar toolbar;
    private Button btn_save_location, btn_get_location;
    private EditText et_name,
            et_address,
            et_district,
            et_latitude,
            et_longitude;
    private LocationManager locationManager;
    private DBHelper dbHelper;

    public static boolean checkForValidString(String str) {
        return str != null && !str.trim().equals("") && !str.trim().equals("null") && str.trim().length() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        //--
        mContext = this;
        dbHelper = DBHelper.getInstance(mContext);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Location");

        et_name = findViewById(R.id.et_name);
        et_address = findViewById(R.id.et_address);
        et_district = findViewById(R.id.et_district);
        btn_get_location = findViewById(R.id.btn_get_location);
        et_latitude = findViewById(R.id.et_latitude);
        et_longitude = findViewById(R.id.et_longitude);
        btn_save_location = findViewById(R.id.btn_save_location);
        //--
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        //--
        btn_get_location.setOnClickListener(view -> getLocation());
        btn_save_location.setOnClickListener(view -> addNewLocation());
        //--
    }

    private void addNewLocation() {
        String name = et_name.getText().toString();
        String address = et_address.getText().toString();
        String distric = et_district.getText().toString();
        double lat = Double.parseDouble(et_latitude.getText().toString());
        double lng = Double.parseDouble(et_longitude.getText().toString());

        if (checkForValidString(name) && lat != 0 && lng != 0) {
            LocationList locationList = new LocationList();
            locationList.setName(name);
            locationList.setAddress(address);
            locationList.setDistrict(distric);
            locationList.setLatitude(lat);
            locationList.setLongitude(lng);
            dbHelper.dataItemDao().insertLocation(locationList);

            et_name.setText(null);
            et_address.setText(null);
            et_district.setText(null);
            et_latitude.setText(null);
            et_longitude.setText(null);

            Toast.makeText(mContext, "Location Added..", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Please check value of name , latitude and longitude", Toast.LENGTH_SHORT).show();
        }

    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
//            et_latitude.setEnabled(false);
//            et_longitude.setEnabled(false);

            et_latitude.setText(location.getLatitude() + "");
            et_longitude.setText(location.getLongitude() + "");
        } else {
            et_latitude.setEnabled(true);
            et_longitude.setEnabled(true);
        }

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(mContext, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
