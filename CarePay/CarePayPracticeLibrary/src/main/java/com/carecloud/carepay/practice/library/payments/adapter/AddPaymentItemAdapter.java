package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/16/17.
 */

public class AddPaymentItemAdapter extends RecyclerView.Adapter<AddPaymentItemAdapter.AddPaymentItemViewHolder> {

    public interface AddPaymentItemCallback{
        void paymentItemSelected(BalanceItemDTO balanceItem);
    }

    private static final int VIEW_TYPE_HEADER = 0x100;
    private static final int VIEW_TYPE_LINE = 0x200;

    private Context context;
    private List<BalanceItemDTO> templateItems = new ArrayList<>();
    private List<BalanceItemDTO> simpleChargeItems = new ArrayList<>();
    private AddPaymentItemCallback callback;

    private NumberFormat currencyFormatter;

    public AddPaymentItemAdapter(Context context, List<BalanceItemDTO> templateItems, List<BalanceItemDTO> simpleChargeItems, AddPaymentItemCallback callback){
        this.context = context;
        this.templateItems = templateItems;
        this.simpleChargeItems = simpleChargeItems;
        this.callback = callback;
        this.currencyFormatter = NumberFormat.getCurrencyInstance();
    }

    @Override
    public int getItemViewType(int position){
        if(!templateItems.isEmpty() && (position == 0 || position == templateItems.size()+1)){
            return VIEW_TYPE_HEADER;
        }else{
            return VIEW_TYPE_LINE;
        }
    }

    @Override
    public AddPaymentItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        switch (viewType){
            case VIEW_TYPE_HEADER:
                view = inflater.inflate(R.layout.item_add_charge_header, parent, false);
                break;
            default:
                view = inflater.inflate(R.layout.item_add_charge_row, parent, false);
        }
        return new AddPaymentItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddPaymentItemViewHolder holder, int position) {
        BalanceItemDTO chargeItem = null;
        switch (getItemViewType(position)){
            case VIEW_TYPE_HEADER:{
                if(position == 0){
                    holder.getHeader().setText(Label.getLabelForView("add_item_header_top"));
                }else{
                    holder.getHeader().setText(Label.getLabelForView("add_item_header_other"));
                }
                break;
            }
            default:{
                if(!templateItems.isEmpty() && position-1 < templateItems.size()){
                    chargeItem = templateItems.get(position -1);
                }else if(templateItems.isEmpty()){
                    chargeItem = simpleChargeItems.get(position);
                }else {
                    chargeItem = simpleChargeItems.get(position-templateItems.size()-2);
                }
            }
        }

        if(chargeItem!=null){
            holder.getDescription().setText(chargeItem.getDescription());

            holder.getAmount().setText(currencyFormatter.format(chargeItem.getAmount()));

            final BalanceItemDTO callbackChargeItem = chargeItem;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(callback!=null){
                        callback.paymentItemSelected(callbackChargeItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(!templateItems.isEmpty()) {
            return templateItems.size() + simpleChargeItems.size() + 2;
        }else{
            return simpleChargeItems.size();
        }
    }

    public void setTemplateItems(List<BalanceItemDTO> templateItems) {
        if(templateItems!=null) {
            this.templateItems = templateItems;
        }else{
            this.templateItems = new ArrayList<>();
        }
    }

    public void setSimpleChargeItems(List<BalanceItemDTO> simpleChargeItems) {
        if(simpleChargeItems!=null) {
            this.simpleChargeItems = simpleChargeItems;
        }else{
            this.simpleChargeItems = new ArrayList<>();
        }
    }


    public class AddPaymentItemViewHolder extends RecyclerView.ViewHolder{
        private TextView header;
        private TextView description;
        private TextView amount;

        public AddPaymentItemViewHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.charge_item_header);
            description = (TextView) itemView.findViewById(R.id.charge_item_description);
            amount = (TextView) itemView.findViewById(R.id.charge_item_amount);
        }

        public TextView getHeader() {
            return header;
        }

        public void setHeader(TextView header) {
            this.header = header;
        }

        public TextView getDescription() {
            return description;
        }

        public void setDescription(TextView description) {
            this.description = description;
        }

        public TextView getAmount() {
            return amount;
        }

        public void setAmount(TextView amount) {
            this.amount = amount;
        }
    }
}
