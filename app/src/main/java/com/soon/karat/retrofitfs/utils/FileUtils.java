package com.soon.karat.retrofitfs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUtils {

    public static String getRealPathFromURI_API11to18(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context
                , uri
                , proj
                , null
                , null
                , null);

        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , column
                , sel
                , new String[]{id}
                , null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static void openActionPick(Activity activity, String directory, String fileType, int requestCode, boolean allowMultipleFiles) {
        Intent pickerIntent = new Intent(Intent.ACTION_PICK);

        File file = Environment.getExternalStoragePublicDirectory(directory);
        String directoryPath = file.getPath();

        Uri data = Uri.parse(directoryPath);

        pickerIntent.setDataAndType(data, fileType);

        if (allowMultipleFiles) {
            pickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }

        activity.startActivityForResult(pickerIntent, requestCode);
    }

    @NonNull
    public static RequestBody createPartFromString(String string) {
        return RequestBody.create(MultipartBody.FORM, string);
    }

    @NonNull
    public static MultipartBody.Part prepareFilePart(Context context, String partName, Uri fileUri) {

        File file = new File(FileUtils.getRealPathFromURI(context, fileUri));

        RequestBody requestBody = RequestBody.create(
                MediaType.parse(context.getContentResolver().getType(fileUri))
                , file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);

    }


}
