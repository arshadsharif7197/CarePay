package com.carecloud.carepaylibrary;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDocumentsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by lsoco_user on 9/10/2016.
 * Tests for the demographics documents screen
 */
@RunWith(AndroidJUnit4.class)
public class DemograhicsDocumentsScreenTest {

    private DemographicsActivity          activity;
    private DemographicsDocumentsFragment docsParentFragment;

    @Rule
    public ActivityTestRule<DemographicsActivity> activityRule
            = new ActivityTestRule<>(DemographicsActivity.class, true, true);

    @Before
    public void setUp() {
        activity = activityRule.getActivity();
        // move to Documents page
        onView(withId(R.id.demographicsNextButton)).perform(click()).perform(click());
        docsParentFragment = (DemographicsDocumentsFragment) activity.getFragmentAt(2);
    }

    @Test
    public void togglesVisibInvisibOnClickSwitch() {
        // is Documents fragment the current fragment?
        assertThat("Displayed fragment is null", docsParentFragment, is(notNullValue()));

        // does the fragment's root view exist?
        onView(withId(R.id.demogr_docs_root)).check(matches(isDisplayed()));

        // does the switch exist?
        onView(withId(R.id.demogr_insurance_switch)).check(matches(isDisplayed()));

        // is the switch off initially?
        onView(withId(R.id.demogr_insurance_switch)).check(matches(not(isChecked())));

        // if switch on, does the insurance details container is visible?
        onView(withId(R.id.demogr_insurance_switch)).perform(click());
        onView(withId(R.id.demogr_docs_insurance_container)).check(matches(isDisplayed()));

        // if switch off, does the insurance details container is hidden?
        onView(withId(R.id.demogr_insurance_switch)).perform(click());
        onView(withId(R.id.demogr_docs_insurance_container)).check(matches(not(isDisplayed())));
    }

    @Test
    public void displaysBitmapOnClickScanLicense() {
        // ...
    }

    @Test
    public void displaysBitmapOnClickScanInsurance() {
        // ...
    }

    @Test
    public void displaysSelectedState() {
        // ...
    }

    @Test
    public void displaysSelectedPlan() {
        // ...
    }

    @Test
    public void displaysSelectedProvider() {
        // ...
    }

    @Test
    public void goesToNextTabOnClickNext() {
        // ...
    }
}