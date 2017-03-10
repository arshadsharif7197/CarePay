package com.carecloud.carepay.practice.library.signin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.signin.adapters.PracticeSearchAdapter;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionLabels;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionResponseModel;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionUserPractice;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/9/17.
 */

public class PracticeSearchFragment extends BaseDialogFragment implements PracticeSearchAdapter.SelectPracticeCallback {

    public interface SelectPracticeCallback{
        void onSelectPractice(PracticeSelectionResponseModel model, PracticeSelectionUserPractice userPractice);

        void onSelectPracticeCanceled();
    }

    private PracticeSelectionResponseModel practiceSelectionModel;
    private List<PracticeSelectionUserPractice> practiceList = new ArrayList<>();
    private PracticeSelectionUserPractice selectedPractice;


    private RecyclerView searchRecycler;
    private Button continueButton;
    private SearchView searchView;

    private SelectPracticeCallback callback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (SelectPracticeCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached Context must implement SelectPracticeCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        if(args!=null){
            String practiceSelectionArg = args.getString(CarePayConstants.PRACTICE_SELECTION_BUNDLE);
            practiceSelectionModel = DtoHelper.getConvertedDTO(PracticeSelectionResponseModel.class, practiceSelectionArg);
            practiceList = practiceSelectionModel.getPayload().getUserPracticesList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_practice_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        PracticeSelectionLabels labels = practiceSelectionModel.getMetadata().getLabels();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        if(toolbar!=null){
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            if(title!=null){
                ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                title.setLayoutParams(layoutParams);
                title.setGravity(Gravity.CENTER_HORIZONTAL);

                title.setText(StringUtil.getLabelForView(labels.getTitle()));
            }
        }

        continueButton = (Button) findViewById(R.id.nextButton);
        continueButton.setText(StringUtil.getLabelForView(labels.getContinueButton()));
        continueButton.setOnClickListener(continueClick);
        continueButton.setEnabled(false);

        View closeButton = findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onSelectPracticeCanceled();
                dismiss();
            }
        });

        searchView = (SearchView) view.findViewById(R.id.search_entry_view);
        searchView.setOnQueryTextListener(queryTextListener);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler = (RecyclerView) view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);

        if(practiceList.isEmpty()){
            callback.onSelectPracticeCanceled();
            dismiss();
        }

        if(practiceList.size() == 1){
            callback.onSelectPractice(practiceSelectionModel, practiceList.get(0));
            dismiss();
        }
        setAdapter(practiceList);
    }

    private void setAdapter(List<PracticeSelectionUserPractice> practiceList){
        PracticeSearchAdapter practiceSearchAdapter;
        if(searchRecycler.getAdapter() == null){
            practiceSearchAdapter = new PracticeSearchAdapter(getContext(), practiceList, this);
            searchRecycler.setAdapter(practiceSearchAdapter);
        }else{
            practiceSearchAdapter = (PracticeSearchAdapter) searchRecycler.getAdapter();
            practiceSearchAdapter.setPracticeList(practiceList);
            practiceSearchAdapter.notifyDataSetChanged();
        }
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
            if(selectedPractice!=null){
                callback.onSelectPractice(practiceSelectionModel, selectedPractice);
            }
        }
    };

    @Override
    public void onSelectPractice(PracticeSelectionUserPractice practice) {
        searchView.clearFocus();
        SystemUtil.hideSoftKeyboard(getActivity());
        selectedPractice = practice;
        continueButton.setEnabled(true);
    }

    private void findPractice(String search){
        List<PracticeSelectionUserPractice> searchList = new ArrayList<>();
        for(PracticeSelectionUserPractice practice : practiceList){
            if(practice.getPracticeName().toLowerCase().contains(search.toLowerCase())){
                searchList.add(practice);
            }
        }

        setAdapter(searchList);
    }

    private void clearSelectedPractice(){
        selectedPractice = null;
        PracticeSearchAdapter searchAdapter = (PracticeSearchAdapter) searchRecycler.getAdapter();
        searchAdapter.setSelectedPractice(selectedPractice);
    }

}
