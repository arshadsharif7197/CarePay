package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class QrCodeViewDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private AppointmentDTO appointmentDTO;
    private AppointmentMetadataModel appointmentMetadataModel;
    private ImageView qrCodeImageView;
    private ProgressBar qrCodeProgressBar;
    private CarePayTextView scanQRCodeTextView;

    /**
     * @param context        activity context
     * @param appointmentDTO appointment model
     */
    public QrCodeViewDialog(Context context, AppointmentDTO appointmentDTO, AppointmentMetadataModel appointmentMetadataModel) {
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

            WorkflowServiceHelper.getInstance().execute(appointmentMetadataModel.getTransitions()
                    .getCheckinAtOffice(), qrCodeCallBack, createURL(queryStrings));
        }
    }
    /**
     *
     * @param queryStrings the query strings for the url
     * @return complete url
     */
    private Map<String, String> createURL(QueryStrings queryStrings) {

        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put(queryStrings.getAppointmentId().getName(), appointmentDTO.getMetadata().getAppointmentId());
        queryMap.put(queryStrings.getPracticeMgmt().getName(), appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put(queryStrings.getPracticeId().getName(), appointmentDTO.getMetadata().getPracticeId());

        return queryMap;
    }

    WorkflowServiceCallback qrCodeCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            updateUI(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(context,
                    getContext().getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    /**
     *
     * @param workflowDTO workflow model returned by server.
     */
    private void updateUI(WorkflowDTO workflowDTO) {
        JsonObject jsonObject = (JsonObject) workflowDTO.getPayload();

        Gson gson = new Gson();
        QRCodePayloadDTO scanQRCodeDTO = gson.fromJson(jsonObject, QRCodePayloadDTO.class);

        if (scanQRCodeDTO != null) {
            List<Integer> buffer = scanQRCodeDTO.getQrcode().getData();
            byte[] byteArray = new byte[buffer.size()];

            Iterator<Integer> iterator = buffer.iterator();
            int index = 0;

            while (iterator.hasNext()) {
                Integer integer = iterator.next();
                byteArray[index] = integer.byteValue();
                index++;
            }

            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            qrCodeImageView.setImageBitmap(bitmap);
            scanQRCodeTextView.setText(StringUtil.getLabelForView(
                    appointmentMetadataModel.getLabel().getScanQRCodeHeading()));
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
