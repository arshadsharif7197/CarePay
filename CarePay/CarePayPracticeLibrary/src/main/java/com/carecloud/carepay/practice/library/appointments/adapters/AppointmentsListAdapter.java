package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedBoldLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaExtraBold;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Picasso;

import java.util.List;
import org.apache.commons.lang3.StringUtils;





/**
 * Created by harshal_patil on 10/19/2016.
 */

public class AppointmentsListAdapter extends RecyclerView.Adapter<AppointmentsListAdapter.AppointmentsListViewHolder> {

    private Context context;
    private List<com.carecloud.carepaylibray.appointments.models.AppointmentDTO> appointmentsArrayList;
    private AppointmentLabelDTO appointmentLabels;

    /**
     * This will create a list of appointments
     * @param context context
     * @param appointmentsArrayList appointmentsArrayList
     */
    public AppointmentsListAdapter(Context context, List<com.carecloud.carepaylibray.appointments.models.AppointmentDTO> appointmentsArrayList, AppointmentsResultModel appointmentInfo) {

        this.context = context;
        this.appointmentsArrayList = appointmentsArrayList;
        this.appointmentLabels = appointmentInfo.getMetadata().getLabel();
    }

    @Override
    public AppointmentsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View appointmentsListItemView = LayoutInflater.from(context).inflate(
                R.layout.appointment_card, parent, false);
        return new AppointmentsListViewHolder(appointmentsListItemView);
    }

    @Override
    public void onBindViewHolder(final AppointmentsListViewHolder holder, int position) {
        final Object object = appointmentsArrayList.get(position);
        final AppointmentsPayloadDTO item = ((AppointmentDTO) object).getPayload();
        holder.doctorName.setText(item.getProvider().getName());
        holder.doctorType.setText(item.getProvider().getSpecialty().getName());
        holder.appointmentLocation.setText(item.getLocation().getName());
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        DateUtil.getInstance().setDateRaw(item.getStartTime());
        holder.appointmentDate.setText(DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal());
        String startDay = StringUtils.substringBefore(DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal(), ",");
        String endDay =DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal()
                .substring(DateUtil.getInstance().getDateAsDayMonthDayOrdinal().indexOf(","));
        String strToday = startDay.replace(startDay, "Today")+ endDay ;
        holder.appointmentDate.setText(strToday);
        holder.appointmentTime.setText(DateUtil.getInstance().getTime12Hour());
        holder.startCheckInTextview.setText(StringUtil.getLabelForView(
                appointmentLabels.getAppointmentsPracticeCheckin()));
        if(!item.getAppointmentStatusModel().getName().equals("Pending"))
        {
            GradientDrawable shape =  new GradientDrawable();
            //shape.setShape(GradientDrawable.OVAL);
            shape.setCornerRadius(50.0f);
            shape.setColor(Color.LTGRAY);
            if(Build.VERSION.SDK_INT>=16) {
                holder.startCheckInTextview.setBackground(shape);
            }else{
                holder.startCheckInTextview.setBackgroundDrawable(shape);
            }
            holder.startCheckInTextview.setBackground(shape);
            holder.startCheckInTextview.setClickable(false);
        }
        String photoUrl = item.getProvider().getPhoto();
        if (TextUtils.isEmpty(photoUrl)) {
            holder.shortNameTextview.setText(StringUtil.onShortDrName(item.getProvider().getName()));
        } else {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    holder.shortNameTextview.setText(StringUtil.onShortDrName(item.getProvider().getName()));
                }
            });

            builder.build().load(photoUrl).transform(new CircleImageTransform())
                    .resize(58, 58).into(holder.profileImage);
            holder.profileImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentsArrayList.size();
    }

    class AppointmentsListViewHolder extends RecyclerView.ViewHolder {

        private TextView shortNameTextview;
        private TextView startCheckInTextview;
        private CustomProxyNovaSemiBoldLabel doctorName;
        private CustomProxyNovaRegularLabel doctorType;
        private CustomGothamRoundedBoldLabel appointmentLocation;
        private CustomProxyNovaExtraBold appointmentDate;
        private CustomGothamRoundedBoldLabel appointmentTime;
        //private ImageView cellAvatar;
        private ImageView profileImage;

        AppointmentsListViewHolder(View itemView) {
            super(itemView);

            doctorName = (CustomProxyNovaSemiBoldLabel) itemView.findViewById(R.id.appointmentNameTextView);
            doctorType = (CustomProxyNovaRegularLabel) itemView.findViewById(R.id.appointmentTypeTextView);
            shortNameTextview = (TextView) itemView.findViewById(R.id.appointmentShortnameTextView);
            startCheckInTextview = (TextView) itemView.findViewById(R.id.checkInTextview);
            appointmentLocation = (CustomGothamRoundedBoldLabel) itemView.findViewById(R.id.appointmentLocationTextview);
            appointmentDate = (CustomProxyNovaExtraBold) itemView.findViewById(R.id.appointmentDateTextView);
            appointmentTime = (CustomGothamRoundedBoldLabel) itemView.findViewById(R.id.appointmentTimeTextView);
            //cellAvatar = (ImageView) itemView.findViewById(R.id.cellAvatarImageView);
            profileImage = (ImageView) itemView.findViewById(R.id.appointUserPicImageView);

        }
    }

}

