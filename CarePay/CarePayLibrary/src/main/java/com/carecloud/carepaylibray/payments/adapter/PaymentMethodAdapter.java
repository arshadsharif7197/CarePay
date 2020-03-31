package com.carecloud.carepaylibray.payments.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lmenendez on 2/27/17.
 */

public class PaymentMethodAdapter extends BaseAdapter {

    private Context context;
    private List<PaymentsMethodsDTO> paymentMethodsList;
    private HashMap<String, Integer> paymentTypeMap;

    private int selectedItem = -1;

    /**
     * Constructor
     *
     * @param context            context for adapter
     * @param paymentMethodsList list of payment methods
     */
    public PaymentMethodAdapter(Context context, List<PaymentsMethodsDTO> paymentMethodsList) {
        this.context = context;
        this.paymentMethodsList = paymentMethodsList;
        this.paymentTypeMap = initPaymentTypeMap();
    }

    private HashMap<String, Integer> initPaymentTypeMap() {
        // Initialize HashMap.
        paymentTypeMap = new HashMap<>();
        paymentTypeMap.put(CarePayConstants.TYPE_CASH, R.drawable.payment_cash_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_CREDIT_CARD, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_CHECK, R.drawable.payment_check_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_GIFT_CARD, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_PAYPAL, R.drawable.payment_paypal_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_HSA, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_FSA, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_PAYMENT_PLAN, R.drawable.payment_credit_card_button_selector);
        return paymentTypeMap;
    }

    public void setSelectedItem(int position) {
        this.selectedItem = position;
    }

    @Override
    public int getCount() {
        return paymentMethodsList != null ? paymentMethodsList.size() : 0;
    }

    @Override
    public PaymentsMethodsDTO getItem(int position) {
        return paymentMethodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_payment_method, parent, false);
        }

        CarePayTextView paymentMethodText = convertView.findViewById(R.id.payment_method_text);
        String methodLabel = Label.getLabel("payment_method_" + paymentMethodsList.get(position).getType());
        paymentMethodText.setText(methodLabel);

        ImageView paymentMethodImage = convertView.findViewById(R.id.payment_method_image);
        Integer drawable = paymentTypeMap.get(paymentMethodsList.get(position).getType());
        if (drawable == null) {
            drawable = paymentTypeMap.get(CarePayConstants.TYPE_CREDIT_CARD);
        }
        paymentMethodImage.setImageResource(drawable);

        ImageView paymentMethodCheck = convertView.findViewById(R.id.payment_method_check);

        if (selectedItem == position) {
            paymentMethodText.setSelected(true);
            paymentMethodImage.setSelected(true);
            paymentMethodCheck.setSelected(true);

            paymentMethodText.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD);
        } else {
            paymentMethodText.setSelected(false);
            paymentMethodImage.setSelected(false);
            paymentMethodCheck.setSelected(false);

            paymentMethodText.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_REGULAR);
        }


        return convertView;
    }

    public void setPaymentMethodsList(List<PaymentsMethodsDTO> paymentMethodsList) {
        this.paymentMethodsList = paymentMethodsList;
    }


}
