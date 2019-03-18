package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.PatientSearchResultAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CarePayEditText;
import com.carecloud.carepaylibray.customcomponents.RecyclerViewWithDivider;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.JsonArray;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindPatientDialog extends BaseDialogFragment {

    private TransitionDTO transitionDTO;
    private OnItemClickedListener clickedListener;
    private String titleLabel;
    private String query;

    /**
     * Constructor
     *
     * @param transitionDTO transition dto
     */
    public static FindPatientDialog newInstance(TransitionDTO transitionDTO, String titleLabel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, transitionDTO);
        args.putString("titleLabel", titleLabel);
        FindPatientDialog fragment = new FindPatientDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            transitionDTO = DtoHelper.getConvertedDTO(TransitionDTO.class, args);
            titleLabel = args.getString("titleLabel");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_find_patient, container, false);
    }

    private void initializeView() {
        CarePayEditText findPatientEditBox = (CarePayEditText) findViewById(R.id.find_patient_edit_box);
        findPatientEditBox.addTextChangedListener(getTextChangedListener());
        findPatientEditBox.setOnKeyListener(getKeyListener());

        findViewById(R.id.find_patient_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ISession) getContext()).getWorkflowServiceHelper().interrupt();
                dismiss();
            }
        });

        TextView titleView = (TextView) findViewById(R.id.find_patient_title);
        titleView.setText(titleLabel);
    }

    private View.OnKeyListener getKeyListener() {
        return new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Hide keyboard
                    InputMethodManager manager = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    Map<String, String> queryMap = new HashMap<>();
                    queryMap.put("practice_mgmt", ((ISession) getContext()).getApplicationMode()
                            .getUserPracticeDTO().getPracticeMgmt());
                    queryMap.put("practice_id", ((ISession) getContext()).getApplicationMode()
                            .getUserPracticeDTO().getPracticeId());

                    query = ((CarePayEditText) view).getText().toString().toUpperCase();
                    JsonArray postModel = new JsonArray();
                    postModel.add(query);
                    String postBody = postModel.toString();

                    ((ISession) getContext()).getWorkflowServiceHelper().execute(transitionDTO, findPatientCallback,
                            postBody, queryMap);
                    return true;
                }
                return false;
            }
        };
    }

    private TextWatcher getTextChangedListener() {
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable charSequence) {
                if (charSequence.length() == 0) {
                    findViewById(R.id.patient_searched_list).setVisibility(View.GONE);
                } else if (charSequence.length() > 3) {
                    ((ISession) getContext()).getWorkflowServiceHelper().interrupt();

                    query = charSequence.toString().toUpperCase();
                    JsonArray postModel = new JsonArray();
                    postModel.add(query);
                    String postBody = postModel.toString();
                    ((ISession) getContext()).getWorkflowServiceHelper()
                            .execute(transitionDTO, findPatientCallback, postBody);
                }
            }
        };
    }

    private WorkflowServiceCallback findPatientCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PaymentsModel searchResult = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO.toString());
            if (searchResult != null) {
                findViewById(R.id.patient_searched_list).setVisibility(View.VISIBLE);
                findViewById(R.id.patient_not_found_text).setVisibility(View.GONE);
                List<PatientModel> patients = searchResult.getPaymentPayload().getPatients();
                sortPatients(patients);
                showSearchResultList(patients);
            } else {
                findViewById(R.id.patient_searched_list).setVisibility(View.GONE);
                findViewById(R.id.patient_not_found_text).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            if(isAdded()) {
                findViewById(R.id.patient_searched_list).setVisibility(View.GONE);
                findViewById(R.id.patient_not_found_text).setVisibility(View.VISIBLE);
            }
        }

        private void sortPatients(List<PatientModel> patients) {
            Collections.sort(patients, new Comparator<PatientModel>() {
                @Override
                public int compare(PatientModel left, PatientModel right) {
                    String leftName = left.getFullName();
                    String rightName = right.getFullName();

                    boolean leftStartsWithQuery = leftName.startsWith(query);
                    if (leftStartsWithQuery == rightName.startsWith(query)) {
                        return leftName.compareTo(rightName);
                    }

                    if (leftStartsWithQuery) {
                        return -1;
                    }

                    return 1;
                }
            });
        }
    };

    private void showSearchResultList(List<PatientModel> patients) {
        PatientSearchResultAdapter adapter = new PatientSearchResultAdapter(getContext(), patients);
        setOnItemClickedListener(adapter);

        RecyclerViewWithDivider searchedList = (RecyclerViewWithDivider) findViewById(R.id.patient_searched_list);
        searchedList.setLayoutManager(new LinearLayoutManager(getContext()));
        ViewGroup.LayoutParams params = searchedList.getLayoutParams();
        if (patients != null && patients.size() > 4) {
            params.height = getResources().getDimensionPixelSize(R.dimen.dimen_175dp);
        } else {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        searchedList.setLayoutParams(params);
        searchedList.setAdapter(adapter);
    }

    private void setOnItemClickedListener(PatientSearchResultAdapter adapter) {
        adapter.setClickedListener(new PatientSearchResultAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(PatientModel patient) {
                dismiss();
                clickedListener.onItemClicked(patient);
            }
        });
    }

    public void setClickedListener(OnItemClickedListener clickedListener) {
        this.clickedListener = clickedListener;
    }

    public interface OnItemClickedListener {
        void onItemClicked(PatientModel patient);
    }
}
