package com.carecloud.carepaylibray.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anil_kairamkonda on 8/24/2016.
 */
public class SignUpFragment extends Fragment {
    ScreenModel screenModel;


    View view;
    private TextView et1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout parent = new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(20, 20, 0, 20);

        LinearLayout.LayoutParams LayoutParamsview = new AppBarLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        screenModel = ApplicationWorkflow.Instance().getSignupScreenModel();

        getActivity().setTitle(screenModel.getName());

        int index = 0;


        for (final ScreenComponentModel componentModel : screenModel.getComponentModels()) {

            if (componentModel.getType().equals("inputtext") || componentModel.getType().equals("email") || componentModel.getType().equals("password")) {

                TextInputLayout fullnameTextInputLayout = new TextInputLayout(getActivity());

//                LinearLayout.LayoutParams l2=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                l2.setMargins(14,0,0,0);

                //et1.setLayoutParams(l2);


                et1 = new EditText(getActivity());

                LinearLayout.LayoutParams la2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                la2.setMargins(17, 268, 0, 0);

                et1.setLayoutParams(la2);
                if (componentModel.getType().equals("password")) {
                    et1.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }


                et1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                et1.setTextColor(Color.parseColor("#1f9bde"));
                et1.setLayoutParams(matchWidthParams);
                et1.setTypeface(et1.getTypeface(), Typeface.BOLD);

                fullnameTextInputLayout.setHint(componentModel.getLabel());

                parent.addView(fullnameTextInputLayout, index, matchWidthParams);
                fullnameTextInputLayout.addView(et1);
                index++;


            } else {
                if (componentModel.getType().equals("button")) {
                    LinearLayout.LayoutParams la = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    la.setMargins(1, 99, 17, 17);
                    Button btn1 = new Button(getActivity());

                    btn1.setLayoutParams(la);
                    btn1.setText(componentModel.getLabel());
                    btn1.setBackgroundColor(Color.parseColor("#1f9bde"));
                    btn1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                    btn1.setTextColor(Color.parseColor("#ffffff"));

                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (final ScreenComponentModel componentModel : screenModel.getComponentModels()) {
                                if (componentModel.getType().equals("email") || componentModel.getType().equals("password") && componentModel.isRequired()) {
                                    if (TextUtils.isEmpty(et1.getText().toString())) {
                                        Toast.makeText(getActivity(), "Fields can not be empty", Toast.LENGTH_LONG).show();
                                        break;
                                    } else if (componentModel.getType().equals("email") && !isEmailValid(et1.getText().toString())) {
                                        Toast.makeText(getActivity(), "Please enter a valid email id", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        }
                    });

                    parent.addView(btn1);
                } else if (componentModel.getType().equals("text")) {

                    LinearLayout.LayoutParams alreadyAccountLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    LinearLayout alreadyAccountLayout = new LinearLayout(getActivity());
                    alreadyAccountLayout.setLayoutParams(alreadyAccountLayoutParams);
                    alreadyAccountLayout.setOrientation(LinearLayout.VERTICAL);
                    alreadyAccountLayoutParams.weight = 1;
                    alreadyAccountLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;


                    TextView tv1 = new TextView(getActivity());
                    tv1.setLayoutParams(matchWidthParams);
                    tv1.setText(componentModel.getLabel());
                    tv1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    tv1.setTextColor(Color.parseColor("#1f9bde"));

                    // tv1.setTypeface(et1.getTypeface(), Typeface.BOLD);


                    RelativeLayout relative = new RelativeLayout(getActivity());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    relative.setGravity(Gravity.BOTTOM | Gravity.CENTER);
                    relative.addView(tv1, params);
                    parent.addView(relative, new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        }
        return parent;
    }

    private boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;

    }


}









