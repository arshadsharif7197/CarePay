package com.carecloud.carepay.patient.myhealth.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthProviderDto;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthMainFragment;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthDataInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */

public class CareTeamRecyclerViewAdapter extends RecyclerView.Adapter<CareTeamRecyclerViewAdapter.ViewHolder> {


    private final List<MyHealthProviderDto> providers;
    private final int maxItems;
    private MyHealthDataInterface callback;

    public CareTeamRecyclerViewAdapter(List<MyHealthProviderDto> providers, int maxItems) {
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
            PicassoHelper.get().loadImage(holder.providerImageView.getContext(), holder.providerImageView,
                    holder.initials, provider.getPhoto());
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
            providerImageView = itemView.findViewById(R.id.providerImageView);
            providerNameTextView = itemView.findViewById(R.id.providerNameTextView);
            providerSpecialityTextView = itemView.findViewById(R.id.providerSpecialityTextView);
            myHealthActionButton = itemView.findViewById(R.id.myHealthActionButton);
            initials = itemView.findViewById(R.id.avatarTextView);
            row = itemView.findViewById(R.id.row);
        }
    }
}
