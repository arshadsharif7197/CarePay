package com.carecloud.carepaylibray.customdialogs;

/**
 * Created by sudhir_pingale on 10/12/2016.
 */

import android.view.View;
import android.widget.NumberPicker;

import com.carecloud.carepaylibrary.R;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * A delegate implementing the simple Date Picker functionality and takes care of the spinner UI.
 */
public class SimpleDatePickerDelegate {

    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;

    private final NumberPicker monthSpinner;
    private final NumberPicker yearSpinner;

    private Calendar tempDate;
    private Calendar currentDate;
    private Calendar minDate;
    private Calendar maxDate;

    private String[] shortMonths;
    private int numberOfMonths;

    private Locale currentLocale;

    private OnDateChangedListener onDateChangedListener;

    public SimpleDatePickerDelegate(View parent) {

        setCurrentLocale(Locale.getDefault());

        NumberPicker.OnValueChangeListener onChangeListener = new NumberPicker
                .OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                tempDate.setTimeInMillis(currentDate.getTimeInMillis());

                // take care of wrapping of days and months to update greater fields
                if (picker == monthSpinner) {
                    if (oldVal == 11 && newVal == 0) {
                        tempDate.add(Calendar.MONTH, 1);
                    } else if (oldVal == 0 && newVal == 11) {
                        tempDate.add(Calendar.MONTH, -1);
                    } else {
                        tempDate.add(Calendar.MONTH, newVal - oldVal);
                    }
                } else if (picker == yearSpinner) {
                    tempDate.set(Calendar.YEAR, newVal);
                } else {
                    throw new IllegalArgumentException();
                }

                // now set the date to the adjusted one
                setDate(tempDate.get(Calendar.YEAR), tempDate.get(Calendar.MONTH));
                updateSpinners();
                notifyDateChanged();
            }
        };

        // month
        monthSpinner = (NumberPicker) parent.findViewById(R.id.month);
        monthSpinner.setMinValue(0);
        monthSpinner.setMaxValue(numberOfMonths - 1);
        monthSpinner.setDisplayedValues(shortMonths);
        monthSpinner.setOnLongPressUpdateInterval(200);
        monthSpinner.setOnValueChangedListener(onChangeListener);

        // year
        yearSpinner = (NumberPicker) parent.findViewById(R.id.year);
        yearSpinner.setOnLongPressUpdateInterval(100);
        yearSpinner.setOnValueChangedListener(onChangeListener);

        // set the min date giving priority of the minDate over startYear
        tempDate.clear();
        tempDate.set(DEFAULT_START_YEAR, 0, 1);
        setMinDate(tempDate.getTimeInMillis());

        // set the max date giving priority of the maxDate over endYear
        tempDate.clear();
        tempDate.set(DEFAULT_END_YEAR, 11, 31);
        setMaxDate(tempDate.getTimeInMillis());

        // initialize to current date
        currentDate.setTimeInMillis(System.currentTimeMillis());
        init(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), null);
    }

    public void init(int year, int monthOfYear, OnDateChangedListener onDateChangedListener) {
        setDate(year, monthOfYear);
        updateSpinners();
        onDateChangedListener = onDateChangedListener;
    }

    public void setMinDate(long date) {
        tempDate.setTimeInMillis(date);
        if (tempDate.get(Calendar.YEAR) == minDate.get(Calendar.YEAR)
                && tempDate.get(Calendar.DAY_OF_YEAR) != minDate.get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        minDate.setTimeInMillis(date);
        if (currentDate.before(minDate)) {
            currentDate.setTimeInMillis(minDate.getTimeInMillis());
        }
        updateSpinners();
    }

    public void setMaxDate(long date) {
        tempDate.setTimeInMillis(date);
        if (tempDate.get(Calendar.YEAR) == maxDate.get(Calendar.YEAR)
                && tempDate.get(Calendar.DAY_OF_YEAR) != maxDate.get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        maxDate.setTimeInMillis(date);
        if (currentDate.after(maxDate)) {
            currentDate.setTimeInMillis(maxDate.getTimeInMillis());
        }
        updateSpinners();
    }

    public int getYear() {
        return currentDate.get(Calendar.YEAR);
    }

    public int getMonth() {
        return currentDate.get(Calendar.MONTH);
    }

    /**
     * Sets the current locale.
     *
     * @param locale The current locale.
     */
    protected void setCurrentLocale(Locale locale) {
        if (!locale.equals(currentLocale)) {
            currentLocale = locale;
        }

        tempDate = getCalendarForLocale(tempDate, locale);
        minDate = getCalendarForLocale(minDate, locale);
        maxDate = getCalendarForLocale(maxDate, locale);
        currentDate = getCalendarForLocale(currentDate, locale);

        numberOfMonths = tempDate.getActualMaximum(Calendar.MONTH) + 1;
        shortMonths = new DateFormatSymbols().getShortMonths();

        if (usingNumericMonths()) {
            // We're in a locale where a date should either be all-numeric, or all-text.
            // All-text would require custom NumberPicker formatters for day and year.
            shortMonths = new String[numberOfMonths];
            for (int i = 0; i < numberOfMonths; ++i) {
                shortMonths[i] = String.format("%d", i + 1);
            }
        }
    }

    private void setDate(int year, int month) {
        currentDate.set(Calendar.YEAR, year);
        currentDate.set(Calendar.MONTH, month);
        if (currentDate.before(minDate)) {
            currentDate.setTimeInMillis(minDate.getTimeInMillis());
        } else if (currentDate.after(maxDate)) {
            currentDate.setTimeInMillis(maxDate.getTimeInMillis());
        }
    }

    private void updateSpinners() {
        // set the spinner ranges respecting the min and max dates
        if (currentDate.equals(minDate)) {
            monthSpinner.setDisplayedValues(null);
            monthSpinner.setMinValue(currentDate.get(Calendar.MONTH));
            monthSpinner.setMaxValue(currentDate.getActualMaximum(Calendar.MONTH));
            monthSpinner.setWrapSelectorWheel(false);
        } else if (currentDate.equals(maxDate)) {
            monthSpinner.setDisplayedValues(null);
            monthSpinner.setMinValue(currentDate.getActualMinimum(Calendar.MONTH));
            monthSpinner.setMaxValue(currentDate.get(Calendar.MONTH));
            monthSpinner.setWrapSelectorWheel(false);
        } else {
            monthSpinner.setDisplayedValues(null);
            monthSpinner.setMinValue(0);
            monthSpinner.setMaxValue(11);
            monthSpinner.setWrapSelectorWheel(true);
        }

        // make sure the month names are a zero based array
        // with the months in the month spinner
        String[] displayedValues = Arrays.copyOfRange(
                shortMonths, monthSpinner.getMinValue(), monthSpinner.getMaxValue() + 1);
        monthSpinner.setDisplayedValues(displayedValues);

        // year spinner range does not change based on the current date
        yearSpinner.setMinValue(minDate.get(Calendar.YEAR));
        yearSpinner.setMaxValue(maxDate.get(Calendar.YEAR));
        yearSpinner.setWrapSelectorWheel(false);

        // set the spinner values
        yearSpinner.setValue(currentDate.get(Calendar.YEAR));
        monthSpinner.setValue(currentDate.get(Calendar.MONTH));
    }

    private boolean usingNumericMonths() {
        return Character.isDigit(shortMonths[Calendar.JANUARY].charAt(0));
    }

    /**
     * Gets a calendar for locale bootstrapped with the value of a given calendar.
     *
     * @param oldCalendar The old calendar.
     * @param locale      The locale.
     */
    private Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
        if (oldCalendar == null) {
            return Calendar.getInstance(locale);
        } else {
            long currentTimeMillis = oldCalendar.getTimeInMillis();
            Calendar newCalendar = Calendar.getInstance(locale);
            newCalendar.setTimeInMillis(currentTimeMillis);
            return newCalendar;
        }
    }

    /**
     * Notifies the listener, if such, for a change in the selected date.
     */
    private void notifyDateChanged() {
        if (onDateChangedListener != null) {
            onDateChangedListener.onDateChanged(getYear(), getMonth());
        }
    }

    /**
     * The callback used to indicate the user changed the date.
     */
    public interface OnDateChangedListener {

        /**
         * Called upon a date change.
         *
         * @param year        The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility with {@link
         *                    Calendar}.
         */
        void onDateChanged(int year, int monthOfYear);
    }
}
