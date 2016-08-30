package com.carecloud.carepaylibray.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;

import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;

/*import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;*/

/**
 * Created by harish_revuri on 8/24/2016.
 */

public class SignInFragment extends android.support.v4.app.Fragment{
    private static final String LOG_TAG = SignInFragment.class.getSimpleName();
    View view;
    int viewId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewId=this.getArguments().getInt("viewid");
    }
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout screenLayout  = (LinearLayout) mInflater.inflate(R.layout.fragment_signin, null, false);

        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout parent = (LinearLayout) screenLayout.findViewById(R.id.linearLayoutScreenContainer); // new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams LayoutParamsview = new AppBarLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ScreenModel screenModel = ApplicationWorkflow.Instance().getLoginScreenModel();
        getActivity().setTitle("Sign In");

    //        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

        int index = 0;
        LinearLayout horizontalLayout=null;
        LinearLayout relative = new LinearLayout(getActivity());
        relative.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight=1;
        relative.setLayoutParams(matchWidthParams);
//        horizontalLayout.setLayoutParams();
        for (ScreenComponentModel componentModel : screenModel.getComponentModels()) { // Calling component models dynamically
            if (componentModel.getType().equals("inputtext") || componentModel.getType().equals("password")) {
                TextInputLayout emailTextInputLayout = new TextInputLayout(getActivity());
                TextInputLayout passwordTextInputLayout = new TextInputLayout(getActivity());

                EditText email = new EditText(getActivity());
                EditText password = new EditText(getActivity());


                email.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                //email.setWidth((int) convertPixelsToDp(14.5f, getActivity()));
                //email.setHeight((int) convertPixelsToDp(14f, getActivity()));

                emailTextInputLayout.setHint(componentModel.getLabel());
                email.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);


                email.setTextColor(Color.parseColor("#1f9bde"));
                password.setLayoutParams(matchWidthParams);
                passwordTextInputLayout.setHint(componentModel.getLabel());
                if (componentModel.getType().equals("password")){
                    email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                emailTextInputLayout.addView(email);
                parent.addView(emailTextInputLayout, index, matchWidthParams);
                Log.v(LOG_TAG, "added passwordTextInputLayout");
                passwordTextInputLayout.addView(password);
                index++;

            } else if (componentModel.getType().equals("button")) {
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setCornerRadius(5);
                gradientDrawable.setColor(Color.parseColor("#1f9bde"));
                Button button = new Button(getActivity());
                button.setLayoutParams(matchWidthParams);
                button.setText(componentModel.getLabel());
                //button.setHeight((int) convertPixelsToDp(44, getActivity()));
                //button.setWidth((int) convertPixelsToDp(316f, getActivity()));
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                button.setBackground(gradientDrawable);

                if(componentModel.getLabel().equals("SIGN IN"))
                {
                    button.setTextColor(Color.parseColor("#ffffff"));
                    button.setBackgroundColor(Color.rgb(31, 155, 222));

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HomeScreenFragment mSelectLanguageFragment = new HomeScreenFragment();
                            Bundle mBundle = new Bundle();
                            mBundle.putInt("viewid",viewId);
                            mSelectLanguageFragment.setArguments(mBundle);
                            getFragmentManager().beginTransaction().addToBackStack("siginfragment").replace(viewId, mSelectLanguageFragment).commit();
                        }
                    });
                }
                else {
                    button.setTextColor(Color.parseColor("#1f9bde"));
                    button.setBackgroundColor(Color.WHITE);
                }
                button.setHint(componentModel.getLabel());
                parent.addView(button);
                Log.v(LOG_TAG, "added button");

            } else if (componentModel.getType().equals("text")) {
/*
                LinearLayout.LayoutParams alreadyAccountLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                LinearLayout alreadyAccountLayout = new LinearLayout(getActivity());
                alreadyAccountLayout.setLayoutParams(alreadyAccountLayoutParams);
                alreadyAccountLayout.setOrientation(LinearLayout.HORIZONTAL);
                alreadyAccountLayoutParams.weight = 1;
                alreadyAccountLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;*/

                TextView tv1 = new TextView(getActivity());
                //tv1.setLayoutParams(matchWidthParams);
                tv1.setText(componentModel.getLabel());

                tv1.setTextColor(Color.parseColor("#1f9bde"));

                                //relative.setOrientation(LinearLayout.HORIZONTAL);
                //relative.setBackgroundColor(Color.GRAY);

               /* RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                //params.weight=1.0f;*/
                if (componentModel.getLabel().equals("Change Language"))
                {
                    //params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    //tv1.setBackgroundColor(Color.CYAN);
                    tv1.setLayoutParams(layoutParams);
                    tv1.setGravity(Gravity.LEFT);
                    relative.addView(tv1);
                    Log.v(LOG_TAG,"Added left");
                }
                else if(componentModel.getLabel().equals("Forgot Password"))
                {
                    //params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    //
                    // tv1=new TextView(getActivity());
                    //
                    // tv1.setBackgroundColor(Color.CYAN);;
                    tv1.setLayoutParams(layoutParams);
                    tv1.setGravity(Gravity.RIGHT);
                    relative.addView(tv1);
                    Log.v(LOG_TAG,"Added right");
                }

            }
        }
        parent.addView(relative);
        parent.setGravity(Gravity.BOTTOM);
        return screenLayout;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("CarPayAndroid");
    }
}