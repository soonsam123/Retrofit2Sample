package com.soon.karat.retrofitfs.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.karat.retrofitfs.GitAuthenticationActivity;
import com.soon.karat.retrofitfs.R;
import com.soon.karat.retrofitfs.api.GithubService;
import com.soon.karat.retrofitfs.models.GithubUser;
import com.soon.karat.retrofitfs.utils.GlideApp;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.soon.karat.retrofitfs.fragments.LoginFragment.KEY_ACCESS_TOKEN;

public class GitProfileFragment extends Fragment {

    private static final String TAG = "GitProfileFragment";

    private CircleImageView mProfilePicture;
    private TextView mName;
    private TextView mCompany;
    private TextView mNumbFollowers;
    private TextView mNumbFollowing;
    private TextView mBio;
    private AppCompatButton mRepos;

    private String accessToken = "";

    private GitAuthenticationActivity myContext;

    @Override
    public void onAttach(Context context) {
        myContext = (GitAuthenticationActivity) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_git_profile, container, false);

        mProfilePicture = view.findViewById(R.id.image_profile);
        mName = view.findViewById(R.id.text_name);
        mCompany = view.findViewById(R.id.text_company);
        mNumbFollowers = view.findViewById(R.id.text_number_followers);
        mNumbFollowing = view.findViewById(R.id.text_number_following);
        mBio = view.findViewById(R.id.text_bio);
        mRepos = view.findViewById(R.id.button_repos);

        Bundle bundle = getArguments();
        if (bundle != null) {
            accessToken = bundle.getString(KEY_ACCESS_TOKEN);
        }

        if (accessToken != null) {
            Log.i(TAG, "onCreateView: Access Token: " + accessToken);
            getUserInformation(accessToken);
        } else {
            Log.i(TAG, "onCreateView: Access token is null");
        }

        return view;
    }

    private void getUserInformation(String accessToken) {

        final String accessTokenComplete = "token " + accessToken;

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder newRequest = request.newBuilder().header("Authorization", accessTokenComplete);
                        return chain.proceed(newRequest.build());
                    }
                });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.BASE_URL)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GithubService service = retrofit.create(GithubService.class);
        service.getUserInfo().enqueue(new Callback<GithubUser>() {
            @Override
            public void onResponse(@NonNull Call<GithubUser> call, @NonNull Response<GithubUser> response) {
                if (response.isSuccessful()) {
                    GithubUser user = response.body();
                    if (user != null) {
                        setupWidgetsWithRetrofitValues(user);
                    }
                } else {
                    Toast.makeText(myContext, "Error code: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onResponse: Error code: " + response.code() + " - error message: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GithubUser> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: Failed :( Error message: " + t.getMessage());
                Toast.makeText(myContext, "Failed to connect to the server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupWidgetsWithRetrofitValues(GithubUser user) {

        GlideApp.with(mProfilePicture.getContext())
                .load(user.getAvatar_url())
                .centerCrop()
                .into(mProfilePicture);

        mName.setText(user.getName());
        mCompany.setText(user.getCompany());
        mNumbFollowers.setText(user.getFollowers());
        mNumbFollowing.setText(user.getFollowing());
        mBio.setText(user.getBio());

        mRepos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accessToken != null) {
                    makeTransactionToAnotherFragment(new UserReposFragment(), accessToken);
                }
            }
        });
    }

    private void makeTransactionToAnotherFragment(Fragment fragment, String accessToken) {
        Bundle args = new Bundle();
        args.putString(KEY_ACCESS_TOKEN, accessToken);
        fragment.setArguments(args);

        FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
