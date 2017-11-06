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
import com.carecloud.carepaylibray.customcomponents.SwipeViewHolder;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/14/17
 */

public class PaymentDistributionAdapter extends RecyclerView.Adapter<PaymentDistributionAdapter.BalanceViewHolder> {
    private Context context;
    private List<BalanceItemDTO> balanceItems = new ArrayList<>();
    private PaymentDistributionCallback callback;
    private NumberFormat currencyFormat;
    private PaymentRowType rowType;

    /**
     * Constructor
     * @param context Context
     * @param balanceItems List of Balance Items
     * @param callback callback
     */
    public PaymentDistributionAdapter(Context context, List<BalanceItemDTO> balanceItems, PaymentDistributionCallback callback, PaymentRowType rowType){
        this.context = context;
        this.balanceItems = balanceItems;
        this.callback = callback;
        this.currencyFormat = NumberFormat.getCurrencyInstance();
        this.rowType = rowType;
    }

    @Override
    public BalanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if(rowType == PaymentRowType.NEW_CHARGE){
            view = inflater.inflate(R.layout.item_payment_distribution_charge, parent, false);
        }else {
            view = inflater.inflate(R.layout.item_payment_distribution_balance, parent, false);
        }

        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BalanceViewHolder holder, int position) {
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



        int size = context.getResources().getDimensionPixelSize(R.dimen.payment_details_dialog_icon_size);
        Picasso.with(context)
                .load(balanceItem.getProvider().getPhoto())
                .resize(size, size)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(holder.getProviderPhoto(), new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.getProviderInitials().setVisibility(View.GONE);
                        holder.getProviderPhoto().setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.getProviderInitials().setVisibility(View.VISIBLE);
                        holder.getProviderPhoto().setVisibility(View.GONE);
                    }
                });


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

        if(holder.getClearButton()!=null) {
            holder.getClearButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetSwipedLayoutView(holder);
                    callback.editAmount(0D, balanceItem);
                }
            });
        }

        if(holder.getRemoveButton()!=null) {
            holder.getRemoveButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetSwipedLayoutView(holder);
                    callback.removeCharge(balanceItem);
                }
            });
        }

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

    private void resetSwipedLayoutView(BalanceViewHolder holder){
        View rowLayout = holder.getRowLayout();
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) rowLayout.getLayoutParams();
        layoutParams.leftMargin = 0;
        rowLayout.setLayoutParams(layoutParams);

    }

    public class BalanceViewHolder extends SwipeViewHolder{

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
        private View removeButton;

        BalanceViewHolder(View itemView) {
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
            removeButton = itemView.findViewById(R.id.removeButton);
        }

        public TextView getDescription() {
            return description;
        }

        public TextView getProviderName() {
            return providerName;
        }

        public TextView getProviderInitials() {
            return providerInitials;
        }

        public TextView getLocationName() {
            return locationName;
        }

        public ImageView getProviderPhoto() {
            return providerPhoto;
        }

        public EditText getAmount() {
            return amount;
        }

        public View getPickProviderButton() {
            return pickProviderButton;
        }

        public View getPickLocationButton() {
            return pickLocationButton;
        }

        public TextView getClearButton() {
            return clearButton;
        }

        public View getRowLayout() {
            return rowLayout;
        }

        public void setRowLayout(View rowLayout) {
            this.rowLayout = rowLayout;
        }

        public View getRemoveButton() {
            return removeButton;
        }

        /**
         * Calculate swipe width based on present action button width
         * @return width for swipe offset
         */
        public int getSwipeWidth() {
            if(removeButton!=null){
                return removeButton.getMeasuredWidth();
            }
            return clearButton.getMeasuredWidth();
        }

        @Override
        public View getSwipeableView() {
            return rowLayout;
        }

        @Override
        public void displayUndoOption() {

        }
    }


    public interface PaymentDistributionCallback{
        void pickProvider(View view, BalanceItemDTO balanceItem);

        void pickLocation(View view, BalanceItemDTO balanceItem);

        void editAmount(double amount, BalanceItemDTO balanceItem);

        void pickAmount(BalanceItemDTO balanceItem);

        void removeCharge(BalanceItemDTO chargeItem);
    }

    public enum PaymentRowType{
        BALANCE, NEW_CHARGE;
    }

}
