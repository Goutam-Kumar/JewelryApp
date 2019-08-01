package com.android.jewelry.appinstance;

import android.content.Context;

import com.android.jewelry.apipresenter.ApiConstants;
import com.android.jewelry.apipresenter.RestApi;
import com.splunk.mint.Mint;

import java.util.concurrent.TimeUnit;

import androidx.multidex.MultiDexApplication;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JewelryApp extends MultiDexApplication {
    private static JewelryApp mInstance = null;
    public static Context context;
    public RestApi restApi;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = this;
        Mint.initAndStartSession(this, "49fb2611");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(15000, TimeUnit.MILLISECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASEURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        restApi = retrofit.create(RestApi.class);
    }

    public static synchronized JewelryApp getInstance() {
        return mInstance;
    }
}
