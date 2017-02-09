package com.carecloud.carepay.patient.notifications.fragments;

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

public class NotificationFragment extends BaseFragment {
    private View noNotificationLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        Gson gson = new Gson();
        TextView noNotificationTitle = (TextView) view.findViewById(R.id.no_notification_message_title);
        TextView noNotificationDesc = (TextView) view.findViewById(R.id.no_notificaton_message_desc);
        noNotificationLayout = view.findViewById(R.id.no_notification_layout);

        String appointmentDtoString = getArguments().getString(CarePayConstants.APPOINTMENT_INFO_BUNDLE);
        AppointmentsResultModel appointmentDTO = gson.fromJson(appointmentDtoString, AppointmentsResultModel.class);

        //TODO this is temporary and must be updated to use proper DTO when this class is implemented
        noNotificationTitle.setText(SystemUtil.validateJsonLabel(appointmentDTO.getMetadata().getLabel().getNoNotificationsMessageTitle()));
        noNotificationDesc.setText(SystemUtil.validateJsonLabel(appointmentDTO.getMetadata().getLabel().getNoNotificationsMessageText()));
        noNotificationLayout.setVisibility(View.VISIBLE);
    }
}
