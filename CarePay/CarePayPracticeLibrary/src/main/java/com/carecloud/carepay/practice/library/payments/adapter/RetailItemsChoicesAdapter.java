package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.retail.models.RetailItemOptionChoiceDto;

import java.util.List;

public class RetailItemsChoicesAdapter extends RecyclerView.Adapter<RetailItemsChoicesAdapter.ViewHolder> {

    private Context context;
    private List<RetailItemOptionChoiceDto> retailItemOptionChoices;
    private int defaultChoice;
    private RetailChoiceSelectedListener listener;
    private int priority;

    private ViewHolder selectedChoice = null;

    public RetailItemsChoicesAdapter(Context context,
                                     @NonNull List<RetailItemOptionChoiceDto> retailItemOptionChoices,
                                     int defaultChoice,
                                     RetailChoiceSelectedListener listener,
                                     int priority){
        this.context = context;
        this.retailItemOptionChoices = retailItemOptionChoices;
        this.defaultChoice = defaultChoice;
        this.listener = listener;
        this.priority = priority;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_retail_choices_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RetailItemOptionChoiceDto choice = retailItemOptionChoices.get(position);

        holder.choiceName.setText(choice.getName());

        if(selectedChoice == null && position == defaultChoice){
            holder.choiceName.setSelected(true);
            selectedChoice = holder;
        }else if(holder == selectedChoice){
            holder.choiceName.setSelected(true);
        }else{
            holder.choiceName.setSelected(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedChoice != null){
                    selectedChoice.choiceName.setSelected(false);
                }
                selectedChoice = holder;
                holder.choiceName.setSelected(true);
                listener.onRetailChoiceSelected(choice, priority);
            }
        });

    }

    @Override
    public int getItemCount() {
        return retailItemOptionChoices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView choiceName;

        public ViewHolder(View itemView) {
            super(itemView);
            choiceName = (TextView) itemView.findViewById(R.id.retail_choice);
        }
    }

    public interface RetailChoiceSelectedListener {
        void onRetailChoiceSelected(RetailItemOptionChoiceDto choiceDto, int priority);
    }
}
