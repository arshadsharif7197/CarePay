package com.carecloud.carepay.patient.myhealth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.myhealth.dtos.AllergyDto;
import com.carecloud.carepay.patient.myhealth.dtos.LabDto;
import com.carecloud.carepay.patient.myhealth.dtos.MedicationDto;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthProviderDto;
import com.carecloud.carepay.patient.myhealth.dtos.PatientDto;
import com.carecloud.carepay.patient.myhealth.fragments.AllergyDetailFragment;
import com.carecloud.carepay.patient.myhealth.fragments.CareTeamDetailFragment;
import com.carecloud.carepay.patient.myhealth.fragments.MedicationDetailFragment;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthListFragment;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthMainFragment;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthInterface;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.FileDownloadUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthActivity extends MenuPatientActivity implements MyHealthInterface {

    private MyHealthDto myHealthDto;
    private static final int MY_PERMISSIONS_LAB_REQUEST_WRITE_EXTERNAL_STORAGE = 10;
    private static final int MY_PERMISSIONS_MR_REQUEST_WRITE_EXTERNAL_STORAGE = 11;
    private LabDto selectedLab;

    private MyHealthProviderDto selectedRecordProvider;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        myHealthDto = getConvertedDTO(MyHealthDto.class);
        if (myHealthDto == null) {
            callMyHealthService(icicle);
        } else {
            resumeOnCreate(icicle);
        }

    }

    private void resumeOnCreate(Bundle icicle) {
        if (icicle == null) {
            replaceFragment(MyHealthMainFragment.newInstance(), false);
        }
    }

    private void callMyHealthService(final Bundle icicle) {
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(getTransitionMyHealth(), new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                ShimmerFragment fragment = ShimmerFragment.newInstance(R.layout.shimmer_my_health_item);
                fragment.setRowsNumber(3);
                replaceFragment(fragment, false);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                myHealthDto = DtoHelper.getConvertedDTO(MyHealthDto.class, workflowDTO);
                resumeOnCreate(icicle);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        }, queryMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        selectMenuItem(R.id.myHealthMenuItem);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_my_health")));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            displayToolbar(true, null);
        }
    }

    @Override
    public DTO getDto() {
        return myHealthDto == null ? myHealthDto = getConvertedDTO(MyHealthDto.class) : myHealthDto;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.container_main, fragment, addToBackStack);
    }

    @SuppressLint("NewApi")
    @Override
    public void onSeeAllFullMedicalRecordClicked(MyHealthProviderDto provider) {
        selectedRecordProvider = provider;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PermissionChecker.PERMISSION_GRANTED && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_MR_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            prepareMedicalRecordPdf();
        }
    }

    @Override
    public void onProviderClicked(ProviderDTO provider) {
        addFragment(CareTeamDetailFragment.newInstance(provider.getId()), true);
    }

    @Override
    public void onAllergyClicked(AllergyDto allergy) {
        addFragment(AllergyDetailFragment.newInstance(allergy.getId()), true);
        MixPanelUtil.logEvent(getString(R.string.event_myHealth_viewAllergyDetail),
                getString(R.string.param_allergy_name), allergy.getName());
    }

    @Override
    public void addAllergy() {

    }

    @Override
    public void onMedicationClicked(MedicationDto medication) {
        addFragment(MedicationDetailFragment.newInstance(medication.getId()), true);
        MixPanelUtil.logEvent(getString(R.string.event_myHealth_viewMedicationDetail),
                getString(R.string.param_medication_name), medication.getDrugName());
    }

    @Override
    public void addMedication() {

    }

    @Override
    public void showListFragment(int type) {
        addFragment(MyHealthListFragment.newInstance(type), true);
    }

    @SuppressLint("NewApi")
    @Override
    public void onLabClicked(final LabDto lab) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(Label
                        .getLabel("myHealth.labs.button.dialogMessage.downloadMessage"), lab.getName(),
                DateUtil.getInstance().setDateRaw(lab.getCreatedAt()).getDateAsDayMonthDayOrdinal()))
                .setPositiveButton(Label.getLabel("my_health_confirm_download_button_label"),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                selectedLab = lab;
                                MixPanelUtil.logEvent(getString(R.string.event_myHealth_viewLabResult));
                                if (ContextCompat.checkSelfPermission(MyHealthActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PermissionChecker.PERMISSION_GRANTED
                                        && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            MY_PERMISSIONS_LAB_REQUEST_WRITE_EXTERNAL_STORAGE);
                                } else {
                                    prepareLabPdf(lab);
                                }
                            }
                        })
                .setNegativeButton(Label.getLabel("my_health_cancel"),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_LAB_REQUEST_WRITE_EXTERNAL_STORAGE
                && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            prepareLabPdf(selectedLab);
        } else if (requestCode == MY_PERMISSIONS_MR_REQUEST_WRITE_EXTERNAL_STORAGE
                && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            prepareMedicalRecordPdf();
        }
    }

    private void prepareMedicalRecordPdf() {
        TransitionDTO transitionDTO = myHealthDto.getMetadata().getLinks().getMedicalRecord();
        PatientDto patientDto = getPatientDto(selectedRecordProvider);
        if (patientDto != null) {
            String url = String.format("%s?%s=%s", transitionDTO.getUrl(), "patient_id",
                    String.valueOf(patientDto.getId()));

            FileDownloadUtil.downloadPdf(getContext(), url, patientDto.getFullName(),
                    ".pdf", Label.getLabel("my_health_care_team_records"));
            MixPanelUtil.logEvent(getString(R.string.event_myHealth_viewMedicalRecord));
        } else {
            showErrorNotification("Unable to find Patient Record");
        }
    }

    private PatientDto getPatientDto(MyHealthProviderDto providerDTO) {
        PatientDto selectedPatient = null;
        if (providerDTO == null) {
            selectedPatient = myHealthDto.getPayload().getMyHealthData()
                    .getPatient().getPatients().get(0);
        } else {
            for (PatientDto patientDto : myHealthDto.getPayload().getMyHealthData().getPatient().getPatients()) {
                if (patientDto.getBusinessEntity().getGuid().equals(providerDTO.getBusinessEntity().getGuid())) {
                    return patientDto;
                }
            }
        }
        return selectedPatient;
    }

    private void prepareLabPdf(final LabDto lab) {
        TransitionDTO getPdfTransition = myHealthDto.getMetadata().getLinks().getLabsPdf();
        String url = String.format("%s?%s=%s", getPdfTransition.getUrl(), "labs_id", lab.getId());
        FileDownloadUtil.downloadPdf(getContext(), url, lab.getName(), ".pdf", lab.getPractice());
    }

    @Override
    protected void onProfileChanged(ProfileDto profile) {
        displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_my_health")));
        callMyHealthService(null);
    }

    @Override
    protected Profile getCurrentProfile() {
        return myHealthDto.getPayload().getDelegate();
    }

    @Override
    protected String getScreenTitle(String mainTitle) {
        if (StringUtil.isNullOrEmpty(ApplicationPreferences.getInstance().getProfileId())) {
            return mainTitle;
        } else {
            if (ApplicationPreferences.getInstance().getUserLanguage().equals("en")) {
                return String.format("%s's %s", profileName, Label.getLabel("my_health_delegate_title"));
            } else {
                return String.format("%s de %s", Label.getLabel("my_health_delegate_title"), profileName);
            }
        }
    }
}
