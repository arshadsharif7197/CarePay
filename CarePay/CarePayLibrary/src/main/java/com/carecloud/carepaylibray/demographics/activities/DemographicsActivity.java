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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsAddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDetailsFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDocumentsFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsMoreDetailsFragment;
import com.carecloud.carepaylibray.demographics.models.DemAddressPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemInsurancePayloadPojo;
import com.carecloud.carepaylibray.demographics.models.DemPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemPersDetailsPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemIdDocPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadResponseModel;
import com.carecloud.carepaylibray.keyboard.Constants;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jahirul Bhuiyan on 8/31/2016.
 * Main activity for Demographics sign-up sub-flow
 */
public class DemographicsActivity extends KeyboardHolderActivity {
    
    private TextView    titleTextView;
    private int         currentPageIndex;
    private ViewPager   viewPager;
    private ProgressBar demographicProgressBar;
    private ImageView   tabImageView;
    
    private DemographicModel modelGet = null;
    private DemAddressPayloadDto     addressModel;
    private DemPersDetailsPayloadDto detailsModel;
    private DemIdDocPayloadDto       demPayloadIdDocPojo;
    private List<DemInsurancePayloadPojo> insuranceModelList = new ArrayList<>();
    
    public DemPayloadDto getDemographicInfoPayloadModel() {
        DemPayloadDto infoModel = null;
        if (modelGet != null) {
            DemographicPayloadResponseModel response = modelGet.getPayload();
            if (response != null) {
                DemographicPayloadInfoModel infoModelPayload = response.getDemographics();
                if (infoModelPayload != null) {
                    infoModel = infoModelPayload.getPayload();
                }
            }
        }
        return infoModel;
    }
    
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
        titleTextView.setText("Address");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(DemographicsActivity.this, R.drawable.icn_patient_mode_nav_back));
        (DemographicsActivity.this).setSupportActionBar(toolbar);
        
        // set the language
        Intent intent = getIntent();
        if (intent.hasExtra(KeyboardHolderActivity.KEY_LANG_ID)) {
            setLangId(intent.getIntExtra(KeyboardHolderActivity.KEY_LANG_ID, Constants.LANG_EN));
        } else if (intent.hasExtra("demographics_model")) {
            String demographicsModelString = intent.getStringExtra("demographics_model");
            Gson gson = new Gson();
            modelGet = gson.fromJson(demographicsModelString, DemographicModel.class);
        }
        // set the progress bar
        demographicProgressBar = (ProgressBar) findViewById(R.id.demographicProgressBar);
        demographicProgressBar.setVisibility(View.GONE);

        tabImageView = (ImageView) findViewById(R.id.demographicsOnboardingTab);

        // b/e
        isStoragePermissionGranted();
        setupPager();
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
    }

    private void setScreenHeader(int position) {
        switch (position) {
            case 0:
                tabImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icn_signup_step_1));
                titleTextView.setText("Address");
                break;
            case 1:
                tabImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icn_signup_step_2));
                titleTextView.setText("Details");
                break;
            case 2:
                tabImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icn_signup_step_3));
                titleTextView.setText("Documents");
                break;
            case 3:
                tabImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icn_signup_step_4));
                titleTextView.setText("More Details");
                break;
            default:
                break;
        }
    }
    
    public void setCurrentItem(int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }
    
    public DemographicModel getModel() {
        return modelGet;
    }
    
    public DemPersDetailsPayloadDto getDetailsModel() {
        return detailsModel;
    }
    
    public void setAddressModel(DemAddressPayloadDto addressModel) {
        this.addressModel = addressModel;
    }
    
    public DemAddressPayloadDto getAddressModel() {
        return addressModel;
    }
    
    public void setDetailsModel(DemPersDetailsPayloadDto detailsModel) {
        this.detailsModel = detailsModel;
    }
    
    public DemIdDocPayloadDto getDemPayloadIdDocPojo() {
        return demPayloadIdDocPojo;
    }
    
    public void setDemPayloadIdDocPojo(DemIdDocPayloadDto demPayloadIdDocPojo) {
        this.demPayloadIdDocPojo = demPayloadIdDocPojo;
    }
    
    public void setModel(DemographicModel modelGet) {
        this.modelGet = modelGet;
    }
    
    public List<DemInsurancePayloadPojo> getInsuranceModelList() {
        return insuranceModelList;
    }
    
    public void setInsuranceModelList(List<DemInsurancePayloadPojo> insuranceModelList) {
        this.insuranceModelList = insuranceModelList;
    }
    
    /**
     * Adapter for the viewpager
     */
    public static class DemographicPagerAdapter extends FragmentPagerAdapter {
        final         int   PAGE_COUNT = 4;

        /**
         * Constructor of the class
         */
        public DemographicPagerAdapter(FragmentManager fm) {
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
            if (currentPageIndex == 0) {
                SystemUtil.hideSoftKeyboard(this);
                onBackPressed();
            } else {
                setCurrentItem(currentPageIndex - 1, true);
            }
        }
        return true;
    }
    
}
