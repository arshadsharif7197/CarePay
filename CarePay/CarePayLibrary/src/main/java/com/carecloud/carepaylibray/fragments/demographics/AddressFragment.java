package com.carecloud.carepaylibray.fragments.demographics;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;

import java.util.ArrayList;

/**
 * Created by Rakesh on 8/23/2016.
 */
public class AddressFragment  extends Fragment {

    ScreenModel getDemographicsAddressScreenModel=new ScreenModel();
    ArrayList<ScreenComponentModel> componentModels= new ArrayList<ScreenComponentModel>();

    public static final AddressFragment newInstance(Bundle bundle) {
        AddressFragment fragment = new AddressFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Address");
        ScrollView scrollView = new ScrollView(getActivity());
        ViewGroup.LayoutParams scrollViewLayoutparams = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollView.setLayoutParams(scrollViewLayoutparams);

        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
         LinearLayout parent = new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(17, 102, 17, 14);
        parent.setFocusable(true);
        parent.isFocusableInTouchMode();
        scrollView.addView(parent);

        LinearLayout linearLayoutCityState = new LinearLayout(getContext());
        linearLayoutCityState.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutCityState.setLayoutParams(matchWidthParams);
        LinearLayout.LayoutParams paramsCityState = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsCityState.weight = 1;


        ScreenModel screenModel = ApplicationWorkflow.Instance().getDemographicsAddressScreenModel();

        getActivity().setTitle(screenModel.getName());
        int index = 0;
        for (ScreenComponentModel componentModel : screenModel.getComponentModels()) {
            if (componentModel.getType().equals("image") || componentModel.getType().equals("ImageView")) {
                ImageView mIVCard = new ImageView(getActivity());
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                childLayoutParams.setMargins(138, 212, 135, 514);
                mIVCard.setLayoutParams(childLayoutParams);
                mIVCard.setImageResource(R.drawable.icn_signup_illustration_step_1_address);
                mIVCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
                mIVCard.setLayoutParams(matchWidthParams);
                parent.addView(mIVCard);
                index++;

            } else if (componentModel.getType().equals("heading")) {
                TextView titleText = new TextView(getActivity());
                titleText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                titleText.setText(componentModel.getLabel());
                titleText.setGravity(Gravity.CENTER);
                titleText.setTextSize(21.0f);
                titleText.setTextColor(ContextCompat.getColor(getActivity(), R.color.charcoal));
                parent.addView(titleText);
                index++;

            } else if (componentModel.getType().equals("subHeading")) {
                TextView titleText = new TextView(getActivity());
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                childLayoutParams.setMargins(0, 0, 0, 60);
                titleText.setLayoutParams(childLayoutParams);
                titleText.setText(componentModel.getLabel());
                titleText.setGravity(Gravity.CENTER);
                titleText.setTextSize(13.0f);
                titleText.setTextColor(ContextCompat.getColor(getActivity(), R.color.cadet_gray));
                parent.addView(titleText);
                index++;

            }
            else if (componentModel.getType().equals("buttonWithImage")) {
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout childLayout = new LinearLayout(getActivity());
                childLayoutParams.setMargins(0, 0, 0, 60);
                childLayout.setLayoutParams(childLayoutParams);
                childLayout.setOrientation(LinearLayout.HORIZONTAL);
                childLayout.setPadding(0, 20, 0, 0);
                ImageView camera = new ImageView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 20, 0);
                camera.setLayoutParams(params);
                camera.setImageResource(R.drawable.icn_camera);

                Button button = new Button(getActivity());
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                button.setLayoutParams(buttonParams);
                buttonParams.setMargins(20, 0, 0, 0);
                buttonParams.gravity = Gravity.CENTER;
                button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_background));
                button.setText(componentModel.getLabel());
                button.setTextSize(13.0f);
                button.setTextColor(ContextCompat.getColor(getActivity(), R.color.rich_electric_blue));
                button.setGravity(Gravity.CENTER);
                button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                button.setClickable(true);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Scan Successful", Toast.LENGTH_LONG).show();
                    }

                });
                button.setClickable(false);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "ReScan", Toast.LENGTH_LONG).show();
                    }

                });

                //  button.setPadding(10,10,10,10);
                childLayout.addView(camera);
                childLayout.addView(button);
                parent.addView(childLayout);
                index++;
            } else if (componentModel.getType().equals("titleText")) {
                TextView titleText = new TextView(getActivity());
                LinearLayout.LayoutParams titleTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                titleTextParams.gravity = Gravity.LEFT;
                titleTextParams.setMargins(0, 0, 0, 60);
                titleText.setLayoutParams(titleTextParams);
                titleText.setText(componentModel.getLabel());
                titleText.setTextSize(17.0f);
                titleText.setTextColor(ContextCompat.getColor(getActivity(), R.color.light_gray));
                parent.addView(titleText);
                index++;

            } else if (componentModel.getType().equals("selector")) {
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout childLayout = new LinearLayout(getActivity());
                childLayout.setWeightSum(2.0f);
                childLayoutParams.setMargins(0, 0, 0, 60);
                childLayout.setLayoutParams(childLayoutParams);
                childLayout.setOrientation(LinearLayout.HORIZONTAL);
                childLayout.setPadding(0, 20, 0, 0);
                TextView inputText = new TextView(getActivity());
                LinearLayout.LayoutParams inputTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                inputText.setLayoutParams(inputTextLayoutParams);
                inputTextLayoutParams.setMargins(20, 0, 0, 0);
                inputTextLayoutParams.gravity = Gravity.LEFT;
                inputTextLayoutParams.weight = 1.3f;
                inputText.setText(componentModel.getLabel());
                inputText.setTextSize(17.0f);
                inputText.setTextColor(ContextCompat.getColor(getActivity(), R.color.charcoal));


                TextView selectText = new TextView(getActivity());
                LinearLayout.LayoutParams selectTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                selectText.setLayoutParams(inputTextLayoutParams);
                selectTextLayoutParams.setMargins(20, 0, 0, 0);
                selectText.setText("Select");
                selectText.setGravity(Gravity.CENTER_HORIZONTAL);
                selectText.setTextSize(14.0f);
                selectText.setTextColor(ContextCompat.getColor(getActivity(), R.color.bright_cerulean));
                selectText.setClickable(true);
                selectText.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getActivity(), "Select State", Toast.LENGTH_LONG).show();

                            }
                        }

                );


                childLayout.addView(inputText);
                childLayout.addView(selectText);

                parent.addView(childLayout);
                index++;
            } else if (componentModel.getType().equals("togglebutton")) {
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout childLayout = new LinearLayout(getActivity());
                childLayout.setWeightSum(2.0f);
                childLayoutParams.setMargins(0, 0, 0, 60);
                childLayout.setLayoutParams(childLayoutParams);
                childLayout.setOrientation(LinearLayout.HORIZONTAL);
                childLayout.setPadding(0, 20, 0, 0);
                final TextView inputText = new TextView(getActivity());
                final LinearLayout.LayoutParams inputTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                inputText.setLayoutParams(inputTextLayoutParams);
                inputTextLayoutParams.setMargins(20, 0, 0, 0);
                inputTextLayoutParams.gravity = Gravity.LEFT;
                inputTextLayoutParams.weight = 1.2f;
                inputText.setText(componentModel.getLabel());
                inputText.setTextSize(17.0f);
                inputText.setTextColor(ContextCompat.getColor(getActivity(), R.color.charcoal));


                Switch switchView = new Switch(getActivity());
                LinearLayout.LayoutParams switchViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                switchView.setLayoutParams(inputTextLayoutParams);
                switchViewLayoutParams.setMargins(20, 0, 0, 0);
                childLayout.addView(inputText);
                childLayout.addView(switchView);
                parent.addView(childLayout);
                index++;
            } else if (componentModel.getType().equals("inputtext")) {
                TextInputLayout textInputLayout = new TextInputLayout(getActivity());
                LinearLayout.LayoutParams switchViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                EditText editText = new EditText(getActivity());
                if (componentModel.getLabel().equals("phonenumber")){

                        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789-+()"));
                    }


                editText.setHint(componentModel.getLabel());

                textInputLayout.addView(editText);
                if (componentModel.getLabel().equals("CITY") || componentModel.getLabel().equals("STATE")) {
                    textInputLayout.setLayoutParams(paramsCityState);
                    linearLayoutCityState.addView(textInputLayout);
                    if (componentModel.getLabel().equals("STATE")) {
                        parent.addView(linearLayoutCityState);
                        index++;
                    }
                } else {

                    parent.addView(textInputLayout);
                    index++;
                }


            } else if (componentModel.getType().equals("button")) {

                Button button = new Button(getActivity());
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                button.setLayoutParams(buttonParams);
                buttonParams.setMargins(17, 64, 17, 62);
                buttonParams.gravity = Gravity.CENTER;


                button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_next));
                button.setText(componentModel.getLabel());
                button.setTextSize(13.0f);
                button.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                button.setGravity(Gravity.CENTER);
                button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Go TO Next Screen", Toast.LENGTH_LONG).show();
                    }
                });
                //  button.setPadding(10,10,10,10);


                parent.addView(button);

                index++;
            }

            }
            return scrollView;
        }



}



