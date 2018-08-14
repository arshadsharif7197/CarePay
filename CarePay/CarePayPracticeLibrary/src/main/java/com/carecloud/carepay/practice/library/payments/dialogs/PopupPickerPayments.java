package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;

/**
 * Created by lmenendez on 3/15/17.
 */

public class PopupPickerPayments extends PopupWindow {
    private Context context;
    private UserPracticeDTO practiceDTO;
    private PaymentPopupListener callback;
    private boolean hasHistory;
    private View popupBackgroundView;

    /**
     * Constructor
     * @param context context
     */
    public PopupPickerPayments(Context context,
                               UserPracticeDTO practiceDTO,
                               PaymentPopupListener callback,
                               boolean hasHistory){
        super(context);
        this.context = context;
        this.practiceDTO = practiceDTO;
        this.callback = callback;
        this.hasHistory = hasHistory;

        initView();
    }

    private void initView(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_picker_payments, null, false);
        popupBackgroundView = view.findViewById(R.id.popup_container);
        setContentView(view);
        setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10);
        }
    }

    @Override
    public void setContentView(View view){
        super.setContentView(view);

        setWidth(context.getResources().getDimensionPixelSize(R.dimen.popup_picker_width));

        View historyItem = view.findViewById(R.id.history_action_item);
        if(hasHistory) {
            historyItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onHistoryAction();
                    dismiss();
                }
            });
        } else {
            view.findViewById(R.id.history_item_layout).setVisibility(View.GONE);
        }

        View addChargeItem = view.findViewById(R.id.add_charge_action_item);
        addChargeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onAddChargeAction();
                dismiss();
            }
        });

        //TODO enable this once retail is ready to release
        boolean isRetailEnabled = practiceDTO.isRetailEnabled();
        View addRetailItem = view.findViewById(R.id.add_retail_action_item);
        if(isRetailEnabled){
            addRetailItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onAddRetailAction();
                    dismiss();
                }
            });
        } else{
            view.findViewById(R.id.retail_item_layout).setVisibility(View.GONE);
        }
    }

    /**
     * Set the background to top view if necessary
     * @param showOnTop true to show above th anchor view
     */
    public void flipPopup(boolean showOnTop){
        if(showOnTop){
            popupBackgroundView.setBackgroundResource(R.drawable.popup_picker_bg_top_new);
        }else{
            popupBackgroundView.setBackgroundResource(R.drawable.popup_picker_bg_new);
        }
    }

    public interface PaymentPopupListener {
        void onHistoryAction();

        void onAddChargeAction();

        void onAddRetailAction();
    }
}
