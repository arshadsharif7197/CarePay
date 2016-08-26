package com.carecloud.carepaylibray.util;

/**
 * Created by sharath_rampally on 8/24/2016.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Utility {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private static final String SHARED_PREF_SEL_LANG_KEY = "sel_lang";

    public static void saveSelectedLanguage(Activity activity, String selLang) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SHARED_PREF_SEL_LANG_KEY, selLang);
        editor.apply();
    }

    public String retrieveSelectedLanguage(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(SHARED_PREF_SEL_LANG_KEY, "English");
    }

    /**
     * Parses the json
     * @return The main model object
     */
//    public WorkflowScreens readFileAndGetArray(Context context) {
//        try {
//            String json;
//            InputStream is = context.getAssets().open("deta.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//            Log.e("LOG_TAG",json.toString());
//
//            JSONObject obj = new JSONObject(json);
//
//            LanguageParser lParser= new LanguageParser();
//            return lParser.getParsedObjectFromJSON(obj.getJSONObject("workflow_screens").toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public EditText getEditText(Context context, int fieldId , String inputType, String hintText){

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        EditText myEditText = new EditText(context); // Pass it an Activity or Context
        myEditText.setId(fieldId);
        myEditText.setHint(hintText);
        myEditText.setLayoutParams(params);
        myEditText.setTextColor(Color.BLACK);


        if(inputType.equalsIgnoreCase("password")){
            myEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else if(inputType.equalsIgnoreCase("phonenumber")){
            myEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        }else if(inputType.equalsIgnoreCase("email")){
            myEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }else{
            myEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        }

        return myEditText;
    }



    public TextView getTextView(Context context, int fieldId , String text){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,10,20,0);

        TextView myTextView = new TextView(context);
        myTextView.setText(text);
        myTextView.setId(fieldId);

        myTextView.setLayoutParams(params);
        myTextView.setTextColor(Color.BLACK);
        return myTextView;
    }
    public Button getButton(Context context, int fieldId , String text){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,10,20,0);
        params.gravity = Gravity.CENTER;

        Button btn = new Button(context);
        btn.setText(text);
        btn.setId(fieldId);
        btn.setLayoutParams(params);
        return btn;
    }

}

