package com.lagel.com.mqttchat.DownloadFile;

import com.lagel.com.mqttchat.Utilities.ApiOnServer;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/*
* Service containing the list of the multipart port and link used to
* upload image/audio or video shared on a chat
*
* */
public class ServiceGenerator
{
    private static final String ChatMulterUpload = ApiOnServer.CHAT_MULTER_UPLOAD_URL;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(300, TimeUnit.SECONDS).writeTimeout(300, TimeUnit.SECONDS)
            .connectTimeout(300, TimeUnit.SECONDS);
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(ChatMulterUpload)
                    .addConverterFactory(GsonConverterFactory.create());
    public static <S> S createService(Class<S> serviceClass)
    {
        Retrofit retrofit = builder.client(httpClient.build())
                .build();
        return retrofit.create(serviceClass);
    }

}