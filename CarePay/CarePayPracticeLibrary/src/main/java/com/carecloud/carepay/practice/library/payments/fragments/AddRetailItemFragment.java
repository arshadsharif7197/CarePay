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
import com.carecloud.carepay.practice.library.payments.adapter.AddRetailItemAdapter;
import com.carecloud.carepay.practice.library.payments.interfaces.AddPaymentItemCallback;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.retail.models.RetailItemDto;
import com.carecloud.carepaylibray.retail.models.RetailProductsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

/**
 * Created by lmenendez on 3/16/17
 */

public class AddRetailItemFragment extends BaseDialogFragment implements AddRetailItemAdapter.AddRetailItemCallback {
    private SearchView searchView;
    private RecyclerView searchRecycler;

    private AddPaymentItemCallback callback;
    private PaymentsModel paymentsModel;
    private RetailProductsModel retailProductsModel;

    public static AddRetailItemFragment getInstance(PaymentsModel paymentsModel){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);

        AddRetailItemFragment fragment = new AddRetailItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        retailProductsModel = paymentsModel.getPaymentPayload().getRetailProducts();
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
                cancel();
                callback.onDismissAddItemFragment();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler = (RecyclerView) view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);

        searchView = (SearchView) view.findViewById(R.id.search_entry_view);
        searchView.setOnQueryTextListener(queryTextListener);

        setAdapter(retailProductsModel.getProducts().getItems());

    }

    private void setupToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        TextView textView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textView.setText(Label.getLabel("payment_retail_items_title"));

    }

    private void setAdapter(List<RetailItemDto> retailItems){
        if(searchRecycler.getAdapter()==null){
            AddRetailItemAdapter adapter = new AddRetailItemAdapter(getContext(), retailItems, this);
            searchRecycler.setAdapter(adapter);
        }else{
            AddRetailItemAdapter adapter = (AddRetailItemAdapter) searchRecycler.getAdapter();
            adapter.setRetailItems(retailItems);
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
            //todo search
//            if(newText.length()>0){
//                searchItems(newText);
//            }else{
//                setAdapter(templateItems, simpleChargeItems);
//            }
            return true;
        }
    };



    public void setCallback(AddPaymentItemCallback callback) {
        this.callback = callback;
    }

    @Override
    public void retailItemSelected(RetailItemDto retailItem) {
        callback.addRetailItem(retailItem);
        dismiss();
    }



}
