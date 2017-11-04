package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.patient.payment.adapters.CreditCardListAdapter;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreditCardListFragment extends BaseFragment implements CreditCardListAdapter.OnCreditCardDetailClickListener {

    private RecyclerView creditCardsListRecyclerView;
    private LinearLayout noCreditCardsView;
    private DemographicDTO demographicsSettingsDTO;

    private DemographicsSettingsFragmentListener callback;


    public static CreditCardListFragment newInstance() {
        return new CreditCardListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SettingsCreditCardListFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_credit_cards_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("credit_cards_label"));
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);

        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        initializeViews(view);
    }

    private void initializeViews(View view) {
        creditCardsListRecyclerView = (RecyclerView) view.findViewById(R.id.creditCardsListRecyclerView);
        creditCardsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noCreditCardsView = (LinearLayout) view.findViewById(R.id.no_credit_cards_view);

        Button addNewCardButton = (Button) view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(addNewCardButtonListener);

        loadCreditCardsList(demographicsSettingsDTO);
    }

    /**
     * Load credit cards list.
     *
     * @param demographicsSettingsDTO the demographics settings dto
     */
    public void loadCreditCardsList(DemographicDTO demographicsSettingsDTO) {
        if (demographicsSettingsDTO != null) {
            List<DemographicsSettingsCreditCardsPayloadDTO> creditCardList = demographicsSettingsDTO.getPayload().getPatientCreditCards();

            if (creditCardList.isEmpty()) {
                creditCardsListRecyclerView.setVisibility(View.GONE);
                noCreditCardsView.setVisibility(View.VISIBLE);
            } else {
                noCreditCardsView.setVisibility(View.GONE);
                creditCardsListRecyclerView.setAdapter(new CreditCardListAdapter(getContext(), creditCardList, this));
            }
        }
    }

    private View.OnClickListener addNewCardButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.displayAddCreditCardFragment();
        }
    };

    @Override
    public void onCreditCardDetail(DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO) {
        callback.displayCreditCardDetailsFragment(creditCardsPayloadDTO);
    }
}