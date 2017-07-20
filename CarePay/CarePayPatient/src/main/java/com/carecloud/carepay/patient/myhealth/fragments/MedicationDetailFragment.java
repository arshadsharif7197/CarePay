package com.carecloud.carepay.patient.myhealth.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.MedicationDto;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.DateUtil;

/**
 * @author pjohnson on 19/07/17.
 */

public class MedicationDetailFragment extends BaseFragment {

    private MyHealthInterface callback;
    private MedicationDto medication;

    public MedicationDetailFragment() {

    }

    public static MedicationDetailFragment newInstance(Integer id) {
        Bundle args = new Bundle();
        args.putInt("medicationId", id);
        MedicationDetailFragment fragment = new MedicationDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (MyHealthInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the Activity must implement MyHealthInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        medication = getMedication(getArguments().getInt("medicationId"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_medication_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        TextView medicationNameTextView = (TextView) view.findViewById(R.id.medicationNameTextView);
        medicationNameTextView.setText(medication.getDrugName());
        TextView practiceTextView = (TextView) view.findViewById(R.id.practiceValueTextView);
        practiceTextView.setText(medication.getPractice());
        TextView prescribedValueTextView = (TextView) view.findViewById(R.id.prescribedValueTextView);
        prescribedValueTextView.setText(medication.getProvider().getName());
        TextView dateValueTextView = (TextView) view.findViewById(R.id.dateValueTextView);
        dateValueTextView.setText(DateUtil.getInstance().setDateRaw(medication.getEffectiveFrom())
                .toStringWithFormatMmSlashDdSlashYyyy());
        TextView strengthValueTextView = (TextView) view.findViewById(R.id.strengthValueTextView);
        if (medication.getStrengthDescription() == null) {
            strengthValueTextView.setText("--");
        } else {
            strengthValueTextView.setText(medication.getStrengthDescription());
        }
        TextView takeValueTextView = (TextView) view.findViewById(R.id.takeValueTextView);
        takeValueTextView.setText(medication.getRouteDescription());
        TextView quantityValueTextView = (TextView) view.findViewById(R.id.quantityValueTextView);
        if (medication.getQuantity() == null) {
            quantityValueTextView.setText("--");
        } else {
            quantityValueTextView.setText(String.valueOf(medication.getQuantity()));
        }
        TextView refillsValueTextView = (TextView) view.findViewById(R.id.refillsValueTextView);
        if (medication.getRefillCount() == null) {
            refillsValueTextView.setText("--");
        } else {
            refillsValueTextView.setText(String.valueOf(medication.getRefillCount()));
        }
        TextView instructionValueTextView = (TextView) view.findViewById(R.id.instructionValueTextView);
        instructionValueTextView.setText(medication.getPrescriptionInstructions());
    }

    private void setUpToolbar(View view) {
        callback.displayToolbar(false, null);
        Toolbar toolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager().getBackStackEntryCount() < 2) {
                    callback.displayToolbar(true, null);
                }
                getActivity().onBackPressed();
            }
        });
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("my_health_allergy_detail_title"));
    }

    private MedicationDto getMedication(int medicationId) {
        MyHealthDto myHealthDto = (MyHealthDto) callback.getDto();
        for (MedicationDto medication : myHealthDto.getPayload().getMyHealthData().getMedications()
                .getMedications()) {
            if (medicationId == medication.getId()) {
                return medication;
            }
        }
        return null;
    }
}
