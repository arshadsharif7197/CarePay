package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.carecloud.carepay.patient.payment.adapters.SettingsCreditCardListAdapter;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCreditCardListFragment extends BaseFragment implements SettingsCreditCardListAdapter.OnCreditCardDetailClickListener {

    private RecyclerView creditCardsListRecyclerView;
    private LinearLayout noCreditCardsView;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;

    private DemographicsSettingsFragmentListener callback;


    public static SettingsCreditCardListFragment newInstance(){
        return new SettingsCreditCardListFragment();
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
        demographicsSettingsDTO = (DemographicsSettingsDTO) callback.getDto();

//        Bundle arguments = getArguments();
//        if (arguments != null) {
//            Gson gson = new Gson();
//            String demographicsSettingsDTOString = arguments.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
//            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_credit_cards_list, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("credit_cards_label"));
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        creditCardsListRecyclerView = (RecyclerView) view.findViewById(R.id.creditCardsListRecyclerView);
        creditCardsListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
    public void loadCreditCardsList(DemographicsSettingsDTO demographicsSettingsDTO) {
        if (demographicsSettingsDTO != null) {
            this.demographicsSettingsDTO = demographicsSettingsDTO;
            List<DemographicsSettingsCreditCardsPayloadDTO> creditCardList = demographicsSettingsDTO.getPayload().getPatientCreditCards();

            if (creditCardList.isEmpty()) {
                creditCardsListRecyclerView.setVisibility(View.GONE);
                noCreditCardsView.setVisibility(View.VISIBLE);
            } else {
                creditCardsListRecyclerView.setAdapter(new SettingsCreditCardListAdapter(getActivity(),
                        creditCardList, this));
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
    public void onCreditCardDetailClickListener(DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO) {
        callback.displayCreditCardDetailsFragment(creditCardsPayloadDTO);
    }
}