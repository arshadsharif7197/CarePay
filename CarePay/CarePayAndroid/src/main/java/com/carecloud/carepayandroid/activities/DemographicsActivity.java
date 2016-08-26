package com.carecloud.carepayandroid.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.fragments.ScanDocumentFragment;
import com.carecloud.carepaylibray.fragments.demographics.AddressFragment;
import com.carecloud.carepaylibray.fragments.demographics.DetailsFragment;
import com.carecloud.carepaylibray.fragments.demographics.DocumentsFragment;
import com.carecloud.carepaylibray.fragments.demographics.MoreDetailsFragment;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class DemographicsActivity extends AppCompatActivity {

    private ViewPager       viewPager;
    private FunPagerAdapter funPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographics
        );
        funPagerAdapter = new FunPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(funPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setScreenTitle(position);
            }

            @Override
            public void onPageSelected(int position) {

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
                setTitle("Address");
                break;
            case 1:
                setTitle("Details");
                break;
            case 2:
                setTitle("Documents");
                break;
            case 3:
                setTitle("More Details");
                break;
            default:
                break;
        }
    }


    public void setCurrentItem(int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }

    /**
     * Adapter
     */
    class FunPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

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
                    AddressFragment addressFragment = AddressFragment.newInstance(null);
                    return addressFragment;
                case 1:
                    DetailsFragment detailsFragment = DetailsFragment.newInstance(null);
                    return detailsFragment;
                case 2:
//                    DocumentsFragment documentsFragment = DocumentsFragment.newInstance(null);
                    ScanDocumentFragment documentsFragment = new ScanDocumentFragment();
                    return documentsFragment;
                case 3:
                    MoreDetailsFragment moreDetailsFragment = MoreDetailsFragment.newInstance(null);
                    return moreDetailsFragment;
                default:
//        	  MyFragment myFragment = new MyFragment();
//      		Bundle data = new Bundle();
//      		data.putInt("current_page", position+1);
//      		myFragment.setArguments(data);
//      		return myFragment;
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

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
//                case 0:
//                    return "Fun Poll/Quiz";
//                case 1:
//                    return "Rate your Day";
//                case 2:
//                    return "Suggetions";

                default:
                    return "";
            }
        }
    }
}