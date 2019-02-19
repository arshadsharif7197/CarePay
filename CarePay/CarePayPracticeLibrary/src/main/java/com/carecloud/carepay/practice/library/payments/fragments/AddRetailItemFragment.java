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
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.retail.models.RetailItemDto;
import com.carecloud.carepaylibray.retail.models.RetailProductsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 3/16/17
 */

public class AddRetailItemFragment extends BaseDialogFragment implements AddRetailItemAdapter.AddRetailItemCallback {
    private static final int BOTTOM_ROW_OFFSET = 2;

    private SearchView searchView;
    private RecyclerView searchRecycler;

    private AddPaymentItemCallback callback;
    private PaymentsModel paymentsModel;
    private RetailProductsModel retailProductsModel;
    private Paging paging;
    private boolean isPaging = false;
    private View emptyState;

    private String searchString;

    public static AddRetailItemFragment getInstance(PaymentsModel paymentsModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);

        AddRetailItemFragment fragment = new AddRetailItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        retailProductsModel = paymentsModel.getPaymentPayload().getRetailProducts();
        paging = retailProductsModel.getProducts().getPaging();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_retail_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
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
        searchRecycler = view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);
        searchRecycler.addOnScrollListener(retailScrollListener);

        searchView = view.findViewById(R.id.search_entry_view);
        searchView.setOnQueryTextListener(queryTextListener);
        searchView.setQueryHint(Label.getLabel("search_field_hint"));

        TextView noResultsMessage = view.findViewById(R.id.no_results_message);
        noResultsMessage.setText(Label.getLabel("payment_retail_items_no_results"));
        emptyState = view.findViewById(R.id.emptyStateScreen);

        setAdapter(retailProductsModel.getProducts().getItems());

    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.search_toolbar);
        TextView textView = toolbar.findViewById(R.id.toolbar_title);
        textView.setText(Label.getLabel("payment_retail_items_title"));

    }

    private void setAdapter(List<RetailItemDto> retailItems) {
        if (searchRecycler.getAdapter() == null) {
            AddRetailItemAdapter adapter = new AddRetailItemAdapter(getContext(), retailItems, this);
            searchRecycler.setAdapter(adapter);
        } else {
            AddRetailItemAdapter adapter = (AddRetailItemAdapter) searchRecycler.getAdapter();
            adapter.setRetailItems(retailItems);
        }

        emptyState.setVisibility(searchRecycler.getAdapter().getItemCount() == 0 ?
                View.VISIBLE : View.GONE);
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
            searchString = newText;
            loadSearchResults();
            return true;
        }
    };

    private RecyclerView.OnScrollListener retailScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (hasMorePages()) {
                int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (last > recyclerView.getAdapter().getItemCount() - BOTTOM_ROW_OFFSET && !isPaging) {
                    loadNextPage();
                    isPaging = true;
                }
            }
        }
    };

    private boolean hasMorePages() {
        return paging.getCurrentPage() != paging.getTotalPages();
    }

    private void loadNextPage() {
        UserPracticeDTO practiceDTO = paymentsModel.getPaymentPayload().getUserPractices().get(0);//safe in practice mode
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", practiceDTO.getPracticeMgmt());
        queryMap.put("practice_id", practiceDTO.getPracticeId());
        queryMap.put("products.page", String.valueOf(paging.getCurrentPage() + 1));
        queryMap.put("products.limit", String.valueOf(paging.getResultsPerPage()));

        if (!StringUtil.isNullOrEmpty(searchString)) {
            queryMap.put("products.search", searchString);
        }

        TransitionDTO nextPageTransition = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getProducts();
        getWorkflowServiceHelper().execute(nextPageTransition, nextPageCallback, queryMap);
    }

    private WorkflowServiceCallback nextPageCallback = new WorkflowServiceCallback() {
        AddRetailItemAdapter adapter;

        @Override
        public void onPreExecute() {
            adapter = (AddRetailItemAdapter) searchRecycler.getAdapter();
            adapter.setLoading(true);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            adapter.setLoading(false);
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            RetailProductsModel.ProductsDto productsDto = paymentsModel.getPaymentPayload()
                    .getRetailProducts().getProducts();
            Paging nextPage = productsDto.getPaging();
            if (nextPage.getCurrentPage() != paging.getCurrentPage()) {
                paging = nextPage;
                List<RetailItemDto> newItems = productsDto.getItems();
                adapter.addRetailItems(newItems);
            }
            isPaging = false;
        }

        @Override
        public void onFailure(String exceptionMessage) {
            adapter.setLoading(false);
            isPaging = false;
            showErrorNotification(exceptionMessage);
        }
    };

    private void loadSearchResults() {
        UserPracticeDTO practiceDTO = paymentsModel.getPaymentPayload().getUserPractices().get(0);//safe in practice mode
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", practiceDTO.getPracticeMgmt());
        queryMap.put("practice_id", practiceDTO.getPracticeId());
        queryMap.put("products.page", String.valueOf(1));
        queryMap.put("products.limit", String.valueOf(paging.getResultsPerPage()));

        if (!StringUtil.isNullOrEmpty(searchString)) {
            queryMap.put("products.search", searchString);
        }

        TransitionDTO nextPageTransition = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getProducts();
        getWorkflowServiceHelper().execute(nextPageTransition, searchCallback, queryMap);
    }

    private WorkflowServiceCallback searchCallback = new WorkflowServiceCallback() {
        AddRetailItemAdapter adapter;

        @Override
        public void onPreExecute() {
            adapter = (AddRetailItemAdapter) searchRecycler.getAdapter();
            adapter.setRetailItems(new ArrayList<RetailItemDto>());
            adapter.setLoading(true);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            adapter.setLoading(false);
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            RetailProductsModel.ProductsDto productsDto = paymentsModel.getPaymentPayload()
                    .getRetailProducts().getProducts();
            paging = productsDto.getPaging();
            List<RetailItemDto> searchItems = productsDto.getItems();
            setAdapter(searchItems);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            adapter.setLoading(false);
            showErrorNotification(exceptionMessage);
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
