package com.android.jewelry.apipresenter;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Goutam on 6/4/2017.
 */

public interface UploadImageInterface {


    @Multipart
    @POST("updateimage/")
    Call<String> uploadFile(@Part MultipartBody.Part fileToUpload,
                            @Part("name") RequestBody mName);


}
