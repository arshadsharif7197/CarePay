package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.interfaces.TelehealthAppointmentCallback;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * @author pjohnson on 2019-06-13.
 */
public class TelehealthAppointmentFragment extends BaseDialogFragment implements View.OnClickListener {

    private TelehealthAppointmentCallback callback;
    private AppointmentDTO appointmentDTO;
    private View backToAppointmentBtn;
    private TextView appointmentDateTextView;
    private TextView appointmentVisitTypeTextView;
    private TextView appointmentTimeTextView;
    private TextView providerInitials;
    private ImageView providerPhoto;
    private TextView providerName;
    private TextView providerSpecialty;
    private TextView locationName;
    private TextView locationAddress;
    private RelativeLayout joinVideoVisitLayout;

    public static TelehealthAppointmentFragment newInstance(AppointmentDTO appointmentDto) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDto);
        TelehealthAppointmentFragment fragment = new TelehealthAppointmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (TelehealthAppointmentCallback) context;
        } else {
            throw new ClassCastException("context must implements FragmentActivityInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_telehealth_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        initView(view);
        setCommonValues();
    }

    private void initView(View view) {
        appointmentDateTextView = view.findViewById(R.id.appointDateTextView);
        appointmentTimeTextView = view.findViewById(R.id.appointTimeTextView);
        appointmentVisitTypeTextView = view.findViewById(R.id.appointmentVisitTypeTextView);

        providerInitials = view.findViewById(R.id.appointShortnameTextView);
        providerPhoto = view.findViewById(R.id.appointUserPicImageView);
        providerName = view.findViewById(R.id.providerName);
        providerSpecialty = view.findViewById(R.id.providerSpecialty);

        locationName = view.findViewById(R.id.appointAddressHeaderTextView);
        locationAddress = view.findViewById(R.id.appointAddressTextView);

        joinVideoVisitLayout = view.findViewById(R.id.join_video_visit_layout);
        joinVideoVisitLayout.setOnClickListener(this);
        backToAppointmentBtn = view.findViewById(R.id.back_to_appointment_btn);
        backToAppointmentBtn.setOnClickListener(this);
    }

    private void setCommonValues() {
        appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, getArguments());

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime());
        appointmentDateTextView.setText(dateUtil.getDateAsWeekdayMonthDayYear());
        appointmentTimeTextView.setText(dateUtil.getTime12Hour());
        appointmentVisitTypeTextView.setText(StringUtil.
                capitalize(appointmentDTO.getPayload().getVisitType().getName()));

        final ProviderDTO provider = appointmentDTO.getPayload().getProvider();
        providerInitials.setText(StringUtil.getShortName(provider.getName()));
        providerName.setText(provider.getName());
        providerSpecialty.setText(provider.getSpecialty().getName());

        int size = getResources().getDimensionPixelSize(R.dimen.apt_dl_image_ht_wdh);
        Picasso.with(getContext())
                .load(provider.getPhoto())
                .resize(size, size)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(providerPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        providerInitials.setVisibility(View.GONE);
                        providerPhoto.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        providerPhoto.setVisibility(View.GONE);
                        providerInitials.setVisibility(View.VISIBLE);
                    }
                });

        LocationDTO location = appointmentDTO.getPayload().getLocation();
        locationAddress.setText(StringUtil.capitalize(location.getName()));
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> callback.onCancel());
        TextView title = toolbar.findViewById(R.id.toolbarTitle);
        title.setText(Label.getLabel("patient.delegate.profileList.title.label"));
    }

    @Override
    public void onClick(View view) {
        if (view == backToAppointmentBtn) {
            callback.onCancel();
        } else if (view == joinVideoVisitLayout) {
            callback.onVideoVisit();
        }
    }
}
