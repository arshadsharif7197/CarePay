package com.carecloud.carepay.patient.appointments.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayUtil;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pjohnson on 19/10/18.
 */
public abstract class BaseAppointmentAdapter extends RecyclerView.Adapter<BaseAppointmentAdapter.ViewHolder> {

    protected static final int VIEW_TYPE_HEADER = 1;
    protected static final int VIEW_TYPE_APPOINTMENT = 2;
    private static final int VIEW_TYPE_LOADING = 3;
    protected Context context;
    protected List<AppointmentDTO> sortedAppointments = new ArrayList<>();
    protected List<UserPracticeDTO> userPracticeDTOs;
    protected Map<String, Set<String>> enabledPracticeLocations;
    protected SelectAppointmentCallback callback;


    @Override
    public int getItemViewType(int position) {
        if (position >= sortedAppointments.size()) {
            return VIEW_TYPE_LOADING;
        }
        AppointmentDTO appointmentDTO = sortedAppointments.get(position);
        if (appointmentDTO.getPayload().getDisplayStyle() == AppointmentDisplayStyle.HEADER) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_APPOINTMENT;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = inflater.inflate(R.layout.item_appointment_list_header, parent, false);
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = inflater.inflate(R.layout.item_loading, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_appointment, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return sortedAppointments.size();
    }


    protected void cleanupViews(ViewHolder holder) {
        holder.checkOutButton.setVisibility(View.GONE);
        holder.checkedOutLabel.setVisibility(View.GONE);
        holder.todayTimeLayout.setVisibility(View.GONE);
        holder.upcomingDateLayout.setVisibility(View.GONE);
        holder.todayTimeMessage.setVisibility(View.GONE);
        holder.cellAvatar.setVisibility(View.GONE);
        holder.initials.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(null);//need to remove this for header just in case
        holder.videoVisitIndicator.setVisibility(View.GONE);
    }

    protected void bindView(final ViewHolder holder, AppointmentsPayloadDTO appointmentsPayload,
                            boolean shouldShowCheckoutButton) {
        //Appointment
        ProviderDTO provider = appointmentsPayload.getProvider();
        if (!StringUtil.isNullOrEmpty(provider.getName())) {
            holder.initials.setText(StringUtil.getShortName(provider.getName()));
        }
        holder.doctorName.setText(StringUtil.getLabelForView(provider.getName()));
        String speciality=provider.getSpecialty().getName();
        if (speciality==null||speciality.equalsIgnoreCase("Not Defined")){
            holder.doctorType.setText("");
        }else {
            holder.doctorType.setText(speciality);
        }
        //holder.doctorType.setText(StringUtil.getLabelForView(provider.getSpecialty().getName()));
        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentsPayload.getStartTime());
        AppointmentDisplayStyle style = getDisplayStyle(appointmentsPayload);

        switch (style) {
            case CHECKED_IN: {
                if (appointmentsPayload.getVisitType().hasVideoOption() && !shouldShowCheckoutButton) {
                    if (appointmentsPayload.canStartVideoVisit()) {
                        holder.videoVisitIndicator.setVisibility(View.VISIBLE);
                    } else {
                        holder.todayTimeTextView.setText(dateUtil.getTime12Hour());
                        holder.todayTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                        holder.todayTimeLayout.setVisibility(View.VISIBLE);
                    }
                } else if (shouldShowCheckoutButton) {
                    if(appointmentsPayload.getVisitType().hasVideoOption())
                    {
                        holder.videoVisitIndicator.setVisibility(View.VISIBLE);
                    }else {
                        holder.checkOutButton.setVisibility(View.VISIBLE);
                        holder.checkOutButton.setClickable(true);
                    }
                }
                holder.doctorName.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_green);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_checked_in);
                break;
            }
            case PENDING: {
                holder.todayTimeLayout.setVisibility(View.VISIBLE);
                holder.todayTimeTextView.setText(dateUtil.getTime12Hour());
                holder.todayTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                holder.doctorName.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_green_border);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_upcoming);
                break;
            }
            case REQUESTED: {
                holder.todayTimeMessage.setVisibility(View.VISIBLE);
                holder.todayTimeMessage.setText(Label.getLabel("appointment_status_pending"));
                holder.todayTimeLayout.setVisibility(View.VISIBLE);
                holder.todayTimeTextView.setText(dateUtil.getTime12Hour());
                holder.todayTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.lightning_yellow));
                holder.doctorName.setTextColor(ContextCompat.getColor(context, R.color.lightning_yellow));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_orange);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_pending);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                break;
            }
            case MISSED: {
                holder.todayTimeMessage.setVisibility(View.VISIBLE);
                holder.todayTimeMessage.setText(Label.getLabel("missed_appointments_heading"));
                holder.todayTimeLayout.setVisibility(View.VISIBLE);
                holder.todayTimeTextView.setText(dateUtil.getTime12Hour());
                holder.todayTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.remove_red));
                holder.doctorName.setTextColor(ContextCompat.getColor(context, R.color.remove_red));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_red);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_missed);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                break;
            }
            case CANCELED: {
                holder.todayTimeMessage.setVisibility(View.VISIBLE);
                holder.todayTimeMessage.setText(Label.getLabel("appointment_canceled"));
                holder.todayTimeLayout.setVisibility(View.VISIBLE);
                holder.todayTimeTextView.setText(dateUtil.getTime12Hour());
                holder.todayTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.pastel_blue));
                holder.doctorName.setTextColor(ContextCompat.getColor(context, R.color.pastel_blue));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_canceled);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                break;
            }
            case PENDING_UPCOMING: {
                holder.upcomingDateLayout.setVisibility(View.VISIBLE);
                holder.upcomingDateTextView.setText(dateUtil.getDayLiteralAbbr());
                holder.upcomingMonthTextView.setText(dateUtil.getDateAsMonthLiteralDay());
                holder.upcomingTimeTextView.setText(dateUtil.getTime12Hour());
                holder.doctorName.setTextColor(ContextCompat.getColor(context, R.color.grayRound));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_green_border);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_upcoming);
                break;
            }
            case REQUESTED_UPCOMING: {
                holder.upcomingDateLayout.setVisibility(View.VISIBLE);
                holder.upcomingDateTextView.setText(dateUtil.getDayLiteralAbbr());
                holder.upcomingMonthTextView.setText(dateUtil.getDateAsMonthLiteralDay());
                holder.upcomingTimeTextView.setText(dateUtil.getTime12Hour());
                holder.doctorName.setTextColor(ContextCompat.getColor(context, R.color.lightning_yellow));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_orange);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_pending);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                break;
            }
            case CANCELED_UPCOMING: {
                holder.upcomingDateLayout.setVisibility(View.VISIBLE);
                holder.upcomingDateTextView.setText(dateUtil.getDayLiteralAbbr());
                holder.upcomingMonthTextView.setText(dateUtil.getDateAsMonthLiteralDay());
                holder.upcomingTimeTextView.setText(dateUtil.getTime12Hour());
                holder.doctorName.setTextColor(ContextCompat.getColor(context, R.color.pastel_blue));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_canceled);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                break;
            }
            case CHECKED_OUT: {
                holder.checkedOutLabel.setVisibility(View.VISIBLE);
                holder.doctorName.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.initials.setBackgroundResource(R.drawable.round_tv);
                holder.initials.setVisibility(View.VISIBLE);
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_checked_out);
                break;
            }
            default: {
                cleanupViews(holder);
            }
        }

        int size = context.getResources().getDimensionPixelSize(R.dimen.payment_details_dialog_icon_size);
        Picasso.with(context).load(appointmentsPayload.getProvider().getPhoto())
                .resize(size, size)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(holder.profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.profileImage.setVisibility(View.VISIBLE);
                        holder.initials.setVisibility(View.GONE);
                        holder.cellAvatar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.profileImage.setVisibility(View.GONE);
                        holder.initials.setVisibility(View.VISIBLE);
                    }
                });
    }

    protected AppointmentDisplayStyle getDisplayStyle(AppointmentsPayloadDTO appointmentPayload) {
        AppointmentDisplayStyle displayStyle = appointmentPayload.getDisplayStyle();
        if (displayStyle == null) {
            displayStyle = AppointmentDisplayUtil.determineDisplayStyle(appointmentPayload);
            appointmentPayload.setDisplayStyle(displayStyle);
        }
        return displayStyle;
    }

    protected boolean isBreezePractice(String practiceId) {
        if (practiceId == null) {
            return false;
        }
        for (UserPracticeDTO userPracticeDTO : userPracticeDTOs) {
            if (userPracticeDTO.getPracticeId() != null && userPracticeDTO.getPracticeId().equals(practiceId)) {
                return userPracticeDTO.isBreezePractice();
            }
        }
        return false;
    }

    protected boolean shouldShowCheckOutButton(AppointmentDTO appointmentDTO,
                                               Set<String> enabledLocations,
                                               boolean isBreezePractice) {
        DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime());
        boolean isTheLocationWithBreezeEnabled = enabledLocations == null;
        if (enabledLocations != null) {
            for (String locationId : enabledLocations) {
                if (locationId.equals(appointmentDTO.getPayload().getLocation().getGuid())) {
                    isTheLocationWithBreezeEnabled = true;
                    break;
                }
            }
        }

        return isBreezePractice && isTheLocationWithBreezeEnabled
                && appointmentDTO.getPayload().canCheckOut()
                && callback.canCheckOut(appointmentDTO)
                && DateUtil.getInstance().isToday();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionHeaderTitle;

        ImageView cellAvatar;
        ImageView profileImage;
        TextView initials;
        TextView doctorName;
        TextView doctorType;

        View todayTimeLayout;
        TextView todayTimeTextView;
        TextView todayTimeMessage;

        View upcomingDateLayout;
        TextView upcomingDateTextView;
        TextView upcomingMonthTextView;
        TextView upcomingTimeTextView;

        Button checkOutButton;
        TextView checkedOutLabel;
        View videoVisitIndicator;

        View listItemDivider;

        ViewHolder(View itemView) {
            super(itemView);
            //Header view
            sectionHeaderTitle = itemView.findViewById(R.id.appointments_section_header_title);
            if (sectionHeaderTitle != null) {//skip looking up all the other views which will be null anyway
                return;
            }

            //Appointment Views
            cellAvatar = itemView.findViewById(R.id.cellAvatarImageView);
            profileImage = itemView.findViewById(R.id.providerPicImageView);
            initials = itemView.findViewById(R.id.avatarTextView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorType = itemView.findViewById(R.id.doctor_type);

            // Today
            todayTimeLayout = itemView.findViewById(R.id.todayTimeLayout);
            todayTimeTextView = itemView.findViewById(R.id.todayTimeTextView);
            todayTimeMessage = itemView.findViewById(R.id.todayTimeMessage);

            // Upcoming
            upcomingDateLayout = itemView.findViewById(R.id.upcomingDateLayout);
            upcomingDateTextView = itemView.findViewById(R.id.upcomingDateTextView);
            upcomingMonthTextView = itemView.findViewById(R.id.upcomingMonthTextView);
            upcomingTimeTextView = itemView.findViewById(R.id.upcomingTimeTextView);

            //Check-Out
            checkOutButton = itemView.findViewById(R.id.check_out_button);
            checkedOutLabel = itemView.findViewById(R.id.checked_out_label);

            //Video Visit
            videoVisitIndicator = itemView.findViewById(R.id.visit_type_video);

            listItemDivider = itemView.findViewById(R.id.appointment_list_item_divider);

        }

    }

    public interface SelectAppointmentCallback {
        void onItemTapped(AppointmentDTO appointmentDTO);

        void onCheckoutTapped(AppointmentDTO appointmentDTO);

        boolean canCheckOut(AppointmentDTO appointmentDTO);
    }
}
