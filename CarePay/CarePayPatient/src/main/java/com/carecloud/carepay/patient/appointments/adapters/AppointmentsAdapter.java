package com.carecloud.carepay.patient.appointments.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.appointments.dialog.CheckInOfficeNowAppointmentDialog;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.BaseDoctorInfoDialog;
import com.carecloud.carepaylibray.customdialogs.CancelAppointmentDialog;
import com.carecloud.carepaylibray.customdialogs.QueueAppointmentDialog;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by harshal_patil on 9/8/2016.
 */
public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>
        implements CancelAppointmentDialog.CancelAppointmentCallback {

    private Context context;
    private List<Object> appointmentItems;
    private AppointmentLabelDTO appointmentLabels;
    private AppointmentsResultModel appointmentInfo;
    private AppointmentsListFragment appointmentsListFragment;

    /**
     * Constructor.
     *
     * @param context                  context
     * @param appointmentItems         list of appointments
     * @param appointmentsListFragment screen instance
     */
    public AppointmentsAdapter(Context context, List<Object> appointmentItems,
                               AppointmentsListFragment appointmentsListFragment,
                               AppointmentsResultModel appointmentInfo) {

        this.context = context;
        this.appointmentItems = appointmentItems;
        this.appointmentsListFragment = appointmentsListFragment;
        this.appointmentInfo = appointmentInfo;
        this.appointmentLabels = appointmentInfo.getMetadata().getLabel();
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.appointment_list_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AppointmentViewHolder holder, final int position) {
        final Object object = appointmentItems.get(position);
        View view = appointmentsListFragment.getView();

        if (view == null) {
            return;
        }

        if (object.getClass() == AppointmentDTO.class) {
            holder.appointmentSectionLinearLayout.setVisibility(View.GONE);
            holder.appointmentItemLinearLayout.setVisibility(View.VISIBLE);

            final AppointmentsPayloadDTO item = ((AppointmentDTO) object).getPayload();
            holder.doctorName.setText(item.getProvider().getName());
            holder.doctorType.setText(item.getProvider().getSpecialty().getName());

            // Date of Upcoming appointment
            String upcomingStartTime = item.getStartTime();
            DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
            DateUtil.getInstance().setDateRaw(upcomingStartTime);
            String time12Hour = DateUtil.getInstance().getTime12Hour();

            DateUtil.getInstance().setFormat("EEE dd MMM");
            String dayLiteralAbbr = DateUtil.getInstance().getDayLiteralAbbr();
            String monthLiteralAbbr = DateUtil.getInstance().getMonthLiteralAbbr();
            int day = DateUtil.getInstance().getDay();

            final String sectionHeaderTitle = getSectionHeaderTitle(upcomingStartTime);
            final boolean isPending = item.getAppointmentStatusModel().getId() == 1;
            final boolean isCheckedIn = item.getAppointmentStatusModel().getId() == 2;
            final boolean isCanceled = item.getAppointmentStatusModel().getId() == 4;
            final boolean isRequested = item.getAppointmentStatusModel().getId() == 5;

            if (getSectionHeaderTitle(upcomingStartTime).equals(appointmentLabels.getUpcomingAppointmentsHeading())) {
                if (isCheckedIn) {
                    holder.todayTimeLinearLayout.setVisibility(View.VISIBLE);
                    holder.upcomingDateLinearLayout.setVisibility(View.GONE);
                    holder.todayTimeTextView.setText(appointmentLabels.getAppointmentsCheckedInLabel());
                    holder.todayTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
                } else {
                    holder.todayTimeLinearLayout.setVisibility(View.GONE);
                    holder.upcomingDateLinearLayout.setVisibility(View.VISIBLE);
                    SystemUtil.setProximaNovaLightTypeface(context, holder.upcomingDateTextView);

                    holder.upcomingDateTextView.setText(dayLiteralAbbr);
                    holder.upcomingMonthTextView.setText(monthLiteralAbbr.toUpperCase() + " " + day);
                    holder.upcomingTimeTextView.setText(time12Hour);
                }
            } else {
                holder.todayTimeLinearLayout.setVisibility(View.VISIBLE);
                holder.upcomingDateLinearLayout.setVisibility(View.GONE);
                if (isCheckedIn) {
                    holder.todayTimeTextView.setText(StringUtil.getLabelForView(
                            appointmentLabels.getAppointmentsCheckedInLabel()));
                    holder.todayTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
                } else {
                    holder.todayTimeTextView.setText(time12Hour);
                    holder.todayTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.dark_green));
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View appointmentListItem) {
                    // Restricted the appointment list item click if it is appointment header type.
                    if (object.getClass() == AppointmentDTO.class) {
                        // appointment clicked item saved so that it can be used on Payment
                        AppointmentDTO item = ((AppointmentDTO) object);
                        AppointmentsActivity.model = item;

                        // Missed Appointment
                        if (sectionHeaderTitle.equalsIgnoreCase(CarePayConstants.DAY_OVER) && !isCheckedIn) {
                            new CancelAppointmentDialog(context, item, appointmentInfo,
                                    BaseDoctorInfoDialog.AppointmentType.MISSED_APPOINTMENT, null).show();

                        } else if (isCheckedIn) {
                            // Checked-In Appointment
                            new QueueAppointmentDialog(context, item, appointmentLabels).show();

                        } else if (isCanceled) {
                            // Cancelled Appointment
                            new CancelAppointmentDialog(context, item, appointmentInfo,
                                    BaseDoctorInfoDialog.AppointmentType.CANCELLED_APPOINTMENT, AppointmentsAdapter.this).show();

                        } else if (isAppointmentCancellable(item)) {
                            // Appointment as long as it's 24 hours or more in the future
                            new CancelAppointmentDialog(context, item, appointmentInfo,
                                    BaseDoctorInfoDialog.AppointmentType.CANCEL_APPOINTMENT, AppointmentsAdapter.this).show();

                        } else if (isRequested) {
                            // Requested Appointment
                            new CancelAppointmentDialog(context, item, appointmentInfo,
                                    BaseDoctorInfoDialog.AppointmentType.REQUESTED_APPOINTMENT, null).show();

                        } else if (isPending) {
                            if (getSectionHeaderTitle(item.getPayload().getStartTime())
                                    .equals(appointmentLabels.getTodayAppointmentsHeading())) {

                                // Pending Appointment && Appointments for the current business day
                                new CheckInOfficeNowAppointmentDialog(context, false, item, appointmentInfo).show();

                            } else {
                                // Pending Appointment && Appointment in the future that is not today
                                new CancelAppointmentDialog(context, item, appointmentInfo,
                                        BaseDoctorInfoDialog.AppointmentType.UPCOMING_APPOINTMENT, null).show();
                            }
                        } else {
                            // Other
                            new CheckInOfficeNowAppointmentDialog(context, false, item, appointmentInfo).show();
                        }
                    }
                }
            });

            String photoUrl = item.getProvider().getPhoto();
            if (TextUtils.isEmpty(photoUrl)) {
                holder.shortName.setText(StringUtil.onShortDrName(item.getProvider().getName()));
            } else {
                Picasso.Builder builder = new Picasso.Builder(context);
                builder.listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        holder.shortName.setText(StringUtil.onShortDrName(item.getProvider().getName()));
                    }
                });

                builder.build().load(photoUrl).transform(new CircleImageTransform())
                        .resize(58, 58).into(holder.profileImage);
                holder.profileImage.setVisibility(View.VISIBLE);
            }

            if (isPending) {
                holder.cellAvatar.setVisibility(View.INVISIBLE);
            }

            if (sectionHeaderTitle.equalsIgnoreCase(CarePayConstants.DAY_OVER) && !isCheckedIn) {
                holder.missedAppointmentTextView.setVisibility(View.VISIBLE);
                holder.missedAppointmentTextView.setText(appointmentLabels.getMissedAppointmentsHeading());
                holder.missedAppointmentTextView.setTextColor(
                        ContextCompat.getColor(view.getContext(), R.color.optionl_gray));

                holder.doctorName.setTextColor(ContextCompat.getColor(view.getContext(), R.color.optionl_gray));
                holder.todayTimeTextView.setTypeface(null, Typeface.BOLD);
                holder.todayTimeTextView.setTextColor(
                        ContextCompat.getColor(view.getContext(), R.color.lightningyellow));

                holder.cellAvatar.setVisibility(View.VISIBLE);
                holder.cellAvatar.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.icn_cell_avatar_badge_missed));
            } else if (isCanceled) {
                holder.cellAvatar.setVisibility(View.VISIBLE);
                holder.doctorName.setTextColor(ContextCompat.getColor(view.getContext(), R.color.optionl_gray));
                holder.cellAvatar.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.icn_cell_avatar_badge_canceled));
            } else {
                holder.cellAvatar.setVisibility(View.INVISIBLE);
                holder.missedAppointmentTextView.setVisibility(View.GONE);
                holder.doctorName.setTextColor(ContextCompat.getColor(view.getContext(), R.color.bright_cerulean));
            }

            /*if(isPending && (sectionHeaderTitle.equalsIgnoreCase(CarePayConstants.DAY_TODAY) ||
                    sectionHeaderTitle.equalsIgnoreCase(CarePayConstants.DAY_UPCOMING))){
                holder.cellAvatar.setVisibility(View.VISIBLE);
                holder.cellAvatar.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.icn_cell_avatar_badge_pending));
            }*/

            if (isCheckedIn) {
                holder.appointmentItemLinearLayout.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.checked_in_appointment_bg));
            } else {
                holder.appointmentItemLinearLayout.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.white));
            }

            // Check if this item is last in section and hide it's divider
            if (appointmentItems.size() > (position + 1)) {
                Object nextItem = appointmentItems.get(position + 1);
                if (nextItem.getClass() == AppointmentSectionHeaderModel.class) {
                    holder.listItemDivider.setVisibility(View.GONE);
                }
            }
        } else {
            AppointmentSectionHeaderModel item = (AppointmentSectionHeaderModel) object;
            String title = getSectionHeaderTitleByDay(item.getAppointmentHeader());

            if (position == 0) {
                holder.appointmentSectionLinearLayout.setVisibility(View.GONE);
                holder.appointmentItemLinearLayout.setVisibility(View.GONE);

                CarePayTextView appointmentStickyHeaderTitle =
                        (CarePayTextView) view.findViewById(R.id.appointments_sticky_header_title);
                appointmentStickyHeaderTitle.setText(title);
                appointmentStickyHeaderTitle.setVisibility(View.VISIBLE);
                appointmentStickyHeaderTitle.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
            } else {
                holder.appointmentSectionLinearLayout.setVisibility(View.VISIBLE);
                holder.appointmentItemLinearLayout.setVisibility(View.GONE);
                holder.appointmentSectionHeaderTitle.setText(title);
                holder.appointmentSectionHeaderTitle.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
            }
        }

        // OnScrollListener for RecyclerView to get first visible element in the list and then change the sticky header accordingly
        final RecyclerView appointmentRecyclerView = (RecyclerView) view.findViewById(R.id.appointments_recycler_view);
        appointmentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager recycleViewManager = (LinearLayoutManager) appointmentRecyclerView.getLayoutManager();
                int firstVisibleItemPosition = recycleViewManager.findFirstVisibleItemPosition();

                // Return when swipe to refresh in progress..
                if (firstVisibleItemPosition == -1) {
                    return;
                }

                if (appointmentItems.size() > firstVisibleItemPosition) {

                    Object object = appointmentItems.get(firstVisibleItemPosition);
                    if (object.getClass() == AppointmentDTO.class) {
                        AppointmentDTO item = (AppointmentDTO) object;
                        View view = appointmentsListFragment.getView();

                        if (view != null) {
                            CarePayTextView appointmentStickyHeaderTitle =
                                    (CarePayTextView) view.findViewById(R.id.appointments_sticky_header_title);

                            boolean isCheckedIn = item.getPayload().getAppointmentStatusModel().getId() == 2;
                            if (isCheckedIn) {
                                appointmentStickyHeaderTitle.setVisibility(View.GONE);
                            } else {
                                String sectionHeaderTitle = getSectionHeaderTitleByDay(
                                        getSectionHeaderTitle(item.getPayload().getStartTime()));

                                appointmentStickyHeaderTitle.setText(sectionHeaderTitle);
                                appointmentStickyHeaderTitle.setVisibility(View.VISIBLE);
                                appointmentStickyHeaderTitle.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
                            }
                        }
                    }
                }
            }
        });
    }

    private boolean isAppointmentCancellable(AppointmentDTO item) {
        // Get appointment date/time in required format
        String appointmentTimeStr = item.getPayload().getStartTime();
        Date appointmentTime = DateUtil.getInstance().setDateRaw(appointmentTimeStr).getDate();

        // Get current date/time in required format
        String currentTime = DateUtil.getDateRaw(DateUtil.getInstance().setToCurrent().getDate());
        Date currentDate = DateUtil.getInstance().setDateRaw(currentTime).getDate();

        if (appointmentTime != null && currentDate != null) {
            long differenceInMilli = appointmentTime.getTime() - currentDate.getTime();
            long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMilli);

            if (differenceInMinutes > CarePayConstants.APPOINTMENT_CANCEL_TIME_IN_MINUTES) {
                return true;
            }
        }
        return false;
    }

    private String getSectionHeaderTitleByDay(String day) {
        if (day.equalsIgnoreCase(CarePayConstants.DAY_OVER) ||
                day.equalsIgnoreCase(appointmentLabels.getTodayAppointmentsHeading())) {
            return appointmentLabels.getTodayAppointmentsHeading();
        } else {
            return appointmentLabels.getUpcomingAppointmentsHeading();
        }
    }

    /**
     * Hide the header when swipe to refresh in progress.
     */
    public void hideHeaderView() {
        View view = appointmentsListFragment.getView();
        CarePayTextView headerTitle;
        if (view != null) {
            headerTitle = (CarePayTextView)
                    view.findViewById(R.id.appointments_sticky_header_title);
            headerTitle.setVisibility(View.GONE);
        }
    }

    private String getSectionHeaderTitle(String appointmentRawDate) {
        // Current date
        String currentDate = DateUtil.getInstance().setToCurrent().getDateAsMMddyyyy();
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_HEADER_DATE_FORMAT);
        Date currentConvertedDate = DateUtil.getInstance().setDateRaw(currentDate).getDate();

        // Appointment date
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        String appointmentDate = DateUtil.getInstance().setDateRaw(appointmentRawDate).getDateAsMMddyyyy();
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_HEADER_DATE_FORMAT);
        Date convertedAppointmentDate = DateUtil.getInstance().setDateRaw(appointmentDate).getDate();

        if (convertedAppointmentDate.after(currentConvertedDate)
                && !appointmentDate.equalsIgnoreCase(currentDate)) {
            return appointmentLabels.getUpcomingAppointmentsHeading();
        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
            return CarePayConstants.DAY_OVER;
        } else {
            // Get appointment date/time in required format
            DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
            Date appointmentTime = DateUtil.getInstance().setDateRaw(appointmentRawDate).getDate();

            // Get current date/time in required format
            String currentTime = DateUtil.getDateRaw(DateUtil.getInstance().setToCurrent().getDate());
            Date currentDateTemp = DateUtil.getInstance().setDateRaw(currentTime).getDate();

            if (appointmentTime != null && currentDate != null) {
                long differenceInMilli = appointmentTime.getTime() - currentDateTemp.getTime();
                long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMilli);
                if (differenceInMinutes < 0) {
                    return CarePayConstants.DAY_OVER;
                }
            }
            return appointmentLabels.getTodayAppointmentsHeading();
        }
    }

    @Override
    public int getItemCount() {
        return appointmentItems.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private CarePayTextView upcomingDateTextView;
        private CarePayTextView appointmentSectionHeaderTitle;
        private CarePayTextView shortName;
        private CarePayTextView todayTimeTextView;
        private CarePayTextView doctorName;
        private CarePayTextView missedAppointmentTextView;
        private CarePayTextView upcomingMonthTextView;
        private CarePayTextView upcomingTimeTextView;
        private CarePayTextView doctorType;
        private ImageView cellAvatar;
        private ImageView profileImage;
        private LinearLayout appointmentSectionLinearLayout;
        private LinearLayout appointmentItemLinearLayout;
        private LinearLayout todayTimeLinearLayout;
        private LinearLayout upcomingDateLinearLayout;
        private View listItemDivider;

        AppointmentViewHolder(View itemView) {
            super(itemView);
            doctorName = (CarePayTextView) itemView.findViewById(R.id.doctor_name);
            doctorName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.bright_cerulean));

            doctorType = (CarePayTextView) itemView.findViewById(R.id.doctor_type);
            doctorType.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.lightSlateGray));

            shortName = (CarePayTextView) itemView.findViewById(R.id.avatarTextView);
            shortName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.lightSlateGray));

            cellAvatar = (ImageView) itemView.findViewById(R.id.cellAvatarImageView);
            profileImage = (ImageView) itemView.findViewById(R.id.providerPicImageView);

            appointmentSectionLinearLayout = (LinearLayout) itemView.findViewById(R.id.appointment_section_linear_layout);
            appointmentItemLinearLayout = (LinearLayout) itemView.findViewById(R.id.appointment_item_linear_layout);
            appointmentSectionHeaderTitle = (CarePayTextView)
                    itemView.findViewById(R.id.appointments_section_header_title);

            // Today
            todayTimeLinearLayout = (LinearLayout) itemView.findViewById(R.id.todayTimeLinearlayout);
            todayTimeTextView = (CarePayTextView) itemView.findViewById(R.id.todayTimeTextView);

            // Upcoming
            upcomingDateLinearLayout = (LinearLayout) itemView.findViewById(R.id.upcomingDateLinearlayout);
            upcomingDateTextView = (CarePayTextView) itemView.findViewById(R.id.upcomingDateTextView);
            upcomingMonthTextView = (CarePayTextView) itemView.findViewById(R.id.upcomingMonthTextView);
            upcomingTimeTextView = (CarePayTextView) itemView.findViewById(R.id.upcomingTimeTextView);

            // Missed
            missedAppointmentTextView = (CarePayTextView)
                    itemView.findViewById(R.id.missed_appointment_text_view);

            listItemDivider = itemView.findViewById(R.id.appointment_list_item_divider);
        }
    }

    @Override
    public void onCancelAppointment(AppointmentDTO appointmentDTO) {
        appointmentItems.remove(appointmentDTO);
        notifyDataSetChanged();
    }
}