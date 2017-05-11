package com.carecloud.carepay.patient.notifications.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationType;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayUtil;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

    /**
     * Constructor
     * @param context context
     * @param notificationItems list of notifications
     * @param callback callback
     */
    public NotificationsAdapter(Context context, List<NotificationItem> notificationItems, SelectNotificationCallback callback){
        this.context = context;
        this.notificationItems = notificationItems;
        this.callback = callback;
    }

    public void setNotificationItems(List<NotificationItem> notificationItems){
        this.notificationItems = notificationItems;
        notifyDataSetChanged();
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notification_list_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        NotificationItem notificationItem = notificationItems.get(position);
        NotificationType notificationType = notificationItem.getPayload().getNotificationType();

        resetViews(holder);
        if(notificationType != null) {
            switch (notificationType) {
                case payment:
                    displayPaymentNotification(holder, notificationItem);
                    break;
                case credit_card:
                    displayCreditCardNotification(holder, notificationItem);
                    break;
                default:
                case appointment:
                    displayAppointmentNotification(holder, notificationItem);
                    break;
            }
        }

        holder.time.setText(getTimeStamp(notificationItem));
    }

    private void displayPaymentNotification(NotificationViewHolder holder, NotificationItem notificationItem){

    }

    private void displayCreditCardNotification(NotificationViewHolder holder, NotificationItem notificationItem){

    }

    private void displayAppointmentNotification(final NotificationViewHolder holder, NotificationItem notificationItem){
        AppointmentDTO appointment = notificationItem.getPayload().getAppointment();

        holder.message.setText(notificationItem.getPayload().getAlertMessage());
        holder.initials.setText(StringUtil.getShortName(appointment.getPayload().getProvider().getName()));
        holder.header.setText("Notification");

        AppointmentDisplayStyle displayStyle = AppointmentDisplayUtil.determineDisplayStyle(appointment);

        switch (displayStyle){
            case MISSED:{
                holder.header.setTextColor(ContextCompat.getColor(context, R.color.remove_red));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_red);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_missed);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                break;
            }
            case PENDING:
            case PENDING_UPCOMING:{
                holder.header.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_green_border);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_upcoming);
                break;
            }
            case CANCELED:
            case CANCELED_UPCOMING:{
                holder.header.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_canceled);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                break;
            }
            default:
                resetViews(holder);
        }

        int size = context.getResources().getDimensionPixelSize(R.dimen.payment_details_dialog_icon_size);
        Picasso.with(context).load(appointment.getPayload().getProvider().getPhoto())
                .resize(size, size)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.image.setVisibility(View.VISIBLE);
                        holder.initials.setVisibility(View.GONE);
                        holder.cellAvatar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.image.setVisibility(View.GONE);
                        holder.initials.setVisibility(View.VISIBLE);
                    }
                });

    }

    private String getTimeStamp(NotificationItem notificationItem){
        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(notificationItem.getMetadata().getCreatedDt());

        return dateUtil.toStringWithFormatMmSlashDdSlashYyyy();
    }

    private void resetViews(NotificationViewHolder holder){
        holder.initials.setVisibility(View.VISIBLE);
        holder.cellAvatar.setVisibility(View.GONE);
        holder.image.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(null);
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
