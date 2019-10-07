package com.carecloud.carepay.patient.delegate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * @author pjohnson on 2019-06-13.
 */
public class DelegatesRecyclerAdapter extends RecyclerView.Adapter<DelegatesRecyclerAdapter.ViewHolder> {

    private final List<ProfileDto> profiles;
    private ManageDelegateInterface callback;
    private final int viewType;

    public DelegatesRecyclerAdapter(List<ProfileDto> profiles, int viewType) {
        this.profiles = profiles;
        this.viewType = viewType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_profile_big, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ProfileDto profile = profiles.get(position);
        holder.profileNameTextView.setText(getProfileName(profile.getProfile().getDelegateDemographics()));
        holder.profileShortNameTextView.setText(StringUtil.getShortName(profile.getProfile().getDelegateDemographics()
                .getPayload().getPersonalDetails().getFullName()));
        holder.profileShortNameTextView.setVisibility(View.VISIBLE);
        if (profile.getProfile().getLinks() != null && !profile.getProfile().getLinks().isEmpty()) {
            holder.profileRelationTextView.setText(StringUtil.capitalize(profile.getProfile()
                    .getLinks().get(0).getRelationType()));
        }

        PicassoHelper.get().loadImage(holder.profileImageView.getContext(),
                holder.profileImageView,
                holder.profileShortNameTextView, profile.getProfile().getDelegateDemographics().getPayload()
                        .getPersonalDetails().getProfilePhoto(),
                holder.profileImageView.getContext().getResources().getDimensionPixelSize(R.dimen.menuIconSize));

        holder.itemView.setOnClickListener(view -> callback.onProfileClicked(profile, position));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    private String getProfileName(DemographicPayloadInfoDTO demographics) {
        return StringUtil
                .getCapitalizedUserName(demographics.getPayload()
                        .getPersonalDetails().getFirstName(), demographics
                        .getPayload().getPersonalDetails().getLastName());
    }

    public interface ManageDelegateInterface {
        void onProfileClicked(ProfileDto profile, int position);
    }

    public void setCallback(ManageDelegateInterface callback) {
        this.callback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView profileImageView;
        final TextView profileShortNameTextView;
        final TextView profileNameTextView;
        final TextView profileRelationTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            profileShortNameTextView = itemView.findViewById(R.id.profileShortNameTextView);
            profileNameTextView = itemView.findViewById(R.id.profileNameTextView);
            profileRelationTextView = itemView.findViewById(R.id.profileRelationTextView);
        }
    }
}
