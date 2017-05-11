package com.carecloud.carepay.patient.notifications.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.notifications.adapters.NotificationsAdapter;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/8/17
 */

public class NotificationFragment extends BaseFragment implements NotificationsAdapter.SelectNotificationCallback {

    private NotificationsDTO notificationsDTO;
    private List<NotificationItem> notificationItems = new ArrayList<>();

    private View noNotificationLayout;
    private RecyclerView notificationsRecycler;
    private SwipeRefreshLayout refreshLayout;

    /**
     * Instantiate Notification Fragment with Notification data
     * @param notificationsDTO notification data
     * @return NotificationFragment
     */
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
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                refreshNotifications();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        notificationsRecycler = (RecyclerView) view.findViewById(R.id.notifications_recycler);
        notificationsRecycler.setLayoutManager(linearLayoutManager);

        setAdapter();

    }

    @Override
    public void onResume(){
        super.onResume();
        refreshNotifications();
    }

    private void setAdapter(){
        if(!notificationItems.isEmpty()){
            if(notificationsRecycler.getAdapter()==null) {
                NotificationsAdapter adapter = new NotificationsAdapter(getContext(), notificationItems, this);
                notificationsRecycler.setAdapter(adapter);
            }else{
                NotificationsAdapter adapter = (NotificationsAdapter) notificationsRecycler.getAdapter();
                adapter.setNotificationItems(notificationItems);
            }

            refreshLayout.setVisibility(View.VISIBLE);
            noNotificationLayout.setVisibility(View.GONE);

        }else{
            refreshLayout.setVisibility(View.GONE);
            noNotificationLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void notificationSelected(NotificationItem notificationItem) {

    }

    private void refreshNotifications(){
        TransitionDTO refreshTransition = notificationsDTO.getMetadata().getLinks().getNotifications();
        getWorkflowServiceHelper().execute(refreshTransition, refreshCallback);
    }

    private WorkflowServiceCallback refreshCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            refreshLayout.setRefreshing(false);
            NotificationsDTO notificationsDTO = DtoHelper.getConvertedDTO(NotificationsDTO.class, workflowDTO);
            if(notificationsDTO!=null){
                notificationItems = notificationsDTO.getPayload().getNotifications();
            }
            setAdapter();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            refreshLayout.setRefreshing(false);
            showErrorNotification(exceptionMessage);
        }
    };
}
