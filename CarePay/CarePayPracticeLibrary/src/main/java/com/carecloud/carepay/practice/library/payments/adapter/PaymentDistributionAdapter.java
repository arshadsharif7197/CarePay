package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/14/17.
 */

public class PaymentDistributionAdapter extends RecyclerView.Adapter<PaymentDistributionAdapter.PaymentDistributionViewHolder> {
    private Context context;
    private List<BalanceItemDTO> balanceItems = new ArrayList<>();
    private PaymentDistributionCallback callback;
    private NumberFormat currencyFormat;

    /**
     * Constructor
     * @param context Context
     * @param balanceItems List of Balance Items
     * @param callback callback
     */
    public PaymentDistributionAdapter(Context context, List<BalanceItemDTO> balanceItems, PaymentDistributionCallback callback){
        this.context = context;
        this.balanceItems = balanceItems;
        this.callback = callback;
        this.currencyFormat = NumberFormat.getCurrencyInstance();
    }

    @Override
    public PaymentDistributionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_payment_distribution, parent, false);
        return new PaymentDistributionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PaymentDistributionViewHolder holder, int position) {
        resetSwipedLayoutView(holder);

        final BalanceItemDTO balanceItem = balanceItems.get(position);
        holder.getDescription().setText(StringUtil.getLabelForView(balanceItem.getDescription()));

        String providerName = balanceItem.getProvider().getName();
        if(providerName == null){
            holder.getProviderName().setText(Label.getLabel("payment_choose_provider"));
            holder.getProviderName().setTextColor(context.getResources().getColor(R.color.slateGray));
            if(balanceItem.getProvider().hasError()){
                holder.getProviderName().setError(Label.getLabel("payment_choose_provider"));
            }
        }else {
            holder.getProviderName().setText(StringUtil.getLabelForView(providerName));
            holder.getProviderName().setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.getProviderName().setError(null);
        }

        String providerInitials = StringUtil.getShortName(balanceItem.getProvider().getName());
        if(providerName == null){
            providerInitials = "+";
        }
        holder.getProviderInitials().setText(providerInitials);

        String locationName = balanceItem.getLocation().getName();
        if(locationName == null){
            holder.getLocationName().setText(Label.getLabel("payment_choose_location"));
            holder.getLocationName().setTextColor(context.getResources().getColor(R.color.slateGray));
            if(balanceItem.getLocation().hasError()){
                holder.getLocationName().setError(Label.getLabel("payment_choose_location"));
            }
        }else {
            holder.getLocationName().setText(StringUtil.getLabelForView(locationName));
            holder.getLocationName().setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.getLocationName().setError(null);
        }

        double amount = balanceItem.getBalance();
        EditText amountTextView = holder.getAmount();
        if(amount==0D) {
            amountTextView.setText(context.getString(R.string.em_dash));
        }else{
            amountTextView.setText(currencyFormat.format(amount));
        }

        if(StringUtil.isNullOrEmpty(balanceItem.getProvider().getPhoto())){
            holder.getProviderInitials().setVisibility(View.VISIBLE);
            holder.getProviderPhoto().setVisibility(View.GONE);
        }else{
            holder.getProviderInitials().setVisibility(View.GONE);
            holder.getProviderPhoto().setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(balanceItem.getProvider().getPhoto())
                    .into(holder.getProviderPhoto());
        }

        amountTextView.setFocusable(false);
        amountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.pickAmount(balanceItem);
            }
        });

        amountTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                SystemUtil.hideSoftKeyboard(context, textView);
                double amount = 0D;
                try{
                    amount = Double.parseDouble(textView.getText().toString());
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }
                callback.editAmount(amount, balanceItem);
                return true;
            }
        });

        amountTextView.setSelectAllOnFocus(true);

//        amountTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if(!hasFocus){
//                    double amount = 0D;
//                    try{
//                        amount = Double.parseDouble(((TextView) view).getText().toString());
//                    }catch (NumberFormatException nfe){
//                        nfe.printStackTrace();
//                    }
//                    callback.editAmount(amount, balanceItem);
//                }
//            }
//        });

        holder.getClearButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSwipedLayoutView(holder);
                callback.editAmount(0D, balanceItem);
            }
        });

        holder.getPickProviderButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.pickProvider(view, balanceItem);
            }
        });

        holder.getPickLocationButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.pickLocation(view, balanceItem);
            }
        });

        holder.getProviderName().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.pickProvider(view, balanceItem);
            }
        });

        holder.getLocationName().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.pickLocation(view, balanceItem);
            }
        });

        holder.getProviderInitials().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.pickProvider(view, balanceItem);
            }
        });


    }

    @Override
    public int getItemCount() {
        return balanceItems.size();
    }

    public void setBalanceItems(List<BalanceItemDTO> balanceItems) {
        this.balanceItems = balanceItems;
    }

    private void resetSwipedLayoutView(PaymentDistributionViewHolder holder){
        View rowLayout = holder.getRowLayout();
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) rowLayout.getLayoutParams();
        layoutParams.leftMargin = 0;
        rowLayout.setLayoutParams(layoutParams);

    }

    public class PaymentDistributionViewHolder extends RecyclerView.ViewHolder{

        private TextView description;
        private TextView providerName;
        private TextView providerInitials;
        private TextView locationName;
        private TextView clearButton;
        private EditText amount;
        private ImageView providerPhoto;
        private View pickProviderButton;
        private View pickLocationButton;
        private View rowLayout;

        PaymentDistributionViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.payment_detail_description);
            providerName = (TextView) itemView.findViewById(R.id.provider_name);
            providerInitials = (TextView) itemView.findViewById(R.id.provider_short_name);
            locationName = (TextView) itemView.findViewById(R.id.location_name);
            clearButton = (TextView) itemView.findViewById(R.id.clear_button);
            providerPhoto = (ImageView) itemView.findViewById(R.id.provider_image);
            amount = (EditText) itemView.findViewById(R.id.payment_amount);
            pickProviderButton = itemView.findViewById(R.id.pick_provider_button);
            pickLocationButton = itemView.findViewById(R.id.pick_location_button);
            rowLayout = itemView.findViewById(R.id.payment_info_row);
        }

        public TextView getDescription() {
            return description;
        }

        public void setDescription(TextView description) {
            this.description = description;
        }

        public TextView getProviderName() {
            return providerName;
        }

        public void setProviderName(TextView providerName) {
            this.providerName = providerName;
        }

        public TextView getProviderInitials() {
            return providerInitials;
        }

        public void setProviderInitials(TextView providerInitials) {
            this.providerInitials = providerInitials;
        }

        public TextView getLocationName() {
            return locationName;
        }

        public void setLocationName(TextView locationName) {
            this.locationName = locationName;
        }

        public ImageView getProviderPhoto() {
            return providerPhoto;
        }

        public void setProviderPhoto(ImageView providerPhoto) {
            this.providerPhoto = providerPhoto;
        }

        public EditText getAmount() {
            return amount;
        }

        public void setAmount(EditText amount) {
            this.amount = amount;
        }

        public View getPickProviderButton() {
            return pickProviderButton;
        }

        public void setPickProviderButton(View pickProviderButton) {
            this.pickProviderButton = pickProviderButton;
        }

        public View getPickLocationButton() {
            return pickLocationButton;
        }

        public void setPickLocationButton(View pickLocationButton) {
            this.pickLocationButton = pickLocationButton;
        }

        public TextView getClearButton() {
            return clearButton;
        }

        public void setClearButton(TextView clearButton) {
            this.clearButton = clearButton;
        }

        public View getRowLayout() {
            return rowLayout;
        }

        public void setRowLayout(View rowLayout) {
            this.rowLayout = rowLayout;
        }

    }

    public interface PaymentDistributionCallback{
        void pickProvider(View view, BalanceItemDTO balanceItem);

        void pickLocation(View view, BalanceItemDTO balanceItem);

        void editAmount(double amount, BalanceItemDTO balanceItem);

        void pickAmount(BalanceItemDTO balanceItem);
    }

}
