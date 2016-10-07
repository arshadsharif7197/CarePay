package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepaylibray.appointments.models.Appointment;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadModel;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedBoldLabel;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaLightLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.customdialogs.CheckInOfficeNowAppointmentDialog;
import com.carecloud.carepaylibray.customdialogs.QueueAppointmentDialog;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by harshal_patil on 9/8/2016.
 */
public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

    private Context context;
    private List<Object> appointmentItems;
    private AppointmentsListFragment appointmentsListFragment;

    /**
     * Constructor.
     * @param context: context
     * @param appointmentItems: appointment list
     * @param appointmentsListFragment: Screen instance
     */
    public AppointmentsAdapter(Context context, List<Object> appointmentItems,
                               AppointmentsListFragment appointmentsListFragment) {

        this.context = context;
        this.appointmentItems = appointmentItems;
        this.appointmentsListFragment = appointmentsListFragment;
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.appointment_list_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, final int position) {
        final Object object = appointmentItems.get(position);
        View view = appointmentsListFragment.getView();

        if (object.getClass() == Appointment.class) {
            holder.appointmentSectionLinearLayout.setVisibility(View.GONE);
            holder.appointmentItemLinearLayout.setVisibility(View.VISIBLE);

            final AppointmentsPayloadModel item = ((Appointment) object).getPayload();

            holder.doctorName.setText(item.getProvider().getName());
            SystemUtil.setProximaNovaSemiboldTypeface(context, holder.doctorName);

            holder.doctorType.setText(item.getProvider().getSpecialty());
            SystemUtil.setProximaNovaRegularTypeface(context, holder.doctorType);

            // Date of Upcoming appointment
            String upcomingStartTime = item.getStartTime();
            DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
            DateUtil.getInstance().setDateRaw(upcomingStartTime);
            String time12Hour = DateUtil.getInstance().getTime12Hour();

            DateUtil.getInstance().setFormat("EEE dd MMM");
            String dayLiteralAbbr = DateUtil.getInstance().getDayLiteralAbbr();
            String monthLiteralAbbr = DateUtil.getInstance().getMonthLiteralAbbr();
            int day = DateUtil.getInstance().getDay();

            final boolean isPending = item.getAppointmentStatusModel().getId() == 1;
            final boolean isCheckedIn = item.getAppointmentStatusModel().getId() == 2;

            if (getSectionHeaderTitle(upcomingStartTime).equals(CarePayConstants.DAY_UPCOMING)) {
                if(isCheckedIn) {
                    holder.todayTimeLinearLayout.setVisibility(View.VISIBLE);
                    holder.upcomingDateLinearLayout.setVisibility(View.GONE);
                    holder.todayTimeTextView.setText(context.getString(R.string.checked_in_label));
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
                if(isCheckedIn) {
                    holder.todayTimeTextView.setText(context.getString(R.string.checked_in_label));
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
                    if (object.getClass() == Appointment.class) {

                        // appointment clicked item saved so that it can be used on Payment
                        Appointment item = ((Appointment) object);
                        AppointmentsActivity.model = item;

                        if (isPending) {
                            new CheckInOfficeNowAppointmentDialog(context, item).show();
                        } else if (isCheckedIn) {
                            new QueueAppointmentDialog(context, item).show();
                        } else {
                            new CheckInOfficeNowAppointmentDialog(context, item).show();
                        }
                    }
                }
            });

//            if (TextUtils.isEmpty(item.getPhoto())) {
                holder.shortName.setText(StringUtil.onShortDrName(item.getProvider().getName()));
//            } else {
//                Picasso.with(context).load(item.getPhoto()).transform(new CircleImageTransform()).resize(58, 58).into(holder.profileImage);
//                holder.profileImage.setVisibility(View.VISIBLE);
//            }

            if (isPending) {
                holder.cellAvatar.setVisibility(View.INVISIBLE);
            } else {
                holder.cellAvatar.setVisibility(View.INVISIBLE);
            }

            if (isCheckedIn) {
                holder.appointmentItemLinearLayout.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.checked_in_appointment_bg));
            } else {
                holder.appointmentItemLinearLayout.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.white));
            }
        } else {
            AppointmentSectionHeaderModel item = (AppointmentSectionHeaderModel) object;
            if (position == 0) {
                holder.appointmentSectionLinearLayout.setVisibility(View.GONE);
                holder.appointmentItemLinearLayout.setVisibility(View.GONE);

                if (view != null) {
                    CustomProxyNovaSemiBoldLabel appointmentStickyHeaderTitle =
                            (CustomProxyNovaSemiBoldLabel) view.findViewById(R.id.appointments_sticky_header_title);
                    appointmentStickyHeaderTitle.setText(item.getAppointmentHeader());
                    appointmentStickyHeaderTitle.setVisibility(View.VISIBLE);
                }
            } else {
                String title = item.getAppointmentHeader();
                holder.appointmentSectionLinearLayout.setVisibility(View.VISIBLE);
                holder.appointmentItemLinearLayout.setVisibility(View.GONE);
                holder.appointmentSectionHeaderTitle.setText(title);
            }
        }

        // OnScrollListener for RecyclerView to get first visible element in the list and then change the sticky header accordingly
        if (view != null) {
            final RecyclerView appointmentRecyclerView = (RecyclerView) view.findViewById(R.id.appointments_recycler_view);
            appointmentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    LinearLayoutManager recycleViewManager = (LinearLayoutManager) appointmentRecyclerView.getLayoutManager();
                    int firstVisibleItemPosition = recycleViewManager.findFirstVisibleItemPosition();

                    if (appointmentItems.size() > firstVisibleItemPosition) {
                        Object object = appointmentItems.get(firstVisibleItemPosition);
                        if (object.getClass() == Appointment.class) {
                            Appointment item = (Appointment) object;
                            View view = appointmentsListFragment.getView();

                            if (view != null) {
                                CustomProxyNovaSemiBoldLabel appointmentStickyHeaderTitle =
                                        (CustomProxyNovaSemiBoldLabel) view.findViewById(R.id.appointments_sticky_header_title);

                                boolean isCheckedIn = item.getPayload().getAppointmentStatusModel().getId() == 2;
                                if (isCheckedIn) {
                                    appointmentStickyHeaderTitle.setVisibility(View.GONE);
                                } else {
                                    String sectionHeaderTitle = getSectionHeaderTitle(item.getPayload().getStartTime());
                                    appointmentStickyHeaderTitle.setText(sectionHeaderTitle);
                                    appointmentStickyHeaderTitle.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private String getSectionHeaderTitle(String appointmentRawDate) {
        // Current date
        String currentDate = DateUtil.getInstance().setToCurrent().getDateAsMMddyyyy();
        Date currentConvertedDate = DateUtil.getInstance().setDateRaw(currentDate).getDate();

        // Appointment date
        String appointmentDate = DateUtil.getInstance().setDateRaw(appointmentRawDate).getDateAsMMddyyyy();
        Date convertedAppointmentDate = DateUtil.getInstance().setDateRaw(appointmentDate).getDate();

        String previousDay = "";
        if (convertedAppointmentDate.after(currentConvertedDate)
                && !appointmentDate.equalsIgnoreCase(currentDate)) {
            previousDay = CarePayConstants.DAY_UPCOMING;
        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
            // Do nothing
        } else {
            previousDay = CarePayConstants.DAY_TODAY;
        }

        return previousDay;
    }

    @Override
    public int getItemCount() {
        return appointmentItems.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private CustomProxyNovaLightLabel upcomingDateTextView;
        private CustomGothamRoundedMediumLabel appointmentSectionHeaderTitle;
        private CustomGothamRoundedMediumLabel shortName;
        private CustomGothamRoundedBoldLabel todayTimeTextView;
        private CustomProxyNovaSemiBoldLabel doctorName;
        private CustomProxyNovaRegularLabel upcomingMonthTextView;
        private CustomProxyNovaRegularLabel upcomingTimeTextView;
        private CustomProxyNovaRegularLabel doctorType;
        private ImageView cellAvatar;
        private ImageView profileImage;
        private LinearLayout appointmentSectionLinearLayout;
        private LinearLayout appointmentItemLinearLayout;
        private LinearLayout todayTimeLinearLayout;
        private LinearLayout upcomingDateLinearLayout;

        AppointmentViewHolder(View itemView) {
            super(itemView);
            doctorName = (CustomProxyNovaSemiBoldLabel) itemView.findViewById(R.id.doctor_name);
            doctorType = (CustomProxyNovaRegularLabel) itemView.findViewById(R.id.doctor_type);
            doctorType.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.lightSlateGray));

            shortName = (CustomGothamRoundedMediumLabel) itemView.findViewById(R.id.avatarTextView);
            cellAvatar = (ImageView) itemView.findViewById(R.id.cellAvatarImageView);
            profileImage = (ImageView) itemView.findViewById(R.id.providerPicImageView);

            appointmentSectionLinearLayout = (LinearLayout) itemView.findViewById(R.id.appointment_section_linear_layout);
            appointmentItemLinearLayout = (LinearLayout) itemView.findViewById(R.id.appointment_item_linear_layout);
            appointmentSectionHeaderTitle = (CustomGothamRoundedMediumLabel) itemView.findViewById(R.id.appointments_section_header_title);

            // Today
            todayTimeLinearLayout = (LinearLayout) itemView.findViewById(R.id.todayTimeLinearlayout);
            todayTimeTextView = (CustomGothamRoundedBoldLabel) itemView.findViewById(R.id.todayTimeTextView);

            // Upcoming
            upcomingDateLinearLayout = (LinearLayout) itemView.findViewById(R.id.upcomingDateLinearlayout);
            upcomingDateTextView = (CustomProxyNovaLightLabel) itemView.findViewById(R.id.upcomingDateTextView);
            upcomingMonthTextView = (CustomProxyNovaRegularLabel) itemView.findViewById(R.id.upcomingMonthTextView);
            upcomingTimeTextView = (CustomProxyNovaRegularLabel) itemView.findViewById(R.id.upcomingTimeTextView);
        }
    }
}