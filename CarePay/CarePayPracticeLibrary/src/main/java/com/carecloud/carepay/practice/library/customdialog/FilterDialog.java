package com.carecloud.carepay.practice.library.customdialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.carecloud.carepay.practice.library.checkin.filters.CustomFilterListAdapter;
import com.carecloud.carepay.practice.library.checkin.filters.CustomSearchAdapter;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class FilterDialog extends PopupWindow
        implements CustomFilterListAdapter.OnFilterOptionChangedListener, CustomSearchAdapter.OnSearchChangedListener {

    private static final String TAG = "FilterDialog";

    private Context context;
    private View parentView;
    private Button clearFiltersButton;
    private ImageView clearSearchImageView;
    private EditText searchPatientEditText;
    private RecyclerView filterableDataRecyclerView;
    private CustomFilterListAdapter doctorsLocationsAdapter;
    private CustomSearchAdapter patientAdapter;
    private FilterModel filterModel;

    private FilterCallBack filterCallBack;


    /**
     * @param context    the context to inflate custom popup layout
     * @param parentView a parent view to get the {@link View#getWindowToken()} token from
     */
    public FilterDialog(Context context, View parentView, FilterModel filterModel) {

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
        this.filterModel = filterModel;
        this.context = context;
        this.parentView = parentView;
        initialiseViews();

        displayRecyclerView();
    }

    public interface FilterCallBack {
        void applyFilter();
    }


    private void displayRecyclerView() {
        // Set our custom adapter as the ListView's adapter.
        LinearLayoutManager filterableListLayoutManager = new LinearLayoutManager(context);
        filterableListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        filterableListLayoutManager.setAutoMeasureEnabled(false);
        filterableDataRecyclerView.setLayoutManager(filterableListLayoutManager);

        patientAdapter = new CustomSearchAdapter(context, this, filterModel.getPatients());
        doctorsLocationsAdapter = new CustomFilterListAdapter(context, this, filterModel.getDoctorsPlusLocations());
        filterableDataRecyclerView.setAdapter(doctorsLocationsAdapter);
    }

    private void initialiseViews() {
        View popupWindowLayout = this.getContentView();

        filterableDataRecyclerView = (RecyclerView) popupWindowLayout.findViewById(R.id.filterableDataRecyclerView);

        CarePayTextView titleTextView = (CarePayTextView) popupWindowLayout.findViewById(R.id.titleTextView);
        titleTextView.setText(filterModel.getPracticePaymentsFilter());

        ImageView closeFilterWindowImageView = (ImageView) popupWindowLayout.findViewById(R.id.closeFilterWindowImageView);
        closeFilterWindowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        clearSearchImageView = (ImageView) popupWindowLayout.findViewById(R.id.clearSearchImageView);
        clearSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPatientEditText.setText("");
                searchPatientEditText.clearFocus();
                filterModel.clearFilterByPatients();
            }
        });

        searchPatientEditText = (EditText) popupWindowLayout.findViewById(R.id.searchPatientEditText);
        searchPatientEditText.setHint(filterModel.getPracticePaymentsFilterFindPatientByName());
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
                if (string.toString().isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
                    clearFiltersButton.setVisibility(View.GONE);
                } else {
                    clearFiltersButton.setVisibility(View.VISIBLE);
                }
            }
        });

        searchPatientEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    clearSearchImageView.setVisibility(View.VISIBLE);
                    filterableDataRecyclerView.setAdapter(patientAdapter);
                } else {
                    clearSearchImageView.setVisibility(View.GONE);
                    filterableDataRecyclerView.setAdapter(doctorsLocationsAdapter);
                }
            }
        });

        clearFiltersButton = (Button) popupWindowLayout.findViewById(R.id.clearFiltersButton);
        clearFiltersButton.setText(filterModel.getPracticePaymentsFilterClearFilters());
        clearFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClearFilters");
                clearSearchImageView.performClick();
                filterModel.clear();
                filterableDataRecyclerView.setAdapter(doctorsLocationsAdapter);
                filterCallBack.applyFilter();
            }
        });

        SystemUtil.setProximaNovaSemiboldTypefaceEdittext(context, searchPatientEditText);
        SystemUtil.setGothamRoundedMediumTypeface(context, clearFiltersButton);
    }

    public void showPopWindow() {
        showAtLocation(parentView, Gravity.TOP | Gravity.RIGHT, 0, 0);
    }

    @Override
    public void onFilterChanged(FilterDataDTO filteredDataDTO) {
        filterCallBack.applyFilter();
    }

    @Override
    public void onSearchChanged(FilterDataDTO filteredDataDTO) {
        Log.d(TAG, "onSearchChanged " + filteredDataDTO.toString());
        filterCallBack.applyFilter();
    }
}