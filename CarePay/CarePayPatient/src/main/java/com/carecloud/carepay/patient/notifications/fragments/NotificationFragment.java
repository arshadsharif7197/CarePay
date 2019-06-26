package com.carecloud.carepay.patient.notifications.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.notifications.adapters.NotificationsAdapter;
import com.carecloud.carepay.patient.notifications.models.CustomNotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationType;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customcomponents.SwipeViewHolder;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
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

    private static final long NOTIFICATION_DELETE_DELAY = 1000 * 5;
    private static final String TAG = NotificationFragment.class.getName();
    private static final int ITEMS_PER_PAGE = 100;
    private static final int BOTTOM_ROW_OFFSET = (int) (ITEMS_PER_PAGE * 0.33);

    private NotificationsDTO notificationsDTO;
    private List<NotificationItem> notificationItems = new ArrayList<>();
    private NotificationCallback callback;

    private View noNotificationLayout;
    private RecyclerView notificationsRecycler;
    private NotificationsAdapter notificationsAdapter;
    private SwipeRefreshLayout refreshLayout;
    private Set<NotificationType> supportedNotificationTypes = new HashSet<>();

    private Handler handler;
    private boolean isPaging;
    private Paging paging;

    public interface NotificationCallback {
        void displayNotification(NotificationItem notificationItem);
    }

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
        supportedNotificationTypes.add(NotificationType.pending_forms);
        supportedNotificationTypes.add(NotificationType.payments);
        supportedNotificationTypes.add(NotificationType.secure_message);
        supportedNotificationTypes.add(NotificationType.pending_survey);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        notificationsDTO = DtoHelper.getConvertedDTO(NotificationsDTO.class, args);
        notificationItems = filterNotifications(notificationsDTO.getPayload().getNotifications(),
                supportedNotificationTypes);
        paging = notificationsDTO.getPayload().getPaging();
        addAppStatusNotification();
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        noNotificationLayout = view.findViewById(R.id.no_notification_layout);
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                loadNextPage(true);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        notificationsRecycler = view.findViewById(R.id.notifications_recycler);
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
                int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (last > notificationsAdapter.getItemCount() - BOTTOM_ROW_OFFSET && !isPaging) {
                    if (hasMorePages()) {
                        loadNextPage(false);
                        isPaging = true;
                    }
                }
            }
        });
        setAdapter();
    }

    private boolean hasMorePages() {
        return paging.getCurrentPage() < paging.getTotalPages();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notifications_menu, menu);
        menu.findItem(R.id.deleteAllNotifications)
                .setTitle(Label.getLabel("notification.notificationList.button.label.deleteAllTitle")
                        .replace("?", ""));
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
        fragment.setCallback(new ConfirmationCallback() {
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


    private void setAdapter() {
        boolean canViewNotifications = canViewAnyNotification(notificationsDTO.getPayload().getDelegate() == null ?
                notificationsDTO.getPayload().getPracticeInformation()
                : notificationsDTO.getPayload().getUserLinks().getDelegatePracticeInformation());
        if (canViewNotifications) {
            if (!notificationItems.isEmpty()) {
                if (notificationsAdapter == null) {
                    notificationsAdapter = new NotificationsAdapter(getContext(), notificationItems, this);
                    notificationsRecycler.setAdapter(notificationsAdapter);
                } else {
                    notificationsAdapter.setNotificationItems(notificationItems);
                }

                refreshLayout.setVisibility(View.VISIBLE);
                noNotificationLayout.setVisibility(View.GONE);

            } else {
                showEmptyScreen();
            }
        } else {
            showEmptyScreen();
            TextView titleTextView = noNotificationLayout.findViewById(R.id.no_notification_message_title);
            titleTextView.setText(Label.getLabel("patient.delegation.delegates.permissions.label.noPermission"));
            noNotificationLayout.findViewById(R.id.no_notificaton_message_desc).setVisibility(View.GONE);
        }
    }

    private void showEmptyScreen() {
        refreshLayout.setVisibility(View.GONE);
        noNotificationLayout.setVisibility(View.VISIBLE);
    }

    private boolean canViewAnyNotification(List<UserPracticeDTO> userPractices) {
        boolean atLeastOneHasPermission = false;
        for (UserPracticeDTO practice : userPractices) {
            if (notificationsDTO.getPayload().canViewNotifications(practice.getPracticeId())) {
                atLeastOneHasPermission = true;
            }
        }
        return atLeastOneHasPermission
                || (userPractices.isEmpty() && notificationsDTO.getPayload().getDelegate() == null);
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
        notificationsAdapter.notifyItemChanged(holder.getAdapterPosition());

    }

    @Override
    public UserPracticeDTO getUserPracticeById(String practiceId) {
        for (UserPracticeDTO practice : notificationsDTO.getPayload().getPracticeInformation()) {
            if (practice.getPracticeId().equals(practiceId)) {
                return practice;
            }
        }
        return null;
    }

    private void loadNextPage(boolean refresh) {
        long currentPage;
        if (refresh) {
            currentPage = 0;
        } else {
            currentPage = paging.getCurrentPage();
        }

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("page", String.valueOf(currentPage + 1));
        queryMap.put("limit", String.valueOf(ITEMS_PER_PAGE));

        TransitionDTO refreshTransition = notificationsDTO.getMetadata().getLinks().getNotifications();
        getWorkflowServiceHelper().execute(refreshTransition, getRefreshCallback(refresh), queryMap);
    }

    private WorkflowServiceCallback getRefreshCallback(final boolean refresh) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                if (!refresh) {
                    notificationsAdapter.setLoading(true);
                }
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                refreshLayout.setRefreshing(false);
                NotificationsDTO notificationsDTO = DtoHelper.getConvertedDTO(NotificationsDTO.class, workflowDTO);
                paging = notificationsDTO.getPayload().getPaging();
                if (refresh) {
                    notificationItems = filterNotifications(notificationsDTO.getPayload().getNotifications(),
                            supportedNotificationTypes);
                    addAppStatusNotification();
                } else {
                    notificationItems.addAll(filterNotifications(notificationsDTO.getPayload().getNotifications(),
                            supportedNotificationTypes));
                }
                notificationsAdapter.setLoading(false);
                isPaging = false;
                setAdapter();
            }


            @Override
            public void onFailure(String exceptionMessage) {
                refreshLayout.setRefreshing(false);
                if (!refresh) {
                    notificationsAdapter.setLoading(false);
                }
                isPaging = false;
                showErrorNotification(exceptionMessage);
            }
        };
    }

    @Override
    public void startNewSwipe() {
        deleteNotificationRunnable.run();
    }

    @Override
    public void viewSwipeCompleted(SwipeViewHolder holder) {
        NotificationItem notificationItem = notificationItems.get(holder.getAdapterPosition());
        if (notificationItem instanceof CustomNotificationItem) {
            getApplicationPreferences().setRemindLatest(false);
            notificationItems.remove(notificationItem);
            notificationsAdapter.notifyItemRemoved(holder.getAdapterPosition());
            if (notificationItems.size() == 0) {
                setAdapter();
            }
        } else {
            notificationsAdapter.scheduleNotificationRemoval(notificationItem);
            notificationsAdapter.notifyItemChanged(holder.getAdapterPosition());
            deleteNotificationRunnable.setNotificationItem(notificationItem);
            handler.postDelayed(deleteNotificationRunnable, NOTIFICATION_DELETE_DELAY);
        }
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
                notificationsAdapter.notifyItemRemoved(index);
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
            NotificationType notificationType = notificationItem.getMetadata().getNotificationType();
            if (notificationType != null && notificationTypes.contains(notificationType)) {
                if (notificationType.equals(NotificationType.pending_forms)
                        && notificationItem.getPayload().getPracticeName() == null) {
                    //Prevent showing old notifications without pending form data
                    continue;
                }
                if (notificationType.equals(NotificationType.pending_survey)
                        && (!notificationsDTO.getPayload()
                        .canViewSurveyNotifications(notificationItem.getMetadata().getPracticeId()))) {
                    continue;
                }
                filteredList.add(notificationItem);
            } else {
                Log.d("test", "test");
            }
        }
        return filteredList;
    }

    private void addAppStatusNotification() {
        boolean shouldRemind = getApplicationPreferences().shouldRemindLatest();
        if (shouldRemind) {
            boolean isLatest = getApplicationPreferences().isLatestVersion();
            CustomNotificationItem customNotificationItem = new CustomNotificationItem();
            customNotificationItem.setNotificationType(isLatest ?
                    CustomNotificationItem.TYPE_APP_UPDATED :
                    CustomNotificationItem.TYPE_UPDATE_REQUIRED);

            notificationItems.add(0, customNotificationItem);
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(deleteNotificationRunnable);
        super.onDestroy();
    }
}
