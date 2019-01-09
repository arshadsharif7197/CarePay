package com.carecloud.carepaylibray.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseActivity;

import java.util.Map;

/**
 * @author pjohnson on 19/02/18.
 */

public class FileDownloadUtil {

    public static void downloadPdf(Context context, String url, String title,
                                   String fileExtension, String description) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title + fileExtension);
        request.setDescription(description);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setMimeType("application/pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.addRequestHeader("Accept", "application/pdf");
        request.addRequestHeader("username", ((BaseActivity) context).getAppAuthorizationHelper().getCurrUser());
        request.addRequestHeader("Authorization", ((BaseActivity) context).getAppAuthorizationHelper().getIdToken());
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + fileExtension);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    public static void downloadFile(Context context, @NonNull String url, @NonNull String filename, @NonNull String extension, String description, Map<String, String> headers){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        if(!filename.contains(extension)){
            filename = filename + "." + extension;
        }
        request.setTitle(filename);

        if(description != null) {
            request.setDescription(description);
        }

        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        request.setMimeType(mimeType);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        if(headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addRequestHeader(entry.getKey(), entry.getValue());
            }
        }
        request.addRequestHeader("Accept", mimeType);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if(downloadManager != null) {
            downloadManager.enqueue(request);
        } else {
            Toast.makeText(context, Label.getLabel("connection_error_message"), Toast.LENGTH_SHORT).show();
        }
    }
}
