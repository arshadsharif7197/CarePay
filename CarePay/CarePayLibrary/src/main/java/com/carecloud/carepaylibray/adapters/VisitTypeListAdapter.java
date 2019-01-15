package com.carecloud.carepaylibray.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPrePaymentDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by pjohnson on 13/03/17
 */

public class VisitTypeListAdapter extends RecyclerView.Adapter<VisitTypeListAdapter.ViewHolder> {

    public interface VisitTypeSelectionCallback {
        void onVisitTypeSelected(VisitTypeDTO visitTypeDTO);
    }

    private VisitTypeSelectionCallback callback;
    private List<VisitTypeDTO> visitTypes = new ArrayList<>();
    private List<AppointmentsPrePaymentDTO> prepaidVisitTypes = new ArrayList<>();
    private Context context;

    /**
     * Constructor
     *
     * @param context           context
     * @param visitTypes        visit types
     * @param prepaidVisitTypes prepaid visit types from practice settings
     * @param callback          callback
     */
    public VisitTypeListAdapter(Context context, List<VisitTypeDTO> visitTypes,
                                List<AppointmentsPrePaymentDTO> prepaidVisitTypes,
                                VisitTypeSelectionCallback callback) {
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
        if (amount > 0) {
            visitTypeDTO.setAmount(amount);
            holder.amount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(amount));
            holder.notificationPaymentImage.setVisibility(View.VISIBLE);
            holder.amount.setVisibility(View.VISIBLE);
        } else {
            holder.notificationPaymentImage.setVisibility(View.GONE);
            holder.amount.setVisibility(View.GONE);
        }

        holder.videoType.setVisibility(visitTypeDTO.hasVideoOption() ? View.VISIBLE : View.GONE);

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

    private double getVisitAmount(int id) {
        for (AppointmentsPrePaymentDTO prePaymentDTO : prepaidVisitTypes) {
            if (prePaymentDTO.getVisitType() == id) {
                return prePaymentDTO.getAmount();
            }
        }
        return 0D;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        TextView amount;
        ImageView notificationPaymentImage;
        View videoType;

        ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.visitTypeListItem);
            amount = itemView.findViewById(R.id.prepaymentAmount);
            notificationPaymentImage = itemView.findViewById(R.id.notificationPaymentImage);
            videoType = itemView.findViewById(R.id.visit_type_video);
        }
    }
}
