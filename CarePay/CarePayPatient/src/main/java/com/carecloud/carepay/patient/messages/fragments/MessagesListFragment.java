package com.carecloud.carepay.patient.messages.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.MessagesViewModel;
import com.carecloud.carepay.patient.messages.adapters.MessagesListAdapter;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingModelDto;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.customcomponents.SwipeViewHolder;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.utils.SwipeHelper;

import java.util.List;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesListFragment extends BaseFragment
        implements MessagesListAdapter.SelectMessageThreadCallback, SwipeHelper.SwipeHelperListener {

    private static final int BOTTOM_ROW_OFFSET = 2;
    private static final long MESSAGE_DELETE_DELAY = 1000 * 5;

    private SwipeRefreshLayout refreshLayoutView;
    private View noMessagesLayout;
    private View actionButton;
    private RecyclerView recyclerView;
    private TextView noMessagesDescription;
    private View butonNewMessage;
    private TextView noMessagesTitle;

    private MessageNavigationCallback callback;
    private MessagingModelDto messagingDto;

    private boolean refreshing = true;
    private boolean isPaging = false;

    private Handler handler;
    private MessagesViewModel viewModel;

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
        viewModel = ViewModelProviders.of(getActivity()).get(MessagesViewModel.class);
        messagingDto = viewModel.getMessagesDto().getValue();
        viewModel.getThreadsObservable().observe(this, this::updateDisplayDataModel);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_messages_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        refreshLayoutView = view.findViewById(R.id.swipeRefreshLayout);
        refreshLayoutView.setOnRefreshListener(onRefreshListener);

        noMessagesLayout = view.findViewById(R.id.no_messages_layout);
        actionButton = view.findViewById(R.id.fab);
        actionButton.setOnClickListener(v -> showProvidersFragment());
        noMessagesDescription = view.findViewById(R.id.no_messages_description);
        noMessagesTitle = view.findViewById(R.id.no_messages_title);

        recyclerView = view.findViewById(R.id.messages_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(scrollListener);

        SwipeHelper swipeHelper = new SwipeHelper(this);
        ItemTouchHelper notificationsTouchHelper = new ItemTouchHelper(swipeHelper);
        notificationsTouchHelper.attachToRecyclerView(recyclerView);

        butonNewMessage = view.findViewById(R.id.new_message_button);
        butonNewMessage.setOnClickListener(v -> showProvidersFragment());

        refreshing = true;
        updateDisplayDataModel(messagingDto);
    }

    private void showProvidersFragment() {
        callback.addFragment(MessagesProvidersFragment.newInstance(), true);
    }

    public void refreshListMessages() {
        refreshing = true;
        viewModel.getThreads(0, 0);
    }

    private void setAdapters() {
        List<Messages.Reply> threads = messagingDto.getPayload().getMessages().getData();
        MessagesListAdapter adapter = (MessagesListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setThreads(threads);
        } else {
            adapter = new MessagesListAdapter(getContext(), threads, this,
                    messagingDto.getPayload().getInbox().getUserId());
            recyclerView.setAdapter(adapter);
        }
        Profile delegateUser = messagingDto.getPayload().getDelegate();
        if (!threads.isEmpty()) {
            noMessagesLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            actionButton.setVisibility(View.VISIBLE);
            refreshLayoutView.setEnabled(true);
        } else if (delegateUser != null && !callback.canSendProvidersMessages()) {
            noMessagesLayout.setVisibility(View.VISIBLE);
            noMessagesDescription.setVisibility(View.GONE);
            noMessagesTitle.setText(Label.getLabel("patient.delegation.delegates.permissions.label.noPermission"));
            butonNewMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
            refreshLayoutView.setEnabled(false);
        } else {
            noMessagesLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
            refreshLayoutView.setEnabled(false);
        }
    }

    /**
     * Update or set the display model
     *
     * @param messagingDataModel updated model
     */
    private void updateDisplayDataModel(MessagingModelDto messagingDataModel) {
        if (this.messagingDto == null || refreshing) {
            this.messagingDto = messagingDataModel;
        } else {
            this.messagingDto.getPayload().getMessages().setPaging(messagingDataModel
                    .getPayload().getMessages().getPaging());
            this.messagingDto.getPayload().getMessages().getData().addAll(messagingDataModel
                    .getPayload().getMessages().getData());
        }

        setAdapters();
        isPaging = false;
        refreshing = false;
        refreshLayoutView.setRefreshing(refreshing);
    }

    private boolean hasMorePages() {
        Paging paging = messagingDto.getPayload().getMessages().getPaging();
        return paging.getCurrentPage() != paging.getTotalPages();
    }

    @Override
    public void onMessageSelected(Messages.Reply thread) {
        callback.displayThreadMessages(thread, false);
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
            viewModel.getThreads(0, 0);
        }
    };

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            deleteMessageRunnable.run();
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (hasMorePages()) {
                int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (last > recyclerView.getAdapter().getItemCount() - BOTTOM_ROW_OFFSET && !isPaging) {
                    Paging paging = messagingDto.getPayload().getMessages().getPaging();
                    viewModel.getThreads(paging.getCurrentPage() + 1, paging.getResultsPerPage());
                    isPaging = true;
                }
            }

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
                viewModel.deleteMessageThread(deleteThread).observe(MessagesListFragment.this,
                        messagingModelDto -> {
                            //reset delete thread
                            deleteThread = null;
                            Paging paging = messagingDto.getPayload().getMessages().getPaging();
                            if (messagesListAdapter.getItemCount() == 0) {
                                setAdapters();
                            } else if (paging.getResultsPerPage() % messagesListAdapter.getItemCount() < BOTTOM_ROW_OFFSET
                                    && hasMorePages()) {
                                viewModel.getThreads(paging.getCurrentPage() + 1, paging.getResultsPerPage());
                            }
                        });

            }
        }
    }
}
