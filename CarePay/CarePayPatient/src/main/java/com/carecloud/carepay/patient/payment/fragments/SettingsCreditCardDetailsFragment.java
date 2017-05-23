package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.demographics.activities.DemographicsSettingsActivity;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCreditCardDetailsFragment extends BaseFragment {

    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO = null;
    private static final String TAG = SettingsCreditCardDetailsFragment.class.getName();

    private DemographicsSettingsFragmentListener callback;

    public interface IOnCreditCardOperationSuccess {
        void onCreditCardOperation(DemographicsSettingsDTO demographicsSettingsDTO);
    }


    public static SettingsCreditCardDetailsFragment newInstance(DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO){
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String creditCardsPayloadDTOString = gson.toJson(creditCardsPayloadDTO);
        bundle.putString(CarePayConstants.CREDIT_CARD_BUNDLE, creditCardsPayloadDTOString);

        SettingsCreditCardDetailsFragment settingsCreditCardDetailsFragment = new SettingsCreditCardDetailsFragment();
        settingsCreditCardDetailsFragment.setArguments(bundle);
        return settingsCreditCardDetailsFragment;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String demographicsSettingsDTOString = arguments.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
//            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
            demographicsSettingsDTO = (DemographicsSettingsDTO) callback.getDto();

            demographicsSettingsDTOString = arguments.getString(CarePayConstants.CREDIT_CARD_BUNDLE);
            creditCardsPayloadDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsCreditCardsPayloadDTO.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_credit_card_details, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(StringUtil.getFormattedCardNumber(creditCardsPayloadDTO.getPayload()
                .getCardType(), creditCardsPayloadDTO.getPayload().getCardNumber()));
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {

        Button setAsDefaultButton = (Button) view.findViewById(R.id.setAsDefaultButton);
        setAsDefaultButton.setOnClickListener(setAsDefaultButtonListener);
        setAsDefaultButton.setText(Label.getLabel("setting_set_as_default"));

        if (demographicsSettingsDTO != null) {
            try {
                CarePayTextView nameOnCardLabel = (CarePayTextView) view.findViewById(R.id.nameOnCardLabel);
                CarePayTextView nameOnCardValue = (CarePayTextView) view.findViewById(R.id.nameOnCardValue);
                nameOnCardLabel.setText(Label.getLabel("credit_card_name_label"));
                nameOnCardValue.setText(creditCardsPayloadDTO.getPayload().getNameOnCard());

                CarePayTextView cardNumberLabel = (CarePayTextView) view.findViewById(R.id.cardNumberLabel);
                CarePayTextView cardNumberValue = (CarePayTextView) view.findViewById(R.id.cardNumberValue);
                cardNumberLabel.setText(Label.getLabel("credit_card_number_label"));
                cardNumberValue.setText(creditCardsPayloadDTO.getPayload().getCardNumber());

                CarePayTextView expirationDateLabel = (CarePayTextView) view.findViewById(R.id.expirationDateLabel);
                CarePayTextView expirationDateValue = (CarePayTextView) view.findViewById(R.id.expirationDateValue);
                expirationDateLabel.setText(Label.getLabel("credit_card_expiration_label"));
                expirationDateValue.setText(creditCardsPayloadDTO.getPayload().getExpireDt());

                CarePayTextView billingAddressLabel = (CarePayTextView) view.findViewById(R.id.billingAddressLabel);
                billingAddressLabel.setText(Label.getLabel("credit_card_billing_address_label"));

                CarePayTextView addressLabel = (CarePayTextView) view.findViewById(R.id.addressLabel);
                CarePayTextView addressValue = (CarePayTextView) view.findViewById(R.id.addressValue);
                addressLabel.setText(Label.getLabel("setting_address"));

                DemographicAddressPayloadDTO addressDTO = demographicsSettingsDTO.getPayload().getDemographics()
                        .getPayload().getAddress();
                addressValue.setText(addressDTO.getAddress1());

                CarePayTextView zipcodeLabel = (CarePayTextView) view.findViewById(R.id.zipcodeLabel);
                CarePayTextView zipcodeValue = (CarePayTextView) view.findViewById(R.id.zipcodeValue);
                zipcodeLabel.setText(Label.getLabel("zipcode_label"));
                zipcodeValue.setText(addressDTO.getZipcode());

                CarePayTextView cityLabel = (CarePayTextView) view.findViewById(R.id.cityLabel);
                CarePayTextView cityValue = (CarePayTextView) view.findViewById(R.id.cityValue);
                cityLabel.setText(Label.getLabel("city_label"));
                cityValue.setText(addressDTO.getCity());

                CarePayTextView stateLabel = (CarePayTextView) view.findViewById(R.id.stateLabel);
                CarePayTextView stateValue = (CarePayTextView) view.findViewById(R.id.stateValue);
                stateLabel.setText(Label.getLabel("state_label"));
                stateValue.setText(addressDTO.getState());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.setting_credit_card, menu);
        menu.getItem(0).setTitle(Label.getLabel("edit_credit_card_remove_label"));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_remove_credit_card) {
            showConfirmRemoveDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener setAsDefaultButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setAsDefaultRequest();
        }
    };

    private void setAsDefaultRequest() {
        try {
            JSONObject setAsDefaultPayload = new JSONObject();
            setAsDefaultPayload.put("card_id", creditCardsPayloadDTO.getPayload().getHashCreditCardsId());
            setAsDefaultPayload.put("is_default", true);
            String body = setAsDefaultPayload.toString();
            TransitionDTO transitionDTO = demographicsSettingsDTO.getMetadata().getTransitions().getUpdateCreditCard();
            getWorkflowServiceHelper().execute(transitionDTO, creditCardOperationCallback, body, null, getWorkflowServiceHelper().getPreferredLanguageHeader());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeCreditCardRequest() {
        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("card_id", creditCardsPayloadDTO.getPayload().getHashCreditCardsId());

            TransitionDTO transitionDTO = demographicsSettingsDTO.getMetadata().getTransitions().getDeleteCreditCard();
            getWorkflowServiceHelper().execute(transitionDTO, creditCardOperationCallback, queryMap, getWorkflowServiceHelper().getPreferredLanguageHeader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WorkflowServiceCallback creditCardOperationCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            try {
                Gson gson = new Gson();
                DemographicsSettingsDTO removeCreditCardResponseDTO = gson.fromJson(workflowDTO.toString(),
                        DemographicsSettingsDTO.class);
                demographicsSettingsDTO.getPayload().setPatientCreditCards(removeCreditCardResponseDTO.getPayload()
                        .getPatientCreditCards());
                ((DemographicsSettingsActivity) getActivity()).onCreditCardOperation(demographicsSettingsDTO);
                getActivity().onBackPressed();

                SystemUtil.showSuccessToast(getActivity(), Label.getLabel("settings_saved_success_message"));
            } catch (Exception e) {
                Log.e(TAG, "Credit Card onPostExecute" + e.getMessage());
                showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(TAG, "Credit Card onFailure" + exceptionMessage);
        }
    };

    private void showConfirmRemoveDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage("This credit card will be removed?")
                .setPositiveButton(Label.getLabel("edit_credit_card_remove_label"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        removeCreditCardRequest();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
