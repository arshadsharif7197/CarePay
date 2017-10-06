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
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lmenendez on 3/16/17
 */

public class AddPaymentItemFragment extends BaseDialogFragment implements AddPaymentItemAdapter.AddPaymentItemCallback {
    public interface AddItemCallback{
        void addChargeItem(SimpleChargeItem chargeItem);
    }

    private SearchView searchView;
    private RecyclerView searchRecycler;
    private List<SimpleChargeItem> templateItems = new ArrayList<>();
    private List<SimpleChargeItem> simpleChargeItems = new ArrayList<>();

    private AddItemCallback callback;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        Gson gson = new Gson();
        if(args!=null){
            String payloadInfo = args.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            SimpleChargeItem[] balanceItemArray = gson.fromJson(payloadInfo, SimpleChargeItem[].class);
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
        textView.setText(Label.getLabel("payment_add_item_button"));

    }

    private void setAdapter(List<SimpleChargeItem> templateItems, List<SimpleChargeItem> simpleChargeItems){
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
        addTemplateItem(Label.getLabel("practice_payments_detail_dialog_insurance_copay"), 0D, IntegratedPaymentLineItem.TYPE_COPAY);
        addTemplateItem(Label.getLabel("practice_payments_detail_dialog_co_insurance"), 0D, IntegratedPaymentLineItem.TYPE_COINSURANCE);
        addTemplateItem(Label.getLabel("practice_payments_detail_dialog_insurance_deductible"), 0D, IntegratedPaymentLineItem.TYPE_DEDUCTABLE);
        addTemplateItem(Label.getLabel("practice_payments_detail_dialog_pre_payment"), 0D, IntegratedPaymentLineItem.TYPE_PREPAYMENT);
        addTemplateItem(Label.getLabel("practice_payments_detail_dialog_cancellation_fee"), 0D, IntegratedPaymentLineItem.TYPE_CANCELLATION);
        addTemplateItem(Label.getLabel("practice_payments_detail_dialog_other"), 0D, IntegratedPaymentLineItem.TYPE_OTHER);
}

    private void addTemplateItem(String description, double amount, @IntegratedPaymentLineItem.LineItemType String responsibilityType){
        SimpleChargeItem templateItem = new SimpleChargeItem();
        templateItem.setDescription(description);
        templateItem.setAmount(amount);
        templateItem.setResponsibilityType(responsibilityType);
        templateItems.add(templateItem);
    }

    private void searchItems(String searchText){
        List<SimpleChargeItem> searchItems = new ArrayList<>();
        for(SimpleChargeItem templateItem : templateItems){
            if(templateItem.getDescription().toLowerCase().contains(searchText.toLowerCase())){
                searchItems.add(templateItem);
            }
        }

        for(SimpleChargeItem simpleChargeItem : simpleChargeItems){
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
    public void paymentItemSelected(SimpleChargeItem chargeItem) {
        if(callback!=null){
            callback.addChargeItem(chargeItem);
        }
        dismiss();
    }

}
