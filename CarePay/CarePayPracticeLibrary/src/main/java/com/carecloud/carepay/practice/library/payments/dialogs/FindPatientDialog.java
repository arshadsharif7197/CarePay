package com.carecloud.carepay.practice.library.payments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.PatientSearchResultAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.payments.models.PatientDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindPatientDialog extends Dialog {

    private Context context;
    private String closeLabel;
    private String hintLabel;
    private TransitionDTO transitionDTO;
    private OnItemClickedListener clickedListener;

    /**
     * Constructor
     *
     * @param context       context
     * @param transitionDTO transition dto
     */
    public FindPatientDialog(Context context, TransitionDTO transitionDTO, String closeLabel, String hintLabel) {
        super(context);
        this.context = context;
        this.transitionDTO = transitionDTO;
        this.closeLabel = closeLabel;
        this.hintLabel = hintLabel;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_find_patient);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        initializeView();
    }

    private void initializeView() {
        EditText findPatientEditBox = (EditText) findViewById(R.id.find_patient_edit_box);
        findPatientEditBox.setHint(hintLabel);

        setTextListener(findPatientEditBox);
        setKeyListener(findPatientEditBox);

        ((TextView) findViewById(R.id.find_patient_close_label)).setText(closeLabel);
        findViewById(R.id.find_patient_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void setKeyListener(final EditText editText) {
        editText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Hide keyboard
                    InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    Map<String, String> queryMap = new HashMap<>();
                    queryMap.put("practice_mgmt", ((ISession) context).getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
                    queryMap.put("practice_id", ((ISession) context).getApplicationMode().getUserPracticeDTO().getPracticeId());

                    JsonArray postModel = new JsonArray();
                    postModel.add(editText.getText().toString());
                    String postBody = postModel.toString();

                    ((ISession) context).getWorkflowServiceHelper().execute(transitionDTO, findPatientCallback, postBody, queryMap);
                    return true;
                }
                return false;
            }
        });
    }

    private void setTextListener(final EditText editView) {
        editView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable charSequence) {
                String query = editView.getText().toString();
                if (query.length() == 0) {
                    findViewById(R.id.patient_searched_list).setVisibility(View.GONE);
                }
            }
        });
    }

    private WorkflowServiceCallback findPatientCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            ((ISession) context).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) context).hideProgressDialog();

            PaymentsModel searchResult = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO.toString());
            if (searchResult != null) {
                List<PatientDTO> patients = searchResult.getPaymentPayload().getPatients();
                showSearchResultList(patients);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ((ISession) context).hideProgressDialog();
        }
    };

    private void showSearchResultList(List<PatientDTO> patients) {
        PatientSearchResultAdapter adapter = new PatientSearchResultAdapter(context, patients);
        setOnItemClickedListener(adapter);

        RecyclerView searchedList = (RecyclerView) findViewById(R.id.patient_searched_list);
        searchedList.setLayoutManager(new LinearLayoutManager(context));
        searchedList.setAdapter(adapter);
        searchedList.setVisibility(View.VISIBLE);
    }

    private void setOnItemClickedListener(PatientSearchResultAdapter adapter) {
        adapter.setClickedListener(new PatientSearchResultAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(PatientDTO patient) {
                dismiss();
                clickedListener.onItemClicked(patient);
            }
        });
    }

    public void setClickedListener(OnItemClickedListener clickedListener) {
        this.clickedListener = clickedListener;
    }

    public interface OnItemClickedListener {
        void onItemClicked(PatientDTO patient);
    }
}
