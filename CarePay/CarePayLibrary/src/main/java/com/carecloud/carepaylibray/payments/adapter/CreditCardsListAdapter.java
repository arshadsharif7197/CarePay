package com.carecloud.carepaylibray.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 5/30/17
 */

public class CreditCardsListAdapter extends RecyclerView.Adapter<CreditCardsListAdapter.ViewHolder> {

    public interface CreditCardSelectionListener{
        void onCreditCardItemSelected(int position);
    }

    private Context context;
    private CreditCardSelectionListener callback;
    private List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardsList = new ArrayList<>();

    private int selectedItem = -1;

    /**
     * Constructor
     * @param context Context
     * @param creditCardsList listof card info
     * @param callback callback for selection
     */
    public CreditCardsListAdapter(Context context, List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardsList, CreditCardSelectionListener callback){
        this.context = context;
        this.callback = callback;
        this.creditCardsList = creditCardsList;
    }

    /**
     * set selected card for view
     * @param position position in list
     */
    public void setSelectedItem(int position){
        boolean shouldRefresh = selectedItem != -1 && selectedItem != position;
        this.selectedItem = position;
        if(shouldRefresh){
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_credit_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        PaymentCreditCardsPayloadDTO creditCardInfo = creditCardsList.get(position).getPayload();

        holder.creditCardText.setText(creditCardInfo.getCardType()+" "+creditCardInfo.getCardNumber());

        if(creditCardInfo.isDefault()){
            holder.defaultCardText.setVisibility(View.VISIBLE);
            if(selectedItem == -1) {
                setSelectedItem(position);
                callback.onCreditCardItemSelected(position);
            }
        }else {
            holder.defaultCardText.setVisibility(View.GONE);
        }

        if(selectedItem == position){
            holder.creditCardText.setSelected(true);
            holder.paymentMethodImage.setSelected(true);
            holder.paymentMethodCheck.setSelected(true);

            holder.creditCardText.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD);
        }else{
            holder.creditCardText.setSelected(false);
            holder.paymentMethodImage.setSelected(false);
            holder.paymentMethodCheck.setSelected(false);

            holder.creditCardText.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_REGULAR);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback!=null){
                    callback.onCreditCardItemSelected(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return creditCardsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View paymentMethodImage;
        View paymentMethodCheck;
        TextView defaultCardText;
        CarePayTextView creditCardText;

        public ViewHolder(View itemView) {
            super(itemView);
            paymentMethodImage = itemView.findViewById(R.id.credit_card_image);
            paymentMethodCheck = itemView.findViewById(R.id.credit_card_check);
            defaultCardText = (TextView) itemView.findViewById(R.id.credit_card_default);
            creditCardText = (CarePayTextView) itemView.findViewById(R.id.credit_card_text);

        }
    }

}
