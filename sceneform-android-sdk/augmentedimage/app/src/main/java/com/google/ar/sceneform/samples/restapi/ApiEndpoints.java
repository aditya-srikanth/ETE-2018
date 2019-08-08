package com.google.ar.sceneform.samples.restapi;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiEndpoints {

    @Multipart
    @POST
    Call<ResponseBody> sendImage(@Part MultipartBody.Part file,
                                 @Part("visualFeatures") String features);
}


