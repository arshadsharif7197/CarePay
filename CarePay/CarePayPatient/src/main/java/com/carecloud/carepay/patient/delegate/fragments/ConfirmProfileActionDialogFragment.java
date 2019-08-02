package com.carecloud.carepay.patient.delegate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.delegate.interfaces.ProfileConfirmationCallback;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2019-07-12.
 */
public class ConfirmProfileActionDialogFragment extends BaseDialogFragment {

    private ProfileConfirmationCallback callback;
    private ProfileDto profileDto;

    public static ConfirmProfileActionDialogFragment newInstance(ProfileDto profileDto, String title, String message) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, profileDto);
        args.putString("title", title);
        args.putString("message", message);
        ConfirmProfileActionDialogFragment fragment = new ConfirmProfileActionDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        profileDto = DtoHelper.getConvertedDTO(ProfileDto.class, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm_profile_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView dialogTitle = view.findViewById(R.id.dialogTitle);
        dialogTitle.setText(getArguments().getString("title"));
        TextView dialogMessage = view.findViewById(R.id.dialogMessage);
        dialogMessage.setText(getArguments().getString("message"));
        Button yesButton = view.findViewById(R.id.yesButton);
        yesButton.setOnClickListener(view1 -> {
            callback.confirmAction(profileDto);
            dismiss();
        });
        Button noButton = view.findViewById(R.id.noButton);
        noButton.setOnClickListener(view1 -> {
            callback.cancel();
            dismiss();
        });
    }

    public void setCallback(ProfileConfirmationCallback mergeProfileCallback) {
        callback = mergeProfileCallback;
    }
}
