package com.carecloud.carepay.patient.appointments.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.fragments.ChooseProviderFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {

    private Context context;
    private List<Object> providersArrayList;
    private OnProviderListItemClickListener listener;
    private ChooseProviderFragment chooseProviderFragment;

    /**
     * Constructor.
     *
     * @param context                context
     * @param providersArrayList     list of providers
     * @param listener               Onclick listener
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

        if (object.getClass() == AppointmentResourcesDTO.class) {
            holder.providerSectionLinearLayout.setVisibility(View.GONE);
            holder.providerItemLinearLayout.setVisibility(View.VISIBLE);

            AppointmentResourcesDTO resources = (AppointmentResourcesDTO) object;
            String providerName = resources.getResource().getProvider().getName();

            holder.shortName.setText(StringUtil.getShortName(providerName));
            holder.doctorName.setText(providerName);
            holder.doctorType.setText(resources.getResource().getProvider().getSpecialty().getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View listItem) {
                    listener.onProviderListItemClickListener(holder.getAdapterPosition());
                }
            });
            loadImage(holder.providerPicImageView, resources.getResource().getProvider().getPhoto());
        } else {
            AppointmentSectionHeaderModel item = (AppointmentSectionHeaderModel) object;
            if (position == 0) {
                holder.providerSectionLinearLayout.setVisibility(View.GONE);
                holder.providerItemLinearLayout.setVisibility(View.GONE);

                CarePayTextView providerStickyHeaderTitle =
                        view.findViewById(R.id.providers_sticky_header_title);
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

    private void loadImage(final ImageView imageView, String url) {
        int size = context.getResources().getDimensionPixelSize(R.dimen.payment_details_dialog_icon_size);
        Picasso.with(context).load(url)
                .resize(size, size)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        imageView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return providersArrayList.size();
    }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView shortName;
        private CarePayTextView doctorName;
        private CarePayTextView doctorType;
        private CarePayTextView providerSectionHeaderTitle;
        private ImageView providerPicImageView;

        private LinearLayout providerSectionLinearLayout;
        private LinearLayout providerItemLinearLayout;

        ProviderViewHolder(View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorType = itemView.findViewById(R.id.doctor_type);
            shortName = itemView.findViewById(R.id.avatarTextView);
            providerSectionLinearLayout = itemView.findViewById(R.id.providers_section_linear_layout);
            providerItemLinearLayout = itemView.findViewById(R.id.provider_item_linear_layout);
            providerSectionHeaderTitle = itemView.findViewById(R.id.providers_section_header_title);
            providerPicImageView = itemView.findViewById(R.id.providerPicImageView);
        }
    }

    public interface OnProviderListItemClickListener {
        void onProviderListItemClickListener(int position);
    }
}
