package com.carecloud.carepay.patient.myhealth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
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
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

import java.util.List;

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

        List<PracticePatientIdsDTO> practicePatientIds = myHealthDto.getPayload().getPracticePatientIds();
        if (!practicePatientIds.isEmpty()) {
            getApplicationPreferences().writeObjectToSharedPreference(
                    CarePayConstants.KEY_PRACTICE_PATIENT_IDS, practicePatientIds);
        }
        setTransitionBalance(myHealthDto.getMetadata().getLinks().getPatientBalances());
        setTransitionLogout(myHealthDto.getMetadata().getTransitions().getLogout());
        setTransitionProfile(myHealthDto.getMetadata().getLinks().getProfileUpdate());
        setTransitionAppointments(myHealthDto.getMetadata().getLinks().getAppointments());
        setTransitionNotifications(myHealthDto.getMetadata().getLinks().getNotifications());
        setTransitionMyHealth(myHealthDto.getMetadata().getLinks().getMyHealth());
        setTransitionRetail(myHealthDto.getMetadata().getLinks().getRetail());

        getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants
                .DEMOGRAPHICS_ADDRESS_BUNDLE, myHealthDto.getPayload().getDemographicDTO().getAddress());

        String userImageUrl = myHealthDto.getPayload().getDemographicDTO()
                .getPersonalDetails().getProfilePhoto();
        if (userImageUrl != null) {
            getApplicationPreferences().setUserPhotoUrl(userImageUrl);
        }
        if (icicle == null) {
            replaceFragment(MyHealthMainFragment.newInstance(), false);
        }
        String userId = myHealthDto.getPayload().getPracticePatientIds().get(0).getUserId();
        MixPanelUtil.setUser(userId);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = navigationView.getMenu().findItem(com.carecloud.carepaylibrary.R.id.nav_my_health);
        menuItem.setChecked(true);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            displayToolbar(true, menuItem.getTitle().toString());
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
    }

    @Override
    public void addAllergy() {

    }

    @Override
    public void onMedicationClicked(MedicationDto medication) {
        addFragment(MedicationDetailFragment.newInstance(medication.getId()), true);
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
    public void onLabClicked(LabDto lab) {
        selectedLab = lab;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PermissionChecker.PERMISSION_GRANTED && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_LAB_REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            prepareLabPdf(lab);
        }
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

            downloadPdf(url, patientDto.getFullName(), ".pdf", "Medical Record");
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
        downloadPdf(url, lab.getName(), ".pdf", lab.getPractice());
    }

    private void downloadPdf(String url, String title,
                             String fileExtension, String description) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title + fileExtension);
        request.setDescription(description);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setMimeType("application/pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.addRequestHeader("Accept", "application/pdf");
        request.addRequestHeader("username", getAppAuthorizationHelper().getCurrUser());
        request.addRequestHeader("Authorization", getAppAuthorizationHelper().getIdToken());
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + fileExtension);

        DownloadManager downloadManager = (DownloadManager) getContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
}
