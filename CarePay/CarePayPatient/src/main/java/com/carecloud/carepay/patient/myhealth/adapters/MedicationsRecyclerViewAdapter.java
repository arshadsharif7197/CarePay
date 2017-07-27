package com.carecloud.carepay.patient.myhealth.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.MedicationDto;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthMainFragment;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthDataInterface;
import com.carecloud.carepay.service.library.label.Label;

import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */

public class MedicationsRecyclerViewAdapter extends RecyclerView.Adapter<MedicationsRecyclerViewAdapter.ViewHolder> {


    private final List<MedicationDto> medications;
    private final int maxItems;
    private MyHealthDataInterface callback;

    /**
     * @param medications a list of medications
     * @param maxItems    the max number of items to show
     */
    public MedicationsRecyclerViewAdapter(List<MedicationDto> medications, int maxItems) {
        this.medications = medications;
        this.maxItems = maxItems;
    }

    public void setCallback(MyHealthDataInterface callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MyHealthMainFragment.MAX_ITEMS_TO_SHOW) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_health_action, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_health, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if ((position < maxItems) && (position < medications.size())) {
            final MedicationDto medication = medications.get(position);
            holder.medicationNameTextView.setText(medication.getDrugName());
            holder.frequencyTextView.setText(medication.getFrequencyDescription());
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onMedicationClicked(medication);
                }
            });
        } else {
            holder.myHealthActionButton.setText(Label.getLabel("my_health_add_medication_button_label"));
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.addMedication();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return maxItems == MyHealthMainFragment.MAX_ITEMS_TO_SHOW ?
                Math.min(medications.size() + 1, maxItems + 1) : medications.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == maxItems) || ((medications.size()) == position)) {
            return MyHealthMainFragment.MAX_ITEMS_TO_SHOW;
        }
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView myHealthActionButton;
        TextView medicationNameTextView;
        TextView frequencyTextView;
        ViewGroup row;

        public ViewHolder(View itemView) {
            super(itemView);
            medicationNameTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            frequencyTextView = (TextView) itemView.findViewById(R.id.subTitleTextView);
            row = (ViewGroup) itemView.findViewById(R.id.row);
            myHealthActionButton = (TextView) itemView.findViewById(R.id.myHealthActionButton);
        }
    }
}
