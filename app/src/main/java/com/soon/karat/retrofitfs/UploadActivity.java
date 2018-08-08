package com.soon.karat.retrofitfs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.karat.retrofitfs.api.UserService;
import com.soon.karat.retrofitfs.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This Activity allows you to upload a single photo with
 * multiple Parts: as description, location, photographer, year.
 * The photo is stored as {@link MultipartBody.Part} and the other fields
 * are Strings that are send to Retrofit as {@link RequestBody}.
 * </p>
 * You can use this activity also as inspiration if you want
 * to upload other kind of files as videos, documents or audios.
 */
public class UploadActivity extends MenuAppCompatActivity {

    private static final String TAG = "UploadActivity";

    private static final int GALLERY_REQUEST_CODE = 7;
    private static final int WRITE_EXTERNAL_REQUEST_CODE = 10;

    private TextInputEditText mDescription;
    private TextInputEditText mLocation;
    private TextInputEditText mPhotographer;
    private TextInputEditText mYear;
    private TextView mFileName;

    private Uri photoUri;
    private String realPath;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                FileUtils.openActionPick(this, Environment.DIRECTORY_PICTURES, "image/*", GALLERY_REQUEST_CODE, false);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {

                    photoUri = uri;

                    /*if (Build.VERSION.SDK_INT < 19) {
                        realPath = FileUtils.getRealPathFromURI_API11to18(this, uri);
                    } else { // KIT KAT or greater
                        realPath = FileUtils.getRealPathFromURI_API19(this, uri);
                    }*/

                    realPath = FileUtils.getRealPathFromURI(this, uri);

                    String text = "Path: " + uri.getPath()
                            + " - RealPath: " + realPath;
                    mFileName.setText(text);

                }
            }
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.upload_upload));
        }

        mDescription = findViewById(R.id.edit_text_description);
        mLocation = findViewById(R.id.edit_text_location);
        mPhotographer = findViewById(R.id.edit_text_photographer);
        mYear = findViewById(R.id.edit_text_year);

        mFileName = findViewById(R.id.text_file_name);

        AppCompatButton mPickPhoto = findViewById(R.id.button_select_file);
        AppCompatButton mUpload = findViewById(R.id.button_upload);

        mPickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    FileUtils.openActionPick(UploadActivity.this, Environment.DIRECTORY_PICTURES, "image/*", GALLERY_REQUEST_CODE, false);
                } else {
                    ActivityCompat.requestPermissions(UploadActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_REQUEST_CODE);
                }
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoUri != null) {

                    // -----------------------------------------------------------------
                    //                          Logging OkHttp
                    // -----------------------------------------------------------------
                    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    if (BuildConfig.DEBUG) {
                        okHttpClientBuilder.addInterceptor(interceptor);
                    }


                    // -----------------------------------------------------------------
                    //                         Create the PartMap
                    // -----------------------------------------------------------------
                    // 1. Create the Parts for the Strings
                    RequestBody descriptionPart = createPartFromString(mDescription.getText().toString());
                    RequestBody locationPart = createPartFromString(mLocation.getText().toString());
                    RequestBody photographerPart = createPartFromString(mPhotographer.getText().toString());
                    RequestBody yearPart = createPartFromString(mYear.getText().toString());

                    // 2. Create multi part for the Uri
                    MultipartBody.Part file = prepareFilePart("photo", photoUri);

                    // 3. Create the map
                    Map<String, RequestBody> partMap = new HashMap<>();
                    partMap.put("client", createPartFromString("android")); // Add some default values
                    partMap.put("secret", createPartFromString("hunter2"));

                    if (!TextUtils.isEmpty(mDescription.getText().toString())) { // Just add non-empty values
                        partMap.put("description", descriptionPart);
                    }

                    if (!TextUtils.isEmpty(mLocation.getText().toString())) {
                        partMap.put("location", locationPart);
                    }

                    if (!TextUtils.isEmpty(mPhotographer.getText().toString())) {
                        partMap.put("photographer", photographerPart);
                    }

                    if (!TextUtils.isEmpty(mYear.getText().toString())) {
                        partMap.put("year", yearPart);
                    }

                    // -----------------------------------------------------------------
                    //                          Retrofit Call
                    // -----------------------------------------------------------------
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(UserService.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClientBuilder.build())
                            .build();

                    UserService service = retrofit.create(UserService.class);
                    service.uploadPhoto(partMap, file).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(UploadActivity.this, "Surprisingly worked", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i(TAG, "onResponse: Error code: " + response.code() + " - error message: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Throwable t) {
                            t.printStackTrace();
                            Log.i(TAG, "onFailure: Error message: " + t.getMessage());
                        }
                    });

                } else {
                    Toast.makeText(UploadActivity.this, "No photo was found", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @NonNull
    private RequestBody createPartFromString(String string) {
        return RequestBody.create(MultipartBody.FORM, string);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        File file = new File(FileUtils.getRealPathFromURI(this, fileUri));

        RequestBody requestBody = RequestBody.create(
                MediaType.parse(getContentResolver().getType(fileUri))
                , file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);

    }

}
