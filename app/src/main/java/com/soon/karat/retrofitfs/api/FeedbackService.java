package com.soon.karat.retrofitfs.api;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedbackService {

    /**
     * This is used in case you want to send the feedback forms statically,
     * you previously know you will be sending 4 fields.
     * In case you want to send dynamically because you don't know how many
     * fields there will be or because there are many fields, see method
     * below.
     * @param name the name of the user
     * @param email the email of the user
     * @param age the age of the user
     * @param topics the topics the user are interested in
     * @return a response body
     */
    @FormUrlEncoded
    @POST("feedback")
    Call<ResponseBody> sendFeedback(
            @Field("name") String name,
            @Field("email") String email,
            @Field("age") String age,
            @Field("topics") String topics
    );

    /**
     * In case you want to send the fields dynamically because you
     * do not know how many there will be or there will be too many,
     * send the fields as a {@link FieldMap} made by a {@link Map}.
     * </p>
     * This map will send the fields as {@link Object}, parsing an
     * {@link java.util.ArrayList} as an {@link Object} in this way
     * will not work, that is why we put the topics (which are an
     * {@link java.util.ArrayList}) separated from the others.
     * @param map a map with the value and the object.
     * @param topics the topics the user are interested in as an List
     * @return a response body
     */
    @FormUrlEncoded
    @POST("feedback")
    Call<ResponseBody> sendFeedback(
            @FieldMap Map<String, Object> map,
            @Field("topics") List<String> topics
            );
}
