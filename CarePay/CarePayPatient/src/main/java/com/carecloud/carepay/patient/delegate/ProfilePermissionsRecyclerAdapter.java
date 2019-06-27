package com.carecloud.carepay.patient.delegate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.profile.Permission;
import com.carecloud.carepaylibray.profile.Permissions;
import com.carecloud.carepaylibray.profile.ProfileLink;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 2019-06-14.
 */
class ProfilePermissionsRecyclerAdapter extends RecyclerView.Adapter<ProfilePermissionsRecyclerAdapter.ViewHolder> {

    private final List<ProfileLink> links;
    private final Map<String, UserPracticeDTO> practicesMap;
    private final boolean showButtons;
    private ProfileEditInterface callback;

    public ProfilePermissionsRecyclerAdapter(List<ProfileLink> links,
                                             Map<String, UserPracticeDTO> practicesMap,
                                             boolean showButtons) {
        this.links = links;
        this.practicesMap = practicesMap;
        this.showButtons = showButtons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_permission,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ProfileLink profileLink = links.get(position);
        UserPracticeDTO userPracticeDTO = practicesMap.get(profileLink.getPracticeId());
        holder.practiceShortNameTextView.setText(StringUtil.getShortName(userPracticeDTO.getPracticeName()));
        PicassoHelper.get().loadImage(holder.practiceImageView.getContext(),
                holder.practiceImageView,
                holder.practiceShortNameTextView, userPracticeDTO.getPracticePhoto());
        holder.toggleImageView.setOnClickListener(view -> {
            holder.permissionsContainer.setVisibility(!holder.toggleImageView.isSelected()
                    ? View.VISIBLE : View.GONE);
            holder.toggleImageView.setSelected(!holder.toggleImageView.isSelected());
            rotateIcon(holder.toggleImageView);
        });
        holder.practiceNameTextView.setText(StringUtil.capitalize(userPracticeDTO.getPracticeName()));
        holder.expirationDateTextView.setText(String.format(Label
                        .getLabel("profile.profileDetail.item.permission.expires"),
                DateUtil.getInstance().setDateRaw(profileLink.getExpirationDate())
                        .toStringWithFormatMmSlashDdSlashYyyy()));
        setUpPermissionsNames(holder.profilePermissionRecycler, profileLink);
        holder.disconnectButton.setVisibility(showButtons ? View.VISIBLE : View.GONE);
        holder.mergeButton.setVisibility(showButtons ? View.VISIBLE : View.GONE);
        holder.disconnectButton.setOnClickListener(v -> callback.onDisconnectClicked(profileLink));
    }

    private void setUpPermissionsNames(RecyclerView recyclerView, ProfileLink profileLink) {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setAdapter(new PermissionsNameAdapter(getPermissionsList(profileLink
                    .getPermissionDto().getPermissions())));
        }
    }

    private List<Permission> getPermissionsList(Permissions permissions) {
        List<Permission> permissionList = new ArrayList<>();
        if (permissions.getCheckInAndCheckOut() != null
                && permissions.getCheckInAndCheckOut().isEnabled()) {
            permissionList.add(permissions.getCheckInAndCheckOut());
        }
        if (permissions.getEditAccountSettings() != null
                && permissions.getEditAccountSettings().isEnabled()) {
            permissionList.add(permissions.getEditAccountSettings());
        }
        if (permissions.getMakePayments() != null
                && permissions.getMakePayments().isEnabled()) {
            permissionList.add(permissions.getMakePayments());
        }
        if (permissions.getMessageProviders() != null
                && permissions.getMessageProviders().isEnabled()) {
            permissionList.add(permissions.getMessageProviders());
        }
        if (permissions.getReviewForms() != null
                && permissions.getReviewForms().isEnabled()) {
            permissionList.add(permissions.getReviewForms());
        }
        if (permissions.getScheduleAppointments() != null
                && permissions.getScheduleAppointments().isEnabled()) {
            permissionList.add(permissions.getScheduleAppointments());
        }
        if (permissions.getViewAccoutSettings() != null
                && permissions.getViewAccoutSettings().isEnabled()) {
            permissionList.add(permissions.getViewAccoutSettings());
        }
        if (permissions.getViewAllergies() != null
                && permissions.getViewAllergies().isEnabled()) {
            permissionList.add(permissions.getViewAllergies());
        }
        if (permissions.getViewAllMyHealth() != null
                && permissions.getViewAllMyHealth().isEnabled()) {
            permissionList.add(permissions.getViewAllMyHealth());
        }
        if (permissions.getViewAndCreateVisitSummaries() != null
                && permissions.getViewAndCreateVisitSummaries().isEnabled()) {
            permissionList.add(permissions.getViewAndCreateVisitSummaries());
        }
        if (permissions.getViewAndSubmitSurveys() != null
                && permissions.getViewAndSubmitSurveys().isEnabled()) {
            permissionList.add(permissions.getViewAndSubmitSurveys());
        }
        if (permissions.getViewAppointments() != null
                && permissions.getViewAppointments().isEnabled()) {
            permissionList.add(permissions.getViewAppointments());
        }
        if (permissions.getViewBalanceAndHistoricalPayments() != null
                && permissions.getViewBalanceAndHistoricalPayments().isEnabled()) {
            permissionList.add(permissions.getViewBalanceAndHistoricalPayments());
        }
        if (permissions.getViewBalanceDetails() != null
                && permissions.getViewBalanceDetails().isEnabled()) {
            permissionList.add(permissions.getViewBalanceDetails());
        }
        if (permissions.getViewCareTeam() != null
                && permissions.getViewCareTeam().isEnabled()) {
            permissionList.add(permissions.getViewCareTeam());
        }
        if (permissions.getViewConditions() != null
                && permissions.getViewConditions().isEnabled()) {
            permissionList.add(permissions.getViewConditions());
        }
        if (permissions.getViewForms() != null
                && permissions.getViewForms().isEnabled()) {
            permissionList.add(permissions.getViewForms());
        }
        if (permissions.getViewLabResults() != null
                && permissions.getViewLabResults().isEnabled()) {
            permissionList.add(permissions.getViewLabResults());
        }
        if (permissions.getViewMedications() != null
                && permissions.getViewMedications().isEnabled()) {
            permissionList.add(permissions.getViewMedications());
        }
        if (permissions.getViewMessages() != null
                && permissions.getViewMessages().isEnabled()) {
            permissionList.add(permissions.getViewMessages());
        }
        if (permissions.getViewNotifications() != null
                && permissions.getViewNotifications().isEnabled()) {
            permissionList.add(permissions.getViewNotifications());
        }
        if (permissions.getViewPatientStatements() != null
                && permissions.getViewPatientStatements().isEnabled()) {
            permissionList.add(permissions.getViewPatientStatements());
        }
        return permissionList;
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    private void rotateIcon(View view) {
        Animation rotation;
        if (view.isSelected()) {
            rotation = AnimationUtils.loadAnimation(view.getContext(), R.anim.ic_profile_arrow_rotation);
        } else {
            rotation = AnimationUtils.loadAnimation(view.getContext(), R.anim.ic_profile_arrow_rotation_unselected);
        }
        rotation.setRepeatCount(0);
        rotation.setDuration(250);
        view.startAnimation(rotation);
        rotation.setFillAfter(true);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View permissionsContainer;
        TextView practiceShortNameTextView;
        ImageView practiceImageView;
        ImageView toggleImageView;
        TextView practiceNameTextView;
        TextView expirationDateTextView;
        RecyclerView profilePermissionRecycler;
        Button disconnectButton;
        Button mergeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            practiceShortNameTextView = itemView.findViewById(R.id.practiceShortNameTextView);
            practiceImageView = itemView.findViewById(R.id.practiceImageView);
            toggleImageView = itemView.findViewById(R.id.toggleImageView);
            practiceNameTextView = itemView.findViewById(R.id.practiceNameTextView);
            expirationDateTextView = itemView.findViewById(R.id.expirationDateTextView);
            permissionsContainer = itemView.findViewById(R.id.permissionsContainer);
            profilePermissionRecycler = itemView.findViewById(R.id.profilePermissionRecycler);
            disconnectButton = itemView.findViewById(R.id.disconnectButton);
            mergeButton = itemView.findViewById(R.id.mergeButton);
        }
    }

    public void setCallback(ProfileEditInterface callback) {
        this.callback = callback;
    }

    public interface ProfileEditInterface {
        void onDisconnectClicked(ProfileLink profileLink);
    }
}
