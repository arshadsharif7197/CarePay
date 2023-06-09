package com.carecloud.carepay.patient.delegate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
public class ProfilePermissionsRecyclerAdapter extends RecyclerView.Adapter<ProfilePermissionsRecyclerAdapter.ViewHolder> {

    final List<ProfileLink> links;
    private final Map<String, UserPracticeDTO> practicesMap;
    final boolean showButtons;
    private ProfileEditInterface callback;

    public ProfilePermissionsRecyclerAdapter(List<ProfileLink> links,
                                             Map<String, UserPracticeDTO> practicesMap,
                                             boolean showButtons) {
        this.links = links;
        this.practicesMap = practicesMap;
        this.showButtons = showButtons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_permission,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
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
        setUpPermissionsNames(holder, profileLink);
        holder.disconnectButton.setVisibility(showButtons ? View.VISIBLE : View.GONE);
        holder.disconnectButton.setOnClickListener(v -> callback.onDisconnectClicked(profileLink));
    }

    protected void setUpPermissionsNames(ViewHolder viewHolder, ProfileLink profileLink) {
        if (viewHolder.profilePermissionRecycler.getAdapter() == null) {
            viewHolder.profilePermissionRecycler.setLayoutManager(new LinearLayoutManager(viewHolder
                    .profilePermissionRecycler.getContext()));
            viewHolder.profilePermissionRecycler.setAdapter(new PermissionsNameAdapter(getPermissionsList(profileLink
                    .getPermissionDto().getPermissions(), false)));
        }
    }

    List<Permission> getPermissionsList(Permissions permissions, boolean alsoDisabled) {
        List<Permission> permissionList = new ArrayList<>();
        if (permissions.getViewAppointments() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewAppointments().getLabel())
                && (permissions.getViewAppointments().isEnabled() || alsoDisabled)) {
            permissions.getViewAppointments().setKey("view_appointments");
            permissionList.add(permissions.getViewAppointments());
        }
        if (permissions.getScheduleAppointments() != null
                && !StringUtil.isNullOrEmpty(permissions.getScheduleAppointments().getLabel())
                && (permissions.getScheduleAppointments().isEnabled() || alsoDisabled)) {
            permissions.getScheduleAppointments().setKey("schedule_appointments");
            permissionList.add(permissions.getScheduleAppointments());
        }
        if (permissions.getCheckInAndCheckOut() != null
                && !StringUtil.isNullOrEmpty(permissions.getCheckInAndCheckOut().getLabel())
                && (permissions.getCheckInAndCheckOut().isEnabled() || alsoDisabled)) {
            permissions.getCheckInAndCheckOut().setKey("checkin_and_checkout");
            permissionList.add(permissions.getCheckInAndCheckOut());
        }
        if (permissions.getViewAllMyHealth() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewAllMyHealth().getLabel())
                && (permissions.getViewAllMyHealth().isEnabled() || alsoDisabled)) {
            permissions.getViewAllMyHealth().setKey("view_all_my_health");
            permissionList.add(permissions.getViewAllMyHealth());
        }
        if (permissions.getViewCareTeam() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewCareTeam().getLabel())
                && (permissions.getViewCareTeam().isEnabled() || alsoDisabled)) {
            permissions.getViewCareTeam().setKey("view_care_team");
            permissionList.add(permissions.getViewCareTeam());
        }
        if (permissions.getViewAllergies() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewAllergies().getLabel())
                && (permissions.getViewAllergies().isEnabled() || alsoDisabled)) {
            permissions.getViewAllergies().setKey("view_allergies");
            permissionList.add(permissions.getViewAllergies());
        }
        if (permissions.getViewConditions() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewConditions().getLabel())
                && (permissions.getViewConditions().isEnabled() || alsoDisabled)) {
            permissions.getViewConditions().setKey("view_conditions");
            permissionList.add(permissions.getViewConditions());
        }
        if (permissions.getViewMedications() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewMedications().getLabel())
                && (permissions.getViewMedications().isEnabled() || alsoDisabled)) {
            permissions.getViewMedications().setKey("view_medications");
            permissionList.add(permissions.getViewMedications());
        }
        if (permissions.getViewLabResults() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewLabResults().getLabel())
                && (permissions.getViewLabResults().isEnabled() || alsoDisabled)) {
            permissions.getViewLabResults().setKey("view_lab_results");
            permissionList.add(permissions.getViewLabResults());
        }
        if (permissions.getViewAndCreateVisitSummaries() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewAndCreateVisitSummaries().getLabel())
                && (permissions.getViewAndCreateVisitSummaries().isEnabled() || alsoDisabled)) {
            permissions.getViewAndCreateVisitSummaries().setKey("view_and_create_visit_summaries");
            permissionList.add(permissions.getViewAndCreateVisitSummaries());
        }
        if (permissions.getViewBalanceAndHistoricalPayments() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewBalanceAndHistoricalPayments().getLabel())
                && (permissions.getViewBalanceAndHistoricalPayments().isEnabled() || alsoDisabled)) {
            permissions.getViewBalanceAndHistoricalPayments().setKey("view_balance_and_historical_payments");
            permissionList.add(permissions.getViewBalanceAndHistoricalPayments());
        }
        if (permissions.getMakePayments() != null
                && !StringUtil.isNullOrEmpty(permissions.getMakePayments().getLabel())
                && (permissions.getMakePayments().isEnabled() || alsoDisabled)) {
            permissions.getMakePayments().setKey("make_payments");
            permissionList.add(permissions.getMakePayments());
        }
        if (permissions.getViewPatientStatements() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewPatientStatements().getLabel())
                && (permissions.getViewPatientStatements().isEnabled() || alsoDisabled)) {
            permissions.getViewPatientStatements().setKey("view_patient_statements");
            permissionList.add(permissions.getViewPatientStatements());
        }
        if (permissions.getViewBalanceDetails() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewBalanceDetails().getLabel())
                && (permissions.getViewBalanceDetails().isEnabled() || alsoDisabled)) {
            permissions.getViewBalanceDetails().setKey("view_balance_details");
            permissionList.add(permissions.getViewBalanceDetails());
        }
        if (permissions.getViewNotifications() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewNotifications().getLabel())
                && (permissions.getViewNotifications().isEnabled() || alsoDisabled)) {
            permissions.getViewNotifications().setKey("view_notifications");
            permissionList.add(permissions.getViewNotifications());
        }
        if (permissions.getMessageProviders() != null
                && !StringUtil.isNullOrEmpty(permissions.getMessageProviders().getLabel())
                && (permissions.getMessageProviders().isEnabled() || alsoDisabled)) {
            permissions.getMessageProviders().setKey("message_providers");
            permissionList.add(permissions.getMessageProviders());
        }
        if (permissions.getViewForms() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewForms().getLabel())
                && (permissions.getViewForms().isEnabled() || alsoDisabled)) {
            permissions.getViewForms().setKey("view_forms");
            permissionList.add(permissions.getViewForms());
        }
        if (permissions.getReviewForms() != null
                && !StringUtil.isNullOrEmpty(permissions.getReviewForms().getLabel())
                && (permissions.getReviewForms().isEnabled() || alsoDisabled)) {
            permissions.getReviewForms().setKey("review_forms");
            permissionList.add(permissions.getReviewForms());
        }
        if (permissions.getViewAndSubmitSurveys() != null
                && !StringUtil.isNullOrEmpty(permissions.getViewAndSubmitSurveys().getLabel())
                && (permissions.getViewAndSubmitSurveys().isEnabled() || alsoDisabled)) {
            permissions.getViewAndSubmitSurveys().setKey("view_and_submit_surveys");
            permissionList.add(permissions.getViewAndSubmitSurveys());
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
        Button updatePermissionsButton;

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
            updatePermissionsButton = itemView.findViewById(R.id.updatePermissionsButton);
        }
    }

    public void setCallback(ProfileEditInterface callback) {
        this.callback = callback;
    }

    public interface ProfileEditInterface {
        void onDisconnectClicked(ProfileLink profileLink);
    }
}
