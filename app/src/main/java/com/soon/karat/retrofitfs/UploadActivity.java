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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.karat.retrofitfs.api.LocalHostService;
import com.soon.karat.retrofitfs.utils.FileUtils;

import java.io.File;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends MenuAppCompatActivity {

    private static final String TAG = "UploadActivity";

    private static final int GALLERY_REQUEST_CODE = 7;
    private static final int WRITE_EXTERNAL_REQUEST_CODE = 10;

    private Toolbar mToolbar;

    private TextInputEditText mDescription;
    private AppCompatButton mPickPhoto;
    private TextView mFileName;
    private AppCompatButton mUpload;

    private Uri photoUri;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                openActionPick(Environment.DIRECTORY_PICTURES, "image/*", GALLERY_REQUEST_CODE);
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
                    mFileName.setText(uri.getPath());
                    photoUri = uri;
                }
            }
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.upload_upload));
        }

        mDescription = findViewById(R.id.edit_text_description);
        mPickPhoto = findViewById(R.id.button_select_file);
        mFileName = findViewById(R.id.text_file_name);
        mUpload = findViewById(R.id.button_upload);

        mPickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    openActionPick(Environment.DIRECTORY_PICTURES, "image/*", GALLERY_REQUEST_CODE);
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

                    String description = mDescription.getText().toString();

                    RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM, description);

                    File originalFile = FileUtils.getFile(UploadActivity.this, photoUri);

                    RequestBody filePart = RequestBody.create(
                            MediaType.parse(getContentResolver().getType(photoUri)),
                            originalFile);

                    MultipartBody.Part file = MultipartBody.Part.createFormData("photo", originalFile.getName(), filePart);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(LocalHostService.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    LocalHostService service = retrofit.create(LocalHostService.class);
                    service.uploadPhoto(descriptionPart, file).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                            Toast.makeText(UploadActivity.this, "Surprisingly worked", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
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

    private void openActionPick(String directory, String fileType, int requestCode) {
        Intent pickerIntent = new Intent(Intent.ACTION_PICK);

        File file = Environment.getExternalStoragePublicDirectory(directory);
        String directoryPath = file.getPath();

        Uri data = Uri.parse(directoryPath);

        pickerIntent.setDataAndType(data, fileType);

        startActivityForResult(pickerIntent, requestCode);
    }

}
