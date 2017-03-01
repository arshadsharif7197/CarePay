package com.carecloud.carepaylibray.payments.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;

import java.util.List;

/**
 * Created by lmenendez on 2/27/17.
 */

public class CreditCardsAdapter extends BaseAdapter {

    private Context context;
    private List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardsList;

    private int selectedItem = -1;

    public CreditCardsAdapter(Context context, List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardsList){
        this.context = context;
        this.creditCardsList = creditCardsList;
    }

    public void setSelectedItem(int position){
        this.selectedItem = position;
    }

    @Override
    public int getCount() {
        return creditCardsList !=null? creditCardsList.size():0;
    }

    @Override
    public PaymentsPatientsCreditCardsPayloadListDTO getItem(int position) {
        return creditCardsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_credit_card, parent, false);
        }

        PaymentCreditCardsPayloadDTO creditCardInfo = creditCardsList.get(position).getPayload();

        CarePayTextView creditCardText = (CarePayTextView) convertView.findViewById(R.id.credit_card_text);
        creditCardText.setText(creditCardInfo.getCardType()+" "+creditCardInfo.getCardNumber());

        ImageView paymentMethodImage = (ImageView) convertView.findViewById(R.id.credit_card_image);
        ImageView paymentMethodCheck = (ImageView) convertView.findViewById(R.id.credit_card_check);

        CarePayTextView defaultCardText = (CarePayTextView) convertView.findViewById(R.id.credit_card_default);
        defaultCardText.setVisibility(View.GONE);//todo where is the default card


        if(selectedItem == position){
            creditCardText.setSelected(true);
            paymentMethodImage.setSelected(true);
            paymentMethodCheck.setSelected(true);

            creditCardText.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD);
        }else{
            creditCardText.setSelected(false);
            paymentMethodImage.setSelected(false);
            paymentMethodCheck.setSelected(false);

            creditCardText.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_REGULAR);
        }


        return convertView;
    }

    public void setCreditCardsList(List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardsList) {
        this.creditCardsList = creditCardsList;
    }


}
