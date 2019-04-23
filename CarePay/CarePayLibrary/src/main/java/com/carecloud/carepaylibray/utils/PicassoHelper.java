package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.carecloud.carepaylibrary.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Set;

/**
 * Created by lmenendez on 3/10/17.
 */

public class PicassoHelper {

    private static PicassoHelper INSTANCE;
    private static Map<String, String> headers;

    private PicassoHelper() {
//        Picasso.setSingletonInstance(Picasso.with(((AndroidPlatform) Platform.get()).getContext()));
    }

    public static PicassoHelper get() {
        if (INSTANCE == null) {
            INSTANCE = new PicassoHelper();
        }

        return INSTANCE;
    }

    /**
     * Instance Creation
     *
     * @param context Context
     * @return Picasso
     */
    public static Picasso getPicassoInstance(final Context context) {

        Picasso picasso;
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new CustomOkHttpDownloader(context));
        picasso = builder.build();
        picasso.setLoggingEnabled(true);

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
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            return connection;
        }
    }

    public void loadImage(Context context, final ImageView imageView, final View viewToHide, String photoUrl) {
        loadImage(context, imageView, viewToHide, photoUrl,
                context.getResources().getDimensionPixelSize(R.dimen.payment_details_dialog_icon_size));
    }

    public void loadImage(Context context, final ImageView imageView, final View viewToHide, String photoUrl,
                          int size) {
        loadImage(context, imageView, viewToHide, null, photoUrl, size);
    }

    public void loadImage(Context context, final ImageView imageView, final View viewToHide, final View viewToShow, String photoUrl,
                          int size) {
        if (!StringUtil.isNullOrEmpty(photoUrl)) {
            Picasso.with(context).load(photoUrl)
                    .resize(size, size)
                    .centerCrop()
                    .transform(new CircleImageTransform())
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            imageView.setVisibility(View.VISIBLE);
                            if (viewToHide != null) {
                                viewToHide.setVisibility(View.GONE);
                            }
                            if (viewToShow != null) {
                                viewToShow.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError() {
                            if (viewToHide != null) {
                                viewToHide.setVisibility(View.VISIBLE);
                            }
                            if (viewToShow != null) {
                                viewToShow.setVisibility(View.GONE);
                            }
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            if (viewToHide != null) {
                viewToHide.setVisibility(View.VISIBLE);
            }
            if (viewToShow != null) {
                viewToShow.setVisibility(View.GONE);
            }
            imageView.setVisibility(View.INVISIBLE);
        }
    }

}
