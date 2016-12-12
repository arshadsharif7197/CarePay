package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * DialogFragment for displaying a simple date (month and year) picker.
 */
public class SimpleDatePickerDialogFragment extends DialogFragment {

    public static final int NULL_INT = -1;
    private static final String ARG_MONTH = "month";
    private static final String ARG_YEAR = "year";
    private static final String ARG_MIN_DATE = "min_date";
    private static final String ARG_MAX_DATE = "max_date";

    private SimpleDatePickerDialog.OnDateSetListener onDateSetListener;

    /**
     * Create a new default instance of the DialogFragment
     */
    public SimpleDatePickerDialogFragment() {
        // Do nothing
    }

    /**
     * Create a new instance of the DialogFragment
     *
     * @param year  the initial year
     * @param month the initial month
     * @return the fragment instance
     */
    public static SimpleDatePickerDialogFragment getInstance(int year, int month) {
        return getInstance(year, month, NULL_INT, NULL_INT);
    }

    /**
     * Create a new instance of the DialogFragment
     *
     * @param year    the initial year
     * @param month   the initial month
     * @param minDate set the min date in milliseconds which should be less then initial date set.
     * @param maxDate set the max date in milliseconds which should not be less then current date.
     * @return The instance
     */
    public static SimpleDatePickerDialogFragment getInstance(int year, int month, long minDate,
                                                             long maxDate) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_MONTH, month);
        bundle.putInt(ARG_YEAR, year);
        bundle.putLong(ARG_MIN_DATE, minDate);
        bundle.putLong(ARG_MAX_DATE, maxDate);

        SimpleDatePickerDialogFragment datePickerDialogFragment = new
                SimpleDatePickerDialogFragment();
        datePickerDialogFragment.setArguments(bundle);
        return datePickerDialogFragment;
    }

    /**
     * Get callback of the year and month selected.
     *
     * @param dateSetListener To get call of selected date
     */
    public void setOnDateSetListener(SimpleDatePickerDialog.OnDateSetListener dateSetListener) {
        this.onDateSetListener = dateSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle dataBundle = getArguments();
        int year = dataBundle.getInt(ARG_YEAR);
        int month = dataBundle.getInt(ARG_MONTH);
        long minDate = dataBundle.getLong(ARG_MIN_DATE);
        long maxDate = dataBundle.getLong(ARG_MAX_DATE);
        checkForValidMinDate(year, month, minDate);
        checkForValidMaxDate(year, month, maxDate);
        SimpleDatePickerDialog simpleDatePickerDialog = new SimpleDatePickerDialog(
                getActivity(), onDateSetListener, year, month);
        if (minDate != NULL_INT) {
            simpleDatePickerDialog.setMinDate(minDate);
        } else {
            simpleDatePickerDialog.setMinDate(System.currentTimeMillis());
        }
        if (maxDate != NULL_INT) {
            simpleDatePickerDialog.setMaxDate(maxDate);
        }
        return simpleDatePickerDialog;
    }

    private void checkForValidMinDate(int year, int month, long minDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.MONTH, month);
        if (calendar.getTimeInMillis() < minDate) {
            throw new IllegalArgumentException("Min date should be less than initial date set");
        }
    }

    private void checkForValidMaxDate(int year, int month, long maxDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.MONTH, month);
        if (calendar.getTimeInMillis() < maxDate) {
            throw new IllegalArgumentException(
                    "Max date should not be less than current date.");
        }
    }
}