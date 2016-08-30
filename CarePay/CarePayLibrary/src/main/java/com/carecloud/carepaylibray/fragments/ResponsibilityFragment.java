package com.carecloud.carepaylibray.fragments;

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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
        return renderer.createLayout1();
    }

    /**
     * Builds dynamically the layout
     */
    public static class ResponsibilityLayoutRenderer {

        private AppCompatActivity               mActivity;
        private ArrayList<ScreenComponentModel> mComponents;
        private ArrayList<View>                 mViews;
        private int mCompCount = 0; // the index of the components

        private Button mPayButton;

        public ResponsibilityLayoutRenderer(AppCompatActivity context) {
            mActivity = context;
            mComponents = ApplicationWorkflow.Instance().getResponsabScreenModel().getComponentModels();
            mViews = new ArrayList<>();
        }

        public View createLayout() {
            Typeface typeProxima = Typeface.createFromAsset(mActivity.getAssets(), "fonts/ProximaNova-Reg.otf");

            // scroll view
            ScrollView mRoot = new ScrollView(mActivity);
            mRoot.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                               FrameLayout.LayoutParams.MATCH_PARENT));

            // main layout container
            LinearLayout mainLl = new LinearLayout(mActivity);
            LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                                               FrameLayout.LayoutParams.MATCH_PARENT);
            llParams.setMargins(10, 10, 10, 10);
            mainLl.setLayoutParams(llParams);
            mainLl.setOrientation(LinearLayout.VERTICAL);
            mainLl.setGravity(Gravity.CENTER);

            // components
            // doctor
            TextView tvDoctor = new TextView(mActivity);
            LinearLayout.LayoutParams tvDoctorLayoutParams
                    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT);
            tvDoctorLayoutParams.setMargins(0, 10, 0, 0);
            tvDoctor.setLayoutParams(tvDoctorLayoutParams);
            tvDoctor.setAllCaps(true);
            mainLl.addView(tvDoctor);
            mViews.add(tvDoctor);
            mCompCount++;

            // cost label
            TextView tvCostLabel = new TextView(mActivity);
            LinearLayout.LayoutParams tvCostLabelLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvCostLabelLp.setMargins(10, 70, 10, 0);
            tvCostLabel.setLayoutParams(tvCostLabelLp);
            tvCostLabel.setTypeface(typeProxima);
            tvCostLabel.setTextSize(25);
            mainLl.addView(tvCostLabel);
            mViews.add(tvCostLabel);
            mCompCount++;

            // cost
            TextView tvCost = new TextView(mActivity);
            LinearLayout.LayoutParams tvCostLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                               ViewGroup.LayoutParams.WRAP_CONTENT);
            tvCostLp.setMargins(10, 10, 10, 0);
            tvCost.setLayoutParams(tvCostLp);
            tvCost.setTypeface(typeProxima);
            tvCost.setTextSize(70);
            mainLl.addView(tvCost);
            mViews.add(tvCost);
            mCompCount++;

            TableLayout tableLayout = new TableLayout(mActivity);
            LinearLayout.LayoutParams tlLp = new LinearLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                                                           TableLayout.LayoutParams.WRAP_CONTENT);
            tlLp.setMargins(20, 40, 20, 0);
            tableLayout.setStretchAllColumns(true);
            tableLayout.setLayoutParams(tlLp);

            TableRow rowPrevBal = new TableRow(mActivity);
            TableRow.LayoutParams rowLp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            rowPrevBal.setLayoutParams(rowLp);
            // previous balance label
            TextView tvPrevBalLabel = new TextView(mActivity);
            rowPrevBal.addView(tvPrevBalLabel);
            mViews.add(tvPrevBalLabel);
            mCompCount++;
            // previous balance
            TextView tvBal = new TextView(mActivity);
            tvBal.setGravity(Gravity.RIGHT);
            rowPrevBal.addView(tvBal);
            mViews.add(tvBal);
            mCompCount++;

            TableRow rowInsCoPay = new TableRow(mActivity);
            TableRow.LayoutParams rowInsCoPayLp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            rowInsCoPay.setLayoutParams(rowInsCoPayLp);
            // label insurance copay
            TextView tvLabelInsLabel = new TextView(mActivity);
            rowInsCoPay.addView(tvLabelInsLabel);
            mViews.add(tvLabelInsLabel);
            mCompCount++;
            // insurance copay
            TextView tvInsCoPay = new TextView(mActivity);
            tvInsCoPay.setGravity(Gravity.RIGHT);
            rowInsCoPay.addView(tvInsCoPay);
            mViews.add(tvInsCoPay);
            mCompCount++;
            tableLayout.addView(rowPrevBal);
            tableLayout.addView(rowInsCoPay);
            mainLl.addView(tableLayout);

            // sign and pay
            Button btnPay = new Button(mActivity);
            LinearLayout.LayoutParams btnPayLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                               ViewGroup.LayoutParams.WRAP_CONTENT);
            btnPayLp.setMargins(0, 50, 0, 0);
            btnPay.setLayoutParams(btnPayLp);
            btnPay.setTextSize(20);
            btnPay.setTypeface(typeProxima);
            btnPay.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));
            mainLl.addView(btnPay);
            mViews.add(btnPay);
            mCompCount++;

            mRoot.addView(mainLl);

            // populate views
            populateViews();

            return mRoot;
        }

        public View createLayout1() {
            // typeface
            Typeface typeProxima = Typeface.createFromAsset(mActivity.getAssets(), "fonts/ProximaNova-Reg.otf");

            // create the root container
            ViewGroup.LayoutParams matchParentLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                    ViewGroup.LayoutParams.MATCH_PARENT);
            ViewGroup.LayoutParams wrapHeightLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                            ViewGroup.LayoutParams.WRAP_CONTENT);

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
            toolbar.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));
            mActivity.setSupportActionBar(toolbar);
            // change the color of up button
            // todo get assets
//            Drawable upArrow = ContextCompat.getDrawable(mActivity, R.drawable.abc_ic_ab_back_material);
//            if (upArrow != null) {
//                Log.v("Renderer", "back arrow");
//                upArrow.setColorFilter(ContextCompat.getColor(mActivity, R.color.white), PorterDuff.Mode.CLEAR);
//            } else {
//                Log.v("Renderer", "null back arrow");
//            }
            ActionBar actionBar = mActivity.getSupportActionBar();
//            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setDisplayHomeAsUpEnabled(true);


            // create total balance container
            LinearLayout llTotalContainer = new LinearLayout(mActivity);
            llTotalContainer.setLayoutParams(wrapHeightLp);
            llTotalContainer.setOrientation(LinearLayout.VERTICAL);
            llTotalContainer.setPadding(0, 20, 0, 60); // todo externalize
            llTotalContainer.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));

            // create balance details container
            LinearLayout llBalanceDetailsContainer = new LinearLayout(mActivity);
            llBalanceDetailsContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams llBalDetContLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                     ViewGroup.LayoutParams.WRAP_CONTENT);
            llBalDetContLp.setMargins(60, 100, 60,  100); // todo externalize
            llBalanceDetailsContainer.setLayoutParams(llBalDetContLp);

            // create 'other components' container
            LinearLayout llOtherCompContainer = new LinearLayout(mActivity);
            llOtherCompContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams llOtherCompContLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                        ViewGroup.LayoutParams.WRAP_CONTENT);
            llOtherCompContLp.setMargins(120, 20, 120, 10); // todo externalize
            llOtherCompContainer.setLayoutParams(llOtherCompContLp);

            ScreenComponentModel currentCompModel; // holds the current layout component to be added
            int componentsCount = mComponents.size();
            int i = 0;
            while(i < componentsCount) {
                currentCompModel = mComponents.get(i);
                if(i == 0 && currentCompModel.getType().equals("text")) {
                    // first label encountered; place it in the toolbar
                    // todo replace with call to the custom component  factory
                    TextView tvLabelTotal = new TextView(mActivity);
                    tvLabelTotal.setLayoutParams(wrapHeightLp);
                    tvLabelTotal.setGravity(Gravity.CENTER_HORIZONTAL);
                    tvLabelTotal.setTypeface(typeProxima);
                    tvLabelTotal.setTextSize(20); // todo externalize
                    tvLabelTotal.setTextColor(mActivity.getResources().getColor(R.color.white));
                    tvLabelTotal.setText(currentCompModel.getLabel());
                    llTotalContainer.addView(tvLabelTotal);

                    // fetch the next; it should be a textValue
                    ++i;
                    currentCompModel = mComponents.get(i);
                    if(currentCompModel.getType().equals("textValue")) {
                        // add it to the toolbar as well
                        // todo replace with call to the component factory
                        TextView tvValueTotal = new TextView(mActivity);
                        tvValueTotal.setLayoutParams(wrapHeightLp);
                        tvValueTotal.setGravity(Gravity.CENTER_HORIZONTAL);
                        tvValueTotal.setTypeface(typeProxima);
                        tvValueTotal.setTextColor(mActivity.getResources().getColor(R.color.white));
                        tvValueTotal.setTextSize(55); // todo externalize
                        tvValueTotal.setText(currentCompModel.getLabel());
                        llTotalContainer.addView(tvValueTotal);
                        ++i;
                    }
                } else if(currentCompModel.getType().equals("text")) {
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
                    LinearLayout.LayoutParams tvDetailLabelLp = new LinearLayout.LayoutParams(0,
                                                                                              ViewGroup.LayoutParams.WRAP_CONTENT);
                    tvDetailLabelLp.weight = 1;
                    tvDetailLabel.setLayoutParams(tvDetailLabelLp);
                    tvDetailLabel.setTypeface(typeProxima);
                    tvDetailLabel.setText(currentCompModel.getLabel());
                    tvDetailLabel.setGravity(Gravity.START);
                    detailLl.addView(tvDetailLabel);
                    ++i;
                    currentCompModel = mComponents.get(i);
                    if(currentCompModel.getType().equals("textValue")) {
                        // add the value view
                        // todo replace with call to the factory
                        TextView tvDetailValue = new TextView(mActivity);
                        LinearLayout.LayoutParams tvDetailValueLp = new LinearLayout.LayoutParams(0,
                                                                                                  ViewGroup.LayoutParams.WRAP_CONTENT);
                        tvDetailValueLp.weight = 1;
                        tvDetailValue.setLayoutParams(tvDetailValueLp);
                        tvDetailValue.setTypeface(typeProxima);
                        tvDetailValue.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
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
                        separator.setBackgroundColor(mActivity.getResources().getColor(R.color.light_gray));
                        llBalanceDetailsContainer.addView(separator);
                    }
                } else if(currentCompModel.getType().equals("button")){
                    // add the button
                    // todo replace with call to the factory
                    mPayButton = new Button(mActivity);
                    mPayButton.setLayoutParams(wrapHeightLp);
                    mPayButton.setTextColor(mActivity.getResources().getColor(R.color.white));
                    mPayButton.setTypeface(typeProxima);
                    mPayButton.setText(currentCompModel.getLabel());
                    mPayButton.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));
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

        private void populateViews() {
            for (int i = 0; i < mCompCount; i++) {
                ScreenComponentModel comp = mComponents.get(i);
                View view = mViews.get(i);
                String label = comp.getLabel();
                if (view instanceof Button) {
                    ((Button) view).setText(label);
                } else if (view instanceof TextView) {
                    ((TextView) view).setText(label);
                } // etc
            }
        }

        public ArrayList<View> getViews() {
            return mViews;
        }

        public int getCount() {
            return mCompCount;
        }
    }

}
