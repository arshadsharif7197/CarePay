package com.carecloud.carepay.patient.tutorial.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import com.carecloud.carepay.patient.tutorial.TutorialFragment;


public class TutorialAdapter extends FragmentPagerAdapter {

    private List<TutorialFragment> fragments;

    /**
     * @param fm Fragment Manager
     * @param fragments to be displayed
     */
    public TutorialAdapter(FragmentManager fm, List<TutorialFragment> fragments) {
        super(fm);
        this.fragments = fragments;

    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }


    @Override
    public int getCount() {
        return this.fragments.size();
    }

}
