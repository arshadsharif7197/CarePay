package com.carecloud.carepay.service.library;

import android.content.Context;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
 * Created by Jahirul Bhuiyan on 9/15/2016.
 * This is an generic class use for API call using retrofit.
 * Any HTTP request will be created from this class.
 * Default header for HTTP request are added by default.
 * Default Base URL created from String.xml.
 * If needed to change there is a constructor which accept base url as parameter.
 * This class is deprecated from now Please
 * @deprecated use WorkflowServiceHelper class functionality instead
 */

@Deprecated
public class BaseServiceGenerator {
    private static String API_BASE_URL;
    private static Retrofit.Builder builder;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public BaseServiceGenerator() {
        API_BASE_URL=HttpConstants.getApiBaseUrl();
        generateBuilder();
    }

    private void generateBuilder() {
        builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
    }
    /**
     * Create the retrofil service for the specific service class
     * @param serviceClass Specific service class for converting in to retrofit service model
     * */
    public  <S> S createService(final CognitoAppHelper cognitoAppHelper, Class<S> serviceClass) {
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
                if( !isNullOrEmpty(cognitoAppHelper.getCurrUser())) {
                    requestBuilderWithToken.header("username", cognitoAppHelper.getCurrUser());
                }
                if(cognitoAppHelper.getCurrSession()!=null &&  !isNullOrEmpty(cognitoAppHelper.getCurrSession().getIdToken().getJWTToken())){
                    requestBuilderWithToken.header("Authorization", cognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
                }
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

    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.trim().equals(""));
    }
}
