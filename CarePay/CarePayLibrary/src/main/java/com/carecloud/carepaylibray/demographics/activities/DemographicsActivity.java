package com.carecloud.carepaylibray.demographics.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsAddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDetailsFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDocumentsFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsMoreDetailsFragment;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadAddressModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadDriversLicenseModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoPayloadModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadPersonalDetailsModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadResponseModel;
import com.carecloud.carepaylibray.demographics.services.DemographicService;
import com.carecloud.carepaylibray.keyboard.Constants;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Jahirul Bhuiyan on 8/31/2016.
 * Main activity for Demographics sign-up sub-flow
 */
public class DemographicsActivity extends KeyboardHolderActivity {

    private TextView    titleTextView;
    private int         currentPageIndex;
    private ViewPager   viewPager;
    private ProgressBar demographicProgressBar;

    private DemographicModel demographicModel = null;

    public DemographicPayloadInfoPayloadModel getDemographicInfoPayloadModel() {
        if(demographicModel != null) {
            DemographicPayloadResponseModel response = demographicModel.getPayload();
            if(response != null) {
                DemographicPayloadInfoModel infoModel = response.getDemographics();
                if(infoModel != null) {
                    return infoModel.getPayload();
                }
            }
        }
        return null;
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
        } else if(intent.hasExtra("demographics_model")) {
            demographicModel = intent.getParcelableExtra("demographics_model");
        }
        // set the progress bar
        demographicProgressBar = (ProgressBar) findViewById(R.id.demographicProgressBar);
        demographicProgressBar.setVisibility(View.GONE);

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
                setScreenTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        viewPager.addOnPageChangeListener(pageChangeListener);

        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setOnPageChangeListener(pageChangeListener);
        indicator.setViewPager(viewPager);
    }

    public void confirmDemographicInformation() {
        DemographicPayloadAddressModel demographicPayloadAddressModel = new DemographicPayloadAddressModel();
        demographicPayloadAddressModel.setAddress1("5200 Blue legun dr");
        demographicPayloadAddressModel.setAddress1("#800");
        demographicPayloadAddressModel.setCity("Miami");
        demographicPayloadAddressModel.setState("FL");
        demographicPayloadAddressModel.setZipcode("33127");
        demographicPayloadAddressModel.setPhone("18007654222");

        DemographicPayloadPersonalDetailsModel demographicPayloadPersonalDetailsModel = new DemographicPayloadPersonalDetailsModel();
        demographicPayloadPersonalDetailsModel.setFirstName("Jahirul");
        demographicPayloadPersonalDetailsModel.setMiddleName("I");
        demographicPayloadPersonalDetailsModel.setLastName("Bhuiyan");
        demographicPayloadPersonalDetailsModel.setDateOfBirth("02/11/1983");
        demographicPayloadPersonalDetailsModel.setPrimaryRace("Asian");
        demographicPayloadPersonalDetailsModel.setEthnicity("White");
        demographicPayloadPersonalDetailsModel.setPreferredLanguage("English");

        DemographicPayloadDriversLicenseModel demographicPayloadDriversLicenseModel = new DemographicPayloadDriversLicenseModel();

        List<String> updates = new ArrayList<String>();
        DemographicPayloadModel demographicPayloadModel = new DemographicPayloadModel();
        demographicPayloadModel.setAddress(demographicPayloadAddressModel);
        demographicPayloadModel.setPersonalDetails(demographicPayloadPersonalDetailsModel);
        demographicPayloadModel.setDriversLicense(demographicPayloadDriversLicenseModel);
        demographicPayloadModel.setUpdates(updates);

        /*DemographicModel demographicPostModel = new DemographicModel();
        demographicPostModel.setPayload(demographicPayloadModel);*/

        demographicProgressBar.setVisibility(View.VISIBLE);
        DemographicService apptService = (new BaseServiceGenerator(this)).createService(DemographicService.class); //, String token, String searchString
        Call<DemographicModel> call = apptService.confirmDemographicInformation(demographicPayloadModel);
        call.enqueue(new Callback<DemographicModel>() {
            @Override
            public void onResponse(Call<DemographicModel> call, Response<DemographicModel> response) {
                demographicModel = response.body();
                demographicProgressBar.setVisibility(View.GONE);
                Log.d(LOG_TAG, "demogr post succeeded");

                Intent appointmentIntent = new Intent(DemographicsActivity.this, AppointmentsActivity.class);
                startActivity(appointmentIntent);
                finish();
            }

            @Override
            public void onFailure(Call<DemographicModel> call, Throwable t) {
                Log.d(LOG_TAG, "demogr post failed", t);
                demographicProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setScreenTitle(int position) {
        switch (position) {
            case 0:
                titleTextView.setText("Address");
                break;
            case 1:
                titleTextView.setText("Details");
                break;
            case 2:
                titleTextView.setText("Documents");
                break;
            case 3:
                titleTextView.setText("More Details");
                break;
            default:
                break;
        }
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }

    /**
     * Adapter for the viewpager
     */

    class DemographicPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

        final         int   PAGE_COUNT = 4;
        private final int[] ICONS      = new int[]{
                R.drawable.signup_step1_indicator,
                R.drawable.signup_step2_indicator,
                R.drawable.signup_step3_indicator,
                R.drawable.signup_step4_indicator
        };

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


        @Override
        public int getIconResId(int index) {
            return ICONS[index];
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