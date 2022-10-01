package com.practice.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.weather.ModelClasses.OpenWeatherMap;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchedCityActivity extends AppCompatActivity {

    private TextView city, temperature, weatherCondition, humidity, maxTemperature, minTemperatue;
    private ImageView imageViewWeather;
    ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_city);

        city = findViewById(R.id.textViewCitySearched);
        temperature = findViewById(R.id.textViewTempSearched);
        weatherCondition = findViewById(R.id.textViewWeatherConditionSearched);
        humidity = findViewById(R.id.textViewHumiditySearched);
        maxTemperature = findViewById(R.id.textViewMaxTempSearched);
        minTemperatue = findViewById(R.id.textViewMinTempSearched);
        imageViewWeather = findViewById(R.id.imageViewWeatherSearched);
        backButton = findViewById(R.id.buttonBack);

        Intent i = getIntent();
        getWeatherData(i.getStringExtra("cityname"));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    /**
     * this makes the request to the cloud and gets the data to be displayed based on city
     * @param name - the name of the city
     */
    public void getWeatherData(String name){
        WeatherAPI weatherAPI = RetrofitWeather.getClient().create(WeatherAPI.class);
        Call<OpenWeatherMap> call = weatherAPI.getWeatherWithCityName(name);

        // this method will let you retrieve the HTTP request data asynchronously i.e. in the background
        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
               if (response.isSuccessful()){
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
               }else {
                   Toast.makeText(SearchedCityActivity.this, "city name not found", Toast.LENGTH_LONG).show();
               }

            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {

            }
        });
    }

}