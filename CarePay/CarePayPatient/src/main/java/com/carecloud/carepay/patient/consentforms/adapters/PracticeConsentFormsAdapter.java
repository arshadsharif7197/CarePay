package com.carecloud.carepay.patient.consentforms.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormsProviderInterface;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.consentforms.models.payload.FormDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 8/03/18.
 */

public class PracticeConsentFormsAdapter extends RecyclerView.Adapter<PracticeConsentFormsAdapter.ViewHolder> {

    private final List<FormDTO> practiceFormsList;
    private final Map<String, UserPracticeDTO> practicesInformationMap;
    private ConsentFormsProviderInterface callback;

    public PracticeConsentFormsAdapter(List<FormDTO> practiceFormsList,
                                       Map<String, UserPracticeDTO> practicesInformation) {
        this.practiceFormsList = practiceFormsList;
        practicesInformationMap = practicesInformation;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_consent_form, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FormDTO formDto = practiceFormsList.get(position);
        UserPracticeDTO practice = practicesInformationMap.get(formDto.getMetadata().getPracticeId());
        holder.practiceNameTextView.setText(practice.getPracticeName());
        holder.practiceAddressTextView.setText(practice.getAddressDTO().getFullAddress());
        holder.practiceShortNameTextView.setText(StringUtil.getShortName(practice.getPracticeName()));
        Picasso.with(holder.practiceImageView.getContext())
                .load(practice.getPracticePhoto())
                .resize(60, 60)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(holder.practiceImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.practiceImageView.setVisibility(View.VISIBLE);
                        holder.practiceShortNameTextView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.practiceImageView.setVisibility(View.INVISIBLE);
                        holder.practiceShortNameTextView.setVisibility(View.VISIBLE);
                    }
                });
        if (formDto.getPendingForms().size() == 0) {
            holder.formStatusTextView.setText(Label.getLabel("consentForms.providersList.item.label.formsFilledStatus"));
            holder.formStatusTextView.setTextColor(holder.formStatusTextView.getContext()
                    .getResources().getColor(R.color.cadet_gray));
        } else if (formDto.getPendingForms().size() == 1) {
            holder.formStatusTextView.setText(String.format(Label
                            .getLabel("consentForms.providersList.item.label.pendingFormCount"),
                    formDto.getPendingForms().size()));
            holder.formStatusTextView.setTextColor(holder.formStatusTextView.getContext()
                    .getResources().getColor(R.color.lightning_yellow));
        } else {
            holder.formStatusTextView.setText(String.format(Label
                            .getLabel("consentForms.providersList.item.label.pendingFormsCount"),
                    formDto.getPendingForms().size()));
            holder.formStatusTextView.setTextColor(holder.formStatusTextView.getContext()
                    .getResources().getColor(R.color.lightning_yellow));
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onProviderSelected(formDto, position);
            }
        });
    }

    public void setCallback(ConsentFormsProviderInterface callback) {
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return practiceFormsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView formStatusTextView;
        TextView practiceNameTextView;
        TextView practiceAddressTextView;
        TextView practiceShortNameTextView;
        ImageView practiceImageView;
        View container;

        public ViewHolder(View itemView) {
            super(itemView);
            formStatusTextView = (TextView) itemView.findViewById(R.id.formStatusTextView);
            practiceNameTextView = (TextView) itemView.findViewById(R.id.providerNameTextView);
            practiceAddressTextView = (TextView) itemView.findViewById(R.id.providerAddressTextView);
            practiceShortNameTextView = (TextView) itemView.findViewById(R.id.providerShortNameTextView);
            practiceImageView = (ImageView) itemView.findViewById(R.id.providerImageView);
            container = itemView.findViewById(R.id.container);
        }
    }
}