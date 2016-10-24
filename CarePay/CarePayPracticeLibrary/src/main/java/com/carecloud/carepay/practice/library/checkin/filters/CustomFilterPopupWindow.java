package com.carecloud.carepay.practice.library.checkin.filters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by sudhir_pingale on 10/19/2016.
 */

public class CustomFilterPopupWindow extends PopupWindow
        implements CustomFilterListAdapter.OnFilterOptionChangedListener {

    private Context context;
    private View parentView;
    private Button clearFiltersButton;
    private ImageView clearSearchImageView;
    private EditText searchPatientEditText;
    private RecyclerView filterableDataRecyclerView;
    private CustomFilterListAdapter listAdapter;
    private CustomFilterPopupWindow popupWindow;
    private HashMap<Integer, FilterableDataDTO> filteredDataMap;

    /**
     * @param context    the context to inflate custom popup layout
     * @param parentView a parent view to get the {@link View#getWindowToken()} token from
     */
    public CustomFilterPopupWindow(Context context, View parentView) {

        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.custom_filter_popup_window_layout, null);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        update();
        setOutsideTouchable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.popupWindow = this;
        this.context = context;
        this.parentView = parentView;
        filteredDataMap = new HashMap<>();

        initialiseViews();

        displayRecyclerView();
    }

    private void displayRecyclerView() {
        // Set our custom adapter as the ListView's adapter.
        listAdapter = new CustomFilterListAdapter(context, popupWindow, getProviderAndLocationData());
        LinearLayoutManager filterableListLayoutManager = new LinearLayoutManager(context);
        filterableListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        filterableListLayoutManager.setAutoMeasureEnabled(false);
        filterableDataRecyclerView.setLayoutManager(filterableListLayoutManager);
        filterableDataRecyclerView.setAdapter(listAdapter);
    }

    private void initialiseViews() {
        View popupWindowLayout = this.getContentView();
        clearSearchImageView = (ImageView) popupWindowLayout.findViewById(R.id.clearSearchImageView);
        searchPatientEditText = (EditText) popupWindowLayout.findViewById(R.id.searchPatientEditText);
        filterableDataRecyclerView = (RecyclerView) popupWindowLayout.findViewById(R.id.filterableDataRecyclerView);
        clearFiltersButton = (Button) popupWindowLayout.findViewById(R.id.clearFiltersButton);
        ImageView closeFilterWindowImageView = (ImageView) popupWindowLayout.findViewById(R.id.closeFilterWindowImageView);

        closeFilterWindowImageView.setOnClickListener(closeFilterWindowListener);
        clearSearchImageView.setOnClickListener(clearSearchTextListener);
        searchPatientEditText.addTextChangedListener(searchEditTextWatcher);
        searchPatientEditText.setOnFocusChangeListener(searchPatientEditTextFocusChangeListener);
        clearFiltersButton.setOnClickListener(clearFilterListener);

        SystemUtil.setProximaNovaSemiboldTypefaceEdittext(context,searchPatientEditText);
        SystemUtil.setGothamRoundedMediumTypeface(context,clearFiltersButton);
    }

    public void showPopWindow() {
        showAtLocation(parentView, Gravity.TOP | Gravity.RIGHT, 0, 0);
    }

    private View.OnFocusChangeListener searchPatientEditTextFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                clearSearchImageView.setVisibility(View.VISIBLE);
                CustomFilterListAdapter adapter = new CustomFilterListAdapter(context, popupWindow,
                        getPatientData());
                filterableDataRecyclerView.setAdapter(adapter);
                adapter.changeAdapterDataSetType(true);
            } else {
                clearSearchImageView.setVisibility(View.GONE);
                filterableDataRecyclerView.setAdapter(listAdapter);
                listAdapter.changeAdapterDataSetType(false);
                if (!filteredDataMap.isEmpty()) {
                    clearFiltersButton.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private View.OnClickListener closeFilterWindowListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    private View.OnClickListener clearFilterListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clearSearchImageView.performClick();
            listAdapter = new CustomFilterListAdapter(context, popupWindow, getProviderAndLocationData());
            filterableDataRecyclerView.setAdapter(listAdapter);
            clearFiltersButton.setVisibility(View.GONE);
            filteredDataMap.clear();
            /*
             * To give callback to the parent screen that all filters cleared
             */
        }
    };

    private View.OnClickListener clearSearchTextListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            searchPatientEditText.setText("");
            searchPatientEditText.clearFocus();
        }
    };

    private TextWatcher searchEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence string, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence string, int start, int before, int count) {
            if (count < before) {
                // We're deleting char so we need to reset the adapter data
                ((CustomFilterListAdapter) filterableDataRecyclerView.getAdapter()).resetData();
            }
            ((CustomFilterListAdapter) filterableDataRecyclerView.getAdapter()).getFilter()
                    .filter(string.toString());
        }

        @Override
        public void afterTextChanged(Editable string) {
            if (string.toString().isEmpty()) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
                clearFiltersButton.setVisibility(View.GONE);
            } else {
                clearFiltersButton.setVisibility(View.VISIBLE);
            }
        }
    };

    /*
     * To get the provider and location data from Check-In screen when DTO is finalised
     */
    private ArrayList<Object> getProviderAndLocationData() {
        Object[] filterableProviderDataDTOs;
        filterableProviderDataDTOs = new Object[]{"Doctors",
            new FilterableDataDTO(0, "1-Dr. Name"), new FilterableDataDTO(1, "2-Dr. Name"),
            new FilterableDataDTO(2, "3-Dr. Name"), new FilterableDataDTO(3, "4-Dr. Name"),
            new FilterableDataDTO(4, "5-Dr. Name"), new FilterableDataDTO(5, "6-Dr. Name"),
            new FilterableDataDTO(6, "7-Dr. Name")
        };

        Object[] filterableLocationDataDTOs;
        filterableLocationDataDTOs = new Object[]{"Locations",
                new FilterableDataDTO(7, "8-Location Name"), new FilterableDataDTO(8, "9-Location Name"),
                new FilterableDataDTO(9, "10-Location Name"), new FilterableDataDTO(10, "11-Location Name"),
                new FilterableDataDTO(11, "12-Location Name"), new FilterableDataDTO(12, "13-Location Name")
        };
        ArrayList<Object> filterableDataDTOList = new ArrayList<>();
        filterableDataDTOList.addAll(Arrays.asList(filterableProviderDataDTOs));
        filterableDataDTOList.addAll(Arrays.asList(filterableLocationDataDTOs));

        return filterableDataDTOList;
    }

    /*
     * To get the patient data from Check-In screen when DTO is finalised
     */
    private ArrayList<Object> getPatientData() {
        Object[] filterablePatientDataDTOs;
        filterablePatientDataDTOs = new Object[]{
            new FilterableDataDTO(0, "1-Patient Name"), new FilterableDataDTO(1, "2-Patient Name"),
            new FilterableDataDTO(2, "3-Patient Name"), new FilterableDataDTO(3, "4-Patient Name"),
            new FilterableDataDTO(4, "5-Patient Name"), new FilterableDataDTO(5, "6-Patient Name"),
            new FilterableDataDTO(6, "7-Patient Name"), new FilterableDataDTO(7, "8-Patient Name"),
            new FilterableDataDTO(8, "9-Patient Name"), new FilterableDataDTO(9, "10-Patient Name"),
            new FilterableDataDTO(10, "11-Patient Name"), new FilterableDataDTO(11, "12-Patient Name"),
            new FilterableDataDTO(12, "13-Patient Name"), new FilterableDataDTO(13, "14-Patient Name"),
            new FilterableDataDTO(14, "15-Patient Name"), new FilterableDataDTO(15, "16-Patient Name"),
            new FilterableDataDTO(16, "17-Patient Name"), new FilterableDataDTO(17, "18-Patient Name"),
            new FilterableDataDTO(18, "19-Patient Name"), new FilterableDataDTO(19, "111-Patient Name"),
            new FilterableDataDTO(20, "112-Patient Name"), new FilterableDataDTO(21, "113-Patient Name"),
            new FilterableDataDTO(22, "114-Patient Name"), new FilterableDataDTO(23, "115-Patient Name")
        };
        ArrayList<Object> filterableDataDTOList = new ArrayList<>();
        filterableDataDTOList.addAll(Arrays.asList(filterablePatientDataDTOs));

        return filterableDataDTOList;
    }

    @Override
    public void onFilterChanged(boolean isPatientListFilter, boolean isOptionEnabled,
                                FilterableDataDTO filteredDataDTO) {
        if (isOptionEnabled) {
            filteredDataMap.put(filteredDataDTO.getId(), filteredDataDTO);
        } else {
            filteredDataMap.remove(filteredDataDTO.getId());
        }

        if (filteredDataMap.isEmpty()) {
            clearFiltersButton.setVisibility(View.GONE);
        } else {
            clearFiltersButton.setVisibility(View.VISIBLE);
        }
       /*
        * To give callback to the parent screen wherever Custom Filter used
        */
    }
}