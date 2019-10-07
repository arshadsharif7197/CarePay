package com.carecloud.carepay.practice.library.payments.dialogs;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.models.ResponsibilityHeaderModel;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.models.UserAuthPermissions;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 11/7/17
 */

public class FormsResponsibilityFragmentDialog extends ResponsibilityFragmentDialog {

    private UserAuthPermissions authPermissions;

    /**
     * Ger Instance
     * @param paymentsModel payment model
     * @param authPermissions permissions
     * @param headerModel heaer
     * @param selectedBalance patient balance
     * @return instance
     */
    public static FormsResponsibilityFragmentDialog newInstance(PaymentsModel paymentsModel,
                                                           UserAuthPermissions authPermissions,
                                                           ResponsibilityHeaderModel headerModel,
                                                                PatientBalanceDTO selectedBalance) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, headerModel);
        DtoHelper.bundleDto(args, authPermissions);
        DtoHelper.bundleDto(args, selectedBalance);

        FormsResponsibilityFragmentDialog dialog = new FormsResponsibilityFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        authPermissions = DtoHelper.getConvertedDTO(UserAuthPermissions.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_dialog_payment_responsibility_forms, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View addAppt = view.findViewById(R.id.payment_pay_button);
        addAppt.setEnabled(authPermissions.canScheduleAppointment);

        TextView message = (TextView) view.findViewById(R.id.no_payment_message);
        message.setText(Label.getLabel("payment_balance_empty_appointment_screen"));

        View leftButton = view.findViewById(R.id.payment_plan_button);
        leftButton.setVisibility(View.VISIBLE);

    }


}
