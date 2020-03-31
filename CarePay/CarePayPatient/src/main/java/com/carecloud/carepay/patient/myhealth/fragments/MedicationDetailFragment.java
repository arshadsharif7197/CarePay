package com.carecloud.carepay.patient.myhealth.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.MyHealthViewModel;
import com.carecloud.carepay.patient.myhealth.dtos.MedicationDto;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

/**
 * @author pjohnson on 19/07/17.
 */
public class MedicationDetailFragment extends BaseFragment {

    private MyHealthInterface callback;
    private MedicationDto medication;
    private MyHealthViewModel model;
    private MyHealthDto myHealthDto;

    public MedicationDetailFragment() {

    }

    /**
     * @param id the medication Id
     * @return a new instance of MedicationDetailFragment
     */
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
        model = new ViewModelProvider(getActivity()).get(MyHealthViewModel.class);
        myHealthDto = model.getMyHealthDto().getValue();
        medication = getMedication(getArguments().getInt("medicationId"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medication_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        TextView medicationNameTextView = view.findViewById(R.id.medicationNameTextView);
        medicationNameTextView.setText(medication.getDrugName());
        TextView practiceTextView = view.findViewById(R.id.practiceValueTextView);
        practiceTextView.setText(medication.getPractice());

        TextView prescribedValueTextView = view.findViewById(R.id.prescribedValueTextView);
        if (medication.getProvider() == null) {
            prescribedValueTextView.setText("--");
        } else {
            prescribedValueTextView.setText(medication.getProvider().getName());
        }

        TextView dateValueTextView = view.findViewById(R.id.dateValueTextView);
        dateValueTextView.setText(DateUtil.getInstance().setDateRaw(medication.getEffectiveFrom())
                .toStringWithFormatMmSlashDdSlashYyyy());
        TextView strengthValueTextView = view.findViewById(R.id.strengthValueTextView);
        if (medication.getStrengthDescription() == null) {
            strengthValueTextView.setText("--");
        } else {
            strengthValueTextView.setText(medication.getStrengthDescription());
        }
        TextView takeValueTextView = view.findViewById(R.id.takeValueTextView);
        takeValueTextView.setText(medication.getRouteDescription());
        TextView quantityValueTextView = view.findViewById(R.id.quantityValueTextView);
        if (medication.getQuantity() == null) {
            quantityValueTextView.setText("--");
        } else {
            quantityValueTextView.setText(String.valueOf(medication.getQuantity()));
        }
        TextView refillsValueTextView = view.findViewById(R.id.refillsValueTextView);
        if (medication.getRefillCount() == null) {
            refillsValueTextView.setText("--");
        } else {
            refillsValueTextView.setText(String.valueOf(medication.getRefillCount()));
        }
        TextView instructionValueTextView = view.findViewById(R.id.instructionValueTextView);
        instructionValueTextView.setText(medication.getPrescriptionInstructions());

        view.findViewById(R.id.educationButton).setOnClickListener(view1 -> {

            String code = "";
            String codeSystem = "";
            String url = "";
            boolean shouldCallBreezeService = true;
            if (medication.getRxNormCode() != null) {
                code = medication.getRxNormCode();
                codeSystem = "rxnorm";
            } else if (medication.getSnomed() != null) {
                code = medication.getSnomed();
                codeSystem = "snomed";
            } else if (medication.getLoinc() != null) {
                code = medication.getLoinc();
                codeSystem = "loinc";
            } else {
                shouldCallBreezeService = false;
                url = "http://vsearch.nlm.nih.gov/vivisimo/cgi-bin/query-meta?v%3Aproject=medlineplus&query="
                        + medication.getDrugName();
            }

            MixPanelUtil.logEvent(getString(R.string.event_myHealth_viewMedicationEducation),
                    getString(R.string.param_medication_name), medication.getDrugName());

            if (shouldCallBreezeService) {
                model.getMedicationDto().observe(this,
                        dto -> openExternalBrowser(dto.getPayload().getEducationMaterial().getLink()));
                model.callMedicationBreezeService(medication, code, codeSystem);
            } else {
                openExternalBrowser(url);
            }

        });
    }

    private void openExternalBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void setUpToolbar(View view) {
        callback.displayToolbar(false, null);
        Toolbar toolbar = view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> getActivity().onBackPressed());
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("my_health_medication_detail_title"));
    }

    private MedicationDto getMedication(int medicationId) {
        for (MedicationDto medication : myHealthDto.getPayload().getMyHealthData().getMedications()
                .getMedications()) {
            if (medicationId == medication.getId()) {
                return medication;
            }
        }
        return null;
    }
}
