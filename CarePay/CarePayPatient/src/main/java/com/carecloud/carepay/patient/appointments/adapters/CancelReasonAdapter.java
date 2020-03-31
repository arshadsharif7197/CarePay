package com.carecloud.carepay.patient.appointments.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.appointments.models.CancellationReasonDTO;

import java.util.List;

/**
 * @author pjohnson on 2020-02-10.
 */
public class CancelReasonAdapter extends RecyclerView.Adapter<CancelReasonAdapter.ViewHolder> {

    private final List<CancellationReasonDTO> cancellationReasons;
    private final CancelReasonItemInterface callback;
    private RadioButton selectedOptionView;

    public CancelReasonAdapter(List<CancellationReasonDTO> cancellationReasons,
                               CancelReasonItemInterface callback) {
        this.cancellationReasons = cancellationReasons;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cancel_appointment_reason_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CancellationReasonDTO reasonDTO = cancellationReasons.get(position);
        holder.cancelAppointmentRadioButton.setText(reasonDTO.getAppointmentCancellationReason().getName());
        holder.cancelAppointmentRadioButton.setOnClickListener(v -> {
            if (selectedOptionView != null) {
                selectedOptionView.setChecked(false);
            }
            selectedOptionView = holder.cancelAppointmentRadioButton;
            selectedOptionView.setChecked(true);
            callback.onCancelReasonSelected(reasonDTO);
        });
    }

    @Override
    public int getItemCount() {
        return cancellationReasons.size();
    }

    public interface CancelReasonItemInterface {
        void onCancelReasonSelected(CancellationReasonDTO reasonDTO);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatRadioButton cancelAppointmentRadioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cancelAppointmentRadioButton = itemView.findViewById(R.id.cancelAppointmentRadioButton);
        }
    }
}
