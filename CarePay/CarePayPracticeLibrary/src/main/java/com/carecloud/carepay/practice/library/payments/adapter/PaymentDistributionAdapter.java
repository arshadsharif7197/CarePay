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
    private static final int MINIMUM_SWIPE_DISTANCE = 200;
    private static final int MAX_VERTICAL_VARIANCE = 30;

    private Context context;
    private List<BalanceItemDTO> balanceItems = new ArrayList<>();
    private PaymentDistributionCallback callback;
    private String clearLabel;
    private NumberFormat currencyFormat;

    public PaymentDistributionAdapter(Context context, List<BalanceItemDTO> balanceItems, PaymentDistributionCallback callback, String clearLabel){
        this.context = context;
        this.balanceItems = balanceItems;
        this.callback = callback;
        this.clearLabel = clearLabel;
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
        final BalanceItemDTO balanceItem = balanceItems.get(position);
        holder.getDescription().setText(StringUtil.getLabelForView(balanceItem.getDescription()));
        holder.getProviderName().setText(StringUtil.getLabelForView(balanceItem.getProvider().getName()));
        holder.getProviderInitials().setText(StringUtil.getShortName(balanceItem.getProvider().getName()));
        holder.getLocationName().setText(StringUtil.getLabelForView(balanceItem.getLocation().getName()));
        holder.getClearButton().setText(clearLabel);

        double amount=0D;
        TextView amountTextView = holder.getAmount();
        try{
            amount = Double.parseDouble(balanceItem.getBalance());
        }catch (NumberFormatException nfe){
            nfe.printStackTrace();
        }
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
                View rowLayout = holder.getRowLayout();
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) rowLayout.getLayoutParams();
                layoutParams.leftMargin = 0;
                view.setVisibility(View.GONE);
                rowLayout.setLayoutParams(layoutParams);
                callback.editAmount(0D, balanceItem);
            }
        });

//        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(context, new SwipeGestureDetector(balanceItem));
//        holder.getRowLayout().setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return gestureDetector.onTouchEvent(event);
//            }
//        });

        holder.pickProviderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.pickProvider(view, balanceItem);
            }
        });

        holder.pickLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.pickLocation(view, balanceItem);
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

        public PaymentDistributionViewHolder(View itemView) {
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
    }

//    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener{
//        private PaymentDistributionViewHolder viewHolder;
//
//        public SwipeGestureDetector(PaymentDistributionViewHolder viewHolder){
//            this.viewHolder = viewHolder;
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
//            if(Math.abs(e1.getX()-e2.getX()) > MINIMUM_SWIPE_DISTANCE && Math.abs(e1.getY() - e2.getY()) < MAX_VERTICAL_VARIANCE){
//                //swipe is detected, now which direction???
//                if(e1.getX() > e2.getX()){
//                    //this is right to left swipe
//
//                }else{
//                    //must be left to right
//
//                }
//                return true;
//            }
//            return false;
//        }
//
//    }
}
