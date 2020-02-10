package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.CreditCardBillingInformationDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass
 */
public class CreditCardDetailsFragment extends BaseFragment {

    private DemographicDTO demographicsSettingsDTO = null;
    private DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO = null;
    private static final String TAG = CreditCardDetailsFragment.class.getName();

    private DemographicsSettingsFragmentListener callback;


    /**
     * Instantiate new CreditCardDetailsFragment
     *
     * @param creditCardsPayloadDTO creditCard info
     * @return new fragment
     */
    public static CreditCardDetailsFragment newInstance(DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String creditCardsPayloadDTOString = gson.toJson(creditCardsPayloadDTO);
        bundle.putString(CarePayConstants.CREDIT_CARD_BUNDLE, creditCardsPayloadDTOString);

        CreditCardDetailsFragment creditCardDetailsFragment = new CreditCardDetailsFragment();
        creditCardDetailsFragment.setArguments(bundle);
        return creditCardDetailsFragment;
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
            demographicsSettingsDTO = (DemographicDTO) callback.getDto();

            String demographicsSettingsDTOString = arguments.getString(CarePayConstants.CREDIT_CARD_BUNDLE);
            creditCardsPayloadDTO = gson.fromJson(demographicsSettingsDTOString,
                    DemographicsSettingsCreditCardsPayloadDTO.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_credit_card_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(StringUtil.getFormattedCardNumber(
                creditCardsPayloadDTO.getPayload().getCardType(),
                creditCardsPayloadDTO.getPayload().getCardNumber()));
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        callback.setToolbar(toolbar);

        initializeViews(view);

    }

    private void initializeViews(View view) {

        if (demographicsSettingsDTO != null && creditCardsPayloadDTO != null) {
            TextView nameOnCardValue = view.findViewById(R.id.nameOnCardValue);
            nameOnCardValue.setText(creditCardsPayloadDTO.getPayload().getNameOnCard());

            TextView cardNumberValue = view.findViewById(R.id.cardNumberValue);
            cardNumberValue.setText(getMaskedCardNumber(creditCardsPayloadDTO.getPayload().getCardNumber(),
                    creditCardsPayloadDTO.getPayload().getCardType()));

            TextView expirationDateValue = view.findViewById(R.id.expirationDateValue);
            expirationDateValue.setText(creditCardsPayloadDTO.getPayload().getExpireDt());
            if (checkCreditCardExpired()) {
                expirationDateValue.setTextColor(getResources().getColor(R.color.redAlert));
            }

            TextView addressValue = view.findViewById(R.id.addressValue);
            TextView zipcodeValue = view.findViewById(R.id.zipcodeValue);
            TextView cityValue = view.findViewById(R.id.cityValue);
            TextView stateValue = view.findViewById(R.id.stateValue);

            CreditCardBillingInformationDTO billingInformationDTO = creditCardsPayloadDTO
                    .getPayload().getBillingInformation();
            if (StringUtil.isNullOrEmpty(billingInformationDTO.getLine1())
                    && billingInformationDTO.getSameAsPatient()) {
                DemographicAddressPayloadDTO addressDTO = demographicsSettingsDTO.getPayload()
                        .getDemographics().getPayload().getAddress();
                if (!StringUtil.isNullOrEmpty(addressDTO.getAddress1())) {
                    String fullAddress = addressDTO.getAddress1()
                            + (!StringUtil.isNullOrEmpty(addressDTO.getAddress2())
                            ? addressDTO.getAddress2() : "");
                    addressValue.setText(fullAddress);
                }
                zipcodeValue.setText(addressDTO.getZipcode());
                cityValue.setText(addressDTO.getCity());
                stateValue.setText(addressDTO.getState());
            } else {
                if (!StringUtil.isNullOrEmpty(billingInformationDTO.getLine1())) {
                    String fullAddress = billingInformationDTO.getLine1()
                            + (!StringUtil.isNullOrEmpty(billingInformationDTO.getLine2())
                            ? billingInformationDTO.getLine2() : "");
                    addressValue.setText(fullAddress);
                }
                zipcodeValue.setText(billingInformationDTO.getZip());
                cityValue.setText(billingInformationDTO.getCity());
                stateValue.setText(billingInformationDTO.getState());
            }
            Button setAsDefaultButton = view.findViewById(R.id.setAsDefaultButton);
            setAsDefaultButton.setOnClickListener(setAsDefaultButtonListener);

            setAsDefaultButton.setEnabled(!creditCardsPayloadDTO.getPayload().isDefault() && !checkCreditCardExpired());
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

    private String getMaskedCardNumber(String cardNumber, String cardType) {
        if (cardNumber == null) {
            cardNumber = "";
        }
        String formatted = "";
        int maxlength = maxLength(cardType);
        for (int i = 0; i < maxlength - cardNumber.length(); i++) {
            if (((i == 10 || i == 4) && maxlength == 15) || ((i % 4 == 0) && maxlength == 16)) {
                formatted += " ";
            }
            formatted += "X";
        }
        if (maxlength == 16) {
            formatted += " ";
        }
        formatted += cardNumber;
        return formatted;
    }

    private int maxLength(String cardType) {
        if (cardType.toLowerCase().contains("amex")
                || (cardType.toLowerCase().contains("american")
                && cardType.toLowerCase().contains("express"))) {
            return 15;
        }
        return 16;
    }

    private View.OnClickListener setAsDefaultButtonListener = view -> setAsDefaultRequest();

    private void setAsDefaultRequest() {
        try {
            JSONObject setAsDefaultPayload = new JSONObject();
            setAsDefaultPayload.put("card_id", creditCardsPayloadDTO.getPayload().getHashCreditCardsId());
            setAsDefaultPayload.put("is_default", true);
            String body = setAsDefaultPayload.toString();
            TransitionDTO transitionDTO = demographicsSettingsDTO.getMetadata().getTransitions().getUpdateCreditCard();
            getWorkflowServiceHelper().execute(transitionDTO, creditCardOperationCallback, body,
                    null, getWorkflowServiceHelper().getPreferredLanguageHeader());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeCreditCardRequest() {
        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("card_id", creditCardsPayloadDTO.getPayload().getHashCreditCardsId());

            TransitionDTO transitionDTO = demographicsSettingsDTO.getMetadata().getTransitions().getDeleteCreditCard();
            getWorkflowServiceHelper().execute(transitionDTO, creditCardOperationCallback, queryMap,
                    getWorkflowServiceHelper().getPreferredLanguageHeader());
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
                DemographicDTO removeCreditCardResponseDTO = DtoHelper
                        .getConvertedDTO(DemographicDTO.class, workflowDTO);
                demographicsSettingsDTO.getPayload()
                        .setPatientCreditCards(removeCreditCardResponseDTO.getPayload().getPatientCreditCards());
                callback.onCreditCardOperation(demographicsSettingsDTO);
                SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
                MixPanelUtil.logEvent(getString(R.string.event_updated_credit_cards), getString(R.string.param_is_payment), false);
                getActivity().onBackPressed();
            } catch (Exception e) {
                Log.e(TAG, "Credit Card onPostExecute" + e.getMessage());
                showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(TAG, "Credit Card onFailure" + exceptionMessage);
        }
    };

    private void showConfirmRemoveDialog() {
        String title = Label.getLabel("settings.removeCreditCard.confirmation.label.title");
        String message = String.format(Label.getLabel("settings.removeCreditCard.confirmation.label.description"),
                creditCardsPayloadDTO.getPayload().getCardNumber());
        ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(title, message);
        dialogFragment.setCallback(this::removeCreditCardRequest);
        FragmentManager fm = getFragmentManager();
        dialogFragment.show(fm, dialogFragment.getClass().getName());
    }

    private boolean checkCreditCardExpired() {
        Date expDate = DateUtil.getInstance().setDateRaw(creditCardsPayloadDTO.getPayload().getExpireDt()).getDate();
        expDate = DateUtil.getLastDayOfMonth(expDate);
        expDate = DateUtil.getLastHourOfDay(expDate);
        return expDate.before(new Date());
    }
}
