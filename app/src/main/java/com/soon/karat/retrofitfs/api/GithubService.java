package com.soon.karat.retrofitfs.api;

import com.soon.karat.retrofitfs.models.GitHubRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubService {

    String BASE_URL = "https://api.github.com";

    @GET("users/{user}/repos")
    Call<List<GitHubRepo>> getUserRepos(@Path("user") String user);

}
