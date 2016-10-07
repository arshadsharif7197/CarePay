package com.carecloud.carepaylibray.payment.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseCreditCardFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup chooseCreditCardRadioGroup;
    private Button nextButton, addNewCardButton;
    private Activity mActivity;
    private RadioGroup.LayoutParams radioGroupLayoutParam;
    private String[] creditCardsArray;

    public ChooseCreditCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_credit_card, container, false);
        mActivity = getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(R.string.credit_card);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        radioGroupLayoutParam = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin);
        radioGroupLayoutParam.setMargins(margin, margin, margin, margin);

        // TODO : Change the array to dynamic credit cards info from APIs
        creditCardsArray = new String[]{"VISA 4567 ****", "MasterCard 0123 ****"};

        initilizeViews(view);
        return view;
    }

    private RadioButton getCreditCardRadioButton(String cardInfo, int index) {
        RadioButton radioButtonView = new RadioButton(mActivity);
        radioButtonView.setId(index);
        radioButtonView.setButtonDrawable(null);
        radioButtonView.setBackground(null);
        radioButtonView.setText(cardInfo);
        radioButtonView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.payment_credit_card_button_selector, 0,
                R.drawable.check_box_intake, 0);
        radioButtonView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        radioButtonView.setTextColor(ContextCompat.getColor(mActivity, R.color.slateGray));
        radioButtonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.payment_method_layout_lable_text_size));
        radioButtonView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin));

        return radioButtonView;
    }

    private void initilizeViews(View view) {
        chooseCreditCardRadioGroup = (RadioGroup) view.findViewById(R.id.chooseCreditCardRadioGroup);
        nextButton = (Button) view.findViewById(R.id.nextButton);
        addNewCardButton = (Button) view.findViewById(R.id.addNewCardButton);
        chooseCreditCardRadioGroup.setOnCheckedChangeListener(this);
        nextButton.setOnClickListener(nextButtonListener);
        addNewCardButton.setOnClickListener(addNewCardButtonListener);
        nextButton.setVisibility(View.INVISIBLE);

        for (int i = 0; i < creditCardsArray.length; i++) {
            chooseCreditCardRadioGroup.addView(getCreditCardRadioButton(creditCardsArray[i], i), radioGroupLayoutParam);

            View dividerLineView = new View(mActivity);
            dividerLineView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1
            ));
            dividerLineView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.cadet_gray));
            chooseCreditCardRadioGroup.addView(dividerLineView);
            onSetRadioButtonRegularTypeFace();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        nextButton.setVisibility(View.VISIBLE);
        onSetRadioButtonRegularTypeFace();
        RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
        onSetRadioButtonSemiBoldTypeFace(selectedRadioButton);

        if (selectedRadioButton.getText().toString().equalsIgnoreCase(creditCardsArray[0])) {

        } else if (selectedRadioButton.getText().toString().equalsIgnoreCase(creditCardsArray[1])) {

        }
    }

    private void onSetRadioButtonRegularTypeFace() {
        for (int i = 0; i < chooseCreditCardRadioGroup.getChildCount(); i++) {
            if (i % 2 == 0) {
                SystemUtil.setProximaNovaRegularTypeface(this.mActivity, (RadioButton) chooseCreditCardRadioGroup.getChildAt(i));
                ((RadioButton) chooseCreditCardRadioGroup.getChildAt(i)).setTextColor(ContextCompat.getColor(mActivity, R.color.slateGray));
            }
        }
    }

    private void onSetRadioButtonSemiBoldTypeFace(RadioButton radioButton) {
        SystemUtil.setProximaNovaSemiboldTypeface(this.mActivity, radioButton);
        radioButton.setTextColor(ContextCompat.getColor(mActivity, R.color.blue_cerulian));
    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener addNewCardButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}
