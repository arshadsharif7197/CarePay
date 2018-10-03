package com.carecloud.carepay.patient.retail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayDialogFragment;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.retail.fragments.RetailListFragment;
import com.carecloud.carepay.patient.retail.interfaces.RetailPatientInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
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
import com.carecloud.carepaylibray.retail.fragments.RetailFragment;
import com.carecloud.carepaylibray.retail.models.RetailModel;
import com.carecloud.carepaylibray.retail.models.RetailPracticeDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.google.android.gms.wallet.MaskedWallet;

import java.util.HashMap;
import java.util.Map;

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
                retailModel = DtoHelper.getConvertedDTO(RetailModel.class, workflowDTO);
                resumeOnCreate();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        }, queryMap);
    }

    private void resumeOnCreate() {
        paymentsModel = getConvertedDTO(PaymentsModel.class);
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
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_purchase);
        menuItem.setChecked(true);
        title = menuItem.getTitle().toString();
        if (!hideToolbar) {
            displayToolbar(true);
        }
    }

    @Override
    public void onStop() {
        if (retailModel != null && !retailModel.getPayload().getRetailPracticeList().isEmpty()) {
            MixPanelUtil.logEvent(getString(R.string.event_retail_ended));
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_MASKED_WALLET:
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
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {
        // nothing
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
    public void showPaymentPendingConfirmation(PaymentsModel paymentsModel) {
        new CustomMessageToast(this, Label.getLabel("payments_external_pending"),
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
    public void createWalletFragment(MaskedWallet maskedWallet, Double amount) {
        replaceFragment(AndroidPayDialogFragment.newInstance(maskedWallet, paymentsModel, amount),
                true);
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
}
