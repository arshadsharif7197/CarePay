package com.carecloud.carepayclover.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepayclover.CheckedInActivity;
import com.carecloud.carepayclover.CloverMainActivity;
import com.carecloud.carepayclover.R;
import com.carecloud.carepayclover.RotateActivity;
import com.carecloud.carepaylibray.appointments.models.Appointment;
import com.carecloud.carepaylibray.appointments.models.AppointmentPatientModel;

import java.util.ArrayList;

/**
 * Created by Jahirul on 09/27/15.
 */
public class CheckedInAdapter extends RecyclerView.Adapter<CheckedInAdapter.CartViewHolder> {

    private Context context;

    ArrayList<Appointment> mData;

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
        AppointmentPatientModel patientModel=mMaster.getPayload().getPatient();
        holder.patientNameTextView.setText(patientModel.getFirstName()+" " + patientModel.getLastName());
        holder.patientBalanceTextView.setText("Balance: $20.00");//+ String.format("%.2f",(Integer.parseInt( patientModel.getGenderId())*2)));
        holder.container.setTag(patientModel);
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
        public CartViewHolder(View view) {
            super(view);
            container = view;
            view.setOnClickListener(this);
            patientNameTextView= (TextView) view.findViewById(R.id.patientNameTextView);
            patientBalanceTextView= (TextView) view.findViewById(R.id.patientBalanceTextView);
            paymentTextview= (TextView) view.findViewById(R.id.paymentTextview);
            assistTextview= (TextView) view.findViewById(R.id.assistTextview);
            paymentTextview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewId=view.getId();
            if(viewId==R.id.paymentTextview) {
                Appointment model = (Appointment) view.getRootView().getTag();
                Intent rotateInIntent = new Intent(context, RotateActivity.class);
                rotateInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                rotateInIntent.putExtra("rotate_title","Ready to pay");//Sent!
                rotateInIntent.putExtra("rotate_sub_title","Please flip screen towards patient");//attendant
                rotateInIntent.putExtra("total_pay_balance",20.00);
                rotateInIntent.putExtra("previous_balance",15.00);
                rotateInIntent.putExtra("insurance_co_pay_balance",5.00);
                context.startActivity(rotateInIntent);
            }
        }
    }
}
