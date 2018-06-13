package com.example.android.geojson;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "DrawGeojsonLineActivity";

    private MapView mapView;
    private MapboxMap mapboxMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibWFubnVtYXBib3giLCJhIjoiY2poNjEwbDNzMTdwbzJ3bzM3MzVqc2cxNiJ9.5Wr8tMCuDxxtc8cBQqONKg");
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        new DrawGeoJson().execute();
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private class DrawGeoJson extends AsyncTask<Void,Void,List<LatLng>>{

        @Override
        protected List<LatLng> doInBackground(Void... voids) {
            ArrayList<LatLng> points = new ArrayList<>();

            try{
                InputStream inputStream = getAssets().open("example.geojson");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                StringBuilder stringBuilder = new StringBuilder();
                int cp;
                while ((cp=bufferedReader.read()) != -1){
                    stringBuilder.append((char) cp);
                }
                inputStream.close();

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                JSONArray features = jsonObject.getJSONArray("features");
                JSONObject feature = features.getJSONObject(0);
                JSONObject geometry = feature.getJSONObject("geometry");
                if(geometry != null){
                    String type = geometry.getString("type");

                    if(!TextUtils.isEmpty(type) && type.equalsIgnoreCase("LineString")){

                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        for(int i=0;i<coordinates.length();i++){
                            JSONArray coordinate = coordinates.getJSONArray(i);
                            LatLng latLng = new LatLng(coordinate.getDouble(1),coordinate.getDouble(0));
                            points.add(latLng);
                        }
                    }
                }
            }catch (Exception exception){
                Log.e(TAG, "Exception Loading GeoJSON: " + exception.toString());
            }

            return points;
        }
        @Override
        protected void onPostExecute(List<LatLng> points){
            super.onPostExecute(points);

            if (points.size()>0){
                mapboxMap.addPolyline(new PolylineOptions()
                            .addAll(points)
                            .color(Color.parseColor("#3bb2d0"))
                            .width(2));
            }
        }
    }
}
