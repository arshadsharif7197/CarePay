package com.carecloud.carepay.mini.utils;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Set;

/**
 * Created by lmenendez on 3/10/17
 */

public class PicassoHelper {

    private static Map<String, String> headers;

    private PicassoHelper() {
    }

    /**
     * Instance Creation
     * @param context Context
     * @param headers headers
     * @return Picasso
     */
    public static Picasso getPicassoInstance(final Context context, Map<String, String> headers) {
        setHeaders(headers);

        Picasso picasso;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new CustomOkHttpDownloader(context));
        picasso = builder.build();

        return picasso;
    }

    public static void setHeaders(Map<String, String> headers) {
        PicassoHelper.headers = headers;
    }

    private static class CustomOkHttpDownloader extends UrlConnectionDownloader {

        public CustomOkHttpDownloader(Context context) {
            super(context);
        }

        @Override
        protected HttpURLConnection openConnection(final Uri uri) throws IOException {
            HttpURLConnection connection = super.openConnection(uri);
            Set<Map.Entry<String, String>> entrySet =  headers.entrySet();
            for (Map.Entry<String, String> entry: entrySet) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            return connection;
        }
    }

}
