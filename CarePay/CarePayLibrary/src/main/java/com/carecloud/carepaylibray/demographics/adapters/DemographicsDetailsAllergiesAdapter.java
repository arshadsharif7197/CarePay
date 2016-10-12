package com.carecloud.carepaylibray.demographics.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

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
                                                                          "Reaction");
    private List<AllergyPayloadDTO> items;

    public DemographicsDetailsAllergiesAdapter(List<AllergyPayloadDTO> items) {
        this.items = items;
    }

    @Override
    public AllergyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_allergy, parent, false);
        return new AllergyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AllergyViewHolder holder, int position) {
        AllergyPayloadDTO data = items.get(position);
        // populate the view (with both metadata and data)
        holder.categoryLabel.setText(metadataDTO.categoryLabel);
        holder.allergyLabel.setText(metadataDTO.allergyLabel);
        holder.severityTextView.setText(metadataDTO.reactionLabel);
        holder.reactionLabel.setText(metadataDTO.reactionLabel);

        holder.categoryTextView.setText(data.category);
        holder.allergyTextView.setText(data.allergy);
        holder.severityTextView.setText(data.severity);
        holder.reactionLabel.setText(data.reaction);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * View holder for the allergies
     */
    static class AllergyViewHolder extends RecyclerView.ViewHolder {

        View     allergyView;
        TextView categoryLabel;
        TextView allergyLabel;
        TextView severityLabel;
        TextView reactionLabel;
        TextView categoryTextView;
        TextView allergyTextView;
        TextView severityTextView;
        TextView reactionTextView;

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
        }
    }

    /**
     * Data object for allergy
     */
    public static class AllergyPayloadDTO {

        String category;
        public String allergy;
        String severity;
        String reaction;

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

        public String categoryLabel;
        public String allergyLabel;
        public String severityLabel;
        public String reactionLabel;

        public AllergyMetadataDTO(String categoryLabel,
                                  String allergyLabel,
                                  String severityLabel,
                                  String reactionLabel) {
            this.categoryLabel = categoryLabel;
            this.allergyLabel = allergyLabel;
            this.severityLabel = severityLabel;
            this.reactionLabel = reactionLabel;
        }
    }
}
