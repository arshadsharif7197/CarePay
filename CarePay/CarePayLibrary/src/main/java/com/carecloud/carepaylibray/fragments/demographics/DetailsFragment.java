package com.carecloud.carepaylibray.fragments.demographics;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;

import java.util.ArrayList;
import java.util.List;


public class DetailsFragment extends Fragment {
    ScreenModel getDemographicsDetailsScreenModel =new ScreenModel();
    ArrayList<ScreenComponentModel> componentModels= new ArrayList<ScreenComponentModel>();
    List<String> raceData = new ArrayList<String>();
    List<String> ethnicityData = new ArrayList<String>();
    List<String> languageData = new ArrayList<String>();

    String selectedRace =null;


    public static final DetailsFragment newInstance(Bundle bundle) {
        DetailsFragment fragment = new DetailsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("DetailsFragment");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Scan Documents");


        ScrollView scrollView = new ScrollView(getActivity());
        ViewGroup.LayoutParams scrollViewLayoutparams = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollView.setLayoutParams(scrollViewLayoutparams);

        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout parent = new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(20, 10, 20, 10);
        scrollView.addView(parent);


        ScreenModel screenModel = ApplicationWorkflow.Instance().getDemographicsDetailsScreenModel();

        ethnicityData =ApplicationWorkflow.Instance().getethincityDataModel();
        languageData=ApplicationWorkflow.Instance().getlanguageDataModel();

        getActivity().setTitle(screenModel.getName());
        int index = 0;
        for (ScreenComponentModel componentModel : screenModel.getComponentModels()) {
            if (componentModel.getType().equals("image") || componentModel.getType().equals("ImageView")) {
                ImageView mIVCard = new ImageView(getActivity());
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                childLayoutParams.setMargins(33, 90, 57, 14);
                mIVCard.setLayoutParams(childLayoutParams);
                mIVCard.setImageResource(R.drawable.icn_signup_illustration_step_2_details);
                mIVCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
                mIVCard.setLayoutParams(childLayoutParams);
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

            } else if (componentModel.getType().equals("buttonWithImage")) {
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
                camera.setImageResource(R.drawable.icn_placeholder_user_profile);

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
            }else if (componentModel.getType().equals("titleText")) {
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
                childLayoutParams.setMargins(17, 20, 0, 17);
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
                selectText.setTag(componentModel.getLabel());
                selectText.setGravity(Gravity.CENTER_HORIZONTAL);
                selectText.setTextSize(14.0f);
                selectText.setTextColor(ContextCompat.getColor(getActivity(), R.color.bright_cerulean));
                selectText.setClickable(true);
                selectText.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                showAlertDialogWithListview( String.valueOf(((TextView) view).getTag()));


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

                EditText inputText = new EditText(getActivity());
                LinearLayout.LayoutParams inputTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                inputText.setLayoutParams(inputTextLayoutParams);
                inputTextLayoutParams.setMargins(0, 0, 0, 60);
                inputText.setHint(componentModel.getLabel());
                inputTextLayoutParams.gravity = Gravity.LEFT;
                // inputText.setText(componentModel.getLabel());
                inputText.setTextSize(17.0f);
                inputText.setTextColor(ContextCompat.getColor(getActivity(), R.color.light_gray));

                parent.addView(inputText);
                index++;
            } else if (componentModel.getType().equals("button")) {

                Button button = new Button(getActivity());
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                button.setLayoutParams(buttonParams);
                buttonParams.setMargins(17, 56, 17, 14);
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



    public void showAlertDialogWithListview( String type )  {

        if(type.equals("Race")){
            raceData=ApplicationWorkflow.Instance().getRaceDataModel();}
        else if(type.equals("Ethnicity")){
            raceData=ApplicationWorkflow.Instance().getethincityDataModel();}
        else if(type.equals("Preferred Language")){
            raceData=ApplicationWorkflow.Instance().getlanguageDataModel();}


        //Create sequence of items
        final CharSequence[] raceDataArray = raceData.toArray(new String[raceData.size()]);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Race");
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setItems(raceDataArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                selectedRace = raceDataArray[item].toString();  //Selected item in listview
                Toast.makeText(getActivity(), selectedRace,Toast.LENGTH_SHORT).show();
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }
}





