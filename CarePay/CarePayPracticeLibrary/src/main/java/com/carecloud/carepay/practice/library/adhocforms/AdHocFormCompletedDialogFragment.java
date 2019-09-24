package com.carecloud.carepay.practice.library.adhocforms;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.adhocforms.adapters.AdHocRecyclerViewNameAdapter;
import com.carecloud.carepay.practice.library.customdialog.ConfirmationPinDialog;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by lmenendez on 4/11/17
 */

public class AdHocFormCompletedDialogFragment extends BaseDialogFragment {

    private String userImageUrl;
    private FragmentActivityInterface callback;
    private List<String> filledForms;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentActivityInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement CheckCompleteInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public static AdHocFormCompletedDialogFragment newInstance() {
        Bundle args = new Bundle();
        AdHocFormCompletedDialogFragment fragment = new AdHocFormCompletedDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        initPayloads();
        logMixPanelEvent();
    }

    private void logMixPanelEvent() {
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_patient_id),
                getString(R.string.param_forms_count)};
        Object[] values = {getApplicationMode().getUserPracticeDTO().getPracticeId(),
                ((AdHocFormsModel) callback.getDto()).getPayload().getAdhocFormsPatientModeInfo()
                        .getMetadata().getPatientId(), filledForms.size()};
        MixPanelUtil.logEvent(getString(R.string.event_adhoc_forms_completed), params, values);
    }

    private boolean initPayloads() {
        AdHocFormsModel dto = (AdHocFormsModel) callback.getDto();
        filledForms = dto.getPayload().getFilledForms();
        userImageUrl = dto.getPayload().getDemographicDTO().getPayload().getPersonalDetails().getProfilePhoto();

        return true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_checkin_complete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.paymentInformation).setVisibility(View.GONE);
        view.findViewById(R.id.paymentPlanInformation).setVisibility(View.GONE);

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
        userNameTextView.setText(((AdHocFormsModel) callback.getDto())
                .getPayload().getDemographicDTO().getPayload()
                .getPersonalDetails().getFullName());
        appointmentHourTextView.setText(DateUtil.getHoursFormatted(new Date()));
        view.findViewById(R.id.statusContainer).setVisibility(View.GONE);
        view.findViewById(R.id.separator1).setVisibility(View.GONE);
        view.findViewById(R.id.visitTypeContainer).setVisibility(View.GONE);
        view.findViewById(R.id.separator2).setVisibility(View.GONE);

        TextView continueTextView = view.findViewById(R.id.continueTextView);
        continueTextView.setVisibility(View.GONE);
        TextView goToStoreTextView = view.findViewById(R.id.browseOurShopTextView);
        goToStoreTextView.setVisibility(View.GONE);
        ImageView homeModeSwitchImageView = view.findViewById(R.id.homeModeSwitchImageView);
        homeModeSwitchImageView.setOnClickListener(view1 -> showConfirmationPinDialog());
        setUpForAdHocForms(view);
    }

    private void showConfirmationPinDialog() {
        ConfirmationPinDialog confirmationPinDialog = ConfirmationPinDialog.newInstance(
                ((AdHocFormsModel) callback.getDto()).getMetadata().getLinks().getPinpad(),
                false,
                ((AdHocFormsModel) callback.getDto()).getMetadata().getLinks().getLanguage());
        callback.displayDialogFragment(confirmationPinDialog, false);
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

}
