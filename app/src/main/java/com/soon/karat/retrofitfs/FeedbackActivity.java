package com.soon.karat.retrofitfs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.soon.karat.retrofitfs.api.FeedbackService;
import com.soon.karat.retrofitfs.api.ServiceGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This Activity is responsible for sending {@link retrofit2.http.FormUrlEncoded}
 * to the server.
 * NOTE: This class does not handle the errors that can happen, to see this go
 * to {@link SearchActivity}
 */
public class FeedbackActivity extends MenuAppCompatActivity {

    private static final String TAG = "FeedbackActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feedback));
        }

        final TextInputEditText mName = findViewById(R.id.edit_text_name);
        final TextInputEditText mEmail = findViewById(R.id.edit_text_email);
        final TextInputEditText mAge = findViewById(R.id.edit_text_age);
        final TextInputEditText mTopics = findViewById(R.id.edit_text_topics);

        AppCompatButton mSend = findViewById(R.id.button_send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback(mName.getText().toString()
                        , mEmail.getText().toString()
                        , mAge.getText().toString()
                        , mTopics.getText().toString());
            }
        });
    }

    private void sendFeedback(String name, String email, String age, String topics) {

        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        if (!email.isEmpty()) { // We do not want an empty email, do not pass if there is one.
            map.put("email", email);
        }
        map.put("age", age);

        FeedbackService service = ServiceGenerator.createService(FeedbackService.class);
        service.sendFeedback(map, Arrays.asList(topics.split(","))).enqueue(new Callback<ResponseBody>() {
            @Override  // Does not handle errors. See SearchActivity for handling errors
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Toast.makeText(FeedbackActivity.this, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(FeedbackActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
