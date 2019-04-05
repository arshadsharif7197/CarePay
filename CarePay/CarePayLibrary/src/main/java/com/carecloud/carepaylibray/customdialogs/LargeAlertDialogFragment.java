package com.carecloud.carepaylibray.customdialogs;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

/**
 * Created by prem_mourya on 10/5/2016.
 */

public class LargeAlertDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private LargeAlertInterface largeAlertInterface;
    private String actionText;
    private String message;
    private int headerBackGroundColor;
    private int headerIcon;

    //For callback
    public interface LargeAlertInterface {
        void onActionButton();
    }

    public static LargeAlertDialogFragment newInstance(String message,
                                                       String actionText,
                                                       int headerBackGroundColor,
                                                       int headerIcon) {
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("actionText", actionText);
        args.putInt("headerBackGroundColor", headerBackGroundColor);
        args.putInt("headerIcon", headerIcon);
        LargeAlertDialogFragment fragment = new LargeAlertDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        message = args.getString("message");
        actionText = args.getString("actionText");
        headerBackGroundColor = args.getInt("headerBackGroundColor");
        headerIcon = args.getInt("headerIcon");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_large_alert, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(R.id.dialogCloseImageView).setOnClickListener(this);
        Button actionCallButton = view.findViewById(R.id.actionButton);
        actionCallButton.setOnClickListener(this);
        actionCallButton.setText(actionText);
        CarePayTextView largeMssageLabel = view.findViewById(R.id.largeMssageLabel);
        largeMssageLabel.setText(message);
        largeMssageLabel.setTextColor(getContext().getResources().getColor(R.color.white));
        largeMssageLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        setBackgroundColor();
        findViewById(R.id.headerIconImageView).setBackgroundResource(headerIcon);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.dialogCloseImageView) {
            cancel();
        } else if (viewId == R.id.actionButton) {
            if (largeAlertInterface != null) {
                largeAlertInterface.onActionButton();
            }
            cancel();
        }
    }

    private void setBackgroundColor() {
        View header = findViewById(R.id.headerLayout);
        try {
            GradientDrawable drawable = (GradientDrawable) header.getBackground();
            drawable.setColor(ContextCompat.getColor(getContext(), headerBackGroundColor));
        } catch (ClassCastException cce) {
            header.setBackgroundResource(headerBackGroundColor);
        }
    }

    public void setLargeAlertInterface(LargeAlertInterface largeAlertInterface) {
        this.largeAlertInterface = largeAlertInterface;
    }
}