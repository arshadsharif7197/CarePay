package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

/**
 * Created by prem_mourya on 10/5/2016.
 */

public class LargeAlertDialog extends Dialog  implements View.OnClickListener {

    private Context context;
    private LargeAlertInterface largeAlertInterface;
    private Button actionCallButton;
    private CarePayTextView largeMssageLabel;
    private String actionText;
    private String message;
    private LinearLayout headerLayout;
    private int headerBackGroundColor;
    private int headerIcon;

    //For callback
    public interface LargeAlertInterface {
        public void onActionButton();
    }
    /**
     * show custom dialog for large message.
     *
     * @param context the activity context to evaluate
     * @param message the String to evaluate for showing message
     * @param actionText the String to evaluate for button text for action
     * @param largeAlertInterface the String to evaluate for callback method for action
     */
    public LargeAlertDialog(Context context,String message,String actionText ,LargeAlertInterface largeAlertInterface){
        super(context);
        this.context = context;
        this.message = message;
        this.largeAlertInterface = largeAlertInterface;
        this.actionText = actionText;
        this.headerBackGroundColor = R.color.lightning_yellow;
    }

    /**
     * show custom dialog for large message.
     *
     * @param context the activity context to evaluate
     * @param message the String to evaluate for showing message
     * @param actionText the String to evaluate for button text for action
     * @param headerBackGroundColor the String to evaluate for header background color
     * @param headerIcon the String to evaluate for header  image
     * @param largeAlertInterface the String to evaluate for callback method for action
     */
    public LargeAlertDialog(Context context,String message,String actionText,int headerBackGroundColor,int headerIcon,LargeAlertInterface largeAlertInterface){
        super(context);
        this.context = context;
        this.message = message;
        this.largeAlertInterface = largeAlertInterface;
        this.actionText = actionText;
        this.headerBackGroundColor = headerBackGroundColor;
        this.headerIcon = headerIcon;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_large_alert);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setAttributes(params);
        findViewById(R.id.dialogCloseImageView).setOnClickListener(this);
        actionCallButton = (Button) findViewById(R.id.actionButton);
        actionCallButton.setOnClickListener(this);
        actionCallButton.setText(actionText);
        largeMssageLabel = (CarePayTextView)findViewById(R.id.largeMssageLabel);
        largeMssageLabel.setText(message);
        largeMssageLabel.setTextColor(context.getResources().getColor(R.color.white));
        largeMssageLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        setBackgroundColor();
        ((ImageView)findViewById(R.id.headerIconImageView)).setBackgroundResource(headerIcon);
    }
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.dialogCloseImageView) {
            cancel();
        }else if(viewId == R.id.actionButton){
            largeAlertInterface.onActionButton();
            cancel();
        }
    }

    private void setBackgroundColor(){
        View header = findViewById(R.id.headerLayout);
        try {
            GradientDrawable drawable = (GradientDrawable) header.getBackground();
            drawable.setColor(ContextCompat.getColor(context, headerBackGroundColor));

        }catch (ClassCastException cce){
            header.setBackgroundResource(headerBackGroundColor);
        }
    }
}
