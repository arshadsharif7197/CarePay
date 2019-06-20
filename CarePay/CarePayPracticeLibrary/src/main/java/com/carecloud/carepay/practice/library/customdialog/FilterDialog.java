package com.carecloud.carepay.practice.library.customdialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.filters.CustomFilterListAdapter;
import com.carecloud.carepay.practice.library.checkin.filters.CustomSearchAdapter;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Set;

public class FilterDialog extends PopupWindow
        implements CustomFilterListAdapter.CustomFilterListAdapterListener,
        CustomSearchAdapter.OnSearchChangedListener {

    private Context context;
    private View parentView;
    private Button clearFiltersButton;
    private ImageView clearSearchImageView;
    private EditText searchPatientEditText;
    private RecyclerView filterableDataRecyclerView;
    private CustomFilterListAdapter doctorsLocationsAdapter;
    private CustomSearchAdapter patientAdapter;
    private FilterModel filterModel;

    private FilterDialogListener callBack;
    private ImageView closeFilterWindowImageView;
    private boolean isOnPatientFilter;

    public void refresh() {
        doctorsLocationsAdapter.notifyDataSetChanged();
    }

    public interface FilterDialogListener {
        void applyFilter();

        void refreshData();

        void showFilterFlag(boolean areThereActiveFilters);
    }

    /**
     * @param context    the context to inflate custom popup layout
     * @param parentView a parent view to get the {@link View#getWindowToken()} token from
     */
    public FilterDialog(Context context,
                        View parentView,
                        FilterModel filterModel) {

        super(context);
        callBack = (FilterDialogListener) context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.custom_filter_popup_window_layout, null);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        update();
        setOutsideTouchable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.filterModel = filterModel;
        this.context = context;
        this.parentView = parentView;
        initialiseViews();

        displayRecyclerView();
        enableFilterButton();
    }

    private void displayRecyclerView() {
        // Set our custom adapter as the ListView's adapter.
        LinearLayoutManager filterableListLayoutManager = new LinearLayoutManager(context);
        filterableListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        filterableListLayoutManager.setAutoMeasureEnabled(false);
        filterableDataRecyclerView.setLayoutManager(filterableListLayoutManager);

        patientAdapter = new CustomSearchAdapter(context, this, filterModel.getPatients());
        doctorsLocationsAdapter = new CustomFilterListAdapter(filterModel, this);
        filterableDataRecyclerView.setAdapter(doctorsLocationsAdapter);
    }

    private void initialiseViews() {
        final View popupWindowLayout = this.getContentView();
        filterableDataRecyclerView = popupWindowLayout.findViewById(R.id.filterableDataRecyclerView);

        closeFilterWindowImageView = popupWindowLayout.findViewById(R.id.closeFilterWindowImageView);
        closeFilterWindowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideSoftKeyboard(context, getContentView());
                if (searchPatientEditText.hasFocus()) {
                    searchPatientEditText.clearFocus();
                } else {
                    dismiss();
                }
            }
        });

        setUpSearchEditText(popupWindowLayout);

        clearFiltersButton = popupWindowLayout.findViewById(R.id.clearFiltersButton);
        clearFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnPatientFilter) {
//                    filterModel.setPatients(new ArrayList<FilterDataDTO>());
                    filterModel.clearPatients();
                    patientAdapter.resetData();
                    doctorsLocationsAdapter = new CustomFilterListAdapter(filterModel, FilterDialog.this);
                    filterableDataRecyclerView.setAdapter(doctorsLocationsAdapter);
                    callBack.applyFilter();
                } else {
                    final Set<String> lastLocationFilter = filterModel.getFilteredLocationsIds();
                    filterModel.clearAll();
                    saveFilter();
                    doctorsLocationsAdapter = new CustomFilterListAdapter(filterModel, FilterDialog.this);
                    filterableDataRecyclerView.setAdapter(doctorsLocationsAdapter);
                    callBack.showFilterFlag(false);
                    if (lastLocationFilter.isEmpty()) {
                        callBack.applyFilter();
                    } else {
                        callBack.refreshData();
                    }
                }
                enableFilterButton();
                clearPatientSearch();
            }
        });

        View blankSpace = popupWindowLayout.findViewById(R.id.blankSpace);
        blankSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void clearPatientSearch() {
        searchPatientEditText.setText("");
        searchPatientEditText.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
    }

    private void setUpSearchEditText(View popupWindowLayout) {
        clearSearchImageView = popupWindowLayout.findViewById(R.id.clearSearchImageView);
        clearSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPatientEditText.setText("");
            }
        });

        searchPatientEditText = popupWindowLayout.findViewById(R.id.searchPatientEditText);
        searchPatientEditText.setHint(Label.getLabel("practice_checkin_filter_find_patient_by_name"));
        searchPatientEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence string, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence string, int start, int before, int count) {
                if (filterableDataRecyclerView.getAdapter() instanceof CustomFilterListAdapter) {
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
                if (string.length() > 0) {
                    clearSearchImageView.setVisibility(View.VISIBLE);
                } else {
                    clearSearchImageView.setVisibility(View.GONE);
                }
            }
        });

        searchPatientEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    filterableDataRecyclerView.setAdapter(patientAdapter);
                    closeFilterWindowImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_nav_back_white));
                    isOnPatientFilter = true;
                    clearFiltersButton.setText("practice.filter.clearFilters.button.clearPatients");
                } else {
                    filterableDataRecyclerView.setAdapter(doctorsLocationsAdapter);
                    closeFilterWindowImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icn_close));
                    isOnPatientFilter = false;
                    clearFiltersButton.setText(Label.getLabel("practice.filter.clearFilters.button.clearAll"));
                }
            }
        });

        searchPatientEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SystemUtil.hideSoftKeyboard(context, getContentView());
                    return true;
                }
                return false;
            }
        });
    }

    public void showPopWindow() {
        showAtLocation(parentView, Gravity.TOP | Gravity.RIGHT, 0, 0);
    }

    @Override
    public void onFilterChanged(FilterDataDTO filterDataDTO) {
        switch (filterDataDTO.getFilterDataType()) {
            case LOCATION:
                applyFilterAndRefresh();
                break;
            default:
                applyFilter();
        }
    }

    @Override
    public void onSearchChanged(FilterDataDTO filteredDataDTO) {
        applyFilter();
        doctorsLocationsAdapter.load();
    }

    private void applyFilter() {
        boolean areThereActiveFilters = enableFilterButton();
        callBack.showFilterFlag(areThereActiveFilters);
        callBack.applyFilter();
        saveFilter();
    }

    private void applyFilterAndRefresh() {
        boolean areThereActiveFilters = enableFilterButton();
        callBack.showFilterFlag(areThereActiveFilters);
        saveFilter();
        callBack.refreshData();
    }

    private boolean enableFilterButton() {
        boolean areThereActiveFilters = filterModel.areThereActiveFilters();
        clearFiltersButton.setEnabled(areThereActiveFilters);
        return areThereActiveFilters;
    }

    private void saveFilter() {
        String practiceId = ((BaseActivity) context).getApplicationMode().getUserPracticeDTO().getPracticeId();
        String userId = ((BaseActivity) context).getApplicationMode().getUserPracticeDTO().getUserId();
        ApplicationPreferences.getInstance().setSelectedProvidersId(practiceId, userId,
                filterModel.getFilteredDoctorsIds());
        ApplicationPreferences.getInstance().setSelectedLocationsId(practiceId, userId,
                filterModel.getFilteredLocationsIds());
    }
}