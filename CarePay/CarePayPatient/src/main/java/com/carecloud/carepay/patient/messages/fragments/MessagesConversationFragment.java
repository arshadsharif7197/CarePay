package com.carecloud.carepay.patient.messages.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.adapters.MessagesConversationAdapter;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 7/5/17
 */

public class MessagesConversationFragment extends BaseFragment {

    private MessageNavigationCallback callback;
    private Messages.Reply thread;
    private List<Messages.Reply> messages = new ArrayList<>();

    private SwipeRefreshLayout refreshLayoutView;
    private RecyclerView recyclerView;

    private boolean refreshing = true;

    /**
     * Get new instance of MessagesConversationFragment
     * @param thread base thread
     * @return new MessagesConversationFragment
     */
    public static MessagesConversationFragment newInstance(Messages.Reply thread) {

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, thread);

        MessagesConversationFragment fragment = new MessagesConversationFragment();
        fragment.setArguments(args);
        return fragment;
    }

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

        Bundle args = getArguments();
        if(args != null){
            thread =  DtoHelper.getConvertedDTO(Messages.Reply.class, args);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_conversation_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initToolbar(view);

        refreshLayoutView = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        refreshLayoutView.setOnRefreshListener(onRefreshListener);

        recyclerView = (RecyclerView) view.findViewById(R.id.messages_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshing = true;
        refreshThreadMessages();
    }

    private void initToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(thread.getSubject());

        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setAdapter(){
        MessagesConversationAdapter adapter = (MessagesConversationAdapter) recyclerView.getAdapter();
        if(adapter != null){
            adapter.setMessages(messages);
        }else{
            adapter = new MessagesConversationAdapter(getContext(), messages, thread.getAuthor().getUserId());
            recyclerView.setAdapter(adapter);
        }

    }

    public void updateThreadMessages(Messages.Reply thread){
        if(messages.isEmpty() || refreshing){
            messages = getMessagesList(thread);
        }else{
            messages.addAll(thread.getReplies());
        }

        setAdapter();

        refreshing = false;
        refreshLayoutView.setRefreshing(refreshing);
    }

    private List<Messages.Reply> getMessagesList(Messages.Reply thread){
        List<Messages.Reply> messages = thread.getReplies();
        messages.add(0, thread);//need to add the OP to the list of replies because its not included
        return messages;
    }

    private void refreshThreadMessages(){
        callback.getThreadMessages(thread);
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshing = true;
            refreshLayoutView.setRefreshing(refreshing);
            refreshThreadMessages();
        }
    };

}
