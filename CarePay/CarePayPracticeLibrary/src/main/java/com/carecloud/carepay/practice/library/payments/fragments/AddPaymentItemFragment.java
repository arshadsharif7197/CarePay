package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.AddPaymentItemAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lmenendez on 3/16/17.
 */

public class AddPaymentItemFragment extends BaseDialogFragment implements AddPaymentItemAdapter.AddPaymentItemCallback {
    public interface AddItemCallback{
        void addChargeItem(BalanceItemDTO chargeItem);
    }

    private SearchView searchView;
    private RecyclerView searchRecycler;
    private List<BalanceItemDTO> templateItems = new ArrayList<>();
    private List<BalanceItemDTO> simpleChargeItems = new ArrayList<>();

    private AddItemCallback callback;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        Gson gson = new Gson();
        if(args!=null){
            String payloadInfo = args.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            BalanceItemDTO[] balanceItemArray = gson.fromJson(payloadInfo, BalanceItemDTO[].class);
            simpleChargeItems = Arrays.asList(balanceItemArray);
        }

        getBaseTemplateItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        setupToolbar(view);

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler = (RecyclerView) view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);

        searchView = (SearchView) view.findViewById(R.id.search_entry_view);
        searchView.setOnQueryTextListener(queryTextListener);

        setAdapter(templateItems, simpleChargeItems);

    }

    private void setupToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        TextView textView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textView.setText(Label.getLabelForView("payment_add_item_button"));

    }

    private void setAdapter(List<BalanceItemDTO> templateItems, List<BalanceItemDTO> simpleChargeItems){
        if(searchRecycler.getAdapter()==null){
            AddPaymentItemAdapter adapter = new AddPaymentItemAdapter(getContext(), templateItems, simpleChargeItems, this);
            searchRecycler.setAdapter(adapter);
        }else{
            AddPaymentItemAdapter adapter = (AddPaymentItemAdapter) searchRecycler.getAdapter();
            adapter.setSimpleChargeItems(simpleChargeItems);
            adapter.setTemplateItems(templateItems);
            adapter.notifyDataSetChanged();
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
            if(newText.length()>0){
                searchItems(newText);
            }else{
                setAdapter(templateItems, simpleChargeItems);
            }
            return true;
        }
    };

    private void getBaseTemplateItems(){
        addTemplateItem(PendingBalancePayloadDTO.CO_PAY_TYPE, 0);
        addTemplateItem(PendingBalancePayloadDTO.CO_INSURANCE_TYPE, 0);
        addTemplateItem(PendingBalancePayloadDTO.DEDUCTIBLE_TYPE, 0);

    }

    private void addTemplateItem(String description, double amount){
        BalanceItemDTO templateItem = new BalanceItemDTO();
        templateItem.setDescription(description);
        templateItem.setBalance(amount);
        templateItem.setAmount(amount);
        templateItems.add(templateItem);
    }

    private void searchItems(String searchText){
        List<BalanceItemDTO> searchItems = new ArrayList<>();
        for(BalanceItemDTO templateItem : templateItems){
            if(templateItem.getDescription().toLowerCase().contains(searchText.toLowerCase())){
                searchItems.add(templateItem);
            }
        }

        for(BalanceItemDTO simpleChargeItem : simpleChargeItems){
            if(simpleChargeItem.getDescription().toLowerCase().contains(searchText.toLowerCase())){
                searchItems.add(simpleChargeItem);
            }
        }

        setAdapter(null, searchItems);

    }

    public void setCallback(AddItemCallback callback) {
        this.callback = callback;
    }

    @Override
    public void paymentItemSelected(BalanceItemDTO balanceItem) {
        if(callback!=null){
            callback.addChargeItem(balanceItem);
        }
        dismiss();
    }

}
