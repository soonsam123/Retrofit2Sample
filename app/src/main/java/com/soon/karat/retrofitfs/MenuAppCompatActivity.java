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
        }
        return super.onOptionsItemSelected(item);
    }


}
