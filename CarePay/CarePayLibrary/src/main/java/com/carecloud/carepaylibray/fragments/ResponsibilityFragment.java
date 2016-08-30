package com.carecloud.carepaylibray.fragments;

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
        getActivity().setTitle("Responsibility"); // todo get title from component
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
            toolbar.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));
            mActivity.setSupportActionBar(toolbar);
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // create total balance container
            LinearLayout totalContainerLl = new LinearLayout(mActivity);
            totalContainerLl.setLayoutParams(wrapHeightLp);
            totalContainerLl.setOrientation(LinearLayout.VERTICAL);
            totalContainerLl.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));

            // create balance details container
            LinearLayout llBalanceDetails = new LinearLayout(mActivity);
            llBalanceDetails.setOrientation(LinearLayout.VERTICAL);
            llBalanceDetails.setLayoutParams(wrapHeightLp);

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
                    tvLabelTotal.setText(currentCompModel.getLabel());
                    totalContainerLl.addView(tvLabelTotal);
                    // fetch the next; it should be a textValue
                    ++i;
                    currentCompModel = mComponents.get(i);
                    if(currentCompModel.getType().equals("textValue")) {
                        // add it to the toolbar as well
                        // todo replace with call to the component factory
                        TextView tvValueTotal = new TextView(mActivity);
                        tvValueTotal.setLayoutParams(wrapHeightLp);
                        tvValueTotal.setText(currentCompModel.getLabel());
                        totalContainerLl.addView(tvValueTotal);
                        ++i;
                    }
                } else if(currentCompModel.getType().equals("text")) {
                    // create a horizontal LinearLayout for (label, value)
                    LinearLayout detailLl = new LinearLayout(mActivity);
                    detailLl.setLayoutParams(wrapHeightLp);
                    // following component go by pairs (label, value) in balance details container
                    // create the label view
                    // todo replace with call to the factory
                    TextView tvDetailLabel = new TextView(mActivity);
                    tvDetailLabel.setLayoutParams(wrapHeightLp);
                    tvDetailLabel.setText(currentCompModel.getLabel());
                    detailLl.addView(tvDetailLabel);
                    ++i;
                    currentCompModel = mComponents.get(i);
                    if(currentCompModel.getType().equals("textValue")) {
                        // add the value view
                        // todo replace with call to the factory
                        TextView tvDetailValue = new TextView(mActivity);
                        tvDetailValue.setLayoutParams(wrapHeightLp);
                        tvDetailLabel.setText(currentCompModel.getLabel());
                        detailLl.addView(tvDetailValue);
                        ++i;

                        // add the horizontal linear layout
                        // add (label, value) pair to details container
                        llBalanceDetails.addView(detailLl);
                        // add separator

                    }
                } else if(currentCompModel.getType().equals("button")){
                    // add the button
                    // todo replace with call to the factory
                    mPayButton = new Button(mActivity);
                    mPayButton.setLayoutParams(wrapHeightLp);
                    mPayButton.setText(currentCompModel.getLabel());
                    ++i;
                } else {
                    // all other components will be added directly to the main linear layout
                    ++i;
                }
            }
            // add containers to the root
            mainLl.addView(toolbar);
            mainLl.addView(totalContainerLl);
            mainLl.addView(llBalanceDetails);
            mainLl.addView(mPayButton);
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
