package com.carecloud.carepay.patient.payment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.Date;
import java.util.List;

public class CreditCardListAdapter extends RecyclerView.Adapter<CreditCardListAdapter.SettingsCreditCardListViewHolder> {

    public interface OnCreditCardDetailClickListener {
        void onCreditCardDetail(DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO);
    }

    private Context context;
    private List<DemographicsSettingsCreditCardsPayloadDTO> creditCardList;
    private OnCreditCardDetailClickListener onCreditCardDetailClickListener;

    /**
     * Instantiates a new Settings credit card list adapter.
     *
     * @param context        the context
     * @param creditCardList the credit card list
     * @param callback       the callback
     */
    public CreditCardListAdapter(Context context, List<DemographicsSettingsCreditCardsPayloadDTO> creditCardList,
                                 OnCreditCardDetailClickListener callback) {
        this.context = context;
        this.creditCardList = creditCardList;
        this.onCreditCardDetailClickListener = callback;
    }

    @Override
    public SettingsCreditCardListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View paymentDetailsListItemView = LayoutInflater.from(context).inflate(
                R.layout.settings_creditcard_list_item, parent, false);
        return new SettingsCreditCardListViewHolder(paymentDetailsListItemView);
    }

    @Override
    public void onBindViewHolder(final SettingsCreditCardListViewHolder holder, int position) {
        final DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO = creditCardList.get(position);
        holder.creditCardTextView.setText(StringUtil.getEncodedCardNumber(
                creditCardsPayloadDTO.getPayload().getCardType(),
                creditCardsPayloadDTO.getPayload().getCardNumber()));

        Date expDate = DateUtil.getInstance().setDateRaw(creditCardsPayloadDTO.getPayload().getExpireDt()).getDate();
        expDate = DateUtil.getLastDayOfMonth(expDate);
        expDate = DateUtil.getLastHourOfDay(expDate);
        Date now = new Date();
        if (expDate.before(now)) {
            holder.cardIcon.setImageResource(R.drawable.icn_payment_credit_card_expiring);
        }

        if (creditCardsPayloadDTO.getPayload().isDefault()) {
            holder.defaultTextView.setVisibility(View.VISIBLE);
        } else {
            holder.defaultTextView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> onCreditCardDetailClickListener.onCreditCardDetail(creditCardsPayloadDTO));
    }

    @Override
    public int getItemCount() {
        return creditCardList.size();
    }

    class SettingsCreditCardListViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView creditCardTextView;
        private CarePayTextView defaultTextView;
        private ImageView cardIcon;

        SettingsCreditCardListViewHolder(View itemView) {
            super(itemView);
            creditCardTextView = itemView.findViewById(R.id.credit_card_text);
            defaultTextView = itemView.findViewById(R.id.credit_card_default);
            cardIcon = itemView.findViewById(R.id.credit_card_image);
        }
    }
}
