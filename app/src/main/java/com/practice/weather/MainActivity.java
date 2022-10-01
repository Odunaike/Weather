package com.practice.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.practice.weather.ModelClasses.OpenWeatherMap;
import com.squareup.picasso.Picasso;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView city, temperature, weatherCondition, humidity, maxTemperature, minTemperatue;
    private ImageView imageViewWeather, search;
    private EditText editText;

    //I will get the location of the user using google play service fusedlocationprovider
    FusedLocationProviderClient fusedLocationProviderClient;
    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.textViewCity);
        temperature = findViewById(R.id.textViewTemp);
        weatherCondition = findViewById(R.id.textViewWeatherCondition);
        humidity = findViewById(R.id.textViewHumidity);
        maxTemperature = findViewById(R.id.textViewMaxTemp);
        minTemperatue = findViewById(R.id.textViewMinTemp);
        imageViewWeather = findViewById(R.id.imageViewWeather);
        search = findViewById(R.id.imageViewSearch);
        editText = findViewById(R.id.editTextCity);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = editText.getText().toString();
                if (!cityName.isEmpty()){
                    editText.setText("");
                    Intent i = new Intent(MainActivity.this, SearchedCityActivity.class);
                    i.putExtra("cityname", cityName);
                    startActivity(i);

                }else{
                    Toast.makeText(MainActivity.this, "You should enter a city name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

    }

    /**
     * This method gets any previous location request on the user device.
     * If the location is null, it calls the requestLastLocation() method.
     */
    public void getLastLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                                Log.e("problem", "location is not null");
                                getWeatherData(lat, lon);
                            }else{
                                requestNewLocationData();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "could not get last location", Toast.LENGTH_SHORT).show();
                }
            });

        }else{

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);

        }
    }

    /**
     * If location is null because no previous location request could be retrieved,
     * this method is called to request for a new location.
     */
    @SuppressLint("MissingPermission")
    public void requestNewLocationData(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(50);
        locationRequest.setFastestInterval(10);
        locationRequest.setNumUpdates(1);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        Log.e("problem", "onLocationResult() is working");
                        Location lastLocation = locationResult.getLastLocation();
                        lat = lastLocation.getLatitude();
                        lon = lastLocation.getLongitude();
                        getWeatherData(lat, lon);
                    }
                }, Looper.myLooper());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "request loading", Toast.LENGTH_SHORT).show();
                getLastLocation();
            }
        }
    }

    /**
     * this makes the request to the cloud and gets the data to be displayed based on cordinates
     * @param lat - latitude based on the user device
     * @param lon - longitude based on the user device
     */
    public void getWeatherData(double lat, double lon){
        WeatherAPI weatherAPI = RetrofitWeather.getClient().create(WeatherAPI.class);
        Call<OpenWeatherMap> call = weatherAPI.getWeatherWithLocation(lat, lon);

        // this method will let you retrieve the HTTP request data asynchronously i.e. in the background
        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
                OpenWeatherMap openWeatherMap = response.body();

                city.setText(openWeatherMap.getName() + ", " + openWeatherMap.getSys().getCountry());
                temperature.setText(openWeatherMap.getMain().getTemp() + "°C");
                weatherCondition.setText(openWeatherMap.getWeather().get(0).getDescription());
                humidity.setText(openWeatherMap.getMain().getHumidity() + "%");
                minTemperatue.setText(openWeatherMap.getMain().getTempMin() + "°C");
                maxTemperature.setText(openWeatherMap.getMain().getTempMax() + "°C");

                String iconCode = openWeatherMap.getWeather().get(0).getIcon();
                Picasso.get().load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imageViewWeather);

            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {

            }
        });
    }

}