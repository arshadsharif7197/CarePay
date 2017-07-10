package com.carecloud.carepay.patient.messages.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 7/5/17
 */

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.ViewHolder> {

    public interface SelectMessageThreadCallback{
        void onMessageSelected(Messages.Reply thread);
    }

    private Context context;
    private List<Messages.Reply> threads = new ArrayList<>();
    private SelectMessageThreadCallback callback;
    private String userId;

    /**
     * Constructor
     * @param context context
     * @param threads list of threads
     * @param callback callback
     */
    public MessagesListAdapter(Context context, List<Messages.Reply> threads, SelectMessageThreadCallback callback, String userId){
        this.context = context;
        this.threads = threads;
        this.callback = callback;
        this.userId = userId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_messages_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Messages.Reply thread = threads.get(position);

        String providerName = digProvider(thread);
        holder.providerName.setText(providerName);
        holder.providerInitials.setText(StringUtil.getShortName(providerName));
        holder.providerTitle.setText(thread.getSubject());

        String dateString = DateUtil.getInstance().setDateRaw(thread.getLastUpdate()).shiftDateToGMT().toContextualMessageDate();

        holder.timeStamp.setText(dateString);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onMessageSelected(thread);
            }
        });
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    /**
     * set threads and reset recycler
     * @param threads new threads list
     */
    public void setThreads(List<Messages.Reply> threads){
        this.threads = threads;
        notifyDataSetChanged();
    }

    /**
     * add threads to recycler
     * @param threads add threads
     */
    public void appendThreads(List<Messages.Reply> threads){
        this.threads.addAll(threads);
        notifyDataSetChanged();
    }

    private String digProvider(Messages.Reply thread){
        List<Messages.Reply> replies = thread.getReplies();
        if(!replies.isEmpty()){
            for(Messages.Reply reply : replies){
                for(Messages.Participant participant : reply.getParticipants()){
                    if(participant.getType() != null && participant.getType().equals("provider")){
                        return participant.getName();
                    }
                }
            }
        }else{
            for(Messages.Participant participant : thread.getParticipants()){
                if(!participant.getUserId().equals(userId)){
                    return participant.getName();
                }
            }
        }
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView unreadCount;
        ImageView providerImage;
        TextView providerInitials;
        TextView providerName;
        TextView providerTitle;
        TextView timeStamp;

        ViewHolder(View itemView) {
            super(itemView);
            unreadCount = (TextView) itemView.findViewById(R.id.unread_count);
            providerImage = (ImageView) itemView.findViewById(R.id.provider_image);
            providerInitials = (TextView) itemView.findViewById(R.id.provider_initials);
            providerName = (TextView) itemView.findViewById(R.id.provider_name);
            providerTitle = (TextView) itemView.findViewById(R.id.provider_title);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
        }
    }
}
