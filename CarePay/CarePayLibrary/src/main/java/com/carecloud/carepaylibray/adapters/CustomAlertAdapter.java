package com.carecloud.carepaylibray.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

import java.util.List;

public class CustomAlertAdapter extends BaseAdapter {

    Context ctx = null;
    List<String> listarray = null;
    private LayoutInflater inflater = null;

    /**
     * @param activty activity
     * @param list list
     */
    public CustomAlertAdapter(Activity activty, List<String> list) {
        this.ctx = activty;
        inflater = activty.getLayoutInflater();
        this.listarray = list;
    }

    @Override
    public int getCount() {

        return listarray.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.alert_list_row, null);
            holder.titlename = (TextView) convertView.findViewById(R.id.textviewName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String dataValue = listarray.get(position);
        holder.titlename.setText(dataValue);

        return convertView;
    }

    private static class ViewHolder {
        TextView titlename;
    }
}
