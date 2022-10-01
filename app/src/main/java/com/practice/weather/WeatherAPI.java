package com.practice.weather;

import com.practice.weather.ModelClasses.OpenWeatherMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    /**
     * This method will be used to get the weather info based on the lat and lon.
     * The lat and lon are info that can be generated when the user switches on his location
     * the parameters are appended with @Query so it passes those values when invoking the actual API call
     * @param lat  - the latitude based on location
     * @param lon - the longitude based on location
     * @return -  An instance of the OpenWeatherMap that holds all the info
     */
    @GET("weather?units=metric&appid=593748ac3303d8d78e8530daff7cae1a")
    Call<OpenWeatherMap> getWeatherWithLocation(@Query("lat") double lat, @Query("lon") double lon);

    /**
     * This method will be used to get the weather info based on the city name.
     * the parameter is appended with @Query so it passes the value when invoking the actual API call
     * @param name - the city name
     * @return -  An instance of the OpenWeatherMap that holds all the info for that city
     */
    @GET("weather?units=metric&appid=593748ac3303d8d78e8530daff7cae1a")
    Call<OpenWeatherMap> getWeatherWithCityName(@Query("q") String name);

}
