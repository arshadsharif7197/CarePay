package com.carecloud.carepay.patient.myhealth.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.ReactionDto;

import java.util.List;

/**
 * @author pjohnson on 19/07/17.
 */

public class ReactionsRecyclerViewAdapter extends RecyclerView.Adapter<ReactionsRecyclerViewAdapter.ViewHolder> {

    private final List<ReactionDto> reactions;

    public ReactionsRecyclerViewAdapter(List<ReactionDto> reactions) {
        this.reactions = reactions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_allergy_reaction, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReactionDto reaction = reactions.get(position);
        holder.labelTextView.setText(reaction.getName());
        holder.valueTextView.setText(reaction.getSeverity());
    }

    @Override
    public int getItemCount() {
        return reactions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView labelTextView;
        TextView valueTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            labelTextView = (TextView) itemView.findViewById(R.id.labelTextView);
            valueTextView = (TextView) itemView.findViewById(R.id.valueTextView);
        }
    }
}
