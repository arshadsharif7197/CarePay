package com.carecloud.carepay.patient.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by lmenendez on 5/2/17.
 */

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {
    public static final int VIEW_TYPE_HEADER = 1;
    public static final int VIEW_TYPE_APPOINTMENT = 2;

    public interface SelectAppointmentCallback{
        void onItemTapped(AppointmentDTO appointmentDTO);
    }

    private Context context;
    private SelectAppointmentCallback callback;
    private List<AppointmentDTO> appointmentItems = new ArrayList<>();

    private List<AppointmentDTO> sortedAppointments = new ArrayList<>();

    public AppointmentListAdapter(Context context, List<AppointmentDTO> appointmentItems, SelectAppointmentCallback callback){
        this.context = context;
        this.appointmentItems = appointmentItems;
        this.callback = callback;

        sortAppointments();
    }

    @Override
    public int getItemViewType(int position){
        AppointmentDTO appointmentDTO = sortedAppointments.get(position);
        if(appointmentDTO.getPayload().getDisplayStyle()==AppointmentDisplayStyle.HEADER){
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_APPOINTMENT;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        if(viewType == VIEW_TYPE_HEADER){
            view = inflater.inflate(R.layout.appointment_list_header, parent, false);
        }else{
            view = inflater.inflate(R.layout.appointment_list_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AppointmentsPayloadDTO appointmentsPayload = sortedAppointments.get(position).getPayload();

        //Header
        if(getItemViewType(position) == VIEW_TYPE_HEADER){
            holder.sectionHeaderTitle.setText(appointmentsPayload.getId());
            return;
        }

        //Appointment
        ProviderDTO provider = appointmentsPayload.getProvider();
        if(!StringUtil.isNullOrEmpty(provider.getName())) {
            holder.initials.setText(StringUtil.getShortName(provider.getName()));
        }
        holder.doctorName.setText(StringUtil.getLabelForView(provider.getName()));
        holder.doctorType.setText(StringUtil.getLabelForView(provider.getSpecialty().getName()));

        Picasso.with(context).load(appointmentsPayload.getProvider().getPhoto())
                .resize(58, 58)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(holder.profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.profileImage.setVisibility(View.VISIBLE);
                        holder.initials.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.profileImage.setVisibility(View.GONE);
                        holder.initials.setVisibility(View.VISIBLE);
                    }
                });

        //cleanup
        cleanupViews(holder);

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentsPayload.getStartTime());
        AppointmentDisplayStyle style = appointmentsPayload.getDisplayStyle();
        switch (style){
            case CHECKED_IN:{
                holder.checkOutButton.setVisibility(View.VISIBLE);
                //todo set checkout button listener
                holder.checkOutButton.setClickable(false);
                break;
            }
            case PENDING:{
                holder.todayTimeLayout.setVisibility(View.VISIBLE);
                holder.todayTimeTextView.setText(dateUtil.getTime12Hour());
                break;
            }
            case REQUESTED:{
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_pending);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                holder.todayTimeLayout.setVisibility(View.VISIBLE);
                holder.todayTimeTextView.setText(dateUtil.getTime12Hour());
                break;
            }
            case MISSED:{
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_missed);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                holder.todayTimeLayout.setVisibility(View.VISIBLE);
                holder.missedAppointmentTextView.setVisibility(View.VISIBLE);
                holder.todayTimeTextView.setText(dateUtil.getTime12Hour());
                break;
            }
            case CANCELED:{
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_canceled);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                holder.todayTimeLayout.setVisibility(View.VISIBLE);
                holder.todayTimeTextView.setText(dateUtil.getTime12Hour());
                break;
            }
            case PENDING_UPCOMING:{
                holder.upcomingDateLayout.setVisibility(View.VISIBLE);
                holder.upcomingDateTextView.setText(dateUtil.getDayLiteralAbbr());
                holder.upcomingMonthTextView.setText(dateUtil.getMonthLiteralAbbr());
                holder.upcomingTimeTextView.setText(dateUtil.getTime12Hour());
                break;
            }
            case REQUESTED_UPCOMING:{
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_pending);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                holder.upcomingDateLayout.setVisibility(View.VISIBLE);
                holder.upcomingDateTextView.setText(dateUtil.getDayLiteralAbbr());
                holder.upcomingMonthTextView.setText(dateUtil.getMonthLiteralAbbr());
                holder.upcomingTimeTextView.setText(dateUtil.getTime12Hour());
                break;
            }
            case CANCELED_UPCOMING:{
                holder.cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_canceled);
                holder.cellAvatar.setVisibility(View.VISIBLE);
                holder.upcomingDateLayout.setVisibility(View.VISIBLE);
                holder.upcomingDateTextView.setText(dateUtil.getDayLiteralAbbr());
                holder.upcomingMonthTextView.setText(dateUtil.getMonthLiteralAbbr());
                holder.upcomingTimeTextView.setText(dateUtil.getTime12Hour());
                break;
            }
            default:{
                cleanupViews(holder);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null){
                    callback.onItemTapped(sortedAppointments.get(position));
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return sortedAppointments.size();
    }

    public void setAppointmentItems(List<AppointmentDTO> appointmentItems){
        this.appointmentItems = appointmentItems;
        sortAppointments();
        notifyDataSetChanged();
    }

    private void sortAppointments(){
        Collections.sort(appointmentItems, new Comparator<AppointmentDTO>() {
            @Override
            public int compare(AppointmentDTO left, AppointmentDTO right) {
                AppointmentDisplayStyle leftStyle = getDisplayStyle(left);
                AppointmentDisplayStyle rightStyle = getDisplayStyle(right);

                Date leftDate = DateUtil.getInstance().setDateRaw(left.getPayload().getStartTime()).getDate();
                Date rightDate = DateUtil.getInstance().setDateRaw(right.getPayload().getStartTime()).getDate();

                if(leftStyle == AppointmentDisplayStyle.CHECKED_IN || rightStyle == AppointmentDisplayStyle.CHECKED_IN) {//Check-in should go on top
                    if(leftStyle != rightStyle) {
                        if (leftStyle == AppointmentDisplayStyle.CHECKED_IN) {//left should come first
                            return -1;
                        }else {//right should come first
                            return 1;
                        }
                    }else{
                        //compare the dates
                        return leftDate.compareTo(rightDate);
                    }
                }
                return leftDate.compareTo(rightDate);
            }

        });

        addHeaders();
    }

    private void addHeaders(){
        sortedAppointments.clear();

        boolean checkinHeader = false;
        boolean todayHeader = false;
        boolean upcomingHeader = false;
        AppointmentDTO headerTemplate;

        for(AppointmentDTO appointmentDTO : appointmentItems){
            if(appointmentDTO.getPayload().getDisplayStyle()==null){//required here to handle the case where user has only 1 appt and it was not run through comparator
                appointmentDTO.getPayload().setDisplayStyle(determineDisplayStyle(appointmentDTO));
            }

            //check if we need to add a checked in header
            if( !checkinHeader && appointmentDTO.getPayload().getDisplayStyle() == AppointmentDisplayStyle.CHECKED_IN){
                headerTemplate = new AppointmentDTO();
                headerTemplate.getPayload().setDisplayStyle(AppointmentDisplayStyle.HEADER);
                headerTemplate.getPayload().setId(Label.getLabel("appointments_checked_in_label"));
                sortedAppointments.add(headerTemplate);
                checkinHeader = true;
            }

            //check if we need a today header
            if(!todayHeader && appointmentDTO.getPayload().getDisplayStyle() != AppointmentDisplayStyle.CHECKED_IN && appointmentDTO.getPayload().isAppointmentToday()){
                headerTemplate = new AppointmentDTO();
                headerTemplate.getPayload().setDisplayStyle(AppointmentDisplayStyle.HEADER);
                headerTemplate.getPayload().setId(Label.getLabel("today_appointments_heading"));
                sortedAppointments.add(headerTemplate);
                todayHeader = true;
            }

            //check if we need an upcoming header
            if(!upcomingHeader && appointmentDTO.getPayload().getDisplayStyle() != AppointmentDisplayStyle.CHECKED_IN && !appointmentDTO.getPayload().isAppointmentToday()){
                headerTemplate = new AppointmentDTO();
                headerTemplate.getPayload().setDisplayStyle(AppointmentDisplayStyle.HEADER);
                headerTemplate.getPayload().setId(Label.getLabel("upcoming_appointments_heading"));
                sortedAppointments.add(headerTemplate);
                upcomingHeader = true;
            }

            sortedAppointments.add(appointmentDTO);
        }
    }

    private AppointmentDisplayStyle getDisplayStyle(AppointmentDTO appointmentDTO){
        AppointmentDisplayStyle displayStyle = appointmentDTO.getPayload().getDisplayStyle();
        if(displayStyle == null){
            displayStyle = determineDisplayStyle(appointmentDTO);
            appointmentDTO.getPayload().setDisplayStyle(displayStyle);
        }
        return displayStyle;
    }

    private AppointmentDisplayStyle determineDisplayStyle(AppointmentDTO appointmentDTO){
        AppointmentsPayloadDTO appointmentsPayload =  appointmentDTO.getPayload();
        switch (appointmentsPayload.getAppointmentStatus().getCode()){
            case CarePayConstants.CHECKED_IN:
            case CarePayConstants.IN_PROGRESS_IN_ROOM:
            case CarePayConstants.IN_PROGRESS_OUT_ROOM:
                return AppointmentDisplayStyle.CHECKED_IN;
            case CarePayConstants.CHECKED_OUT:
            case CarePayConstants.BILLED:
            case CarePayConstants.MANUALLY_BILLED:
                return AppointmentDisplayStyle.CHECKED_OUT;
            case CarePayConstants.REQUESTED:
                if(appointmentsPayload.isAppointmentToday()) {
                    return AppointmentDisplayStyle.REQUESTED;
                }else{
                    return AppointmentDisplayStyle.REQUESTED_UPCOMING;
                }
            case CarePayConstants.CANCELLED:
                if(appointmentsPayload.isAppointmentToday()) {
                    return AppointmentDisplayStyle.CANCELED;
                }else{
                    return AppointmentDisplayStyle.CANCELED_UPCOMING;
                }
            case CarePayConstants.PENDING:
            case CarePayConstants.CHECKING_IN:
            default: {
                if(appointmentsPayload.isAppointmentToday()) {
                    if(appointmentsPayload.isAppointmentOver()){
                        return AppointmentDisplayStyle.MISSED;
                    }
                    return AppointmentDisplayStyle.PENDING;
                }
                return AppointmentDisplayStyle.PENDING_UPCOMING;
            }
        }
    }

    private void cleanupViews(ViewHolder holder){
        holder.checkOutButton.setVisibility(View.GONE);
        holder.todayTimeLayout.setVisibility(View.GONE);
        holder.upcomingDateLayout.setVisibility(View.GONE);
        holder.missedAppointmentTextView.setVisibility(View.GONE);
        holder.cellAvatar.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(null);//need to remove this for header just in case
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
        TextView missedAppointmentTextView;

        View upcomingDateLayout;
        TextView upcomingDateTextView;
        TextView upcomingMonthTextView;
        TextView upcomingTimeTextView;

        Button checkOutButton;

        View listItemDivider;

        ViewHolder(View itemView) {
            super(itemView);
            //Header view
            sectionHeaderTitle = (TextView) itemView.findViewById(R.id.appointments_section_header_title);
            if(sectionHeaderTitle != null){//skip looking up all the other views which will be null anyway
                return;
            }

            //Appointment Views
            cellAvatar = (ImageView) itemView.findViewById(R.id.cellAvatarImageView);
            profileImage = (ImageView) itemView.findViewById(R.id.providerPicImageView);
            initials = (TextView) itemView.findViewById(R.id.avatarTextView);
            doctorName = (TextView) itemView.findViewById(R.id.doctor_name);
            doctorType = (TextView) itemView.findViewById(R.id.doctor_type);

            // Today
            todayTimeLayout = itemView.findViewById(R.id.todayTimeLayout);
            todayTimeTextView = (TextView) itemView.findViewById(R.id.todayTimeTextView);

            // Missed
            missedAppointmentTextView = (TextView) itemView.findViewById(R.id.missed_appointment_text_view);

            // Upcoming
            upcomingDateLayout = itemView.findViewById(R.id.upcomingDateLayout);
            upcomingDateTextView = (TextView) itemView.findViewById(R.id.upcomingDateTextView);
            upcomingMonthTextView = (TextView) itemView.findViewById(R.id.upcomingMonthTextView);
            upcomingTimeTextView = (TextView) itemView.findViewById(R.id.upcomingTimeTextView);

            //Check-Out
            checkOutButton = (Button) itemView.findViewById(R.id.check_out_button);

            listItemDivider = itemView.findViewById(R.id.appointment_list_item_divider);

        }

    }

}
