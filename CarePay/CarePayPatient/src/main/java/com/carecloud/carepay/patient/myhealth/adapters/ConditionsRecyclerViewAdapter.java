package com.carecloud.carepay.patient.myhealth.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.AssertionDto;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthMainFragment;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthDataInterface;

import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */

public class ConditionsRecyclerViewAdapter extends RecyclerView.Adapter<ConditionsRecyclerViewAdapter.ViewHolder> {

    private final List<AssertionDto> assertions;
    private MyHealthDataInterface callback;

    public ConditionsRecyclerViewAdapter(List<AssertionDto> providers) {
        this.assertions = providers;
    }

    public void setCallback(MyHealthDataInterface callback) {
        this.callback = callback;
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
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onConditionClicked(assertion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(assertions.size(), MyHealthMainFragment.MAX_ITEMS_TO_SHOW);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView conditionNameTextView;
        TextView practiceTextView;
        ViewGroup row;

        public ViewHolder(View itemView) {
            super(itemView);
            conditionNameTextView = (TextView) itemView.findViewById(R.id.conditionNameTextView);
            practiceTextView = (TextView) itemView.findViewById(R.id.practiceTextView);
            row = (ViewGroup) itemView.findViewById(R.id.row);
        }
    }
}
