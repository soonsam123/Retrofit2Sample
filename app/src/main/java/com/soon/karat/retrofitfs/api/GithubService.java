package com.soon.karat.retrofitfs.api;

import com.soon.karat.retrofitfs.models.AccessToken;
import com.soon.karat.retrofitfs.models.GitHubRepo;
import com.soon.karat.retrofitfs.models.GithubUser;
import com.soon.karat.retrofitfs.models.User;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface GithubService {

    String BASE_URL = "https://api.github.com/";

    @GET("users/{user}/repos")
    Call<List<GitHubRepo>> getUserRepos(@Path("user") String user);

    @GET("users/{user}")
    Call<GithubUser> getUserByName(@Path("user") String user);

    /**
     * For searching in a static way.
     *
     * @param id    id of the user
     * @param order the order that the results will be displayed
     * @param page  the page number that will be shown
     * @return a response body
     */
    @GET("users")
    Call<ResponseBody> searchForUsers(
            @Query("id") Integer id,
            @Query("sort") String order,
            @Query("page") Integer page
    );

    /**
     * For searching in a more dynamic way, because by parsing a map
     * you can vary the number of queries you will send.
     * To learn how to send a single query to every request, check
     * {@link com.soon.karat.retrofitfs.SearchActivity}.
     *
     * @param id      the id of the user.
     * @param queries a map with the value and the object of the query.
     * @return a response body.
     */
    @GET("users")
    Call<ResponseBody> searchForUsers(
            @Query("id") Integer id,
            @QueryMap Map<String, Object> queries
    );

    /**
     * This method sends the code to github to get back the user's
     * {@link AccessToken}.
     * </p>
     * We need to specify the Accept header, so GitHub will send the
     * object as a Json and then retrofit will be able to convert
     * it to our java model.
     * </p>
     * We are sending the fields as {@link FormUrlEncoded}.
     * </p>
     * The full documentation about GitHub OAuth is found in the link below:
     * https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/
     *
     * @param clientId     the app's client id, adds more security.
     * @param clientSecret the app's client secret, adds more security.
     * @param code         the code we got back from github website.
     * @return an {@link AccessToken} that will be used to recognize the user and
     * display its data.
     */
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    Call<AccessToken> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String code
    );

    @GET("user")
    Call<GithubUser> getUserInfo();

    @GET("user/repos")
    Call<List<GitHubRepo>> getUserReposWithAuth();
}
