package com.carecloud.carepay.patient.Notification.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;

/**
 * Created by lmenendez on 2/8/17.
 */

public class NotificationFragment extends Fragment {
    private TextView noNotificationTitle, noNotificationDesc;
    private View noNotificationLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){

        noNotificationTitle = (TextView) view.findViewById(R.id.no_notification_message_title);
        noNotificationDesc = (TextView) view.findViewById(R.id.no_notificaton_message_desc);
        noNotificationLayout = view.findViewById(R.id.no_notification_layout);

        //TODO this is temporary and must be removed when this class is implemented
        noNotificationTitle.setText("No notifications");
        noNotificationDesc.setText("You do not have any notifications available");
        noNotificationLayout.setVisibility(View.VISIBLE);
    }
}
