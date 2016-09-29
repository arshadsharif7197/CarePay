package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.customdialogs.CheckInOfficeNowAppointmentDialog;
import com.carecloud.carepaylibray.customdialogs.QueueAppointmentDialog;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;

/**
 * Created by harshal_patil on 9/8/2016.
 */
public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

    private Context context;
    private ArrayList<Object> appointmentItems;
    private AppointmentsListFragment appointmentsListFragment;

    public AppointmentsAdapter(Context context, ArrayList<Object> appointmentItems,
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

        if (object.getClass() == AppointmentModel.class) {
            holder.appointmentSectionLinearLayout.setVisibility(View.GONE);
            holder.appointmentItemLinearLayout.setVisibility(View.VISIBLE);

            final AppointmentModel item = (AppointmentModel) object;

            holder.doctorName.setText(item.getDoctorName());
            SystemUtil.setProximaNovaSemiboldTypeface(context, holder.doctorName);

            holder.doctorType.setText(item.getAppointmentType());
            SystemUtil.setProximaNovaRegularTypeface(context, holder.doctorType);

            String splitStr[] = item.getAppointmentTime().split(" ");
            if (splitStr.length > 3) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(splitStr[0] + "\n" + splitStr[1].toUpperCase() + "\n" + splitStr[2] + " " + splitStr[3]);

                Spannable span = new SpannableString(stringBuilder);
                span.setSpan(new RelativeSizeSpan(1.75f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.Feldgrau)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SystemUtil.setProximaNovaRegularTypeface(context, holder.time);

                if (item.isCheckedIn()) {
                    holder.time.setText(context.getString(R.string.checked_in_label));
                    holder.time.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
                } else {
                    holder.time.setText(span);
                    holder.time.setTextColor(ContextCompat.getColor(context, R.color.Feldgrau));
                }
            } else {
                if (item.isCheckedIn()) {
                    holder.time.setText(context.getString(R.string.checked_in_label));
                    holder.time.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
                } else {
                    holder.time.setText(item.getAppointmentTime());
                    holder.time.setTextColor(ContextCompat.getColor(context, R.color.dark_green));
                }
            }

            SystemUtil.setProximaNovaRegularTypeface(context, holder.time);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Restricted the appointment list item click if it is appointment header type.*/
                    if (object.getClass() == AppointmentModel.class) {
                        AppointmentModel item = (AppointmentModel) object;
                        if (item.isPending()) {
                            new CheckInOfficeNowAppointmentDialog(context, item).show();
                        } else if (item.isCheckedIn()) {
                            new QueueAppointmentDialog(context, item).show();
                        } else {
                            // appointment clicked item saved so that it can be used on Payment
                            AppointmentsActivity.model = item;
                            new CheckInOfficeNowAppointmentDialog(context, item).show();
                        }
                    }
                }
            });
            holder.shortName.setText(SystemUtil.onShortDrName(item.getDoctorName()));

            if (item.isPending()) {
                holder.cellAvatar.setVisibility(View.INVISIBLE);
            } else {
                holder.cellAvatar.setVisibility(View.INVISIBLE);
            }

            if (item.isCheckedIn()) {
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
                }
            } else {
                holder.appointmentSectionLinearLayout.setVisibility(View.VISIBLE);
                holder.appointmentItemLinearLayout.setVisibility(View.GONE);
                holder.appointmentSectionHeaderTitle.setText(item.getAppointmentHeader());
            }
        }

        /*OnScrollListener for RecyclerView to get first visible element in the list
            and then change the sticky header accordingly*/
        if (view != null) {
            final RecyclerView appointmentRecyclerView = (RecyclerView) view.findViewById(R.id.appointments_recycler_view);
            appointmentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    LinearLayoutManager mLayoutManager = (LinearLayoutManager) appointmentRecyclerView.getLayoutManager();
                    int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

                    Object object = appointmentItems.get(firstVisibleItemPosition);
                    if (object.getClass() == AppointmentModel.class) {
                        final AppointmentModel item = (AppointmentModel) object;
                        View view = appointmentsListFragment.getView();

                        if (view != null) {
                            CustomProxyNovaSemiBoldLabel appointmentStickyHeaderTitle =
                                    (CustomProxyNovaSemiBoldLabel) view.findViewById(R.id.appointments_sticky_header_title);

                            if (item.isCheckedIn()) {
                                appointmentStickyHeaderTitle.setVisibility(View.GONE);
                            } else {
                                appointmentStickyHeaderTitle.setText(item.getAppointmentHeader());
                                appointmentStickyHeaderTitle.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return appointmentItems.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private TextView doctorName, doctorType, time, shortName;
        private CustomProxyNovaSemiBoldLabel appointmentSectionHeaderTitle;
        private ImageView cellAvatar;
        private LinearLayout appointmentSectionLinearLayout, appointmentItemLinearLayout;

        AppointmentViewHolder(View itemView) {
            super(itemView);
            doctorName = (TextView) itemView.findViewById(R.id.doctor_name);
            doctorType = (TextView) itemView.findViewById(R.id.doctor_type);
            time = (TextView) itemView.findViewById(R.id.time);

            shortName = (TextView) itemView.findViewById(R.id.avtarTextView);
            cellAvatar = (ImageView) itemView.findViewById(R.id.cellAvtarImageView);

            appointmentSectionLinearLayout = (LinearLayout) itemView.findViewById(R.id.appointment_section_linear_layout);
            appointmentItemLinearLayout = (LinearLayout) itemView.findViewById(R.id.appointment_item_linear_layout);
            appointmentSectionHeaderTitle = (CustomProxyNovaSemiBoldLabel) itemView.findViewById(R.id.appointments_section_header_title);
        }
    }
}