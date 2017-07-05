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

public class MessagesConversationAdapter extends RecyclerView.Adapter<MessagesConversationAdapter.ViewHolder> {

    private static final int TYPE_SENT = 111;
    private static final int TYPE_RECEIVED = 222;

    private Context context;
    private List<Messages.Reply> messages = new ArrayList<>();
    private String userId;

    /**
     * constuctor
     * @param context context
     * @param messages messages list
     * @param userId current user id
     */
    public MessagesConversationAdapter(Context context, List<Messages.Reply> messages, String userId){
        this.context = context;
        this.messages = messages;
        this.userId = userId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if(viewType == TYPE_SENT){
            view = inflater.inflate(R.layout.item_messages_sent, parent, false);
        }else{
            view = inflater.inflate(R.layout.item_messages_received, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Messages.Reply message = messages.get(position);

        holder.participantInitials.setText(StringUtil.getShortName(message.getAuthor().getName()));
        holder.participantName.setText(message.getAuthor().getName());

        String time = DateUtil.getInstance().setDateRaw(message.getLastUpdate()).getTime12Hour();
        holder.timeStamp.setText(time);

        holder.messageText.setText(message.getBody());
    }

    @Override
    public int getItemViewType(int position){
        Messages.Reply message = messages.get(position);
        if(message.getAuthor().getUserId().equals(userId)){
            return TYPE_SENT;
        }
        return TYPE_RECEIVED;
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * set new messages list
     * @param messages new messages list
     */
    public void setMessages(List<Messages.Reply> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeHeader;
        ImageView participantImage;
        TextView participantInitials;
        View metaView;
        TextView participantName;
        TextView timeStamp;
        TextView messageText;

        ViewHolder(View itemView) {
            super(itemView);
            timeHeader = (TextView) itemView.findViewById(R.id.time_header);
            participantImage = (ImageView) itemView.findViewById(R.id.participant_image);
            participantInitials = (TextView) itemView.findViewById(R.id.participant_initials);
            metaView = itemView.findViewById(R.id.message_metadata);
            participantName = (TextView) itemView.findViewById(R.id.participant_name);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            messageText = (TextView) itemView.findViewById(R.id.message_text);

        }
    }
}
