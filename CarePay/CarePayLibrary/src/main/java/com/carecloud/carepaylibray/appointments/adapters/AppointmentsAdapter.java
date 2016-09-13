package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.utils.Utility;

import java.util.ArrayList;

/**
 * Created by harshal_patil on 9/8/2016.
 */
public class AppointmentsAdapter extends RecyclerView.Adapter <AppointmentsAdapter.AppointmentViewHolder> {

        Context mContext;

        ArrayList <AppointmentModel> mAppointmentItems;
        ArrayList<Integer> mSectionHeaderIndices;

    public AppointmentsAdapter(Context context, ArrayList <AppointmentModel> appointmentItems) {
        mContext = context;
        mAppointmentItems = appointmentItems;
        initSectionHeaders();
    }
    public AppointmentsAdapter(ArrayList<AppointmentModel> appointmentItems) {
        this.mAppointmentItems = appointmentItems;

    }
    void initSectionHeaders() {
        mSectionHeaderIndices = new ArrayList<>();
        if (mAppointmentItems.isEmpty()) return;

        String tempText = "";
        int index = 0;
        for (AppointmentModel aptItem : mAppointmentItems) {
            String doctorName = aptItem.getDoctorName();
            if (doctorName != null && !doctorName.equalsIgnoreCase(tempText)) {
                mSectionHeaderIndices.add(index);
                tempText = doctorName;
            }
            index++;
        }
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.appointment_list_item, parent, false);
        return new AppointmentViewHolder(view);
       }

    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, final int position) {
        final AppointmentModel item = mAppointmentItems.get(position);
        holder.doctorName.setText(item.getDoctorName());
        holder.doctorType.setText(item.getAptType());

        String splitStr[]= item.getAptTime().replaceAll("UTC","").split(" ");
        String htmlStr="";
        if(splitStr.length > 3) {
             htmlStr ="<div style=\"text-align: center;color: #90a4ae;font-weight: normal;\"><span style=\"color: #455a64;\"><b>"+splitStr[0]+"</b></span><br>"+splitStr[1]+"<br><span style=\"border-top: 1px solid #90a4ae;\">"+splitStr[2]+" "+splitStr[3]+"</span></div>";
            holder.time.setText(Html.fromHtml(htmlStr));
        }else{
            holder.time.setText(item.getAptTime().replaceAll("UTC",""));
            holder.time.setTextColor(mContext.getResources().getColor(R.color.dark_green));
        }
        //bindSectionHeader(holder, position, item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((AppointmentsActivity)mContext).showAppointmentsDialog(item);
           // Toast.makeText(mContext,"Clicked "+item.getAptId(),Toast.LENGTH_LONG).show();
         }
    });
        holder.shortName.setText(Utility.onShortDrName(item.getDoctorName()));
    }

    @Override
    public int getItemCount() {
        return mAppointmentItems.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {

        TextView doctorName, doctorType, time, sectionText,shortName;
        View sectionHeader, divider;

    public AppointmentViewHolder(View itemView) {
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
        shortName=(TextView)itemView.findViewById(R.id.short_textview);

    }
}
}