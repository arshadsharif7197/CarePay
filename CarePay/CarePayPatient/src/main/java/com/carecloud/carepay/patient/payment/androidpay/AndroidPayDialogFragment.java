package com.carecloud.carepay.patient.payment.androidpay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.android.gms.wallet.MaskedWallet;

/**
 * Created by lmenendez on 10/17/17
 */

public class AndroidPayDialogFragment extends BaseDialogFragment {
    private static final String KEY_MASKED_WALLET = "masked_wallet";

    private ViewGroup detailsContainer;
    private ViewGroup buttonContainer;

    private AndroidPayAdapter androidPayAdapter;

    private MaskedWallet maskedWallet;
    private PaymentsModel paymentsModel;

    public static AndroidPayDialogFragment newInstance(MaskedWallet maskedWallet, PaymentsModel paymentsModel){
        Bundle args = new Bundle();
        args.putParcelable(KEY_MASKED_WALLET, maskedWallet);
        DtoHelper.bundleDto(args, paymentsModel);

        AndroidPayDialogFragment fragment = new AndroidPayDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        if(args != null){
            maskedWallet = args.getParcelable(KEY_MASKED_WALLET);
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        }
        androidPayAdapter = new AndroidPayAdapter(getActivity(), paymentsModel.getPaymentPayload().getMerchantServices(), getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_androidpay_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        detailsContainer = (ViewGroup) view.findViewById(R.id.androidpay_invoice_container);
        buttonContainer = (ViewGroup) view.findViewById(R.id.androidpay_confirm_button_container);
        initChildFragments();
    }

    private void initChildFragments(){
        androidPayAdapter.createWalletDetails(maskedWallet, detailsContainer);
    }
}
