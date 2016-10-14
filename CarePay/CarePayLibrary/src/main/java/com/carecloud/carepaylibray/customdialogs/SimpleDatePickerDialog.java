package com.carecloud.carepaylibray.customdialogs;

/**
 * Created by sudhir_pingale on 10/12/2016.
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.carecloud.carepaylibrary.R;

/**
 * A simple dialog containing month and year picker and also provides callback on positive
 * selection.
 */
public class SimpleDatePickerDialog extends AlertDialog implements DialogInterface
        .OnClickListener, SimpleDatePickerDelegate.OnDateChangedListener {

    private static final String YEAR = "year";
    private static final String MONTH = "month";

    private SimpleDatePickerDelegate simpleDatePickerDelegate;
    private OnDateSetListener dateSetListener;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param year        The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility with {@link
         *                    java.util.Calendar}.
         */
        void onDateSet(int year, int monthOfYear);
    }

    /**
     * @param context The context the dialog is to run in.
     */
    public SimpleDatePickerDialog(Context context, OnDateSetListener listener, int year,
                                  int monthOfYear) {
        this(context, 0, listener, year, monthOfYear);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param theme   the theme to apply to this dialog
     */
    @SuppressLint("InflateParams")
    public SimpleDatePickerDialog(Context context, int theme, OnDateSetListener listener, int year,
                                  int monthOfYear) {
        super(context, theme);

        dateSetListener = listener;

        Context themeContext = getContext();
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        View view = inflater.inflate(R.layout.month_year_picker, null);
        setView(view);
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);

        simpleDatePickerDelegate = new SimpleDatePickerDelegate(view);
        simpleDatePickerDelegate.init(year, monthOfYear, this);
    }

    @Override
    public void onDateChanged(int year, int month) {
        // Stub - do nothing
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (dateSetListener != null) {
                    dateSetListener.onDateSet(
                            simpleDatePickerDelegate.getYear(),
                            simpleDatePickerDelegate.getMonth());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
            default:
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, simpleDatePickerDelegate.getYear());
        state.putInt(MONTH, simpleDatePickerDelegate.getMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        simpleDatePickerDelegate.init(year, month, this);
    }

    public void setMinDate(long minDate) {
        simpleDatePickerDelegate.setMinDate(minDate);
    }

    public void setMaxDate(long maxDate) {
        simpleDatePickerDelegate.setMaxDate(maxDate);
    }
}
