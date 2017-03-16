package com.carecloud.carepaylibray.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

import java.util.List;

/**
 * Created by pjohnson on 13/03/17.
 */

public class VisitTypeListAdapter extends BaseAdapter {

    private List<VisitTypeDTO> listItems;

    public VisitTypeListAdapter(Context context, List<VisitTypeDTO> items) {
        this.listItems = items;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public String getItem(int position) {
        return listItems.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return listItems.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        VisitTypeListHolder holder;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.dialog_visit_type_list_item, viewGroup, false);

            holder = new VisitTypeListHolder();
            holder.type = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.visitTypeListItem);
            view.setTag(holder);
        } else {
            holder = (VisitTypeListHolder) view.getTag();
        }

        holder.type.setText(getItem(position));
        return view;
    }

    class VisitTypeListHolder {
        TextView type;
    }
}
