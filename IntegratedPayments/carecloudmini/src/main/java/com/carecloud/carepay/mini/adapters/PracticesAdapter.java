package com.carecloud.carepay.mini.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 6/24/17
 */

public class PracticesAdapter extends RecyclerView.Adapter<PracticesAdapter.ViewHolder> {

    public interface SelectPracticeListener{
        void onPracticeSelected(UserPracticeDTO selectedPractice);
    }

    private Context context;
    private SelectPracticeListener listener;
    private List<UserPracticeDTO> practices = new ArrayList<>();

    private View lastIndicator;

    /**
     * Constructor
     * @param context Context
     * @param practices list of practices
     * @param listener practice selection listener
     */
    public PracticesAdapter(Context context, List<UserPracticeDTO> practices, SelectPracticeListener listener){
        this.context = context;
        this.practices = practices;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selection_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserPracticeDTO userPracticeDTO = practices.get(position);

        holder.listItemText.setText(userPracticeDTO.getPracticeName());
        holder.listItemIndicator.setSelected(false);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPracticeSelected(userPracticeDTO);
                if(lastIndicator != null){
                    lastIndicator.setSelected(false);
                }
                holder.listItemIndicator.setSelected(true);
                lastIndicator = holder.listItemIndicator;
            }
        });
    }

    @Override
    public int getItemCount() {
        return practices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView listItemText;
        View listItemIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            listItemText = (TextView) itemView.findViewById(R.id.list_item_text);
            listItemIndicator = itemView.findViewById(R.id.list_item_indicator);
        }

    }
}
