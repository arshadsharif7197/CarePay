package com.carecloud.carepay.patient.notifications.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/8/17.
 */

public class NotificationFragment extends BaseFragment {

    private NotificationsDTO notificationsDTO;
    private List<NotificationItem> notificationItems = new ArrayList<>();

    private View noNotificationLayout;
    private RecyclerView notificationsRecycler;

    public static NotificationFragment newInstance(NotificationsDTO notificationsDTO){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, notificationsDTO);

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        if(args != null){
            notificationsDTO = DtoHelper.getConvertedDTO(NotificationsDTO.class, args);
            if(notificationsDTO!=null){
                notificationItems = notificationsDTO.getPayload().getNotifications();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        noNotificationLayout = view.findViewById(R.id.no_notification_layout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        notificationsRecycler = (RecyclerView) view.findViewById(R.id.notifications_recycler);
        noNotificationLayout.setVisibility(View.VISIBLE);


    }
}
