package com.carecloud.carepaylibray.medications.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
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

        void restoreItem(MedicationsAllergiesObject item);
    }

    private List<? extends MedicationsAllergiesObject> items;
    private MedicationAllergiesAdapterCallback callback;
    private Context context;
    private ApplicationMode.ApplicationType applicationType;

    /**
     * Initialization
     *
     * @param items    list of MedicationAlergyObject
     * @param callback calback for removing an item
     */
    public MedicationAllergiesAdapter(Context context, ApplicationMode.ApplicationType applicationType,
                                      List<? extends MedicationsAllergiesObject> items,
                                      MedicationAllergiesAdapterCallback callback) {
        this.context = context;
        this.applicationType = applicationType;
        this.items = items;
        this.callback = callback;
    }


    @Override
    public MedicationAllergiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
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

        if (item.isDeleted()) {
            holder.delete.setTextColor(ContextCompat.getColor(context, R.color.lightning_yellow));
            holder.delete.setText(Label.getLabel("medication_allergies_undo_button"));
            holder.strike.setVisibility(View.VISIBLE);
            if (holder.name.getLineCount() > 1) {
                holder.strike2.setVisibility(View.VISIBLE);
            }
            if (applicationType != ApplicationMode.ApplicationType.PATIENT) {
                holder.delete.setBackgroundResource(R.drawable.button_round_orange_border);
            }
        } else {
            holder.delete.setTextColor(ContextCompat.getColor(context, R.color.remove_red));
            holder.delete.setText(Label.getLabel("medication_allergies_delete_button"));
            holder.strike.setVisibility(View.GONE);
            holder.strike2.setVisibility(View.GONE);
            if (applicationType != ApplicationMode.ApplicationType.PATIENT) {
                holder.delete.setBackgroundResource(R.drawable.button_round_red_border);
            }
        }

        holder.divider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    if (item.isDeleted()) {
                        callback.restoreItem(item);
                    } else {
                        callback.deleteItem(item);
                    }
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
        View strike;
        View strike2;
        View divider;

        MedicationAllergiesViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.medication_allergy_text_view);
            this.delete = itemView.findViewById(R.id.medication_allergy_delete_button);
            this.strike = itemView.findViewById(R.id.medication_allergy_strike);
            this.strike2 = itemView.findViewById(R.id.medication_allergy_strike2);
            this.divider = itemView.findViewById(R.id.divider);
        }
    }
}
