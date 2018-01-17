package com.carecloud.carepay.practice.library.payments.dialogs;

import android.os.Bundle;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.models.ResponsibilityHeaderModel;
import com.carecloud.carepaylibray.base.models.UserAuthPermissions;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 11/7/17
 */

public class FormsResponsibilityFragmentDialog extends ResponsibilityFragmentDialog {

    /**
     * @param paymentsModel the payment model
     * @param leftLabel     the label of the left bottom button
     * @param rightLabel    the label of the right bottom button
     * @return new instance of a ResponsibilityFragmentDialog
     */
    public static FormsResponsibilityFragmentDialog newInstance(PaymentsModel paymentsModel,
                                                           UserAuthPermissions authPermissions,
                                                           String leftLabel,
                                                           String rightLabel,
                                                           String emptyMessage,
                                                           ResponsibilityHeaderModel headerModel) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, headerModel);
        DtoHelper.bundleDto(args, authPermissions);
        args.putString(KEY_LEFT_BUTTON, leftLabel);
        args.putString(KEY_RIGHT_BUTTON, rightLabel);
        args.putString(KEY_EMPTY_MESSAGE, emptyMessage);

        FormsResponsibilityFragmentDialog dialog = new FormsResponsibilityFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_dialog_payment_responsibility_forms;
    }

}
