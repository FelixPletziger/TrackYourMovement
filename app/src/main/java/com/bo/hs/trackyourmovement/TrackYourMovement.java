package com.bo.hs.trackyourmovement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TrackYourMovement extends AppCompatActivity {

    private Switch gps;
    private TextView lat;
    private TextView lon;
    private TextView speed;
    private ListView coordinates;

    private LocationManager locationManager;
    private LocationListener locationListener;

    DecimalFormat coorfor = new DecimalFormat("0.00000");
    DecimalFormat speedfor = new DecimalFormat("0");

    ArrayList<String> list = new ArrayList<String>(); /** Items entered by the user is stored in this ArrayList variable */
    ArrayAdapter<String> adapter; /** Declaring an ArrayAdapter to set items to ListView */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_your_movement);

        gps   = (Switch) findViewById(R.id.gps_switch);
        lat   = (TextView) findViewById(R.id.lat);
        lon   = (TextView) findViewById(R.id.lon);
        speed = (TextView) findViewById(R.id.speed);

        coordinates = (ListView) findViewById(R.id.coorlist);

        list.add("Latitude:\t\tLongitude:\t\tGeschwindigkeit:");
        /** Defining the ArrayAdapter to set items to ListView */
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        coordinates.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(lat.getText()!="" && lon.getText()!="") {
                    if (location.hasSpeed()){
                        list.add(coorfor.format(location.getLatitude()) + "\t\t\t" + coorfor.format(location.getLongitude()) + "\t\t\t\t\t" + speedfor.format(location.getSpeed()));
                    }else{
                        list.add(coorfor.format(location.getLatitude()) + "\t\t\t" + coorfor.format(location.getLongitude()));
                    }
                    adapter.notifyDataSetChanged();
                }
                lat.setText(coorfor.format(location.getLatitude()));
                lon.setText(coorfor.format(location.getLongitude()));
                speed.setText(coorfor.format(location.getSpeed()));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10 /*Indikator*/);
                return;
            }
        }else{
            configureSwitch();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureSwitch();
                return;
        }
    }

    private void configureSwitch() {
        gps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    /* requestLocationUpdates( provider | minTime in milsec | minDistance in meter | locationListener ) */
                    locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
                }else{
                    locationManager.removeUpdates(locationListener);
                }
            }
        });

    }
}
