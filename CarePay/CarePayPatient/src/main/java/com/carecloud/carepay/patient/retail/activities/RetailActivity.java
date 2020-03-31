package com.carecloud.carepay.patient.retail.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.retail.fragments.RetailListFragment;
import com.carecloud.carepay.patient.retail.interfaces.RetailPatientInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.retail.fragments.RetailFragment;
import com.carecloud.carepaylibray.retail.models.RetailModel;
import com.carecloud.carepaylibray.retail.models.RetailPracticeDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

import java.util.HashMap;
import java.util.Map;

//import com.google.android.gms.wallet.MaskedWallet;

/**
 * Created by lmenendez on 2/8/17
 */

public class RetailActivity extends MenuPatientActivity implements RetailPatientInterface {

    private RetailModel retailModel;
    private PaymentsModel paymentsModel;

    private RetailFragment retailFragment;
    private String title;
    private boolean hideToolbar = false;

    private RetailPracticeDTO selectedPractice;
    private UserPracticeDTO userPracticeDTO;
    private Fragment androidPayTargetFragment;

    private Bundle webViewBundle = new Bundle();

    private boolean hasMultipleStores = false;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        retailModel = getConvertedDTO(RetailModel.class);
        paymentsModel = getConvertedDTO(PaymentsModel.class);
        if (retailModel == null) {
            callRetailService();
        } else {
            resumeOnCreate();
        }
    }

    private void callRetailService() {
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(getTransitionRetail(), new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                replaceFragment(ShimmerFragment.newInstance(R.layout.shimmer_retail_item, false),
                        false);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
                retailModel = DtoHelper.getConvertedDTO(RetailModel.class, workflowDTO);
                resumeOnCreate();
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                hideProgressDialog();
                showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        }, queryMap);
    }

    private void resumeOnCreate() {
        Fragment fragment;
        if (retailModel.getPayload().getRetailPracticeList().size() == 1) {
            selectedPractice = retailModel.getPayload().getRetailPracticeList().get(0);
            displayRetailStore(retailModel, selectedPractice, lookupUserPractice(selectedPractice),
                    false);
        } else {
            hasMultipleStores = true;
            fragment = RetailListFragment.newInstance(retailModel);
            replaceFragment(fragment, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectMenuItem(R.id.shopMenuItem);
        title = getScreenTitle(Label.getLabel("shop_button"));
        if (!hideToolbar) {
            displayToolbar(true);
        }
    }

    @Override
    public void onStop() {
        if (retailModel != null && !retailModel.getPayload().getRetailPracticeList().isEmpty()) {
            MixPanelUtil.logEvent(getString(R.string.event_retail_ended));
            MixPanelUtil.endTimer(getString(R.string.timer_shopping));
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
                forwardAndroidPayResult(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (retailFragment == null || !retailFragment.isAdded() || !retailFragment.handleBackButton()) {
            super.onBackPressed();
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.container_main);
            if (current instanceof RetailListFragment) {
                displayToolbar(true);
            }
        }
    }

    @Override
    public void displayRetailStore(RetailModel retailModel,
                                   RetailPracticeDTO retailPractice,
                                   UserPracticeDTO userPracticeDTO) {
        displayRetailStore(retailModel, retailPractice, userPracticeDTO, true);
    }

    private void displayRetailStore(RetailModel retailModel, RetailPracticeDTO retailPractice,
                                    UserPracticeDTO userPracticeDTO, boolean addToBackStack) {
        selectedPractice = retailPractice;
        userPracticeDTO.setPatientId(selectedPractice.getPatientId());
        this.userPracticeDTO = userPracticeDTO;
        retailFragment = RetailFragment.newInstance(retailModel, selectedPractice, userPracticeDTO, addToBackStack);
        replaceFragment(retailFragment, addToBackStack);
    }

    @Override
    public void displayToolbar(boolean visibility) {
        displayToolbar(visibility, title);
        hideToolbar = !visibility;
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
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.container_main, fragment, addToBackStack);
    }

    private UserPracticeDTO lookupUserPractice(RetailPracticeDTO retailPracticeDTO) {
        for (UserPracticeDTO userPracticeDTO : retailModel.getPayload().getPracticeInformation()) {
            if (retailPracticeDTO.getPracticeId() != null
                    && retailPracticeDTO.getPracticeId().equals(userPracticeDTO.getPracticeId())) {
                userPracticeDTO.setPatientId(retailPracticeDTO.getPatientId());
                this.userPracticeDTO = userPracticeDTO;
                return userPracticeDTO;
            }
        }
        return new UserPracticeDTO();
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        replaceFragment(PatientPaymentMethodFragment
                .newInstance(paymentsModel, amount, false), true);
        displayToolbar(false);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod,
                                      double amount,
                                      PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = ChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            replaceFragment(fragment, true);
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
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        RetailModel retailModel = DtoHelper.getConvertedDTO(RetailModel.class, workflowDTO);
        if (hasMultipleStores) {
            getSupportFragmentManager().popBackStack(RetailFragment.class.getName(), 0);
        } else {
            while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStackImmediate();//these need to go immediately
            }
            getSupportFragmentManager().popBackStack();//last one needs to go after we load the redirect
        }
        RetailFragment retailFragment = (RetailFragment) getSupportFragmentManager()
                .findFragmentByTag(RetailFragment.class.getName());
        if (retailFragment != null) {
            retailFragment.loadPaymentRedirectUrl(retailModel.getPayload().getReturnUrl(), false);
        } else {
            retailFragment = RetailFragment.newInstance(retailModel,
                    selectedPractice, userPracticeDTO,
                    getSupportFragmentManager().getBackStackEntryCount() > 0);
            retailFragment.loadPaymentRedirectUrl(retailModel.getPayload().getReturnUrl(), false);
            replaceFragment(retailFragment, true);
        }
    }

    @Override
    public void showPaymentPendingConfirmation(PaymentsModel paymentsModel, String practiceId) {
        new CustomMessageToast(this, Label.getLabel("payment_queued_patient"),
                CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();
    }

    @Override
    public void setAndroidPayTargetFragment(Fragment fragment) {
        androidPayTargetFragment = fragment;
    }

    @Override
    public Fragment getAndroidPayTargetFragment() {
        return androidPayTargetFragment;
    }

    @Override
    public void forwardAndroidPayResult(int requestCode, int resultCode, Intent data) {
        Fragment targetFragment = getAndroidPayTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        return userPracticeDTO;
    }

    @Override
    public void onPaymentCashFinished() {
        //NA
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Fragment fragment = AddNewCreditCardFragment.newInstance(paymentsModel, amount);
        replaceFragment(fragment, true);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {
        //Works only when chooseCreditCardFragment is used in selectMode
    }

    @Override
    public DTO getDto() {
        return retailModel;
    }

    @Override
    protected void onProfileChanged(ProfileDto profile) {
        title = getScreenTitle(Label.getLabel("shop_button"));
        displayToolbar(true, title);
        callRetailService();
    }

    @Override
    protected Profile getCurrentProfile() {
        return retailModel.getPayload().getDelegate();
    }
}
