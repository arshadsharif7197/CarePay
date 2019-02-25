package com.carecloud.carepay.patient.consentforms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentFormProvidersListFragment;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentFormViewPagerFragment;
import com.carecloud.carepay.patient.consentforms.fragments.FilledFormFragment;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormActivityInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.UserFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsentFormsActivity extends MenuPatientActivity implements ConsentFormActivityInterface {

    private ConsentFormDTO consentFormsDTO;
    private List<PracticeForm> selectedForms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consentFormsDTO = getConvertedDTO(ConsentFormDTO.class);
        if (consentFormsDTO == null) {
            callConsentFormsService(savedInstanceState);
        } else {
            resumeOnCreate(savedInstanceState);
        }
    }

    private void callConsentFormsService(final Bundle savedInstanceState) {
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(getTransitionForms(), new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                replaceFragment(ShimmerFragment.newInstance(R.layout.shimmer_default_item), false);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                consentFormsDTO = DtoHelper.getConvertedDTO(ConsentFormDTO.class, workflowDTO);
                resumeOnCreate(savedInstanceState);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        }, queryMap);
    }

    private void resumeOnCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            replaceFragment(ConsentFormProvidersListFragment.newInstance(), false);
        }
        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        if (extra != null && !extra.isEmpty()) {
            String practiceId = extra.getString("practiceId");
            for (int i = 0; i < consentFormsDTO.getPayload().getUserForms().size(); i++) {
                if (practiceId.equals(consentFormsDTO.getPayload().getUserForms().get(i).getMetadata().getPracticeId())) {
                    addFragment(ConsentFormViewPagerFragment.newInstance(i), true);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectMenuItem(R.id.formsMenuItem);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            displayToolbar(true, getScreenTitle(Label.getLabel("adhoc_show_forms_button_label")));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            displayToolbar(true, getScreenTitle(Label.getLabel("adhoc_show_forms_button_label")));
        }
    }

    @Override
    public DTO getDto() {
        return consentFormsDTO == null ? consentFormsDTO = getConvertedDTO(ConsentFormDTO.class) : consentFormsDTO;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void onProviderSelected(UserFormDTO practiceForm, int position) {
        addFragment(ConsentFormViewPagerFragment.newInstance(position), true);
    }

    @Override
    public void showForms(List<PracticeForm> selectedForms,
                          List<ConsentFormUserResponseDTO> responses,
                          int selectedProviderIndex, boolean showSignButton) {
        this.selectedForms = selectedForms;
        addFragment(FilledFormFragment
                .newInstance(selectedProviderIndex, showSignButton, responses), true);
    }

    @Override
    public List<PracticeForm> getAllFormsToShow() {
        return selectedForms;
    }

    @Override
    public void showAllDone(WorkflowDTO workflowDTO) {
        TransitionDTO transition = consentFormsDTO.getMetadata().getLinks().getSelf();
        getWorkflowServiceHelper().execute(transition, updateFormCallBack);
        updateBadgeCounters();
    }

    WorkflowServiceCallback updateFormCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            finish();
            PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    protected void onProfileChanged(ProfileDto profile) {
        displayToolbar(true, getScreenTitle(Label.getLabel("adhoc_show_forms_button_label")));
        callConsentFormsService(null);
    }

    @Override
    protected Profile getCurrentProfile() {
        return consentFormsDTO.getPayload().getDelegate();
    }
}
