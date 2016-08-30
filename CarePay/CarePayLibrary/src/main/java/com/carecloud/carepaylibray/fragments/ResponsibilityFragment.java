package com.carecloud.carepaylibray.fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;

import java.util.ArrayList;

/**
 * The fragment corresponding to patient responsibility screen
 */
public class ResponsibilityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ResponsibilityLayoutRenderer renderer = new ResponsibilityLayoutRenderer((AppCompatActivity) getActivity());
        return renderer.createLayout();
    }

    /**
     * Builds dynamically the layout
     */
    public static class ResponsibilityLayoutRenderer {

        private AppCompatActivity               mActivity;
        private ArrayList<ScreenComponentModel> mComponents;
        private Button mPayButton;

        public ResponsibilityLayoutRenderer(AppCompatActivity context) {
            mActivity = context;
            mComponents = ApplicationWorkflow.Instance().getResponsabScreenModel().getComponentModels();
        }

        public View createLayout() {
            // typeface
            Typeface typeProxima = Typeface.createFromAsset(mActivity.getAssets(), "fonts/ProximaNova-Reg.otf");

            // create the root container
            ViewGroup.LayoutParams matchParentLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                              ViewGroup.LayoutParams.MATCH_PARENT);
            ViewGroup.LayoutParams wrapHeightLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                             ViewGroup.LayoutParams.WRAP_CONTENT);
            ViewGroup.LayoutParams zeroWidthLp = new ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            int colorPrimary = mActivity.getResources().getColor(R.color.colorPrimary);
            int colorWhite = ContextCompat.getColor(mActivity, R.color.white);
            int colorGrey = mActivity.getResources().getColor(R.color.light_gray);


            ScrollView root = new ScrollView(mActivity);
            root.setLayoutParams(matchParentLp);

            // create the main linear layout (vertical)
            LinearLayout mainLl = new LinearLayout(mActivity);
            mainLl.setLayoutParams(matchParentLp);
            mainLl.setOrientation(LinearLayout.VERTICAL);

            // create the toolbar
            Toolbar toolbar = new Toolbar(mActivity);
            toolbar.setLayoutParams(wrapHeightLp);
            toolbar.setTitle("");
            toolbar.setBackgroundColor(colorPrimary);
            mActivity.setSupportActionBar(toolbar);
            // change the color of up button
            // todo get assets
            Drawable upArrow = ContextCompat.getDrawable(mActivity, R.drawable.abc_ic_ab_back_material);
            if (upArrow != null) {
                upArrow.setColorFilter(colorWhite, PorterDuff.Mode.CLEAR);
            }
            ActionBar actionBar = mActivity.getSupportActionBar();
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setDisplayHomeAsUpEnabled(true);


            // create total balance container
            LinearLayout llTotalContainer = new LinearLayout(mActivity);
            llTotalContainer.setLayoutParams(wrapHeightLp);
            llTotalContainer.setOrientation(LinearLayout.VERTICAL);
            llTotalContainer.setPadding(0, 20, 0, 60); // todo externalize
            llTotalContainer.setBackgroundColor(colorPrimary);

            // create balance details container
            LinearLayout llBalanceDetailsContainer = new LinearLayout(mActivity);
            llBalanceDetailsContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams llBalDetContLp = new LinearLayout.LayoutParams(wrapHeightLp);
            llBalDetContLp.setMargins(60, 100, 60, 100); // todo externalize
            llBalanceDetailsContainer.setLayoutParams(llBalDetContLp);

            // create 'other components' container
            LinearLayout llOtherCompContainer = new LinearLayout(mActivity);
            llOtherCompContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams llOtherCompContLp = new LinearLayout.LayoutParams(wrapHeightLp);
            llOtherCompContLp.setMargins(120, 20, 120, 10); // todo externalize
            llOtherCompContainer.setLayoutParams(llOtherCompContLp);

            ScreenComponentModel currentCompModel; // holds the current layout component to be added
            int componentsCount = mComponents.size();
            int i = 0;
            while (i < componentsCount) {
                currentCompModel = mComponents.get(i);
                if (i == 0 && currentCompModel.getType().equals("text")) {
                    // first label encountered; place it in the toolbar
                    // todo replace with call to the custom component  factory
                    TextView tvLabelTotal = new TextView(mActivity);
                    tvLabelTotal.setLayoutParams(wrapHeightLp);
                    tvLabelTotal.setGravity(Gravity.CENTER_HORIZONTAL);
                    tvLabelTotal.setTypeface(typeProxima);
                    tvLabelTotal.setTextSize(20); // todo externalize
                    tvLabelTotal.setTextColor(colorWhite);
                    tvLabelTotal.setText(currentCompModel.getLabel());
                    llTotalContainer.addView(tvLabelTotal);

                    // fetch the next; it should be a textValue
                    ++i;
                    currentCompModel = mComponents.get(i);
                    if (currentCompModel.getType().equals("textValue")) {
                        // add it to the toolbar as well
                        // todo replace with call to the component factory
                        TextView tvValueTotal = new TextView(mActivity);
                        tvValueTotal.setLayoutParams(wrapHeightLp);
                        tvValueTotal.setGravity(Gravity.CENTER_HORIZONTAL);
                        tvValueTotal.setTypeface(typeProxima);
                        tvValueTotal.setTextColor(colorWhite);
                        tvValueTotal.setTextSize(55); // todo externalize
                        tvValueTotal.setText(currentCompModel.getLabel());
                        llTotalContainer.addView(tvValueTotal);
                        ++i;
                    }
                } else if (currentCompModel.getType().equals("text")) {
                    // create a horizontal LinearLayout for (label, value)
                    LinearLayout detailLl = new LinearLayout(mActivity);
                    detailLl.setWeightSum(2);
                    LinearLayout.LayoutParams detailLlLp = new LinearLayout.LayoutParams(wrapHeightLp);
                    detailLlLp.setMargins(0, 20, 0, 20); // todo externalize
                    detailLl.setLayoutParams(detailLlLp);
                    detailLl.setOrientation(LinearLayout.HORIZONTAL);
                    // following component go by pairs (label, value) in balance details container
                    // create the label view
                    // todo replace with call to the factory
                    TextView tvDetailLabel = new TextView(mActivity);
                    LinearLayout.LayoutParams tvDetailLabelLp = new LinearLayout.LayoutParams(zeroWidthLp);
                    tvDetailLabelLp.weight = 1;
                    tvDetailLabel.setLayoutParams(tvDetailLabelLp);
                    tvDetailLabel.setTypeface(typeProxima);
                    tvDetailLabel.setText(currentCompModel.getLabel());
                    tvDetailLabel.setGravity(Gravity.START);
                    detailLl.addView(tvDetailLabel);
                    ++i;
                    currentCompModel = mComponents.get(i);
                    if (currentCompModel.getType().equals("textValue")) {
                        // add the value view
                        // todo replace with call to the factory
                        TextView tvDetailValue = new TextView(mActivity);
                        LinearLayout.LayoutParams tvDetailValueLp = new LinearLayout.LayoutParams(zeroWidthLp);
                        tvDetailValueLp.weight = 1;
                        tvDetailValue.setLayoutParams(tvDetailValueLp);
                        tvDetailValue.setTypeface(typeProxima);
                        tvDetailValue.setTextColor(colorPrimary);
                        tvDetailValue.setText(currentCompModel.getLabel());
                        tvDetailValue.setGravity(Gravity.END);
                        detailLl.addView(tvDetailValue);
                        ++i;

                        // add the horizontal linear layout
                        // add (label, value) pair to details container
                        llBalanceDetailsContainer.addView(detailLl);
                        // add separator
                        View separator = new View(mActivity);
                        separator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                             2));
                        separator.setBackgroundColor(colorGrey);
                        llBalanceDetailsContainer.addView(separator);
                    }
                } else if (currentCompModel.getType().equals("button")) {
                    // add the button
                    // todo replace with call to the factory
                    mPayButton = new Button(mActivity);
                    mPayButton.setLayoutParams(wrapHeightLp);
                    mPayButton.setTextColor(colorWhite);
                    mPayButton.setTypeface(typeProxima);
                    mPayButton.setText(currentCompModel.getLabel());
                    mPayButton.setBackgroundColor(colorPrimary);
                    // add the button to 'other components' container
                    llOtherCompContainer.addView(mPayButton);
                    ++i;
                } else {
                    // all other components will be eventually added to the 'other components' container
                    ++i;
                }
            }
            // add containers to the root
            mainLl.addView(toolbar);
            mainLl.addView(llTotalContainer);
            mainLl.addView(llBalanceDetailsContainer);
            mainLl.addView(llOtherCompContainer);
            root.addView(mainLl);

            return root;
        }
    }
}
