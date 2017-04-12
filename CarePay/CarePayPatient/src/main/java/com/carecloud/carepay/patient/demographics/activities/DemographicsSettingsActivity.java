package com.carecloud.carepay.patient.demographics.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.demographics.fragments.settings.DemographicsSettingsFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.HelpFragment;
import com.carecloud.carepay.patient.demographics.fragments.settings.SupportFragment;
import com.carecloud.carepay.patient.payment.fragments.SettingAddCreditCardFragment;
import com.carecloud.carepay.patient.payment.fragments.SettingsCreditCardDetailsFragment;
import com.carecloud.carepay.patient.payment.fragments.SettingsCreditCardListFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.List;

/**
 * Main activity for Settings workflow
 */
public class DemographicsSettingsActivity extends BasePatientActivity implements
        SettingsCreditCardDetailsFragment.IOnCreditCardOperationSuccess,
        SettingsCreditCardListFragment.ISettingsCreditCardListFragmentListener,
        DemographicsSettingsFragment.IDemographicsSettingsFragmentListener,
        HelpFragment.HelpFragmentListener, CarePayCameraReady, CarePayCameraCallback {

    DemographicsSettingsDTO demographicsSettingsDTO;
    private CarePayCameraCallback carePayCameraCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographics_settings);

        demographicsSettingsDTO = getConvertedDTO(DemographicsSettingsDTO.class);

        getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants.DEMOGRAPHICS_ADDRESS_BUNDLE,
                demographicsSettingsDTO.getPayload().getDemographics().getPayload().getAddress());

        FragmentManager fm = getSupportFragmentManager();
        DemographicsSettingsFragment fragment = (DemographicsSettingsFragment)
                fm.findFragmentByTag(DemographicsSettingsFragment.class.getSimpleName());

        Bundle bundle = new Bundle();
        if (fragment == null) {
            Gson gson = new Gson();
            String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
            bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);
            fragment = DemographicsSettingsFragment.newInstance(demographicsSettingsDTO);
        }
        if (fragment.getArguments() != null) {
            fragment.getArguments().putAll(bundle);
        } else {
            fragment.setArguments(bundle);
        }

        Fragment demographicsSettingsFragment = fm
                .findFragmentByTag(DemographicsSettingsFragment.class.getSimpleName());
        if (!(demographicsSettingsFragment != null
                && demographicsSettingsFragment instanceof DemographicsSettingsFragment)) {
            fm.beginTransaction().add(R.id.activity_demographics_settings, fragment,
                    DemographicsSettingsFragment.class.getSimpleName()).commit();
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
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            for (Fragment fragment : fragmentList) {
                if (fragment.getClass().getName().equalsIgnoreCase(SettingsCreditCardListFragment.class.getName())) {
                    ((SettingsCreditCardListFragment) fragment).loadCreditCardsList(demographicsSettingsDTO);
                } else if (fragment.getClass().getName().equalsIgnoreCase(DemographicsSettingsFragment.class.getName())) {
                    this.demographicsSettingsDTO = demographicsSettingsDTO;
                    ((DemographicsSettingsFragment) fragment).updateCreditCardsList(demographicsSettingsDTO);
                }
            }
        }
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
    public void initializeAddNewCreditCardFragment() {
        FragmentManager fragmentmanager = getSupportFragmentManager();
        SettingAddCreditCardFragment fragment = (SettingAddCreditCardFragment)
                fragmentmanager.findFragmentByTag(SettingAddCreditCardFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new SettingAddCreditCardFragment();
        }

        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, gson.toJson(demographicsSettingsDTO));
        bundle.putString(CarePayConstants.PAYEEZY_MERCHANT_SERVICE_BUNDLE, gson.toJson(demographicsSettingsDTO.getPayload().getPapiAccounts()));
        fragment.setArguments(bundle);
        navigateToFragment(fragment, true);
    }

    @Override
    public void initializeCreditCardListFragment() {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
        bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);

        FragmentManager fm = getSupportFragmentManager();
        SettingsCreditCardListFragment fragment = (SettingsCreditCardListFragment)
                fm.findFragmentByTag(SettingsCreditCardListFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new SettingsCreditCardListFragment();
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
    public void showHelpFragment() {
        navigateToFragment(new HelpFragment(), true);
    }

    @Override
    public void showSupportFragment() {
        navigateToFragment(new SupportFragment(), true);
    }
}
