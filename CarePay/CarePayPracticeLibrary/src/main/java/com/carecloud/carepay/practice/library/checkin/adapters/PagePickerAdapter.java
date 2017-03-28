package com.carecloud.carepay.practice.library.checkin.adapters;

import android.content.Context;
import android.view.View;

import com.carecloud.carepay.practice.library.payments.adapter.PopupPickerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/28/17.
 */

public class PagePickerAdapter extends PopupPickerAdapter {

    public interface PagePickerCallback{
        void sendMessage(String message);
    }


    private List<String> messageList = new ArrayList<>();
    private PagePickerCallback callback;

    /**
     * Constructor
     * @param context context
     * @param messageList list of page message strings
     * @param callback callback to send page
     */
    public PagePickerAdapter(Context context, List<String> messageList, PagePickerCallback callback){
        super(context);
        this.messageList = messageList;
        this.callback = callback;
    }

    @Override
    public void onBindViewHolder(PopupListViewHolder holder, int position) {
        final String message = messageList.get(position);
        holder.getName().setText(message);
        holder.getName().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.sendMessage(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
