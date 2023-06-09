package com.carecloud.test_module.actions

import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import androidx.test.espresso.web.model.Atoms
import androidx.test.espresso.web.sugar.Web.onWebView
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import java.util.concurrent.TimeoutException


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
                .perform(if (customClick) customClickAction() else click())
    }

    /**
     * Scroll to view and click action based on content description of the view
     * @param contentDescription Content description of the view
     */
    protected fun scrollToAndClick(contentDescription: String) {
        onView(withContentDescription(contentDescription)).perform(scroll(), click())
    }

    /**
     * Default click action on specific text
     * @param text Text of the view
     */
    protected fun clickOnSpecificText(text: String) {
        onView(withText(text)).perform(click())
    }

    /**
     * Default click action on specific id
     * @param id id of the view
     */
    protected fun clickOnSpecificId(id: Int) {
        onView(withId(id)).perform(click())
    }

    /**
     * Verifies a view is visible
     * @param contentDescription Content description of the view
     */
    protected fun verifyViewVisible(contentDescription: String) {
        onView(withContentDescription(contentDescription)).check(matches(notNullValue())).check(matches(isDisplayed()))
    }

    /**
     * Verifies a view is visible
     * @param id Id of the view
     */
    protected fun verifyViewVisible(id: Int) {
        onView(withId(id)).check(matches(notNullValue())).check(matches(isDisplayed()))
    }

    /**
     * Verifies a specific text is shown on a view
     * @param contentDescription Content description of the view
     * @param textMatch Text to match
     */
    protected fun verifyTextOnView(contentDescription: String, textMatch: String) {
        onView(withContentDescription(contentDescription)).check(matches(withText(containsString(textMatch))))
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
        onView(allOf(withContentDescription(contentDescription), isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
    }

    /**
     * Click on an item in a recycler view list based on text
     * @param contentDescription Content description of the view
     * @param textMatch Text to match with element on list
     */
    protected fun clickOnRecyclerViewItem(contentDescription: String, textMatch: String) {
        onView(withContentDescription(contentDescription)).perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                        hasDescendant(withText(textMatch)), click()))
    }

    /**
     * Click on an item in a recycler view list based on text
     * @param itemContentDescription Content description of the view
     * @param textMatch Text to match with element on list
     */
    protected fun clickOnRecyclerViewItem(itemContentDescription: String,
                                          recyclerViewContentDescription: String,
                                          textMatch: String) {
        onData(withContentDescription(itemContentDescription))
                .inAdapterView(withContentDescription("recyclerViewContentDescription"))
                .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                        hasDescendant(withText(textMatch)), click()))
    }

    /**
     * Click on an item in a recycler view list based on text
     * @param contentDescription Content description of the view
     * @param textMatch Text to match with element on list
     */
    protected fun clickOnRecyclerViewItemChildren(contentDescription: String, position: Int, textMatch: String) {
        onView(withContentDescription(contentDescription)).perform(RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(position, clickWithinRecyclerViewItem(textMatch)))
    }

    /**
     * Click on an item in a recycler view list based on id
     * @param contentDescription Content description of the recycler view
     * @param position Position of the item
     * @param id ID to match with element on list
     */
    protected fun clickOnRecyclerViewItemChildrenWithId(contentDescription: String, position: Int, id: Int) {
        onView(withContentDescription(contentDescription)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, clickChildViewWithId(id)))
    }

    /**
     * Click on an item in a recycler view list based on id
     * @param contentDescription Content description of the recycler view
     * @param itemContentDescription Content description of the item
     * @param id ID to match with element on list
     */
    protected fun clickOnSpecificRecyclerViewItemChildrenWithId(contentDescription: String,
                                                                itemContentDescription: String,
                                                                id: Int) {
        onView(withContentDescription(contentDescription)).perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                        hasDescendant(withContentDescription(itemContentDescription)),
//                        withContentDescription(itemContentDescription),
                        clickChildViewWithId(id)))
    }

    /**
     * Click on an item in a recycler view list based on position
     * @param id id of the view
     * @param position Position of element on list
     */
    protected fun clickOnRecyclerViewItem(id: Int, position: Int) {
        onView(withId(id)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
    }

    /**
     * Verify if item is visible in Recycler view
     * @param contentDescription Content description of the view
     * @param textMatch Text to match with element on list
     */
    protected fun verifyItemInRecyclerView(contentDescription: String, textMatch: String, exactMatch: Boolean = true) {
        if (exactMatch) {
            onView(withContentDescription(contentDescription)).check(matches(hasDescendant(withText(textMatch))))
        } else {
            onView(withContentDescription(contentDescription)).check(matches(hasDescendant(withText(containsString(textMatch)))))
        }
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

    protected fun scrollWebView() {
        onWebView().perform(Atoms.script("window.scrollTo(0, document.body.scrollHeight)"))
    }

    /**
     * Replace into text input
     * @param contentDescription Content description of the view
     * @param stringToType String to type into text input
     * @param closeKeyboard Boolean to close keyboard after type, in case next step is hidden by keyboard view
     */
    protected fun replaceText(contentDescription: String, stringToType: String, closeKeyboard: Boolean = false) {
        if (closeKeyboard) {
            onView(withContentDescription(contentDescription)).perform(replaceText(stringToType), closeSoftKeyboard())
        } else {
            onView(withContentDescription(contentDescription)).perform(replaceText(stringToType))
        }
    }

    /**
     * Click on a popup window item
     * @param textMatch Text to match in the popup
     */
    protected fun clickInPopupWindow(textMatch: String) {
        onView(withText(textMatch)).inRoot(RootMatchers.isPlatformPopup()).perform(click())
    }

    /**
     * Click on an item on an adapter list
     * @param contentDescription Content descriprion of the list
     * @param position position of item on the list
     */
    protected fun clickOnItemOnList(contentDescription: String, position: Int) {
        onData(anything()).inAdapterView(withContentDescription(contentDescription)).atPosition(position).perform(click())
    }

    /**
     * Get text from recycler view item using either position or matching text
     * @param contentDescription Content description of the view
     * @param position Index position of the item on the list
     * @param textMatch Text used to match item on the list
     */
    private fun getTextFromRecyclerViewItem(contentDescription: String, position: Int = -1, textMatch: String = "") {
        if (position >= 0 && textMatch.isEmpty()) {
            onView(allOf(withContentDescription(contentDescription), isDisplayed())).perform(RecyclerViewActions
                    .actionOnItemAtPosition<RecyclerView.ViewHolder>(position, getText()))
        } else if (textMatch.isNotEmpty()) {
            onView(allOf(withContentDescription(contentDescription), isDisplayed())).perform(
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
                return isEnabled()
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
    protected fun wait(contentDescription: String = "", milliseconds: Long) {
        if (contentDescription.isNotEmpty()) {
            onView(isRoot()).perform(waitId(contentDescription, milliseconds))
        } else {
            onView(isRoot()).perform(waitFor(milliseconds)).withFailureHandler { error, viewMatcher ->

            }
        }
    }

    /**
     * Perform action of waiting for a specific view content description.
     * @param contentDescription The content description of the view to wait for.
     * @param millis The timeout of until when to wait for.
     */
    private fun waitId(contentDescription: String, millis: Long): ViewAction {
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
                for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                    if (child is TextView) {
                        stringHolder.add(child.text.toString())
                    }
                }
//                (view as ViewGroup).children.forEach { view -> if(view is TextView) stringHolder.add(view.text.toString()) }
            }
        }

    }


    private fun scroll(): ViewAction {
        stringHolder = mutableListOf()
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(withEffectiveVisibility(Visibility.VISIBLE), isDescendantOfA(anyOf(
                        isAssignableFrom(ScrollView::class.java), isAssignableFrom(HorizontalScrollView::class.java), isAssignableFrom(NestedScrollView::class.java))))
            }

            override fun getDescription(): String {
                return "Scrolling and clicking on a view"
            }

            override fun perform(uiController: UiController, view: View) {
                ScrollToAction().perform(uiController, view)
            }
        }
    }

    /**
     * Searches a view's children, no matter the level until it find a match and clicks it
     * @param textMatch Text of the view to find
     */
    private fun clickWithinRecyclerViewItem(textMatch: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun getDescription(): String {
                return "getting text from a TextView"
            }

            override fun perform(uiController: UiController, view: View) {
                val viewMatcher = withText(textMatch)
                for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                    if (viewMatcher.matches(child)) {
                        child.performClick()
                        return
                    }
                }
            }
        }
    }

    private fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id)
                v.performClick()
            }
        }
    }

    /**
     * Perform action of waiting for a specific time.
     */
    private fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds."
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }

    protected fun scrollDown(contentDescription: String) {
        onView(withContentDescription(contentDescription)).perform(swipeUp())
    }

    protected fun scrollABitDown(contentDescription: String) {
        onView(withContentDescription(contentDescription)).perform(GeneralSwipeAction(Swipe.SLOW, GeneralLocation.CENTER,
                GeneralLocation.TOP_CENTER, Press.FINGER))
    }

}
