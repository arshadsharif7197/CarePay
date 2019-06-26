package com.carecloud.carepay.patient.delegate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.profile.Permission;

import java.util.List;

/**
 * @author pjohnson on 2019-06-14.
 */
class PermissionsNameAdapter extends RecyclerView.Adapter<PermissionsNameAdapter.ViewHolder> {

    private final List<Permission> permissionsList;

    public PermissionsNameAdapter(List<Permission> permissionsList) {
        this.permissionsList = permissionsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_permission_name, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Permission permission = permissionsList.get(position);
        holder.permissionNameTextView.setText(Label.getLabel(permission.getLabel()));
    }

    @Override
    public int getItemCount() {
        return permissionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView permissionNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            permissionNameTextView = itemView.findViewById(R.id.permissionNameTextView);
        }
    }
}
