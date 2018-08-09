package com.soon.karat.retrofitfs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.soon.karat.retrofitfs.api.MessageService;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * This Activity shows how to send primitive data to retrofit
 * as text/plain. For that you need to add a different conversor
 * factory {@link ScalarsConverterFactory}
 * </p>
 * You also need to add a new line to your grade dependencies.
 */
public class MessageActivity extends MenuAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.message));
        }

        final TextInputEditText mMessage = findViewById(R.id.edit_text_message);
        AppCompatButton mSend = findViewById(R.id.button_send);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mMessage.getText().toString());
            }
        });
    }

    private void sendMessage(String message) {

        // In case you want to parse the message as a RequestBody, use the line below.
        /*RequestBody body = RequestBody.create(MediaType.parse("text/plain"), message);*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MessageService.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        MessageService service = retrofit.create(MessageService.class);
        service.sendMessage(message).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(MessageActivity.this, response.body(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
