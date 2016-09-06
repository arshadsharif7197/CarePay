package com.carecloud.carepaylibrary;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.carecloud.carepaylibray.activities.DemographicsActivity;
import com.carecloud.carepaylibray.activities.MainActivityLibrary;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Test the renderer for ResponsibilityScreen
 */
@RunWith(AndroidJUnit4.class)
public class ResponsibilityScreenRendererTest {

    @Rule
    public ActivityTestRule<DemographicsActivity> activity = new ActivityTestRule<>(DemographicsActivity.class,
                                                                                    true,
                                                                                    true);

    @Before
    public void doSmthBeforeTest() {
        // place the fragment
    }

    @Test
    public void sample() {
        assertThat(true, is(true));
    }

    @After
    public void doSmthAfterTest() {
        // nothing for now
    }
//
//    public void testCountOfViewsMatchesCountOfComponents() { // todo set up testing environ
//        ResponsibilityFragment.ResponsibilityLayoutRenderer renderer
//                = new ResponsibilityFragment.ResponsibilityLayoutRenderer(getContext());
//        renderer.createLayout();
//        ArrayList<View> views = renderer.getViews();
//        ArrayList<ScreenComponentModel> componentModels = ApplicationWorkflow.Instance()
//                .getResponsabScreenModel().getComponentModels();
//        int count = renderer.getCount();
//
//        assertEquals(count, views.size());
//        assertEquals(count, componentModels.size());
//    }
//
//    public void testLabelsInViewsMatchLabelsOfComponents() {
//        ResponsibilityFragment.ResponsibilityLayoutRenderer renderer
//                = new ResponsibilityFragment.ResponsibilityLayoutRenderer((AppCompatActivity) getContext());
//        renderer.createLayout();
//        ArrayList<ScreenComponentModel> componentModels = ApplicationWorkflow.Instance()
//                .getResponsabScreenModel().getComponentModels();
//        ArrayList<View> views = renderer.getViews();
//        for(int i = 0; i < views.size(); i++) {
//            View view = views.get(i);
//            if(view instanceof TextView) {
//                assertEquals(((TextView)view).getText(), componentModels.get(i).getLabel());
//            }
//        }
//    }
//
//    public void testCostIsSumOfDetailedBalances() {
//        ResponsibilityFragment.ResponsibilityLayoutRenderer renderer
//                = new ResponsibilityFragment.ResponsibilityLayoutRenderer((AppCompatActivity) getContext());
//        renderer.createLayout();
//        ArrayList<View> views = renderer.getViews();
//        TextView costView = (TextView) views.get(2);
//        TextView balanceView = (TextView) views.get(4);
//        TextView copayView = (TextView) views.get(6);
//        double cost = Double.parseDouble((costView.getText().toString().substring(1)));
//        double balance = Double.parseDouble((balanceView.getText().toString().substring(1)));
//        double copay = Double.parseDouble((copayView.getText().toString().substring(1)));
//        assertEquals(cost, balance + copay);
//    }
}