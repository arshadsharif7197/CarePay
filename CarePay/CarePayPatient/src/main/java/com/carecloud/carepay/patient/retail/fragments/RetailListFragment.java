package com.carecloud.carepay.patient.retail.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.retail.adapters.RetailStoreListAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.retail.RetailModel;
import com.carecloud.carepaylibray.retail.RetailPracticeDTO;
import com.carecloud.carepaylibray.retail.interfaces.RetailInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 11/20/17
 */

public class RetailListFragment extends BaseFragment implements RetailStoreListAdapter.RetailSelectionCallback {

    private RetailInterface callback;
    private RetailModel retailModel;
    private List<UserPracticeDTO> retailPracticesList = new ArrayList<>();

    private RecyclerView practiceStoreRecycler;
    private View noRetailLayout;

    /**
     * Constructor
     * @param retailModel retailModel
     * @return new instance of RetailListFragment
     */
    public static RetailListFragment newInstance(RetailModel retailModel){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, retailModel);

        RetailListFragment fragment = new RetailListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            callback = (RetailInterface) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached Context must implement RetailInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        retailModel = DtoHelper.getConvertedDTO(RetailModel.class, args);
        if(retailModel != null) {
            retailPracticesList = retailModel.getPayload().getPracticeInformation();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_purchase_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        practiceStoreRecycler = (RecyclerView) view.findViewById(R.id.practice_retail_list);
        practiceStoreRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        noRetailLayout = view.findViewById(R.id.no_purchase_layout);

        setAdapter();
    }

    private void setAdapter(){
        RetailStoreListAdapter adapter = new RetailStoreListAdapter(getContext(), retailPracticesList, this);
        practiceStoreRecycler.setAdapter(adapter);

        if(retailPracticesList.isEmpty()){
            noRetailLayout.setVisibility(View.VISIBLE);
            practiceStoreRecycler.setVisibility(View.GONE);
        }else{
            noRetailLayout.setVisibility(View.GONE);
            practiceStoreRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStoreSelected(UserPracticeDTO userPracticeDTO) {
//        RetailPracticeDTO selectedPractice = null;
//        for(RetailPracticeDTO retailPracticeDTO : retailModel.getPayload().getRetailPracticeList()){
//            if(retailPracticeDTO.getPracticeId()!=null && retailPracticeDTO.getPracticeId().equals(userPracticeDTO.getPracticeId())){
//                selectedPractice = retailPracticeDTO;
//                break;
//            }
//        }
//
//        callback.displayRetailStore(retailModel, selectedPractice, userPracticeDTO);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_id", userPracticeDTO.getPracticeId());
        queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
        queryMap.put("store_only", "true");

        TransitionDTO transitionDTO = retailModel.getMetadata().getLinks().getRetail();
        getWorkflowServiceHelper().execute(transitionDTO, getRetailStoreCallback(userPracticeDTO), queryMap);


    }

    private WorkflowServiceCallback getRetailStoreCallback(final UserPracticeDTO userPracticeDTO) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                RetailModel retailModel = DtoHelper.getConvertedDTO(RetailModel.class, workflowDTO);
                RetailPracticeDTO selectedPractice = retailModel.getPayload().getRetailPracticeList().get(0);
                callback.displayRetailStore(RetailListFragment.this.retailModel, selectedPractice, userPracticeDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        };
    }
}
