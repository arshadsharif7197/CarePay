package com.carecloud.carepaylibray.utils;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseActivity;

import java.io.File;
import java.util.Map;

/**
 * @author pjohnson on 19/02/18.
 */

public class FileDownloadUtil {

    public static long downloadPdf(Context context, String url, String title,
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
        return downloadManager.enqueue(request);
    }

    public static long downloadFile(Context context,
                                    @NonNull String url,
                                    @NonNull String filename,
                                    @NonNull String extension,
                                    String description,
                                    Map<String, String> headers) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        if (!filename.contains(extension)) {
            filename = filename + "." + extension;
        }
        request.setTitle(filename);

        if (description != null) {
            request.setDescription(description);
        }

        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        request.setMimeType(mimeType);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addRequestHeader(entry.getKey(), entry.getValue());
            }
        }
        request.addRequestHeader("Accept", mimeType);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            return downloadManager.enqueue(request);
        } else {
            Toast.makeText(context, Label.getLabel("connection_error_message"), Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    public static void openDownloadedAttachment(final Context context, Uri attachmentUri, final String attachmentMimeType) {
        if (attachmentUri != null) {
            // Get Content Uri.
            if (ContentResolver.SCHEME_FILE.equals(attachmentUri.getScheme())) {
                // FileUri - Convert it to contentUri.
                File file = new File(attachmentUri.getPath());
                attachmentUri = FileProvider.getUriForFile(context,
                        "com.carecloud.carepay.patient.provider", file);
            }

            Intent openAttachmentIntent = new Intent(Intent.ACTION_VIEW);
            openAttachmentIntent.setDataAndType(attachmentUri, attachmentMimeType);
            openAttachmentIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(openAttachmentIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, context.getString(R.string.alert_title_server_error), Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void openDownloadDirectory(Context context) {
        context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }
}
