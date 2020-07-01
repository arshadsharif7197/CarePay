package com.carecloud.carepay.patient.messages.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * Created by lmenendez on 7/5/17
 */

public class MessagesProvidersAdapter extends RecyclerView.Adapter<MessagesProvidersAdapter.ViewHolder> {

    public interface SelectProviderCallback {
        void onProviderSelected(ProviderContact provider);
    }

    private Context context;
    private List<ProviderContact> providers;
    private List<UserPracticeDTO> practices;
    private SelectProviderCallback callback;

    /**
     * Constructor
     *
     * @param context   context
     * @param providers list of providers
     * @param practices
     * @param callback  callback
     */
    public MessagesProvidersAdapter(Context context, List<ProviderContact> providers, List<UserPracticeDTO> practices, SelectProviderCallback callback) {
        this.context = context;
        this.providers = providers;
        this.callback = callback;
        this.practices = practices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_messages_providers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProviderContact provider = providers.get(position);
        UserPracticeDTO practice = digPractice(provider.getBusinessEntityId());

        String providerName = provider.getName();
        holder.providerName.setText(providerName);
        holder.speciality.setText(provider.getPrimarySpecialty());
        holder.providerInitials.setText(StringUtil.getShortName(providerName));
        holder.providerImage.setVisibility(View.GONE);
        holder.providerInitials.setVisibility(View.VISIBLE);

        if (practice != null) {
            holder.practiceName.setText(practice.getPracticeName());
            holder.practiceName.setVisibility(View.VISIBLE);
        } else {
            holder.practiceName.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(view -> callback.onProviderSelected(provider));

        if (!StringUtil.isNullOrEmpty(provider.getPhoto())) {
            PicassoHelper.get().loadImage(context, holder.providerImage, holder.providerInitials, provider.getPhoto());
        }
    }

    @Override
    public int getItemCount() {
        return providers.size();
    }


    private UserPracticeDTO digPractice(String providerEntity) {
        for (UserPracticeDTO userPracticeDTO : practices) {
            if (userPracticeDTO.getPracticeId().equals(providerEntity)) {
                return userPracticeDTO;
            }
        }
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView unreadCount;
        ImageView providerImage;
        TextView providerInitials;
        TextView providerName;
        TextView speciality;
        TextView practiceName;

        ViewHolder(View itemView) {
            super(itemView);
            unreadCount = itemView.findViewById(R.id.unread_count);
            providerImage = itemView.findViewById(R.id.provider_image);
            providerInitials = itemView.findViewById(R.id.provider_initials);
            providerName = itemView.findViewById(R.id.provider_name);
            speciality = itemView.findViewById(R.id.provider_speciality);
            practiceName = itemView.findViewById(R.id.practice_name);
        }
    }
}
