package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckCompleteInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.models.PatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

/**
 * Created by lmenendez on 4/11/17
 */

public class CheckInCompletedDialogFragment extends BaseDialogFragment {

    private boolean hasPayment;
    private AppointmentDTO selectedAppointment;
    private String userImageUrl;
    private CheckCompleteInterface callback;
    private PatientPaymentPayload patientPaymentPayload;

    private @Defs.AppointmentNavigationTypeDef int appointmentNavigationType;

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
     * @param hasPayment             boolean indicating if there has been a payment in the process
     * @param appointmentsPayloadDTO the appointment balances
     * @return an instance of CheckInCompletedDialogFragment
     */
    public static CheckInCompletedDialogFragment newInstance(boolean hasPayment, AppointmentDTO appointmentsPayloadDTO) {
        Bundle args = new Bundle();
        args.putBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, hasPayment);
        if (appointmentsPayloadDTO != null) {
            DtoHelper.bundleDto(args, appointmentsPayloadDTO);
        }
        CheckInCompletedDialogFragment fragment = new CheckInCompletedDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        hasPayment = getArguments().getBoolean(CarePayConstants.EXTRA_HAS_PAYMENT, false);
        DTO dto = callback.getDto();
        if (hasPayment) {
            patientPaymentPayload = ((PaymentsModel) dto).getPaymentPayload().getPatientPayments().getPayload().get(0);
            selectedAppointment = DtoHelper.getConvertedDTO(AppointmentDTO.class, getArguments());
            userImageUrl = ((PaymentsModel) dto).getPaymentPayload().getPatientBalances().get(0)
                    .getDemographics().getPayload().getPersonalDetails().getProfilePhoto();
        } else {
            selectedAppointment = ((AppointmentsResultModel) dto).getPayload().getAppointments().get(0);
            userImageUrl = ((AppointmentsResultModel) dto).getPayload().getPatientBalances().get(0)
                    .getDemographics().getPayload().getPersonalDetails().getProfilePhoto();
        }
        appointmentNavigationType = getApplicationPreferences().getAppointmentNavigationOption();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_checkin_complete, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        userNameTextView.setText(selectedAppointment.getPayload().getPatient().getFirstName() + " "
                + selectedAppointment.getPayload().getPatient().getLastName());
        TextView appointmentHourTextView = (TextView) view.findViewById(R.id.appointmentHourTextView);
        appointmentHourTextView.setText(DateUtil.getHoursFormatted(selectedAppointment.getPayload()
                .getStartTime()));
        TextView appointmentProviderTextView = (TextView) view.findViewById(R.id.appointmentProviderTextView);
        appointmentProviderTextView.setText(String.format(Label.getLabel("checkin_complete_provider_label"),
                selectedAppointment.getPayload().getProvider().getName()));

        TextView appointmentStatusTextView = (TextView) view.findViewById(R.id.appointmentStatusTextView);
        appointmentStatusTextView.setText(selectedAppointment.getPayload().getAppointmentStatus().getName());

        TextView appointmentVisitTypeTextView = (TextView) view.findViewById(R.id.appointmentVisitTypeTextView);
        appointmentVisitTypeTextView.setText(selectedAppointment.getPayload().getVisitType().getName());

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
            paymentTypeTextView.setText(patientPaymentPayload.getType());

            TextView paymentMethodTextView = (TextView) view.findViewById(R.id.paymentMethodTextView);
            paymentMethodTextView.setText(patientPaymentPayload.getMethod());

            TextView confirmationNumberTextView = (TextView) view.findViewById(R.id.confirmationNumberTextView);
            confirmationNumberTextView.setText(patientPaymentPayload.getConfirmation());

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
            TextView totalPaidTextView = (TextView) view.findViewById(R.id.totalPaidTextView);
            totalPaidTextView.setText(currencyFormatter.format(patientPaymentPayload.getTotal()));

        } else {
            paymentTypeTextView.setText(Label.getLabel("payment_confirm_type_no_paid"));
        }

        if(appointmentNavigationType == Defs.NAVIGATE_CHECKOUT){
            TextView successMessage = (TextView) view.findViewById(R.id.successMessage);
            successMessage.setText(Label.getLabel("confirm_appointment_checkout"));
        }

    }
}
