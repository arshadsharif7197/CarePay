package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
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
import com.carecloud.carepaylibray.appointments.models.IntelligentSchedulerDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.google.gson.Gson;

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
            IntelligentSchedulerDTO intelligentSchedulerDTO = new Gson().fromJson(allQuestions, IntelligentSchedulerDTO.class);
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
        Toolbar toolbar = view.findViewById(R.id.intelligent_scheduler_toolbar);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.intelligent_scheduler_title);
            TextView exit = toolbar.findViewById(R.id.intelligent_scheduler_exit);
            title.setText(Label.getLabel("add_appointment_header"));
            exit.setText(Label.getLabel("demographics_exit"));
            toolbar.setTitle("");

            if (getDialog() == null) {
                toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
                toolbar.setNavigationOnClickListener(view12 -> callback.onBack());
            }
            exit.setOnClickListener((view1) -> callback.onExit());
        }
    }

    private void initializeViews(View view) {
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        nextButton.setEnabled(false);
//        nextButton.setText(Label.getLabel("common.button.continue"));

/*        RecyclerView creditCardsRecyclerView = view.findViewById(R.id.list_credit_cards);
        creditCardsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final CreditCardsListAdapter creditCardsListAdapter = new CreditCardsListAdapter(getContext(),
                creditCardList, this, true);
        creditCardsRecyclerView.setAdapter(creditCardsListAdapter);*/
    }

  /*  @Override
    public void onCreditCardItemSelected(PaymentCreditCardsPayloadDTO creditCard) {
            nextButton.setEnabled(!expDate.before(new Date()));
    }*/
}