package com.carecloud.carepaylibray.payments.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.carecloud.carepay.service.library.CarePayConstants;
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
     * @param context context for adapter
     * @param paymentMethodsList list of payment methods
     * @param paymentTypeMap map of payment types for icons
     */
    public PaymentMethodAdapter(Context context, List<PaymentsMethodsDTO> paymentMethodsList, HashMap<String, Integer> paymentTypeMap){
        this.context = context;
        this.paymentMethodsList = paymentMethodsList;
        this.paymentTypeMap = paymentTypeMap;
    }

    public void setSelectedItem(int position){
        this.selectedItem = position;
    }

    @Override
    public int getCount() {
        return paymentMethodsList!=null?paymentMethodsList.size():0;
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
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_payment_method, parent, false);
        }

        CarePayTextView paymentMethodText = (CarePayTextView) convertView.findViewById(R.id.payment_method_text);
        paymentMethodText.setText(paymentMethodsList.get(position).getLabel());

        ImageView paymentMethodImage = (ImageView) convertView.findViewById(R.id.payment_method_image);
        Integer drawable = paymentTypeMap.get(paymentMethodsList.get(position).getType());
        if(drawable == null){
            drawable = paymentTypeMap.get(CarePayConstants.TYPE_CREDIT_CARD);
        }
        paymentMethodImage.setImageResource(drawable);

        ImageView paymentMethodCheck = (ImageView) convertView.findViewById(R.id.payment_method_check);

        if(selectedItem == position){
            paymentMethodText.setSelected(true);
            paymentMethodImage.setSelected(true);
            paymentMethodCheck.setSelected(true);

            paymentMethodText.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD);
        }else{
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
