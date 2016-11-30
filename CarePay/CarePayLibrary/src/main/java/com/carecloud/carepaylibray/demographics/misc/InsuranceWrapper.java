package com.carecloud.carepaylibray.demographics.misc;

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

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.scanner.InsuranceScannerFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Wrapper of an entity holding a insurance scanner fragment
 */
public class InsuranceWrapper {

    private InsuranceScannerFragment wrapperScannerFragment;
    private LinearLayout             holderWrapperView;

    /**
     * Ctor
     * @param context The context
     * @param labels Tghe labels from remote
     * @param metadata The metadata
     * @param payload The payload
     * @param parentView The parent view (holfing the wrapppers)
     * @param clickListener  OnClick log-back
     */
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
        removeClickable.setPadding(0, 8, 0, 8);
        removeClickable.setText(labels == null ? CarePayConstants.NOT_DEFINED : labels.getDocumentsRemove());
        removeClickable.setTextSize(14);
        removeClickable.setTextColor(ContextCompat.getColor(context, com.carecloud.carepaylibrary.R.color.harvard_crimson));
        clickListener.setInsuranceWrapper(this);
        removeClickable.setOnClickListener(clickListener);
        SystemUtil.setProximaNovaSemiboldTypeface(context, removeClickable);
        holderWrapperView.addView(removeClickable, 1);

        // add the fragment
        wrapperScannerFragment = new InsuranceScannerFragment();
        wrapperScannerFragment.setInsuranceMetadataDTO(metadata);
        wrapperScannerFragment.setInsuranceDTO(payload, SystemUtil.getPlaceholderAsBase64(context));
        FragmentManager fm = context.getSupportFragmentManager();
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
