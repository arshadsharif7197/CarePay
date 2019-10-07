package com.carecloud.carepay.patient.delegate.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.profile.Permission;
import com.carecloud.carepaylibray.profile.ProfileLink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 2019-08-01.
 */
public class DelegatePermissionRecyclerAdapter extends ProfilePermissionsRecyclerAdapter {
    private DelegateEditInterface callback;

    public DelegatePermissionRecyclerAdapter(List<ProfileLink> links,
                                             Map<String, UserPracticeDTO> practicesMap,
                                             boolean showButtons) {
        super(links, practicesMap, showButtons);
    }

    protected void setUpPermissionsNames(ViewHolder viewHolder, ProfileLink profileLink) {
        List<Permission> permissions = getPermissionsList(profileLink
                .getPermissionDto().getPermissions(), true);
        if (viewHolder.profilePermissionRecycler.getAdapter() == null) {
            viewHolder.profilePermissionRecycler.setLayoutManager(new LinearLayoutManager(viewHolder
                    .profilePermissionRecycler.getContext()));
            Map<String, List<Permission>> permissionMap = getPermissionMap(profileLink, permissions);
            initializeCheckboxes(permissions);
            viewHolder.profilePermissionRecycler.setAdapter(new DelegatePermissionsNameAdapter(permissions,
                    permissionMap, showButtons, enable -> viewHolder.updatePermissionsButton.setEnabled(enable)));
        }
    }

    private HashMap<String, List<Permission>> getPermissionMap(ProfileLink profileLink, List<Permission> permissions) {
        HashMap<String, List<Permission>> map = new HashMap<>();
        for (Permission permission : profileLink.getPermissionsHierarchy()) {
            if (permission.getParent() != null) {
                if (!map.containsKey(permission.getParent())) {
                    map.put(permission.getParent(), new ArrayList<>());
                }
                Permission sharedPermission = getPermissionFromList(permissions, permission, true);
                map.get(permission.getParent()).add(sharedPermission);
            } else {
                Permission sharedPermission = getPermissionFromList(permissions, permission, false);
                sharedPermission.setCheckBoxEnabled(true);
            }
        }
        return map;
    }

    private Permission getPermissionFromList(List<Permission> permissions, Permission mapPermission, boolean setParent) {
        for (Permission permission : permissions) {
            if (permission.getKey().equals(mapPermission.getKey())) {
                if (setParent) {
                    permission.setParent(mapPermission.getParent());
                }
                return permission;
            }
        }
        return null;
    }

    private void initializeCheckboxes(List<Permission> permissions) {
        Permission parent = null;
        for (Permission permission : permissions) {
            if (permission.getParent() == null) {
                parent = permission;
            } else {
                permission.setCheckBoxEnabled(parent.isEnabled());
            }

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ProfileLink profileLink = links.get(position);
        holder.disconnectButton.setText(Label.getLabel("practice.delegation.button.label.revoke"));
        holder.disconnectButton.setOnClickListener(v -> {
            callback.onRevokedAccessClicked(profileLink);
        });
        holder.updatePermissionsButton.setVisibility(showButtons ? View.VISIBLE : View.GONE);
        holder.updatePermissionsButton.setOnClickListener(v -> {
            List<Permission> permissionsList = ((DelegatePermissionsNameAdapter) holder.profilePermissionRecycler
                    .getAdapter()).getPermissions();
            callback.onUpdatePermissionsClicked(profileLink, permissionsList);
        });
    }

    public void setCallback(DelegateEditInterface callback) {
        this.callback = callback;
    }

    public interface DelegateEditInterface {
        void onUpdatePermissionsClicked(ProfileLink profileLink, List<Permission> permissions);

        void onRevokedAccessClicked(ProfileLink profileLink);
    }
}
