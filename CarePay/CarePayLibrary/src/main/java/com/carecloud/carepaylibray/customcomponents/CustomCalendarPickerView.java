package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.squareup.timessquare.CalendarPickerView;


/**
 * Created by arpit_jain1 on 10/13/2016.
 * Custom Calendar Pick View to set typeface for calendar date and month.
 */

public class CustomCalendarPickerView extends CalendarPickerView {

    Context context;

    /**
     * Constructor to initialise Custom Calendar Picker View
     *
     * @param context application context
     * @param attrs A collection of attributes, as found associated with a tag in an XML document
     */
    public CustomCalendarPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    /**
     * Method to set typeface for calendar date and month
     */
    private void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/proximanova_semibold.otf");
        this.setTypeface(typeface);

        Typeface monthTypeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/proximanova_semibold.otf");
        this.setTitleTypeface(monthTypeface);

        Typeface dateTypeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/gotham_rounded_book.otf");
        this.setDateTypeface(dateTypeface);
    }
}