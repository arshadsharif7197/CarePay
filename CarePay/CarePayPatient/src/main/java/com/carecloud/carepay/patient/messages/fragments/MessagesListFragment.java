package com.carecloud.carepay.patient.messages.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.adapters.MessagesListAdapter;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingDataModel;
import com.carecloud.carepay.patient.messages.models.Paging;
import com.carecloud.carepaylibray.base.BaseFragment;

import java.util.List;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesListFragment extends BaseFragment implements MessagesListAdapter.SelectMessageThreadCallback {

    private static int BOTTOM_ROW_OFFSET = 2;

    private SwipeRefreshLayout refreshLayoutView;
    private View noMessagesLayout;
    private View actionButton;
    private RecyclerView recyclerView;


    private MessageNavigationCallback callback;
    private MessagingDataModel messagingDataModel;

    private boolean refreshing = true;
    private boolean isPaging = false;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (MessageNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement MessageNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_messages_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        refreshLayoutView = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        refreshLayoutView.setOnRefreshListener(onRefreshListener);

        noMessagesLayout = view.findViewById(R.id.no_messages_layout);
        actionButton = view.findViewById(R.id.fab);
        actionButton.setOnClickListener(newMessageAction);

        recyclerView = (RecyclerView) view.findViewById(R.id.messages_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(scrollListener);

        View butonNewMessage = view.findViewById(R.id.new_message_button);
        butonNewMessage.setOnClickListener(newMessageAction);
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshing = true;
        refreshList();
    }

    private void setAdapters(){
        List<Messages.Reply> threads = messagingDataModel.getMessages().getData();
        MessagesListAdapter adapter = (MessagesListAdapter) recyclerView.getAdapter();
        if(adapter != null){
            if(refreshing){
                adapter.setThreads(threads);
            }else {
                adapter.appendThreads(threads);
            }
        }else{
            adapter = new MessagesListAdapter(getContext(), threads, this, callback.getUserId());
            recyclerView.setAdapter(adapter);
        }

        if(threads.isEmpty()){
            noMessagesLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
            refreshLayoutView.setEnabled(false);
        }else{
            noMessagesLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            actionButton.setVisibility(View.VISIBLE);
            refreshLayoutView.setEnabled(true);
        }
    }

    private void refreshList(){
        callback.getMessageThreads(0, 0);
    }

    /**
     * Update or set the display model
     * @param messagingDataModel updated model
     */
    public void updateDisplayDataModel(MessagingDataModel messagingDataModel){
        if(this.messagingDataModel == null || refreshing) {
            this.messagingDataModel = messagingDataModel;
        }else{
            this.messagingDataModel.getMessages().setPaging(messagingDataModel.getMessages().getPaging());
            this.messagingDataModel.getMessages().getData().addAll(messagingDataModel.getMessages().getData());
        }

        setAdapters();
        isPaging = false;
        refreshing = false;
        refreshLayoutView.setRefreshing(refreshing);
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshing = true;
            refreshLayoutView.setRefreshing(refreshing);
            refreshList();
        }
    };

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(hasMorePages()) {
                int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (last > recyclerView.getAdapter().getItemCount() - BOTTOM_ROW_OFFSET && !isPaging){
                    Paging paging = messagingDataModel.getMessages().getPaging();
                    callback.getMessageThreads(paging.getCurrentPage()+1, paging.getResultsPerPage());
                    isPaging = true;
                }
            }

        }
    };

    @Override
    public void onMessageSelected(Messages.Reply thread) {
        callback.displayThreadMessages(thread);
    }

    private View.OnClickListener newMessageAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.startNewThread();
        }
    };

    private boolean hasMorePages(){
        Paging paging = messagingDataModel.getMessages().getPaging();
        return paging.getCurrentPage() != paging.getTotalPages();
    }
}
