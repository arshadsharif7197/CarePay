package com.carecloud.carepay.practice.library.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class ChangeModeDialog extends Dialog implements View.OnClickListener {

    private List<String> options;
    private Context context;
    private PatientModeClickListener patientModeClickListener;
    private LogoutClickListener logoutClickListener;

    public interface PatientModeClickListener {
        void onPatientModeSelected();
    }

    public interface LogoutClickListener {
        void onLogoutSelected();
    }

    /**
     * Constructor.
     * @param context context
     * @param patientModeClickListener patient mode click listener
     * @param logoutClickListener logout click listener
     */
    public ChangeModeDialog(Context context, PatientModeClickListener patientModeClickListener,
                            LogoutClickListener logoutClickListener, List<String> options) {
        super(context);
        this.context = context;
        this.patientModeClickListener = patientModeClickListener;
        this.logoutClickListener = logoutClickListener;
        this.options = options;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_change_mode);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        CustomProxyNovaSemiBoldLabel patientMode = (CustomProxyNovaSemiBoldLabel)
                findViewById(R.id.dialog_patient_mode_option);
        patientMode.setText(StringUtil.getLabelForView(options.get(0)));
        patientMode.setTextColor(ContextCompat.getColor(context,
                com.carecloud.carepaylibrary.R.color.textview_default_textcolor));
        patientMode.setOnClickListener(this);

        CustomProxyNovaSemiBoldLabel logout = (CustomProxyNovaSemiBoldLabel)
                findViewById(R.id.dialog_logout_option);
        logout.setText(StringUtil.getLabelForView(options.get(1)));
        logout.setTextColor(ContextCompat.getColor(context,
                com.carecloud.carepaylibrary.R.color.textview_default_textcolor));
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialog_patient_mode_option) {
            patientModeClickListener.onPatientModeSelected();
        } else if (viewId == R.id.dialog_logout_option) {
            logoutClickListener.onLogoutSelected();
        }

        dismiss();
    }
}
