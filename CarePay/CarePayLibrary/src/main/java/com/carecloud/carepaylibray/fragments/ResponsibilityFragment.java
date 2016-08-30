package com.carecloud.carepaylibray.fragments;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        private Button                          mPayButton;
        private LinearLayout.LayoutParams matchParentLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                  ViewGroup.LayoutParams.MATCH_PARENT);
        private LinearLayout.LayoutParams wrapHeightLp  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                  ViewGroup.LayoutParams.WRAP_CONTENT);
        private LinearLayout.LayoutParams zeroWidthLp   = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        private LinearLayout.LayoutParams zeroHeightLp   = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        private Typeface typeProxima;
        private Typeface typeGothamRounded;
        private int      colorPrimary;
        private int      colorWhite;
        private int      colorGrey;


        public ResponsibilityLayoutRenderer(AppCompatActivity context) {
            mActivity = context;
            mComponents = ApplicationWorkflow.Instance().getResponsabScreenModel().getComponentModels();
            typeProxima = Typeface.createFromAsset(mActivity.getAssets(), "fonts/ProximaNova-Reg.otf");
            typeGothamRounded = Typeface.createFromAsset(mActivity.getAssets(), "fonts/GothamRnd-Medium.otf");
            colorPrimary = mActivity.getResources().getColor(R.color.colorPrimary);
            colorWhite = ContextCompat.getColor(mActivity, R.color.white);
            colorGrey = mActivity.getResources().getColor(R.color.light_gray);
        }

        public View createLayout() {
            ScrollView root = new ScrollView(mActivity);
            root.setLayoutParams(matchParentLp);
            root.setFillViewport(true);

            // create the main linear layout (vertical)
            LinearLayout mainLl = createVerticalLinearLayout(matchParentLp);
            mainLl.setWeightSum(9);

            // create the toolbar
            Toolbar toolbar = createToolbar();

            // create total balance container
            LinearLayout.LayoutParams llTotalContainerLp = new LinearLayout.LayoutParams(zeroHeightLp);
            llTotalContainerLp.weight = 4;
            LinearLayout llTotalContainer = createVerticalLinearLayout(llTotalContainerLp);
            llTotalContainer.setPadding(0, 20, 0, 60); // todo externalize
            llTotalContainer.setBackgroundColor(colorPrimary);

            // create balance details container
            LinearLayout.LayoutParams llBalanceDetailsContainerLp = new LinearLayout.LayoutParams(zeroHeightLp);
            llBalanceDetailsContainerLp.weight = 3;
            llBalanceDetailsContainerLp.setMargins(60, 120, 60, 120); // todo externalize
            LinearLayout llBalanceDetailsContainer = createVerticalLinearLayout(llBalanceDetailsContainerLp);

            // create 'other components' container
            LinearLayout.LayoutParams llOtherCompContainerLp = new LinearLayout.LayoutParams(zeroHeightLp);
            llOtherCompContainerLp.weight = 1;
            llOtherCompContainerLp.setMargins(150, 20, 150, 10);  // todo externalize
            LinearLayout llOtherCompContainer = createVerticalLinearLayout(llOtherCompContainerLp);

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
//                        TextView tvValueTotal = new TextView(mActivity);
//                        tvValueTotal.setLayoutParams(wrapHeightLp);
//                        tvValueTotal.setGravity(Gravity.CENTER_HORIZONTAL);
//                        tvValueTotal.setTypeface(typeProxima);
//                        tvValueTotal.setTextColor(colorWhite);
//                        tvValueTotal.setTextSize(55); // todo externalize
//                        tvValueTotal.setText(currentCompModel.getLabel());
                        TextView tvValueTotal = createTextView(wrapHeightLp, 55, colorWhite, currentCompModel); // todo externalize fontSize
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
                    LinearLayout.LayoutParams tvDetailLabelLp = new LinearLayout.LayoutParams(zeroWidthLp);
                    tvDetailLabelLp.weight = 1;

                    TextView tvDetailLabel = createTextView(tvDetailLabelLp, -1, -1, currentCompModel); // todo get defaults
                    detailLl.addView(tvDetailLabel);
                    tvDetailLabel.setGravity(Gravity.START);
                    ++i;
                    currentCompModel = mComponents.get(i);
                    if (currentCompModel.getType().equals("textValue")) {
                        // add the value view
                        // todo replace with call to the factory
                        LinearLayout.LayoutParams tvDetailValueLp = new LinearLayout.LayoutParams(zeroWidthLp);
                        tvDetailValueLp.weight = 1;
                        TextView tvDetailValue = createTextView(tvDetailValueLp, -1, colorPrimary, currentCompModel);
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
                    // add the button to 'other components' container
                    mPayButton = createButton(currentCompModel);
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

        /**
         * Creates a button
         *
         * @param currentCompModel The screen model component to build from
         * @return The button
         */
        private Button createButton(ScreenComponentModel currentCompModel) {
            Button payButton = new Button(mActivity);
            payButton.setLayoutParams(wrapHeightLp);
            payButton.setTextColor(colorWhite);
            payButton.setTypeface(typeProxima);
            payButton.setText(currentCompModel.getLabel());
            payButton.setBackgroundColor(colorPrimary);
            return payButton;
        }

        /**
         * Create a linear layout container
         * @param lp The layout params
         * @return The container
         */
        private LinearLayout createVerticalLinearLayout(LinearLayout.LayoutParams lp) {
            LinearLayout llVertContainer = new LinearLayout(mActivity);
            llVertContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams llBalDetContLp = new LinearLayout.LayoutParams(lp);
            llVertContainer.setLayoutParams(llBalDetContLp);
            return llVertContainer;
        }

        /**
         * @param lp               The layout params
         * @param textSize         The text size or -1 to use defaults
         * @param color            The color or -1 to use default
         * @param currentCompModel The screen component model
         * @return The ext view
         */
        private TextView createTextView(ViewGroup.LayoutParams lp, float textSize, int color,
                                        ScreenComponentModel currentCompModel) {
            TextView textView = new TextView(mActivity);
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTypeface(typeProxima);
            if (textSize != -1) {
                textView.setTextSize(textSize); // todo externalize
            }
            if (color != -1) {
                textView.setTextColor(color);
            }
            textView.setText(currentCompModel.getLabel());
            return textView;
        }

        /**
         * Create the toolbar
         * @return the toolbar
         */
        private Toolbar createToolbar() {
            Toolbar toolbar = new Toolbar(mActivity);
            LinearLayout.LayoutParams toolbarLp = new LinearLayout.LayoutParams(zeroHeightLp);
            toolbarLp.weight = 1;
            toolbar.setLayoutParams(toolbarLp);
            TextView tvToolbarTitle = new TextView(mActivity);
            toolbar.setTitle("");
            tvToolbarTitle.setText("Responsibility");  // todo get title from component
            tvToolbarTitle.setTextColor(colorWhite);
            tvToolbarTitle.setTextSize(20);
            tvToolbarTitle.setTypeface(typeGothamRounded);
            toolbar.addView(tvToolbarTitle);
            toolbar.setBackgroundColor(colorPrimary);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(10); // todo externalize
            }
            toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_back);
            mActivity.setSupportActionBar(toolbar);
            return toolbar;
        }
    }
}