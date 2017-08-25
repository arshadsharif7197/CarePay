package com.carecloud.carepaylibray.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPrePaymentDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pjohnson on 13/03/17
 */

public class VisitTypeListAdapter extends RecyclerView.Adapter<VisitTypeListAdapter.ViewHolder> {

    public interface VisitTypeSelectionCallback{
        void onVisitTypeSelected(VisitTypeDTO visitTypeDTO);
    }

    private VisitTypeSelectionCallback callback;
    private List<VisitTypeDTO> visitTypes = new ArrayList<>();
    private List<AppointmentsPrePaymentDTO> prepaidVisitTypes = new ArrayList<>();
    private Context context;

    /**
     * Constructor
     * @param context context
     * @param visitTypes visit types
     * @param prepaidVisitTypes prepaid visit types from practice settings
     * @param callback callback
     */
    public VisitTypeListAdapter(Context context, List<VisitTypeDTO> visitTypes, List<AppointmentsPrePaymentDTO> prepaidVisitTypes, VisitTypeSelectionCallback callback) {
        this.context = context;
        this.visitTypes = visitTypes;
        this.prepaidVisitTypes = prepaidVisitTypes;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_visit_type_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final VisitTypeDTO visitTypeDTO = visitTypes.get(position);
        holder.type.setText(visitTypeDTO.getName());

        double amount = getVisitAmount(visitTypeDTO.getId());
        if(amount > 0){
            visitTypeDTO.setAmount(amount);
            holder.amount.setText(NumberFormat.getCurrencyInstance().format(amount));
            holder.prepayLayout.setVisibility(View.VISIBLE);
        }else{
            holder.prepayLayout.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onVisitTypeSelected(visitTypeDTO);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return visitTypes.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return visitTypes.size();
    }

    private double getVisitAmount(int id){
        for(AppointmentsPrePaymentDTO prePaymentDTO : prepaidVisitTypes){
            if(prePaymentDTO.getVisitType() == id){
                return prePaymentDTO.getAmount();
            }
        }
        return 0D;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        TextView amount;
        View prepayLayout;

        ViewHolder(View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.visitTypeListItem);
            amount = (TextView) itemView.findViewById(R.id.prepaymentAmount);
            prepayLayout = itemView.findViewById(R.id.prepaymentLayout);
        }
    }
}
