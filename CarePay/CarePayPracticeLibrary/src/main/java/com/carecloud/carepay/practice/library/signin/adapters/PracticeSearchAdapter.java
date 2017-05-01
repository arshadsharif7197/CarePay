package com.carecloud.carepay.practice.library.signin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionUserPractice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/9/17.
 */

public class PracticeSearchAdapter extends RecyclerView.Adapter<PracticeSearchAdapter.PracticeViewHolder> {

    public interface SelectPracticeAdapterCallback {
        void onSelectPractice(PracticeSelectionUserPractice practice);
    }

    private Context context;
    private List<PracticeSelectionUserPractice> practiceList = new ArrayList<>();
    private SelectPracticeAdapterCallback callback;

    private PracticeSelectionUserPractice selectedPractice;

    /**
     * Constructor
     * @param context Context
     * @param practiceList list of practices
     * @param callback select practice callback
     */
    public PracticeSearchAdapter(Context context, List<PracticeSelectionUserPractice> practiceList, SelectPracticeAdapterCallback callback){
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
        final PracticeSelectionUserPractice practice = practiceList.get(position);

        holder.getPracticeName().setText(practice.getPracticeName());
//        PicassoHelper.getPicassoInstance(context).load(practice.getPracticePhoto()).into(holder.getPracticeImage());

        holder.getPracticeCheck().setSelected(practice == selectedPractice);

        holder.getItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPractice = practice;
                if(callback!=null){
                    callback.onSelectPractice(practice);
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return practiceList.size();
    }

    public void setSelectedPractice(PracticeSelectionUserPractice selectedPractice) {
        this.selectedPractice = selectedPractice;
    }

    public void setPracticeList(List<PracticeSelectionUserPractice> practiceList) {
        this.practiceList = practiceList;
    }


    class PracticeViewHolder extends RecyclerView.ViewHolder{

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
