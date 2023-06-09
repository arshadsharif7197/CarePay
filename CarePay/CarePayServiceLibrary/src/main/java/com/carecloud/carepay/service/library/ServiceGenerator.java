package com.carecloud.carepay.service.library;

import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepay.service.library.interceptors.HeaderInterceptor;
import com.carecloud.carepay.service.library.interceptors.JSONFormattedLoggingInterceptor;

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
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * This is an generic class use for API call using retrofit.
 * Any HTTP request will be created from this class.
 * Default header for HTTP request are added by default.
 * Default Base URL created from HttpConstants.
 */

public class ServiceGenerator {

    private static String API_BASE_URL = HttpConstants.getApiBaseUrl();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    //private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static ServiceGenerator instance;

    private ServiceGenerator() {
    }

    public static ServiceGenerator getInstance() {
        if (instance == null) {
            instance = new ServiceGenerator();
        }
        return instance;
    }

    /**
     * Create the retrofil service for the specific service class
     *
     * @param serviceClass Specific service class for converting in to retrofit service model
     */
    public <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public <S> S createService(Class<S> serviceClass, Map<String, String> headers) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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
                        .removeHeader("x-api-key")
                        .removeHeader("username")
                        .method(original.method(), original.body());
                DeviceIdentifierDTO deviceIdentifierDTO = HttpConstants.getDeviceInformation();
                if (deviceIdentifierDTO != null) {
                    requestBuilderWithToken.header("deviceinformation", deviceIdentifierDTO.toString());
                }

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
//        httpClient.addInterceptor(new JSONFormattedLoggingInterceptor());
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();

        return retrofit.create(serviceClass);
    }
}