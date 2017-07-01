package com.carecloud.carepay.patient.messages.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.models.MessagingDataModel;
import com.carecloud.carepaylibray.base.BaseFragment;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesListFragment extends BaseFragment {

    private View noMessagesLayout;
    private View actionButton;
    private RecyclerView recyclerView;


    private MessageNavigationCallback callback;
    private MessagingDataModel messagingDataModel;

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
        noMessagesLayout = view.findViewById(R.id.no_messages_layout);
        actionButton = view.findViewById(R.id.fab);

        recyclerView = (RecyclerView) view.findViewById(R.id.messages_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

    }

    private void setAdapters(){

    }

    public void setDisplayDataModel(MessagingDataModel messagingDataModel){
        this.messagingDataModel = messagingDataModel;
    }

}
