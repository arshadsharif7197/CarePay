package com.carecloud.carepay.patient.consentforms.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormsProviderInterface;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.consentforms.models.UserFormDTO;
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

    private final List<UserPracticeDTO> practices;
    private final Map<String, Boolean> practicesPermissionsMap;
    private final Map<String, UserFormDTO> practiceFormsMap;
    private ConsentFormsProviderInterface callback;

    public PracticeConsentFormsAdapter(List<UserPracticeDTO> practices,
                                       Map<String, Boolean> practicesPermissions,
                                       Map<String, UserFormDTO> practiceFormsMap) {
        this.practices = practices;
        this.practiceFormsMap = practiceFormsMap;
        practicesPermissionsMap = practicesPermissions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_consent_form, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UserPracticeDTO practice = practices.get(position);
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

        UserFormDTO formDto = null;
        if (practicesPermissionsMap.get(practice.getPracticeId()) != null
                && practicesPermissionsMap.get(practice.getPracticeId())) {
            formDto = practiceFormsMap.get(practice.getPracticeId());
            holder.formStatusTextView.setVisibility(View.VISIBLE);
            if (formDto.getPendingForms().getForms().size() == 0) {
                holder.formStatusTextView.setText(Label.getLabel("consentForms.providersList.item.label.formsFilledStatus"));
                holder.formStatusTextView.setTextColor(holder.formStatusTextView.getContext()
                        .getResources().getColor(R.color.cadet_gray));
            } else if (formDto.getPendingForms().getForms().size() == 1) {
                holder.formStatusTextView.setText(String.format(Label
                                .getLabel("consentForms.providersList.item.label.pendingFormCount"),
                        formDto.getPendingForms().getForms().size()));
                holder.formStatusTextView.setTextColor(holder.formStatusTextView.getContext()
                        .getResources().getColor(R.color.lightning_yellow));
            } else {
                holder.formStatusTextView.setText(String.format(Label
                                .getLabel("consentForms.providersList.item.label.pendingFormsCount"),
                        formDto.getPendingForms().getForms().size()));
                holder.formStatusTextView.setTextColor(holder.formStatusTextView.getContext()
                        .getResources().getColor(R.color.lightning_yellow));
            }
        } else {
            holder.formStatusTextView.setVisibility(View.GONE);
        }

        UserFormDTO finalFormDto = formDto;
        holder.container.setOnClickListener(v -> callback.onProviderSelected(finalFormDto, position));
    }

    public void setCallback(ConsentFormsProviderInterface callback) {
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return practices.size();
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
            formStatusTextView = itemView.findViewById(R.id.formStatusTextView);
            practiceNameTextView = itemView.findViewById(R.id.providerNameTextView);
            practiceAddressTextView = itemView.findViewById(R.id.providerAddressTextView);
            practiceShortNameTextView = itemView.findViewById(R.id.providerShortNameTextView);
            practiceImageView = itemView.findViewById(R.id.providerImageView);
            container = itemView.findViewById(R.id.container);
        }
    }
}
