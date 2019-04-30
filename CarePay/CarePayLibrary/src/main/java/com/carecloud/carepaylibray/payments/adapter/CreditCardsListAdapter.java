package com.carecloud.carepaylibray.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by lmenendez on 5/30/17
 */

public class CreditCardsListAdapter extends RecyclerView.Adapter<CreditCardsListAdapter.ViewHolder> {

    private final boolean showSeparator;
    private Context context;
    private CreditCardSelectionListener callback;
    private List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardsList;
    private ViewHolder lastHighlightedView = null;

    private PaymentCreditCardsPayloadDTO selectedItem;

    /**
     * Constructor
     *
     * @param context         Context
     * @param creditCardsList listof card info
     * @param callback        callback for selection
     * @param showSeparator
     */
    public CreditCardsListAdapter(Context context,
                                  List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardsList,
                                  CreditCardSelectionListener callback,
                                  boolean showSeparator) {
        this.context = context;
        this.callback = callback;
        this.creditCardsList = creditCardsList;
        this.showSeparator = showSeparator;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_credit_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PaymentCreditCardsPayloadDTO creditCardInfo = creditCardsList.get(position).getPayload();

        holder.creditCardText.setText(String.format("%s %s", creditCardInfo.getCardType(),
                creditCardInfo.getCardNumber()));

        Date expDate = DateUtil.getInstance().setDateRaw(creditCardInfo.getExpireDt()).getDate();
        expDate = DateUtil.getLastDayOfMonth(expDate);
        if (expDate.before(new Date())) {
            holder.paymentMethodImage.setImageResource(R.drawable.icn_payment_credit_card_expiring);
        }
        if (creditCardInfo.isDefault()) {
            holder.defaultCardText.setVisibility(View.VISIBLE);
        } else {
            holder.defaultCardText.setVisibility(View.GONE);
        }

        if ((creditCardInfo.isDefault() && selectedItem == null) || isTheSameCard(creditCardInfo)) {
            lastHighlightedView = holder;
            highlightRow(holder, true, CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD);
        } else {
            highlightRow(holder, false, CustomAssetStyleable.PROXIMA_NOVA_REGULAR);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    selectedItem = creditCardInfo;
                    if (lastHighlightedView != null) {
                        highlightRow(lastHighlightedView, false, CustomAssetStyleable.PROXIMA_NOVA_REGULAR);
                    }
                    highlightRow(holder, true, CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD);
                    lastHighlightedView = holder;
                    callback.onCreditCardItemSelected(creditCardInfo);
                }
            }
        });
    }

    protected boolean isTheSameCard(PaymentCreditCardsPayloadDTO creditCardInfo) {
        return selectedItem != null
                && ((creditCardInfo.getCreditCardsId() != null
                && creditCardInfo.getCreditCardsId().equals(selectedItem.getCreditCardsId()))
                || (creditCardInfo.getCompleteNumber() != null
                && creditCardInfo.getCompleteNumber().equals(selectedItem.getCompleteNumber())));
    }

    protected void highlightRow(ViewHolder holder, boolean selected, int proximaNovaSemiBold) {
        holder.creditCardText.setSelected(selected);
        holder.paymentMethodImage.setSelected(selected);
        holder.paymentMethodCheck.setSelected(selected);
        holder.creditCardText.setFontAttribute(proximaNovaSemiBold);
    }

    @Override
    public int getItemCount() {
        return creditCardsList.size();
    }

    public void setSelectedCreditCard(PaymentCreditCardsPayloadDTO creditCardPayload) {
        selectedItem = creditCardPayload;
    }

    public interface CreditCardSelectionListener {
        void onCreditCardItemSelected(PaymentCreditCardsPayloadDTO creditCard);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView paymentMethodImage;
        View paymentMethodCheck;
        TextView defaultCardText;
        CarePayTextView creditCardText;
        View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            paymentMethodImage = itemView.findViewById(R.id.credit_card_image);
            paymentMethodCheck = itemView.findViewById(R.id.credit_card_check);
            defaultCardText = (TextView) itemView.findViewById(R.id.credit_card_default);
            creditCardText = (CarePayTextView) itemView.findViewById(R.id.credit_card_text);
            divider = itemView.findViewById(R.id.divider);
            if (showSeparator) {
                divider.setVisibility(View.VISIBLE);
            }
        }
    }

}
