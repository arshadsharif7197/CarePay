package com.carecloud.carepay.patient.messages.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 7/5/17
 */

public class MessagesProvidersAdapter extends RecyclerView.Adapter<MessagesProvidersAdapter.ViewHolder> {

    public interface SelectProviderCallback {
        void onProviderSelected(ProviderContact provider);
    }

    private Context context;
    private List<ProviderContact> providers = new ArrayList<>();
    private SelectProviderCallback callback;

    /**
     * Constructor
     *
     * @param context   context
     * @param providers list of providers
     * @param callback  callback
     */
    public MessagesProvidersAdapter(Context context, List<ProviderContact> providers, SelectProviderCallback callback) {
        this.context = context;
        this.providers = providers;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_messages_providers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProviderContact provider = providers.get(position);

        String providerName = provider.getName();
        holder.providerName.setText(providerName);
        holder.providerInitials.setText(StringUtil.getShortName(providerName));
        holder.providerImage.setVisibility(View.GONE);
        holder.providerInitials.setVisibility(View.VISIBLE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onProviderSelected(provider);
            }
        });

        if(!StringUtil.isNullOrEmpty(provider.getPhoto())){
            PicassoHelper.get().loadImage(context, holder.providerImage, holder.providerInitials, provider.getPhoto());
        }
    }

    @Override
    public int getItemCount() {
        return providers.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView unreadCount;
        ImageView providerImage;
        TextView providerInitials;
        TextView providerName;
        TextView providerTitle;

        ViewHolder(View itemView) {
            super(itemView);
            unreadCount = itemView.findViewById(R.id.unread_count);
            providerImage = itemView.findViewById(R.id.provider_image);
            providerInitials = itemView.findViewById(R.id.provider_initials);
            providerName = itemView.findViewById(R.id.provider_name);
            providerTitle = itemView.findViewById(R.id.provider_title);
        }
    }
}
