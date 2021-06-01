package com.carecloud.carepay.practice.library.signin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.signinsignup.dto.Partners;

import java.util.List;

/**
 * Created by lmenendez on 3/9/17.
 */

public class PartnersSearchAdapter extends RecyclerView.Adapter<PartnersSearchAdapter.PracticeViewHolder> {

    public interface SelectPracticeAdapterCallback {
        void onSelectPractice(Partners practice);
    }

    private Context context;
    private List<Partners> practiceList;
    private SelectPracticeAdapterCallback callback;

    private Partners selectedPractice;

    /**
     * Constructor
     *
     * @param context      Context
     * @param practiceList list of practices
     * @param callback     select practice callback
     */
    public PartnersSearchAdapter(Context context, List<Partners> practiceList, SelectPracticeAdapterCallback callback) {
        this.context = context;
        this.practiceList = practiceList;
        this.callback = callback;
    }


    @Override
    public PracticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_practice_list_row, parent, false);
        return new PracticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PracticeViewHolder holder, int position) {
        final Partners practice = practiceList.get(position);

        holder.getPracticeName().setText(practice.getLabel());
        if (selectedPractice == null || !practice.getPracticeMgmt().equals(selectedPractice.getPracticeMgmt())) {
            holder.getPracticeCheck().setSelected(false);
        } else {
            holder.getPracticeCheck().setSelected(true);
        }

        holder.getItem().setOnClickListener(view -> {
            selectedPractice = practice;
            if (callback != null) {
                callback.onSelectPractice(practice);
            }
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return practiceList != null ? practiceList.size() : 0;
    }

    public void setSelectedPractice(Partners selectedPractice) {
        this.selectedPractice = selectedPractice;
    }

    public void setPracticeList(List<Partners> practiceList) {
        this.practiceList = practiceList;
    }


    class PracticeViewHolder extends RecyclerView.ViewHolder {

        private ImageView practiceImage;
        private TextView practiceName;
        private ImageView practiceCheck;

        private View item;

        public PracticeViewHolder(View itemView) {
            super(itemView);
            this.practiceImage = (ImageView) itemView.findViewById(R.id.practiceImage);
            this.practiceName = (TextView) itemView.findViewById(R.id.practice_name);
            this.practiceCheck = (ImageView) itemView.findViewById(R.id.practice_check);
            this.item = itemView;
        }

        public ImageView getPracticeImage() {
            return practiceImage;
        }

        public void setPracticeImage(ImageView practiceImage) {
            this.practiceImage = practiceImage;
        }

        public TextView getPracticeName() {
            return practiceName;
        }

        public void setPracticeName(TextView practiceName) {
            this.practiceName = practiceName;
        }

        public ImageView getPracticeCheck() {
            return practiceCheck;
        }

        public void setPracticeCheck(ImageView practiceCheck) {
            this.practiceCheck = practiceCheck;
        }

        public View getItem() {
            return item;
        }
    }

}
