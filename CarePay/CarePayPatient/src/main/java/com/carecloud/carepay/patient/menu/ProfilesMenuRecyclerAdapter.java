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
    public static final int SMALL_PROFILE_VIEW_TYPE = 100;
    public static final int BIG_PROFILE_VIEW_TYPE = 101;
    private final int viewType;

    public ProfilesMenuRecyclerAdapter(List<ProfileDto> profiles, int viewType) {
        this.profiles = profiles;
        this.viewType = viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.layout_item_profile_small;
        if (viewType == BIG_PROFILE_VIEW_TYPE) {
            layout = R.layout.layout_item_profile_big;
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProfileDto profile = profiles.get(position);
        holder.profileNameTextView.setText(getProfileName(profile.getProfile().getDemographics()));
        holder.profileShortNameTextView.setText(StringUtil.getShortName(profile.getProfile().getDemographics()
                .getPayload().getPersonalDetails().getFullName()));
        holder.profileShortNameTextView.setVisibility(View.VISIBLE);
        if (profile.getProfile().getLinks() != null && !profile.getProfile().getLinks().isEmpty()) {
            holder.profileRelationTextView.setText(StringUtil.capitalize(profile.getProfile()
                    .getLinks().get(0).getRelationType()));
        }

        PicassoHelper.get().loadImage(holder.profileImageView.getContext(),
                holder.profileImageView,
                holder.profileShortNameTextView, profile.getProfile().getDemographics().getPayload()
                        .getPersonalDetails().getProfilePhoto(),
                holder.profileImageView.getContext().getResources().getDimensionPixelSize(R.dimen.menuIconSize));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onProfileClicked(profile, position);
            }
        });
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

    public interface ProfileMenuInterface {
        void onProfileClicked(ProfileDto profile, int position);
    }

    public void setCallback(ProfileMenuInterface callback) {
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
