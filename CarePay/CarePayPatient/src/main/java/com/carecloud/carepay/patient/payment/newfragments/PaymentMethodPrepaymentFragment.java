package com.carecloud.carepay.patient.payment.newfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by lmenendez on 8/21/17
 */

public class PaymentMethodPrepaymentFragment extends PatientPaymentMethodFragment {
    private static final String KEY_TITLE = "title";

    private AppointmentPrepaymentCallback callback;
    private String titleString;

    /**
     * @param practiceId the practice Id
     * @param amount     the amount
     * @return an instance of PatientPaymentMethodFragment
     */
    public static PaymentMethodPrepaymentFragment newInstance(String practiceId,
                                                              double amount,
                                                              String title) {
        PaymentMethodPrepaymentFragment fragment = new PaymentMethodPrepaymentFragment();
        Bundle args = new Bundle();
        args.putString(CarePayConstants.PRACTICE_ID, practiceId);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        args.putString(KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try {
//            if (context instanceof AppointmentViewHandler) {
//                callback = (AppointmentPrepaymentCallback) ((AppointmentViewHandler) context).getAppointmentPresenter();
//            } else {
//                callback = (AppointmentPrepaymentCallback) context;
//            }
//        } catch (ClassCastException cce) {
//            throw new ClassCastException("Attached Context must implement PaymentMethodInterface");
//        }
//    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        titleString = args.getString(KEY_TITLE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_pre_payment_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        super.onViewCreated(view, icicle);

        TextView prepaymentAmount = view.findViewById(R.id.prepaymentAmount);
        prepaymentAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(amountToMakePayment));

    }

    @Override
    public void setupTitleViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(titleString);
            toolbar.setTitle("");

            toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
            toolbar.setNavigationOnClickListener(view1 -> {
                getActivity().onBackPressed();
//                callback.onPaymentDismissed();
            });
//            if (callback instanceof PatientAppointmentPresenter) {
//                ((PatientAppointmentPresenter) callback).displayToolbar(false, null);
//            }
        }

    }

}
