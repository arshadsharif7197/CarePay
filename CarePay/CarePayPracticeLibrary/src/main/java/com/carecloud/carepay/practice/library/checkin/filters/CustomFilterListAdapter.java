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
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

/**
 * Custom adapter for displaying an array of FilterableDataDTO objects.
 */
public class CustomFilterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<FilterDataDTO> filterableDataDTOList;
    private Context context;
    private static final int ROW_SECTION_HEADER = 0;
    private static final int ROW_ITEM = 1;

    private OnFilterOptionChangedListener onFilterOptionChangedListener;

    /**
     * The callback used to indicate the user selected the filter.
     */
    public interface OnFilterOptionChangedListener {

        /**
         * Called upon a filter change.
         *
         * @param filteredDataDTO The Data DTO that is filtered.
         */
        void onFilterChanged(FilterDataDTO filteredDataDTO);
    }

    /**
     * Constructor.
     *
     * @param context context
     * @param items   list of occurrence
     */
    public CustomFilterListAdapter(Context context, OnFilterOptionChangedListener onFilterOptionChangedListener,
                                   List<FilterDataDTO> items) {
        this.context = context;
        this.filterableDataDTOList = items;
        this.onFilterOptionChangedListener = onFilterOptionChangedListener;
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
            SystemUtil.setProximaNovaSemiboldTypeface(context, checkBox);
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
                onFilterOptionChangedListener.onFilterChanged(filterDataDTO);
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
            SystemUtil.setGothamRoundedBoldTypeface(context, headerItemTextView);
        }

        TextView getTextView() {
            return headerItemTextView;
        }

        public void setTextView(TextView textViewSectionHeader) {
            this.headerItemTextView = textViewSectionHeader;
        }
    }
}