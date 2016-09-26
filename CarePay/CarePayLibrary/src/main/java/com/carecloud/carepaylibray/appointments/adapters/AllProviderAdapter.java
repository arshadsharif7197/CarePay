package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;

public class AllProviderAdapter extends RecyclerView.Adapter<AllProviderAdapter.ProviderViewHolder> {

    private Context context;
    private OnAllListItemClickListener listener;
    private ArrayList<AppointmentModel> providerItems;

    public AllProviderAdapter(Context context, ArrayList<AppointmentModel> providerItems,
                              OnAllListItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.providerItems = providerItems;
    }

    @Override
    public ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View chooseProviderListItemView = LayoutInflater.from(context).inflate(
                R.layout.choose_provider_list_item, parent, false);
        return new ProviderViewHolder(chooseProviderListItemView);
    }

    @Override
    public void onBindViewHolder(final ProviderViewHolder holder, int position) {
        final AppointmentModel item = providerItems.get(position);
        holder.doctorName.setText(item.getDoctorName());
        holder.doctorType.setText(item.getAppointmentType());

        SystemUtil.setGothamRoundedMediumTypeface(context, holder.shortName);
        holder.shortName.setText(SystemUtil.onShortDrName(item.getDoctorName()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAllListItemClickListener(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return providerItems.size();
    }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {

        TextView doctorName, doctorType, shortName;

        ProviderViewHolder(View itemView) {
            super(itemView);

            // set doctor name
            Typeface textViewFont_proximanova_semibold = Typeface.createFromAsset(
                    itemView.getResources().getAssets(), "fonts/proximanova_semibold.otf");
            doctorName = (TextView) itemView.findViewById(R.id.doctor_name);
            doctorName.setTypeface(textViewFont_proximanova_semibold);

            // set doctor type
            Typeface textViewFont_proximanova_regular = Typeface.createFromAsset(
                    itemView.getResources().getAssets(), "fonts/proximanova_regular.otf");
            doctorType = (TextView) itemView.findViewById(R.id.doctor_type);
            doctorType.setTypeface(textViewFont_proximanova_regular);

            shortName = (TextView) itemView.findViewById(R.id.avtarTextView);
        }
    }

    public interface OnAllListItemClickListener {
        void onAllListItemClickListener(int position);
    }
}
