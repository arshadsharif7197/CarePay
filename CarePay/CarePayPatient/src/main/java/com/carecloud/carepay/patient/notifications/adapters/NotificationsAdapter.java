package com.carecloud.carepay.patient.notifications.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 5/11/17.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    public interface SelectNotificationCallback{
        void notificationSelected(NotificationItem notificationItem);
    }

    private Context context;
    private List<NotificationItem> notificationItems = new ArrayList<>();
    private SelectNotificationCallback callback;

    public NotificationsAdapter(Context context, List<NotificationItem> notificationItems, SelectNotificationCallback callback){
        this.context = context;
        this.notificationItems = notificationItems;
        this.callback = callback;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notification_list_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        NotificationItem notificationItem = notificationItems.get(position);
        NotificationType notificationType = notificationItem.getPayload().getNotificationType();
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder{
        TextView initials;
        ImageView cellAvatar;
        ImageView image;
        TextView header;
        TextView message;
        TextView time;

        NotificationViewHolder(View itemView) {
            super(itemView);
            initials = (TextView) itemView.findViewById(R.id.avatarTextView);
            cellAvatar = (ImageView) itemView.findViewById(R.id.cellAvatarImageView);
            image = (ImageView) itemView.findViewById(R.id.providerPicImageView);
            header = (TextView) itemView.findViewById(R.id.notification_header);
            message = (TextView) itemView.findViewById(R.id.notification_message);
            time = (TextView) itemView.findViewById(R.id.notification_time);
        }
    }
}
