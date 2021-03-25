package com.carecloud.carepay.practice.library.signin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.signinsignup.dto.Partners;

import java.util.List;

public class PartnersAdapter extends RecyclerView.Adapter<PartnersAdapter.PracticeViewHolder> {

    private Context context;
    private List<Partners> partnersList;
    private PartnerSelectedListener callback;

    public interface PartnerSelectedListener {
        void onPartnerSelected(Partners partners);
    }

    private UserPracticeDTO selectedPractice;

    /**
     * Constructor
     *
     * @param context      Context
     * @param partnersList list of practices
     * @param callback     select practice callback
     */
    public PartnersAdapter(Context context, List<Partners> partnersList, PartnerSelectedListener callback) {
        this.context = context;
        this.partnersList = partnersList;
        this.callback = callback;
    }


    @Override
    public PracticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_partner_list_row, parent, false);
        return new PracticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PracticeViewHolder holder, int position) {
        holder.tvPartnerLabel.setText(partnersList.get(position).getLabel());

        holder.partnerLayout.setOnClickListener(view -> {
            if (callback != null) {
                callback.onPartnerSelected(partnersList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return partnersList.size();
    }

    public void setSelectedPractice(UserPracticeDTO selectedPractice) {
        this.selectedPractice = selectedPractice;
    }

    class PracticeViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPartner;
        private TextView tvPartnerLabel;
        private LinearLayout partnerLayout;

        public PracticeViewHolder(View itemView) {
            super(itemView);
            this.ivPartner = itemView.findViewById(R.id.iv_partner_image);
            this.tvPartnerLabel = itemView.findViewById(R.id.tv_partner_label);
            this.partnerLayout = itemView.findViewById(R.id.item_partner);
        }
    }
}
