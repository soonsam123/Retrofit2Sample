package com.soon.karat.retrofitfs.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.soon.karat.retrofitfs.GitAuthenticationActivity;
import com.soon.karat.retrofitfs.R;
import com.soon.karat.retrofitfs.api.GithubService;
import com.soon.karat.retrofitfs.models.GitHubRepo;
import com.soon.karat.retrofitfs.ui.UserReposAdapter;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserReposFragment extends Fragment {

    private static final String TAG = "UserReposFragment";

    private RecyclerView mRecyclerView;

    private GitAuthenticationActivity myContext;

    @Override
    public void onAttach(Context context) {
        myContext = (GitAuthenticationActivity) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_repos, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);

        String accessToken = "";

        Bundle bundle = getArguments();
        if (bundle != null) {
            accessToken = bundle.getString(LoginFragment.KEY_ACCESS_TOKEN);
        }

        if (accessToken != null) {
            getUserRepositories(accessToken);
        } else {
            Log.i(TAG, "onCreateView: Access token was not found");
        }

        return view;
    }

    private void getUserRepositories(String accessToken) {

        final String accessTokenComplete = "token " + accessToken;

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder newBuilder = request.newBuilder().header("Authorization", accessTokenComplete);
                return chain.proceed(newBuilder.build());
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.BASE_URL)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GithubService service = retrofit.create(GithubService.class);
        service.getUserReposWithAuth().enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(@NonNull Call<List<GitHubRepo>> call, @NonNull retrofit2.Response<List<GitHubRepo>> response) {
                if (response.isSuccessful()) {
                    List<GitHubRepo> repositories = response.body();
                    if (repositories != null) {
                        setupRecyclerView(repositories);
                    } else {
                        Toast.makeText(myContext, "This user has no repositories", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(myContext, "Error code: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onResponse: Error code: " + response.code() + " - error message: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GitHubRepo>> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: Failed :( Error message: " + t.getMessage());
                Toast.makeText(myContext, "Failed to connect to the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView(List<GitHubRepo> repositories) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(myContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(new UserReposAdapter(repositories));
    }
}
