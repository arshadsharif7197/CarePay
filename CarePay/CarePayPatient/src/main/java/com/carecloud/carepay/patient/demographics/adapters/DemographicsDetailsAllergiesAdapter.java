package com.carecloud.carepay.patient.demographics.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import java.util.List;

/**
 * Created by lsoco_user on 10/11/2016.
 * Adapter for the recycler view holding the allergies.
 */
public class DemographicsDetailsAllergiesAdapter
        extends RecyclerView.Adapter<DemographicsDetailsAllergiesAdapter.AllergyViewHolder> {

    // for now a unique hardcoded metadata DTO (later a list fetched from b/e)
    private final AllergyMetadataDTO metadataDTO = new AllergyMetadataDTO("Category",
                                                                          "Allergy",
                                                                          "Severity",
                                                                          "Reaction",
                                                                          "Remove");
    private List<AllergyPayloadDTO> items;

    public DemographicsDetailsAllergiesAdapter(List<AllergyPayloadDTO> items) {
        this.items = items;
    }

    @Override
    public AllergyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_allergy, parent, false);
        return new AllergyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllergyViewHolder holder, int position) {
        // populate the view (with both metadata and data)
        holder.categoryLabel.setText(metadataDTO.categoryLabel);
        holder.allergyLabel.setText(metadataDTO.allergyLabel);
        holder.severityLabel.setText(metadataDTO.severityLabel);
        holder.reactionLabel.setText(metadataDTO.reactionLabel);
        holder.removeTextView.setText(metadataDTO.removeLabel);

        AllergyPayloadDTO data = items.get(position);
        holder.categoryTextView.setText(data.category);
        holder.allergyTextView.setText(data.allergy);
        holder.severityTextView.setText(data.severity);
        holder.reactionTextView.setText(data.reaction);

        holder.setClickListener(new OnAllergyItemClickListener(position));
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
     * Adds an allergy item in the list
     * @param newItem A new item to be added
     */
    public void addAtFront(AllergyPayloadDTO newItem) {
        items.add(0, newItem);
        notifyDataSetChanged();
        notifyItemInserted(0);
    }

    /**
     * View holder for the allergies
     */
    static class AllergyViewHolder extends RecyclerView.ViewHolder {

        View                       allergyView;
        TextView                   categoryLabel;
        TextView                   allergyLabel;
        TextView                   severityLabel;
        TextView                   reactionLabel;
        TextView                   categoryTextView;
        TextView                   allergyTextView;
        TextView                   severityTextView;
        TextView                   reactionTextView;
        TextView                   removeTextView;
        OnAllergyItemClickListener clickListener;

        AllergyViewHolder(View itemView) {
            super(itemView);
            allergyView = itemView;

            categoryLabel = (TextView) allergyView.findViewById(R.id.viewAllergyCategoryLabel);
            allergyLabel = (TextView) allergyView.findViewById(R.id.viewAllergyLabel);
            severityLabel = (TextView) allergyView.findViewById(R.id.viewAllergySeverityLabel);
            reactionLabel = (TextView) allergyView.findViewById(R.id.viewAllergyReactionLabel);

            categoryTextView = (TextView) allergyView.findViewById(R.id.viewAllergyCategoryTextView);
            allergyTextView = (TextView) allergyView.findViewById(R.id.viewAllergyTextView);
            severityTextView = (TextView) allergyView.findViewById(R.id.viewAllergySeverityTextView);
            reactionTextView = (TextView) allergyView.findViewById(R.id.viewAllergyReactionTextView);

            removeTextView = (TextView) allergyView.findViewById(R.id.viewAllergyRemove);

            setTypefaces();
        }

        private void setTypefaces() {
            Context context = itemView.getContext();

            setProximaNovaExtraboldTypeface(context, categoryLabel);
            setProximaNovaExtraboldTypeface(context, allergyLabel);
            setProximaNovaExtraboldTypeface(context, severityLabel);
            setProximaNovaExtraboldTypeface(context, reactionLabel);
            setProximaNovaSemiboldTypeface(context, removeTextView);

            setProximaNovaRegularTypeface(context, categoryTextView);
            setProximaNovaRegularTypeface(context, allergyTextView);
            setProximaNovaRegularTypeface(context, severityTextView);
            setProximaNovaRegularTypeface(context, reactionTextView);
        }

        void setClickListener(OnAllergyItemClickListener clickListener) {
            this.clickListener = clickListener;
            removeTextView.setOnClickListener(clickListener);
        }
    }

    /**
     * Data object for allergy
     */
    public static class AllergyPayloadDTO {

        String category;
        String allergy;
        String severity;
        String reaction;

        /**
         * Ctor for allergy DTO
         * @param category The allergy category.
         * @param allergy The allergy name.
         * @param severity The severity.
         * @param reaction The reaction.
         */
        public AllergyPayloadDTO(String category,
                                 String allergy,
                                 String severity,
                                 String reaction) {
            this.category = category;
            this.allergy = allergy;
            this.severity = severity;
            this.reaction = reaction;
        }
    }

    /**
     * Meta-data object for allergy
     */
    private static class AllergyMetadataDTO {

        String categoryLabel;
        String allergyLabel;
        String severityLabel;
        String reactionLabel;
        String removeLabel;

        AllergyMetadataDTO(String categoryLabel,
                           String allergyLabel,
                           String severityLabel,
                           String reactionLabel,
                           String removeLabel) {
            this.categoryLabel = categoryLabel;
            this.allergyLabel = allergyLabel;
            this.severityLabel = severityLabel;
            this.reactionLabel = reactionLabel;
            this.removeLabel = removeLabel;
        }
    }

    /**
     * Custom OnClickListener that accept the item position as parameter.
     */
    private class OnAllergyItemClickListener implements View.OnClickListener {

        private int position;

        OnAllergyItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            removeAt(position);
        }
    }
}