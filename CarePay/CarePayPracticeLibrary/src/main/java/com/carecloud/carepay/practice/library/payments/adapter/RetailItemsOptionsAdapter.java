package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.retail.models.RetailItemOptionDto;

import java.util.List;

public class RetailItemsOptionsAdapter extends RecyclerView.Adapter<RetailItemsOptionsAdapter.ViewHolder> {

    private Context context;
    private List<RetailItemOptionDto> retailItemOptions;
    private RetailItemsChoicesAdapter.RetailChoiceSelectedListener listener;

    public RetailItemsOptionsAdapter(Context context,
                                     @NonNull List<RetailItemOptionDto> retailItemOptions,
                                     RetailItemsChoicesAdapter.RetailChoiceSelectedListener listener){
        this.context = context;
        this.retailItemOptions = retailItemOptions;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_retail_option_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RetailItemOptionDto retailOption = retailItemOptions.get(position);

        holder.optionName.setText(retailOption.getName());
        holder.choicesRecycler.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        setAdapter(holder.choicesRecycler, retailOption, position);
    }

    @Override
    public int getItemCount() {
        return retailItemOptions.size();
    }

    private void setAdapter(RecyclerView choicesRecycler, RetailItemOptionDto optionDto, int position){
        RetailItemsChoicesAdapter adapter = new RetailItemsChoicesAdapter(context,
                optionDto.getChoices(), optionDto.getDefaultChoice(), listener, position);
        choicesRecycler.setAdapter(adapter);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView optionName;
        RecyclerView choicesRecycler;

        public ViewHolder(View itemView) {
            super(itemView);
            optionName = (TextView) itemView.findViewById(R.id.option_name);
            choicesRecycler = (RecyclerView) itemView.findViewById(R.id.choices_recycler);
        }
    }
}
