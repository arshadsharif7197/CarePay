package com.carecloud.carepaylibray.appointments.createappointment.visittype;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author pjohnson on 1/16/19.
 */
public class VisitTypeAdapter extends RecyclerView.Adapter<VisitTypeAdapter.ViewHolder> {

    private final List<VisitTypeDTO> visitTypes;
    private final HashMap<String, Double> prePayments;
    private final OnVisitTypeListItemClickListener listener;

    public VisitTypeAdapter(List<VisitTypeDTO> visitTypes,
                            HashMap<String, Double> prePayments,
                            OnVisitTypeListItemClickListener listener) {
        this.visitTypes = visitTypes;
        this.prePayments = prePayments;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_visit_type, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final VisitTypeDTO visitTypeDTO = visitTypes.get(position);
        holder.visitTypeNameTextView.setText(StringUtil.capitalize(visitTypeDTO.getName()));
        double amount = 0;
        if (prePayments.get(visitTypeDTO.getId()) != null) {
            amount = prePayments.get(visitTypeDTO.getId());
        }
        if (amount > 0) {
            String amountText = String.format(Label
                            .getLabel("createAppointment.visitTypeList.item.label.prepaymentMessage"),
                    NumberFormat.getCurrencyInstance(Locale.US).format(amount));
            holder.prepaymentAmount.setText(amountText);
            holder.prepaymentAmount.setVisibility(View.VISIBLE);
            holder.alertImageView.setVisibility(View.VISIBLE);
            visitTypeDTO.setAmount(amount);
        } else {
            holder.prepaymentAmount.setVisibility(View.GONE);
            holder.alertImageView.setVisibility(View.GONE);
        }

        holder.videoType.setVisibility(visitTypeDTO.hasVideoOption() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onVisitTypeListItemClickListener(visitTypeDTO);
            }
        });
    }

    @Override
    public int getItemCount() {
        return visitTypes.size();
    }

    public interface OnVisitTypeListItemClickListener {
        void onVisitTypeListItemClickListener(VisitTypeDTO visitTypeDTO);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView visitTypeNameTextView;
        TextView prepaymentAmount;
        View videoType;
        ImageView alertImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            visitTypeNameTextView = itemView.findViewById(R.id.visitTypeNameTextView);
            prepaymentAmount = itemView.findViewById(R.id.prepaymentAmount);
            videoType = itemView.findViewById(R.id.videoImageView);
            alertImageView = itemView.findViewById(R.id.alertImageView);
        }
    }
}
