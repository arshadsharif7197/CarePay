package com.carecloud.carepay.patient.appointments.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.carecloud.carepay.patient.appointments.fragments.ChooseProviderFragment;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.ProvidersScheduleDTO;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {

    private Context context;
    private List<Object> providersArrayList;
    private OnProviderListItemClickListener listener;
    private ChooseProviderFragment chooseProviderFragment;

    /**
     * Constructor.
     * @param context context
     * @param providersArrayList list of providers
     * @param listener Onclick listener
     * @param chooseProviderFragment screen instance
     */
    public ProviderAdapter(Context context, List<Object> providersArrayList,
                           OnProviderListItemClickListener listener,
                           ChooseProviderFragment chooseProviderFragment) {

        this.context = context;
        this.listener = listener;
        this.providersArrayList = providersArrayList;
        this.chooseProviderFragment = chooseProviderFragment;
    }

    @Override
    public ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View chooseProviderListItemView = LayoutInflater.from(context).inflate(
                R.layout.choose_provider_list_item, parent, false);
        return new ProviderViewHolder(chooseProviderListItemView);
    }

    @Override
    public void onBindViewHolder(final ProviderViewHolder holder, int position) {
        final Object object = providersArrayList.get(position);

        View view = chooseProviderFragment.getView();
        if (view == null) {
            return;
        }

        if (object.getClass() == ProvidersScheduleDTO.class) {
            holder.providerSectionLinearLayout.setVisibility(View.GONE);
            holder.providerItemLinearLayout.setVisibility(View.VISIBLE);

            ProvidersScheduleDTO provider = (ProvidersScheduleDTO) object;
            holder.doctorName.setText(provider.getName());
            holder.doctorType.setText(provider.getSpecialty());
            holder.shortName.setText(StringUtil.onShortDrName(provider.getName()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View listItem) {
                    listener.onProviderListItemClickListener(holder.getAdapterPosition());
                }
            });
        } else {
            AppointmentSectionHeaderModel item = (AppointmentSectionHeaderModel) object;
            if (position == 0) {
                holder.providerSectionLinearLayout.setVisibility(View.GONE);
                holder.providerItemLinearLayout.setVisibility(View.GONE);

                CustomProxyNovaSemiBoldLabel providerStickyHeaderTitle =
                        (CustomProxyNovaSemiBoldLabel) view.findViewById(R.id.providers_sticky_header_title);
                providerStickyHeaderTitle.setText(item.getAppointmentHeader());
                providerStickyHeaderTitle.setTextColor(
                        ContextCompat.getColor(view.getContext(), R.color.lightSlateGray));
                providerStickyHeaderTitle.setVisibility(View.VISIBLE);
            } else {
                String title = item.getAppointmentHeader();
                holder.providerSectionLinearLayout.setVisibility(View.VISIBLE);
                holder.providerItemLinearLayout.setVisibility(View.GONE);
                holder.providerSectionHeaderTitle.setText(title);
            }
        }
    }

    @Override
    public int getItemCount() {
        return providersArrayList.size();
    }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {

        private CustomGothamRoundedMediumLabel shortName;
        private CustomProxyNovaSemiBoldLabel doctorName;
        private CustomProxyNovaRegularLabel doctorType;

        private CustomGothamRoundedMediumLabel providerSectionHeaderTitle;
        private LinearLayout providerSectionLinearLayout;
        private LinearLayout providerItemLinearLayout;

        ProviderViewHolder(View itemView) {
            super(itemView);

            // set doctor name
            doctorName = (CustomProxyNovaSemiBoldLabel) itemView.findViewById(R.id.doctor_name);
            // set doctor type
            doctorType = (CustomProxyNovaRegularLabel) itemView.findViewById(R.id.doctor_type);
            // set doctor short name
            shortName = (CustomGothamRoundedMediumLabel) itemView.findViewById(R.id.avatarTextView);

            providerSectionLinearLayout = (LinearLayout)
                    itemView.findViewById(R.id.providers_section_linear_layout);
            providerItemLinearLayout = (LinearLayout)
                    itemView.findViewById(R.id.provider_item_linear_layout);
            providerSectionHeaderTitle = (CustomGothamRoundedMediumLabel)
                    itemView.findViewById(R.id.providers_section_header_title);
        }
    }

    public interface OnProviderListItemClickListener {
        void onProviderListItemClickListener(int position);
    }
}
