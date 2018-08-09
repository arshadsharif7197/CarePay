package com.carecloud.carepay.practice.library.checkin.filters;

/**
 * Created by sudhir_pingale on 10/19/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter for displaying an array of FilterableDataDTO objects.
 */
public class CustomSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private Context context;
    private List<FilterDataDTO> filteredPatients;
    private List<FilterDataDTO> allPatients;
    private Filter filter;

    // private boolean isPatientListData = false;
    private OnSearchChangedListener searchChangedListener;

    /**
     * The callback used to indicate the user selected the filter.
     */
    public interface OnSearchChangedListener {

        /**
         * Called upon a filter change.
         *
         * @param filteredData The Data DTO that is filtered.
         */
        void onSearchChanged(FilterDataDTO filteredData);
    }

    /**
     * Constructor.
     *
     * @param context context
     * @param items   list of occurrence
     */
    public CustomSearchAdapter(Context context, OnSearchChangedListener searchChangedListener,
                               List<FilterDataDTO> items) {
        this.context = context;
        this.filteredPatients = items;
        this.allPatients = items;
        this.searchChangedListener = searchChangedListener;
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.filteredPatients.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View customFilterListDataRow = inflater.inflate(R.layout.custom_filter_patient_list_item,
                viewGroup, false);
        return new ViewHolderFilterableDataItem(customFilterListDataRow);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        FilterDataDTO filterDataDTO = this.filteredPatients.get(position);
        ViewHolderFilterableDataItem vhDataItem = (ViewHolderFilterableDataItem) viewHolder;
        vhDataItem.setFilterDataDTO(filterDataDTO);
        viewHolder.itemView.setTag(vhDataItem);
    }

    /**
     * Custom ViewHolder for displaying an FilterableDataItem in RecyclerView.
     */
    private class ViewHolderFilterableDataItem extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private ImageView patientImageView;
        private ImageView selectedItemImageView;
        private FilterDataDTO filterDataDTO;

        ViewHolderFilterableDataItem(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.patientItemCheckBox);
            patientImageView = (ImageView) view.findViewById(R.id.patientImageView);
            selectedItemImageView = (ImageView) view.findViewById(R.id.selectedItemImageView);
            SystemUtil.setProximaNovaSemiboldTypeface(context, checkBox);
            patientImageView.setVisibility(View.VISIBLE);
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                filterDataDTO.setChecked(isChecked);
                if (filterDataDTO.isChecked()) {
                    selectedItemImageView.setVisibility(View.VISIBLE);
                } else {
                    selectedItemImageView.setVisibility(View.GONE);
                }
                searchChangedListener.onSearchChanged(filterDataDTO);
            }
        };


        public void setFilterDataDTO(FilterDataDTO filterDataDTO) {
            this.filterDataDTO = filterDataDTO;
            checkBox.setText(filterDataDTO.getDisplayText());
            checkBox.setChecked(filterDataDTO.isChecked());
            if (filterDataDTO.isChecked()) {
                selectedItemImageView.setVisibility(View.VISIBLE);
            } else {
                selectedItemImageView.setVisibility(View.GONE);
            }
            if (!StringUtil.isNullOrEmpty(filterDataDTO.getImageURL())) {
                Picasso.with(context)
                        .load(filterDataDTO.getImageURL())
                        .transform(new CircleImageTransform())
                        .placeholder(R.drawable.icn_placeholder_user_profile_png)
                        .into(patientImageView);
            } else {
                patientImageView.setImageResource(R.drawable.icn_placeholder_user_profile_png);
            }
        }
    }

    public void resetData() {
        filteredPatients = allPatients;
    }

    /*
     * Create our filter
     */
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterableDataDTOFilter();
        }
        return filter;
    }

    /**
     * Custom Filter for filtering an filterable data in RecyclerView.
     */
    private class FilterableDataDTOFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = allPatients;
                results.count = allPatients.size();
            } else {
                // We perform filtering operation
                List<FilterDataDTO> filterableDataDTOList = new ArrayList<>();

                for (FilterDataDTO dataDTO : allPatients) {
                    try {
                        String displayName = dataDTO.getDisplayText();
                        String[] nameList = displayName.split(" ");
                        String middleName = "";
                        String lastName = "";
                        if (nameList.length > 0) {
                            if (nameList.length > 2) {
                                middleName = nameList[1];
                                lastName = nameList[2];
                            } else {
                                lastName = nameList[1];
                            }
                        }

                        if (displayName.toUpperCase().startsWith(constraint.toString().toUpperCase())
                                || lastName.toUpperCase().startsWith(constraint.toString().toUpperCase())
                                || middleName.toUpperCase().startsWith(constraint.toString().toUpperCase())) {
                            filterableDataDTOList.add(dataDTO);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                results.values = filterableDataDTOList;
                results.count = filterableDataDTOList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            filteredPatients = (List<FilterDataDTO>) results.values;
            notifyDataSetChanged();
        }
    }
}