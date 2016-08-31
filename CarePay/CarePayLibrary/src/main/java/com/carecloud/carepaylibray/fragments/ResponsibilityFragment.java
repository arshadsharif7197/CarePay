package com.carecloud.carepaylibray.fragments;

import android.content.Context;
import android.os.Build;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ResponsibilityLayoutRenderer renderer = new ResponsibilityLayoutRenderer((AppCompatActivity) getActivity());
        return renderer.createLayout();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.add("Test");
        super.onCreateOptionsMenu(menu, inflater);
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
        private LinearLayout.LayoutParams zeroHeightLp  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        private Typeface typeProxima;
        private Typeface typeGothamRounded;
        private Typeface typeProximaSemiBold;
        private int      colorPrimary;
        private int      colorWhite;
        private int      colorYellowGreen;
        private int      colorGlitter;
        private int      colorCharcoal;
        private Typeface typeGothamRoundedBook;
        private ArrayList<View> views = new ArrayList<>();
        private int viewsCount;


        public ResponsibilityLayoutRenderer(Context context) {
            mActivity = (AppCompatActivity) context;
            mComponents = ApplicationWorkflow.Instance().getResponsabScreenModel().getComponentModels();
            typeProxima = Typeface.createFromAsset(mActivity.getAssets(), "fonts/ProximaNova-Reg.otf");
            typeGothamRounded = Typeface.createFromAsset(mActivity.getAssets(), "fonts/GothamRnd-Medium.otf");
            typeProximaSemiBold = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Proxima_Nova_Semibold.otf");
            typeGothamRoundedBook = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Gotham-rounded-book.otf");
            colorPrimary = mActivity.getResources().getColor(R.color.colorPrimary);
            colorWhite = mActivity.getResources().getColor(R.color.white);
            colorYellowGreen = mActivity.getResources().getColor(R.color.yellowGreen);
            colorGlitter = mActivity.getResources().getColor(R.color.glitter);
            colorCharcoal = mActivity.getResources().getColor(R.color.charcoal);
        }

        /**
         * Creates the layout
         *
         * @return The view containing the layout
         */
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
            llTotalContainerLp.weight = 3;
            LinearLayout llTotalContainer = createVerticalLinearLayout(llTotalContainerLp);
            llTotalContainer.setPadding(0, 36, 0, 37); // todo externalize
            llTotalContainer.setBackgroundColor(colorPrimary);

            // create balance details container
            LinearLayout.LayoutParams llBalanceDetailsContainerLp = new LinearLayout.LayoutParams(zeroHeightLp);
            llBalanceDetailsContainerLp.weight = 4;
            llBalanceDetailsContainerLp.setMargins(60, 120, 60, 120); // todo externalize
            LinearLayout llBalanceDetailsContainer = createVerticalLinearLayout(llBalanceDetailsContainerLp);

            // create 'other components' container
            LinearLayout.LayoutParams llOtherCompContainerLp = new LinearLayout.LayoutParams(zeroHeightLp);
            llOtherCompContainerLp.weight = 1;
            llOtherCompContainerLp.setMargins(30, 20, 30, 10);  // todo externalize
            LinearLayout llOtherCompContainer = createVerticalLinearLayout(llOtherCompContainerLp);

            ScreenComponentModel currentCompModel; // holds the current layout component to be added
            int componentsCount = mComponents.size();
            int i = 0;
            viewsCount = 0;
            views.clear();
            while (i < componentsCount) {
                currentCompModel = mComponents.get(i);
                if (i == 0 && currentCompModel.getType().equals("text")) {
                    // first label encountered; place it in the toolbar
                    TextView tvLabelTotal = createTextView(wrapHeightLp, 21, colorGlitter, typeGothamRoundedBook, currentCompModel);
                    llTotalContainer.addView(tvLabelTotal);
                    views.add(tvLabelTotal); // for tests
                    ++viewsCount;

                    // fetch the next; it should be a textValue
                    ++i;
                    currentCompModel = mComponents.get(i);
                    if (currentCompModel.getType().equals("textValue")) {
                        // add it to the toolbar as well
                        TextView tvValueTotal = createTextView(wrapHeightLp, 73, colorWhite, typeGothamRounded, currentCompModel); // todo externalize fontSize
                        llTotalContainer.setPadding(0, 16, 0, 0);
                        llTotalContainer.addView(tvValueTotal);
                        views.add(tvValueTotal);
                        ++viewsCount;
                        ++i;
                    }
                } else if (currentCompModel.getType().equals("text")) {
                    // create a horizontal LinearLayout for (label, value)
                    LinearLayout detailLl = new LinearLayout(mActivity);
                    detailLl.setWeightSum(4);
                    LinearLayout.LayoutParams detailLlLp = new LinearLayout.LayoutParams(wrapHeightLp);
                    detailLlLp.setMargins(0, 30, 0, 0); // todo externalize
                    detailLl.setLayoutParams(detailLlLp);
                    detailLl.setOrientation(LinearLayout.HORIZONTAL);
                    // following component go by pairs (label, value) in balance details container
                    // create the label view
                    // todo replace with call to the factory
                    LinearLayout.LayoutParams tvDetailLabelLp = new LinearLayout.LayoutParams(zeroWidthLp);
                    tvDetailLabelLp.weight = 3;

                    TextView tvDetailLabel = createTextView(tvDetailLabelLp,
                                                            17, colorCharcoal, typeProxima,
                                                            currentCompModel); // todo get defaults
                    tvDetailLabel.setGravity(Gravity.START);
                    detailLl.addView(tvDetailLabel);
                    views.add(tvDetailLabel);
                    ++viewsCount;
                    ++i;
                    currentCompModel = mComponents.get(i);
                    if (currentCompModel.getType().equals("textValue")) {
                        // add the value view
                        // todo replace with call to the factory
                        LinearLayout.LayoutParams tvDetailValueLp = new LinearLayout.LayoutParams(zeroWidthLp);
                        tvDetailValueLp.weight = 1;
                        TextView tvDetailValue = createTextView(tvDetailValueLp,
                                                                14, colorPrimary, typeProximaSemiBold,
                                                                currentCompModel);
                        tvDetailValue.setGravity(Gravity.END);
                        detailLl.addView(tvDetailValue);
                        views.add(tvDetailValue);
                        ++viewsCount;
                        ++i;

                        // add the horizontal linear layout
                        // add (label, value) pair to details container
                        llBalanceDetailsContainer.addView(detailLl);
                    }
                } else if (currentCompModel.getType().equals("button")) {
                    // add the button
                    // todo replace with call to the factory
                    // add the button to 'other components' container
                    mPayButton = createButton(currentCompModel);
                    llOtherCompContainer.addView(mPayButton);
                    views.add(mPayButton);
                    ++viewsCount;
                    ++i;
                } else {
                    // all other components will be eventually added to the 'other components' container
                    ++viewsCount;
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wrapHeightLp);
            lp.setMargins(0, 0, 0, 27); // to externalize
            payButton.setLayoutParams(lp);
            payButton.setTextColor(colorWhite);
            payButton.setTypeface(typeGothamRounded);
            payButton.setTextSize(15);
            payButton.setText(currentCompModel.getLabel());
            payButton.setBackgroundColor(colorYellowGreen);
            return payButton;
        }

        /**
         * Create a linear layout container
         *
         * @param lp The layout params
         * @return The container
         */
        private LinearLayout createVerticalLinearLayout(LinearLayout.LayoutParams lp) {
            LinearLayout llVertContainer = new LinearLayout(mActivity);
            llVertContainer.setOrientation(LinearLayout.VERTICAL);
            llVertContainer.setLayoutParams(lp);
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
                                        Typeface typeface, ScreenComponentModel currentCompModel) {
            TextView textView = new TextView(mActivity);
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTypeface(typeface);
            if (textSize != -1) {
                textView.setTextSize(textSize); // todo externalize
            }
            textView.setTextColor(color);
            textView.setText(currentCompModel.getLabel());
            return textView;
        }

        /**
         * Create the toolbar
         *
         * @return the toolbar
         */
        private Toolbar createToolbar() {
            Toolbar toolbar = new Toolbar(mActivity);
            LinearLayout.LayoutParams toolbarLp = new LinearLayout.LayoutParams(zeroHeightLp);
            toolbarLp.weight = 1;
            toolbar.setLayoutParams(toolbarLp);
            toolbar.setTitleTextColor(colorWhite);
//            toolbar.setTitle("Responsibility");
//            TextView tvToolbarTitle = new TextView(mActivity);
//            tvToolbarTitle.setText("Responsibility");  // todo get title from component
//            tvToolbarTitle.setTextColor(colorWhite);
//            tvToolbarTitle.setTextSize(20);
//            tvToolbarTitle.setTypeface(typeGothamRounded);
//            toolbar.addView(tvToolbarTitle);
            toolbar.setBackgroundColor(colorPrimary);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(10); // todo externalize
            }
            toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_back);
//            Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.icn_patient_mode_nav_back);
//            toolbar.setOverflowIcon(drawable);
            mActivity.setSupportActionBar(toolbar);
            mActivity.getSupportActionBar().setTitle("Responsibility");
            return toolbar;
        }

        public ArrayList<View> getViews() {
            return views;
        }

        public int getCount() {
            return viewsCount;
        }
    }
}