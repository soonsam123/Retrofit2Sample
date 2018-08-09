package com.soon.karat.retrofitfs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MenuAppCompatActivity extends AppCompatActivity {

    // ------------------------------------------------------------------------------
    //                                     Menu
    // ------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_repos:
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                startActivity(mainActivityIntent);
                break;
            case R.id.menu_register:
                Intent registerActivityIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerActivityIntent);
                break;
            case R.id.menu_upload:
                Intent uploadActivityIntent = new Intent(this, UploadActivity.class);
                startActivity(uploadActivityIntent);
                break;
            case R.id.menu_upload_album:
                Intent uploadAlbumActivityIntent = new Intent(this, UploadAlbumActivity.class);
                startActivity(uploadAlbumActivityIntent);
                break;
            case R.id.menu_download:
                Intent profileActivityIntent = new Intent(this, DownloadActivity.class);
                startActivity(profileActivityIntent);
                break;
            case R.id.menu_search:
                Intent searchActivityIntent = new Intent(this, SearchActivity.class);
                startActivity(searchActivityIntent);
                break;
            case R.id.menu_feedback:
                Intent feedbackActivityIntent = new Intent(this, FeedbackActivity.class);
                startActivity(feedbackActivityIntent);
                break;
            case R.id.menu_message:
                Intent messageActivityIntent = new Intent(this, MessageActivity.class);
                startActivity(messageActivityIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
