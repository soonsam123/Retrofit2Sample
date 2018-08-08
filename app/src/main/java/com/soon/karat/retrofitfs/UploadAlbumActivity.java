package com.soon.karat.retrofitfs;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.soon.karat.retrofitfs.api.UserService;
import com.soon.karat.retrofitfs.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This activity allows you to upload multiple photos at once with a
 * single description. The photos are store as {@link MultipartBody.Part}
 * and the description {@link String} as {@link RequestBody}.
 */
public class UploadAlbumActivity extends MenuAppCompatActivity {

    private static final String TAG = "UploadAlbumActivity";

    private static final int GALLERY_REQUEST_CODE = 9;
    private static final int PERMISSIONS_WRITE_EXTERNAL_CODE = 11;

    private TextInputEditText mDescription;

    private ArrayList<Uri> fileUris = new ArrayList<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_WRITE_EXTERNAL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FileUtils.openActionPick(this, Environment.DIRECTORY_PICTURES,
                        "image/*", GALLERY_REQUEST_CODE, true);
            }
        }
        // NOTE: I am not handling the case the user denys the permission, you should do this in a real app.

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
                fileUris.add(uri);
            }

        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_album);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.upload_upload_a_album));
        }

        mDescription = findViewById(R.id.edit_text_description);
        AppCompatButton mPickPhotos = findViewById(R.id.button_select_files);
        AppCompatButton mUpload = findViewById(R.id.button_upload);

        mPickPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(UploadAlbumActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    FileUtils.openActionPick(UploadAlbumActivity.this, Environment.DIRECTORY_PICTURES,
                            "image/*", GALLERY_REQUEST_CODE, true);
                } else {
                    ActivityCompat.requestPermissions(UploadAlbumActivity.this,
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_WRITE_EXTERNAL_CODE);
                }
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fileUris != null) {

                    // ------------------------------------------------------------------
                    //                              Logging
                    // ------------------------------------------------------------------
                    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    if (BuildConfig.DEBUG) {
                        okHttpClientBuilder.addInterceptor(interceptor);
                    }

                    // ------------------------------------------------------------------
                    //                           Prepare Files
                    // ------------------------------------------------------------------
                    RequestBody descriptionPart = FileUtils.createPartFromString(mDescription.getText().toString());

                    List<MultipartBody.Part> filesParts = new ArrayList<>();
                    for (int i = 0; i < fileUris.size(); i++) {
                        String partName = "Photo" + i;
                        filesParts.add(FileUtils.prepareFilePart(UploadAlbumActivity.this,
                                partName,
                                fileUris.get(i)));
                    }

                    // ------------------------------------------------------------------
                    //                       Create and call Retrofit
                    // ------------------------------------------------------------------
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(UserService.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClientBuilder.build())
                            .build();

                    UserService service = retrofit.create(UserService.class);
                    service.uploadAlbum(descriptionPart, filesParts).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(UploadAlbumActivity.this, "Upload successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i(TAG, "onResponse: Error code: " + response.code() + " - message: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            t.printStackTrace();
                            Log.i(TAG, "onFailure: Error message: " + t.getMessage());
                        }
                    });

                } else {
                    Toast.makeText(UploadAlbumActivity.this, "No photos were found", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



}
