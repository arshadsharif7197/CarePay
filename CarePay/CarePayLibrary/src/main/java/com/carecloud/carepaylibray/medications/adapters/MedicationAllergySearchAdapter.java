package com.carecloud.carepaylibray.medications.adapters;

import android.content.Context;
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
 * Created by lmenendez on 2/16/17.
 */

public class MedicationAllergySearchAdapter extends RecyclerView.Adapter<MedicationAllergySearchAdapter.SearchViewHolder> {

    public interface SearchItemSelectedCallback {
        void searchItemSelected(MedicationsAllergiesObject item);
    }

    private Context context;
    private List<? extends MedicationsAllergiesObject> items;
    private SearchItemSelectedCallback callback;

    /**
     * @param context    Context for adapter
     * @param items      list of medication or allergies
     * @param callback   callback for selecting items
     * @param searchMode
     */
    public MedicationAllergySearchAdapter(Context context, List<? extends MedicationsAllergiesObject> items,
                                          SearchItemSelectedCallback callback, int searchMode) {
        this.context = context;
        this.items = items;
        this.callback = callback;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_medication_search_row, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        final MedicationsAllergiesObject item = items.get(position);
        if (item instanceof AllergiesObject) {
            holder.getItemText().setText(((AllergiesObject) item).getInteroperableDesc());
        } else {
            holder.getItemText().setText(item.getDisplayName());
        }

        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.searchItemSelected(item);
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

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        private TextView itemText;
        private View rootView;

        SearchViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.itemText = (TextView) itemView.findViewById(R.id.search_text_view);
        }

        public TextView getItemText() {
            return itemText;
        }

        public void setItem(TextView itemText) {
            this.itemText = itemText;
        }

        public View getRootView() {
            return rootView;
        }

        public void setRootView(View rootView) {
            this.rootView = rootView;
        }
    }
}
