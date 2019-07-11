package app.trirail.com.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import app.trirail.com.myapplication.database.DBHelper;
import app.trirail.com.myapplication.model.location.LocationList;
import app.trirail.com.myapplication.model.location.LocationResponseModel;

public class MainActivity extends AppCompatActivity {
    private Button btn_add_location,
            btn_show_location;
    private Context mContext;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        dbHelper = DBHelper.getInstance(mContext);
        //--
        btn_add_location = findViewById(R.id.btn_add_location);
        btn_show_location = findViewById(R.id.btn_show_location);
        //--
        btn_show_location.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, LocationActivity.class);
            startActivity(intent);
        });
        btn_add_location.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, AddLocationActivity.class);
            startActivity(intent);
        });
        //-- Save Location in to DB
        saveLocation();

    }

    private void saveLocation() {
        int count = dbHelper.dataItemDao().locationCount();
        AssetManager assetManager = this.getAssets();
        List<LocationList> locationLists = new ArrayList<>();
        try {
            if (count == 0) {
                InputStream ims = assetManager.open("data.json");
                Gson gson = new Gson();
                Reader reader = new InputStreamReader(ims);
                LocationResponseModel gsonObj = gson.fromJson(reader, LocationResponseModel.class);
                locationLists = gsonObj.getData();
                dbHelper.dataItemDao().insertAllStops(locationLists);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
