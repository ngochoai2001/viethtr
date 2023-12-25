package com.example.vietnamesehtr.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    public static RetrofitInstance retrofit;
     ApiInterface apiInterface;

    public RetrofitInstance() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://5271-116-96-85-52.ngrok-free.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    public ApiInterface getApiInterface() {
        return apiInterface;
    }

    public void setApiInterface(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    public static RetrofitInstance getRetrofit() {
        if(retrofit == null){
            retrofit = new RetrofitInstance();
        }
        return retrofit;
    }

}
