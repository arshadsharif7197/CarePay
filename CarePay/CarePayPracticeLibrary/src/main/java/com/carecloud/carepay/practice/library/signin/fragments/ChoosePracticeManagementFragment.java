package com.carecloud.carepay.practice.library.signin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.signin.adapters.PartnersSearchAdapter;
import com.carecloud.carepay.practice.library.signin.interfaces.SelectPracticeCallback;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.signinsignup.dto.Partners;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ViewUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChoosePracticeManagementFragment extends BaseDialogFragment
        implements PartnersSearchAdapter.SelectPracticeAdapterCallback {

    private List<Partners> practiceList = new ArrayList<>();
    private Partners selectedPracticeManagement;


    private RecyclerView searchRecycler;
    private Button continueButton;
    private SearchView searchView;

    private SelectPracticeCallback callback;

    public static ChoosePracticeManagementFragment newInstance(List<Partners> pmsPartners) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(Defs.partnersInfo, gson.toJson(pmsPartners));
        ChoosePracticeManagementFragment fragment = new ChoosePracticeManagementFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (SelectPracticeCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement SelectPracticeCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle args = getArguments();
        Gson gson = new Gson();
        if (args != null) {
            String payloadInfo = args.getString(Defs.partnersInfo);
            Type type = new TypeToken<List<Partners>>() {
            }.getType();
            practiceList = gson.fromJson(payloadInfo, type);
            practiceList = getEnablePartners();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_practice_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        Toolbar toolbar = view.findViewById(R.id.search_toolbar);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.toolbar_title);
            if (title != null) {
                ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                title.setLayoutParams(layoutParams);
                title.setGravity(Gravity.CENTER_HORIZONTAL);

                title.setText(Label.getLabel("practice_management_select"));
            }
        }

        continueButton = (Button) findViewById(R.id.nextButton);
        continueButton.setText(Label.getLabel("practice_list_continue"));
        continueButton.setOnClickListener(continueClick);

        View closeButton = findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(view1 -> {
            dismiss();
        });

        searchView = view.findViewById(R.id.search_entry_view);
        for (TextView textView : ViewUtils.findChildrenByClass(searchView, TextView.class)) {
            textView.setHintTextColor(getResources().getColor(R.color.optional_gray));
            textView.setTextColor(getResources().getColor(R.color.textview_default_textcolor));
        }

        searchView.setQueryHint(Label.getLabel("search_field_hint"));
        searchView.setOnQueryTextListener(queryTextListener);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        searchRecycler = view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);

        setAdapter(practiceList);
    }

    private void setAdapter(List<Partners> practiceList) {
        PartnersSearchAdapter partnersSearchAdapter;
        if (searchRecycler.getAdapter() == null) {
            partnersSearchAdapter = new PartnersSearchAdapter(getContext(), practiceList, this);
            selectedPracticeManagement = getPreviousSelectedPractice(practiceList, ApplicationPreferences
                    .getInstance().getStartPracticeManagement());
            if (selectedPracticeManagement != null) {
                partnersSearchAdapter.setSelectedPractice(selectedPracticeManagement);
                continueButton.setEnabled(true);
            }
            searchRecycler.setAdapter(partnersSearchAdapter);
        } else {
            partnersSearchAdapter = (PartnersSearchAdapter) searchRecycler.getAdapter();
            partnersSearchAdapter.setPracticeList(practiceList);
            partnersSearchAdapter.notifyDataSetChanged();
        }
    }

    private List<Partners> getEnablePartners() {
        List<Partners> enablePartnersList = new ArrayList<>();
        for (Partners partners : practiceList) {
            if (partners.getImplemented()) {
                enablePartnersList.add(partners);
            }
        }
        return enablePartnersList;
    }

    private Partners getPreviousSelectedPractice(List<Partners> practiceList,
                                                 String practiceId) {
        if (practiceId != null) {
            for (Partners practice : practiceList) {
                if (practice.getPracticeMgmt().equals(practiceId)) {
                    return practice;
                }
            }
        }
        return null;
    }


    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchView.clearFocus();
            SystemUtil.hideSoftKeyboard(getActivity());
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            clearSelectedPractice();
            findPractice(newText);
            continueButton.setEnabled(false);
            return true;
        }
    };

    private View.OnClickListener continueClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (selectedPracticeManagement != null) {
                callback.onSelectPracticeManagement(selectedPracticeManagement);
                dismiss();
            }
        }
    };

    @Override
    public void onSelectPractice(Partners practice) {
        searchView.clearFocus();
        SystemUtil.hideSoftKeyboard(getActivity());
        selectedPracticeManagement = practice;
        continueButton.setEnabled(true);
    }

    private void findPractice(String search) {
        List<Partners> searchList = new ArrayList<>();
        for (Partners practice : practiceList) {
            if (practice.getLabel().toLowerCase().contains(search.toLowerCase())) {
                searchList.add(practice);
            }
        }
        setAdapter(searchList);
    }

    private void clearSelectedPractice() {
        selectedPracticeManagement = null;
        PartnersSearchAdapter searchAdapter = (PartnersSearchAdapter) searchRecycler.getAdapter();
        searchAdapter.setSelectedPractice(selectedPracticeManagement);
    }

}
