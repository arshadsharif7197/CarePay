package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentMetadataModel;
import com.carecloud.carepaylibray.appointments.models.QRCodePayloadDTO;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class QrCodeViewDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private AppointmentDTO appointmentDTO;

    private ImageView qrCodeImageView;
    private ProgressBar qrCodeProgressBar;
    private CarePayTextView scanQRCodeTextView;
    private AppointmentMetadataModel appointmentMetadataModel;

    /**
     * @param context        activity context
     * @param appointmentDTO appointment model
     */
    public QrCodeViewDialog(Context context, AppointmentDTO appointmentDTO,
                            AppointmentMetadataModel appointmentMetadataModel) {
        super(context);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentMetadataModel = appointmentMetadataModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_qrcode_view);
        setCancelable(false);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setAttributes(params);

        ImageView cancelImageView = (ImageView) findViewById(R.id.cancelImageView);
        cancelImageView.setOnClickListener(this);

        scanQRCodeTextView = (CarePayTextView) findViewById(R.id.scanQRCodeTextView);
        qrCodeProgressBar = (ProgressBar) findViewById(R.id.qrCodeProgressBar);
        qrCodeImageView = (ImageView) findViewById(R.id.qrCodeImageView);

        callGenerateQRCodeAPI(); //API call for generating QR code
    }

    /**
     * Method to generate QR code API
     */
    private void callGenerateQRCodeAPI() {
        if (appointmentMetadataModel != null && appointmentMetadataModel.getTransitions() != null &&
                appointmentMetadataModel.getTransitions().getCheckinAtOffice() != null) {

            JsonObject queryStringObject = appointmentMetadataModel.getTransitions()
                    .getCheckinAtOffice().getQueryString();
            Gson gson = new Gson();
            QueryStrings queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);

            ((ISession) context).getWorkflowServiceHelper().execute(appointmentMetadataModel.getTransitions()
                    .getCheckinAtOffice(), qrCodeCallBack, getQueryParam(queryStrings));
        } else {
            /*Error in generating QR code*/
            scanQRCodeTextView.setText(appointmentMetadataModel.getLabel().getQrCodeErrorMessage());
            qrCodeProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * @param queryStrings the query strings for the url
     * @return queryMap
     */
    private Map<String, String> getQueryParam(QueryStrings queryStrings) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put(queryStrings.getAppointmentId().getName(), appointmentDTO.getMetadata().getAppointmentId());
        queryMap.put(queryStrings.getPracticeMgmt().getName(), appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put(queryStrings.getPracticeId().getName(), appointmentDTO.getMetadata().getPracticeId());

        return queryMap;
    }

    WorkflowServiceCallback qrCodeCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            updateUI(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            SystemUtil.showDefaultFailureDialog(context);
            Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    /**
     * @param workflowDTO workflow model returned by server.
     */
    private void updateUI(WorkflowDTO workflowDTO) {
        JsonObject jsonObject = workflowDTO.getPayload();

        Gson gson = new Gson();
        QRCodePayloadDTO scanQRCodeDTO = gson.fromJson(jsonObject, QRCodePayloadDTO.class);

        if (scanQRCodeDTO != null) {
            Picasso.with(context).load(scanQRCodeDTO.getQrCode()).into(qrCodeImageView);
            scanQRCodeTextView.setText(appointmentMetadataModel.getLabel().getScanQRCodeHeading());
            qrCodeProgressBar.setVisibility(View.GONE);
        } else {
            /*Error in generating QR code*/
            scanQRCodeTextView.setText(appointmentMetadataModel.getLabel().getQrCodeErrorMessage());
            qrCodeProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.cancelImageView) {
            cancel();
        }
    }
}
