package com.carecloud.carepayandroid.responsibility;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.carecloud.carepaylibray.ApplicationWorkFlow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Builds dynamically the layout
 */
public class ResponsibilityLayoutRenderer {

    private Context mContext;
    private ArrayList<ScreenComponentModel> mComponents;
    private ArrayList<View> mViews;
    private int mCompCount = 0; // the index of the components

    public ResponsibilityLayoutRenderer(Context context) {
        mContext = context;
        mComponents = ApplicationWorkFlow.Instance().getResponsabScreenModel().getComponentModels();
        mViews = new ArrayList<>();
    }

    public View createLayout() {
        // scroll view
        ScrollView mRoot = new ScrollView(mContext);
        mRoot.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                          FrameLayout.LayoutParams.MATCH_PARENT));

        // main layout container
        LinearLayout mainLl = new LinearLayout(mContext);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                                           FrameLayout.LayoutParams.MATCH_PARENT);
        llParams.setMargins(10, 10, 10, 10);
        mainLl.setLayoutParams(llParams);
        mainLl.setOrientation(LinearLayout.VERTICAL);
        mainLl.setGravity(Gravity.CENTER);

        // components
        // doctor
        TextView tvDoctor = new TextView(mContext);
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
        TextView tvCostLabel = new TextView(mContext);
        LinearLayout.LayoutParams tvCostLabelLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                           ViewGroup.LayoutParams.WRAP_CONTENT);
        tvCostLabelLp.setMargins(10, 70, 10, 0);
        tvCostLabel.setLayoutParams(tvCostLabelLp);
        tvCostLabel.setTextSize(20);
        mainLl.addView(tvCostLabel);
        mViews.add(tvCostLabel);
        mCompCount++;

        // cost
        TextView tvCost = new TextView(mContext);
        LinearLayout.LayoutParams tvCostLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                           ViewGroup.LayoutParams.WRAP_CONTENT);
        tvCostLp.setMargins(10, 10, 10, 0);
        tvCost.setLayoutParams(tvCostLp);
        tvCost.setTextSize(50);
        mainLl.addView(tvCost);
        mViews.add(tvCost);
        mCompCount++;

        TableLayout tableLayout = new TableLayout(mContext);
        LinearLayout.LayoutParams tlLp = new LinearLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                                                       TableLayout.LayoutParams.WRAP_CONTENT);
        tlLp.setMargins(20, 40, 20, 0);
        tableLayout.setStretchAllColumns(true);
        tableLayout.setLayoutParams(tlLp);

        TableRow rowPrevBal = new TableRow(mContext);
        TableRow.LayoutParams rowLp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        rowPrevBal.setLayoutParams(rowLp);
        // previous balance label
        TextView tvPrevBalLabel = new TextView(mContext);
        rowPrevBal.addView(tvPrevBalLabel);
        mViews.add(tvPrevBalLabel);
        mCompCount++;
        // previous balance
        TextView tvBal = new TextView(mContext);
        tvBal.setGravity(Gravity.RIGHT);
        rowPrevBal.addView(tvBal);
        mViews.add(tvBal);
        mCompCount++;

        TableRow rowInsCoPay = new TableRow(mContext);
        TableRow.LayoutParams rowInsCoPayLp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        rowInsCoPay.setLayoutParams(rowInsCoPayLp);
        // label insurance copay
        TextView tvLabelInsLabel = new TextView(mContext);
        rowInsCoPay.addView(tvLabelInsLabel);
        mViews.add(tvLabelInsLabel);
        mCompCount++;
        // insurance copay
        TextView tvInsCoPay = new TextView(mContext);
        tvInsCoPay.setGravity(Gravity.RIGHT);
        rowInsCoPay.addView(tvInsCoPay);
        mViews.add(tvInsCoPay);
        mCompCount++;
        tableLayout.addView(rowPrevBal);
        tableLayout.addView(rowInsCoPay);
        mainLl.addView(tableLayout);

        // sign and pay
        Button btnPay = new Button(mContext);
        LinearLayout.LayoutParams btnPayLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                           ViewGroup.LayoutParams.WRAP_CONTENT);
        btnPayLp.setMargins(0, 50, 0, 0);
        btnPay.setLayoutParams(btnPayLp);
        btnPay.setTextSize(20);
        mainLl.addView(btnPay);
        mViews.add(btnPay);
        mCompCount++;

        assert(mCompCount == 8);

        mRoot.addView(mainLl);

        // populate views
        populateViews();

        return mRoot;
    }

    private void populateViews() {
        for(int i = 0; i < mCompCount; i++) {
            ScreenComponentModel comp = mComponents.get(i);
            View view = mViews.get(i);
            String label = comp.getLabel();
            if(view instanceof Button) {
                ((Button) view).setText(label);
            } else if(view instanceof TextView) {
                ((TextView) view).setText(label);
            } // etc
        }
    }
}
