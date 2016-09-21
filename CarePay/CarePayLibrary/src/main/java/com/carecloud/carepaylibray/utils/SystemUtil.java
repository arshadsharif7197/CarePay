package com.carecloud.carepaylibray.utils;

/**
 * Created by sharath_rampally on 8/24/2016.
 * Utilities
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

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

    public static void setGothamRoundedBookTypeface(Context context, TextView view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/gotham_rounded_book.otf");
        view.setTypeface(typeface);
    }

    public static void setGothamRoundedMediumTypeface(Context context, TextView view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/gotham_rounded_medium.otf");
        view.setTypeface(typeface);
    }

    public static void setProximaNovaSemiboldTypefaceEdittext(Context context, EditText view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova_semibold.otf");
        view.setTypeface(typeface);
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
        String hint = (String) textInputLayout.getTag();
        if (hint == null) {
            hint = ""; // if no hint use the empty string
        }
        String hintCaps = hint.toUpperCase();
        String text = editText.getText().toString();
        if (hasFocus) {
            // focus gained; set the hint to text input layout
            textInputLayout.setHint(hintCaps);
            if (StringUtil.isNullOrEmpty(text)) {
                // if no text set empty hint in the edit
                editText.setHint("");
            }
        } else {
            if (StringUtil.isNullOrEmpty(text)) { // lose focus, and no text in the edit
                // remove hint from the text input layout
                textInputLayout.setHint("");
                // change hint to lower in the edit
                editText.setHint(hint);
            } else {
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

}