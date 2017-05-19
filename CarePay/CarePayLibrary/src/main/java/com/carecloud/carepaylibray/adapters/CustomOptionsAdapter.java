package com.carecloud.carepaylibray.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;

import java.util.ArrayList;
import java.util.List;

public class CustomOptionsAdapter extends BaseAdapter {

    Context context = null;
    List<DemographicsOption> optionsList = new ArrayList<>();

    /**
     * @param context context
     * @param list list
     */
    public CustomOptionsAdapter(Context context, List<DemographicsOption> list) {
        this.context = context;
        this.optionsList = list;
    }

    @Override
    public int getCount() {

        return optionsList.size();
    }

    @Override
    public DemographicsOption getItem(int position) {
        return optionsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.alert_list_row, container, false);
            holder.titlename = (TextView) convertView.findViewById(R.id.textviewName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String dataValue = optionsList.get(position).getLabel();
        holder.titlename.setText(dataValue);

        return convertView;
    }

    private static class ViewHolder {
        TextView titlename;
    }
}
