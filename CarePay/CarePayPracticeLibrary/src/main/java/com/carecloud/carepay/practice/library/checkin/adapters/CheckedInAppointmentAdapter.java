package com.carecloud.carepay.practice.library.checkin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.CheckInActivity;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.customcomponent.AppointmentStatusCardView;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jahirul Bhuiyan on 10/17/2016.
 */

public class CheckedInAppointmentAdapter extends RecyclerView.Adapter<CheckedInAppointmentAdapter.CartViewHolder> {

    private Context context;
    private ArrayList<AppointmentPayloadDTO> appointmentArrayList;
    private List<PatientBalanceDTO> patientBalances;
    private boolean isWaitingRoom;

    /**
     * Constructor.
     *
     * @param context context
     * @param data    list of appointments
     */
    public CheckedInAppointmentAdapter(Context context, ArrayList<AppointmentPayloadDTO> data,
                                       List<PatientBalanceDTO> patientBalances, boolean isWaitingRoom) {

        this.context = context;
        this.appointmentArrayList = data;
        this.patientBalances = patientBalances;
        this.isWaitingRoom = isWaitingRoom;
    }

    private AppointmentPayloadDTO getAppointmentById(String id) {
        for (AppointmentPayloadDTO model : appointmentArrayList) {
            if (id.equals(model.getId())) {
                return model;
            }
        }
        return null;
    }

    /**
     * Creates view.
     *
     * @param parent   parent view.
     * @param viewType view type
     * @return created view
     */
    public CheckedInAppointmentAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checked_in_list_item_layout, parent, false);
        CartViewHolder viewHolder = new CartViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CheckedInAppointmentAdapter.CartViewHolder holder, final int position) {
        AppointmentPayloadDTO appointmentItem = appointmentArrayList.get(position);
        PatientModel patientModel = appointmentItem.getPatient();
        holder.appointmentStatusCartView.setPatientName(patientModel.getFullName());
        holder.appointmentStatusCartView.setAppointmentId(appointmentItem.getId());

        holder.appointmentStatusCartView.setAmount(getTotalBalance(position));
        //holder.appointmentStatusCartView.setPatientImage(patientModel.getPhoto());
        holder.appointmentStatusCartView.setProviderName(appointmentItem.getProvider().getName());
        holder.appointmentStatusCartView.setAppointmentTime(DateUtil.getInstance().setDateRaw(appointmentItem.getStartTime()).getDate().getTime());
        holder.appointmentStatusCartView.setTag(appointmentItem);
        holder.appointmentStatusCartView.setWaitingRoom(isWaitingRoom);
        holder.appointmentStatusCartView.setShortName(patientModel.getShortName());
        /*Picasso.with(context).load(patientModel.getPhoto()).transform(
                new CircleImageTransform()).resize(160, 160).into(holder.patientPicImageView);
        holder.paymentTextview.setTag(patientModel);
        Log.d("PatientPhoto", patientModel.getPhoto());*/
    }

    private double getTotalBalance(int position) {
        AppointmentPayloadDTO appointmentItem = appointmentArrayList.get(position);
        String id = appointmentItem.getPatient().getPatientId();

        for (PatientBalanceDTO patientBalanceDTO: patientBalances) {
            PendingBalanceDTO pendingBalanceDTO = patientBalanceDTO.getBalances().get(0);
            if (pendingBalanceDTO.getMetadata().getPatientId().equals(id) && !pendingBalanceDTO.getPayload().isEmpty()) {
                return pendingBalanceDTO.getPayload().get(0).getAmount();
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        if (appointmentArrayList != null) {
            return appointmentArrayList.size();
        }
        return 0;
    }

    public void setData(ArrayList<AppointmentPayloadDTO> appointmentArrayList) {
        this.appointmentArrayList = appointmentArrayList;
    }

    public void clearData() {
        appointmentArrayList.clear();
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //View container;
        /*TextView patientNameTextView;
        TextView patientBalanceTextView;
        TextView paymentTextview;
        TextView assistTextview;
        ImageView patientPicImageView;*/
        AppointmentStatusCardView appointmentStatusCartView;
        /**
         * Constructor.
         * @param view view
         */

        CartViewHolder(View view) {
            super(view);
            appointmentStatusCartView= (AppointmentStatusCardView) view;
            appointmentStatusCartView.setOnClickListener(this);
            /*container = view;

            view.setOnClickListener(this);
            patientNameTextView = (TextView) view.findViewById(R.id.patientNameTextView);
            patientBalanceTextView = (TextView) view.findViewById(R.id.patientBalanceTextView);
            paymentTextview = (TextView) view.findViewById(R.id.paymentTextview);
            assistTextview = (TextView) view.findViewById(R.id.assistTextview);
            patientPicImageView = (ImageView) view.findViewById(R.id.patientPicImageView);
            paymentTextview.setOnClickListener(this);*/
        }

        @Override
        public void onClick(View view) {
            ((CheckInActivity)context).onCheckInItemClick((AppointmentPayloadDTO)view.getTag(), isWaitingRoom);
        }

        /*@Override
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
        }*/
    }
}
