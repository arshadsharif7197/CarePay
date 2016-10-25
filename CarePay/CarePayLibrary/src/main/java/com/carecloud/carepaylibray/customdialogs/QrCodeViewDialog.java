package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QrCodeViewDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private AppointmentDTO appointmentDTO;

    /**
     *
     * @param context activity context
     * @param appointmentDTO appointment model
     */
    public QrCodeViewDialog(Context context, AppointmentDTO appointmentDTO) {
        super(context);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
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

        ImageView qrCodeImageView = (ImageView) findViewById(R.id.qrCodeImageView);
        try {
            Bitmap bitmap = encodeAsBitmap(appointmentDTO.getPayload().getId());
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException ex) {
            ex.printStackTrace();
        }

        (findViewById(R.id.qrcodeCancelButton)).setOnClickListener(this);
        (findViewById(R.id.dialogQRHeaderTextView)).setOnClickListener(this);
    }

    /**
     * @param convertToQRCode string which needs to be converted to QR code
     * @return bitmap
     * @throws WriterException throws WriterException that needs to be caught
     */
    Bitmap encodeAsBitmap(String convertToQRCode) throws WriterException {
        BitMatrix result;
        int pixelWidth = 500;
        try {
            result = new MultiFormatWriter().encode(convertToQRCode, BarcodeFormat.QR_CODE,
                    pixelWidth, pixelWidth, null);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        int heightCounter = 0;
        while (heightCounter < height) {
            int offset = heightCounter * width;
            for (int widthCounter = 0; widthCounter < width; widthCounter++) {
                pixels[offset + widthCounter] = result.get(widthCounter, heightCounter) ?
                        ContextCompat.getColor(context, R.color.black)
                        : ContextCompat.getColor(context, R.color.white);
            }

            heightCounter++;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, pixelWidth, 0, 0, width, height);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialogQRHeaderTextView) {
            cancel();
        } else if (viewId == R.id.qrcodeCancelButton) {
            cancel();
        }
    }
}
