//package com.carecloud.carepay.patient.patientsplash;
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.test.espresso.NoMatchingViewException;
//import androidx.test.espresso.ViewAssertion;
//import androidx.test.espresso.ViewInteraction;
//import androidx.test.espresso.assertion.ViewAssertions;
//import androidx.test.rule.ActivityTestRule;
//import androidx.test.runner.AndroidJUnit4;
//import androidx.recyclerview.widget.RecyclerView;
//import android.test.suitebuilder.annotation.LargeTest;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//
//import com.carecloud.carepay.patient.R;
//import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
//import com.carecloud.carepay.patient.base.PatientNavigationHelper;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;
//import org.hamcrest.TypeSafeMatcher;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
//import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withParent;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.allOf;
//import static org.hamcrest.Matchers.greaterThan;
//
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class AppointmentsActivityTest extends BaseActivityTest {

//    private String TAG = AppointmentsActivityTest.class.getSimpleName();
//
//    /**
//     * login user
//     */
//    @Before
//    public void loginUser() throws Throwable {
//        user = "sun@sun.com";
//        passowrd = "Test123!";
//
//        getWorkflowServiceHelper().executeApplicationStartRequest(applicationStartCallback);
//    }
//
//
//    @Rule
//    public ActivityTestRule<AppointmentsActivity> activityTestRule = new ActivityTestRule<AppointmentsActivity>(AppointmentsActivity.class, true, false) {
//
//
//        @Override
//        protected void beforeActivityLaunched() {
//        /*
//            BaseActivity baseActivity = (BaseActivity)getActivity();
//            workflowServiceHelper = baseActivity.getWorkflowServiceHelper();
//            appAuthorizationHelper = baseActivity.getAppAuthorizationHelper();
//            applicationMode = baseActivity.getApplicationMode();
//            try {
//                AppointmentsActivityTest.this.loginUser();
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//
//        */
//
//        }
//
//        @Override
//        protected void afterActivityLaunched() {
//        }
//
//        @Override
//        protected void afterActivityFinished() {
//        }
//
//
//    };
//
//
//
//    /**
//     * exist the element
//     */
//    @Test
//    @Ignore
//    public void checkExistElement() {
//
//       startActivity();
//
//        ViewInteraction imageButton = onView(
//                allOf(withId(R.id.fab),
//                        childAtPosition(
//                                allOf(withId(R.id.appointmentsList),
//                                        childAtPosition(
//                                                withId(R.id.container_main),
//                                                0)),
//                                1),
//                        isDisplayed()));
//
//        imageButton.check(ViewAssertions.matches(isDisplayed()));
//    }
//
//
//
//    /**
//     * has more than zero appointments
//     */
//    @Test
//    public void verifyAppoitments() throws Throwable {
//
//       startActivity();
//        onView(allOf(withId(R.id.appointments_recycler_view))).check(new RecyclerViewItemCountAssertion(0));
//
//    }
//
//
//    /**
//     * has not defined elements
//     */
//    @Test
//    @Ignore
//    public void hasNotDefined() {
//
//
//        onView(withParent(withId(R.id.appointmentsList))).check(ViewAssertions.matches(withText("CANCEL")));
//
//
//    }
//
//
//    /**
//     * has the screen the button
//     */
//    @Test
//    @Ignore
//    public void hasButton() {
//
//        startActivity();
//        ViewInteraction recyclerView = onView(
//                allOf(withId(R.id.appointments_recycler_view),
//                        withParent(withId(R.id.appointment_section_linear_layout)),
//                        isDisplayed()));
//        recyclerView.perform(actionOnItemAtPosition(4, click()));
//
//        ViewInteraction button = onView(
//                allOf(withId(R.id.cancelAppointmentButton), isDisplayed()));
//
//        button.check(matches(isDisplayed()));
//
//    }
//
//    /**
//     * start activity
//     */
//    public void startActivity() {
//        delay(10000);
//        Bundle bundle = new Bundle();
//        Intent intent = new Intent();
//        bundle.putString(PatientNavigationHelper.class.getSimpleName(), workFlowDtoStringAppointments);
//        intent.putExtra(PatientNavigationHelper.class.getSimpleName(), bundle);
//        activityTestRule.launchActivity(intent);
//    }
//
//    public class RecyclerViewItemCountAssertion implements ViewAssertion {
//        private final int expectedCount;
//
//        public RecyclerViewItemCountAssertion(int expectedCount) {
//            this.expectedCount = expectedCount;
//        }
//
//        @Override
//        public void check(View view, NoMatchingViewException noViewFoundException) {
//            if (noViewFoundException != null) {
//                throw noViewFoundException;
//            }
//
//            RecyclerView recyclerView = (RecyclerView) view;
//            RecyclerView.Adapter adapter = recyclerView.getAdapter();
//
//            assertThat(adapter.getItemCount(), greaterThan((expectedCount)));
//        }
//    }
//
//
//    /**
//     * verify and return matcher for of a element
//     */
//    private static Matcher<View> childAtPosition(
//            final Matcher<View> parentMatcher, final int position) {
//
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Child at position " + position + " in parent ");
//                parentMatcher.describeTo(description);
//            }
//
//            @Override
//            public boolean matchesSafely(View view) {
//                ViewParent parent = view.getParent();
//                return parent instanceof ViewGroup && parentMatcher.matches(parent)
//                        && view.equals(((ViewGroup) parent).getChildAt(position));
//            }
//        };
//    }
//
//
//
//}