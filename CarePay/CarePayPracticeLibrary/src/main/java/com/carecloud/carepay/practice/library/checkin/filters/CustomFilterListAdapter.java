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
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.service.library.label.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter for displaying an array of FilterableDataDTO objects.
 */
public class CustomFilterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private FilterModel filterModel;
    private List<FilterDataDTO> filterableDataDTOList;
    private static final int ROW_SECTION_HEADER = 0;
    private static final int ROW_ITEM = 1;

    private CustomFilterListAdapterListener callback;

    /**
     * The callback used to indicate the user selected the filter.
     */
    public interface CustomFilterListAdapterListener {

        /**
         * Called upon a filter change.
         */
        void onFilterChanged();
    }

    /**
     * Constructor.
     *
     * @param filterModel Filter Model
     */
    public CustomFilterListAdapter(FilterModel filterModel,
                                   CustomFilterListAdapterListener callback) {
        this.context = context;
        this.filterModel = filterModel;
        this.callback = callback;

        load();
    }

    /**
     * Loads filterable list
     */
    public void load() {
        filterableDataDTOList = new ArrayList<>();

        ArrayList<FilterDataDTO> patients = filterModel.getCheckedPatients();
        if (!patients.isEmpty()) {
            filterableDataDTOList.add(new FilterDataDTO(Label.getLabel("practice_filter_patients")));
            filterableDataDTOList.addAll(patients);
        }

        filterableDataDTOList.add(new FilterDataDTO(Label.getLabel("practice_filter_doctors")));
        filterableDataDTOList.addAll(filterModel.getDoctors());
        filterableDataDTOList.add(new FilterDataDTO(Label.getLabel("practice_filter_locations")));
        filterableDataDTOList.addAll(filterModel.getLocations());
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.filterableDataDTOList.size();
    }

    @Override
    public int getItemViewType(int position) {
        FilterDataDTO filterDataDTO = filterableDataDTOList.get(position);
        if (filterDataDTO.getFilterDataType() == FilterDataDTO.FilterDataType.HEADER) {
            return ROW_SECTION_HEADER;
        } else {
            return ROW_ITEM;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == ROW_SECTION_HEADER) {
            View customFilterListHeaderRow = inflater.inflate(R.layout.custom_filter_patient_list_header_item,
                    viewGroup, false);
            viewHolder = new ViewHolderFilterableSectionHeader(customFilterListHeaderRow);
        } else if (viewType == ROW_ITEM) {
            View customFilterListDataRow = inflater.inflate(R.layout.custom_filter_patient_list_item,
                    viewGroup, false);
            viewHolder = new ViewHolderFilterableDataItem(customFilterListDataRow);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        FilterDataDTO filterDataDTO = this.filterableDataDTOList.get(position);
        if (viewHolder.getItemViewType() == ROW_SECTION_HEADER) {
            ViewHolderFilterableSectionHeader vhSectionHeader = (ViewHolderFilterableSectionHeader) viewHolder;
            vhSectionHeader.getTextView().setText(filterDataDTO.getDisplayText());
            viewHolder.itemView.setTag(vhSectionHeader);
        } else {
            ViewHolderFilterableDataItem vhDataItem = (ViewHolderFilterableDataItem) viewHolder;
            vhDataItem.setFilterDataDTO(filterDataDTO);
            viewHolder.itemView.setTag(vhDataItem);
        }
    }

    /**
     * Custom ViewHolder for displaying an FilterableDataItem in RecyclerView.
     */
    private class ViewHolderFilterableDataItem extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private ImageView selectedItemImageView;
        private FilterDataDTO filterDataDTO;

        ViewHolderFilterableDataItem(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.patientItemCheckBox);
            selectedItemImageView = (ImageView) view.findViewById(R.id.selectedItemImageView);
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
                callback.onFilterChanged();
            }
        };

        void setFilterDataDTO(FilterDataDTO filterDataDTO) {
            this.filterDataDTO = filterDataDTO;
            checkBox.setText(filterDataDTO.getDisplayText());
            checkBox.setChecked(filterDataDTO.isChecked());
            if (filterDataDTO.isChecked()) {
                selectedItemImageView.setVisibility(View.VISIBLE);
            } else {
                selectedItemImageView.setVisibility(View.GONE);
            }
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
        }

        TextView getTextView() {
            return headerItemTextView;
        }

        public void setTextView(TextView textViewSectionHeader) {
            this.headerItemTextView = textViewSectionHeader;
        }
    }
}