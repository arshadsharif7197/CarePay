package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.PaymentsActivity;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PatienceBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class PracticePaymentsAdapter extends RecyclerView.Adapter<PracticePaymentsAdapter.CartViewHolder> {

    private Context context;
    private PaymentsModel paymentsModel;
    private List<PaymentsPatientBalancessDTO> patientBalances;

    /**
     * Constructor.
     *
     * @param context         context
     * @param paymentsModel list of patients
     */
    public PracticePaymentsAdapter(Context context, PaymentsModel paymentsModel) {
        this.context = context;
        this.paymentsModel = paymentsModel;
        this.patientBalances = paymentsModel.getPaymentPayload().getPatientBalances();
    }

    /**
     * Creates view.
     *
     * @param parent   parent view.
     * @param viewType view type
     * @return created view
     */
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payments_list_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int position) {
        DemographicsSettingsDemographicsDTO demographics = patientBalances.get(position).getDemographics();
        final DemographicsSettingsPersonalDetailsPayloadDTO personalDetails = demographics.getPayload().getPersonalDetails();
        holder.shortName.setText(StringUtil.onShortDrName(personalDetails.getFirstName() + " " + personalDetails.getLastName()));

        String photoUrl = personalDetails.getProfilePhoto();
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    holder.shortName.setText(StringUtil.onShortDrName(
                            personalDetails.getFirstName() + " " + personalDetails.getLastName()));
                }
            });

            builder.build().load(photoUrl).transform(new CircleImageTransform())
                    .resize(60, 60).into(holder.profilePicture);

            holder.profilePicture.setVisibility(View.VISIBLE);
        }

        holder.patientName.setText(personalDetails.getFirstName() + " " + personalDetails.getLastName());

        double balanceAmount = 0;
        List<PatienceBalanceDTO> balances = patientBalances.get(position).getBalances();
        for (int i = 0; i < balances.size(); i++) {
            List<PatiencePayloadDTO> payload = balances.get(i).getPayload();
            for (int j = 0; j < payload.size(); j++) {
                balanceAmount += payload.get(j).getAmount();
            }
        }

        holder.providerName.setText(getProviderName(balances.get(0).getMetadata().getPatientId()));
        holder.amount.setText(String.format(Locale.getDefault(), "$%.2f", balanceAmount));
    }

    private String getProviderName(String patientId) {
        if (!StringUtil.isNullOrEmpty(patientId)) {
            List<ProviderIndexDTO> providerIndex = paymentsModel.getPaymentPayload().getProviderIndex();

            for (ProviderIndexDTO providerIndexDTO : providerIndex) {
                List<String> patientIds = providerIndexDTO.getPatientIds();

                for (String id : patientIds) {
                    if (id.equalsIgnoreCase(patientId)) {
                        return providerIndexDTO.getName();
                    }
                }
            }
        }

        return StringUtil.getLabelForView("");
    }

    @Override
    public int getItemCount() {
        if (patientBalances != null) {
            return patientBalances.size();
        }
        return 0;
    }

    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CarePayTextView shortName;
        CarePayTextView patientName;
        CarePayTextView providerName;
        CarePayTextView amount;
        ImageView profilePicture;

        /**
         * Constructor.
         *
         * @param view view
         */
        CartViewHolder(View view) {
            super(view);
            shortName = (CarePayTextView) view.findViewById(R.id.patient_short_name);
            patientName = (CarePayTextView) view.findViewById(R.id.patient_name_text_view);
            providerName = (CarePayTextView) view.findViewById(R.id.provider_name_text_view);
            amount = (CarePayTextView) view.findViewById(R.id.amount_text_view);
            profilePicture = (ImageView) view.findViewById(R.id.patient_pic_image_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ((PaymentsActivity) context).onPatientItemClick(getAdapterPosition());
        }
    }
}
