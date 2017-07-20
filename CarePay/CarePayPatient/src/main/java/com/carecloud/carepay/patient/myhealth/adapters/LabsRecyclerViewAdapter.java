package com.carecloud.carepay.patient.myhealth.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.LabDto;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthMainFragment;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthDataInterface;

import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */

public class LabsRecyclerViewAdapter extends RecyclerView.Adapter<LabsRecyclerViewAdapter.ViewHolder> {


    private final List<LabDto> labs;
    private final int maxItems;
    private final boolean showEndSign;
    private MyHealthDataInterface callback;

    /**
     * @param labs     the list of labs
     * @param maxItems the number of max items to show
     */
    public LabsRecyclerViewAdapter(List<LabDto> labs, int maxItems) {
        this(labs, maxItems, false);
    }

    /**
     * @param labs        the list of labs
     * @param maxItems    the number of max items to show
     * @param showEndSign a boolean indicating to show or not the end sign
     */
    public LabsRecyclerViewAdapter(List<LabDto> labs, int maxItems, boolean showEndSign) {
        this.labs = labs;
        this.maxItems = maxItems;
        this.showEndSign = showEndSign;
    }

    public void setCallback(MyHealthDataInterface callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_health, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LabDto lab = labs.get(position);
        holder.labNameTextView.setText(lab.getName());
        holder.dateTextView.setText(String.format("Performed on %s", lab.getCreatedAt()));
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onLabClicked(lab);
            }
        });
        holder.endSign.setVisibility(showEndSign ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return maxItems == MyHealthMainFragment.MAX_ITEMS_TO_SHOW ?
                Math.min(labs.size(), maxItems) : labs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView myHealthActionButton;
        TextView labNameTextView;
        TextView dateTextView;
        TextView endSign;
        ViewGroup row;

        public ViewHolder(View itemView) {
            super(itemView);
            labNameTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.subTitleTextView);
            endSign = (TextView) itemView.findViewById(R.id.endSign);
            row = (ViewGroup) itemView.findViewById(R.id.row);
            myHealthActionButton = (TextView) itemView.findViewById(R.id.myHealthActionButton);
        }
    }
}
