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

import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentMetadataModel;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.appointments.models.ScanQRCodeDTO;
import com.carecloud.carepaylibray.appointments.services.AppointmentService;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrCodeViewDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private AppointmentDTO appointmentDTO;
    private AppointmentMetadataModel appointmentMetadataModel;
    private ImageView qrCodeImageView;
    private ProgressBar qrCodeProgressBar;
    private CarePayTextView scanQRCodeTextView;

    /**
     *
     * @param context activity context
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
        if(appointmentMetadataModel != null && appointmentMetadataModel.getTransitions() != null &&
            appointmentMetadataModel.getTransitions().getCheckinAtOffice() != null &&
            appointmentMetadataModel.getTransitions().getCheckinAtOffice().getQueryStrings()
                != null) {

            String url = appointmentMetadataModel.getTransitions().getCheckinAtOffice().getUrl();
            QueryStrings queryStrings = appointmentMetadataModel.getTransitions()
                    .getCheckinAtOffice().getQueryStrings();

            AppointmentService aptService
                    = (new BaseServiceGenerator(context)).createService(AppointmentService.class);
            Call<ScanQRCodeDTO> call = aptService.getQRCode(createURL(url, queryStrings));
            call.enqueue(new Callback<ScanQRCodeDTO>() {

                @Override
                public void onResponse(Call<ScanQRCodeDTO> call, Response<ScanQRCodeDTO> response) {
                    ScanQRCodeDTO scanQRCodeDTO = response.body();

                    if(scanQRCodeDTO != null) {
                        List<Integer> buffer = scanQRCodeDTO.getPayload().getQrcode().getData();
                        byte[]  byteArray = new byte[buffer.size()];

                        Iterator<Integer> iterator = buffer.iterator();
                        int index = 0;

                        while(iterator.hasNext()) {
                            Integer integer = iterator.next();
                            byteArray[index] = integer.byteValue();
                            index++;
                        }

                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                                byteArray.length);
                        qrCodeImageView.setImageBitmap(bitmap);
                        scanQRCodeTextView.setText(StringUtil.getLabelForView(
                                appointmentMetadataModel.getLabel().getScanQRCodeHeading()));
                        qrCodeProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ScanQRCodeDTO> call, Throwable throwable) {
                    qrCodeProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     *
     * @param url the url for check in at office
     * @param queryStrings the query strings for the url
     * @return complete url
     */
    private String createURL(String url, QueryStrings queryStrings) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append("?");
        sb.append(queryStrings.getAppointmentId().getName());
        sb.append("=");
        sb.append(appointmentDTO.getMetadata().getAppointmentId());
        sb.append("&");
        sb.append(queryStrings.getPracticeMgmt().getName());
        sb.append("=");
        sb.append(appointmentDTO.getMetadata().getPracticeMgmt());
        sb.append("&");
        sb.append(queryStrings.getPracticeId().getName());
        sb.append("=");
        sb.append(appointmentDTO.getMetadata().getPracticeId());

        return sb.toString();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.cancelImageView) {
            cancel();
        }
    }
}
