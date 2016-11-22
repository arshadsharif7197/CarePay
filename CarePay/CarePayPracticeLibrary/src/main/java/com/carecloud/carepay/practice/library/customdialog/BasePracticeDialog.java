package com.carecloud.carepay.practice.library.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

/**
 * Created by prem_mourya on 11/21/2016.
 */

public abstract class BasePracticeDialog extends Dialog implements View.OnClickListener {


    private Context context;


    /**
     * Constructor.
     * @param context context
     */
    public BasePracticeDialog(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_base_practice);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        onInitialization();
        //TODO labels will be get from DTO and Set
        setDialogCancelText(context.getString(R.string.cancel));
    }

    private void onInitialization(){
        ((LinearLayout) findViewById(R.id.closeViewLayout)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if(viewId == R.id.closeViewLayout){
            onDialogCancel();
        }
    }

    protected void setDialogTitle(String title){
        ((CarePayTextView) findViewById(R.id.content_view_header_title)).setText(title);
    }

    private void setDialogCancelText(String title){
        ((CarePayTextView) findViewById(R.id.closeTextView)).setText(title);
    }

    // if caller want to change on cancel then override this method in extended class
    protected  void onDialogCancel(){
        dismiss();
    }

    protected abstract  void onAddContentView(LayoutInflater inflater);

    /**
     * inflate the footer view by default visibility gone if have footer then override this method in extended class
     * @param inflater
     */
    protected abstract void onAddFooterView(LayoutInflater inflater);


}
