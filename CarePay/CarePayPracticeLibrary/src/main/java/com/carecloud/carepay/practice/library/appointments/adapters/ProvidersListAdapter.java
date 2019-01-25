package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProvidersListAdapter extends RecyclerView.Adapter<ProvidersListAdapter.ProvidersListViewHolder> {

    private Context context;
    private OnProviderListItemClickListener listener;
    private List<AppointmentResourcesDTO> providersArrayList;

    /**
     * This will create a list of appointments
     *
     * @param context            context
     * @param providersArrayList providersArrayList
     * @param listener           listener
     */
    public ProvidersListAdapter(Context context, List<AppointmentResourcesDTO> providersArrayList,
                                AppointmentsResultModel appointmentInfo,
                                OnProviderListItemClickListener listener) {

        this.context = context;
        this.listener = listener;
        this.providersArrayList = providersArrayList;
    }

    @Override
    public ProvidersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View providersListItemView = LayoutInflater.from(context).inflate(
                R.layout.cardview_provider_item, parent, false);
        return new ProvidersListViewHolder(providersListItemView);
    }

    @Override
    public void onBindViewHolder(final ProvidersListViewHolder holder, int position) {
        AppointmentResourcesDTO resource = providersArrayList.get(position);

        String providerName = resource.getResource().getProvider().getName();
        holder.shortName.setText(StringUtil.getShortName(providerName));
        holder.doctorName.setText(providerName);
        holder.doctorType.setText(resource.getResource().getProvider().getSpecialty().getName());
        holder.scheduleAppointment.setText(Label.getLabel("provider_list_schedule_appointment_button"));
        holder.scheduleAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onProviderListItemClickListener(holder.getAdapterPosition());
            }
        });

        loadImage(holder.providerPicImageView, resource.getResource().getProvider().getPhoto());
    }

    protected void loadImage(final ImageView imageView, String url) {
        int size = context.getResources().getDimensionPixelSize(R.dimen.provider_card_avatar_size);
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

    class ProvidersListViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView shortName;
        private CarePayTextView doctorName;
        private CarePayTextView doctorType;
        private CarePayTextView placeName;
        private CarePayTextView address;
        private Button scheduleAppointment;
        private ImageView providerPicImageView;

        ProvidersListViewHolder(View itemView) {
            super(itemView);

            shortName = itemView.findViewById(R.id.provider_short_name);
            doctorName = itemView.findViewById(R.id.provider_doctor_name);
            doctorType = itemView.findViewById(R.id.provider_doctor_speciality);
            placeName = itemView.findViewById(R.id.provider_place_name);
            address = itemView.findViewById(R.id.provider_place_address);

            scheduleAppointment = itemView.findViewById(R.id.provider_schedule_appointment);
            providerPicImageView = itemView.findViewById(R.id.providerPicImageView);
        }
    }

    public interface OnProviderListItemClickListener {
        void onProviderListItemClickListener(int position);
    }
}
