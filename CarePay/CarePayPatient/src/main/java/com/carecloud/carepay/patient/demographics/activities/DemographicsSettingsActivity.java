package com.carecloud.carepay.patient.demographics.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.demographics.fragments.settings.ChangePasswordFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.DemographicsExpandedFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.DemographicsInformationFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.DemographicsSettingsFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.EditProfileFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.HelpFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.SettingsDocumentsFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.SupportFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.UpdateEmailFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.UpdateNameFragment;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.patient.payment.fragments.CreditCardDetailsFragment;
import com.carecloud.carepay.patient.payment.fragments.CreditCardListFragment;
import com.carecloud.carepay.patient.payment.fragments.SettingAddCreditCardFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInUser;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.PhysicianDto;
import com.carecloud.carepaylibray.demographics.fragments.EmergencyContactFragment;
import com.carecloud.carepaylibray.demographics.fragments.HomeAlertDialogFragment;
import com.carecloud.carepaylibray.demographics.fragments.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.fragments.SearchPhysicianFragment;
import com.carecloud.carepaylibray.demographics.interfaces.DemographicExtendedInterface;
import com.carecloud.carepaylibray.demographics.interfaces.EmergencyContactFragmentInterface;
import com.carecloud.carepaylibray.demographics.interfaces.PhysicianFragmentInterface;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Main activity for Settings workflow
 */
public class DemographicsSettingsActivity extends BasePatientActivity implements
        DemographicsSettingsFragmentListener, InsuranceEditDialog.InsuranceEditDialogListener,
        DemographicExtendedInterface {

    DemographicDTO demographicsSettingsDTO;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographics_settings);

        demographicsSettingsDTO = getConvertedDTO(DemographicDTO.class);
        rootView = findViewById(R.id.activity_demographics_settings);

        getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants.DEMOGRAPHICS_ADDRESS_BUNDLE,
                demographicsSettingsDTO.getPayload().getDemographics().getPayload().getAddress());

        if (savedInstanceState == null) {
            DemographicsSettingsFragment fragment = DemographicsSettingsFragment.newInstance();
            replaceFragment(fragment, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId() == R.id.action_remove_credit_card || item.getItemId() == R.id.deleteEmergencyContact) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        rootView.requestFocus();
        SystemUtil.hideSoftKeyboard(this);
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onCreditCardOperation(DemographicDTO demographicsSettingsDTO) {
        if (getSupportFragmentManager().findFragmentByTag(CreditCardListFragment.class.getName()) != null) {
            ((CreditCardListFragment) getSupportFragmentManager()
                    .findFragmentByTag(CreditCardListFragment.class.getName()))
                    .loadCreditCardsList(demographicsSettingsDTO);
        }
        this.demographicsSettingsDTO = demographicsSettingsDTO;
    }

    @Override
    public void logOut() {
        UnifiedSignInUser user = new UnifiedSignInUser();
        user.setEmail(getApplicationPreferences().getUserId());
        user.setDeviceToken(((AndroidPlatform) Platform.get()).openDefaultSharedPreferences()
                .getString(CarePayConstants.FCM_TOKEN, null));
        UnifiedSignInDTO signInDTO = new UnifiedSignInDTO();
        signInDTO.setUser(user);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("x-api-key", HttpConstants.getApiStartKey());
        headersMap.put("transition", "true");
        Map<String, String> queryMap = new HashMap<>();
        String payload = new Gson().toJson(signInDTO);
        getWorkflowServiceHelper().execute(MenuPatientActivity.getTransitionLogout(), logoutWorkflowCallback,
                payload, queryMap, headersMap);

    }

    @Override
    public void displayEditProfileFragment() {
        EditProfileFragment editProfileFragment = EditProfileFragment.newInstance();
        replaceFragment(editProfileFragment, true);
    }

    @Override
    public void displayUpdateEmailFragment() {
        UpdateEmailFragment updateEmailFragment = UpdateEmailFragment.newInstance();
        replaceFragment(updateEmailFragment, true);
    }

    @Override
    public void displayUpdatePasswordFragment() {
        ChangePasswordFragment changePasswordFragment = ChangePasswordFragment.newInstance();
        replaceFragment(changePasswordFragment, true);
    }

    @Override
    public void displayUpdateNameFragment() {
        UpdateNameFragment updateNameFragment = UpdateNameFragment.newInstance();
        replaceFragment(updateNameFragment, true);
    }

    @Override
    public void displayDemographicsFragment() {
        DemographicsInformationFragment demographicsInformationFragment =
                DemographicsInformationFragment.newInstance();
        replaceFragment(demographicsInformationFragment, true);
    }

    @Override
    public void displayExpandedDemographicsFragment() {
        DemographicsExpandedFragment demographicsExpandedFragment = DemographicsExpandedFragment.newInstance();
        replaceFragment(demographicsExpandedFragment, true);
    }

    @Override
    public void displayDocumentsFragment() {
        SettingsDocumentsFragment settingsDocumentsFragment = SettingsDocumentsFragment.newInstance();
        replaceFragment(settingsDocumentsFragment, true);
    }

    @Override
    public void editInsurance(DemographicDTO demographicDTO, int editedIndex) {
        InsuranceEditDialog insuranceEditDialog = InsuranceEditDialog
                .newInstance(demographicDTO, editedIndex, false, false);

        replaceFragment(insuranceEditDialog, true);
    }


    @Override
    public void displayCreditCardListFragment() {
        CreditCardListFragment creditCardListFragment = new CreditCardListFragment();
        replaceFragment(creditCardListFragment, true);
    }

    @Override
    public void displayAddCreditCardFragment() {
        SettingAddCreditCardFragment settingAddCreditCardFragment = new SettingAddCreditCardFragment();
        replaceFragment(settingAddCreditCardFragment, true);
    }

    @Override
    public void displayCreditCardDetailsFragment(DemographicsSettingsCreditCardsPayloadDTO
                                                         creditCardsPayloadDTO) {
        CreditCardDetailsFragment creditCardDetailsFragment = CreditCardDetailsFragment
                .newInstance(creditCardsPayloadDTO);
        replaceFragment(creditCardDetailsFragment, true);
    }

    @Override
    public void displayHelpFragment() {
        replaceFragment(new HelpFragment(), true);
        MixPanelUtil.logEvent(getString(R.string.event_help_clicked));
    }

    @Override
    public void showSupportFragment() {
        replaceFragment(new SupportFragment(), true);
    }

    @Override
    public DTO getDto() {
        return demographicsSettingsDTO;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.activity_demographics_settings, fragment, addToBackStack);
    }

    @Override
    public void onInsuranceEdited(DemographicDTO demographicDTO, boolean proceed) {
        SystemUtil.hideSoftKeyboard(this);
        onBackPressed();

        FragmentManager fm = getSupportFragmentManager();

        // Update Health Insurance Fragment
        String tag = SettingsDocumentsFragment.class.getName();
        SettingsDocumentsFragment settingsDocumentsFragment =
                (SettingsDocumentsFragment) fm.findFragmentByTag(tag);

        settingsDocumentsFragment.updateInsuranceList(demographicDTO);
    }

    @Override
    public void goOneStepBack() {
        onBackPressed();
    }

    @Override
    public void showRemovePrimaryInsuranceDialog(HomeAlertDialogFragment.HomeAlertInterface callback) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HomeAlertDialogFragment homeAlertDialogFragment = HomeAlertDialogFragment
                .newInstance(Label.getLabel("demographics_insurance_primary_alert_title"),
                        Label.getLabel("demographics_insurance_primary_alert_message"));
        homeAlertDialogFragment.setCallback(callback);
        String tag = homeAlertDialogFragment.getClass().getName();
        homeAlertDialogFragment.show(ft, tag);
    }

    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.activity_demographics_settings, fragment, addToBackStack);
    }

    @Override
    public void showAddEditEmergencyContactDialog() {
        addFragment(EmergencyContactFragment.newInstance(), true);
    }

    @Override
    public void updateEmergencyContact(PatientModel emergencyContact) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.activity_demographics_settings);
        if (fragment instanceof EmergencyContactFragmentInterface) {
            ((EmergencyContactFragmentInterface) fragment).updateEmergencyContact(emergencyContact);
        }
    }

    @Override
    public void showSearchPhysicianFragmentDialog(PhysicianDto physicianDto, int physicianType) {
        addFragment(SearchPhysicianFragment.newInstance(physicianDto, physicianType), true);
    }

    @Override
    public void onPhysicianSelected(PhysicianDto physician, int physicianType) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.activity_demographics_settings);
        if (fragment instanceof PhysicianFragmentInterface) {
            ((PhysicianFragmentInterface) fragment).setPhysician(physician, physicianType);
        }
    }

    @Override
    public AppointmentDTO getAppointment() {
        return null;
    }

    private WorkflowServiceCallback logoutWorkflowCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            getAppAuthorizationHelper().setAccessToken(null);
            navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
