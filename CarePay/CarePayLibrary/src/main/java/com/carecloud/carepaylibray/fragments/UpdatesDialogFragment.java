package com.carecloud.carepaylibray.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;

/**
 * Created by anil_kairamkonda on 8/28/2016.
 */
public class UpdatesDialogFragment extends DialogFragment {
    private ScreenModel screenModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout parent = new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(20, 50, 20, 20);

        screenModel = ApplicationWorkflow.Instance().getUpdatesDialogScreenModel();

        LinearLayout linearLayout4 = new LinearLayout(getActivity());
        linearLayout4.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout4.setGravity(Gravity.BOTTOM |Gravity.RIGHT);


        for (final ScreenComponentModel componentModel : screenModel.getComponentModels()) {
            if (componentModel.getType().equals("text")) {
                TextView selectUpdatesTextview = new TextView(getActivity());
                selectUpdatesTextview.setLayoutParams(matchWidthParams);
                selectUpdatesTextview.setText(componentModel.getLabel());
                selectUpdatesTextview.setPadding(20, 10, 0, 10);
                selectUpdatesTextview.setTypeface( Typeface.createFromAsset(getActivity().getAssets(),"fonts/GothamRnd-Medium.otf"));
                selectUpdatesTextview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                parent.addView(selectUpdatesTextview);


            } else if (componentModel.getType().equals("checkbox")) {
                final CheckBox checkBox = new CheckBox(getActivity());
                checkBox.setPadding(0, 10, 0, 0);
                checkBox.setButtonDrawable(R.drawable.cell_checkbox_off);
                final TextView textView = new TextView(getActivity());

                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout linearLayout2 = new LinearLayout(getActivity());
                linearLayout2.setLayoutParams(itemParams);
                linearLayout2.setPadding(20,15,0,15);
                linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                textView.setTypeface( Typeface.createFromAsset(getActivity().getAssets(),"fonts/ProximaNova-Reg.otf"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setText(componentModel.getLabel());
                textView.setPadding(0, 15, 0, 15);
                textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                        if (isChecked) {
                            checkBox.setButtonDrawable(R.drawable.cell_checkbox_on);
                            textView.setTextColor(getResources().getColor(R.color.rich_electric_blue));
                        } else {
                            textView.setTextColor(getResources().getColor(R.color.slateGray));
                            checkBox.setButtonDrawable(R.drawable.cell_checkbox_off);
                        }
                    }
                });

                checkBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));
                checkBox.setGravity(Gravity.END);
                linearLayout2.addView(textView);
                linearLayout2.addView(checkBox);
                linearLayout2.setGravity(Gravity.LEFT);

                parent.addView(linearLayout2);

            } else if (componentModel.getType().equals("button")) {
                Button selectButton = new Button(getActivity());
                selectButton.setText(componentModel.getLabel());
                selectButton.setTextColor(Color.parseColor("#1f9bde"));
                selectButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                selectButton.setPadding(20, 10, 10, 10);
                selectButton.setBackgroundColor(Color.TRANSPARENT);
                linearLayout4.addView(selectButton);
            }
        }
        if (linearLayout4.getChildCount() > 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.setMargins(0, 114, 0, 0);
            View view = new View(getActivity());
            view.setLayoutParams(params);
            view.setBackgroundColor(getResources().getColor(R.color.slateGray));
            parent.addView(view);
            parent.addView(linearLayout4);
        }
        return parent;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }
}


