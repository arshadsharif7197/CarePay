package com.carecloud.carepay.patient.demographics.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.demographics.fragments.settings.DemographicsInformationFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.DemographicsSettingsDocumentsFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.DemographicsSettingsFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.EditProfileFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.HelpFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.SupportFragment;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.patient.payment.fragments.SettingAddCreditCardFragment;
import com.carecloud.carepay.patient.payment.fragments.CreditCardDetailsFragment;
import com.carecloud.carepay.patient.payment.fragments.CreditCardListFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

/**
 * Main activity for Settings workflow
 */
public class DemographicsSettingsActivity extends BasePatientActivity implements
        DemographicsSettingsFragmentListener,
        CarePayCameraReady, CarePayCameraCallback {

    DemographicsSettingsDTO demographicsSettingsDTO;
    private CarePayCameraCallback carePayCameraCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographics_settings);

        demographicsSettingsDTO = getConvertedDTO(DemographicsSettingsDTO.class);

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
            SystemUtil.hideSoftKeyboard(this);
            onBackPressed();
        }
        if (item.getItemId() == R.id.action_remove_credit_card) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onCreditCardOperation(DemographicsSettingsDTO demographicsSettingsDTO) {
        if (getSupportFragmentManager().findFragmentByTag(CreditCardListFragment.class.getName()) != null) {
            ((CreditCardListFragment) getSupportFragmentManager()
                    .findFragmentByTag(CreditCardListFragment.class.getName()))
                    .loadCreditCardsList(demographicsSettingsDTO);
        }
        this.demographicsSettingsDTO = demographicsSettingsDTO;
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(final Fragment fragment, final boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.activity_demographics_settings, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void displayEditProfileFragment() {
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        EditProfileFragment fragment = (EditProfileFragment)
//                fm.findFragmentByTag(EditProfileFragment.class.getSimpleName());
//        if (fragment == null) {
//            fragment = EditProfileFragment.newInstance();
//        }
//        fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
//                EditProfileFragment.class.getSimpleName()).addToBackStack(null).commit();

        EditProfileFragment editProfileFragment = EditProfileFragment.newInstance();
        replaceFragment(editProfileFragment, true);
    }

    @Override
    public void displayDemographicsFragment() {
        DemographicsInformationFragment demographicsInformationFragment = DemographicsInformationFragment.newInstance();
        replaceFragment(demographicsInformationFragment, true);
    }

    @Override
    public void displayExpandedDemographicsFragment() {

    }

    @Override
    public void displayDocumentsFragment() {
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        DemographicsSettingsDocumentsFragment fragment = (DemographicsSettingsDocumentsFragment)
//                fm.findFragmentByTag(DemographicsSettingsDocumentsFragment.class.getSimpleName());
//        if (fragment == null) {
//            fragment = DemographicsSettingsDocumentsFragment.newInstance();
//        }
//        fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
//                DemographicsSettingsDocumentsFragment.class.getSimpleName()).addToBackStack(null).commit();
//
        DemographicsSettingsDocumentsFragment demographicsSettingsDocumentsFragment = DemographicsSettingsDocumentsFragment.newInstance();
        replaceFragment(demographicsSettingsDocumentsFragment, true);
    }

    @Override
    public void displayCreditCardListFragment() {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
        bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);

        FragmentManager fm = getSupportFragmentManager();
        CreditCardListFragment fragment = (CreditCardListFragment)
                fm.findFragmentByTag(CreditCardListFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new CreditCardListFragment();
        }

        //fix for random crashes
        if (fragment.getArguments() != null) {
            fragment.getArguments().putAll(bundle);
        } else {
            fragment.setArguments(bundle);
        }

        navigateToFragment(fragment, true);
    }

    @Override
    public void displayAddCreditCardFragment() {
        SettingAddCreditCardFragment settingAddCreditCardFragment = new SettingAddCreditCardFragment();
        replaceFragment(settingAddCreditCardFragment, true);
    }

    @Override
    public void displayCreditCardDetailsFragment(DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO) {
        CreditCardDetailsFragment creditCardDetailsFragment = CreditCardDetailsFragment.newInstance(creditCardsPayloadDTO);
        replaceFragment(creditCardDetailsFragment, true);
    }

    @Override
    public void captureImage(CarePayCameraCallback callback) {
        this.carePayCameraCallback = callback;

        String tag = CarePayCameraFragment.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }

        CarePayCameraFragment dialog = new CarePayCameraFragment();
        dialog.show(ft, tag);
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        if (carePayCameraCallback != null) {
            carePayCameraCallback.onCapturedSuccess(bitmap);
        }
    }

    @Override
    public void onCaptureFail() {
        if (carePayCameraCallback != null) {
            carePayCameraCallback.onCaptureFail();
        }
    }

    @Override
    public void displayHelpFragment() {
        navigateToFragment(new HelpFragment(), true);
    }

    @Override
    public void showSupportFragment() {
        navigateToFragment(new SupportFragment(), true);
    }

    @Override
    public DTO getDto() {
        return demographicsSettingsDTO;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.activity_demographics_settings, fragment, addToBackStack);
    }
}
