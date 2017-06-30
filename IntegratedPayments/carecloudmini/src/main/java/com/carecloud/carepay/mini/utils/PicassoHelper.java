package com.carecloud.carepay.mini.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lmenendez on 3/10/17
 */

public class PicassoHelper {

    private static Map<String, String> headers = new HashMap<>();

    private PicassoHelper() {
    }

    /**
     * Instance Creation
     * @param context Context
     * @return Picasso
     */
    public static Picasso getPicassoInstance(final Context context) {
        Picasso picasso;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new CustomOkHttpDownloader(context));
        builder.listener(new CustomListener());
        picasso = builder.build();

        return picasso;
    }

    /**
     * Update the headers to use for network calls
     * @param headers header map
     */
    public static void setHeaders(Map<String, String> headers) {
        if(headers != null) {
            PicassoHelper.headers = headers;
        }
    }

    private static class CustomOkHttpDownloader extends UrlConnectionDownloader {

        CustomOkHttpDownloader(Context context) {
            super(context);
        }

        @Override
        protected HttpURLConnection openConnection(final Uri uri) throws IOException {
            HttpURLConnection connection = super.openConnection(uri);
            connection.setRequestMethod("GET");
            Set<Map.Entry<String, String>> entrySet =  headers.entrySet();
            for (Map.Entry<String, String> entry: entrySet) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            return connection;
        }
    }

    private static class CustomListener implements Picasso.Listener{
        @Override
        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
            Log.d(PicassoHelper.class.getName(), "Failed to load " + uri + "\n" + exception.getMessage());
        }
    }

}
