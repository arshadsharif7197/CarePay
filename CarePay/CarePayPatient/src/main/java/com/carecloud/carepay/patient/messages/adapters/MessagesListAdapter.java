package com.carecloud.carepay.patient.messages.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingDataModel;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.customcomponents.SwipeViewHolder;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 7/5/17
 */

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.ViewHolder> {

    public interface SelectMessageThreadCallback {
        void onMessageSelected(Messages.Reply thread, String practiceName);

        void undoDeleteMessage(Messages.Reply thread);
    }

    private Context context;
    private List<Messages.Reply> threads;
    private MessagingDataModel messagingDataModel;
    private SelectMessageThreadCallback callback;
    private String userId;
    private List<Messages.Reply> removeThreads = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context            context
     * @param threads            list of threads
     * @param callback           callback
     * @param messagingDataModel
     */
    public MessagesListAdapter(Context context, List<Messages.Reply> threads, SelectMessageThreadCallback callback, String userId, MessagingDataModel messagingDataModel) {
        this.context = context;
        this.threads = threads;
        this.callback = callback;
        this.userId = userId;
        this.messagingDataModel = messagingDataModel;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_messages_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Messages.Reply thread = threads.get(position);

        //reset
        holder.deleteLayout.setVisibility(View.VISIBLE);
        holder.undoButton.setVisibility(View.GONE);
        holder.swipeLayout.setVisibility(View.VISIBLE);

        Messages.Participant provider = digProvider(thread);
        UserPracticeDTO practice = digPractice(provider.getUserId());

        if (provider != null) {
            holder.providerName.setText(provider.getName());
            holder.providerName.setVisibility(View.VISIBLE);
        } else {
            holder.providerName.setVisibility(View.GONE);
        }
        if (practice != null) {
            holder.practiceName.setText(practice.getPracticeName());
            holder.practiceName.setVisibility(View.VISIBLE);
        } else {
            holder.practiceName.setVisibility(View.GONE);
        }
        holder.providerInitials.setText(StringUtil.getShortName(provider.getName()));
        holder.providerTitle.setText(thread.getSubject());

        String dateString = DateUtil.getInstance().setDateRaw(thread.getLastUpdate())
                .shiftDateToGMT().toContextualMessageDate();

        holder.timeStamp.setText(dateString);

        if (removeThreads.contains(thread)) {
            holder.swipeLayout.setVisibility(View.INVISIBLE);
            holder.displayUndoOption();
        }

        holder.swipeLayout.setOnClickListener(view -> {
            holder.unreadCount.setVisibility(View.GONE);
            if (practice != null) {
                callback.onMessageSelected(thread, practice.getPracticeName());
            } else {
                callback.onMessageSelected(thread, "");
            }
            thread.setRead(true);
        });

        holder.undoButton.setOnClickListener(view -> callback.undoDeleteMessage(thread));

        if (!thread.isRead()) {
            holder.unreadCount.setVisibility(View.VISIBLE);
        } else {
            holder.unreadCount.setVisibility(View.GONE);
        }

        if (!StringUtil.isNullOrEmpty(provider.getPhoto())) {
            PicassoHelper.get().loadImage(context, holder.providerImage, holder.providerInitials, provider.getPhoto());
        } else {
            holder.providerImage.setVisibility(View.INVISIBLE);
            holder.providerInitials.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    /**
     * Get a thread by position
     *
     * @param position position
     * @return Thread
     */
    public Messages.Reply getThread(int position) {
        if (threads.size() > position && position > -1) {
            return threads.get(position);
        }
        return null;
    }

    /**
     * set threads and reset recycler
     *
     * @param threads new threads list
     */
    public void setThreads(List<Messages.Reply> threads) {
        this.threads = threads;
        notifyDataSetChanged();
    }

    /**
     * Mark thread as scheduled for Delete
     *
     * @param thread Thread to delete
     */
    public void scheduleMessageRemoval(Messages.Reply thread) {
        removeThreads.add(thread);
    }

    /**
     * Remove Thread from delete queue
     *
     * @param thread Thread to remove from delete queue
     */
    public void clearMessageRemoval(Messages.Reply thread) {
        removeThreads.remove(thread);
        int position = threads.indexOf(thread);
        notifyItemChanged(position);
    }

    /**
     * Finalize removing a thread
     *
     * @param thread thread
     */
    public void finalizeMessageRemoval(Messages.Reply thread) {
        int position = threads.indexOf(thread);
        threads.remove(thread);
        notifyItemRemoved(position);
        removeThreads.remove(thread);
    }


    private Messages.Participant digProvider(Messages.Reply thread) {
        List<Messages.Reply> replies = thread.getReplies();
        if (!replies.isEmpty()) {
            for (Messages.Reply reply : replies) {
                for (Messages.Participant participant : reply.getParticipants()) {
                    if (participant.getType() != null && participant.getType().equals("provider")) {
                        return participant;
                    }
                }
            }
        }

        for (Messages.Participant participant : thread.getParticipants()) {
            if (!participant.getUserId().equals(userId)) {
                return participant;
            }
        }

        return null;
    }

    private UserPracticeDTO digPractice(String providerId) {
        for (ProviderContact providerContact : messagingDataModel.getProviderContacts()) {
            if (providerContact.getId().equals(providerId)) {
                for (UserPracticeDTO userPracticeDTO : messagingDataModel.getUserPractices()) {
                    if (userPracticeDTO.getPracticeId().equals(providerContact.getBusinessEntityId())) {
                        return userPracticeDTO;
                    }
                }
            }
        }
        return null;
    }

    class ViewHolder extends SwipeViewHolder {

        TextView unreadCount;
        ImageView providerImage;
        TextView providerInitials;
        TextView providerName;
        TextView practiceName;
        TextView providerTitle;
        TextView timeStamp;
        View swipeLayout;
        View undoButton;
        View deleteLayout;

        ViewHolder(View itemView) {
            super(itemView);
            unreadCount = itemView.findViewById(R.id.unread_count);
            providerImage = itemView.findViewById(R.id.provider_image);
            providerInitials = itemView.findViewById(R.id.provider_initials);
            providerName = itemView.findViewById(R.id.provider_name);
            practiceName = itemView.findViewById(R.id.practice_name);
            providerTitle = itemView.findViewById(R.id.provider_title);
            timeStamp = itemView.findViewById(R.id.time_stamp);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            undoButton = itemView.findViewById(R.id.undo_button);
            deleteLayout = itemView.findViewById(R.id.delete_message);
        }

        @Override
        public int getSwipeWidth() {
            return swipeLayout.getMeasuredWidth();
        }

        @Override
        public View getSwipeableView() {
            return swipeLayout;
        }

        @Override
        public void displayUndoOption() {
            deleteLayout.setVisibility(View.GONE);
            undoButton.setVisibility(View.VISIBLE);
        }
    }
}
