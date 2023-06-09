package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.adhocforms.adapters.AdHocRecyclerViewNameAdapter;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckCompleteInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.survey.model.SurveyDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment.MODE_ADD;
import static com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment.MODE_CREATE;
import static com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment.MODE_EDIT;

/**
 * Created by lmenendez on 4/11/17
 */

public class CheckInCompletedDialogFragment extends BaseDialogFragment {

    private boolean hasPayment;
    private boolean isCash;
    private boolean hasPaymentPlan;
    private boolean isAdHocForms;
    private @PaymentPlanConfirmationFragment.ConfirmationMode
    int confirmationMode;

    private AppointmentDTO selectedAppointment;
    private String userImageUrl;
    private CheckCompleteInterface callback;
    private IntegratedPatientPaymentPayload patientPaymentPayload;
    private PaymentPlanDTO paymentPlanDTO;
    private List<String> filledForms;
    private PaymentsModel paymentsModel;

    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    private boolean storeButtonEnabled = false;

    private
    @Defs.AppointmentNavigationTypeDef
    int appointmentNavigationType;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (CheckCompleteInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement CheckCompleteInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    /**
     * @param appointmentDTO the appointment balances
     * @param extras         extra flags
     * @return an instance of CheckInCompletedDialogFragment
     */
    public static CheckInCompletedDialogFragment newInstance(AppointmentDTO appointmentDTO,
                                                             Bundle extras) {
        Bundle args = new Bundle();
        args.putAll(extras);
        if (appointmentDTO != null) {
            DtoHelper.bundleDto(args, appointmentDTO);
        }
        CheckInCompletedDialogFragment fragment = new CheckInCompletedDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        if (args != null) {
            hasPayment = args.getBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, false);
            isCash = args.getBoolean(CarePayConstants.EXTRA_PAYMENT_CASH, false);
            isAdHocForms = args.getBoolean(CarePayConstants.ADHOC_FORMS, false);
            confirmationMode = args.getInt(CarePayConstants.EXTRA_CONFIRMATION_MODE, -1);
        }
        appointmentNavigationType = getApplicationPreferences().getAppointmentNavigationOption();
        initPayloads(icicle);
    }

    private boolean initPayloads(Bundle icicle) {
        DTO dto = callback.getDto();
        if (dto != null) {
            selectedAppointment = DtoHelper.getConvertedDTO(AppointmentDTO.class, getArguments());
            if (selectedAppointment == null) {
                selectedAppointment = DtoHelper.getConvertedDTO(AppointmentDTO.class, icicle);
            }
            if (hasPayment || isCash) {
                paymentsModel = (PaymentsModel) dto;
                hasPaymentPlan = confirmationMode > -1;
                if (hasPaymentPlan) {
                    paymentPlanDTO = paymentsModel.getPaymentPayload().getPatientPaymentPlans().get(0);
                } else {
                    patientPaymentPayload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload();
                    userImageUrl = paymentsModel.getPaymentPayload().getPatientBalances().get(0)
                            .getDemographics().getPayload().getPersonalDetails().getProfilePhoto();
                }
                storeButtonEnabled = paymentsModel.getPaymentPayload().getUserPractices().get(0).isRetailEnabled();
            } else if (isAdHocForms) {
                filledForms = ((AppointmentsResultModel) dto).getPayload().getFilledForms();
                userImageUrl = ((AppointmentsResultModel) dto).getPayload().getDemographicDTO().getPayload()
                        .getPersonalDetails().getProfilePhoto();
            } else {
                if (selectedAppointment == null) {
                    selectedAppointment = ((AppointmentsResultModel) dto).getPayload().getAppointments().get(0);
                }
                if (!((AppointmentsResultModel) dto).getPayload().getPatientBalances().isEmpty()) {
                    userImageUrl = ((AppointmentsResultModel) dto).getPayload().getPatientBalances().get(0)
                            .getDemographics().getPayload().getPersonalDetails().getProfilePhoto();
                }
                storeButtonEnabled = ((AppointmentsResultModel) dto).getPayload().getUserPractices()
                        .get(0).isRetailEnabled();
            }

            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        if (selectedAppointment != null) {
            DtoHelper.bundleDto(icicle, selectedAppointment);
        }
        super.onSaveInstanceState(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_checkin_complete, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (selectedAppointment == null && !isAdHocForms) {
            return;
        }

        if (!initPayloads(savedInstanceState)) {
            return;
        }

        view.findViewById(R.id.paymentInformation)
                .setVisibility((hasPayment && !hasPaymentPlan) || isCash ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.paymentPlanInformation).setVisibility(hasPaymentPlan ? View.VISIBLE : View.GONE);

        final ImageView userImage = view.findViewById(R.id.userImage);
        Picasso.with(getContext()).load(userImageUrl)
                .placeholder(R.drawable.icn_placeholder_user_profile_png)
                .transform(new CircleImageTransform())
                .into(userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        userImage.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                R.drawable.icn_placeholder_user_profile_png));

                    }
                });

        TextView userNameTextView = view.findViewById(R.id.userNameTextView);
        TextView appointmentHourTextView = view.findViewById(R.id.appointmentHourTextView);
        if (isAdHocForms) {
            userNameTextView.setText(((AppointmentsResultModel) callback.getDto())
                    .getPayload().getDemographicDTO().getPayload()
                    .getPersonalDetails().getFullName());
            appointmentHourTextView.setText(DateUtil.getHoursFormatted(new Date()));
            view.findViewById(R.id.statusContainer).setVisibility(View.GONE);
            view.findViewById(R.id.separator1).setVisibility(View.GONE);
            view.findViewById(R.id.visitTypeContainer).setVisibility(View.GONE);
            view.findViewById(R.id.separator2).setVisibility(View.GONE);
        } else {
            userNameTextView.setText(String.format("%s %s", selectedAppointment.getPayload().getPatient().getFirstName(),
                    selectedAppointment.getPayload().getPatient().getLastName()));
            appointmentHourTextView.setText(DateUtil.getHoursFormatted(selectedAppointment.getPayload()
                    .getStartTime()));
            TextView appointmentProviderTextView = view.findViewById(R.id.appointmentProviderTextView);
            appointmentProviderTextView.setText(String.format(Label.getLabel("checkin_complete_provider_label"),
                    selectedAppointment.getPayload().getProvider().getName()));
            TextView appointmentStatusTextView = view.findViewById(R.id.appointmentStatusTextView);
            String status = selectedAppointment.getPayload().getAppointmentStatus().getName();
            if ("checked-in".equals(status.toLowerCase()) || "checked-out".equals(status.toLowerCase())) {
                status = String.format(Label.getLabel("confirm_appointment_checkout_status"), status.replace("-", " "));
            }
            appointmentStatusTextView.setText(status);
            TextView appointmentVisitTypeTextView = view.findViewById(R.id.appointmentVisitTypeTextView);
            appointmentVisitTypeTextView.setText(selectedAppointment.getPayload().getVisitType().getName());
        }

        TextView continueTextView = view.findViewById(R.id.continueTextView);
        continueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.logout();
            }
        });
        TextView goToStoreTextView = view.findViewById(R.id.browseOurShopTextView);
        goToStoreTextView.setVisibility(storeButtonEnabled && !isCash ? View.VISIBLE : View.GONE);
        goToStoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.goToShop();
            }
        });
        ImageView homeModeSwitchImageView = view.findViewById(R.id.homeModeSwitchImageView);
        homeModeSwitchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showConfirmationPinDialog();
            }
        });


        TextView paymentTypeTextView = view.findViewById(R.id.paymentTypeTextView);
        if (hasPayment && !hasPaymentPlan) {
            paymentTypeTextView.setText(Label.getLabel("payment_type_one_time"));

            TextView paymentMethodTextView = view.findViewById(R.id.paymentMethodTextView);
            paymentMethodTextView.setText(PaymentConfirmationFragment.getPaymentMethod(patientPaymentPayload));

            TextView confirmationNumberTextView = view.findViewById(R.id.confirmationNumberTextView);
            confirmationNumberTextView.setText(patientPaymentPayload.getConfirmation());

            TextView totalPaidTextView = view.findViewById(R.id.totalPaidTextView);
            totalPaidTextView.setText(currencyFormatter.format(patientPaymentPayload.getTotalPaid()));

            if (appointmentNavigationType == Defs.NAVIGATE_CHECKOUT) {
                TextView successMessage = view.findViewById(R.id.successMessage);
                successMessage.setText(Label.getLabel("confirm_appointment_checkout"));
            }
            //todo display possible errors

        } else if (hasPaymentPlan) {
            PaymentPlanPayloadDTO paymentPlanPayloadDTO = paymentPlanDTO.getPayload();

            paymentTypeTextView.setText(getMessageLabel());

            TextView totalAmount = view.findViewById(R.id.payment_confirm_amount_value);
            totalAmount.setText(currencyFormatter.format(paymentPlanPayloadDTO.getAmount()));

            TextView installments = view.findViewById(R.id.payment_confirm_installments_value);
            installments.setText(String.valueOf(paymentPlanPayloadDTO.getPaymentPlanDetails().getInstallments()));

            String paymentAmountString = currencyFormatter
                    .format(paymentPlanPayloadDTO.getPaymentPlanDetails().getAmount()) +
                    paymentPlanPayloadDTO.getPaymentPlanDetails().getFrequencyString();
            TextView paymentAmount = view.findViewById(R.id.payment_confirm_payment_value);
            paymentAmount.setText(paymentAmountString);

            TextView dueDate = view.findViewById(R.id.payment_confirm_due_value);
            dueDate.setText(StringUtil.getOrdinal(getApplicationPreferences().getUserLanguage(),
                    paymentPlanPayloadDTO.getPaymentPlanDetails().getDayOfMonth()));

        } else if (isAdHocForms) {
            setUpForAdHocForms(view);
        } else if (isCash) {
            view.findViewById(R.id.paymentTypeLayout).setVisibility(View.GONE);
            view.findViewById(R.id.separator3).setVisibility(View.GONE);

            view.findViewById(R.id.confirmationNumberLayout).setVisibility(View.GONE);
            view.findViewById(R.id.separator6).setVisibility(View.GONE);

            view.findViewById(R.id.continueTextView).setVisibility(View.GONE);

            ImageView successImage = view.findViewById(R.id.checkImage);
            successImage.setImageResource(R.drawable.cash_success);

            TextView paymentMethodTextView = view.findViewById(R.id.paymentMethodTextView);
            paymentMethodTextView.setText(Label.getLabel("payment_method_cash"));

            TextView totalLabel = view.findViewById(R.id.totalPaidLabel);
            totalLabel.setText(Label.getLabel("payment_confirm_cash_total"));

            //only take first balance since this is practice app
            double pendingAmount = paymentsModel.getPaymentPayload().getPatientBalances().get(0)
                    .getBalances().get(0).getPayload().get(0).getAmount();
            TextView totalAmount = view.findViewById(R.id.totalPaidTextView);
            totalAmount.setText(currencyFormatter.format(pendingAmount));

            View main = view.findViewById(R.id.mainLayout);
            main.setBackgroundResource(R.color.lightning_yellow);

            TextView successMessage = view.findViewById(R.id.successMessage);
            if (appointmentNavigationType == Defs.NAVIGATE_CHECKOUT) {
                successMessage.setText(Label.getLabel("confirm_appointment_checkout_cash_payment_message"));
            } else {
                successMessage.setText(Label.getLabel("confirm_appointment_checkin_cash_payment_message"));
            }

        } else {
            paymentTypeTextView.setText(Label.getLabel("payment_confirm_type_no_paid"));

            if (appointmentNavigationType == Defs.NAVIGATE_CHECKOUT) {
                TextView successMessage = view.findViewById(R.id.successMessage);
                successMessage.setText(Label.getLabel("confirm_appointment_checkout"));
            }
        }

        manageSurvey(view);

    }

    private void manageSurvey(View view) {
        Bundle extra = getArguments();
        long id = extra.getLong(SurveyDTO.class.getSimpleName(), -1);
        if (id > 0 && !isCash) {
            if (view.findViewById(R.id.browseOurShopTextView).getVisibility() == View.VISIBLE) {
                view.findViewById(R.id.continueTextView).setVisibility(View.GONE);
            }
            TextView surveyButton = view.findViewById(R.id.surveyButton);
            surveyButton.setVisibility(View.VISIBLE);
            surveyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.fillSurvey();
                }
            });
        }
    }

    private void setUpForAdHocForms(View view) {
        boolean isPlural = filledForms.size() > 1;
        ((TextView) view.findViewById(R.id.paymentDetailsLabel))
                .setText(Label.getLabel(isPlural ? "adhoc_final_step_signed_forms_label"
                        : "adhoc_final_step_signed_forms_label_singular"));
        ((TextView) view.findViewById(R.id.successMessage))
                .setText(Label.getLabel(isPlural ? "adhoc_final_step_message" : "adhoc_final_step_message_singular"));
        view.findViewById(R.id.paymentTypeLayout).setVisibility(View.GONE);
        view.findViewById(R.id.continueTextView).setVisibility(View.GONE);
        view.findViewById(R.id.separator3).setVisibility(View.GONE);
        RecyclerView signedFormsRecyclerView = view.findViewById(R.id.signedFormsRecyclerView);
        signedFormsRecyclerView.setVisibility(View.VISIBLE);
        signedFormsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdHocRecyclerViewNameAdapter adapter = new AdHocRecyclerViewNameAdapter(filledForms);
        signedFormsRecyclerView.setAdapter(adapter);
    }

    private String getMessageLabel() {
        switch (confirmationMode) {
            case MODE_ADD:
                return Label.getLabel("payment_plan_success_add_short");
            case MODE_EDIT:
                return Label.getLabel("payment_plan_success_edit_short");
            case MODE_CREATE:
            default:
                return Label.getLabel("payment_plan_success_create_short");
        }
    }

}
