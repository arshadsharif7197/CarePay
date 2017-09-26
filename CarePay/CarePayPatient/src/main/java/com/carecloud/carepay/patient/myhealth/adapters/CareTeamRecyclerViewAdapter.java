package com.carecloud.carepay.patient.myhealth.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthMainFragment;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthDataInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */

public class CareTeamRecyclerViewAdapter extends RecyclerView.Adapter<CareTeamRecyclerViewAdapter.ViewHolder> {


    private final List<ProviderDTO> providers;
    private final int maxItems;
    private MyHealthDataInterface callback;

    public CareTeamRecyclerViewAdapter(List<ProviderDTO> providers, int maxItems) {
        this.providers = providers;
        this.maxItems = maxItems;
    }

    public void setCallback(MyHealthDataInterface callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MyHealthMainFragment.MAX_ITEMS_TO_SHOW) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_health_action, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_care_team, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if ((position < maxItems) && (position < providers.size())) {
            final ProviderDTO provider = providers.get(position);
            holder.providerNameTextView.setText(provider.getFullName());
            holder.providerSpecialityTextView.setText(String.format("%s, %s",
                    provider.getSpecialityName(), provider.getPractice()));
            holder.initials.setText(StringUtil.getShortName(provider.getFullName()));
            Picasso.with(holder.providerImageView.getContext()).load(provider.getPhoto())
                    .transform(new CircleImageTransform())
                    .into(holder.providerImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.providerImageView.setVisibility(View.VISIBLE);
                            holder.initials.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.providerImageView.setVisibility(View.GONE);
                            holder.initials.setVisibility(View.VISIBLE);
                        }
                    });
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onProviderClicked(provider);
                }
            });
        } else {
            holder.myHealthActionButton.setText(Label.getLabel("my_health_full_medical_record_button_label"));
            holder.myHealthActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onSeeAllFullMedicalRecordClicked(null);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return maxItems == MyHealthMainFragment.MAX_ITEMS_TO_SHOW ?
                Math.min(providers.size(), maxItems) : providers.size();
//        return maxItems == MyHealthMainFragment.MAX_ITEMS_TO_SHOW ?
//                Math.min(providers.size() + 1, maxItems + 1) : providers.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == maxItems) || ((providers.size()) == position)) {
            return MyHealthMainFragment.MAX_ITEMS_TO_SHOW;
        }
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView providerImageView;
        TextView providerNameTextView;
        TextView providerSpecialityTextView;
        TextView myHealthActionButton;
        TextView initials;
        ViewGroup row;

        public ViewHolder(View itemView) {
            super(itemView);
            providerImageView = (ImageView) itemView.findViewById(R.id.providerImageView);
            providerNameTextView = (TextView) itemView.findViewById(R.id.providerNameTextView);
            providerSpecialityTextView = (TextView) itemView.findViewById(R.id.providerSpecialityTextView);
            myHealthActionButton = (TextView) itemView.findViewById(R.id.myHealthActionButton);
            initials = (TextView) itemView.findViewById(R.id.avatarTextView);
            row = (ViewGroup) itemView.findViewById(R.id.row);
        }
    }
}
