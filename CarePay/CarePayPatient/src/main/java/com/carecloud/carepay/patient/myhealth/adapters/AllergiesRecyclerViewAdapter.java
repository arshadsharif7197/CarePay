package com.carecloud.carepay.patient.myhealth.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.AllergyDto;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthMainFragment;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthDataInterface;
import com.carecloud.carepay.service.library.label.Label;

import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */

public class AllergiesRecyclerViewAdapter extends RecyclerView.Adapter<AllergiesRecyclerViewAdapter.ViewHolder> {


    private final List<AllergyDto> allergies;
    private MyHealthDataInterface callback;

    public AllergiesRecyclerViewAdapter(List<AllergyDto> providers) {
        this.allergies = providers;
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
                    .inflate(R.layout.item_condition, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if ((position < MyHealthMainFragment.MAX_ITEMS_TO_SHOW) && (position < allergies.size())) {
            final AllergyDto allergy = allergies.get(position);
            holder.allergyNameTextView.setText(allergy.getName());
            holder.practiceTextView.setText(allergy.getPractice());
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onAllergyClicked(allergy);
                }
            });
        } else {
            holder.myHealthActionButton.setText(Label.getLabel("my_health_add_allergy_button_label"));
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.addAllergy();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(allergies.size() + 1, MyHealthMainFragment.MAX_ITEMS_TO_SHOW + 1);
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == MyHealthMainFragment.MAX_ITEMS_TO_SHOW) || ((allergies.size()) == position)) {
            return MyHealthMainFragment.MAX_ITEMS_TO_SHOW;
        }
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myHealthActionButton;
        TextView allergyNameTextView;
        TextView practiceTextView;
        ViewGroup row;

        public ViewHolder(View itemView) {
            super(itemView);
            allergyNameTextView = (TextView) itemView.findViewById(R.id.conditionNameTextView);
            practiceTextView = (TextView) itemView.findViewById(R.id.practiceTextView);
            row = (ViewGroup) itemView.findViewById(R.id.row);
            myHealthActionButton = (TextView) itemView.findViewById(R.id.myHealthActionButton);
        }
    }
}
