package com.carecloud.carepay.practice.library.retail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.adapters.LanguageAdapter;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerLanguage;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.retail.fragments.RetailFragment;
import com.carecloud.carepaylibray.retail.interfaces.RetailInterface;
import com.carecloud.carepaylibray.retail.models.RetailModel;
import com.carecloud.carepaylibray.retail.models.RetailPracticeDTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 20/04/18.
 */
public class RetailPracticeActivity extends BasePracticeActivity implements RetailInterface,
        PaymentMethodDialogInterface {

    private RetailModel retailModel;
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
        setLeftPanelTexts();

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
            displayRetailStore(retailModel, retailPractice, userPracticeDTO);
        }
    }

    private void setLeftPanelTexts() {
        TextView retailMessageTextView = findViewById(R.id.retailMessageTextView);
        retailMessageTextView.setText(String
                .format(Label.getLabel("retail.patientModeRetail.leftPanel.message.title"),
                        retailModel.getPayload().getPracticeInformation().get(0).getPracticeName()));
        TextView retailSubMessageTextView = findViewById(R.id.retailSubMessageTextView);
        retailSubMessageTextView.setText(Label.getLabel("retail.patientModeRetail.leftPanel.message.subtitle"));

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
        final TextView languageSwitch = findViewById(R.id.languageSpinner);
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        headers.put("username", getApplicationPreferences().getUserName());
        headers.put("username_patient", getApplicationPreferences().getPatientId());
        final PopupPickerLanguage popupPickerLanguage = new PopupPickerLanguage(getContext(), true,
                languages, new LanguageAdapter.LanguageInterface() {
            @Override
            public void onLanguageSelected(OptionDTO language) {
                changeLanguage(languageTransition, language.getCode().toLowerCase(), headers, new BasePracticeActivity.SimpleCallback() {
                    @Override
                    public void callback() {
                        setLeftPanelTexts();
                        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
                    }
                });
            }
        });
        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int offsetX = view.getWidth() / 2 - popupPickerLanguage.getWidth() / 2;
                int offsetY = -view.getHeight() - popupPickerLanguage.getHeight();
                popupPickerLanguage.showAsDropDown(view, offsetX, offsetY);
            }
        });
        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
    }

    @Override
    public void displayRetailStore(RetailModel retailModel, RetailPracticeDTO retailPractice,
                                   UserPracticeDTO practiceDTO) {
        RetailFragment fragment = RetailFragment.newInstance(retailPractice, userPracticeDTO, false);
        replaceFragment(fragment, false);
    }

    @Override
    public void displayToolbar(boolean visibility) {
        //no toolbar to show/hide in practice app
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
        PracticeAddNewCreditCardFragment fragment = PracticeAddNewCreditCardFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {

    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        RetailModel retailModel = DtoHelper.getConvertedDTO(RetailModel.class, workflowDTO);
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

        //need to reset fullscreen after dismissing payment dialogs
        setSystemUiVisibility();
        setNavigationBarVisibility();
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
        //when dismissing the payment method dialog the nav bar was showing up
        setSystemUiVisibility();
        setNavigationBarVisibility();
    }

    @Override
    public void onCashSelected(PaymentsModel paymentsModel) {
        //Not implemented
    }

    @Override
    public void onStop() {
        if (retailModel != null && !retailModel.getPayload().getRetailPracticeList().isEmpty()) {
            MixPanelUtil.logEvent(getString(R.string.event_retail_ended));
            MixPanelUtil.endTimer(getString(R.string.timer_shopping));
        }
        super.onStop();
    }
}
