package com.soon.karat.retrofitfs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.soon.karat.retrofitfs.api.FileDownloadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * The user has two options in this Activity
 * <ol>
 *     <li>Download a small file</li>
 *     <li>Download a BIG file</li>
 * </ol>
 *
 * </p>
 * By following the comments you can see the part that are
 * redirected to download the small files and the part
 * tha are redirected to download the big files.
 * </p>
 * The difference is very small, it only differs
 * that to download the big files you need to pass to a
 * background thread otherwise the app will crash.
 */
public class DownloadActivity extends MenuAppCompatActivity {

    private static final String TAG = "DownloadActivity";

    private static final int PERMISSIONS_WRITE_EXTERNAL_CODE_SMALL = 41;
    private static final int PERMISSIONS_WRITE_EXTERNAL_CODE_BIG = 47;

    private String smallFile = "";
    private String bigFile = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.download_download));
        }
        // ----------------------------------------------------------------
        //             User clicked in the small files BUTTON
        // ----------------------------------------------------------------
        if (requestCode == PERMISSIONS_WRITE_EXTERNAL_CODE_SMALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadSmallFile(smallFile);
            }
        }

        // ----------------------------------------------------------------
        //             User clicked in the big files BUTTON
        // ----------------------------------------------------------------
        if (requestCode == PERMISSIONS_WRITE_EXTERNAL_CODE_BIG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadBigFile(bigFile);
            }
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        AppCompatButton mDownloadSmallFile = findViewById(R.id.button_download_small_file);
        AppCompatButton mDownloadBigFile = findViewById(R.id.button_download_big_file);

        // dynamical url!
        smallFile = "https://avatars3.githubusercontent.com/u/7794026";
        bigFile = "https://images.unsplash.com/photo-1449130015084-2d48a345ae62?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=bb29652e99f70314468e31f729b28f74&auto=format&fit=crop&w=500&q=60";

        // ----------------------------------------------------------------
        //                Used for downloading Small files
        // ----------------------------------------------------------------
        mDownloadSmallFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(DownloadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    downloadSmallFile(smallFile);
                } else {
                    ActivityCompat.requestPermissions(DownloadActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_WRITE_EXTERNAL_CODE_SMALL);
                }
            }
        });


        // ----------------------------------------------------------------
        //                Used for downloading BIG files
        // ----------------------------------------------------------------
        mDownloadBigFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(DownloadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    downloadBigFile(bigFile);
                } else {
                    ActivityCompat.requestPermissions(DownloadActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_WRITE_EXTERNAL_CODE_BIG);
                }
            }
        });
    }

    /**
     * Create and call retrofit 2 with a dynamic url where the file is located
     * in the internet.
     * @param fileUrl the url of the file on the internet, can be changed dynamically
     */
    private void downloadSmallFile(String fileUrl) {
        // ----------------------------------------------------------------
        //                   Create and call Retrofit
        // ----------------------------------------------------------------
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FileDownloadService.BASE_URL) // As we are parsing the dynamically url,
                .build();                              // the base url doesn't matter here.

        FileDownloadService service = retrofit.create(FileDownloadService.class);

        service.downloadFile(fileUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        boolean success = writeResponseBodyToDisk(response.body(), "Future Studio.png");
                        Toast.makeText(DownloadActivity.this, "download was successful: " + success, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "onResponse: Response body is null");
                        Toast.makeText(DownloadActivity.this, "Failed because there was no response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i(TAG, "onResponse: Error code: " + response.code() + " - Error message: " + response.message());
                    Toast.makeText(DownloadActivity.this, "Error " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: Error message: " + t.getMessage());
                Toast.makeText(DownloadActivity.this, R.string.download_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Create and call retrofit 2 with a dynamic url where the file is located
     * in the internet.
     * @param fileUrl the url of the file on the internet, can be changed dynamically
     */
    private void downloadBigFile(String fileUrl) {
        // ----------------------------------------------------------------
        //                   Create and call Retrofit
        // ----------------------------------------------------------------
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FileDownloadService.BASE_URL) // As we are parsing the dynamically url,
                .build();                              // the base url doesn't matter here.

        FileDownloadService service = retrofit.create(FileDownloadService.class);

        service.downloadFileStream(fileUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {

                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                writeResponseBodyToDisk(response.body(), "Future Studio.png");
                                return null;
                            }
                        }.execute();

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: Error message: " + t.getMessage());
                Toast.makeText(DownloadActivity.this, R.string.download_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            // todo change the file location/name according to your needs
            File file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.i(TAG, "writeResponseBodyToDisk: Progress: " + fileSizeDownloaded + "/" + fileSize);
                    Toast.makeText(this, "Progress: " + fileSizeDownloaded + "/" + fileSize, Toast.LENGTH_SHORT).show();
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


}
