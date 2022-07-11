package com.carecloud.carepaylibray.customdialogs;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

public class ExitAlertDialog extends BaseDialogFragment implements View.OnClickListener {
    private ExitAlertInterface exitAlertInterface;
    private String message,yes,no;
    private int headerBackGroundColor;
    CarePayTextView largeMssageLabel;
    CarePayButton yesButton;
    CarePayButton noButton;


    //For callback
    public interface ExitAlertInterface {
        void onActionButton();
    }



    public static ExitAlertDialog newInstance(String message, String yes, String no) {
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("yes", yes);
        args.putString("no", no);
        ExitAlertDialog fragment = new ExitAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        message = args.getString("message");
        yes = args.getString("yes");
        no = args.getString("no");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_selfpay_alert, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(R.id.actionButtonNo).setOnClickListener(this);
        findViewById(R.id.actionButtonYes).setOnClickListener(this);
        largeMssageLabel= (CarePayTextView) findViewById(R.id.largeMssageLabel);
        yesButton= (CarePayButton) findViewById(R.id.actionButtonYes);
        noButton= (CarePayButton) findViewById(R.id.actionButtonNo);
        largeMssageLabel.setText(message);
        yesButton.setText(yes);
        noButton.setText(no);


    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.actionButtonNo) {
            cancel();
        } else if (viewId == R.id.actionButtonYes) {
            if (exitAlertInterface != null) {
                exitAlertInterface.onActionButton();
            }
            cancel();
        }
    }

    private void setBackgroundColor() {
        View header = findViewById(R.id.headerLayout);
        if (headerBackGroundColor == -1) {
            headerBackGroundColor = R.color.Feldgrau;
        }
        try {
            GradientDrawable drawable = (GradientDrawable) header.getBackground();
            drawable.setColor(ContextCompat.getColor(getContext(), headerBackGroundColor));
        } catch (ClassCastException cce) {
            header.setBackgroundResource(headerBackGroundColor);
        }
    }

    public void setExitAlertInterface(ExitAlertInterface exitAlertInterface) {
        this.exitAlertInterface = exitAlertInterface;
    }


}
