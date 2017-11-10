package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.shamrocksdk.connections.models.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 11/9/17
 */

public class IntegratedPaymentsChooseDeviceAdapter extends RecyclerView.Adapter<IntegratedPaymentsChooseDeviceAdapter.ViewHolder> {
    public interface ChooseDeviceCallback{
        void onDeviceSelected(Device device);
    }

    private Context context;
    private List<Device> deviceList = new ArrayList<>();
    private ChooseDeviceCallback callback;

    private View lastSelectedView;

    /**
     * Constructor
     * @param context context
     * @param deviceList device List
     * @param callback callback
     */
    public IntegratedPaymentsChooseDeviceAdapter(Context context, List<Device> deviceList, ChooseDeviceCallback callback){
        this.context = context;
        this.deviceList = deviceList;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_integrated_payment_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Device device = deviceList.get(position);

        holder.deviceName.setText(StringUtil.captialize(device.getName()));
        holder.deviceStatus.setText(StringUtil.captialize(device.getState()).replace("_", ""));

        switch (device.getState()){
            case Device.STATE_OFFLINE:
                holder.deviceStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.button_rounded_red_background));
                break;
            case Device.STATE_IN_USE:
                holder.deviceStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.button_rounded_yellow_background));
                break;
            default:
            case Device.STATE_READY:
                holder.deviceStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.green_rounded_button_selector));
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastSelectedView != null){
                    lastSelectedView.setSelected(false);
                }
                lastSelectedView = holder.layout;
                lastSelectedView.setSelected(true);
                callback.onDeviceSelected(device);
            }
        });

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    /**
     * update device list
     * @param deviceList new device list
     */
    public void setDeviceList(List<Device> deviceList){
        this.deviceList = deviceList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView deviceName;
        TextView deviceStatus;
        View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceStatus = (TextView) itemView.findViewById(R.id.status_flag);
            layout = itemView.findViewById(R.id.device_item_layout);
        }
    }
}
