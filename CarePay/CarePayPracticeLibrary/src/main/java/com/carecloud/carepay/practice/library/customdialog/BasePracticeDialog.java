package com.carecloud.carepay.practice.library.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

/**
 * Created by prem_mourya on 11/21/2016.
 */

public abstract class BasePracticeDialog extends Dialog implements View.OnClickListener {


    private Context context;
    private boolean isFooterVisible;
    private String cancelString;


    /**
     * Constructor.
     *
     * @param context context
     */
    public BasePracticeDialog(Context context, String cancelString, boolean isFooterVisible) {
        super(context);
        this.context = context;
        this.cancelString = cancelString;
        this.isFooterVisible = isFooterVisible;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_base_practice);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.88);
        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        onInitialization();
        setDialogCancelText(cancelString);
    }

    private void onInitialization() {
        (findViewById(R.id.closeViewLayout)).setOnClickListener(this);
        (findViewById(R.id.footer_layout)).setVisibility(isFooterVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.closeViewLayout) {
            onDialogCancel();
        }
    }

    protected void setDialogTitle(String title) {
        ((CarePayTextView) findViewById(R.id.content_view_header_title)).setText(title);
    }

    protected void removeHeader() {
        CarePayTextView carePayTextView = (CarePayTextView) findViewById(R.id.content_view_header_title);
        ((ViewGroup) carePayTextView.getParent()).removeView(carePayTextView);
    }


    protected void setCancelImage(int resourceId) {
        ((ImageView) findViewById(R.id.cancel_img)).setImageResource(resourceId);
    }

    private void setDialogCancelText(String title) {
        ((CarePayTextView) findViewById(R.id.closeTextView)).setText(title);
    }

    // if caller want to change on cancel then override this method in extended class
    protected void onDialogCancel() {
        dismiss();
    }

    protected abstract void onAddContentView(LayoutInflater inflater);

    /**
     * inflate the footer view by default visibility gone if have footer then override this method in extended class
     *
     * @param inflater inflater
     */
    protected abstract void onAddFooterView(LayoutInflater inflater);


}
