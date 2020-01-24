package com.example.excitingplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> places = new ArrayList<String>();

    static ArrayList<LatLng> locations = new ArrayList<LatLng>();

    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.excitingplaces",Context.MODE_PRIVATE);



        ArrayList<String> longitude = new ArrayList<String>();
        ArrayList<String> latitude = new ArrayList<String>();

        places.clear();

        locations.clear();

        longitude.clear();
        latitude.clear();

        try{
            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));



            longitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lon",ObjectSerializer.serialize(new ArrayList<String>())));

            latitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lat",ObjectSerializer.serialize(new ArrayList<String>())));


            Log.i("Infu","Successful Retrieval");
        }catch (Exception e){
            Log.i("Info","Failed");
            e.printStackTrace();
        }

        if(places.size()>0 && longitude.size()>0 && latitude.size()>0){
            if(places.size() == longitude.size() && longitude.size() == latitude.size()){
                for(int i=0;i<longitude.size();i++){
                    locations.add(new LatLng(Double.parseDouble(latitude.get(i)),Double.parseDouble(longitude.get(i))));
                }
            }

        }
        else{

            Log.i("Infooo","Faaailed");

            Log.i("Sizes",String.valueOf(places.size())+" "+String.valueOf(latitude.size()));

                places.add("Add a new place..");

                locations.add(new LatLng(0,0));

        }

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,places);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);

                intent.putExtra("position",position);

                startActivity(intent);

            }
        });

    }
}