package com.carecloud.carepay.patient.notifications.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepay.patient.notifications.adapters.NotificationsAdapter;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationType;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customcomponents.SwipeViewHolder;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SwipeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lmenendez on 2/8/17
 */

public class NotificationFragment extends BaseFragment
        implements NotificationsAdapter.SelectNotificationCallback, SwipeHelper.SwipeHelperListener {


    public interface NotificationCallback {
        void displayNotification(NotificationItem notificationItem);
    }

    private static final long NOTIFICATION_DELETE_DELAY = 1000 * 5;
    private static final String TAG = NotificationFragment.class.getName();

    private NotificationsDTO notificationsDTO;
    private List<NotificationItem> notificationItems = new ArrayList<>();
    private NotificationCallback callback;

    private View noNotificationLayout;
    private RecyclerView notificationsRecycler;
    private NotificationsAdapter notificationsAdapter;
    private SwipeRefreshLayout refreshLayout;
    private Set<NotificationType> supportedNotificationTypes = new HashSet<>();

    private Handler handler;

    /**
     * Instantiate Notification Fragment with Notification data
     *
     * @param notificationsDTO notification data
     * @return NotificationFragment
     */
    public static NotificationFragment newInstance(NotificationsDTO notificationsDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, notificationsDTO);

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (NotificationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement NotificationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        supportedNotificationTypes.add(NotificationType.appointment);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args != null) {
            notificationsDTO = DtoHelper.getConvertedDTO(NotificationsDTO.class, args);
            if (notificationsDTO != null) {
                notificationItems = filterNotifications(notificationsDTO.getPayload().getNotifications(),
                        supportedNotificationTypes);
            }
        }

        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        noNotificationLayout = view.findViewById(R.id.no_notification_layout);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                refreshNotifications();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        notificationsRecycler = (RecyclerView) view.findViewById(R.id.notifications_recycler);
        notificationsRecycler.setLayoutManager(linearLayoutManager);

        SwipeHelper swipeHelper = new SwipeHelper(this);
        ItemTouchHelper notificationsTouchHelper = new ItemTouchHelper(swipeHelper);
        notificationsTouchHelper.attachToRecyclerView(notificationsRecycler);

        notificationsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                deleteNotificationRunnable.run();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        setAdapter();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notifications_menu, menu);
        menu.findItem(R.id.deleteAllNotifications)
                .setTitle(Label.getLabel("notification.notificationList.button.label.deleteAllTitle"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteAllNotifications) {
            showDeleteAllNotificationsConfirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllNotificationsConfirmDialog() {
        ConfirmDialogFragment fragment = ConfirmDialogFragment
                .newInstance(Label.getLabel("notification.notificationList.button.label.deleteAllTitle"),
                        Label.getLabel("notification.notificationList.button.label.deleteAllMessage"),
                        Label.getLabel("cancel"),
                        Label.getLabel("confirm"));
        fragment.setCallback(new ConfirmDialogFragment.ConfirmationCallback() {
            @Override
            public void onConfirm() {
                deleteAllNotifications();
            }
        });
        String tag = fragment.getClass().getName();
        fragment.show(getFragmentManager().beginTransaction(), tag);
    }

    private void deleteAllNotifications() {
        TransitionDTO transitionDTO = notificationsDTO.getMetadata().getTransitions()
                .getDeleteAllNotifications();
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(transitionDTO, deleteAllNotificationsCallback, queryMap);
    }

    private WorkflowServiceCallback deleteAllNotificationsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            notificationItems = new ArrayList<>();
            setAdapter();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        refreshNotifications();
    }

    private void setAdapter() {
        if (!notificationItems.isEmpty()) {
            if (notificationsRecycler.getAdapter() == null) {
                notificationsAdapter = new NotificationsAdapter(getContext(), notificationItems, this);
                notificationsRecycler.setAdapter(notificationsAdapter);
            } else {
                notificationsAdapter = (NotificationsAdapter) notificationsRecycler.getAdapter();
                notificationsAdapter.setNotificationItems(notificationItems);
            }

            refreshLayout.setVisibility(View.VISIBLE);
            noNotificationLayout.setVisibility(View.GONE);

        } else {
            refreshLayout.setVisibility(View.GONE);
            noNotificationLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void notificationSelected(NotificationItem notificationItem) {
        callback.displayNotification(notificationItem);
    }

    @Override
    public void undoDeleteNotification(SwipeViewHolder holder) {
        Log.d(TAG, "Undo delete");
        NotificationItem notificationItem = notificationItems.get(holder.getAdapterPosition());
        notificationsAdapter.clearRemovedNotification(notificationItem);
        deleteNotificationRunnable.setNotificationItem(null);
        handler.removeCallbacks(deleteNotificationRunnable);
        notificationsRecycler.getAdapter().notifyItemChanged(holder.getAdapterPosition());

    }

    private void refreshNotifications() {
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
            if (notificationsDTO != null) {
                notificationItems = filterNotifications(notificationsDTO.getPayload().getNotifications(),
                        supportedNotificationTypes);
            }
            setAdapter();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            refreshLayout.setRefreshing(false);
            showErrorNotification(exceptionMessage);
        }
    };


    @Override
    public void startNewSwipe() {
        deleteNotificationRunnable.run();
    }

    @Override
    public void viewSwipeCompleted(SwipeViewHolder holder) {
        NotificationItem notificationItem = notificationItems.get(holder.getAdapterPosition());
        notificationsAdapter.scheduleNotificationRemoval(notificationItem);
        notificationsRecycler.getAdapter().notifyItemChanged(holder.getAdapterPosition());
        deleteNotificationRunnable.setNotificationItem(notificationItem);
        handler.postDelayed(deleteNotificationRunnable, NOTIFICATION_DELETE_DELAY);
    }

    private DeleteNotificationRunnable deleteNotificationRunnable = new DeleteNotificationRunnable();

    private class DeleteNotificationRunnable implements Runnable {

        public void setNotificationItem(NotificationItem notificationItem) {
            this.notificationItem = notificationItem;
        }

        private NotificationItem notificationItem;


        @Override
        public void run() {
            if (notificationItem != null) {
                Log.d(TAG, "Deleting notification");
                int index = notificationItems.indexOf(notificationItem);
                notificationsAdapter.clearRemovedNotification(notificationItem);
                notificationItems.remove(notificationItem);
                notificationsRecycler.getAdapter().notifyItemRemoved(index);
                deleteNotification(notificationItem);
                notificationItem = null;
                if (notificationItems.size() == 0) {
                    setAdapter();
                }
            }
        }

        private void deleteNotification(NotificationItem notificationItem) {
            TransitionDTO transitionDTO = notificationsDTO.getMetadata().getTransitions()
                    .getDeleteNotifications();
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("notification_id", notificationItem.getPayload().getNotificationId());

            getWorkflowServiceHelper().execute(transitionDTO, deleteNotificationsCallback, queryMap);
        }

        private WorkflowServiceCallback deleteNotificationsCallback = new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                Log.d(TAG, "Delete notificaton successful");
            }

            @Override
            public void onFailure(String exceptionMessage) {
                Log.d(TAG, "Delete notificaton FAILED");
            }
        };
    }

    private List<NotificationItem> filterNotifications(@NonNull List<NotificationItem> notificationItems,
                                                       @NonNull Set<NotificationType> notificationTypes) {
        List<NotificationItem> filteredList = new ArrayList<>();
        for (NotificationItem notificationItem : notificationItems) {
            NotificationType notificationType = notificationItem.getPayload().getNotificationType();
            if (notificationType != null && notificationTypes.contains(notificationType)) {
                filteredList.add(notificationItem);
            }
        }
        return filteredList;
    }

}
