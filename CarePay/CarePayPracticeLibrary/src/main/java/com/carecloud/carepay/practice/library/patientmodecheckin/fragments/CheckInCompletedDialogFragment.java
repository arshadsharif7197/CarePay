package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.adhocforms.AdHocRecyclerViewNameAdapter;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckCompleteInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by lmenendez on 4/11/17
 */

public class CheckInCompletedDialogFragment extends BaseDialogFragment {

    private boolean hasPayment;
    private boolean isAdHocForms;
    private AppointmentDTO selectedAppointment;
    private String userImageUrl;
    private CheckCompleteInterface callback;
    private IntegratedPatientPaymentPayload patientPaymentPayload;
    private List<String> filledForms;

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
     * @param hasPayment     boolean indicating if there has been a payment in the process
     * @param isAdHocForms   indicates if its in the adhoc flow
     * @return an instance of CheckInCompletedDialogFragment
     */
    public static CheckInCompletedDialogFragment newInstance(AppointmentDTO appointmentDTO,
                                                             boolean hasPayment, boolean isAdHocForms) {
        Bundle args = new Bundle();
        args.putBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, hasPayment);
        args.putBoolean(CarePayConstants.ADHOC_FORMS, isAdHocForms);
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
        hasPayment = getArguments().getBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, false);
        isAdHocForms = getArguments().getBoolean(CarePayConstants.ADHOC_FORMS, false);
        DTO dto = callback.getDto();
        if (dto != null) {
            selectedAppointment = DtoHelper.getConvertedDTO(AppointmentDTO.class, getArguments());
            if (selectedAppointment == null) {
                selectedAppointment = DtoHelper.getConvertedDTO(AppointmentDTO.class, icicle);
            }
            if (hasPayment) {
                patientPaymentPayload = ((PaymentsModel) dto).getPaymentPayload().getPatientPayments().getPayload();
                userImageUrl = ((PaymentsModel) dto).getPaymentPayload().getPatientBalances().get(0)
                        .getDemographics().getPayload().getPersonalDetails().getProfilePhoto();
            } else if (isAdHocForms) {
//                selectedAppointment = ((AppointmentsResultModel) dto).getPayload().getAppointments().get(0);
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
            }
        }
        appointmentNavigationType = getApplicationPreferences().getAppointmentNavigationOption();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        DtoHelper.bundleDto(icicle, selectedAppointment);
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

        view.findViewById(R.id.paymentInformation).setVisibility(hasPayment ? View.VISIBLE : View.GONE);

        final ImageView userImage = (ImageView) view.findViewById(R.id.userImage);
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

        TextView userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
        TextView appointmentHourTextView = (TextView) view.findViewById(R.id.appointmentHourTextView);
        if (isAdHocForms) {
            userNameTextView.setText(((AppointmentsResultModel) callback.getDto()).getPayload().getDemographicDTO().getPayload()
                    .getPersonalDetails().getFullName());
            appointmentHourTextView.setText("Now");
            view.findViewById(R.id.statusContainer).setVisibility(View.GONE);
            view.findViewById(R.id.separator1).setVisibility(View.GONE);
            view.findViewById(R.id.visitTypeContainer).setVisibility(View.GONE);
            view.findViewById(R.id.separator2).setVisibility(View.GONE);
        } else {
            userNameTextView.setText(selectedAppointment.getPayload().getPatient().getFirstName() + " "
                    + selectedAppointment.getPayload().getPatient().getLastName());
            appointmentHourTextView.setText(DateUtil.getHoursFormatted(selectedAppointment.getPayload()
                    .getStartTime()));
            TextView appointmentProviderTextView = (TextView) view.findViewById(R.id.appointmentProviderTextView);
            appointmentProviderTextView.setText(String.format(Label.getLabel("checkin_complete_provider_label"),
                    selectedAppointment.getPayload().getProvider().getName()));
            TextView appointmentStatusTextView = (TextView) view.findViewById(R.id.appointmentStatusTextView);
            String status = selectedAppointment.getPayload().getAppointmentStatus().getName();
            if ("checked-in".equals(status.toLowerCase()) || "checked-out".equals(status.toLowerCase())) {
                status = String.format(Label.getLabel("confirm_appointment_checkout_status"), status);
            }
            appointmentStatusTextView.setText(status);
            TextView appointmentVisitTypeTextView = (TextView) view.findViewById(R.id.appointmentVisitTypeTextView);
            appointmentVisitTypeTextView.setText(selectedAppointment.getPayload().getVisitType().getName());
        }

        TextView continueTextView = (TextView) view.findViewById(R.id.continueTextView);
        continueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.logout();
            }
        });
        ImageView homeModeSwitchImageView = (ImageView) view.findViewById(R.id.homeModeSwitchImageView);
        homeModeSwitchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showConfirmationPinDialog();
            }
        });


        TextView paymentTypeTextView = (TextView) view.findViewById(R.id.paymentTypeTextView);
        if (hasPayment) {
            paymentTypeTextView.setText(Label.getLabel("payment_type_one_time"));

            TextView paymentMethodTextView = (TextView) view.findViewById(R.id.paymentMethodTextView);
            paymentMethodTextView.setText(PaymentConfirmationFragment.getPaymentMethod(patientPaymentPayload));

            TextView confirmationNumberTextView = (TextView) view.findViewById(R.id.confirmationNumberTextView);
            confirmationNumberTextView.setText(patientPaymentPayload.getConfirmation());

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
            TextView totalPaidTextView = (TextView) view.findViewById(R.id.totalPaidTextView);
            totalPaidTextView.setText(currencyFormatter.format(patientPaymentPayload.getTotalPaid()));

            //todo display possible errors

        } else if (isAdHocForms) {
            setUpForAdHocForms(view);
        } else {
            paymentTypeTextView.setText(Label.getLabel("payment_confirm_type_no_paid"));
        }

        if (appointmentNavigationType == Defs.NAVIGATE_CHECKOUT) {
            TextView successMessage = (TextView) view.findViewById(R.id.successMessage);
            successMessage.setText(Label.getLabel("confirm_appointment_checkout"));
        }

    }

    private void setUpForAdHocForms(View view) {
        ((TextView) view.findViewById(R.id.paymentDetailsLabel))
                .setText(Label.getLabel("adhoc_final_step_signed_forms_label"));
        ((TextView) view.findViewById(R.id.successMessage))
                .setText(Label.getLabel("adhoc_final_step_message"));
        view.findViewById(R.id.paymentTypeLayout).setVisibility(View.GONE);
        view.findViewById(R.id.continueTextView).setVisibility(View.GONE);
        view.findViewById(R.id.separator3).setVisibility(View.GONE);
        RecyclerView signedFormsRecyclerView = (RecyclerView) view.findViewById(R.id.signedFormsRecyclerView);
        signedFormsRecyclerView.setVisibility(View.VISIBLE);
        signedFormsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdHocRecyclerViewNameAdapter adapter = new AdHocRecyclerViewNameAdapter(filledForms);
        signedFormsRecyclerView.setAdapter(adapter);
    }
}
