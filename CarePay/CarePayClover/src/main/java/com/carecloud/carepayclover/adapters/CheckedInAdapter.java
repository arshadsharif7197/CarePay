package com.carecloud.carepayclover.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepayclover.R;
import com.carecloud.carepayclover.models.AppointmentsCheckedInPayloadAppointmentModel;
import com.carecloud.carepayclover.models.PatientModel;

import java.util.ArrayList;

/**
 * Created by Aditya on 10/20/15.
 */
public class CheckedInAdapter extends RecyclerView.Adapter<CheckedInAdapter.CartViewHolder> {

    private final Context mContext;

    ArrayList<AppointmentsCheckedInPayloadAppointmentModel> mData;

    public CheckedInAdapter(Context context, ArrayList<AppointmentsCheckedInPayloadAppointmentModel> data) {
        mContext = context;
        mData = data;
        System.out.println("size: " + data.size());
    }

    private AppointmentsCheckedInPayloadAppointmentModel getAppointmentById(String id) {
        for (AppointmentsCheckedInPayloadAppointmentModel model : mData) {
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
        AppointmentsCheckedInPayloadAppointmentModel mMaster = mData.get(position);
        PatientModel patientModel=mMaster.getPayload().getPatient();
        holder.patientNameTextView.setText(patientModel.getFirstName()+" " + patientModel.getLastName());
        holder.patientBalanceTextView.setText("Balance: $"+patientModel.getGenderId());
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();

        return 0;
    }

    public void setData(ArrayList<AppointmentsCheckedInPayloadAppointmentModel> _data) {
        this.mData = _data;
    }

    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       // public View container;
        public TextView patientNameTextView,patientBalanceTextView,paymentTextview,assistTextview;
        public CartViewHolder(View view) {
            super(view);
            //container = view;
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
                //AppointmentsCheckedInPayloadAppointmentModel model = (AppointmentsCheckedInPayloadAppointmentModel) view.getRootView().getTag();
            }
        }
    }
}
