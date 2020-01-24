package com.example.excitingplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;

    LocationListener locationListener;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100000,10,locationListener);
            }
        }

    }

    public void setLocationOnMap(Location location, String title){

        mMap.clear();

        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,12));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        final SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.excitingplaces",Context.MODE_PRIVATE);

        if(intent.getIntExtra("position",0)==0){

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    setLocationOnMap(location,"Your Current Location!");

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100000,10,locationListener);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }

        }
        else{
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(MainActivity.locations.get(intent.getIntExtra("position",0)).latitude);
            location.setLongitude(MainActivity.locations.get(intent.getIntExtra("position",0)).longitude);
            setLocationOnMap(location,MainActivity.places.get(intent.getIntExtra("position",0)));
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this, "Location Added!", Toast.LENGTH_SHORT).show();

                String address = "";

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try{
                    List<Address> listAddress = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

                    if(listAddress.get(0).getThoroughfare()!=null){
                        address+=listAddress.get(0).getThoroughfare()+", ";
                    }
                    if(listAddress.get(0).getSubThoroughfare()!=null) {
                        address+= listAddress.get(0).getSubThoroughfare();
                    }
                }catch (Exception e){

                    e.printStackTrace();
                }
                if(address.equals("")){
                    Date currentTime = Calendar.getInstance().getTime();
                    address = currentTime.toString();
                }
                mMap.addMarker(new MarkerOptions().position(latLng).title(address));

                MainActivity.places.add(address);

                MainActivity.locations.add(latLng);

                MainActivity.arrayAdapter.notifyDataSetChanged();



                try{

                    ArrayList<String> longitudes = new ArrayList<String>();
                    ArrayList<String> lattitudes= new ArrayList<String>();

                    for(int i=0 ; i < MainActivity.locations.size(); i++){
                        lattitudes.add(String.valueOf(MainActivity.locations.get(i).latitude));
                        longitudes.add(String.valueOf(MainActivity.locations.get(i).longitude));
                    }
                    sharedPreferences.edit().putString("places",ObjectSerializer.serialize(MainActivity.places)).apply();
                    sharedPreferences.edit().putString("lat",ObjectSerializer.serialize(lattitudes)).apply();
                    sharedPreferences.edit().putString("lon",ObjectSerializer.serialize(longitudes)).apply();
                    Log.i("SharedPref","Success");
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("SharedPref","Failed");
                }





            }
        });


    }
}
