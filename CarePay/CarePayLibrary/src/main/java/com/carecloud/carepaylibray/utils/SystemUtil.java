package com.carecloud.carepaylibray.utils;

/**
 * Created by sharath_rampally on 8/24/2016.
 * Utilities
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

import java.util.Date;
import java.util.Locale;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

public class SystemUtil {

    public static boolean isTablet(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float widthInInches = metrics.widthPixels / metrics.xdpi;
        float heightInInches = metrics.heightPixels / metrics.ydpi;
        double sizeInInches = Math.sqrt(Math.pow(widthInInches, 2) + Math.pow(heightInInches, 2));
        return sizeInInches >= 6.5;
    }

    public static String onShortDrName(String fullName) {
        String stringSplitArr[] = fullName.split(" ");
        return String.valueOf(stringSplitArr[1].charAt(0)) + String.valueOf(stringSplitArr[2].charAt(0));
    }

    /* Font utils*/

    public static void setTypefaceFromAssets(Context context, String pathToFontInAssets, TextView view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), pathToFontInAssets);
        view.setTypeface(typeface);
    }

    public static void setProximaNovaRegularTypeface(Context context, TextView view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova_regular.otf");
        view.setTypeface(typeface);
    }

    public static void setProximaNovaExtraboldTypeface(Context context, TextView view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Extrabld.otf");
        view.setTypeface(typeface);
    }

    public static void setProximaNovaSemiboldTypeface(Context context, TextView view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova_semibold.otf");
        view.setTypeface(typeface);
    }
    public static void setProximaNovaSemiboldTextInputLayout(Context context, TextInputLayout view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova_semibold.otf");
        view.setTypeface(typeface);
    }

    public static void setGothamRoundedBookTypeface(Context context, TextView view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/gotham_rounded_book.otf");
        view.setTypeface(typeface);
    }

    public static void setGothamRoundedMediumTypeface(Context context, TextView view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/gotham_rounded_medium.otf");
        view.setTypeface(typeface);
    }
    public static void setGothamRoundedBoldTypeface(Context context, TextView view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/gotham_rounded_bold.otf");
        view.setTypeface(typeface);
    }

    public static void setProximaNovaSemiboldTypefaceEdittext(Context context, EditText view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova_semibold.otf");
        view.setTypeface(typeface);
    }

    /**
     * Set the type face of a text input layout
     * @param context The context
     * @param layout The layout
     */
    public static void setProximaNovaRegularTypefaceLayout(Context context, TextInputLayout layout) {
        Typeface proximaNovaRegular = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/proximanova_regular.otf");
        layout.setTypeface(proximaNovaRegular);
    }

    /**
     * Hides the keyboard
     *
     * @param activity The activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * Shows the soft keyboard
     * @param activity The activity
     */
    public static void showSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Handles the change from normal to caps of the hint in a view wrapped by a text input layout;
     * The view has to have the input text layout set as tag; the input text layout the hint (as tag)
     * @param view The edit view
     * @param hasFocus When the view gains the focus
     */
    public static void handleHintChange(View view, boolean hasFocus) {
        if (view == null) {
            return;
        }

        EditText editText = (EditText) view;
        TextInputLayout textInputLayout = (TextInputLayout) editText.getTag();
        if (textInputLayout == null) {
            return;
        }

        boolean error = textInputLayout.isErrorEnabled();
        String hint = (String) textInputLayout.getTag();
        if (hint == null) {
            hint = ""; // if no hint, use the empty string as hint
        }
        String hintCaps = hint.toUpperCase();
        String text = editText.getText().toString();
        if (hasFocus) { // focus gained
            // set the hint to text input layout (in caps)
            textInputLayout.setHint(hintCaps);
            if (StringUtil.isNullOrEmpty(text)) { // but only if there is no text in the edit
                editText.setHint("");
            }
        } else { // focus lost
            if (StringUtil.isNullOrEmpty(text) && !error) { // if no text in the edit and error not enabled
                // remove hint from the text input layout
                textInputLayout.setHint("");
                // change hint to lower in the edit
                setProximaNovaRegularTypeface(view.getContext(), editText);
                editText.setHint(hint);
            } else { // there is some text in the edit or the error is enabled
                // keep the hint up
                textInputLayout.setHint(hintCaps);
            }
        }
    }

    /**
     * Shows a message dialog
     * @param context The context
     * @param title The title
     * @param body The message
     */
    public static void showDialogMessage(Context context, String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(body)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                        }
                    }
                });
        AlertDialog userDialog = builder.create();
        userDialog.show();
    }

    /**
     * convert date string in to month and day.
     * @param dateStr the String to evaluate
     */
    public static String[] onDateParseToString(Context context,String dateStr) {
        String stringDate[] = dateStr.split(" ");
        String formateDate[] = new String[2];
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(context.getString(R.string.dateFormatString), Locale.ENGLISH);
            Date appointdate = sdf.parse(stringDate[0]);
            formateDate[0] = android.text.format.DateFormat.format("MMMM", appointdate) + " "
                    + DateUtil.getDayOrdinal(Integer.parseInt(android.text.format.DateFormat.format("dd", appointdate).toString()));
            formateDate[1] = stringDate[1] + " " + stringDate[2];
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return formateDate;
    }

    public static boolean isNotEmptyString(String string){
        if (string != null && !string.isEmpty() && !string.equals("null")){
            return true;
        }
        return false;
    }


}