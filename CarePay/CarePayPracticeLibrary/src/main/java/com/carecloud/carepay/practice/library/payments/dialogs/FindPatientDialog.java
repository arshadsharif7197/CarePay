package com.carecloud.carepay.practice.library.payments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.Map;

public class FindPatientDialog extends Dialog {

    private Context context;
    private PaymentsModel paymentsModel;

    /**
     * Constructor
     * @param context context
     * @param paymentsModel payment model
     */
    public FindPatientDialog(Context context, PaymentsModel paymentsModel) {
        super(context);
        this.context = context;
        this.paymentsModel = paymentsModel;
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
        PaymentsLabelDTO paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();

        EditText findPatientEditBox = (EditText) findViewById(R.id.find_patient_edit_box);
        findPatientEditBox.setHint(paymentsLabel.getPracticePaymentsFindPatientLabel());
        setTextListener(findPatientEditBox);

        ((TextView) findViewById(R.id.find_patient_close_label))
                .setText(paymentsLabel.getPracticePaymentsDetailDialogCloseButton());

        findViewById(R.id.find_patient_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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
                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
                queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());

                JsonArray postModel = new JsonArray();
                postModel.add(editView.getText().toString());
                String postBody = postModel.toString();

                TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getFindPatient();
                WorkflowServiceHelper.getInstance().execute(transitionDTO, findPatientCallback, postBody, queryMap);
            }
        });
    }

    private WorkflowServiceCallback findPatientCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            System.out.println(workflowDTO.toString());
        }

        @Override
        public void onFailure(String exceptionMessage) {

        }
    };
}
