package com.carecloud.carepay.patient.myhealth.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.AssertionDto;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthMainFragment;

import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */
public class ConditionsRecyclerViewAdapter extends RecyclerView.Adapter<ConditionsRecyclerViewAdapter.ViewHolder> {

    private final List<AssertionDto> assertions;
    private final int maxItems;

    public ConditionsRecyclerViewAdapter(List<AssertionDto> assertions, int maxItems) {
        this.assertions = assertions;
        this.maxItems = maxItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_condition, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AssertionDto assertion = assertions.get(position);
        holder.conditionNameTextView.setText(assertion.getName());
        holder.practiceTextView.setText(assertion.getPractice());
    }

    @Override
    public int getItemCount() {
        return maxItems == MyHealthMainFragment.MAX_ITEMS_TO_SHOW ?
                Math.min(assertions.size(), maxItems) : assertions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView conditionNameTextView;
        TextView practiceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            conditionNameTextView = (TextView) itemView.findViewById(R.id.conditionNameTextView);
            practiceTextView = (TextView) itemView.findViewById(R.id.practiceTextView);
        }
    }
}
