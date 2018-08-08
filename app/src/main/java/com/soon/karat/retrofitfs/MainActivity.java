package com.soon.karat.retrofitfs;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.soon.karat.retrofitfs.api.GithubService;
import com.soon.karat.retrofitfs.models.GitHubRepo;
import com.soon.karat.retrofitfs.ui.UserReposAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This Activity lists the repositories for a specific user
 * in github.
 */
public class MainActivity extends MenuAppCompatActivity {

    private static final String TAG = "MainActivity";

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_repositories));
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GithubService service = retrofit.create(GithubService.class);

        service.getUserRepos("soonsam123").enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                if (response.isSuccessful()) {
                    List<GitHubRepo> repositories = response.body();
                    setupRecyclerView(repositories);
                } else {
                    Log.i(TAG, "onResponse: ERROR - body: " + response.body() + " - Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: Error Message" + t.getMessage());
            }
        });
    }

    private void setupRecyclerView(List<GitHubRepo> repositories) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(new UserReposAdapter(repositories));
    }
}
