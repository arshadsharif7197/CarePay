package com.carecloud.carepay.patient.delegate.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.profile.Permission;

import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 2019-06-14.
 */
public class DelegatePermissionsNameAdapter extends RecyclerView.Adapter<DelegatePermissionsNameAdapter.ViewHolder> {

    private final List<Permission> permissionsList;
    private final Map<String, List<Permission>> permissionMap;
    private final DelegateNameItemInterface callback;
    private final boolean showCheckBoxes;

    DelegatePermissionsNameAdapter(List<Permission> permissionsList,
                                   Map<String, List<Permission>> permissionMap,
                                   boolean showCheckBoxes,
                                   DelegateNameItemInterface callback) {
        this.permissionsList = permissionsList;
        this.permissionMap = permissionMap;
        this.showCheckBoxes = showCheckBoxes;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delegate_permission_name, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Permission permission = permissionsList.get(position);
        holder.permissionNameTextView.setText(Label.getLabel(permission.getLabel()));
        holder.permissionCheckBox.setVisibility(showCheckBoxes ? View.VISIBLE : View.GONE);
        holder.permissionCheckBox.setOnCheckedChangeListener(null);
        holder.permissionCheckBox.setChecked(permission.isEnabled());
        holder.permissionCheckBox.setEnabled(permission.isCheckBoxEnabled());
        holder.permissionCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (permission.getParent() == null) {
                changeParentAndChildren(permission, isChecked);
            } else {
                permission.setEnabled(isChecked);
                notifyItemChanged(position);
            }
            callback.enableUpdatePermissionsButton(checkIfEnableButton(permissionsList));
        });
        if (permission.getParent() == null) {
            holder.fakeView.setVisibility(View.GONE);
            holder.fakeTinyView.setVisibility(View.GONE);
            holder.permissionNameTextView.setTypeface(holder.permissionNameTextView.getTypeface(),
                    Typeface.BOLD);
        } else {
            holder.permissionNameTextView.setTypeface(holder.permissionNameTextView.getTypeface());
            holder.fakeView.setVisibility(View.VISIBLE);
            holder.fakeTinyView.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkIfEnableButton(List<Permission> permissionsList) {
        for (Permission permission : permissionsList) {
            if (permission.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void changeParentAndChildren(Permission parentPermission, boolean isChecked) {
        List<Permission> children = permissionMap.get(parentPermission.getKey());
        if (children != null) {
            for (Permission permission : children) {
                if (permission.getParent().equals(parentPermission.getKey())) {
                    permission.setEnabled(isChecked);
                    permission.setCheckBoxEnabled(isChecked);
                }
            }
        }
        parentPermission.setEnabled(isChecked);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return permissionsList.size();
    }

    public List<Permission> getPermissions() {
        return permissionsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView permissionNameTextView;
        View fakeView;
        View fakeTinyView;
        CheckBox permissionCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            permissionNameTextView = itemView.findViewById(R.id.permissionNameTextView);
            fakeView = itemView.findViewById(R.id.fakeView);
            fakeTinyView = itemView.findViewById(R.id.fakeTinyView);
            permissionCheckBox = itemView.findViewById(R.id.permissionCheckBox);
        }
    }

    interface DelegateNameItemInterface {
        void enableUpdatePermissionsButton(boolean enable);
    }
}
