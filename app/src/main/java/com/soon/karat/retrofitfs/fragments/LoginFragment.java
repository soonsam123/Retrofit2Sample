package com.soon.karat.retrofitfs.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.soon.karat.retrofitfs.GitAuthenticationActivity;
import com.soon.karat.retrofitfs.R;
import com.soon.karat.retrofitfs.api.GithubService;
import com.soon.karat.retrofitfs.models.AccessToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    public static final String KEY_ACCESS_TOKEN = "access_token";

    private String clientId = "c0d840c3f8cd70dacb52";
    private String clientSecret = "49ee5c5bc1e2ee216355460306073478c627c7dc";
    private String redirectUri = "test://callback";

    private GitAuthenticationActivity myContext;

    @Override
    public void onAttach(Context context) {
        myContext = (GitAuthenticationActivity) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        AppCompatButton mSignIn = view.findViewById(R.id.button_sign_in_github);
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUsingGitHub();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Uri uri = myContext.getIntent().getData();

        if (uri != null && uri.toString().startsWith(redirectUri)) {
            Log.i(TAG, "onResume: Uri starts with: " + redirectUri);

            String code = uri.getQueryParameter("code");

            // Send this code to get the authentication token
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://github.com/") // Note that the base url is different. See documentation
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            GithubService service = retrofit.create(GithubService.class);

            service.getAccessToken(
                    clientId,
                    clientSecret,
                    code).enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                    if (response.isSuccessful()) {
                        AccessToken accessToken = response.body();
                        if (accessToken != null) {
                            Log.i(TAG, "onResponse: Token: " + accessToken.getAccessToken() + " - type: " + accessToken.getTokenType());
                            makeTransactionToAnotherFragment(new GitProfileFragment(), accessToken.getAccessToken());
                        }
                    } else {
                        Log.i(TAG, "onResponse: Error code: " + response.code() + " - error message: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    Log.i(TAG, "onFailure: Failed :(, error message: " + t.getMessage());
                    Toast.makeText(myContext, "Failed to connect to the server", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    /**
     * Opens the browser in the Github website so the user can login.
     */
    private void signInUsingGitHub() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/login/oauth/authorize"
                        + "?client_id=" + clientId
                        + "&scope=repo&redirect_uri=" + redirectUri));
        startActivity(intent);
    }

    private void makeTransactionToAnotherFragment(Fragment fragment, String accessToken) {
        Bundle args = new Bundle();
        args.putString(KEY_ACCESS_TOKEN, accessToken);
        fragment.setArguments(args);

        FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


}
