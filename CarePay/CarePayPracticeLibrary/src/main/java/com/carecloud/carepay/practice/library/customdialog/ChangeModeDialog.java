package com.carecloud.carepay.practice.library.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibray.base.ISession;
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

        LinearLayout patientMode = (LinearLayout)
                findViewById(R.id.dialog_patient_mode_option);

        TextView patientModeOptionText = (TextView) findViewById(R.id.homeChangeModePatientOptionText);
        patientModeOptionText.setText(StringUtil.getLabelForView(options.get(0)));
        patientModeOptionText.setTextColor(ContextCompat.getColor(context,
                com.carecloud.carepaylibrary.R.color.textview_default_textcolor));
        patientMode.setOnClickListener(this);

        LinearLayout logout = (LinearLayout)
                findViewById(R.id.dialog_logout_option);
        TextView logoutOptionText = (TextView) findViewById(R.id.homeChangeModeLogoutOptionText);
        logoutOptionText.setText(StringUtil.getLabelForView(options.get(1)));
        logoutOptionText.setTextColor(ContextCompat.getColor(context,
                com.carecloud.carepaylibrary.R.color.textview_default_textcolor));
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialog_patient_mode_option) {
            patientModeClickListener.onPatientModeSelected();
            ((ISession) context).getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
        } else if (viewId == R.id.dialog_logout_option) {
            logoutClickListener.onLogoutSelected();
        }

        dismiss();
    }
}
