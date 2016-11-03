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
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by sudhir_pingale on 10/19/2016.
 */

public class CustomFilterPopupWindow extends PopupWindow
        implements CustomFilterListAdapter.OnFilterOptionChangedListener, CustomSearchAdapter.OnSearchChangedListener {

    private Context context;
    private View parentView;
    private Button clearFiltersButton;
    private ImageView clearSearchImageView;
    private EditText searchPatientEditText;
    private RecyclerView filterableDataRecyclerView;
    private CustomFilterListAdapter listAdapter;
    private CustomFilterPopupWindow popupWindow;
    private CarePayTextView titleTextView;

    private ArrayList<FilterDataDTO> filterableDoctorsLocationList;

    private ArrayList<FilterDataDTO> filterablePatientsList;

    private HashSet<String> appointments;
    private HashSet<FilterDataDTO> selectedFilters;
    private HashSet<FilterDataDTO> searchedPatients = new HashSet<>();

    private FilterCallBack filterCallBack;

    /**
     * @param context    the context to inflate custom popup layout
     * @param parentView a parent view to get the {@link View#getWindowToken()} token from
     */
    public CustomFilterPopupWindow(Context context, View parentView, ArrayList<FilterDataDTO> filterableList, ArrayList<FilterDataDTO> filterablePatientsList) {

        super(context);
        filterCallBack = (FilterCallBack) context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.custom_filter_popup_window_layout, null);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        update();
        setOutsideTouchable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.filterableDoctorsLocationList = filterableList;
        this.filterablePatientsList = filterablePatientsList;
        selectedFilters = new HashSet<>();
        searchedPatients = new HashSet<>();
        appointments = getAllAppointment();
        this.popupWindow = this;
        this.context = context;
        this.parentView = parentView;
        initialiseViews();
        displayRecyclerView();
    }

    public interface FilterCallBack {
        void applyFilter(HashSet<String> appointments);
    }


    private void displayRecyclerView() {
        // Set our custom adapter as the ListView's adapter.
        listAdapter = new CustomFilterListAdapter(context, popupWindow, filterableDoctorsLocationList);
        LinearLayoutManager filterableListLayoutManager = new LinearLayoutManager(context);
        filterableListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        filterableListLayoutManager.setAutoMeasureEnabled(false);
        filterableDataRecyclerView.setLayoutManager(filterableListLayoutManager);
        filterableDataRecyclerView.setAdapter(listAdapter);
    }

    private HashSet<String> getAllAppointment() {
        HashSet<String> strings = new HashSet<>();
        for (FilterDataDTO patient : filterablePatientsList) {
            strings.addAll(patient.getAppointmentList());
        }
        return strings;
    }

    private HashSet<String> getFilteredAppointment() {
        HashSet<String> strings = new HashSet<>();
        for (FilterDataDTO patient : selectedFilters) {
            strings.addAll(patient.getAppointmentList());
        }

        return strings;
    }

    private HashSet<String> getPatientAppointment() {
        HashSet<String> strings = new HashSet<>();
        for (FilterDataDTO patient : searchedPatients) {
            strings.addAll(patient.getAppointmentList());
        }
        return strings;
    }

    private void initialiseViews() {
        View popupWindowLayout = this.getContentView();
        clearSearchImageView = (ImageView) popupWindowLayout.findViewById(R.id.clearSearchImageView);
        searchPatientEditText = (EditText) popupWindowLayout.findViewById(R.id.searchPatientEditText);
        filterableDataRecyclerView = (RecyclerView) popupWindowLayout.findViewById(R.id.filterableDataRecyclerView);
        clearFiltersButton = (Button) popupWindowLayout.findViewById(R.id.clearFiltersButton);
        ImageView closeFilterWindowImageView = (ImageView) popupWindowLayout.findViewById(R.id.closeFilterWindowImageView);

        titleTextView= (CarePayTextView) popupWindowLayout.findViewById(R.id.titleTextView);

        closeFilterWindowImageView.setOnClickListener(closeFilterWindowListener);
        clearSearchImageView.setOnClickListener(clearSearchTextListener);
        searchPatientEditText.addTextChangedListener(searchEditTextWatcher);
        searchPatientEditText.setOnFocusChangeListener(searchPatientEditTextFocusChangeListener);
        clearFiltersButton.setOnClickListener(clearFilterListener);
        clearFiltersButton.setVisibility(View.GONE);

        SystemUtil.setProximaNovaSemiboldTypefaceEdittext(context, searchPatientEditText);
        SystemUtil.setGothamRoundedMediumTypeface(context, clearFiltersButton);
    }

    public void setTitle(String title){
        titleTextView.setText(title);
    }

    public void setSearchHint(String hint){
        searchPatientEditText.setHint(hint);
    }

    public void setClearFiltersButtonText(String text){
        clearFiltersButton.setText(text);
    }

    public void showPopWindow() {
        showAtLocation(parentView, Gravity.TOP | Gravity.RIGHT, 0, 0);
    }


    private View.OnFocusChangeListener searchPatientEditTextFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                clearSearchImageView.setVisibility(View.VISIBLE);
                CustomSearchAdapter adapter = new CustomSearchAdapter(context, popupWindow, getFilteredPatientList());
                filterableDataRecyclerView.setAdapter(adapter);
            } else {
                clearSearchImageView.setVisibility(View.GONE);
                filterableDataRecyclerView.setAdapter(listAdapter);
                if (!selectedFilters.isEmpty() || !searchedPatients.isEmpty()) {
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
            clearSelection();
            searchedPatients.clear();
            selectedFilters.clear();
            listAdapter = new CustomFilterListAdapter(context, popupWindow, filterableDoctorsLocationList);
            filterableDataRecyclerView.setAdapter(listAdapter);
            setAppointment();
        }
    };

    private void clearSelection(){
        for (FilterDataDTO filterDataDTO:filterableDoctorsLocationList) {
            filterDataDTO.setChecked(false);
        }
        for (FilterDataDTO filterDataDTO:filterablePatientsList) {
            filterDataDTO.setChecked(false);
        }
    }

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
            if(filterableDataRecyclerView.getAdapter() instanceof  CustomFilterListAdapter){
                return;
            }

            if (count < before) {
                // We're deleting char so we need to reset the adapter data
                ((CustomSearchAdapter) filterableDataRecyclerView.getAdapter()).resetData();
            }
            ((CustomSearchAdapter) filterableDataRecyclerView.getAdapter()).getFilter()
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

    @Override
    public void onFilterChanged(FilterDataDTO filteredDataDTO) {

        if (filteredDataDTO.isChecked()) {
            selectedFilters.add(filteredDataDTO);
        } else {
            selectedFilters.remove(filteredDataDTO);
        }
        searchedPatients.clear();
        setAppointment();
    }

    private void setAppointment() {
        appointments.clear();
        if (!searchedPatients.isEmpty()) {
            appointments = getPatientAppointment();
            clearFiltersButton.setVisibility(View.VISIBLE);
        } else if (!selectedFilters.isEmpty()) {
            appointments = getFilteredAppointment();
            clearFiltersButton.setVisibility(View.VISIBLE);
        } else {
            appointments = getAllAppointment();
            clearFiltersButton.setVisibility(View.GONE);
        }
        filterCallBack.applyFilter(appointments);
    }

    private List<FilterDataDTO> getFilteredPatientList() {
        List<FilterDataDTO> patientList = new ArrayList<>();
        for (FilterDataDTO filterDataDTO : filterablePatientsList) {
            filterDataDTO.setChecked(false);
            for (String appointment : filterDataDTO.getAppointmentList()) {
                if (appointments.contains(appointment)) {
                    patientList.add(filterDataDTO);
                    break;
                }
            }
        }
        return patientList;
    }

    @Override
    public void onSearchChanged(FilterDataDTO filteredData) {
        if (filteredData.isChecked()) {
            searchedPatients.add(filteredData);
        } else {
            searchedPatients.remove(filteredData);
        }
        setAppointment();
    }
}