package com.carecloud.carepaylibray.demographics.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 10/12/2016.
 * Adapter for medications in demographics details.
 */
public class DemographicsDetailsMedicationsAdapter
        extends RecyclerView.Adapter<DemographicsDetailsMedicationsAdapter.MedicationViewHolder> {

    // for now use a unique metadata for all items
    private final MedicationMetadataDTO medicationMetadata
            = new MedicationMetadataDTO("Medication", "Dosage", "Remove");
    private List<MedicationPayloadDTO> items;

    public DemographicsDetailsMedicationsAdapter(List<MedicationPayloadDTO> items) {
        this.items = items;
    }

    @Override
    public MedicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicationViewHolder holder, int position) {
        // apply the metadata (ie labels)
        holder.medicationLabel.setText(medicationMetadata.medicationLabel);
        holder.dosageLabel.setText(medicationMetadata.dosageLabel);
        holder.removeClickable.setText(medicationMetadata.removeLabel);

        MedicationPayloadDTO data = items.get(position);
        // populate views with data
        holder.medicationTextView.setText(data.medication);
        holder.dosageTextView.setText(data.dosage);
        holder.setClickListener(new OnMedicationItemClickListener(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void removeAt(int position) {
        items.remove(position);
        notifyDataSetChanged();
        notifyItemRemoved(position);
    }

    /**
     * Add a medication item
     * @param item The item.
     */
    public void addAtFront(MedicationPayloadDTO item) {
        items.add(0, item);
        notifyDataSetChanged();
        notifyItemInserted(0);
    }

    /**
     * View holder for medication.
     */
    static class MedicationViewHolder extends RecyclerView.ViewHolder {

        View     parentView;
        TextView medicationLabel;
        TextView medicationTextView;
        TextView dosageLabel;
        TextView dosageTextView;
        TextView removeClickable;

        MedicationViewHolder(View itemView) {
            super(itemView);
            this.parentView = itemView;

            medicationLabel = (TextView) itemView.findViewById(R.id.viewMedicLabel);
            medicationTextView = (TextView) itemView.findViewById(R.id.viewMedicTextView);
            dosageLabel = (TextView) itemView.findViewById(R.id.viewMedicDosageLabel);
            dosageTextView = (TextView) itemView.findViewById(R.id.viewMedicDosageTextView);
            removeClickable = (TextView) itemView.findViewById(R.id.viewMedicRemove);

            setTypefaces();
        }

        private void setTypefaces() {
            Context context = parentView.getContext();
            setProximaNovaExtraboldTypeface(context, medicationLabel);
            setProximaNovaExtraboldTypeface(context, dosageLabel);
            setProximaNovaSemiboldTypeface(context, removeClickable);
        }

        void setClickListener(OnMedicationItemClickListener clickListener) {
            removeClickable.setOnClickListener(clickListener);
        }
    }

    /**
     * Payload for medication.
     */
    public static class MedicationPayloadDTO {

        String medication;
        String dosage;

        public MedicationPayloadDTO(String medication, String dosage) {
            this.medication = medication;
            this.dosage = dosage;
        }
    }

    /**
     * Metadata for medication.
     */
    private static class MedicationMetadataDTO {

        String medicationLabel;
        String dosageLabel;
        String removeLabel;

        MedicationMetadataDTO(String medicationLabel, String dosageLabel, String removeLabel) {
            this.medicationLabel = medicationLabel;
            this.dosageLabel = dosageLabel;
            this.removeLabel = removeLabel;
        }
    }

    /**
     * Custom OnClickListener that accept the item position as parameter.
     */
    private class OnMedicationItemClickListener implements View.OnClickListener {

        private int position;

        OnMedicationItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            removeAt(position);
        }
    }
}