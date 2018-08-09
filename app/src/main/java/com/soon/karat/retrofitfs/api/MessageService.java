package com.soon.karat.retrofitfs.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MessageService {

    String BASE_URL = "http://10.0.2.2:3000/";

    /**
     * Seding the message as {@link String}.
     * @param message the message that is being send
     * @return a string with the information
     */
    @POST("message")
    Call<String> sendMessage(@Body String message);

    /**
     * Sending the message as a {@link RequestBody}.
     * @param message the message that is being send
     * @return a string with the information
     */
    @POST("message")
    Call<String> sendMessage(@Body RequestBody message);
}
