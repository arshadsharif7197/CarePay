package com.carecloud.carepaylibray.fragments.demographics;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.fragments.UpdatesDialogFragment;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;

import java.util.ArrayList;
import java.util.List;

public class MoreDetailsFragment extends Fragment {

    List<String> getUpdateItemList = new ArrayList<String>();
    List<String> selectedUpdateItemList = new ArrayList<String>();



    public static final MoreDetailsFragment newInstance(Bundle bundle) {
        MoreDetailsFragment fragment = new MoreDetailsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("MoreDetailsFragment");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.setLayoutParams(matchWidthParams);

        LinearLayout parent = new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(20, 20, 0, 20);
//        LinearLayout.LayoutParams LayoutParamsview = new AppBarLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ScreenModel screenModel = ApplicationWorkflow.Instance().getDemographicsMoreDetailsScreenModel();
        getUpdateItemList =ApplicationWorkflow.Instance().getUpdatesDataModel();
        int index = 0;
        for (ScreenComponentModel componentModel : screenModel.getComponentModels()) {
            if (componentModel.getType().equals("image")|| componentModel.getType().equals("ImageView")) {
                ImageView image = new ImageView(getActivity());
                image.setImageResource(R.drawable.icn_signup_illustration_step_4_more);
                image.setLayoutParams(matchWidthParams);
                parent.addView(image,index,matchWidthParams);


            }else if (componentModel.getType().equals("heading")) {
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
                index++;}

            else if(componentModel.getType().equals("button")){

                Button button =  new Button(getActivity());

                Button select =  new Button(getActivity());
                              select.setText(componentModel.getLabel());
                               select.setLayoutParams(matchWidthParams);
                               parent.addView(select);
                               select.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                       public void onClick(View view) {
                                               FragmentManager fm = getFragmentManager();
                                               UpdatesDialogFragment dialogFragment = new UpdatesDialogFragment ();
                                            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
                                                dialogFragment.show(fm,"Updates Fragment");
                                            }

                                    });

                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                childLayoutParams.setMargins(17, 14, 17, 14);
                button.setLayoutParams(childLayoutParams);
                button.setText(componentModel.getLabel());
                button.setGravity(Gravity.CENTER);
                button.setTextSize(21.0f);
                button.setTextColor(ContextCompat.getColor(getActivity(), com.carecloud.carepaylibrary.R.color.charcoal));
                parent.addView(button);
                index++;

                if (componentModel.getLabel().equals("ADD MORE DETAILS")){
                    button.setBackground(ContextCompat.getDrawable(getActivity(), com.carecloud.carepaylibrary.R.color.bright_cerulean));

                    button.setTextColor(ContextCompat.getColor(getActivity(), com.carecloud.carepaylibrary.R.color.white));



                }else  if (componentModel.getLabel().equals("I'LL DO THIS LATER"))
                {
                    button.setBackground(ContextCompat.getDrawable(getActivity(), com.carecloud.carepaylibrary.R.color.white));
                    button.setTextColor(ContextCompat.getColor(getActivity(), com.carecloud.carepaylibrary.R.color.bright_cerulean));
                    button.setBackground(ContextCompat.getDrawable(getActivity(), com.carecloud.carepaylibrary.R.drawable.button_background));

                }}
            else if(componentModel.getType().equals("Inputtext")) {
                TextInputLayout et12 = new TextInputLayout(getActivity());
                EditText et1 = new EditText(getActivity());
                et1.setLayoutParams(matchWidthParams);
                et1.setHint(componentModel.getLabel());
                et12.addView(et1);
                parent.addView(et12);
            }
            else if(componentModel.getType().equals("togglebutton")) {
              /*  ToggleButton toggle= new ToggleButton(getActivity());
                toggle.setLayoutParams(matchWidthParams);
                toggle.setHint(componentModel.getLabel());
                parent.addView(toggle);*/

                final SwitchCompat wantUpdateSwitch= new SwitchCompat (getActivity());
                wantUpdateSwitch.setLayoutParams(matchWidthParams);
                wantUpdateSwitch.setChecked(false);
                wantUpdateSwitch.setClickable(true);
                wantUpdateSwitch.setTag(componentModel.getLabel());
                wantUpdateSwitch.setText(componentModel.getLabel());
                wantUpdateSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchActionForWantUpdateSwitch(wantUpdateSwitch.isChecked());
                    }
                });
                parent.addView(wantUpdateSwitch);

            }
            else if(componentModel.getType().equals("text")){
                TextView tv1 = new TextView(getActivity());
                tv1.setLayoutParams(matchWidthParams);
                tv1.setText(componentModel.getLabel());
                tv1.setGravity(Gravity.CENTER);
                parent.addView(tv1);
            }

            index++;
        }
        scrollView.addView(parent);
        ViewGroup viewGroup = (ViewGroup) view;
        viewGroup.addView(scrollView);
    }

    private void switchActionForWantUpdateSwitch(boolean checked) {
        if (checked){
            selectedUpdateItemList = new ArrayList<String>();
            showAlertDialog();
        }else{
        }
    }

    private void showAlertDialog() {
        final CharSequence[] dialogList = getUpdateItemList.toArray(new CharSequence[getUpdateItemList.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        builderDialog.setTitle("Select Updates");
        int count = dialogList.length;
        boolean[] is_checked = new boolean[count];

        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                        if(isChecked){
                            selectedUpdateItemList.add(getUpdateItemList.get(whichButton));
                        }else{
                            String selectedLable=getUpdateItemList.get(whichButton);
                            for(int index=0;index<selectedUpdateItemList.size();index++){
                                if(selectedLable.equalsIgnoreCase(selectedUpdateItemList.get(index))){
                                    selectedUpdateItemList.remove(index);
                                }
                            }
                        }
                    }
                });

        builderDialog.setPositiveButton("Select",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),selectedUpdateItemList.toString(),Toast.LENGTH_SHORT).show();
                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),""+selectedUpdateItemList.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();
    }

}