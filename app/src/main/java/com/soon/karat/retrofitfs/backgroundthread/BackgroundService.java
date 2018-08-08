package com.soon.karat.retrofitfs.backgroundthread;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.soon.karat.retrofitfs.api.UserService;
import com.soon.karat.retrofitfs.models.User;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackgroundService extends IntentService {

    private static final String TAG = "BackgroundService";
    
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public BackgroundService() {
        super("BackgroundService");
    }

    /**
     * This is just a demonstration, that's why it is adding a single
     * user to retrofit.
     * It is just showing how to make a synchronous call to retrofit
     * in a background thread.
     * @param intent the intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        User user = new User(
                "Soon",
                "karatesoon@gmail.com",
                21,
                new String[]{"karate", "zouk"});

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserService service = retrofit.create(UserService.class);
        try {
            Response<User> result = service.createAccount(user).execute();
            Log.i(TAG, "onHandleIntent: Success!");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "onHandleIntent: Failed :(");
        }
    }
}
