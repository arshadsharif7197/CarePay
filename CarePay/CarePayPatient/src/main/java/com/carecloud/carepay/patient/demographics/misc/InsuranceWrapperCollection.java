package com.carecloud.carepay.patient.demographics.misc;

import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;

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

    /**
     * Ctor
     * @param context The context
     * @param parent The parent view (holding the view of the cards)
     * @param metadata The metadata (common to all insurance screens)
     * @param labels The global labels
     * @param callback A call back to be executed when performing add/remove insurance card
     */
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

    /**
     * Adds a new card
     * @param payloadDTO The payload
     */
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

    /**
     * For each element i the list, adds a card populated with the payload, or
     * or, if the list is empty, an empty card screen
     * @param payloadDTOs The list of payloads
     */
    public void addAll(List<DemographicInsurancePayloadDTO> payloadDTOs) {
        for (DemographicInsurancePayloadDTO payloadDTO : payloadDTOs) {
            add(payloadDTO);
        }
    }

    /**
     * Remove the card corresponding to the wrapper
     * @param insuranceWrapper The wrapper
     */
    public void remove(InsuranceWrapper insuranceWrapper) {
        parent.removeView(insuranceWrapper.getHolderWrapperView());
        wrappers.remove(insuranceWrapper);
    }

    /**
     * Collects the payloads of the wrappers in a list
     * @return The list of payloads
     */
    public List<DemographicInsurancePayloadDTO> exportPayloadsAsList() {
        List<DemographicInsurancePayloadDTO> payloads = new ArrayList<>();
        for (InsuranceWrapper insuranceWrapper : wrappers) {
            payloads.add(insuranceWrapper.getWrapperPayloadDTO());
        }
        return payloads;
    }

    /**
     * @return Whether the collection is empty
     */
    public boolean isEmpty() {
        return wrappers.isEmpty();
    }
}