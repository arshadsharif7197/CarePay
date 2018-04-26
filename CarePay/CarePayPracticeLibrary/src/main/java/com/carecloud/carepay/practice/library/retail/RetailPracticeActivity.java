package com.carecloud.carepay.practice.library.retail;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.adapters.LanguageAdapter;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.retail.RetailModel;
import com.carecloud.carepaylibray.retail.RetailPracticeDTO;
import com.carecloud.carepaylibray.retail.fragments.RetailFragment;
import com.carecloud.carepaylibray.retail.interfaces.RetailInterface;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 20/04/18.
 */
public class RetailPracticeActivity extends BasePracticeActivity implements RetailInterface,
        PaymentMethodDialogInterface {

    private RetailModel retailModel;
    private boolean isUserInteraction = false;
    private Bundle webViewBundle = new Bundle();
    private PaymentsModel paymentsModel;
    private RetailPracticeDTO retailPractice;
    private UserPracticeDTO userPracticeDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retailModel = getConvertedDTO(RetailModel.class);
        paymentsModel = getConvertedDTO(PaymentsModel.class);
        setContentView(R.layout.activity_retail);
        TextView retailMessageTextView = (TextView) findViewById(R.id.retailMessageTextView);
        retailMessageTextView.setText(String
                .format(Label.getLabel("retail.patientModeRetail.leftPanel.message.title"),
                        retailModel.getPayload().getPracticeInformation().get(0).getPracticeName()));

        View logout = findViewById(R.id.logoutTextview);
        logout.setOnClickListener(homeClick);
        View home = findViewById(R.id.btnHome);
        home.setOnClickListener(homeClick);
        initializeLanguageSpinner(retailModel.getPayload().getLanguages(),
                retailModel.getMetadata().getLinks().getLanguage());

        if (savedInstanceState == null) {
            retailPractice = retailModel.getPayload().getRetailPracticeList().get(0);
            userPracticeDTO = retailModel.getPayload().getPracticeInformation().get(0);
            userPracticeDTO.setPatientId(retailPractice.getPatientId());
            replaceFragment(RetailFragment
                            .newInstance(retailPractice, userPracticeDTO, false),
                    false);
        }
    }

    private View.OnClickListener homeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            goToHome(retailModel.getMetadata().getTransitions().getLogout());
        }
    };

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.retailContainer, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.retailContainer, fragment, addToBackStack);
    }

    @Override
    public DTO getDto() {
        return retailModel;
    }

    private void initializeLanguageSpinner(List<OptionDTO> languages, final TransitionDTO languageTransition) {
        String selectedLanguageStr = getApplicationPreferences().getUserLanguage();
        OptionDTO selectedLanguage = languages.get(0);
        for (OptionDTO language : languages) {
            if (selectedLanguageStr.equals(language.getCode())) {
                selectedLanguage = language;
            }
        }

        final TextView languageSwitch = (TextView) findViewById(R.id.languageSpinner);
        final View languageContainer = findViewById(R.id.languageContainer);
        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageContainer.setVisibility(languageContainer.getVisibility() == View.VISIBLE
                        ? View.GONE : View.VISIBLE);
            }
        });
        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        headers.put("username", getApplicationPreferences().getUserName());
        headers.put("username_patient", getApplicationPreferences().getPatientId());
        RecyclerView languageList = (RecyclerView) findViewById(R.id.languageList);
        LanguageAdapter languageAdapter = new LanguageAdapter(languages, selectedLanguage);
        languageList.setAdapter(languageAdapter);
        languageList.setLayoutManager(new LinearLayoutManager(getContext()));
        languageAdapter.setCallback(new LanguageAdapter.LanguageInterface() {
            @Override
            public void onLanguageSelected(OptionDTO language) {
                languageContainer.setVisibility(View.GONE);
                if (!isUserInteraction) {
                    return;
                }
                changeLanguage(languageTransition, language.getCode().toLowerCase(), headers, new BasePracticeActivity.SimpleCallback() {
                    @Override
                    public void callback() {
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root_layout);
                        getSupportFragmentManager().popBackStackImmediate();
                        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
                    }
                });
            }
        });
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        isUserInteraction = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.languageContainer).setVisibility(View.GONE);
            }
        }, 25);
    }

    @Override
    public void displayRetailStore(RetailModel retailModel, RetailPracticeDTO retailPractice,
                                   UserPracticeDTO practiceDTO) {

    }

    @Override
    public void displayToolbar(boolean visibility) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!visibility);
    }

    @Override
    public PaymentsModel getPaymentModel() {
        return paymentsModel;
    }

    @Override
    public Bundle getWebViewBundle() {
        return webViewBundle;
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        PracticeAddNewCreditCardFragment fragment = new PracticeAddNewCreditCardFragment();
        fragment.setArguments(args);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        RetailModel retailModel = DtoHelper.getConvertedDTO(RetailModel.class, workflowDTO);
        getSupportFragmentManager().popBackStack(RetailFragment.class.getName(), 0);
        RetailFragment retailFragment = (RetailFragment) getSupportFragmentManager()
                .findFragmentByTag(RetailFragment.class.getName());
        if (retailFragment != null) {
            retailFragment.loadPaymentRedirectUrl(retailModel.getPayload().getReturnUrl(), true);
        } else {
            retailFragment = RetailFragment.newInstance(retailModel,
                    retailPractice, userPracticeDTO,
                    getSupportFragmentManager().getBackStackEntryCount() > 0);
            retailFragment.loadPaymentRedirectUrl(retailModel.getPayload().getReturnUrl(), false);
            replaceFragment(retailFragment, true);
        }
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        return retailModel.getPayload().getPracticeInformation().get(0);
    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {

    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount,
                                      PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            ChooseCreditCardFragment fragment = ChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            displayDialogFragment(fragment, true);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Nullable
    @Override
    public String getAppointmentId() {
        return null;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        return null;
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        displayDialogFragment(PracticePaymentMethodDialogFragment.newInstance(paymentsModel, amount), true);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.retailContainer) instanceof RetailFragment) {
            RetailFragment retailFragment = (RetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.retailContainer);
            if (!retailFragment.handleBackButton()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {

    }
}
