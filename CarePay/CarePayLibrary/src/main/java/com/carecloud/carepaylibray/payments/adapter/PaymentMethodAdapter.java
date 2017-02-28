package com.carecloud.carepaylibray.payments.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
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

    public PaymentMethodAdapter(Context context, List<PaymentsMethodsDTO> paymentMethodsList, HashMap<String, Integer> paymentTypeMap){
        this.context = context;
        this.paymentMethodsList = paymentMethodsList;
        this.paymentTypeMap = paymentTypeMap;
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

        TextView paymentMethodText = (TextView) convertView.findViewById(R.id.payment_method_text);
        paymentMethodText.setText(paymentMethodsList.get(position).getLabel());


        ImageView paymentMethodImage = (ImageView) convertView.findViewById(R.id.payment_method_image);
        Integer drawable = paymentTypeMap.get(paymentMethodsList.get(position).getType());
        if(drawable == null){
            drawable = paymentTypeMap.get(CarePayConstants.TYPE_CREDIT_CARD);
        }
        paymentMethodImage.setImageResource(drawable);

        return convertView;
    }
}
