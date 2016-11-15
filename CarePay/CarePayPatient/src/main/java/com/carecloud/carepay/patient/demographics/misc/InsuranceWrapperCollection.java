package com.carecloud.carepay.patient.demographics.misc;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.demographics.fragments.scanner.InsuranceScannerFragment;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsoco_user on 11/15/2016.
 * Holdes a list of insurance wrappers allowing deletion and addition
 */
 public class InsuranceWrapperCollection {

    private static final int MAX_CARDS = 3;
    private AppCompatActivity wrapperContext;
    private LinearLayout      parent;
    private List<InsuranceWrapper> wrappers = new ArrayList<>();
    private DemographicMetadataEntityItemInsuranceDTO                metadata;
    private DemographicLabelsDTO                                     labels;
    private OnClickRemoveOrAddCallback callback;

    public InsuranceWrapperCollection(AppCompatActivity context,
                                      LinearLayout parent,
                                      DemographicMetadataEntityItemInsuranceDTO metadata,
                                      DemographicLabelsDTO labels,
                                      OnClickRemoveOrAddCallback callback) {
        this.wrapperContext = context;
        this.parent = parent;
        this.metadata = metadata;
        this.callback = callback;
        this.labels = labels;
    }

    public void add(DemographicInsurancePayloadDTO payloadDTO) {
        int count = wrappers.size();
        if (count < MAX_CARDS) {
            InsuranceWrapper insuranceWrapper = new InsuranceWrapper(wrapperContext,
                                                                     labels,
                                                                     metadata,
                                                                     payloadDTO,
                                                                     parent,
                                                                     new OnClickRemoveListener(InsuranceWrapperCollection.this,
                                                                                               callback));
            wrappers.add(insuranceWrapper);
            count++;
        }
        if (count >= MAX_CARDS && callback != null) {
            callback.onAfterAdd();
        }
    }

    public void addAll(List<DemographicInsurancePayloadDTO> payloadDTOs) {
        for (DemographicInsurancePayloadDTO payloadDTO : payloadDTOs) {
            add(payloadDTO);
        }
    }

    public void remove(InsuranceWrapper insuranceWrapper) {
        parent.removeView(insuranceWrapper.getHolderWrapperView());
        wrappers.remove(insuranceWrapper);
    }

    public List<DemographicInsurancePayloadDTO> exportPayloadsAsList() {
        List<DemographicInsurancePayloadDTO> payloads = new ArrayList<>();
        for (InsuranceWrapper insuranceWrapper : wrappers) {
            payloads.add(insuranceWrapper.getWrapperPayloadDTO());
        }
        return payloads;
    }
}

/**
 * Wrapper of an entity holding a insurance scanner fragment
 */
class InsuranceWrapper {

    private InsuranceScannerFragment wrapperScannerFragment;
    private LinearLayout             holderWrapperView;

    public InsuranceWrapper(AppCompatActivity context,
                            DemographicLabelsDTO labels,
                            DemographicMetadataEntityItemInsuranceDTO metadata,
                            DemographicInsurancePayloadDTO payload,
                            LinearLayout parentView,
                            OnClickRemoveListener clickListener) {
        // create the container with both fragment and remove button
        holderWrapperView = new LinearLayout(context);
        holderWrapperView.setOrientation(LinearLayout.VERTICAL);

        FrameLayout fragHolder = new FrameLayout(context);
        int fragHolderId = View.generateViewId();
        fragHolder.setId(fragHolderId);
        holderWrapperView.addView(fragHolder, 0);

        // create 'Remove' clickable
        TextView removeClickable = new TextView(context);
        removeClickable.setLayoutParams(new ActionBarOverlayLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
        removeClickable.setGravity(Gravity.CENTER);
        removeClickable.setClickable(true);
        removeClickable.setPadding(0, 5, 0, 5);
        removeClickable.setText(labels == null ? CarePayConstants.NOT_DEFINED : labels.getDocumentsRemove());
        removeClickable.setTextSize(14);
        removeClickable.setTextColor(ContextCompat.getColor(context, R.color.harvard_crimson));
        clickListener.setInsuranceWrapper(this);
        removeClickable.setOnClickListener(clickListener);
        SystemUtil.setProximaNovaSemiboldTypeface(context, removeClickable);
        holderWrapperView.addView(removeClickable, 1);

        // add the fragment
        FragmentManager fm = context.getSupportFragmentManager();
        wrapperScannerFragment = new InsuranceScannerFragment();
        wrapperScannerFragment.setInsuranceMetadataDTO(metadata);
        wrapperScannerFragment.setInsuranceDTO(payload);
        fm.beginTransaction().replace(fragHolderId, wrapperScannerFragment).commit();

        parentView.addView(holderWrapperView);
    }

    public DemographicInsurancePayloadDTO getWrapperPayloadDTO() {
        return wrapperScannerFragment.getInsuranceDTO();
    }

    public LinearLayout getHolderWrapperView() {
        return holderWrapperView;
    }
}
