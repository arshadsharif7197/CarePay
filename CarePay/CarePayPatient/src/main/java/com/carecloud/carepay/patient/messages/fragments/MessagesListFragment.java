package com.carecloud.carepay.patient.messages.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepaylibray.base.BaseFragment;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesListFragment extends BaseFragment {

    View noMessagesLayout;
    View actionButton;

    private MessageNavigationCallback callback;

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

    }

}
