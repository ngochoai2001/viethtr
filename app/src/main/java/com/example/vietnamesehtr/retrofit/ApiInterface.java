package com.example.vietnamesehtr.retrofit;

import android.media.Image;

import com.example.vietnamesehtr.ImageRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("media/upload/")
    public Call<ImageRes> sendImg(@Part MultipartBody.Part file);


}
