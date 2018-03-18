package com.carecloud.carepay.patient.payment.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

public class ChooseCreditCardDialog extends Dialog implements RadioGroup.OnCheckedChangeListener {

    private Context context;
    private PaymentsModel paymentsModel;
    private RadioGroup chooseCreditCardRadioGroup;
    private ChooseCreditCardClickListener listener;

    /**
     * Constructor.
     * @param context context
     * @param paymentsModel payment model item
     * @param listener Onclick listener
     */
    @SuppressWarnings("ConstantConditions")
    public ChooseCreditCardDialog(Context context, PaymentsModel paymentsModel,
                                  ChooseCreditCardClickListener listener) {

        super(context);
        this.context = context;
        this.paymentsModel = paymentsModel;
        this.listener = listener;

        // This is the layout XML file that describes your Dialog layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_choose_credit_card);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        init();
    }

    private void init() {
        RadioGroup.LayoutParams radioGroupLayoutParam = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        int margin = context.getResources().getDimensionPixelSize(R.dimen.dimen_5dp);
        radioGroupLayoutParam.setMargins(margin, margin, margin, margin);

        chooseCreditCardRadioGroup = (RadioGroup) findViewById(R.id.choose_credit_card_list);
        chooseCreditCardRadioGroup.setOnCheckedChangeListener(this);

        if (paymentsModel != null) {

                List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardList = paymentsModel.getPaymentPayload().getPatientCreditCards();

                for (int i = 0; i < creditCardList.size(); i++) {
                    PaymentCreditCardsPayloadDTO creditCardItem = creditCardList.get(i).getPayload();
                    String creditCardName = creditCardItem.getCardType();
                    chooseCreditCardRadioGroup.addView(getCreditCardRadioButton(
                            StringUtil.getEncodedCardNumber(creditCardName,
                                    creditCardItem.getCardNumber()), i), radioGroupLayoutParam);

                    View dividerLineView = new View(context);
                    dividerLineView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1));

                    dividerLineView.setBackgroundColor(ContextCompat.getColor(context, R.color.cadet_gray));
                    chooseCreditCardRadioGroup.addView(dividerLineView);
                    onSetRadioButtonRegularTypeFace();
                }


            ((TextView) findViewById(R.id.choose_credit_card_title)).setText(Label.getLabel("payment_choose_credit_card_button"));
            ((TextView) findViewById(R.id.choose_credit_card_cancel)).setText(Label.getLabel("payment_cancel_button"));
        }

        findViewById(R.id.choose_credit_card_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private RadioButton getCreditCardRadioButton(String cardInfo, int index) {
        RadioButton radioButtonView = new RadioButton(context);
        radioButtonView.setId(index);
        radioButtonView.setButtonDrawable(null);
        radioButtonView.setBackground(null);
        radioButtonView.setText(cardInfo);
        radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.payment_credit_card_button_selector, 0, R.drawable.check_box_intake, 0);
        radioButtonView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        radioButtonView.setTextColor(ContextCompat.getColor(context, R.color.slateGray));
        radioButtonView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimensionPixelSize(R.dimen.payment_method_layout_label_text_size));
        radioButtonView.setCompoundDrawablePadding(
                context.getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin));

        return radioButtonView;
    }

    private void onSetRadioButtonRegularTypeFace() {
        for (int i = 0; i < chooseCreditCardRadioGroup.getChildCount(); i++) {
            if (i % 2 == 0) {
                SystemUtil.setProximaNovaRegularTypeface(context, (RadioButton)
                        chooseCreditCardRadioGroup.getChildAt(i));
                ((RadioButton) chooseCreditCardRadioGroup.getChildAt(i))
                        .setTextColor(ContextCompat.getColor(context, R.color.slateGray));
            }
        }
    }

    private void onSetRadioButtonSemiBoldTypeFace(RadioButton radioButton) {
        SystemUtil.setProximaNovaRegularTypeface(context, radioButton);
        radioButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        onSetRadioButtonRegularTypeFace();
        RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
        onSetRadioButtonSemiBoldTypeFace(selectedRadioButton);
        listener.onCreditCardSelection(checkedId);
        dismiss();
    }

    public interface ChooseCreditCardClickListener {
        void onCreditCardSelection(int selectedIndex);
    }
}
