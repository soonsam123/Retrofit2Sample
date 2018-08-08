package com.soon.karat.retrofitfs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.soon.karat.retrofitfs.api.UserService;
import com.soon.karat.retrofitfs.backgroundthread.BackgroundService;
import com.soon.karat.retrofitfs.models.User;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends MenuAppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private TextInputEditText mName;
    private TextInputEditText mEmail;
    private TextInputEditText mAge;
    private TextInputEditText mTopics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.registration_register));
        }

        mName = findViewById(R.id.edit_text_name);
        mEmail = findViewById(R.id.edit_text_email);
        mAge = findViewById(R.id.edit_text_age);
        mTopics = findViewById(R.id.edit_text_topics);

        AppCompatButton mRegisterAccount = findViewById(R.id.button_register);

        mRegisterAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString();
                String email = mEmail.getText().toString();
                int age = Integer.parseInt(mAge.getText().toString());
                String[] topics = mTopics.getText().toString().split(",");

                User user = new User(name, email, age, topics);

                sendNetworkRequest(user);
            }
        });

    }

    private void sendNetworkRequest(User user) {

        // This process will print the data the server is receiving in the logs so
        // it is easy to debug. However, it could print sensitive data to the logs.
        // That's why we activate this only while in development mode.

        // ----------------------------------------------------------------
        //                            Logging
        // ----------------------------------------------------------------
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(logging);
        }

        // ----------------------------------------------------------------
        //                    Create and call Retrofit
        // ----------------------------------------------------------------

        // Asynchronous on the UI thread
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build()) // Add the client here.
                .build();

        UserService service = retrofit.create(UserService.class);
        service.createAccount(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User newUser = response.body();
                    if (newUser != null) {
                        Toast.makeText(RegisterActivity.this, "Yeah! id - " + newUser.getId(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i(TAG, "onResponse: Error code: " + response.code() + " - message: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: Error message: " + t.getMessage());
            }
        });

        // Synchronous in a background thread
        // Uncomment lines below to use
        /*Intent intent = new Intent(RegisterActivity.this, BackgroundService.class);
        startService(intent);*/

    }
}
