package com.carecloud.carepaylibray.services;

import android.content.Context;

import com.carecloud.carepaylibrary.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jahirul Bhuiyan on 8/23/2016.
 */
public class ServiceGenerator {

    private static String API_BASE_URL;

    public ServiceGenerator(Context context) {
        API_BASE_URL=context.getResources().getString(R.string.api_base_url);
    }

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    //    , String token, String searchString
    public static <S> S createService(Class<S> serviceClass) {

        httpClient.readTimeout(ResponseConstants.READ_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        httpClient.connectTimeout(ResponseConstants.CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        httpClient.writeTimeout(ResponseConstants.WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilderWithToken = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());
                Request request = requestBuilderWithToken.build();
                return chain.proceed(request);

            }
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();

        return retrofit.create(serviceClass);
    }
}
