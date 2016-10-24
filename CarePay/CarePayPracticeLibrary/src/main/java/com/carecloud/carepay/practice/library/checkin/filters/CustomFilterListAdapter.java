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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter for displaying an array of FilterableDataDTO objects.
 */
public class CustomFilterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    // The items to display in your RecyclerView
    private List<Object> filterableDataDTOList;
    private List<Object> origFilterableDataDTOList;
    private Context context;
    private final int sectionHeader = 0;
    private Filter filterableDataDTOFilter;
    private boolean isPatientListData = false;
    private OnFilterOptionChangedListener onFilterOptionChangedListener;

    /**
     * The callback used to indicate the user selected the filter.
     */
    public interface OnFilterOptionChangedListener {

        /**
         * Called upon a filter change.
         *
         * @param isPatientListFilter Weather it is the Patient List Filter.
         * @param isOptionEnabled     Weather the filter option is checked true.
         * @param filteredDataDTO     The Data DTO that is filtered.
         */
        void onFilterChanged(boolean isPatientListFilter, boolean isOptionEnabled,
                             FilterableDataDTO filteredDataDTO);
    }

    /**
     * Constructor.
     *
     * @param context context
     * @param items   list of occurrence
     */
    public CustomFilterListAdapter(Context context, OnFilterOptionChangedListener onFilterOptionChangedListener,
                                   List<Object> items) {
        this.context = context;
        this.filterableDataDTOList = items;
        this.origFilterableDataDTOList = items;
        this.onFilterOptionChangedListener = onFilterOptionChangedListener;
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.filterableDataDTOList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (filterableDataDTOList.get(position) instanceof FilterableDataDTO) {
            return 1;
        } else if (filterableDataDTOList.get(position) instanceof String) {
            return sectionHeader;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (viewType == sectionHeader) {
            View customFilterListHeaderRow = inflater.inflate(R.layout.custom_filter_patient_list_header_item,
                    viewGroup, false);
            viewHolder = new ViewHolderFilterableSectionHeader(customFilterListHeaderRow);
        } else {
            View customFilterListDataRow = inflater.inflate(R.layout.custom_filter_patient_list_item,
                    viewGroup, false);
            viewHolder = new ViewHolderFilterableDataItem(customFilterListDataRow);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder.getItemViewType() == sectionHeader) {
            ViewHolderFilterableSectionHeader vhSectionHeader = (ViewHolderFilterableSectionHeader) viewHolder;
            vhSectionHeader.getTextView().setText(filterableDataDTOList.get(position).toString());
            viewHolder.itemView.setTag(vhSectionHeader);
        } else {
            FilterableDataDTO filterableDataDTO = (FilterableDataDTO) this.filterableDataDTOList.get(position);

            ViewHolderFilterableDataItem vhDataItem = (ViewHolderFilterableDataItem) viewHolder;
            vhDataItem.getCheckBox().setText(filterableDataDTO.getName());
            vhDataItem.getCheckBox().setChecked(filterableDataDTO.isChecked());
            vhDataItem.getCheckBox().setTag(filterableDataDTO);
            vhDataItem.getPatientImageView().setImageResource(R.drawable.container_avatar);

            if (filterableDataDTO.isChecked()) {
                vhDataItem.getSelectedItemImageView().setVisibility(View.VISIBLE);
            } else {
                vhDataItem.getSelectedItemImageView().setVisibility(View.GONE);
            }
            viewHolder.itemView.setTag(vhDataItem);
            vhDataItem.getCheckBox().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox checkBox = (CheckBox) view;
                    FilterableDataDTO filterableDataDTO = (FilterableDataDTO) checkBox.getTag();
                    filterableDataDTO.setChecked(checkBox.isChecked());
                    if (checkBox.isChecked()) {
                        ((ViewGroup) checkBox.getParent()).getChildAt(0).setVisibility(View.VISIBLE);
                    } else {
                        ((ViewGroup) checkBox.getParent()).getChildAt(0).setVisibility(View.GONE);
                    }
                    onFilterOptionChangedListener.onFilterChanged(isPatientListData, checkBox.isChecked(),
                            filterableDataDTO);
                }
            });
        }
    }

    /**
     * Custom ViewHolder for displaying an FilterableDataItem in RecyclerView.
     */
    private class ViewHolderFilterableDataItem extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private ImageView patientImageView;
        private ImageView selectedItemImageView;

        ViewHolderFilterableDataItem(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.patientItemCheckBox);
            patientImageView = (ImageView) view.findViewById(R.id.patientImageView);
            selectedItemImageView = (ImageView) view.findViewById(R.id.selectedItemImageView);
            if (isPatientListData) {
                patientImageView.setVisibility(View.VISIBLE);
            } else {
                patientImageView.setVisibility(View.GONE);
            }
            SystemUtil.setProximaNovaSemiboldTypeface(context, checkBox);
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        public void setPatientImageView(ImageView patientImageView) {
            this.patientImageView = patientImageView;
        }

        public ImageView getPatientImageView() {
            return patientImageView;
        }

        public void setSelectedItemImageView(ImageView selectedItemImageView) {
            this.selectedItemImageView = selectedItemImageView;
        }

        public ImageView getSelectedItemImageView() {
            return selectedItemImageView;
        }
    }

    /**
     * Custom ViewHolder for displaying an FilterableSectionHeader in RecyclerView.
     */
    private class ViewHolderFilterableSectionHeader extends RecyclerView.ViewHolder {

        private TextView headerItemTextView;

        ViewHolderFilterableSectionHeader(View view) {
            super(view);
            headerItemTextView = (TextView) view.findViewById(R.id.headerItemTextView);
            SystemUtil.setGothamRoundedBoldTypeface(context, headerItemTextView);
        }

        TextView getTextView() {
            return headerItemTextView;
        }

        public void setTextView(TextView textViewSectionHeader) {
            this.headerItemTextView = textViewSectionHeader;
        }
    }

    void changeAdapterDataSetType(boolean isPatientListData) {
        this.isPatientListData = isPatientListData;
    }

    void resetData() {
        filterableDataDTOList = origFilterableDataDTOList;
    }

    /*
     * Create our filter
	 */
    @Override
    public Filter getFilter() {
        if (filterableDataDTOFilter == null) {
            filterableDataDTOFilter = new FilterableDataDTOFilter();
        }
        return filterableDataDTOFilter;
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
                results.values = origFilterableDataDTOList;
                results.count = origFilterableDataDTOList.size();
            } else {
                // We perform filtering operation
                List<FilterableDataDTO> nFilterableDataDTOList = new ArrayList<>();

                for (Object object : filterableDataDTOList) {
                    try {
                        FilterableDataDTO dataDTO = (FilterableDataDTO) object;
                        if (dataDTO.getName().toUpperCase().startsWith(constraint.toString()
                                .toUpperCase())) {
                            nFilterableDataDTOList.add(dataDTO);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                results.values = nFilterableDataDTOList;
                results.count = nFilterableDataDTOList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            filterableDataDTOList = (List<Object>) results.values;
            notifyDataSetChanged();
        }
    }
}