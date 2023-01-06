package com.carecloud.carepay.patient.signinsignuppatient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.common.BaseViewModel;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
import com.carecloud.carepaylibray.customdialogs.LargeConfirmationAlertDialog;

public class DialogConfirmation2Fsettings extends BaseDialogFragment implements View.OnClickListener {
    private LargeAlertDialogFragment.LargeAlertInterface largeAlertInterface;

    String message, enable, skip,title = "";
    CarePayTextView detailTextView;
    CarePayButton enableButton;
    CarePayButton skipButton;
    CarePayTextView titleTextView;


    public static DialogConfirmation2Fsettings newInstance(String title,String message, String enable, String skip) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("enable", enable);
        args.putString("skip", skip);
        DialogConfirmation2Fsettings fragment = new DialogConfirmation2Fsettings();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.skipButton) {
            cancel();
        } else if (viewId == R.id.enableButton) {
            if (largeAlertInterface != null) {
                largeAlertInterface.onActionButton();
            }
            cancel();
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        message = args.getString("message");
        title = args.getString("title");
        enable = args.getString("enable");
        skip = args.getString("skip");


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirmation_2f_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(R.id.skipButton).setOnClickListener(this);
        findViewById(R.id.enableButton).setOnClickListener(this);
        enableButton = (CarePayButton) findViewById(R.id.enableButton);
        skipButton = (CarePayButton) findViewById(R.id.skipButton);
        titleTextView = (CarePayTextView) findViewById(R.id.titleText);
        detailTextView = (CarePayTextView) findViewById(R.id.detailsText);
        titleTextView.setText(title);
        detailTextView.setText(message);
        enableButton.setText(enable);
        skipButton.setText(skip);



    }

    //For callback
    public interface LargeAlertInterface {
        void onActionButton();
    }


    public void setLargeAlertInterface(LargeAlertDialogFragment.LargeAlertInterface largeAlertInterface) {
        this.largeAlertInterface = largeAlertInterface;
    }
}
