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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.signin.SignInPracticeViewModel;
import com.carecloud.carepay.practice.library.signin.adapters.PracticeSearchAdapter;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionDTO;
import com.carecloud.carepay.practice.library.signin.interfaces.SelectPracticeCallback;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/9/17.
 */
public class ChoosePracticeFragment extends BaseDialogFragment
        implements PracticeSearchAdapter.SelectPracticeAdapterCallback {

    private List<UserPracticeDTO> practiceList = new ArrayList<>();
    private UserPracticeDTO selectedPractice;


    private RecyclerView searchRecycler;
    private Button continueButton;
    private SearchView searchView;

    private SelectPracticeCallback callback;
    private SignInPracticeViewModel viewModel;

    public static ChoosePracticeFragment newInstance() {
        Bundle args = new Bundle();
        ChoosePracticeFragment fragment = new ChoosePracticeFragment();
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
        viewModel = new ViewModelProvider(getActivity()).get(SignInPracticeViewModel.class);
        PracticeSelectionDTO practiceSelectionModel = viewModel.getPracticesInfoDtoObservable().getValue();
        if (practiceSelectionModel != null) {
            practiceList = practiceSelectionModel.getPayload().getUserPracticesList();
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

                title.setText(Label.getLabel("practice_list_select_a_business"));
            }
        }

        continueButton = (Button) findViewById(R.id.nextButton);
        continueButton.setText(Label.getLabel("practice_list_continue"));
        continueButton.setOnClickListener(continueClick);

        View closeButton = findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(view1 -> {
            callback.onSelectPracticeCanceled();
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

    private void setAdapter(List<UserPracticeDTO> practiceList) {
        PracticeSearchAdapter practiceSearchAdapter;
        if (searchRecycler.getAdapter() == null) {
            practiceSearchAdapter = new PracticeSearchAdapter(getContext(), practiceList, this);
            selectedPractice = getPreviousSelectedPractice(practiceList, ApplicationPreferences
                    .getInstance().getPracticeId());
            if (selectedPractice != null) {
                practiceSearchAdapter.setSelectedPractice(selectedPractice);
                continueButton.setEnabled(true);
            }
            searchRecycler.setAdapter(practiceSearchAdapter);
        } else {
            practiceSearchAdapter = (PracticeSearchAdapter) searchRecycler.getAdapter();
            practiceSearchAdapter.setPracticeList(practiceList);
            practiceSearchAdapter.notifyDataSetChanged();
        }
    }

    private UserPracticeDTO getPreviousSelectedPractice(List<UserPracticeDTO> practiceList,
                                                        String practiceId) {
        if (practiceId != null) {
            for (UserPracticeDTO practice : practiceList) {
                if (practice.getPracticeId().equals(practiceId)) {
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
            if (selectedPractice != null) {
                if (selectedPractice.getLocations().size() == 1) {
                    viewModel.authenticate(selectedPractice, selectedPractice.getLocations().get(0));
                } else {
                    ChoosePracticeLocationFragment fragment = ChoosePracticeLocationFragment
                            .newInstance(selectedPractice);
                    fragment.show(getActivity().getSupportFragmentManager(), fragment.getClass().getName());
                }
                dismiss();
            }
        }
    };

    @Override
    public void onSelectPractice(UserPracticeDTO practice) {
        searchView.clearFocus();
        SystemUtil.hideSoftKeyboard(getActivity());
        selectedPractice = practice;
        continueButton.setEnabled(true);
    }

    private void findPractice(String search) {
        List<UserPracticeDTO> searchList = new ArrayList<>();
        for (UserPracticeDTO practice : practiceList) {
            if (practice.getPracticeName().toLowerCase().contains(search.toLowerCase())) {
                searchList.add(practice);
            }
        }
        setAdapter(searchList);
    }

    private void clearSelectedPractice() {
        selectedPractice = null;
        PracticeSearchAdapter searchAdapter = (PracticeSearchAdapter) searchRecycler.getAdapter();
        searchAdapter.setSelectedPractice(selectedPractice);
    }

}
