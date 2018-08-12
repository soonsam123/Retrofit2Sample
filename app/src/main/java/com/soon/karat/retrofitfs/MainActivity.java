package com.soon.karat.retrofitfs;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.soon.karat.retrofitfs.api.GithubService;
import com.soon.karat.retrofitfs.models.GitHubRepo;
import com.soon.karat.retrofitfs.ui.ReposViewModel;
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

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        mProgressBar = findViewById(R.id.progress_bar);

        mRecyclerView.setVisibility(View.INVISIBLE);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_repositories));
        }


        ReposViewModel model = ViewModelProviders.of(this).get(ReposViewModel.class);
        model.getRepos().observe(this, new Observer<List<GitHubRepo>>() {
            @Override
            public void onChanged(@Nullable List<GitHubRepo> gitHubRepos) {
                setupRecyclerView(gitHubRepos);
            }
        });

    }

    private void setupRecyclerView(List<GitHubRepo> repositories) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(new UserReposAdapter(repositories));
    }
}
