package com.carecloud.carepay.practice.library.homescreen.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenOfficeNewsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenOfficeNewsPayloadDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.RoundedImageTransfer;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

public class OfficeNewsDetailsDialog extends Dialog {

    private Context context;
    private String titleText;
    private String cancelText;
    private HomeScreenOfficeNewsDTO officeNewsPost;

    /**
     * Constructor
     * @param context context
     * @param title dialog title
     * @param cancelText dialog cancel
     * @param officeNewsPost data
     */
    public OfficeNewsDetailsDialog(Context context, String title, String cancelText,
                                   HomeScreenOfficeNewsDTO officeNewsPost) {
        super(context);
        this.context = context;
        this.titleText = title;
        this.cancelText = cancelText;
        this.officeNewsPost = officeNewsPost;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_office_news_details);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        initializeView();
        handleException();
    }

    @SuppressWarnings("AccessStaticViaInstance")
    private void handleException() {
        Thread thread = Thread.currentThread();
        thread.setDefaultUncaughtExceptionHandler(new SystemUtil());
    }

    private void initializeView() {
        ((TextView) findViewById(R.id.office_news_details_cancel_label)).setText(cancelText);
        ((TextView) findViewById(R.id.office_news_details_header)).setText(titleText);

        if (officeNewsPost != null && officeNewsPost.getPayload() != null) {
            HomeScreenOfficeNewsPayloadDTO payload = officeNewsPost.getPayload();

            String publishedDate = DateUtil.getInstance().setDateRaw(payload.getPublishDate())
                    .getDateAsMonthLiteralDayOrdinalYear();
            ((TextView) findViewById(R.id.office_news_published_date)).setText(publishedDate);
            ((TextView) findViewById(R.id.office_news_headline)).setText(payload.getHeadline());
            ((TextView) findViewById(R.id.office_news_body)).setText(payload.getBody());

            ImageView photo = (ImageView) findViewById(R.id.office_news_headline_photo);
            Picasso.with(context).load(payload.getHeadlinePhoto()).transform(
                    new RoundedImageTransfer(20, 0)).resize(528, 222).into(photo);
        }

        findViewById(R.id.office_news_details_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
