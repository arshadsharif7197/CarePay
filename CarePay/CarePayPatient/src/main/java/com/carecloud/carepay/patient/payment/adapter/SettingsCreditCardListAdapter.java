package com.carecloud.carepay.patient.payment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class SettingsCreditCardListAdapter extends RecyclerView.Adapter<SettingsCreditCardListAdapter.SettingsCreditCardListViewHolder> {

    public interface IOnCreditCardDetailClickListener {
        void onCreditCardDetailClickListener(int position);
    }

    private Context context;
    private List<DemographicsSettingsCreditCardsPayloadDTO> creditCardList;
    private DemographicsSettingsLabelsDTO settingsLabelsDTO;
    private IOnCreditCardDetailClickListener onCreditCardDetailClickListener;

    /**
     * Instantiates a new Settings credit card list adapter.
     *
     * @param context           the context
     * @param creditCardList    the credit card list
     * @param settingsLabelsDTO the settings labels dto
     * @param callback          the callback
     */
    public SettingsCreditCardListAdapter(Context context, List<DemographicsSettingsCreditCardsPayloadDTO> creditCardList,
                                         DemographicsSettingsLabelsDTO settingsLabelsDTO, IOnCreditCardDetailClickListener callback) {
        this.context = context;
        this.creditCardList = creditCardList;
        this.settingsLabelsDTO = settingsLabelsDTO;
        this.onCreditCardDetailClickListener = callback;
    }

    @Override
    public SettingsCreditCardListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentDetailsListItemView = LayoutInflater.from(context).inflate(
                R.layout.settings_creditcard_list_item, parent, false);
        return new SettingsCreditCardListViewHolder(paymentDetailsListItemView);
    }

    @Override
    public void onBindViewHolder(final SettingsCreditCardListViewHolder holder, final int position) {
        final DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO = creditCardList.get(position);
        holder.creditCardTextView.setText(StringUtil.getEncodedCardNumber(creditCardsPayloadDTO.getPayload()
                .getCardType(), creditCardsPayloadDTO.getPayload().getCardNumber()));

        if (creditCardsPayloadDTO.getPayload().isDefault()) {
            holder.defaultTextView.setVisibility(View.VISIBLE);
            holder.defaultTextView.setText(settingsLabelsDTO.getSettingDefaultLabel());
        } else {
            holder.defaultTextView.setVisibility(View.GONE);
        }

        holder.detailsTextView.setText(settingsLabelsDTO.getSettingDetailsLabel());
        holder.detailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreditCardDetailClickListener.onCreditCardDetailClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return creditCardList != null ? creditCardList.size() : 0 ;
    }

    class SettingsCreditCardListViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView creditCardTextView;
        private CarePayTextView defaultTextView;
        private CarePayTextView detailsTextView;

        SettingsCreditCardListViewHolder(View itemView) {
            super(itemView);

            creditCardTextView = (CarePayTextView) itemView.findViewById(R.id.creditCardTextView);
            defaultTextView = (CarePayTextView) itemView.findViewById(R.id.defaultTextView);
            detailsTextView = (CarePayTextView) itemView.findViewById(R.id.detailsTextView);
        }
    }
}
