package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;

import java.util.ArrayList;
import java.util.List;

public class ProviderSearchAdapter extends RecyclerView.Adapter<ProviderSearchAdapter.PracticeViewHolder> {

    public interface SelectPracticeAdapterCallback {
        void onSelectPractice(AppointmentResourcesDTO practice);
    }

    private Context context;
    private List<AppointmentResourcesDTO> practiceList = new ArrayList<>();
    private SelectPracticeAdapterCallback callback;

    private AppointmentResourcesDTO selectedPractice;

    /**
     * Constructor
     * @param context Context
     * @param practiceList list of practices
     * @param callback select practice callback
     */
    public ProviderSearchAdapter(Context context, List<AppointmentResourcesDTO> practiceList, SelectPracticeAdapterCallback callback){
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
        final AppointmentResourcesDTO practice = practiceList.get(position);

        holder.item.setContentDescription(practice.getResource().getProvider().getName());
        holder.getPracticeName().setText(practice.getResource().getProvider().getName());

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

    public void setSelectedPractice(AppointmentResourcesDTO selectedPractice) {
        this.selectedPractice = selectedPractice;
    }

    public void setPracticeList(List<AppointmentResourcesDTO> practiceList) {
        this.practiceList = practiceList;
    }


    class PracticeViewHolder extends RecyclerView.ViewHolder{

        private TextView practiceName;
        private ImageView practiceCheck;

        private View item;

        PracticeViewHolder(View itemView) {
            super(itemView);
            this.practiceName = (TextView) itemView.findViewById(R.id.practice_name);
            this.practiceCheck = (ImageView) itemView.findViewById(R.id.practice_check);
            this.item = itemView;
        }

        TextView getPracticeName() {
            return practiceName;
        }

        ImageView getPracticeCheck() {
            return practiceCheck;
        }

        public View getItem() {
            return item;
        }
    }
}
