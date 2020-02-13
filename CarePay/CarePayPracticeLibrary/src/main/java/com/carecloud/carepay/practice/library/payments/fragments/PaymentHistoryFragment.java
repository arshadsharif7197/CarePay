package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.PaymentHistoryAdapter;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentHistoryCallback;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.models.NextPagingDTO;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 9/27/17
 */

public class PaymentHistoryFragment extends BaseDialogFragment implements PaymentHistoryAdapter.HistoryItemClickListener {
    private static final int BOTTOM_ROW_OFFSET = 2;

    private PracticePaymentHistoryCallback callback;
    private PaymentsModel paymentsModel;
    private List<PaymentHistoryItem> paymentHistory = new ArrayList<>();

    private RecyclerView historyRecycler;
    private Paging paging;
    private boolean isPaging = false;

    /**
     * Create instance of PaymentHistoryFragment
     *
     * @param paymentsModel payment model
     * @return new instance of PaymentHistoryFragment
     */
    public static PaymentHistoryFragment newInstance(PaymentsModel paymentsModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);

        PaymentHistoryFragment fragment = new PaymentHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PracticePaymentHistoryCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PracticePaymentHistoryCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        paging = paymentsModel.getPaymentPayload().getTransactionHistory().getPageDetails();
        paymentHistory = paymentsModel.getPaymentPayload().getTransactionHistory().getPaymentHistoryList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        String patientName = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getDemographics().getPayload().getPersonalDetails().getFullName();
        TextView fullName = view.findViewById(R.id.patient_full_name);
        fullName.setText(patientName);

        final ImageView profilePhoto = view.findViewById(R.id.patient_profile_photo);
        final TextView shortName = view.findViewById(R.id.patient_profile_short_name);
        shortName.setText(StringUtil.getShortName(patientName));

        String photoUrl = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getDemographics().getPayload().getPersonalDetails().getProfilePhoto();
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.with(getContext()).load(photoUrl)
                    .transform(new CircleImageTransform())
                    .resize(88, 88)
                    .into(profilePhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            profilePhoto.setVisibility(View.VISIBLE);
                            shortName.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            profilePhoto.setVisibility(View.GONE);
                            shortName.setVisibility(View.VISIBLE);
                        }
                    });
        }

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        String plansLabel = Label.getLabel("payment_plan_heading");
        final String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
        final int plans = paymentsModel.getPaymentPayload().getActivePlans(practiceId).size();
        if (plans > 1) {
            plansLabel = String.format(Label.getLabel("payment_plan_count"), plans);
        }

        historyRecycler = view.findViewById(R.id.history_recycler_view);
        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        historyRecycler.addOnScrollListener(historyScrollListener);
        setAdapter();
    }

    private void setAdapter() {
        setAdapter(null);
    }

    private void setAdapter(List<PaymentHistoryItem> newItems) {
        PaymentHistoryAdapter adapter = (PaymentHistoryAdapter) historyRecycler.getAdapter();
        if (adapter != null) {
            if (newItems != null && !newItems.isEmpty()) {
                adapter.addPaymentHistoryItems(newItems);
            } else {
                adapter.setPaymentHistoryItems(paymentHistory);
            }
        } else {
            adapter = new PaymentHistoryAdapter(getContext(), paymentHistory, this);
            historyRecycler.setAdapter(adapter);
        }
    }

    @Override
    public void onHistoryItemClicked(PaymentHistoryItem item) {
        PracticePaymentHistoryDetailFragment fragment = PracticePaymentHistoryDetailFragment
                .newInstance(item, paymentsModel);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                showDialog();
            }
        });
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    private boolean hasMorePages() {
        return paging.getCurrentPage() != paging.getTotalPages();
    }

    private void loadNextPage() {
        NextPagingDTO next = new NextPagingDTO();
        next.setNextPage(paging.getCurrentPage() + 1);
        next.setPageCount(paging.getResultsPerPage());

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata().getPatientId());

        TransitionDTO nextPageTransition = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getPaymentTransactionHistory();
        String payload = new Gson().toJson(next);
        getWorkflowServiceHelper().execute(nextPageTransition, nextPageCallback, payload, queryMap);
    }

    private RecyclerView.OnScrollListener historyScrollListener = new RecyclerView.OnScrollListener() {

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

    private WorkflowServiceCallback nextPageCallback = new WorkflowServiceCallback() {
        PaymentHistoryAdapter adapter;

        @Override
        public void onPreExecute() {
            adapter = (PaymentHistoryAdapter) historyRecycler.getAdapter();
            adapter.setLoading(true);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            adapter.setLoading(false);
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            Paging nextPage = paymentsModel.getPaymentPayload().getTransactionHistory().getPageDetails();
            if (nextPage.getCurrentPage() != paging.getCurrentPage()) {
                paging = nextPage;
                List<PaymentHistoryItem> newItems = paymentsModel.getPaymentPayload().getTransactionHistory().getPaymentHistoryList();
                setAdapter(newItems);
                paymentHistory.addAll(newItems);
            }
            isPaging = false;
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            adapter.setLoading(false);
            isPaging = false;
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };
}
