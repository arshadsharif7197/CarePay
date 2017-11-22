package com.carecloud.carepay.patient.retail.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 11/20/17
 */

public class RetailStoreListAdapter extends RecyclerView.Adapter<RetailStoreListAdapter.ViewHolder> {
    public interface RetailSelectionCallback{
        void onStoreSelected(UserPracticeDTO userPracticeDTO);
    }

    private Context context;
    private List<UserPracticeDTO> retailPractices = new ArrayList<>();
    private RetailSelectionCallback callback;

    public RetailStoreListAdapter(Context context, List<UserPracticeDTO> retailPractices, RetailSelectionCallback callback){
        this.context = context;
        this.retailPractices = retailPractices;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.retail_store_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserPracticeDTO userPracticeDTO = retailPractices.get(position);

        holder.practiceName.setText(userPracticeDTO.getPracticeName());
        holder.practiceInitials.setText(StringUtil.getShortName(userPracticeDTO.getPracticeName()));
        holder.practiceAddress.setText(userPracticeDTO.getAddressDTO().getFullAddress());

        Picasso.with(context)
                .load(userPracticeDTO.getPracticePhoto())
                .resize(60,60)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(holder.practiceIcon, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.practiceInitials.setVisibility(View.GONE);
                        holder.practiceIcon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.practiceInitials.setVisibility(View.VISIBLE);
                        holder.practiceIcon.setVisibility(View.GONE);
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onStoreSelected(userPracticeDTO);
            }
        });
    }

    @Override
    public int getItemCount() {
        return retailPractices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView practiceName;
        TextView practiceAddress;
        TextView practiceInitials;
        ImageView practiceIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            practiceName = (TextView) itemView.findViewById(R.id.practice_name);
            practiceAddress = (TextView) itemView.findViewById(R.id.practice_address);
            practiceInitials = (TextView) itemView.findViewById(R.id.practiceInitials);
            practiceIcon = (ImageView) itemView.findViewById(R.id.practice_icon);
        }
    }
}
