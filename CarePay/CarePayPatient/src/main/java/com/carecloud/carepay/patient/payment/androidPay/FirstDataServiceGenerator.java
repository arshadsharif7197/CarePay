package com.carecloud.carepay.patient.payment.androidPay;


import com.carecloud.carepay.service.library.HeaderInterceptor;
import com.carecloud.carepay.service.library.constants.HttpConstants;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kkannan on 4/25/16.
 */
public class FirstDataServiceGenerator {

    public static final String API_BASE_URL = "https://api-cert.payeezy.com";


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass, Map<String, String> headers) {
        httpClient.readTimeout(HttpConstants.READ_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        httpClient.connectTimeout(HttpConstants.CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        httpClient.writeTimeout(HttpConstants.WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilderWithToken = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("Cache-Control", "no-cache, no-store")
                        .method(original.method(), original.body());
                Request request = requestBuilderWithToken.build();
                return chain.proceed(request);

            }
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);
        if (headers != null) {
            httpClient.addInterceptor(new HeaderInterceptor(headers));
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();

        return retrofit.create(serviceClass);
    }

    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.trim().equals(""));
    }


}

