package com.carecloud.carepay.patient.messages.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.adapters.MessagesListAdapter;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingDataModel;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.customcomponents.SwipeViewHolder;
import com.carecloud.carepaylibray.utils.SwipeHelper;

import java.util.List;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesListFragment extends BaseFragment implements MessagesListAdapter.SelectMessageThreadCallback, SwipeHelper.SwipeHelperListener {

    private static final int BOTTOM_ROW_OFFSET = 2;
    private static final long MESSAGE_DELETE_DELAY = 1000 * 5;

    private SwipeRefreshLayout refreshLayoutView;
    private View noMessagesLayout;
    private View actionButton;
    private RecyclerView recyclerView;


    private MessageNavigationCallback callback;
    private MessagingDataModel messagingDataModel;

    private boolean refreshing = true;
    private boolean isPaging = false;

    private Handler handler;
    private boolean dontLoad = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (MessageNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement MessageNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        handler = new Handler();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_messages_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        refreshLayoutView = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        refreshLayoutView.setOnRefreshListener(onRefreshListener);

        noMessagesLayout = view.findViewById(R.id.no_messages_layout);
        actionButton = view.findViewById(R.id.fab);
        actionButton.setOnClickListener(newMessageAction);

        recyclerView = (RecyclerView) view.findViewById(R.id.messages_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(scrollListener);

        SwipeHelper swipeHelper = new SwipeHelper(this);
        ItemTouchHelper notificationsTouchHelper = new ItemTouchHelper(swipeHelper);
        notificationsTouchHelper.attachToRecyclerView(recyclerView);

        View butonNewMessage = view.findViewById(R.id.new_message_button);
        butonNewMessage.setOnClickListener(newMessageAction);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshing = true;
        if (!dontLoad) {
            refreshList(true);
            dontLoad = true;
        } else {
            dontLoad = false;
        }

    }

    private void setAdapters() {
        List<Messages.Reply> threads = messagingDataModel.getMessages().getData();
        MessagesListAdapter adapter = (MessagesListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setThreads(threads);
        } else {
            adapter = new MessagesListAdapter(getContext(), threads, this, callback.getUserId());
            recyclerView.setAdapter(adapter);
        }

        if (threads.isEmpty()) {
            noMessagesLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
            refreshLayoutView.setEnabled(false);
        } else {
            noMessagesLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            actionButton.setVisibility(View.VISIBLE);
            refreshLayoutView.setEnabled(true);
        }
    }

    private void refreshList(boolean showShimmerEffect) {
        callback.getMessageThreads(0, 0, showShimmerEffect);
    }

    /**
     * Update or set the display model
     *
     * @param messagingDataModel updated model
     */
    public void updateDisplayDataModel(MessagingDataModel messagingDataModel) {
        if (this.messagingDataModel == null || refreshing) {
            this.messagingDataModel = messagingDataModel;
        } else {
            this.messagingDataModel.getMessages().setPaging(messagingDataModel.getMessages().getPaging());
            this.messagingDataModel.getMessages().getData().addAll(messagingDataModel.getMessages().getData());
        }

        setAdapters();
        isPaging = false;
        refreshing = false;
        refreshLayoutView.setRefreshing(refreshing);
    }

    private boolean hasMorePages() {
        Paging paging = messagingDataModel.getMessages().getPaging();
        return paging.getCurrentPage() != paging.getTotalPages();
    }

    @Override
    public void onMessageSelected(Messages.Reply thread) {
        callback.displayThreadMessages(thread);
    }

    @Override
    public void undoDeleteMessage(Messages.Reply thread) {
        deleteMessageRunnable.setDeleteThread(null);
        handler.removeCallbacks(deleteMessageRunnable);
        MessagesListAdapter messagesListAdapter = (MessagesListAdapter) recyclerView.getAdapter();
        messagesListAdapter.clearMessageRemoval(thread);

    }

    @Override
    public void startNewSwipe() {
        deleteMessageRunnable.run();
    }

    @Override
    public void viewSwipeCompleted(SwipeViewHolder holder) {
        MessagesListAdapter messagesListAdapter = (MessagesListAdapter) recyclerView.getAdapter();
        Messages.Reply thread = messagesListAdapter.getThread(holder.getAdapterPosition());
        messagesListAdapter.scheduleMessageRemoval(thread);
        messagesListAdapter.notifyItemChanged(holder.getAdapterPosition());
        deleteMessageRunnable.setDeleteThread(thread);
        handler.postDelayed(deleteMessageRunnable, MESSAGE_DELETE_DELAY);

    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshing = true;
            refreshLayoutView.setRefreshing(refreshing);
            refreshList(false);
        }
    };

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            deleteMessageRunnable.run();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (hasMorePages()) {
                int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (last > recyclerView.getAdapter().getItemCount() - BOTTOM_ROW_OFFSET && !isPaging) {
                    Paging paging = messagingDataModel.getMessages().getPaging();
                    callback.getMessageThreads(paging.getCurrentPage() + 1, paging.getResultsPerPage(), false);
                    isPaging = true;
                }
            }

        }
    };

    private View.OnClickListener newMessageAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.startNewThread();
        }
    };

    private DeleteMessageRunnable deleteMessageRunnable = new DeleteMessageRunnable();


    private class DeleteMessageRunnable implements Runnable {
        private Messages.Reply deleteThread;

        void setDeleteThread(Messages.Reply deleteThread) {
            this.deleteThread = deleteThread;
        }

        @Override
        public void run() {
            if (deleteThread != null) {
                MessagesListAdapter messagesListAdapter = (MessagesListAdapter) recyclerView.getAdapter();
                messagesListAdapter.finalizeMessageRemoval(deleteThread);
                callback.deleteMessageThread(deleteThread);
                //reset delete thread
                deleteThread = null;
                Paging paging = messagingDataModel.getMessages().getPaging();
                if (messagesListAdapter.getItemCount() == 0) {
                    setAdapters();
                } else if (paging.getResultsPerPage() % messagesListAdapter.getItemCount() < BOTTOM_ROW_OFFSET &&
                        hasMorePages()) {
                    callback.getMessageThreads(paging.getCurrentPage() + 1, paging.getResultsPerPage(), false);
                }
            }
        }
    }
}
