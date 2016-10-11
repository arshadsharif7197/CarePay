package com.carecloud.carepaylibray.demographics.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lsoco_user on 10/11/2016.
 * Adapter for the recycler view holding the allergies.
 */
public class DemographicsDetailsAllergiesAdapter
        extends  RecyclerView.Adapter<DemographicsDetailsAllergiesAdapter.AllergyViewHolder> {

    @Override
    public AllergyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(AllergyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * View holder for the allergies
     */
    static class AllergyViewHolder extends RecyclerView.ViewHolder {
        public AllergyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
