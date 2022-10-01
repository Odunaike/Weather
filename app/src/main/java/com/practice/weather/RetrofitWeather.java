package com.practice.weather;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
    This is like an utility class that creates and initializes the retrofit object
*/
public class RetrofitWeather {

    private static Retrofit retrofit;

    public static Retrofit getClient(){
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
