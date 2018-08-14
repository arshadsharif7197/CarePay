package com.carecloud.carepay.patient.notifications.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationType;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayUtil;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayCustomSpan;
import com.carecloud.carepaylibray.customcomponents.SwipeViewHolder;
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

    public interface SelectNotificationCallback {
        void notificationSelected(NotificationItem notificationItem);

        void undoDeleteNotification(SwipeViewHolder viewHolder);
    }

    private Context context;
    private List<NotificationItem> notificationItems;
    private SelectNotificationCallback callback;
    private List<NotificationItem> removeItems = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context           context
     * @param notificationItems list of notifications
     * @param callback          callback
     */
    public NotificationsAdapter(Context context,
                                List<NotificationItem> notificationItems,
                                SelectNotificationCallback callback) {
        this.context = context;
        this.notificationItems = notificationItems;
        this.callback = callback;
    }

    public void setNotificationItems(List<NotificationItem> notificationItems) {
        this.notificationItems = notificationItems;
        notifyDataSetChanged();
    }

    public void scheduleNotificationRemoval(NotificationItem notificationItem) {
        removeItems.add(notificationItem);
    }

    public void clearRemovedNotification(NotificationItem notificationItem) {
        removeItems.remove(notificationItem);
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
    public void onBindViewHolder(final NotificationViewHolder holder, int position) {
        final NotificationItem notificationItem = notificationItems.get(position);
        NotificationType notificationType = notificationItem.getMetadata().getNotificationType();

        resetViews(holder);
        if (notificationType != null) {
            switch (notificationType) {
                case payments:
                    displayPaymentNotification(holder, notificationItem);
                    break;
                case credit_card:
                    displayCreditCardNotification(holder, notificationItem);
                    break;
                case pending_forms:
                    displayPendingFormNotification(holder, notificationItem);
                    break;
                default:
                case appointment:
                    displayAppointmentNotification(holder, notificationItem);
                    break;
            }
        }

        holder.time.setText(getTimeStamp(notificationItem));

        if (removeItems.contains(notificationItem)) {
            holder.notificationItemView.setVisibility(View.INVISIBLE);
            holder.displayUndoOption();
        }

        holder.undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.undoDeleteNotification(holder);
            }
        });

        holder.getSwipeableView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.notificationSelected(notificationItem);
            }
        });

    }

    private void displayPendingFormNotification(NotificationViewHolder holder, NotificationItem notificationItem) {
        holder.initials.setText(StringUtil.getShortName(notificationItem.getPayload().getPracticeName()));
        holder.initials.setTextColor(ContextCompat.getColor(context, R.color.lightning_yellow));
        holder.initials.setBackgroundResource(R.drawable.round_list_tv_yellow_border);

        String headerText = Label.getLabel("consentForms.providersList.item.label.pendingFormCount");
        if (notificationItem.getPayload().getPendingForms() > 1) {
            headerText = String.format(Label.getLabel("consentForms.providersList.item.label.pendingFormsCount"),
                    notificationItem.getPayload().getPendingForms());
        }
        holder.header.setText(headerText);
        holder.header.setTextColor(ContextCompat.getColor(context, R.color.lightning_yellow));

        String practiceName = notificationItem.getPayload().getPracticeName();
        holder.message.setTextColor(ContextCompat.getColor(context, R.color.charcoal));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(String
                .format(Label.getLabel("consentForms.notification.message.label.pendingFormNotification")
                        , practiceName));
        stringBuilder.setSpan(new CarePayCustomSpan(context,
                        CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD), 0, practiceName.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.message.setText(stringBuilder);
    }

    private void displayPaymentNotification(NotificationViewHolder holder, NotificationItem notificationItem) {
        int headerTextColor;
        String headerText;
        String messageText;
        int imageBackground;
        int imageIcon;
        if (notificationItem.getMetadata().getEvent().getPayload().isPaymentSuccessful()) {
            headerTextColor = R.color.emerald;
            imageBackground = R.drawable.round_list_tv_green;
            imageIcon = R.drawable.icn_payment_confirm_check;
            if (notificationItem.getMetadata().getEvent().getPayload().getScheduledPaymentExecution() == null) {
                headerText = Label.getLabel("consentForms.notification.regularPayment.header.successfulPaymentNotification");
                messageText = Label.getLabel("consentForms.notification.regularPayment.message.successfulPaymentNotification");
            } else {
                headerText = Label.getLabel("consentForms.notification.scheduledPayment.header.successfulPaymentNotification");
                messageText = Label.getLabel("consentForms.notification.scheduledPayment.message.successfulPaymentNotification");
            }

        } else {
            headerTextColor = R.color.remove_red;
            imageBackground = R.drawable.round_list_tv_red;
            imageIcon = R.drawable.icn_close;
            if (notificationItem.getMetadata().getEvent().getPayload().getScheduledPaymentExecution() == null) {
                headerText = Label.getLabel("consentForms.notification.regularPayment.header.successfulPaymentNotification");
                messageText = Label.getLabel("consentForms.notification.regularPayment.message.successfulPaymentNotification");
            } else {
                headerText = Label.getLabel("consentForms.notification.scheduledPayment.header.failedPaymentNotification");
                messageText = Label.getLabel("consentForms.notification.scheduledPayment.message.failedPaymentNotification");
            }
        }
        holder.image.setVisibility(View.VISIBLE);
        holder.initials.setBackgroundResource(imageBackground);
        holder.initials.setText(" ");
        holder.image.setImageDrawable(ContextCompat.getDrawable(context, imageIcon));


        holder.header.setText(headerText);
        holder.header.setTextColor(ContextCompat.getColor(context, headerTextColor));

        String practiceName = "";//notificationItem.getPayload().getPracticeName();
        holder.message.setTextColor(ContextCompat.getColor(context, R.color.charcoal));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(String
                .format(messageText, practiceName));
        stringBuilder.setSpan(new CarePayCustomSpan(context, CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD),
                stringBuilder.length() - practiceName.length(), stringBuilder.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.message.setText(stringBuilder);
    }

    private void displayCreditCardNotification(NotificationViewHolder holder, NotificationItem notificationItem) {

    }

    private void displayAppointmentNotification(final NotificationViewHolder holder,
                                                NotificationItem notificationItem) {
        AppointmentDTO appointment = notificationItem.getPayload().getAppointment();

        ProviderDTO provider = appointment.getPayload().getProvider();
        String appointmentDate = DateUtil.getInstance().setDateRaw(appointment.getPayload()
                .getStartTime()).toStringWithFormatMmSlashDdSlashYyyy();

        holder.message.setText(notificationItem.getPayload().getAlertMessage());
        holder.initials.setText(StringUtil.getShortName(provider.getName()));
        holder.header.setText("Notification");

        AppointmentDisplayStyle displayStyle = AppointmentDisplayUtil.determineDisplayStyle(appointment);
        notificationItem.getPayload().getAppointment().getPayload().setDisplayStyle(displayStyle);

        switch (displayStyle) {
            case MISSED: {
                holder.header.setText(Label.getLabel("notification_missed_appointment_title"));
                holder.header.setTextColor(ContextCompat.getColor(context, R.color.remove_red));
                setFormattedMessage(holder.message, Label.getLabel("notification_missed_appointment_message"),
                        appointmentDate, provider.getName());
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_red);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_missed);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                break;
            }
            case PENDING:
            case PENDING_UPCOMING: {
                holder.header.setText(Label.getLabel("notification_confirmed_appointment_title"));
                holder.header.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                setFormattedMessage(holder.message, Label.getLabel("notification_confirmed_appointment_message"),
                        appointmentDate, provider.getName());
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_green_border);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_upcoming);
                break;
            }
            case CANCELED:
            case CANCELED_UPCOMING: {
                holder.header.setText(Label.getLabel("notification_cancelled_appointment_title"));
                holder.header.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
                setFormattedMessage(holder.message, Label.getLabel("notification_cancelled_appointment_message"),
                        appointmentDate, provider.getName());
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_canceled);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                break;
            }
            case CHECKED_IN: {
                holder.header.setText(Label.getLabel("notification_checked-in_appointment_title"));
                holder.header.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                setFormattedMessage(holder.message, Label.getLabel("notification_checked-in_appointment_message"),
                        appointmentDate, provider.getName());
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_green);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_checked_in);
                break;
            }
            case CHECKED_OUT: {
                holder.header.setText(Label.getLabel("notification_checked-out_appointment_title"));
                holder.header.setTextColor(ContextCompat.getColor(context, R.color.grayRound));
                setFormattedMessage(holder.message, Label.getLabel("notification_checked-out_appointment_message"),
                        appointmentDate, provider.getName());
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.initials.setBackgroundResource(R.drawable.round_tv);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_checked_out);
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

    private String getFormattedMessge(String messageBase, String... fields) {
        if (!messageBase.contains("%s")) {
            return messageBase;
        }
        return String.format(messageBase, fields);
    }

    private void setFormattedMessage(TextView textView, String messageBase, String... fields) {
        String formattedMessage = getFormattedMessge(messageBase, fields);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(formattedMessage);

        for (String field : fields) {
            if (field != null) {
                int start = formattedMessage.indexOf(field);
                if (start > -1) {
                    stringBuilder.setSpan(new CarePayCustomSpan(context,
                                    CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD), start, start + field.length(),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }

        textView.setText(stringBuilder);
    }

    private String getTimeStamp(NotificationItem notificationItem) {
        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(notificationItem.getMetadata().getCreatedDt());

        return dateUtil.toStringWithFormatMmSlashDdSlashYyyy();
    }

    private void resetViews(NotificationViewHolder holder) {
        holder.header.setTextColor(ContextCompat.getColor(context, R.color.textview_default_textcolor));
        holder.initials.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
        holder.initials.setBackgroundResource(R.drawable.round_list_tv);
        holder.initials.setVisibility(View.VISIBLE);
        holder.cellAvatar.setVisibility(View.GONE);
        holder.image.setVisibility(View.GONE);
        holder.deleteButton.setVisibility(View.VISIBLE);
        holder.undoButton.setVisibility(View.GONE);
        holder.notificationItemView.setVisibility(View.VISIBLE);
    }

    class NotificationViewHolder extends SwipeViewHolder {
        TextView initials;
        ImageView cellAvatar;
        ImageView image;
        TextView header;
        TextView message;
        TextView time;

        View deleteButton;
        View notificationItemView;
        View undoButton;

        NotificationViewHolder(View itemView) {
            super(itemView);
            initials = (TextView) itemView.findViewById(R.id.avatarTextView);
            cellAvatar = (ImageView) itemView.findViewById(R.id.cellAvatarImageView);
            image = (ImageView) itemView.findViewById(R.id.providerPicImageView);
            header = (TextView) itemView.findViewById(R.id.notification_header);
            message = (TextView) itemView.findViewById(R.id.notification_message);
            time = (TextView) itemView.findViewById(R.id.notification_time);
            deleteButton = itemView.findViewById(R.id.delete_notification);
            notificationItemView = itemView.findViewById(R.id.notification_item_layout);
            undoButton = itemView.findViewById(R.id.undo_button);
        }

        @Override
        public int getSwipeWidth() {
            return deleteButton.getMeasuredWidth();
        }

        @Override
        public View getSwipeableView() {
            return notificationItemView;
        }

        @Override
        public void displayUndoOption() {
            undoButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
        }
    }
}
