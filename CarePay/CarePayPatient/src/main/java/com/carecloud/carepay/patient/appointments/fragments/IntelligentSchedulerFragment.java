package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.carecloud.carepay.patient.appointments.interfaces.IntelligentSchedulerCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

public class IntelligentSchedulerFragment extends BaseDialogFragment {

    protected Button nextButton;
    private String allQuestions = "";
    private IntelligentSchedulerCallback callback;

    public static IntelligentSchedulerFragment newInstance(String intelligentQuestions) {
        Bundle args = new Bundle();
        args.putString(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY, intelligentQuestions);
        IntelligentSchedulerFragment intelligentSchedulerFragment = new IntelligentSchedulerFragment();
        intelligentSchedulerFragment.setArguments(args);
        return intelligentSchedulerFragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle arguments = getArguments();
        if (arguments != null) {
            allQuestions = arguments.getString(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intelligent_scheduler_questions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        setupTitleViews(view);
        initializeViews(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (IntelligentSchedulerCallback) context;
        } else {
            throw new ClassCastException("context must implements FragmentActivityInterface");
        }
    }

    private void setupTitleViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(Label.getLabel("add_appointment_header"));
            toolbar.setTitle("");
            if (getDialog() == null) {
                toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
                toolbar.setNavigationOnClickListener(view12 -> callback.onCancel());
            } else {
                View close = view.findViewById(R.id.closeViewLayout);
                if (close != null) {
                    close.setOnClickListener(view1 -> cancel());
                }
                ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                title.setLayoutParams(layoutParams);
                title.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    private void initializeViews(View view) {
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(nextButtonListener);
        nextButton.setEnabled(false);
//        nextButton.setText(Label.getLabel("common.button.continue"));

/*        RecyclerView creditCardsRecyclerView = view.findViewById(R.id.list_credit_cards);
        creditCardsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final CreditCardsListAdapter creditCardsListAdapter = new CreditCardsListAdapter(getContext(),
                creditCardList, this, true);
        creditCardsRecyclerView.setAdapter(creditCardsListAdapter);*/
    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
         /*   if (selectedCreditCard != null) {
                if (onlySelectMode) {
                    callback.onCreditCardSelected(selectedCreditCard);
                } else {
                    nextButton.setEnabled(false);
                    IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
                    if (postModel != null && postModel.getAmount() > 0) {
                        processPayment(postModel);
                    } else {
                        processPayment();
                    }
                }
            }*/
        }
    };

  /*  @Override
    public void onCreditCardItemSelected(PaymentCreditCardsPayloadDTO creditCard) {
            nextButton.setEnabled(!expDate.before(new Date()));
    }*/
}