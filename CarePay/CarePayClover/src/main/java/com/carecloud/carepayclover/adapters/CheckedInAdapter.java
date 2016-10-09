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
import com.carecloud.carepaylibray.appointments.models.Appointment;
import com.carecloud.carepaylibray.appointments.models.AppointmentPatientDto;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jahirul on 09/27/15.
 */
public class CheckedInAdapter extends RecyclerView.Adapter<CheckedInAdapter.CartViewHolder> {

    private Context context;

    ArrayList<Appointment> mData;
    int counter;

    public CheckedInAdapter(Context context, ArrayList<Appointment> data) {
        this.context = context;
        mData = data;
        System.out.println("size: " + data.size());
    }

    private Appointment getAppointmentById(String id) {
        for (Appointment model : mData) {
            if (id.equals(model.getPayload().getId()))
                return model;
        }
        return null;
    }

    public CheckedInAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checked_in_list_item_layout, parent, false);
        CartViewHolder viewHolder = new CartViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CheckedInAdapter.CartViewHolder holder, final int position) {
        Appointment mMaster = mData.get(position);
        AppointmentPatientDto patientModel=mMaster.getPayload().getPatient();
        holder.patientNameTextView.setText(patientModel.getFirstName()+" " + patientModel.getLastName());
        holder.patientBalanceTextView.setText(String.format( "Balance: $%.2f", patientModel.getTotalBalance() ));
        Picasso.with(context).load(patientModel.getPhoto()).transform(new CircleImageTransform()).resize(160,160).into(holder.patientPicImageView);
        holder.paymentTextview.setTag(patientModel);
        Log.d("PatientPhoto",patientModel.getPhoto());
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();

        return 0;
    }

    public void setData(ArrayList<Appointment> _data) {
        this.mData = _data;
    }

    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View container;
        public TextView patientNameTextView,patientBalanceTextView,paymentTextview,assistTextview;
        ImageView patientPicImageView;
        public CartViewHolder(View view) {
            super(view);
            container = view;
            view.setOnClickListener(this);
            patientNameTextView= (TextView) view.findViewById(R.id.patientNameTextView);
            patientBalanceTextView= (TextView) view.findViewById(R.id.patientBalanceTextView);
            paymentTextview= (TextView) view.findViewById(R.id.paymentTextview);
            assistTextview= (TextView) view.findViewById(R.id.assistTextview);
            patientPicImageView= (ImageView) view.findViewById(R.id.patientPicImageView);
            paymentTextview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewId=view.getId();
            if(viewId==R.id.paymentTextview) {
                AppointmentPatientDto model = (AppointmentPatientDto) view.getTag();
                Intent rotateInIntent = new Intent(context, RotateActivity.class);
                rotateInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                rotateInIntent.putExtra("rotate_title","Ready to pay");//Sent!
                rotateInIntent.putExtra("rotate_sub_title","Please flip screen towards patient");//attendant
                rotateInIntent.putExtra("total_pay_balance",model.getTotalBalance() );
                rotateInIntent.putExtra("previous_balance", model.getResponsibilityAccount());
                rotateInIntent.putExtra("insurance_co_pay_balance",model.getResponsibilityCopay());
                context.startActivity(rotateInIntent);
            }
        }
    }
}
