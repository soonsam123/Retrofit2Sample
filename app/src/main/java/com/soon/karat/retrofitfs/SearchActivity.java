package com.soon.karat.retrofitfs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soon.karat.retrofitfs.api.GithubService;
import com.soon.karat.retrofitfs.api.ServiceGenerator;
import com.soon.karat.retrofitfs.api.ServiceGeneratorGitHub;
import com.soon.karat.retrofitfs.api.UserService;
import com.soon.karat.retrofitfs.models.APIError;
import com.soon.karat.retrofitfs.models.GithubUser;
import com.soon.karat.retrofitfs.models.User;
import com.soon.karat.retrofitfs.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchActivity extends MenuAppCompatActivity {

    private static final String TAG = "SearchActivity";

    private TextView mDisplayFoundedData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set up toolbar
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.search_search));
        }

        mDisplayFoundedData = findViewById(R.id.text_user_name);

        final TextInputEditText mUserName = findViewById(R.id.edit_text_name);

        AppCompatButton mSearch = findViewById(R.id.button_search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserByName(mUserName.getText().toString());
            }
        });

    }

    private void getUserByName(String name) {

        GithubService service = ServiceGeneratorGitHub.createService(GithubService.class);

        service.getUserByName(name).enqueue(new Callback<GithubUser>() {
            @Override
            public void onResponse(@NonNull Call<GithubUser> call, @NonNull Response<GithubUser> response) {
                if (response.isSuccessful()) {
                    GithubUser user = response.body();
                    mDisplayFoundedData.setText(user.getLogin());
                } else {

                    // There are three options below, you may use only one option at
                    // once. So, if you uncomment one options, let the other two commented.

                    // ----------------------------------------------------------------
                    //                           First Option
                    // ----------------------------------------------------------------
                    // This option is good because if clarify more to the user what is
                    // going on. But you need to put all the errors that can happen.
                    /*switch (response.code()) {
                        case 404:
                            displayMessage("User not found");
                            break;
                        case 500:
                            displayMessage("Server is broken");
                            break;
                        default:
                            displayMessage("Unknown error");
                    }*/


                    // ----------------------------------------------------------------
                    //                           Second Option
                    // ----------------------------------------------------------------
                    // This option shows a Java or XML error, the user will not understand
                    // what we are trying to show him.
                    /*String errorMessage = "";
                    try {
                        errorMessage = "server returned error: " + response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    displayMessage(errorMessage);*/


                    // ----------------------------------------------------------------
                    //                           Third Option
                    // ----------------------------------------------------------------
                    // This is the best approach for handling errors
                    // todo I could not reproduce any error, so you may check if this is correct.
                    APIError error = ErrorUtils.parseError(response);
                    displayMessage(error.getMessage());

                }
            }

            @Override
            public void onFailure(@NonNull Call<GithubUser> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: Failed to connect to the server");
                // can be no internet connection
            }
        });

    }

    private void displayMessage(String message) {
        LinearLayout mContainer = findViewById(R.id.container);
        mDisplayFoundedData.setText(message);
        final Snackbar snackbar = Snackbar.make(mContainer, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
