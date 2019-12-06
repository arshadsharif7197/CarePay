package com.carecloud.carepaylibray.customdialogs;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentMetadataModel;
import com.carecloud.carepaylibray.appointments.models.QRCodePayloadDTO;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class QrCodeViewDialog extends BaseDialogFragment implements View.OnClickListener {

    private AppointmentDTO appointmentDTO;

    private ImageView qrCodeImageView;
    private ProgressBar qrCodeProgressBar;
    private CarePayTextView scanQRCodeTextView;
    private AppointmentMetadataModel appointmentMetadataModel;
    private QRCodeViewDialogListener callBack;

    public interface QRCodeViewDialogListener {
        void onGenerateQRCodeError(String errorMessage);
    }

    /**
     * @param appointmentDTO appointment model
     */
    public static QrCodeViewDialog newInstance(AppointmentDTO appointmentDTO,
                            AppointmentMetadataModel appointmentMetadataModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);
        DtoHelper.bundleDto(args, appointmentMetadataModel);
        QrCodeViewDialog fragment = new QrCodeViewDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, args);
            appointmentMetadataModel = DtoHelper.getConvertedDTO(AppointmentMetadataModel.class, args);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_qrcode_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

            getWorkflowServiceHelper().execute(appointmentMetadataModel.getTransitions()
                    .getCheckinAtOffice(), qrCodeCallBack, getQueryParam(queryStrings));
        } else {
            /*Error in generating QR code*/
            scanQRCodeTextView.setText(Label.getLabel("qr_code_error_message"));
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

    private WorkflowServiceCallback qrCodeCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            updateUI(workflowDTO);
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            hideProgressDialog();
            callBack.onGenerateQRCodeError(serverErrorDto.getMessage().getBody().getError().getMessage());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };

    /**
     * @param workflowDTO workflow model returned by server.
     */
    private void updateUI(WorkflowDTO workflowDTO) {
        QRCodePayloadDTO scanQRCodeDTO = workflowDTO.getPayload(QRCodePayloadDTO.class);

        if (scanQRCodeDTO != null) {
            Picasso.with(getContext()).load(scanQRCodeDTO.getQrCode()).into(qrCodeImageView);
            scanQRCodeTextView.setText(Label.getLabel("scan_qr_code_heading"));
            qrCodeProgressBar.setVisibility(View.GONE);
        } else {
            /*Error in generating QR code*/
            scanQRCodeTextView.setText(Label.getLabel("qr_code_error_message"));
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

    public void setQRCodeViewDialogListener(QRCodeViewDialogListener listener) {
        this.callBack = listener;
    }
}
