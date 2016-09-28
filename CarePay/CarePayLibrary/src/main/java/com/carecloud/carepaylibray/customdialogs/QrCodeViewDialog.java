package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;

/**
 * Created by prem_mourya on 9/27/2016.
 */

public class QrCodeViewDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private AppointmentModel appointmentModel;
    public QrCodeViewDialog(Context context,AppointmentModel appointmentModel){
        super(context);
        this.context = context;
        this.appointmentModel = appointmentModel;
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
        getWindow().setAttributes((WindowManager.LayoutParams) params);
        (findViewById(R.id.qrcodeCancelButton)).setOnClickListener(this);
        (findViewById(R.id.dialogQRHeaderTextView)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
            int viewId = view.getId();
            if(viewId == R.id.dialogQRHeaderTextView){
                cancel();
            }else if(viewId == R.id.qrcodeCancelButton){
                cancel();
            }
    }
}
