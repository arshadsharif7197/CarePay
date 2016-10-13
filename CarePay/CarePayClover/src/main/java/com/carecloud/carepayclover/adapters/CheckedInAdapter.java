package com.carecloud.carepayclover.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepayclover.R;
import com.carecloud.carepayclover.RotateActivity;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentPatientDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jahirul on 09/27/15.
 */
public class CheckedInAdapter extends RecyclerView.Adapter<CheckedInAdapter.CartViewHolder> {

    private Context context;
    private ArrayList<AppointmentDTO> appointmentArrayList;

    /**
     * Constructor.
     *
     * @param context context
     * @param data    list of appointments
     */
    public CheckedInAdapter(Context context, ArrayList<AppointmentDTO> data) {
        this.context = context;
        appointmentArrayList = data;
        System.out.println("size: " + data.size());
    }

    private AppointmentDTO getAppointmentById(String id) {
        for (AppointmentDTO model : appointmentArrayList) {
            if (id.equals(model.getPayload().getId())) {
                return model;
            }
        }
        return null;
    }

    /**
     * Creates view.
     *
     * @param parent   parent view
     * @param viewType view type
     * @return
     */
    public CheckedInAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checked_in_list_item_layout, parent, false);
        CartViewHolder viewHolder = new CartViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CheckedInAdapter.CartViewHolder holder, final int position) {
        AppointmentDTO appointmentItem = appointmentArrayList.get(position);
        AppointmentPatientDTO patientModel = appointmentItem.getPayload().getPatient();
        holder.patientNameTextView.setText(patientModel.getFirstName() + " " + patientModel.getLastName());
        holder.patientBalanceTextView.setText(String.format("Balance: $%.2f", patientModel.getTotalBalance()));
        Picasso.with(context).load(patientModel.getPhoto()).transform(
                new CircleImageTransform()).resize(160, 160).into(holder.patientPicImageView);
        holder.paymentTextview.setTag(patientModel);
        Log.d("PatientPhoto", patientModel.getPhoto());
    }

    @Override
    public int getItemCount() {
        if (appointmentArrayList != null) {
            return appointmentArrayList.size();
        }
        return 0;
    }

    public void setData(ArrayList<AppointmentDTO> appointmentArrayList) {
        this.appointmentArrayList = appointmentArrayList;
    }

    public void clearData() {
        appointmentArrayList.clear();
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View container;
        TextView patientNameTextView;
        TextView patientBalanceTextView;
        TextView paymentTextview;
        TextView assistTextview;
        ImageView patientPicImageView;

        /**
         * Constructor.
         * @param view view
         */
        CartViewHolder(View view) {
            super(view);
            container = view;
            view.setOnClickListener(this);
            patientNameTextView = (TextView) view.findViewById(R.id.patientNameTextView);
            patientBalanceTextView = (TextView) view.findViewById(R.id.patientBalanceTextView);
            paymentTextview = (TextView) view.findViewById(R.id.paymentTextview);
            assistTextview = (TextView) view.findViewById(R.id.assistTextview);
            patientPicImageView = (ImageView) view.findViewById(R.id.patientPicImageView);
            paymentTextview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.paymentTextview) {
                AppointmentPatientDTO model = (AppointmentPatientDTO) view.getTag();
                Intent rotateInIntent = new Intent(context, RotateActivity.class);
                rotateInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                rotateInIntent.putExtra("rotate_title", "Ready to pay");//Sent!
                rotateInIntent.putExtra("rotate_sub_title", "Please flip screen towards patient");//attendant
                rotateInIntent.putExtra("total_pay_balance", model.getTotalBalance());
                rotateInIntent.putExtra("previous_balance", model.getResponsibilityAccount());
                rotateInIntent.putExtra("insurance_co_pay_balance", model.getResponsibilityCopay());
                context.startActivity(rotateInIntent);
            }
        }
    }
}
