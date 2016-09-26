package com.carecloud.carepaylibrary;

import android.content.Context;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibray.activities.LibraryMainActivity;
import com.carecloud.carepaylibray.payment.PaymentActivity;
import com.carecloud.carepaylibray.payment.ResponsibilityFragment;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payment.ResponsibilityFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test the renderer for ResponsibilityScreen
 */
@RunWith(AndroidJUnit4.class)
public class ResponsibilityScreenTest {

    @Rule
    public ActivityTestRule<PaymentActivity> activity = new ActivityTestRule<>(PaymentActivity.class,
                                                                               true,
                                                                               false);

    @Before
    public void setup() {
        activity.launchActivity(new Intent());
    }

    @Test
    public void hasAllViews() {
        onView(withId(R.id.respons_toolbar)).check(matches(isDescendantOfA(withId(R.id.respons_root))));
        onView(withId(R.id.respons_total_label)).check(matches(isDescendantOfA(withId(R.id.respons_root))));
        onView(withId(R.id.respons_total)).check(matches(isDescendantOfA(withId(R.id.respons_root))));
        onView(withId(R.id.respons_prev_balance_label)).check(matches(isDescendantOfA(withId(R.id.respons_root))));
        onView(withId(R.id.respons_prev_balance)).check(matches(isDescendantOfA(withId(R.id.respons_root))));
        onView(withId(R.id.respons_copay_label)).check(matches(isDescendantOfA(withId(R.id.respons_root))));
        onView(withId(R.id.respons_copay)).check(matches(isDescendantOfA(withId(R.id.respons_root))));
        onView(withId(R.id.respons_pay)).check(matches(isDescendantOfA(withId(R.id.respons_root))));
    }

    @Test
    public void hasAllLabels() {
        String titleLabel = activity.getActivity().getString(R.string.respons_title);
        onView(withId(R.id.respons_toolbar_title)).check(matches(withText(titleLabel)));

        String totalLabel = activity.getActivity().getString(R.string.respons_total_label);
        onView(withId(R.id.respons_total_label)).check(matches(withText(totalLabel)));

        String prevLabel = activity.getActivity().getString(R.string.respons_previous_balance_label);
        onView(withId(R.id.respons_prev_balance_label)).check(matches(withText(prevLabel)));

        String copayLabel = activity.getActivity().getString(R.string.respons_copay_label);
        onView(withId(R.id.respons_copay_label)).check(matches(withText(copayLabel)));
    }

    @Test
    public void totalIsSumOfPrevAndCopay() {

        PaymentActivity theActivity = activity.getActivity();

        TextView totalView = (TextView) theActivity.findViewById(R.id.respons_total);
        TextView balanceView = (TextView) theActivity.findViewById(R.id.respons_prev_balance);
        TextView copayView = (TextView) theActivity.findViewById(R.id.respons_copay);

        // test sum
        double cost = Double.parseDouble((totalView.getText().toString().substring(1)));
        double balance = Double.parseDouble((balanceView.getText().toString().substring(1)));
        double copay = Double.parseDouble((copayView.getText().toString().substring(1)));
        assertThat(cost, is(balance + copay));
    }

    @After
    public void closeTest() {
        // nothing for now
    }
}