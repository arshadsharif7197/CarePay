package com.carecloud.carepaylibrary;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepaylibray.ApplicationWorkFlow;
import com.carecloud.carepaylibray.fragments.ResponsibilityFragment;
import com.carecloud.carepaylibray.models.ScreenComponentModel;

import java.util.ArrayList;

/**
 * Test the renderer for ResponsibilityScreen
 */
public class ResponsibilityScreenRendererTest extends ApplicationTestCase<Application> {
    public ResponsibilityScreenRendererTest() {
        super(Application.class);
    }

    public void testCountOfViewsMatchesCountOfComponents() {
        ResponsibilityFragment.ResponsibilityLayoutRenderer renderer
                = new ResponsibilityFragment.ResponsibilityLayoutRenderer(getContext());
        renderer.createLayout();
        ArrayList<View> views = renderer.getViews();
        ArrayList<ScreenComponentModel> componentModels = ApplicationWorkFlow.Instance()
                .getResponsabScreenModel().getComponentModels();
        int count = renderer.getCount();

        assertEquals(count, views.size());
        assertEquals(count, componentModels.size());
    }

    public void testLabelsInViewsMatchLabelsOfComponents() {
        ResponsibilityFragment.ResponsibilityLayoutRenderer renderer
                = new ResponsibilityFragment.ResponsibilityLayoutRenderer(getContext());
        renderer.createLayout();
        ArrayList<ScreenComponentModel> componentModels = ApplicationWorkFlow.Instance()
                .getResponsabScreenModel().getComponentModels();
        ArrayList<View> views = renderer.getViews();
        for(int i = 0; i < views.size(); i++) {
            View view = views.get(i);
            if(view instanceof TextView) {
                assertEquals(((TextView)view).getText(), componentModels.get(i).getLabel());
            }
        }
    }

    public void testCostIsSumOfDetailedBalances() {
        ResponsibilityFragment.ResponsibilityLayoutRenderer renderer
                = new ResponsibilityFragment.ResponsibilityLayoutRenderer(getContext());
        renderer.createLayout();
        ArrayList<View> views = renderer.getViews();
        TextView costView = (TextView) views.get(2);
        TextView balanceView = (TextView) views.get(4);
        TextView copayView = (TextView) views.get(6);
        double cost = Double.parseDouble((costView.getText().toString().substring(1)));
        double balance = Double.parseDouble((balanceView.getText().toString().substring(1)));
        double copay = Double.parseDouble((copayView.getText().toString().substring(1)));
        assertEquals(cost, balance + copay);
    }

}