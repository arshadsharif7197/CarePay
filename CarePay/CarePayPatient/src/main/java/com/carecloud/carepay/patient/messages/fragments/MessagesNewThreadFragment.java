package com.carecloud.carepay.patient.messages.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.MessagesViewModel;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

/**
 * Created by lmenendez on 7/7/17
 */

public class MessagesNewThreadFragment extends BaseFragment implements MediaViewInterface {

    private EditText subjectInput;
    private EditText messageInput;
    private EditText attachmentInput;
    private View buttonCreate;
    private View clearAttachmentButton;

    private ProviderContact provider;
    private MessageNavigationCallback callback;
    private MediaScannerPresenter mediaScannerPresenter;

    private File attachmentFile;
    private MessagesViewModel viewModel;

    /**
     * Get a new instance of MessagesNewThreadFragment
     *
     * @param provider provider to use for sending message
     * @return MessagesNewThreadFragment
     */
    public static MessagesNewThreadFragment newInstance(ProviderContact provider) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, provider);
        MessagesNewThreadFragment fragment = new MessagesNewThreadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (MessageNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement MessageNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        if (args != null) {
            provider = DtoHelper.getConvertedDTO(ProviderContact.class, args);
        }
        viewModel = ViewModelProviders.of(getActivity()).get(MessagesViewModel.class);
        viewModel.getNewThreadObservable()
                .observe(this, messagingThreadDTO -> {
                    String[] params = {getString(R.string.param_provider_id), getString(R.string.param_provider_name)};
                    Object[] values = {provider.getId(), provider.getName()};
                    MixPanelUtil.logEvent(getString(R.string.event_message_new), params, values);
                    callback.displayThreadMessages(messagingThreadDTO.getPayload(), true);
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        callback.displayToolbar(false, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_new_thread, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        initToolbar(view);
        TextInputLayout subjectLayout = view.findViewById(R.id.subjectInputLayout);
        subjectInput = view.findViewById(R.id.subjectEditText);
        subjectInput.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(subjectLayout, null));
        subjectInput.addTextChangedListener(getEmptyTextWatcher(subjectLayout));

        TextInputLayout messageLayout = view.findViewById(R.id.messageInputLayout);
        messageInput = view.findViewById(R.id.messageEditText);
        messageInput.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(messageLayout, null));
        messageInput.addTextChangedListener(getEmptyTextWatcher(messageLayout));

        buttonCreate = view.findViewById(R.id.new_message_button);
        buttonCreate.setOnClickListener(view1 -> viewModel.postNewThread(provider, attachmentFile,
                null, subjectInput.getText().toString(), messageInput.getText().toString()));

        attachmentInput = view.findViewById(R.id.attachmentEditText);

        clearAttachmentButton = view.findViewById(R.id.clearBtn);
        clearAttachmentButton.setOnClickListener(view12 -> {
            attachmentFile = null;
            attachmentInput.setText(null);
            view12.setVisibility(View.INVISIBLE);
        });
        setUpBottomSheet(view);
        validateForm();
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(Label.getLabel("messaging_providers_title"));
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> getActivity().onBackPressed());
    }

    private void validateForm() {
        boolean valid = !StringUtil.isNullOrEmpty(subjectInput.getText().toString()) &&
                !StringUtil.isNullOrEmpty(messageInput.getText().toString());
        buttonCreate.setEnabled(valid);
        buttonCreate.setClickable(valid);
    }

    private void setUpBottomSheet(View view) {
        final View shadow = view.findViewById(R.id.shadow);
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        TextView headerLabel = (TextView) findViewById(R.id.totalPatientResponsibilityLabel);
        headerLabel.setText(Label.getLabel("messaging.create.attachment.actions.title"));
        bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    shadow.setClickable(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                shadow.setAlpha(slideOffset);
            }
        });

        View.OnClickListener bottomSheetClickListener = v -> {
            bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_EXPANDED);
            shadow.setClickable(true);
        };
        view.findViewById(R.id.upload_button).setOnClickListener(bottomSheetClickListener);
        attachmentInput.setOnClickListener(bottomSheetClickListener);

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN));
        shadow.setOnClickListener(view1 -> bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN));
        shadow.setClickable(false);

        mediaScannerPresenter = new MediaScannerPresenter(getContext(),
                this, CarePayCameraPreview.CameraType.SCAN_DOC);
        View takePhotoContainer = view.findViewById(R.id.takePhotoContainer);
        takePhotoContainer.setOnClickListener(view12 -> {
            mediaScannerPresenter.handlePictureAction();
            bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN);
        });

        View chooseFileContainer = view.findViewById(R.id.chooseFileContainer);
        chooseFileContainer.setOnClickListener(view13 -> {
            mediaScannerPresenter.selectFile();
            bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN);
        });
    }

    private void bottomMenuAction(BottomSheetBehavior bottomSheetBehavior, int stateHidden) {
        bottomSheetBehavior.setState(stateHidden);
    }

    private TextWatcher getEmptyTextWatcher(final TextInputLayout layout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!StringUtil.isNullOrEmpty(editable.toString())) {
                    layout.setError(null);
                    layout.setErrorEnabled(false);
                    validateForm();
                } else {
                    layout.setErrorEnabled(true);
                    layout.setError(Label.getLabel("demographics_required_field_msg"));
                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mediaScannerPresenter != null) {
            mediaScannerPresenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void setCapturedBitmap(String path, View view) {
        attachmentFile = new File(path);
        attachmentInput.setText(attachmentFile.getName());
        clearAttachmentButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Nullable
    @Override
    public Fragment getCallingFragment() {
        return this;
    }

    @Override
    public void setupImageBase64() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mediaScannerPresenter != null && mediaScannerPresenter.handleActivityResult(requestCode, resultCode, data);
    }
}
