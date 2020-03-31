package com.carecloud.carepaylibray.common.options;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.service.library.base.OptionNameInterface;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;

import java.util.List;

/**
 * @author pjohnson on 2019-05-22.
 */
public class OptionsRecyclerAdapter extends RecyclerView.Adapter<OptionsRecyclerAdapter.ViewHolder> {

    private final List<? extends OptionNameInterface> options;
    private OnOptionSelectedListener listener;

    public OptionsRecyclerAdapter(List<? extends OptionNameInterface> options) {
        this.options = options;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_option, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final OptionNameInterface option = options.get(position);
        holder.optionNameTextView.setText(option.getDisplayName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onOptionSelected(option instanceof DemographicsOption ?
                        (DemographicsOption) option : null, position);
            }
        });
        if (position == options.size() - 1) {
            holder.separator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public void setListener(OnOptionSelectedListener callback) {
        this.listener = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View separator;
        TextView optionNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            optionNameTextView = itemView.findViewById(R.id.optionNameTextView);
            separator = itemView.findViewById(R.id.separator);
        }
    }
}
