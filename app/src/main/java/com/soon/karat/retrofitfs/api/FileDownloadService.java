package com.soon.karat.retrofitfs.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface FileDownloadService {

    String BASE_URL = "http://10.0.2.2:3000/"; // Use this ip when working in emulator
    /*String BASE_URL = "http://10.200.3.172:3000";*/ // Use your Computer's local ip address when working in a real device

    /**
     * This call download a file that is located in the {@link Url} you are
     * parsing dynamically.
     * </p>
     * NOTE: This is useful only for downloading small files because when
     * downloading retrofit keep the file data in memory, and if the file
     * is big it will crash the app. In case of bigger files you need
     * to use method below.
     * @param url where the file that will be downloaded is located
     * @return a response body
     */
    @GET
    Call<ResponseBody> downloadFile(@Url String url);

    @Streaming
    @GET
    Call<ResponseBody> downloadFileStream(@Url String url);

}
