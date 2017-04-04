package com.carecloud.carepay.patient.demographics.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;

/**
 * Created by lsoco_user on 11/28/2016.
 */

public class ReviewDemographicsActivity extends BasePatientActivity
        implements DemographicsView {

    private DemographicsPresenter presenter;

    //demographics nav
    private View[] checkinFlowViews;
    private View checkinDemographics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_demographic_review);

//        checkinDemographics = findViewById(R.id.checkinDemographicsHeaderLabel);
//        checkinFlowViews = new View[]{checkinDemographics, checkinConsent, checkinMedications, checkinIntake, checkinPayment};

        presenter = new DemographicsPresenterImpl(this);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    public void updateCheckInFlow(CheckinFlowState flowState, int totalPages, int currentPage) {
//        View view = null;
//        switch (flowState) {
//            case DEMOGRAPHICS:
//                view = checkinDemographics;
//                break;
//            case CONSENT:
//                view = checkinConsent;
//                break;
//            case MEDICATIONS_AND_ALLERGIES:
//                view = checkinMedications;
//                break;
//            case INTAKE:
//                view = checkinIntake;
//                break;
//            case PAYMENT:
//                view = checkinPayment;
//                break;
//            default:
//                return;
//        }
//
//        updateCheckinFlow(view, totalPages, currentPage);
    }

//    private void updateCheckinFlow(View highlightView, int totalPages, int currentPage) {
//        if (highlightView == null) {
//            return;
//        }
//
//        for (View flowView : checkinFlowViews) {
//            TextView progress = (TextView) flowView.findViewById(R.id.checkinDemographicsHeaderLabel);
//
//            if (flowView == highlightView && totalPages > 1) {
//
//                    progress.setText(String.format("%d %s %d", currentPage, "of", totalPages));//todo label for "of"
//
//            }
//        }
//    }
}
