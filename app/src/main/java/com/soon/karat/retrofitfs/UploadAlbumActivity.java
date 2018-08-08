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
import android.view.View;
import android.widget.Toast;

import com.soon.karat.retrofitfs.utils.FileUtils;

import java.util.ArrayList;

public class UploadAlbumActivity extends MenuAppCompatActivity {

    private static final String TAG = "UploadAlbumActivity";

    private static final int GALLERY_REQUEST_CODE = 9;
    private static final int PERMISSIONS_WRITE_EXTERNAL_CODE = 11;

    private Toolbar mToolbar;
    private TextInputEditText mDescription;
    private AppCompatButton mPickPhotos;
    private AppCompatButton mUpload;

    private ArrayList<Uri> fileUris;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_WRITE_EXTERNAL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FileUtils.openActionPick(this, Environment.DIRECTORY_PICTURES,
                        "image/*", GALLERY_REQUEST_CODE, true);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            fileUris = new ArrayList<>();
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

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.upload_upload_a_album));
        }

        mDescription = findViewById(R.id.edit_text_description);
        mPickPhotos = findViewById(R.id.button_select_files);
        mUpload = findViewById(R.id.button_upload);

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



                } else {
                    Toast.makeText(UploadAlbumActivity.this, "No photos were found", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



}
