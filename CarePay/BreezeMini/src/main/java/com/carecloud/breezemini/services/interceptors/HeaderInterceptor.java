package com.carecloud.breezemini.services.interceptors;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Kavin Kannan on  05/23/2017
 * This class implement the adding dynamic header functionality for HTTP calls
 *
 */

public class HeaderInterceptor implements Interceptor {

    private Map<String, String> headers = null;

    public HeaderInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (headers != null) {
            Request.Builder builder = request.newBuilder();
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getKey() != null && header.getValue() != null) {
                    builder.addHeader(header.getKey(), header.getValue());
                }
            }
            request = builder.build();
        }
        Response response = chain.proceed(request);
        return response;
    }
}
