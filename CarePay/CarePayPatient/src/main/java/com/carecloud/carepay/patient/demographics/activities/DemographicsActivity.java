package com.carecloud.carepay.patient.demographics.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.demographics.fragments.viewpager.DemographicsAddressFragment;
import com.carecloud.carepay.patient.demographics.fragments.viewpager.DemographicsAllSetFragment;
import com.carecloud.carepay.patient.demographics.fragments.viewpager.DemographicsDetailsFragment;
import com.carecloud.carepay.patient.demographics.fragments.viewpager.DemographicsDocumentsFragmentWthWrapper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.customcomponents.CustomViewPager;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.DemographicMetadataDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemIdDocDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckinDemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.demographics.scanner.IdDocScannerFragment;
import com.carecloud.carepaylibray.demographics.scanner.ProfilePictureFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 8/31/2016.
 * Main activity for Demographics sign-up sub-flow
 */
public class DemographicsActivity extends BasePatientActivity
        implements DemographicsLabelsHolder,
        DemographicsDocumentsFragmentWthWrapper.DemographicsDocumentsFragmentWthWrapperListener,
        DemographicsDetailsFragment.DemographicsDetailsFragmentListener,
        DemographicsAddressFragment.DemographicsAddressFragmentListener,
        CheckinDemographicsFragment.CheckinDemographicsFragmentListener,
        HealthInsuranceFragment.InsuranceDocumentScannerListener,
        CheckInDemographicsBaseFragment.CheckInNavListener,
        CarePayCameraReady {

    private int currentPageIndex;
    // views
    private TextView titleTextView;
    private CustomViewPager viewPager;
    private ImageView tabImageView;
    // jsons (payload)
    private DemographicDTO modelGet = null;
    private DemographicAddressPayloadDTO addressModel;
    private PatientModel detailsModel;
    private DemographicIdDocPayloadDTO idDocModel;
    private List<DemographicInsurancePayloadDTO> insuranceModelList = new ArrayList<>();
    // jsons (metadata)
    private DemographicMetadataEntityAddressDTO addressEntityMetaDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private DemographicMetadataEntityIdDocsDTO idDocsMetaDTO;
    private DemographicLabelsDTO labelsDTO;
    private Toolbar toolbar;
    private String[] fragLabels;

    /**
     * Updating with info model
     *
     * @return updated model
     */
    public DemographicPayloadDTO getDemographicInfoPayloadModel() {
        DemographicPayloadDTO infoModel = null;
        if (modelGet != null && modelGet.getPayload() != null) {
            DemographicPayloadInfoDTO infoModelPayload = modelGet.getPayload().getDemographics();
            if (infoModelPayload != null) {
                infoModel = infoModelPayload.getPayload();
            }
        }
        return infoModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographics);
        modelGet = getConvertedDTO(DemographicDTO.class);
        labelsDTO = modelGet.getMetadata().getLabels();

        // init DTOs
        initDTOsForFragments();

        // init frag labels
        fragLabels = new String[4];
        fragLabels[0] = Label.getLabel("demographics_address_section");
        fragLabels[1] = Label.getLabel("demographics_details_section");
        fragLabels[2] = Label.getLabel("demographics_documents_section");
        fragLabels[3] = Label.getLabel("demographics_allset_section");

        toolbar = (Toolbar) findViewById(R.id.demographics_toolbar);
        titleTextView = (TextView) toolbar.findViewById(R.id.demographics_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(this, titleTextView);
        toolbar.setTitle("");
        titleTextView.setText(fragLabels[0]);
        (DemographicsActivity.this).setSupportActionBar(toolbar);

        // set the progress bar
        ProgressBar demographicProgressBar = (ProgressBar) findViewById(R.id.demographicProgressBar);
        demographicProgressBar.setVisibility(View.GONE);

        tabImageView = (ImageView) findViewById(R.id.demographicsOnboardingTab);

        isStoragePermissionGranted();
        setupPager();

    }


    /**
     * Enable or disable scroll on view pager
     */
    @Override
    public void enableScroll(boolean isScrollEnable) {
        viewPager.setEnablePaging(isScrollEnable);
    }

    private void setupPager() {
        currentPageIndex = 0;
        DemographicPagerAdapter demographicPagerAdapter = new DemographicPagerAdapter(getSupportFragmentManager());
        viewPager = (CustomViewPager) findViewById(R.id.demographicsViewPager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(demographicPagerAdapter);
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    // hide the keyboard (just in case)
                    SystemUtil.hideSoftKeyboard(DemographicsActivity.this);
                }
                currentPageIndex = position;
                setScreenHeader(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        viewPager.addOnPageChangeListener(pageChangeListener);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SystemUtil.hideSoftKeyboard(DemographicsActivity.this);
            }
        }, 800);
    }

    private void setScreenHeader(int position) {
        switch (position) {
            case 0:
                tabImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icn_signup_step_1));
                titleTextView.setText(fragLabels[0]);
                break;
            case 1:
                tabImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icn_signup_step_2));
                titleTextView.setText(fragLabels[1]);
                break;
            case 2:
                tabImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icn_signup_step_3));
                titleTextView.setText(fragLabels[2]);
                break;
            case 3:
                tabImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icn_signup_step_4));
                titleTextView.setText(fragLabels[3]);
                break;
            default:
                break;
        }
    }

    /**
     * Set the current screen in the view pages
     *
     * @param item         Index of the current item
     * @param smoothScroll Whether smooth scroll
     */
    public void setCurrentItem(int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
        if (item > 0) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(DemographicsActivity.this, R.drawable.icn_nav_back));
        } else {
            toolbar.setNavigationIcon(null);
        }
    }

    public DemographicDTO getModel() {
        return modelGet;
    }

    public void setModel(DemographicDTO modelGet) {
        this.modelGet = modelGet;
    }

    public PatientModel getDetailsDTO() {
        return detailsModel;
    }

    public DemographicAddressPayloadDTO getAddressModel() {
        return addressModel;
    }

    public void setAddressModel(DemographicAddressPayloadDTO addressModel) {
        this.addressModel = addressModel;
    }

    public void setDetailsModel(PatientModel detailsModel) {
        this.detailsModel = detailsModel;
    }

    public DemographicIdDocPayloadDTO getIdDocModel() {
        return idDocModel;
    }

    public void setIdDocModel(DemographicIdDocPayloadDTO idDocModel) {
        this.idDocModel = idDocModel;
    }

    public List<DemographicInsurancePayloadDTO> getInsuranceModelList() {
        return insuranceModelList;
    }

    /**
     * checking storage permission
     *
     * @return true if granted
     */

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //   Log.v(TAG, "Permission is granted");
                return true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (currentPageIndex == 0) {
            SystemUtil.hideSoftKeyboard(this);

            if (!HttpConstants.isUseUnifiedAuth()) {
                // sign-out from Cognito
                getAppAuthorizationHelper().getPool().getUser().signOut();
                getAppAuthorizationHelper().setUser(null);
            }

            // finish the app
            DemographicsActivity.this.finishAffinity();
        } else {
            setCurrentItem(currentPageIndex - 1, true);
        }
    }

    private void initDTOsForFragments() {
        // init payload DTOs
        DemographicPayloadDTO infoModel = getDemographicInfoPayloadModel();
        if (infoModel != null) {
            // init payload DTOs
            addressModel = infoModel.getAddress();
            detailsModel = infoModel.getPersonalDetails();
            idDocModel = infoModel.getIdDocument();
            insuranceModelList = infoModel.getInsurances();
        } else {
            addressModel = new DemographicAddressPayloadDTO();
            detailsModel = new PatientModel();
            idDocModel = new DemographicIdDocPayloadDTO();
            insuranceModelList = new ArrayList<>();
        }
        // init metadata DTOs
        DemographicMetadataDTO metadataDTO = modelGet.getMetadata();
        labelsDTO = metadataDTO.getLabels();
        addressEntityMetaDTO = metadataDTO.getDataModels().getDemographic().getAddress();
        persDetailsMetaDTO = metadataDTO.getDataModels().getDemographic().getPersonalDetails();
        idDocsMetaDTO = metadataDTO.getDataModels().getDemographic().getIdentityDocuments();
    }

    @Override
    public DemographicLabelsDTO getLabelsDTO() {
        return labelsDTO;
    }

    @Override
    public void onDemographicDtoChanged(DemographicDTO demographicDTO) {

    }

    @Override
    public void initializeDocumentFragment() {

    }

    @Override
    public void initializeInsurancesFragment() {
        String tag = HealthInsuranceFragment.class.getSimpleName();
        HealthInsuranceFragment fragment = new HealthInsuranceFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, modelGet);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.demographicsHealtInsurance, fragment, tag);
        transaction.commit();
    }

    private void hideShowComponents(boolean isParent) {
        findViewById(R.id.demographics_toolbar).setVisibility(isParent ? View.VISIBLE : View.GONE);
        findViewById(R.id.demogr_content_holder).setVisibility(isParent ? View.VISIBLE : View.GONE);
        findViewById(R.id.insurance_item_holder).setVisibility(isParent ? View.GONE : View.VISIBLE);
    }

    @Override
    public void editInsurance(DemographicDTO demographicDTO, Integer editedIndex, boolean showAsDialog) {

    }

    @Override
    public void navigateToParentFragment() {
        hideShowComponents(true);
        //setCurrentItem(2, true);
        /*CheckinDemographicsFragment fragment = new CheckinDemographicsFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, modelGet);
        fragment.setArguments(args);

        navigateToFragment(fragment, false);
        Log.v(NewReviewDemographicsActivity.class.getSimpleName(), "NewReviewDemographicsActivity");*/
    }

    @Override
    public void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model) {
        if (index >= 0) {
            insuranceModelList.set(index, model);
        } else {
            insuranceModelList.add(model);
        }
        if (modelGet.getPayload().getDemographics() == null) {
            modelGet.getPayload().setDemographics(new DemographicPayloadInfoDTO());
            modelGet.getPayload().getDemographics().setPayload(new DemographicPayloadDTO());
            modelGet.getPayload().getDemographics().getPayload().setInsurances(insuranceModelList);
        }

        initializeInsurancesFragment();
    }

    /**
     * Adapter for the viewpager
     */
    public class DemographicPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 4;

        /**
         * Constructor of the class
         */
        DemographicPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * This method will be invoked when a page is requested to create
         */
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    DemographicsAddressFragment addressFragment = new DemographicsAddressFragment();
                    addressFragment.setPersDetailsMetaDTO(persDetailsMetaDTO);

                    Bundle args = new Bundle();
                    DtoHelper.bundleDto(args, addressEntityMetaDTO);
                    addressFragment.setArguments(args);

                    return addressFragment;
                case 1:
                    DemographicsDetailsFragment demographicsDetailsFragment = new DemographicsDetailsFragment();
                    demographicsDetailsFragment.setPersDetailsMetaDTO(persDetailsMetaDTO);
                    return demographicsDetailsFragment;
                case 2:
                    DemographicsDocumentsFragmentWthWrapper demographicsDocumentsFragment = new DemographicsDocumentsFragmentWthWrapper();
                    demographicsDocumentsFragment.setIdDocsMetaDTO(idDocsMetaDTO);
                    return demographicsDocumentsFragment;
                case 3:
                    return new DemographicsAllSetFragment();
                default:
                    return null;
            }
        }

        /**
         * Returns the number of pages
         */
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

    @Override
    public void initializeIdDocScannerFragment(DemographicIdDocPayloadDTO demPayloadIdDocDTO,
                                               DemographicMetadataEntityItemIdDocDTO demographicMetadataEntityItemIdDocDTO) {
        String tag = "license";

        FragmentManager fm = getSupportFragmentManager();
        // add license fragment
        IdDocScannerFragment fragment = (IdDocScannerFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new IdDocScannerFragment();
            Bundle args = new Bundle();
            DtoHelper.bundleDto(args, demPayloadIdDocDTO);

            if (null != demographicMetadataEntityItemIdDocDTO) {
                DtoHelper.bundleDto(args, demographicMetadataEntityItemIdDocDTO);
            }

            fragment.setArguments(args);
        }

        fm.beginTransaction().replace(R.id.demographicsDocsLicense, fragment, tag).commit();
    }

    @Override
    public void initializeProfilePictureFragment(DemographicLabelsDTO globalLabelDTO,
                                                 PatientModel persDetailsDTO) {

        FragmentManager fm = getSupportFragmentManager();
        String tag = ProfilePictureFragment.class.getSimpleName();
        ProfilePictureFragment fragment = (ProfilePictureFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new ProfilePictureFragment();

            Bundle args = new Bundle();
            DtoHelper.bundleDto(args, persDetailsDTO);
            fragment.setArguments(args);
        }
        fm.beginTransaction()
                .replace(R.id.demographicsAddressPicCapturer, fragment, tag)
                .commit();
    }

    @Override
    public void captureImage(CarePayCameraCallback callback) {

    }

    @Override
    public void applyChangesAndNavTo(DemographicDTO demographicDTO, Integer step) {

    }

    @Override
    public Integer getCurrentStep() {
        return null;
    }

    @Override
    public void setCurrentStep(Integer step) {

    }

    @Override
    public void setCheckinFlow(CheckinFlowState flowState, int totalPages, int currentPage) {

    }

    @Override
    public void navigateToConsentFlow(WorkflowDTO workflowDTO) {

    }
}
