package com.google.ar.sceneform.samples.restapi;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi {

    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;

    public static Retrofit getRetrofit(){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://westcentralus.api.cognitive.microsoft.com/vision/v2.0/")
                .client(okhttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    private static OkHttpClient okhttpClient(){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request baseRequest = chain.request();
                    Request.Builder builder = baseRequest.newBuilder()
                            .addHeader("Ocp-Apim-Subscription-Key",
                                    "01c8333210854059a6c2df093bf1b284");
                    return chain.proceed(builder.build());
                });

        okHttpClientBuilder.addInterceptor(loggingInterceptor);
        okHttpClient = okHttpClientBuilder.build();
        return okHttpClient;
    }
}
