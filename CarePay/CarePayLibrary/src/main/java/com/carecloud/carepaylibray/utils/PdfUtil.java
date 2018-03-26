package com.carecloud.carepaylibray.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.carecloud.carepaylibray.base.BaseActivity;

/**
 * @author pjohnson on 19/02/18.
 */

public class PdfUtil {

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
}
