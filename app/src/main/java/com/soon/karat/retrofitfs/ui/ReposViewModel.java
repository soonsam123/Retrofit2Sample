package com.soon.karat.retrofitfs.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.soon.karat.retrofitfs.api.GithubService;
import com.soon.karat.retrofitfs.api.ServiceGeneratorGitHub;
import com.soon.karat.retrofitfs.models.GitHubRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReposViewModel extends ViewModel {

    private static final String TAG = "ReposViewModel";

    private MutableLiveData<List<GitHubRepo>> repositories;

    public LiveData<List<GitHubRepo>> getRepos() {
        if (repositories == null) {
            repositories = new MutableLiveData<>();
            loadRepos();
        }
        return repositories;
    }

    private void loadRepos() {
        String userName = "soonsam123";
        GithubService service = ServiceGeneratorGitHub.createService(GithubService.class);
        service.getUserRepos(userName).enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(@NonNull Call<List<GitHubRepo>> call, @NonNull Response<List<GitHubRepo>> response) {
                if (response.isSuccessful()) {
                    List<GitHubRepo> repos = response.body();
                    repositories.setValue(repos);
                } else {
                    Log.i(TAG, "onResponse: Error code: " + response.code() + " - error message: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GitHubRepo>> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: Failed to connect to the server");
            }
        });
    }

}
