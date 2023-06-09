package com.carecloud.carepay.patient.myhealth.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.MyHealthViewModel;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthProviderDto;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * @author pjohnson on 19/07/17.
 */

public class CareTeamDetailFragment extends BaseFragment {

    private MyHealthInterface callback;
    private MyHealthProviderDto provider;
    private MyHealthDto myHealthDto;

    public CareTeamDetailFragment() {

    }

    /**
     * @param id the provider id
     * @return a new instance of AllergyDetailFragment
     */
    public static CareTeamDetailFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString("providerId", id);
        CareTeamDetailFragment fragment = new CareTeamDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (MyHealthInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the Activity must implement MyHealthInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        MyHealthViewModel model = ViewModelProviders.of(getActivity()).get(MyHealthViewModel.class);
        myHealthDto = model.getMyHealthDto().getValue();
        provider = getProvider(getArguments().getString("providerId"));
    }

    private MyHealthProviderDto getProvider(String  providerId) {
        for (MyHealthProviderDto provider : myHealthDto.getPayload().getMyHealthData().getProviders().getProviders()) {
            if (providerId == provider.getId()) {
                return provider;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_care_team_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        setUpUI(view);
    }

    private void setUpUI(View view) {
        final TextView avatarTextView = view.findViewById(R.id.avatarTextView);
        avatarTextView.setText(StringUtil.getShortName(provider.getFullName()));
        final ImageView providerImageView = view.findViewById(R.id.providerImageView);
        Picasso.with(getContext()).load(provider.getPhoto())
                .transform(new CircleImageTransform())
                .into(providerImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        providerImageView.setVisibility(View.VISIBLE);
                        avatarTextView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        providerImageView.setVisibility(View.GONE);
                        avatarTextView.setVisibility(View.VISIBLE);
                    }
                });

        TextView providerNameTextView = view.findViewById(R.id.providerNameTextView);
        providerNameTextView.setText(provider.getFullName());
        TextView specialityValueTextView = view.findViewById(R.id.specialityValueTextView);
        specialityValueTextView.setText(provider.getSpecialityName());
        TextView practiceValueTextView = view.findViewById(R.id.practiceValueTextView);
        practiceValueTextView.setText(provider.getPractice());
        TextView addressValueTextView = view.findViewById(R.id.addressValueTextView);
        addressValueTextView.setText(provider.getAddress().getAddress().getLine1());
        TextView zipCodeValueTextView = view.findViewById(R.id.zipCodeValueTextView);
        zipCodeValueTextView.setText(provider.getAddress().getAddress().getZipCode());
        TextView stateValueTextView = view.findViewById(R.id.stateValueTextView);
        stateValueTextView.setText(provider.getAddress().getAddress().getStateName());
        TextView cityValueTextView = view.findViewById(R.id.cityValueTextView);
        cityValueTextView.setText(provider.getAddress().getAddress().getCity());
        TextView phoneValueTextView = view.findViewById(R.id.phoneValueTextView);
        phoneValueTextView.setText(provider.getPhone());
        phoneValueTextView.setOnClickListener(view1 -> dialPhoneNumber(provider.getPhone()));
        view.findViewById(R.id.medicalRecordButton).setOnClickListener(view12 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(Label.getLabel("my_health_download_confirm_message"))
                    .setPositiveButton(Label.getLabel("my_health_confirm_download_button_label"),
                            (dialog, id) -> callback.onSeeAllFullMedicalRecordClicked(provider))
                    .setNegativeButton(Label.getLabel("my_health_cancel"),
                            (dialog, id) -> dialog.dismiss());
            builder.create().show();
        });
    }

    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void setUpToolbar(View view) {
        callback.displayToolbar(false, null);
        Toolbar toolbar = view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> getActivity().onBackPressed());
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("my_health_care_team_detail_title"));
    }
}
