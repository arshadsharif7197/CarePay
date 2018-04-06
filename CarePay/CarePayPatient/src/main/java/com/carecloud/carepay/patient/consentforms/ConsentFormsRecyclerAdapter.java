package com.carecloud.carepay.patient.consentforms;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
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

public class ConsentFormsRecyclerAdapter extends RecyclerView.Adapter<ConsentFormsRecyclerAdapter.ViewHolder> {

    private final List<FormDTO> practiceFormsList;
    private final Map<String, UserPracticeDTO> practicesInformationMap;

    public ConsentFormsRecyclerAdapter(List<FormDTO> practiceFormsList,
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FormDTO practiceForm = practiceFormsList.get(position);
        UserPracticeDTO practice = practicesInformationMap.get(practiceForm.getMetadata().getPracticeId());
        holder.providerNameTextView.setText(practice.getPracticeName());
        holder.providerAddressTextView.setText(practice.getAddressDTO().getFullAddress());
        holder.providerShortNameTextView.setText(StringUtil.getShortName(practice.getPracticeName()));
        Picasso.with(holder.providerImageView.getContext())
                .load(practice.getPracticePhoto())
                .resize(60, 60)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(holder.providerImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.providerImageView.setVisibility(View.VISIBLE);
                        holder.providerShortNameTextView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.providerImageView.setVisibility(View.INVISIBLE);
                        holder.providerShortNameTextView.setVisibility(View.VISIBLE);
                    }
                });
        if (practiceForm.getPendingForms().size() == 0) {
            holder.formStatusTextView.setText(Label.getLabel("consentForms.providersList.item.label.formsFilledStatus"));
            holder.formStatusTextView.setTextColor(holder.formStatusTextView.getContext()
                    .getResources().getColor(R.color.cadet_gray));
        } else if (practiceForm.getPendingForms().size() == 1) {
            holder.formStatusTextView.setText(Label.getLabel("consentForms.providersList.item.label.pendingFormCount"));
            holder.formStatusTextView.setTextColor(holder.formStatusTextView.getContext()
                    .getResources().getColor(R.color.lightning_yellow));
        } else {
            holder.formStatusTextView.setText(Label.getLabel("consentForms.providersList.item.label.pendingFormsCount"));
            holder.formStatusTextView.setTextColor(holder.formStatusTextView.getContext()
                    .getResources().getColor(R.color.lightning_yellow));
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return practiceFormsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView formStatusTextView;
        TextView providerNameTextView;
        TextView providerAddressTextView;
        TextView providerShortNameTextView;
        ImageView providerImageView;
        View container;

        public ViewHolder(View itemView) {
            super(itemView);
            formStatusTextView = (TextView) itemView.findViewById(R.id.formStatusTextView);
            providerNameTextView = (TextView) itemView.findViewById(R.id.providerNameTextView);
            providerAddressTextView = (TextView) itemView.findViewById(R.id.providerAddressTextView);
            providerShortNameTextView = (TextView) itemView.findViewById(R.id.providerShortNameTextView);
            providerImageView = (ImageView) itemView.findViewById(R.id.providerImageView);
            container = itemView.findViewById(R.id.container);
        }
    }
}
