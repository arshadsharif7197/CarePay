package com.carecloud.carepaylibray.medications.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.medications.models.AllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;

import java.util.List;

/**
 * Created by lmenendez on 2/16/17
 */

public class MedicationAllergiesAdapter extends RecyclerView.Adapter<MedicationAllergiesAdapter.MedicationAllergiesViewHolder> {

    public interface MedicationAllergiesAdapterCallback {

        void deleteItem(MedicationsAllergiesObject item);
    }

    private List<? extends MedicationsAllergiesObject> items;
    private MedicationAllergiesAdapterCallback callback;

    /**
     * Initialization
     *
     * @param items    list of MedicationAlergyObject
     * @param callback calback for removing an item
     */
    public MedicationAllergiesAdapter(List<? extends MedicationsAllergiesObject> items,
                                      MedicationAllergiesAdapterCallback callback) {
        this.items = items;
        this.callback = callback;
    }


    @Override
    public MedicationAllergiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_medication_list_row, parent, false);
        return new MedicationAllergiesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicationAllergiesViewHolder holder, int position) {
        final MedicationsAllergiesObject item = items.get(position);
        if (item instanceof AllergiesObject) {
            holder.name.setText(((AllergiesObject) item).getInteroperableDesc());
        } else {
            holder.name.setText(item.getDisplayName());
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.deleteItem(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<? extends MedicationsAllergiesObject> items) {
        this.items = items;
    }


    class MedicationAllergiesViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView delete;

        MedicationAllergiesViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.medication_allergy_text_view);
            this.delete = (TextView) itemView.findViewById(R.id.medication_allergy_delete_button);
        }
    }
}
