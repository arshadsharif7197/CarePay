package com.carecloud.carepay.patient.menu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * @author pjohnson on 2019-06-13.
 */
public class ProfilesMenuRecyclerAdapter extends RecyclerView.Adapter<ProfilesMenuRecyclerAdapter.ViewHolder> {

    private final List<ProfileDto> profiles;
    private ProfileMenuInterface callback;

    public ProfilesMenuRecyclerAdapter(List<ProfileDto> profiles) {
        this.profiles = profiles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProfileDto profile = profiles.get(position);
        holder.nameTextView.setText(getProfileName(profile.getProfile().getDemographics()));
        holder.userShortNameTextView.setText(StringUtil.getShortName(profile.getProfile().getDemographics()
                .getPayload().getPersonalDetails().getFullName()));
        holder.userShortNameTextView.setVisibility(View.VISIBLE);
        PicassoHelper.get().loadImage(holder.menuIconImageView.getContext(),
                holder.menuIconImageView,
                holder.userShortNameTextView, profile.getProfile().getDemographics().getPayload()
                        .getPersonalDetails().getProfilePhoto(),
                holder.menuIconImageView.getContext().getResources().getDimensionPixelSize(R.dimen.menuIconSize));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onProfileClicked(profile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    private String getProfileName(DemographicPayloadInfoDTO demographics) {
        return StringUtil
                .getCapitalizedUserName(demographics.getPayload()
                        .getPersonalDetails().getFirstName(), demographics
                        .getPayload().getPersonalDetails().getLastName());
    }

    interface ProfileMenuInterface {
        void onProfileClicked(ProfileDto profile);
    }

    public void setCallback(ProfileMenuInterface callback) {
        this.callback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView userShortNameTextView;
        private final ImageView menuIconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.menuIconLabelTextView);
            userShortNameTextView = itemView.findViewById(R.id.userShortName);
            menuIconImageView = itemView.findViewById(R.id.menuIconImageView);
        }
    }
}
