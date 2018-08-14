package com.carecloud.carepay.patient.consentforms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentFormPracticeFormsFragment;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentFormProvidersListFragment;
import com.carecloud.carepay.patient.consentforms.fragments.FilledFormFragment;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormActivityInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.consentforms.models.payload.FormDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
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
            for (int i = 0; i < consentFormsDTO.getPayload().getForms().size(); i++) {
                if (practiceId.equals(consentFormsDTO.getPayload().getForms().get(i).getMetadata().getPracticeId())) {
                    addFragment(ConsentFormPracticeFormsFragment.newInstance(i), true);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_forms);
        menuItem.setChecked(true);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            displayToolbar(true, menuItem.getTitle().toString());
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_forms);
            displayToolbar(true, menuItem.getTitle().toString());
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
    public void onProviderSelected(FormDTO practiceForm, int position) {
        addFragment(ConsentFormPracticeFormsFragment.newInstance(position), true);
    }

    @Override
    public void showForms(List<PracticeForm> selectedForms, int selectedProviderIndex, boolean showSignButton) {
        this.selectedForms = selectedForms;
        addFragment(FilledFormFragment
                .newInstance(selectedProviderIndex, showSignButton), true);
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
}
