package com.carecloud.carepaylibray.fragments.demographics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("AddressFragment");
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout parent = new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(20, 20, 0, 20);
//        LinearLayout.LayoutParams LayoutParamsview = new AppBarLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        LinearLayout linearLayoutCityState=new LinearLayout(getContext());
        linearLayoutCityState.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutCityState.setLayoutParams(matchWidthParams);
        LinearLayout.LayoutParams paramsCityState=new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsCityState.weight=1;


        ScreenModel screenModel = ApplicationWorkflow.Instance().getDemographicsAddressScreenModel();

        int index = 0;
        for (ScreenComponentModel componentModel : screenModel.getComponentModels()) {
            if (componentModel.getType().equals("image")|| componentModel.getType().equals("ImageView")) {

                ImageView image = new ImageView(getActivity());
                image.setImageResource(R.drawable.icn_signup_illustration_step_1_address);
                //  image.setLayoutParams(matchWidthParams);
                parent.addView(image,index,matchWidthParams);

                index++;
            }else if(componentModel.getType().equals("phonenumber")) {
                TextInputLayout et12 = new TextInputLayout(getActivity());
                EditText et1 = new EditText(getActivity());
                et1.setHint(componentModel.getLabel());
                et12.addView(et1);
                parent.addView(et12);
                index++;
            }

            else if(componentModel.getType().equals("button")){

                Button select =  new Button(getActivity());
                select.setText(componentModel.getLabel());
                select.setLayoutParams(matchWidthParams);
                parent.addView(select);
                index++;

            }
            else if(componentModel.getType().equals("Inputtext")) {
                TextInputLayout et12 = new TextInputLayout(getActivity());
                EditText et1 = new EditText(getActivity());
                et1.setHint(componentModel.getLabel());
                et12.addView(et1);



                if(componentModel.getLabel().equals("CITY") ||componentModel.getLabel().equals("STATE")){
                    et12.setLayoutParams(paramsCityState);
                    linearLayoutCityState.addView(et12);
                    if(componentModel.getLabel().equals("STATE")){
                        parent.addView(linearLayoutCityState);
                        index++;
                    }
                }else {

                    parent.addView(et12);
                    index++;
                }
            }
            else if(componentModel.getType().equals("togglebutton")) {
                ToggleButton toggle= new ToggleButton(getActivity());
                toggle.setLayoutParams(matchWidthParams);
                toggle.setHint(componentModel.getLabel());
                parent.addView(toggle);

            }
            else if(componentModel.getType().equals("text")){
                TextView tv1 = new TextView(getActivity());
                tv1.setLayoutParams(matchWidthParams);
                tv1.setText(componentModel.getLabel());
                tv1.setGravity(Gravity.CENTER);
                parent.addView(tv1);
                index++;
            }


        }
        ViewGroup viewGroup = (ViewGroup) view;
        viewGroup.addView(parent);
    }


    Button.OnClickListener btnclick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v.getTag().equals("next")){
//                ((DemographicsActivity)getActivity()).setCurrentItem (1, true);
            }
        }

    };
}
