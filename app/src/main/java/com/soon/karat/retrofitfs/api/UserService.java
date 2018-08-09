package com.soon.karat.retrofitfs.api;

import com.soon.karat.retrofitfs.models.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface UserService {

    String BASE_URL = "http://10.0.2.2:3000/"; // Use this ip when working in emulator
    /*String BASE_URL = "http://10.200.3.172:3000";*/ // Use your Computer's local ip address when working in a real device

    // H1. Uncomment this to add STATIC headers
    /*@Headers({
            "Cache-Control: max-age=3600",
            "User-Agent: Android"
    })*/
    @POST("users")
    Call<User> createAccount(@Body User user);

    // H2. For DYNAMIC headers add this inside the method createAccount(HERE!)
    /*@Header("Cache-Control") String cache*/

    // H3. For request headers in OkHttp Interceptor see RegisterActivity.

    /**
     * Send a single Part with an image.
     * If you want to send multiple Parts or multiple Photos
     * you can just add more fields here in this method or you
     * can use the more efficient methods bellow.
     *
     * @param description photo's description
     * @param photo       photo multi part
     * @return a response body
     */
    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadPhoto(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part photo
    );

    /**
     * Send multiple Parts with an image.
     *
     * @param data  multiple parts (description, photographer, location...)
     * @param photo photo multi part
     * @return a response body
     */
    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadPhoto(
            @PartMap Map<String, RequestBody> data,
            @Part MultipartBody.Part photo
    );

    /**
     * Send multiples files with a description.
     *
     * @param description album's description
     * @param files       the multiple files
     * @return a response body
     */
    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadAlbum(
            @Part("description") RequestBody description,
            @Part List<MultipartBody.Part> files
    );

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") String id);

    @GET("users")
    Call<List<User>> getUserByName(@Query("name") String name);

}
