package com.cemetery.map.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.cemetery.map.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static String currentBaseUrl = null;

    /** Get (or rebuild) the Retrofit instance. Pass context to read saved base URL. */
    public static ApiService getService(Context context) {
        String baseUrl = getSavedBaseUrl(context);
        // Rebuild if URL changed
        if (retrofit == null || !baseUrl.equals(currentBaseUrl)) {
            currentBaseUrl = baseUrl;
            retrofit = buildRetrofit(baseUrl);
        }
        return retrofit.create(ApiService.class);
    }

    private static Retrofit buildRetrofit(String baseUrl) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static String getSavedBaseUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREF_BASE_URL, Constants.BASE_URL);
    }

    /** Call this when the user changes the base URL in settings */
    public static void reset() {
        retrofit = null;
        currentBaseUrl = null;
    }
}
