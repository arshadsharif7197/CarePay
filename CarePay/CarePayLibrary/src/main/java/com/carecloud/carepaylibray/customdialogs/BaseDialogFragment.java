package com.carecloud.carepaylibray.customdialogs;

import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

public abstract class BaseDialogFragment extends DialogFragment implements View.OnClickListener {

    protected View view;

    private boolean isFooterVisible;
    private String cancelString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        this.cancelString = arguments.getString("cancelString");
        this.isFooterVisible = arguments.getBoolean("isFooterVisible");;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.base_dialog_fragment, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        onInitialization();
        setDialogCancelText();

        return view;
    }

    private void onInitialization() {
        view.findViewById(R.id.closeViewLayout).setOnClickListener(this);
        view.findViewById(R.id.footer_layout).setVisibility(isFooterVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if(viewId == R.id.closeViewLayout){
            onDialogCancel();
        }
    }

    protected void setDialogTitle(String title){
        ((CarePayTextView) view.findViewById(R.id.content_view_header_title)).setText(title);
    }

    protected void removeHeader(){
        CarePayTextView carePayTextView = (CarePayTextView) view.findViewById(R.id.content_view_header_title);
        ((ViewGroup) carePayTextView.getParent()).removeView(carePayTextView);
    }


    protected void setCancelImage(int resourceId){
        ((ImageView) view.findViewById(R.id.cancel_img)).setImageResource(resourceId);
    }

    private void setDialogCancelText(){
        ((CarePayTextView) view.findViewById(R.id.closeTextView)).setText(cancelString);
    }

    // if caller want to change on cancel then override this method in extended class
    protected  void onDialogCancel(){
        dismiss();
    }

    protected abstract void onAddContentView(LayoutInflater inflater);

    /**
     * inflate the footer view by default visibility gone if have footer then override this method in extended class
     * @param inflater inflater
     */
    protected abstract void onAddFooterView(LayoutInflater inflater);
}
