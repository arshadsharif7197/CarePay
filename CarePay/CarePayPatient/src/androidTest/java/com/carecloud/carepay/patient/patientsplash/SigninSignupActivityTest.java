//package com.carecloud.carepay.patient.patientsplash;
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.test.espresso.ViewInteraction;
//import androidx.test.rule.ActivityTestRule;
//import androidx.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.LargeTest;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//
//import com.carecloud.carepay.patient.R;
//import com.carecloud.carepay.patient.base.PatientNavigationHelper;
//import com.carecloud.carepay.patient.signinsignuppatient.SigninSignupActivity;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;
//import org.hamcrest.TypeSafeMatcher;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.allOf;
//
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class SigninSignupActivityTest extends BaseActivityTest {
//
//
//    /**
//     * login user
//     */
//    @Before
//    public void loginUser() throws Throwable {
//        user="sun@sun.com";
//        passowrd="Test123!";
//
//        getWorkflowServiceHelper().executeApplicationStartRequest(applicationStartCallback);
//
//    }
//
//    @Rule
//    public ActivityTestRule<SigninSignupActivity> activityTestRule = new ActivityTestRule<SigninSignupActivity>(SigninSignupActivity.class, true, false) {
//
//        @Override
//        protected void beforeActivityLaunched() {
//
//
//        }
//
//        @Override
//        protected  void afterActivityLaunched(){
//        }
//
//        @Override
//        protected  void afterActivityFinished(){
//        }
//
//    };
//
//
//
//    @Test
//    public void verifyButtonDisplayed() {
//
//        startActivity();
//        ViewInteraction button = onView(allOf(withId(R.id.signin_button)));
//        button.check(matches(isDisplayed()));
//
//    }
//
//
//    /**
//     * validate labels
//     */
//    @Test
//    public void validateLabels(){
//
//
//        startActivity();
//        ViewInteraction textView = onView(allOf(withId(R.id.changeLanguageText)));
//        textView.check(matches(withText("Change language")));
//
//
//    }
//
//
//
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
//    /**
//     * start activity
//     */
//    public void startActivity() {
//        delay(10000);
//        Bundle bundle = new Bundle();
//        Intent intent = new Intent();
//        bundle.putString(PatientNavigationHelper.class.getSimpleName(), workFlowDtoString);
//        intent.putExtra(PatientNavigationHelper.class.getSimpleName(), bundle);
//        activityTestRule.launchActivity(intent);
//    }
//
//
//}
