package com.carecloud.carepaylibray.androidTest.actions

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers

import org.hamcrest.Matcher

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.PerformException
import androidx.test.espresso.util.TreeIterables
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import java.util.concurrent.TimeoutException
import android.widget.TextView
import androidx.core.view.children
import org.hamcrest.Matchers.*


/**
 * Created by drodriguez on 08/12/19.
 */
var stringHolder = mutableListOf<String>()

open class CustomViewActions {
    /**
     * Default click action based on content description of the view
     * @param contentDescription Content description of the view
     * @param customClick If view is not visible more than 90% use custom click
     */
    protected fun click(contentDescription: String, customClick: Boolean = false) {
        onView(allOf(withContentDescription(contentDescription), isDisplayed()))
                .perform(if (customClick) customClickAction() else ViewActions.click())
    }

    /**
     * Default click action on specific text
     * @param text Text of the view
     */
    protected fun clickOnSpecificText(text: String) {
        onView(withText(text)).perform(ViewActions.click())
    }

    /**
     * Verifies a view is visible
     * @param contentDescription Content description of the view
     */
    protected fun verifyViewVisible(contentDescription: String) {
        onView(withContentDescription(contentDescription)).check(matches(notNullValue())).check(matches(isDisplayed()))
    }

    /**
     * Verifies a view is not visible
     * @param contentDescription Content description of the view
     */
    protected fun verifyViewNotVisible(contentDescription: String) {
        onView(withContentDescription(contentDescription)).check(matches(notNullValue())).check(matches(not(isDisplayed())))
    }

    /**
     * Click on an item in a recycler view list based on position
     * @param contentDescription Content description of the view
     * @param position Position of element on list
     */
    protected fun clickOnRecyclerViewItem(contentDescription: String, position: Int) {
        getTextFromRecyclerViewItem(contentDescription, position)
        onView(withContentDescription(contentDescription)).
                perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, ViewActions.click()))
    }

    /**
     * Click on an item in a recycler view list based on text
     * @param contentDescription Content description of the view
     * @param textMatch Text to match with element on list
     */
    protected fun clickOnRecyclerViewItem(contentDescription: String, textMatch: String) {
        onView(withContentDescription(contentDescription)).perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                        hasDescendant(withText(textMatch)), ViewActions.click()))
    }

    /**
     * Verify if item is visible in Recycler view
     * @param contentDescription Content description of the view
     * @param textMatch Text to match with element on list
     */
    protected fun verifyItemInRecyclerView(contentDescription: String, textMatch: String) {
        onView(withContentDescription(contentDescription)).check(matches(hasDescendant(withText(textMatch))))
    }

    /**
     * Type into text input
     * @param contentDescription Content description of the view
     * @param stringToType String to type into text input
     * @param closeKeyboard Boolean to close keyboard after type, in case next step is hidden by keyboard view
     */
    protected fun type(contentDescription: String, stringToType: String, closeKeyboard: Boolean = false) {
        if (closeKeyboard) {
            onView(withContentDescription(contentDescription)).perform(typeText(stringToType), closeSoftKeyboard())
        } else {
            onView(withContentDescription(contentDescription)).perform(typeText(stringToType))
        }
    }

    /**
     * Get text from recycler view item using either position or matching text
     * @param contentDescription Content description of the view
     * @param position Index position of the item on the list
     * @param textMatch Text used to match item on the list
     */
    private fun getTextFromRecyclerViewItem(contentDescription: String, position: Int = -1, textMatch: String = "") {
        if (position >= 0 && textMatch.isEmpty()) {
            onView(withContentDescription(contentDescription)).
                    perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, getText()))
        } else if (textMatch.isNotEmpty()) {
            onView(withContentDescription(contentDescription)).perform(
                    RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                            hasDescendant(withText(textMatch)), getText()))
        }
    }

    /**
     * Custom click ViewAction implementation
     * @return ViewAction
     */
    private fun customClickAction(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isEnabled()
            }

            override fun getDescription(): String {
                return "Click on view not fully visible"
            }

            override fun perform(uiController: UiController, view: View) {
                view.performClick()
            }
        }
    }

    /**
     * Wait for a specific view
     * @param contentDescription The content description of the view to wait for.
     * @param milliseconds The timeout of until when to wait for.
     */
    protected fun wait(contentDescription: String, milliseconds: Long){
        onView(isRoot()).perform(waitId(contentDescription, milliseconds))
    }


    /**
     * Perform action of waiting for a specific view content description.
     * @param contentDescription The content description of the view to wait for.
     * @param millis The timeout of until when to wait for.
     */
    fun waitId(contentDescription: String, millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for a specific view with content description <$contentDescription> during $millis millis."
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + millis
                val viewMatcher = withContentDescription(contentDescription)

                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)

                // timeout happens
                throw PerformException.Builder()
                        .withActionDescription(this.description)
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(TimeoutException())
                        .build()
            }
        }
    }

    /**
     * Loops thru all children views and extracts the text from each TextView
     * Good for verifying recycler view items
     */
    private fun getText(): ViewAction {
        stringHolder = mutableListOf()
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun getDescription(): String {
                return "getting text from a TextView"
            }

            override fun perform(uiController: UiController, view: View) {
                (view as ViewGroup).children.forEach { view -> if(view is TextView) stringHolder.add(view.text.toString()) }
            }
        }

    }
}
