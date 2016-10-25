package com.carecloud.carepaylibray.demographics.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsAddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDetailsFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDocumentsFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsMoreDetailsFragment;
import com.carecloud.carepaylibray.demographics.models.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.keyboard.Constants;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jahirul Bhuiyan on 8/31/2016.
 * Main activity for Demographics sign-up sub-flow
 */
public class DemographicsActivity extends KeyboardHolderActivity {

    private TextView titleTextView;
    private int currentPageIndex;
    private ViewPager viewPager;
    private ImageView tabImageView;

    private DemographicDTO modelGet = null;
    private DemographicAddressPayloadDTO addressModel;
    private DemographicPersDetailsPayloadDTO detailsModel;
    private DemographicIdDocPayloadDTO idDocModel;
    private List<DemographicInsurancePayloadDTO> insuranceModelList = new ArrayList<>();

    public DemographicPayloadDTO getDemographicInfoPayloadModel() {
        DemographicPayloadDTO infoModel = null;
        if (modelGet != null) {
            DemographicPayloadResponseDTO response = modelGet.getPayload();
            if (response != null) {
                DemographicPayloadInfoModel infoModelPayload = response.getDemographics();
                if (infoModelPayload != null) {
                    infoModel = infoModelPayload.getPayload();
                }
            }
        }
        return infoModel;
    }

    private final String[] fragLabels = {"Address", "Details", "Documents", "All Set"}; // these will come from meta-data

    @Override
    public int getLayoutRes() {
        return R.layout.activity_demographics;
    }

    @Override
    public int getContentsHolderId() {
        return R.id.demogr_content_holder;
    }

    @Override
    public int getKeyboardHolderId() {
        return R.id.demogr_keyboard_holder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.demographics_toolbar);
        titleTextView = (TextView) toolbar.findViewById(R.id.demographics_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(this, titleTextView);
        toolbar.setTitle("");
        titleTextView.setText(fragLabels[0]);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(DemographicsActivity.this, R.drawable.icn_patient_mode_nav_back));
        (DemographicsActivity.this).setSupportActionBar(toolbar);

        // set the language
        Intent intent = getIntent();
        if (intent.hasExtra(KeyboardHolderActivity.KEY_LANG_ID)) {
            setLangId(intent.getIntExtra(KeyboardHolderActivity.KEY_LANG_ID, Constants.LANG_EN));
        } else if (intent.hasExtra("demographics_model")) {
            String demographicsModelString = intent.getStringExtra("demographics_model");
            Gson gson = new Gson();
            modelGet = gson.fromJson(demographicsModelString, DemographicDTO.class);
        }

        // set the progress bar
        ProgressBar demographicProgressBar = (ProgressBar) findViewById(R.id.demographicProgressBar);
        demographicProgressBar.setVisibility(View.GONE);

        tabImageView = (ImageView) findViewById(R.id.demographicsOnboardingTab);

        isStoragePermissionGranted();
        setupPager();

        initDTOsForFragments();

//        createDTOsForTest();
    }

    private void setupPager() {
        currentPageIndex = 0;
        DemographicPagerAdapter demographicPagerAdapter = new DemographicPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.demographicsViewPager);
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

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
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

    public void setCurrentItem(int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }

    public DemographicDTO getModel() {
        return modelGet;
    }

    public DemographicPersDetailsPayloadDTO getDetailsModel() {
        return detailsModel;
    }

    public void setAddressModel(DemographicAddressPayloadDTO addressModel) {
        this.addressModel = addressModel;
    }

    public DemographicAddressPayloadDTO getAddressModel() {
        return addressModel;
    }

    public void setDetailsModel(DemographicPersDetailsPayloadDTO detailsModel) {
        this.detailsModel = detailsModel;
    }

    public DemographicIdDocPayloadDTO getIdDocModel() {
        return idDocModel;
    }

    public void setIdDocModel(DemographicIdDocPayloadDTO idDocModel) {
        this.idDocModel = idDocModel;
    }

    public void setModel(DemographicDTO modelGet) {
        this.modelGet = modelGet;
    }

    public List<DemographicInsurancePayloadDTO> getInsuranceModelList() {
        return insuranceModelList;
    }

    public void setInsuranceModelList(List<DemographicInsurancePayloadDTO> insuranceModelList) {
        this.insuranceModelList = insuranceModelList;
    }

    /**
     * Adapter for the viewpager
     */
    public static class DemographicPagerAdapter extends FragmentPagerAdapter {

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
                    return new DemographicsAddressFragment();
                case 1:
                    return new DemographicsDetailsFragment();
                case 2:
                    return new DemographicsDocumentsFragment();
                case 3:
                    return new DemographicsMoreDetailsFragment();
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

    /**
     * Returns the fragment in the view pager at a certain index. Used in tests
     *
     * @param pos The index
     * @return The fragments
     */
    public Fragment getFragmentAt(int pos) {
        return ((DemographicPagerAdapter) viewPager.getAdapter()).getItem(pos);
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
            // re-launch SigninSignupActivity
            Intent intent = new Intent(this, SigninSignupActivity.class);
            startActivity(intent);
        } else {
            setCurrentItem(currentPageIndex - 1, true);
        }
    }

    private void initDTOsForFragments() {
        DemographicPayloadDTO infoModel = getDemographicInfoPayloadModel();
        if (infoModel != null) {
            addressModel = infoModel.getAddress();
            detailsModel = infoModel.getPersonalDetails();
            List<DemographicIdDocPayloadDTO> idDocDTOs = infoModel.getIdDocuments();
            if(idDocDTOs != null && idDocDTOs.size() > 0) {
                idDocModel = infoModel.getIdDocuments().get(0);
            }
            insuranceModelList = infoModel.getInsurances();
        } else {
            addressModel = new DemographicAddressPayloadDTO();
            detailsModel = new DemographicPersDetailsPayloadDTO();
            idDocModel = new DemographicIdDocPayloadDTO();
            insuranceModelList = new ArrayList<>();
        }
    }
}
