package com.soon.karat.retrofitfs.utils;

import android.util.Log;

import com.soon.karat.retrofitfs.api.ServiceGenerator;
import com.soon.karat.retrofitfs.models.APIError;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter = ServiceGenerator.retrofit()
                .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error;

        try {
            error = converter.convert(response.errorBody());
            Log.i("Debug", "parseError: Converting " + error.getMessage());
        } catch (IOException e) {
            Log.i("Debug", "parseError: Exception");
            return new APIError();
        }

        return error;

    }

}
