package com.carecloud.carepaylibray.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private ScreenModel screenModel;
    private TextView editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout parent = new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(20, 20, 0, 20);

        screenModel = ApplicationWorkflow.Instance().getSignupScreenModel();
        getActivity().setTitle(screenModel.getName());

        int index = 0;
        for (final ScreenComponentModel componentModel : screenModel.getComponentModels()) {
            if (componentModel.getType().equals("inputtext") || componentModel.getType().equals("email") || componentModel.getType().equals("password")) {

                TextInputLayout fullNameTextInputLayout = new TextInputLayout(getActivity());
                editText = new EditText(getActivity());

                LinearLayout.LayoutParams signUpParentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                signUpParentLayoutParams.setMargins(17, 268, 0, 0);

                editText.setLayoutParams(signUpParentLayoutParams);
                if (componentModel.getType().equals("password")) {

                    editText.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                editText.setTextColor(Color.parseColor("#1f9bde"));
                editText.setHintTextColor(Color.parseColor("#78909c"));
                editText.setLayoutParams(matchWidthParams);

                fullNameTextInputLayout.setHint(componentModel.getLabel());

                parent.addView(fullNameTextInputLayout, index, matchWidthParams);
                fullNameTextInputLayout.addView(editText);
                index++;

            } else {
                if (componentModel.getType().equals("button")) {
                    LinearLayout.LayoutParams signUpButtonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    signUpButtonLayoutParams.setMargins(0, 86, 0, 0);
                    Button signUpButton = new Button(getActivity());
                    signUpButton.setLayoutParams(signUpButtonLayoutParams);
                    signUpButton.setText(componentModel.getLabel());
                    signUpButton.setBackgroundColor(Color.parseColor("#1f9bde"));
                    signUpButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    signUpButton.setTextColor(Color.parseColor("#ffffff"));

                    signUpButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (final ScreenComponentModel componentModel : screenModel.getComponentModels()) {
                                if (componentModel.getType().equals("email") || componentModel.getType().equals("password") && componentModel.isRequired()) {
                                    if (TextUtils.isEmpty(editText.getText().toString())) {
                                        Toast.makeText(getActivity(), "Fields can not be empty", Toast.LENGTH_LONG).show();
                                        break;
                                    } else if (componentModel.getType().equals("email") && !isEmailValid(editText.getText().toString())) {
                                        Toast.makeText(getActivity(), "Email field is not valid", Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                }
                            }
                        }
                    });
                    parent.addView(signUpButton);

                } else if (componentModel.getType().equals("text")) {
                    TextView alreadyAccountTextView = new TextView(getActivity());
                    alreadyAccountTextView.setLayoutParams(matchWidthParams);
                    alreadyAccountTextView.setText(componentModel.getLabel());
                    alreadyAccountTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                    alreadyAccountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    alreadyAccountTextView.setTextColor(Color.parseColor("#1f9bde"));

                    RelativeLayout alreadyAccountLayout = new RelativeLayout(getActivity());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    alreadyAccountLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER);
                    alreadyAccountLayout.addView(alreadyAccountTextView, params);
                    parent.addView(alreadyAccountLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
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









