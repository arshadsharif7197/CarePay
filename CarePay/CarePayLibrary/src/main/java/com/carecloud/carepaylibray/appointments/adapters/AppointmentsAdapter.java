package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeader;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.customdialogs.CheckInOfficeNowAppointmentDialog;
import com.carecloud.carepaylibray.customdialogs.PendingAppointmentRequestDialog;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;

/**
 * Created by harshal_patil on 9/8/2016.
 */
public class AppointmentsAdapter extends RecyclerView.Adapter <AppointmentsAdapter.AppointmentViewHolder> {

    private static Context context;
    private ArrayList <Object> appointmentItems;
    private AppointmentsListFragment appointmentsListFragment;

    public AppointmentsAdapter(Context context, ArrayList <Object> appointmentItems, AppointmentsListFragment appointmentsListFragment) {
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
            holder.doctorType.setText(item.getAppointmentType());
            SystemUtil.setGothamRoundedMediumTypeface(context,holder.shortName);

            String splitStr[]= item.getAppointmentTime().replaceAll("UTC","").split(" ");
            if (splitStr.length > 3) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(splitStr[0] + "\n" + splitStr[1] + "\n" + splitStr[2] + " " + splitStr[3]);
                Spannable span = new SpannableString(stringBuilder);
                //span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new RelativeSizeSpan(1.75f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.Feldgrau)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SystemUtil.setProximaNovaRegularTypeface(context, holder.time);
                holder.time.setText(span);
                holder.time.setTextColor(ContextCompat.getColor(context, R.color.Feldgrau));
            } else {
                holder.time.setText(item.getAppointmentTime().replaceAll("UTC", ""));
                holder.time.setTextColor(ContextCompat.getColor(context, R.color.dark_green));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Restricted the appointment list item click if it is appointment header type.*/
                    if (object.getClass() == AppointmentModel.class) {
                        AppointmentModel item = (AppointmentModel) object;
                        if (item.isPending()) {
                            new PendingAppointmentRequestDialog(context, item).show();
                        } else {
                            new CheckInOfficeNowAppointmentDialog(context, item).show();
                        }
                    }
                }
            });
            holder.shortName.setText(SystemUtil.onShortDrName(item.getDoctorName()));

            if (item.isPending()) {
                holder.cellAvtar.setVisibility(View.VISIBLE);
            } else {
                holder.cellAvtar.setVisibility(View.INVISIBLE);
            }

        } else {
            AppointmentSectionHeader item = (AppointmentSectionHeader) object;
            if(position == 0) {
                holder.appointmentSectionLinearLayout.setVisibility(View.GONE);
                holder.appointmentItemLinearLayout.setVisibility(View.GONE);
                CustomProxyNovaSemiBoldLabel appointmentStickyHearderTitle = (CustomProxyNovaSemiBoldLabel) view.findViewById(R.id.appointments_sticky_header_title);
                appointmentStickyHearderTitle.setText(item.getAppointmentHeader());
            } else {
                holder.appointmentSectionLinearLayout.setVisibility(View.VISIBLE);
                holder.appointmentItemLinearLayout.setVisibility(View.GONE);
                holder.appointmentSectionHeaderTitle.setText(item.getAppointmentHeader());
            }
        }

        /*OnScrollListener for RecyclerView to get first visible element in the list
            and then change the sticky header accordingly*/
        final RecyclerView appointmentRecyclerView = (RecyclerView)  view.findViewById(R.id.appointments_recycler_view);
        appointmentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager mLayoutManager = (LinearLayoutManager) appointmentRecyclerView.getLayoutManager();
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                Object object = appointmentItems.get(firstVisibleItemPosition);
                if (object.getClass() == AppointmentModel.class) {
                    final AppointmentModel item = (AppointmentModel) object;
                    View view = appointmentsListFragment.getView();
                    CustomProxyNovaSemiBoldLabel appointmentStickyHearderTitle = (CustomProxyNovaSemiBoldLabel) view.findViewById(R.id.appointments_sticky_header_title);
                    appointmentStickyHearderTitle.setText(item.getAppointmentHeader());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentItems.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private TextView doctorName, doctorType, time, shortName;
        private CustomProxyNovaSemiBoldLabel appointmentSectionHeaderTitle;
        private ImageView cellAvtar;
        private LinearLayout appointmentSectionLinearLayout, appointmentItemLinearLayout;

    AppointmentViewHolder(View itemView) {
        super(itemView);
        Typeface textViewFont_proximanova_semibold = Typeface.createFromAsset(itemView.getResources().getAssets(), "fonts/proximanova_semibold.otf");
        doctorName = (TextView) itemView.findViewById(R.id.doctor_name);
        doctorName.setTypeface(textViewFont_proximanova_semibold);
        Typeface textViewFont_proximanova_regular = Typeface.createFromAsset(itemView.getResources().getAssets(), "fonts/proximanova_regular.otf");
        doctorType = (TextView) itemView.findViewById(R.id.doctor_type);
        doctorType.setTypeface(textViewFont_proximanova_regular);
        Typeface textViewFont_gotham_rounded_bold = Typeface.createFromAsset(itemView.getResources().getAssets(), "fonts/gotham_rounded_bold.otf");
        time = (TextView) itemView.findViewById(R.id.time);
        time.setTypeface(textViewFont_gotham_rounded_bold);
        shortName=(TextView)itemView.findViewById(R.id.avtarTextView);
        cellAvtar =(ImageView) itemView.findViewById(R.id.cellAvtarImageView);

        appointmentSectionLinearLayout = (LinearLayout) itemView.findViewById(R.id.appointment_section_linear_layout);
        appointmentItemLinearLayout = (LinearLayout) itemView.findViewById(R.id.appointment_item_linear_layout);
        appointmentSectionHeaderTitle = (CustomProxyNovaSemiBoldLabel) itemView.findViewById(R.id.appointments_section_header_title);
    }
  }
}