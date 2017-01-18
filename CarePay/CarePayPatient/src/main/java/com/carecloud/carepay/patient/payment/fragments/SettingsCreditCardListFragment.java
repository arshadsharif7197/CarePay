package com.carecloud.carepay.patient.payment.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.payment.adapter.SettingsCreditCardListAdapter;
import com.carecloud.carepay.patient.payment.dialogs.PaymentAmountReceiptDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPatientCreditCardsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCreditCardListFragment extends Fragment implements SettingsCreditCardListAdapter.IOnCreditCardDetailClickListener{

    private RecyclerView creditCardsListRecyclerView;
    private Activity activity;

    private int selectedCreditCard;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private DemographicsSettingsLabelsDTO settingsLabelsDTO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String demographicsSettingsDTOString = arguments.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
            settingsLabelsDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO().getLabels();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_credit_cards_list, container, false);
        activity = getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(settingsLabelsDTO.getCreditCardsLabel());
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        creditCardsListRecyclerView = (RecyclerView) view.findViewById(R.id.creditCardsListRecyclerView);
        creditCardsListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button addNewCardButton = (Button) view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(addNewCardButtonListener);
        addNewCardButton.setText(settingsLabelsDTO.getSettingSetAsDefaultLabel());

        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPatientCreditCardsDTO patientCreditCards = demographicsSettingsDTO.getPayload()
                    .getPatientCreditCards();

            if (patientCreditCards != null) {
                List<DemographicsSettingsCreditCardsPayloadDTO> creditCardList = patientCreditCards.getPayload();
                creditCardsListRecyclerView.setAdapter(new SettingsCreditCardListAdapter(getActivity(),
                        creditCardList,settingsLabelsDTO,this));
            }
        }
    }

    private WorkflowServiceCallback makePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            PaymentAmountReceiptDialog receiptDialog = new PaymentAmountReceiptDialog(getActivity(),
                    gson.fromJson(workflowDTO.toString(), PaymentsModel.class));
            receiptDialog.show();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            System.out.print(exceptionMessage);
        }
    };

    private View.OnClickListener addNewCardButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
            AddNewCreditCardFragment fragment = (AddNewCreditCardFragment)
                fragmentmanager.findFragmentByTag(AddNewCreditCardFragment.class.getSimpleName());
            if (fragment == null) {
                fragment = new AddNewCreditCardFragment();
            }

            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            String paymentsDTOString = gson.toJson(paymentsModel);
            bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
            bundle.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(intakePaymentModel));
            bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, getArguments().getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE));
            fragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
            fragmentTransaction.replace(R.id.payment_frag_holder, fragment);
            fragmentTransaction.addToBackStack(AddNewCreditCardFragment.class.getSimpleName());
            fragmentTransaction.commit();*/
        }
    };

    @Override
    public void OnCreditCardDetailClickListener(int position) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String creditCardsPayloadDTOString = gson.toJson(demographicsSettingsDTO.getPayload()
                .getPatientCreditCards().getPayload().get(position));
        bundle.putString(CarePayConstants.CREDIT_CARD_BUNDLE, creditCardsPayloadDTOString);

        creditCardsPayloadDTOString = gson.toJson(demographicsSettingsDTO);
        bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, creditCardsPayloadDTOString);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        SettingsCreditCardDetailsFragment fragment = (SettingsCreditCardDetailsFragment)
                fm.findFragmentByTag(SettingsCreditCardDetailsFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new SettingsCreditCardDetailsFragment();
        }

        //fix for random crashes
        if(fragment.getArguments() !=null){
            fragment.getArguments().putAll(bundle);
        }else{
            fragment.setArguments(bundle);
        }

        fm.beginTransaction().replace(com.carecloud.carepay.patient.R.id.activity_demographics_settings, fragment,
                SettingsCreditCardDetailsFragment.class.getSimpleName()).addToBackStack(null).commit();
    }
}
