package com.carecloud.carepay.practice.library.patientmodecheckin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.models.PatientModeCheckinDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.fragments.AddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.FormsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.IdentificationFragment;
import com.carecloud.carepaylibray.demographics.fragments.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.fragments.IntakeFormsFragment;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.medications.fragments.AllergiesFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergiesEmptyFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsFragment;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;

import java.util.List;

/**
 * Created by lmenendez on 4/25/17.
 */

public class PatientModeDemographicsPresenter extends DemographicsPresenterImpl {
    public static final String KEY_HANDLE_HOME = "handle_home_button";

    private boolean shouldHandleHomeButton = false;
    private PatientModeCheckinDTO patientModeCheckinDTO;
    private ISession session;
    private DemographicsView demographicsView;

    /**
     * Default Constructor
     *
     * @param demographicsView   demographics View
     * @param savedInstanceState bundle
     * @param session            application session
     */
    public PatientModeDemographicsPresenter(DemographicsView demographicsView, Bundle savedInstanceState, ISession session) {
        super(demographicsView, savedInstanceState, true);
        this.session = session;
        this.demographicsView = demographicsView;

        if (savedInstanceState != null) {
            shouldHandleHomeButton = savedInstanceState.getBoolean(KEY_HANDLE_HOME, false);
        }

        String username = null;
        if (session.getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
            //need to switch to PatientMode
            session.getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
            shouldHandleHomeButton = true;
        } else {
            username = session.getAppAuthorizationHelper().getPatientUser();
        }

        patientModeCheckinDTO = demographicsView.getConvertedDTO(PatientModeCheckinDTO.class);
        if (patientModeCheckinDTO != null) {
            if (patientModeCheckinDTO.getPayload().getCheckinModeDTO().getMetadata().getUsername() != null) {
                username = patientModeCheckinDTO.getPayload().getCheckinModeDTO().getMetadata().getUsername();
                session.getAppAuthorizationHelper().setUser(username);

                MixPanelUtil.setUser(demographicsView.getContext(), patientModeCheckinDTO.getPayload().getCheckinModeDTO().getMetadata().getUserId(), null);
            }
        }

        if (username == null) {
            //bail out of this
            demographicsView.showErrorNotification("Error creating patient log-in");
            exitToPatientHome();
        }

        initWorkflow();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        icicle.putBoolean(KEY_HANDLE_HOME, shouldHandleHomeButton);
    }


    /**
     * Callback if Presenter will handle the home button
     *
     * @return true if Presenter will handle home button
     */
    public boolean handleHomeButtonClick() {
        if (shouldHandleHomeButton) {
            showHomeAlertDialog();
            return true;
        }
        return false;
    }

    private void showHomeAlertDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(null, null);
        confirmDialogFragment.setNegativeAction(true);
        confirmDialogFragment.setCallback(new ConfirmationCallback() {
            @Override
            public void onConfirm() {
                logCheckinCancelled();
                exitToPatientHome();
            }
        });
        String tag = confirmDialogFragment.getClass().getName();
        confirmDialogFragment.show(ft, tag);
    }

    @Override
    protected boolean shouldPreventBackNav() {
        return getCurrentStep() == 1;
    }

    private void exitToPatientHome() {
        if (patientModeCheckinDTO != null) {
            TransitionDTO transitionDTO = patientModeCheckinDTO.getMetaData().getTransitions().getPatientHome();
            session.getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
                @Override
                public void onPreExecute() {
                    session.showProgressDialog();
                }

                @Override
                public void onPostExecute(WorkflowDTO workflowDTO) {
                    session.hideProgressDialog();
                    demographicsView.navigateToWorkflow(workflowDTO);
                }

                @Override
                public void onFailure(String exceptionMessage) {
                    session.hideProgressDialog();
                    session.showErrorNotification(exceptionMessage);
                }
            });
        }
    }

    public List<OptionDTO> getLanguages() {
        return demographicDTO.getPayload().getLanguages();
    }

    public TransitionDTO getLanguageLink() {
        return demographicDTO.getMetadata().getLinks().getLanguage();
    }

    public void changeLanguage(WorkflowDTO workflowDTO) {
        DemographicDTO demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, workflowDTO);
        this.demographicDTO.setMetadata(demographicDTO.getMetadata());
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root_layout);
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.detach(fragment);
            transaction.attach(fragment);
            transaction.commit();
            if (fragment instanceof CheckInDemographicsBaseFragment) {
                ((CheckInDemographicsBaseFragment) fragment).afterLanguageChanged(demographicDTO);
            }
        }
    }

    public void logCheckinCancelled() {
        Fragment currentFragment = getCurrentFragment();
        String currentStep = null;
        if (currentFragment instanceof PersonalInfoFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_personal_info);
        } else if (currentFragment instanceof AddressFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_address);
        } else if (currentFragment instanceof DemographicsFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_demographics);
        } else if (currentFragment instanceof IdentificationFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_identity);
        } else if (currentFragment instanceof HealthInsuranceFragment ||
                currentFragment instanceof InsuranceEditDialog) {
            currentStep = demographicsView.getContext().getString(R.string.step_health_insurance);
        } else if (currentFragment instanceof FormsFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_consent_forms);
        } else if (currentFragment instanceof MedicationsAllergyFragment ||
                currentFragment instanceof MedicationsFragment ||
                (currentFragment instanceof MedicationsAllergiesEmptyFragment &&
                        ((MedicationsAllergiesEmptyFragment) currentFragment).getSelectedMode() ==
                                MedicationsAllergiesEmptyFragment.MEDICATION_MODE)) {
            currentStep = demographicsView.getContext().getString(R.string.step_medications);
        } else if (currentFragment instanceof AllergiesFragment ||
                (currentFragment instanceof MedicationsAllergiesEmptyFragment &&
                        ((MedicationsAllergiesEmptyFragment) currentFragment).getSelectedMode() ==
                                MedicationsAllergiesEmptyFragment.ALLERGY_MODE)) {
            currentStep = demographicsView.getContext().getString(R.string.step_allegies);
        } else if (currentFragment instanceof IntakeFormsFragment) {
            currentStep = demographicsView.getContext().getString(R.string.step_intake);
        }
        if (currentStep != null) {
            boolean isGuest = !ValidationHelper.isValidEmail(((ISession) demographicsView.getContext()).getAppAuthorizationHelper().getCurrUser());
            String[] params = {
                    demographicsView.getContext().getString(R.string.param_last_completed_step),
                    demographicsView.getContext().getString(R.string.param_is_guest)
            };
            Object[] values ={
                    currentStep,
                    isGuest,
            };
            MixPanelUtil.logEvent(demographicsView.getContext().getString(R.string.event_checkin_cancelled),
            params, values);
        }

    }

}
