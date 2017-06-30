package com.carecloud.carepay.mini.services.interceptors;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by kkannan on 4/24/17.
 * https://github.com/square/retrofit/issues/1072#
 */

public class JSONFormattedLoggingInterceptor implements Interceptor {

    @Override public Response intercept(Chain chain) throws IOException {
        Log.i("LoggingInterceptor","inside intercept callback");
        Request request = chain.request();

        long t1 = System.nanoTime();
        String requestLog = String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers());
        if(request.method().compareToIgnoreCase("post")==0){
            requestLog ="\n"+requestLog+"\n"+bodyToString(request);
        }
        Log.d("TAG","request"+"\n"+requestLog);

        Response response = chain.proceed(request);
        long t2 = System.nanoTime();

        String responseLog = String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers());

        String bodyString = toPrettyFormat(response.body().string());

        Log.d("TAG","response"+"\n"+responseLog+"\n"+bodyString);

        return response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), bodyString))
                .build();
    }

    /**
     * Body to string string.
     *
     * @param request the request
     * @return the string
     */
    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    /**
     * To pretty format string.
     *
     * @param jsonString the json string
     * @return the string
     */
    private String toPrettyFormat(String jsonString) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(jsonString).getAsJsonObject();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(json);
        }catch (Exception ex){
            return jsonString;
        }
    }


}
