package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lmenendez on 10/26/17
 */

public class RefundProcessAdapter extends RecyclerView.Adapter<RefundProcessAdapter.ViewHolder> {

    public interface RefundItemActionCallback {
        void onItemCheckChanged(PaymentHistoryLineItem lineItem, boolean checked);

        void onItemAmountSelected(PaymentHistoryLineItem lineItem);
    }

    private Context context;
    private List<PaymentHistoryLineItem> lineItems;
    private RefundItemActionCallback callback;
    private Map<String, ProviderDTO> providerMap = new HashMap<>();
    private Map<String, LocationDTO> locationMap = new HashMap<>();

    /**
     * Constructor
     *
     * @param context   context
     * @param lineItems line items
     * @param callback  callback
     */
    public RefundProcessAdapter(Context context, PaymentsModel paymentsModel, List<PaymentHistoryLineItem> lineItems, RefundItemActionCallback callback) {
        this.context = context;
        this.lineItems = getSortedLineItems(lineItems);
        this.callback = callback;
        if (paymentsModel != null) {
            initLocationsMap(paymentsModel);
            initProviderMap(paymentsModel);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_refund_process, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PaymentHistoryLineItem lineItem = lineItems.get(position);

        holder.amount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(lineItem.getRefundableBalance()));
        holder.description.setText(parseDescription(lineItem.getDescription()));

        LocationDTO locationDTO = locationMap.get(lineItem.getLocationID());
        if (locationDTO != null) {
            holder.location.setText(locationDTO.getName());
        }

        ProviderDTO providerDTO = providerMap.get(lineItem.getProviderID());
        if (providerDTO != null) {
            holder.provider.setText(providerDTO.getName());
            holder.providerInitials.setText(StringUtil.getShortName(providerDTO.getName()));

            int size = context.getResources().getDimensionPixelSize(R.dimen.payment_details_dialog_icon_size);
            Picasso.with(context)
                    .load(providerDTO.getPhoto())
                    .resize(size, size)
                    .centerCrop()
                    .transform(new CircleImageTransform())
                    .into(holder.providerImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.providerImage.setVisibility(View.VISIBLE);
                            holder.providerInitials.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.providerInitials.setVisibility(View.VISIBLE);
                            holder.providerImage.setVisibility(View.GONE);
                        }
                    });
        }

        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(lineItem.isChecked());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lineItem.setChecked(isChecked);
                callback.onItemCheckChanged(lineItem, isChecked);
                notifyDataSetChanged();
            }
        });

        holder.divider.setVisibility(View.VISIBLE);


        holder.amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemAmountSelected(lineItem);
            }
        });

        if (!lineItem.isChecked()) {
            int disabled = ContextCompat.getColor(context, R.color.pastel_blue);
            holder.description.setTextColor(disabled);
            holder.amount.setTextColor(disabled);
        } else {
            holder.description.setTextColor(ContextCompat.getColor(context, R.color.Feldgrau));
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return lineItems.size();
    }


    private void initProviderMap(PaymentsModel paymentsModel) {
        for (ProviderDTO providerDTO : paymentsModel.getPaymentPayload().getProviders()) {
            providerMap.put(providerDTO.getGuid(), providerDTO);
        }
    }

    private void initLocationsMap(PaymentsModel paymentsModel) {
        for (LocationDTO locationDTO : paymentsModel.getPaymentPayload().getLocations()) {
            locationMap.put(locationDTO.getGuid(), locationDTO);
        }
    }

    private List<PaymentHistoryLineItem> getSortedLineItems(List<PaymentHistoryLineItem> lineItems) {
        Collections.sort(lineItems, new Comparator<PaymentHistoryLineItem>() {
            @Override
            public int compare(PaymentHistoryLineItem left, PaymentHistoryLineItem right) {
                return left.getCreditTransactionId().compareTo(right.getCreditTransactionId());
            }
        });
        return lineItems;
    }

    private static String parseDescription(String description) {
        if (description == null) {
            return "";
        }
        switch (description) {
            case IntegratedPaymentLineItem.TYPE_COPAY:
                return Label.getLabel("payment_history_item_copay");
            case IntegratedPaymentLineItem.TYPE_COINSURANCE:
                return Label.getLabel("payment_history_item_coinsurance");
            case IntegratedPaymentLineItem.TYPE_DEDUCTABLE:
                return Label.getLabel("payment_history_item_deductible");
            case IntegratedPaymentLineItem.TYPE_PREPAYMENT:
                return Label.getLabel("payment_history_item_prepayment");
            case IntegratedPaymentLineItem.TYPE_CANCELLATION:
                return Label.getLabel("payment_history_item_cancellation");
            default:
                return description;
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView description;
        TextView provider;
        TextView providerInitials;
        ImageView providerImage;
        TextView location;
        TextView amount;
        View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.refund_check);
            description = itemView.findViewById(R.id.payment_detail_description);
            provider = itemView.findViewById(R.id.provider_name);
            providerInitials = itemView.findViewById(R.id.provider_short_name);
            providerImage = itemView.findViewById(R.id.provider_image);
            location = itemView.findViewById(R.id.location_name);
            amount = itemView.findViewById(R.id.refund_amount);
            divider = itemView.findViewById(R.id.divider);
        }

    }
}
