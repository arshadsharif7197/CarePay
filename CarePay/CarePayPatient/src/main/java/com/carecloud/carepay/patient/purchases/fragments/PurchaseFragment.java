package com.carecloud.carepay.patient.purchases.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

/**
 * Created by lmenendez on 2/8/17.
 */

public class PurchaseFragment extends BaseFragment {

    private View noPurchaseLayout;// this should be available here to access it for show/hide from other methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_purchase, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        Gson gson = new Gson();
        TextView noPurchaseTitle = (TextView) view.findViewById(R.id.no_purchase_message_title);
        TextView noPurchaseDesc = (TextView) view.findViewById(R.id.no_purchase_message_desc);
        noPurchaseLayout = view.findViewById(R.id.no_purchase_layout);

        //TODO this is temporary and must be updated to use proper DTO when this class is implemented
        String appointmentDtoString = getArguments().getString(CarePayConstants.APPOINTMENT_INFO_BUNDLE);
        AppointmentsResultModel appointmentDTO = gson.fromJson(appointmentDtoString, AppointmentsResultModel.class);

        noPurchaseTitle.setText(SystemUtil.validateJsonLabel(appointmentDTO.getMetadata().getLabel().getNoShopMessageTitle()));
        noPurchaseDesc.setText(SystemUtil.validateJsonLabel(appointmentDTO.getMetadata().getLabel().getNoShopMessageText()));
        noPurchaseLayout.setVisibility(View.VISIBLE);

    }
}
