package com.carecloud.carepay.patient.delegate.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private List<Permission> permissions;

    public DelegatePermissionRecyclerAdapter(List<ProfileLink> links,
                                             Map<String, UserPracticeDTO> practicesMap,
                                             boolean showButtons) {
        super(links, practicesMap, showButtons);
    }

    protected void setUpPermissionsNames(RecyclerView recyclerView, ProfileLink profileLink) {
        permissions = getPermissionsList(profileLink
                .getPermissionDto().getPermissions(), true);
        if (recyclerView.getAdapter() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            Map<String, List<Permission>> permissionMap = getPermissionMap(profileLink, permissions);
            initializeCheckboxes(permissions);
            recyclerView.setAdapter(new DelegatePermissionsNameAdapter(permissions, permissionMap));
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
        holder.updatePermissionsButton.setVisibility(View.VISIBLE);
        holder.updatePermissionsButton.setOnClickListener(v -> callback
                .onUpdatePermissionsClicked(profileLink, permissions));
    }

    public void setCallback(DelegateEditInterface callback) {
        this.callback = callback;
    }

    public interface DelegateEditInterface {
        void onUpdatePermissionsClicked(ProfileLink profileLink, List<Permission> permissions);

        void onRevokedAccessClicked(ProfileLink profileLink);
    }
}
