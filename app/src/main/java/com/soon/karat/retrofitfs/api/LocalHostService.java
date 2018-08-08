package com.soon.karat.retrofitfs.api;

import com.soon.karat.retrofitfs.models.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface LocalHostService {

    String BASE_URL = "http://10.0.2.2:3000"; // Use this ip when working in emulator
    /*String BASE_URL = "http://10.200.3.172:3000";*/ // Use your Computer's local ip address when working in a real device

    @POST("users")
    Call<User> createAccount(@Body User user);

    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadPhoto(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part photo
            );
}
