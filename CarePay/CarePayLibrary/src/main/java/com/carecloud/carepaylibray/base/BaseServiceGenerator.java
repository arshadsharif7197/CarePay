package com.carecloud.carepaylibray.base;

import android.content.Context;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.constants.ResponseConstants;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jahirul Bhuiyan on 9/15/2016.
 * This is an generic class use for API call using retrofit.
 * Any HTTP request will be created from this class.
 * Default header for HTTP request are added by default.
 * TODO: Additional header will be added by from the parameters
 * Default Base URL created from String.xml.
 * If needed to change there is a constructor which accept base url as parameter.
 */
public class BaseServiceGenerator {
    private static String API_BASE_URL;
    private static Retrofit.Builder builder;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public BaseServiceGenerator(Context context) {
        API_BASE_URL=context.getResources().getString(R.string.api_base_url);
        generateBuilder();
    }

    public BaseServiceGenerator(Context context, String baseUrl) {
        API_BASE_URL=baseUrl;
        generateBuilder();
    }

    private void generateBuilder() {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return true;//f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        builder = new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
    }

    public  <S> S createService(Class<S> serviceClass) {
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
                        .header("username", CognitoAppHelper.getCurrUser())
                        .header("Authorization", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken())
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
