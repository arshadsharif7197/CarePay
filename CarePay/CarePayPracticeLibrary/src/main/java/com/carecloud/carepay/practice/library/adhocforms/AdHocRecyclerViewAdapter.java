package com.carecloud.carepay.practice.library.adhocforms;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;

import java.util.List;

/**
 * @author pjohnson on 10/07/17.
 */

public class AdHocRecyclerViewAdapter extends RecyclerView.Adapter<AdHocRecyclerViewAdapter.ViewHolder> {

    private final List<String> forms;

    public AdHocRecyclerViewAdapter(List<String> forms) {
        this.forms = forms;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adhoc_form_name, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.formNameTextView.setText(forms.get(position));
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView formNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            formNameTextView = (TextView) itemView.findViewById(R.id.formNameTextView);
        }
    }
}
