package com.carecloud.carepay.practice.library.customcomponent;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.carecloud.carepay.practice.library.appointments.adapters.PatientListAdapter;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.models.MapFilterModel;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * Created by cocampo on 2/10/17.
 */

public class TwoColumnPatientListView extends RecyclerView {
    TwoColumnPatientListViewListener callback;
    private PatientListAdapter paymentsAdapter;

    public TwoColumnPatientListView(Context context) {
        super(context);
        setLayout(context);
    }

    public TwoColumnPatientListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayout(context);
    }

    public TwoColumnPatientListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayout(context);
    }

    private void setLayout(Context context) {
        setHasFixedSize(true);
        setLayoutManager(new GridLayoutManager(context, 2));
    }

    public interface TwoColumnPatientListViewListener {
        void onPatientTapped(Object dto);
    }

    public void setCallback(TwoColumnPatientListViewListener listener) {
        callback = listener;
    }

    /**
     * @param checkInDTO to fill list view
     */
    public void setCheckInDTO(CheckInDTO checkInDTO) {
        if (null != checkInDTO) {
            setPatientAdapter(new PatientListAdapter(getContext(), checkInDTO));
        }
    }

    /**
     * @param paymentsModel to fill list view
     */
    public void setPaymentsModel(PaymentsModel paymentsModel) {
        if (null != paymentsModel) {
            setPatientAdapter(new PatientListAdapter(getContext(), paymentsModel));
        }
    }

    private void setPatientAdapter(PatientListAdapter paymentsAdapter) {
        this.paymentsAdapter = paymentsAdapter;
        paymentsAdapter.setTapListener(new PatientListAdapter.OnItemTappedListener() {
            @Override
            public void onItemTap(Object dto) {
                if (callback != null) {
                    callback.onPatientTapped(dto);
                }
            }
        });

        setAdapter(paymentsAdapter);
    }

    public void applyFilter(FilterModel filterModel) {
         paymentsAdapter.applyFilter(new MapFilterModel(filterModel));
    }

    /**
     * @return number of pending and non-pending patients after filtering
     */
    public int getSizeFilteredPatients() {
        return paymentsAdapter.getSizeFilteredPatients();
    }

    /**
     * @return number of pending patients after filtering
     */
    public int getSizeFilteredPendingPatients() {
        return paymentsAdapter.getSizeFilteredPendingPatients();
    }
}
