package com.carecloud.carepay.patient.myhealth.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.MedicationDto;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 19/07/17.
 */
public class MedicationDetailFragment extends BaseFragment {

    private MyHealthInterface callback;
    private MedicationDto medication;

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
        if (medication.getProvider() == null) {
            prescribedValueTextView.setText("--");
        } else {
            prescribedValueTextView.setText(medication.getProvider().getName());
        }

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

        view.findViewById(R.id.educationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> queryMap = new HashMap<>();
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
                    queryMap.put("code", code);
                    queryMap.put("code_system", codeSystem);
                    queryMap.put("term", medication.getDrugName());
                    TransitionDTO transitionDTO = ((MyHealthDto) callback.getDto()).getMetadata()
                            .getLinks().getEducationMaterial();
                    getWorkflowServiceHelper().execute(transitionDTO, educationCallback, queryMap);
                } else {
                    openExternalBrowser(url);
                }

            }
        });
    }

    private WorkflowServiceCallback educationCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            MyHealthDto myHealthDto = gson.fromJson(workflowDTO.toString(), MyHealthDto.class);
            hideProgressDialog();
            openExternalBrowser(myHealthDto.getPayload().getEducationMaterial().getLink());

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            callback.showErrorToast(exceptionMessage);
        }
    };

    private void openExternalBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void setUpToolbar(View view) {
        callback.displayToolbar(false, null);
        Toolbar toolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("my_health_medication_detail_title"));
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
