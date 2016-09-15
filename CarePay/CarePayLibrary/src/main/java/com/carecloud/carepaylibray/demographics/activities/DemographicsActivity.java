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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsAddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDetailsFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDocumentsFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsMoreDetailsFragment;
import com.carecloud.carepaylibray.keyboard.Constants;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import static com.carecloud.carepaylibray.utils.Utility.setTypefaceFromAssets;


/**
 * Created by Jahirul Bhuiyan on 8/31/2016.
 */
public class DemographicsActivity extends KeyboardHolderActivity {


    TextView title;


    private ViewPager viewPager;
    private FunPagerAdapter funPagerAdapter;

    @Override
    public void replaceFragment(Class fragClass, boolean addToBackStack) {

    }

    @Override
    public void placeInitContentFragment() {

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
        title = (TextView) toolbar.findViewById(R.id.demographics_toolbar_title);
        setTypefaceFromAssets(DemographicsActivity.this, "fonts/gotham_rounded_medium.otf", title);
        toolbar.setTitle("");
        title.setText("Address");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(DemographicsActivity.this, R.drawable.icn_patient_mode_nav_back));

        (DemographicsActivity.this).setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backButtonClick();
            }
        });

        // set the language
        Intent intent = getIntent();
        if (intent.hasExtra(KeyboardHolderActivity.KEY_LANG_ID)) {
            setLangId(intent.getIntExtra(KeyboardHolderActivity.KEY_LANG_ID, Constants.LANG_EN));
        }

        isStoragePermissionGranted();

        funPagerAdapter = new FunPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(funPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setScreenTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
    }

    private void setScreenTitle(int position) {
        switch (position) {
            case 0:
                title.setText("Address");
                break;
            case 1:
                title.setText("Details");
                break;
            case 2:
                title.setText("Documents");
                break;
            case 3:
                title.setText("More Details");
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
    class FunPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

        final int PAGE_COUNT = 4;
        private final int[] ICONS = new int[]{
                R.drawable.signup_step1_indicator,
                R.drawable.signup_step2_indicator,
                R.drawable.signup_step3_indicator,
                R.drawable.signup_step4_indicator
        };

        /**
         * Constructor of the class
         */
        public FunPagerAdapter(FragmentManager fm) {
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
        return ((FunPagerAdapter) viewPager.getAdapter()).getItem(pos);
    }

    private void backButtonClick() {
        Toast.makeText(DemographicsActivity.this, "backButtonClick", Toast.LENGTH_SHORT).show();
    }

}