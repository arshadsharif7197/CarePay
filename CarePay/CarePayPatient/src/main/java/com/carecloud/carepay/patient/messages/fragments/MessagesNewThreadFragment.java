package com.carecloud.carepay.patient.messages.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.models.AttachmentPostModel;
import com.carecloud.carepay.patient.messages.models.AttachmentUploadModel;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingPostModel;
import com.carecloud.carepay.patient.messages.models.MessagingThreadDTO;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.JsonElement;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
    private AttachmentPostModel attachmentPostModel;

    /**
     * Get a new instance of MessagesNewThreadFragment
     * @param provider provider to use for sending message
     * @return MessagesNewThreadFragment
     */
    public static MessagesNewThreadFragment newInstance(ProviderContact provider){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, provider);

        MessagesNewThreadFragment fragment = new MessagesNewThreadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (MessageNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement MessageNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        if(args != null){
            provider = DtoHelper.getConvertedDTO(ProviderContact.class, args);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        callback.displayToolbar(false, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_new_thread, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
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
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                callback.postNewMessage(provider, subjectInput.getText().toString(), messageInput.getText().toString());
                postNewMessage(provider, subjectInput.getText().toString(), messageInput.getText().toString());
            }
        });

        View.OnClickListener selectFileListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile();
            }
        };
        attachmentInput = view.findViewById(R.id.attachmentEditText);
        attachmentInput.setOnClickListener(selectFileListener);

        View uploadButton = view.findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(selectFileListener);

        clearAttachmentButton = view.findViewById(R.id.clearBtn);
        clearAttachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachmentFile = null;
                attachmentInput.setText(null);
                attachmentPostModel = null;
                view.setVisibility(View.INVISIBLE);
            }
        });

        validateForm();
    }

    private void initToolbar(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(Label.getLabel("messaging_subject_title"));

        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void validateForm(){
        boolean valid = !StringUtil.isNullOrEmpty(subjectInput.getText().toString()) &&
                !StringUtil.isNullOrEmpty(messageInput.getText().toString());

        buttonCreate.setEnabled(valid);
        buttonCreate.setClickable(valid);

    }

    private TextWatcher getEmptyTextWatcher(final TextInputLayout layout){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!StringUtil.isNullOrEmpty(editable.toString())){
                    layout.setError(null);
                    layout.setErrorEnabled(false);
                    validateForm();
                }else{
                    layout.setErrorEnabled(true);
                    layout.setError(Label.getLabel("demographics_required_field_msg"));
                }
            }
        };
    }

    private void postNewMessage(ProviderContact providerContact, String subject, String message){
        if(attachmentFile != null){
            uploadFile(attachmentFile);
            return;
        }

        MessagingPostModel postModel = new MessagingPostModel();
        Messages.Participant participant = postModel.getParticipant();
        participant.setName(providerContact.getName());
        participant.setUserId(providerContact.getId());
        participant.setLinkedPatientId(providerContact.getPatientId());

        postModel.setMessage(message);
        postModel.setSubject(subject);

        if(attachmentPostModel != null){
            attachmentPostModel.setPatientId(providerContact.getPatientId());
            attachmentPostModel.setPracticeId(providerContact.getBusinessEntityId());

            postModel.setAttachments(new ArrayList<AttachmentPostModel>());
            postModel.getAttachments().add(attachmentPostModel);
        }

        TransitionDTO newMessage = callback.getDto().getMetadata().getLinks().getNewMessage();
        getWorkflowServiceHelper().execute(newMessage, postNewMessageCallback(providerContact), DtoHelper.getStringDTO(postModel));

    }

    private WorkflowServiceCallback postNewMessageCallback(final ProviderContact provider) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                MessagingThreadDTO messagingThreadDTO = DtoHelper.getConvertedDTO(MessagingThreadDTO.class, workflowDTO);
                getFragmentManager().popBackStack(MessagesProvidersFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                callback.displayThreadMessages(messagingThreadDTO.getPayload());

                String[] params = {getString(R.string.param_provider_id), getString(R.string.param_provider_name)};
                Object[] values = {provider.getId(), provider.getName()};
                MixPanelUtil.logEvent(getString(R.string.event_message_new), params, values);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        };
    }

    private void selectFile(){
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this, null);
        mediaScannerPresenter.selectFile();
    }

    private void uploadFile(File file){
        TransitionDTO transitionDTO = callback.getDto().getMetadata().getLinks().getUploadAttachment();
        Uri uri = Uri.parse(transitionDTO.getUrl());
        String path = uri.getPath();
        String baseUrl = transitionDTO.getUrl();
        if(baseUrl != null && path != null) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - path.length());
        }
        RestCallServiceHelper restCallServiceHelper = new RestCallServiceHelper(getAppAuthorizationHelper(), getApplicationMode());
        restCallServiceHelper.executeRequest(RestDef.POST,
                baseUrl,
                getUploadCallback(file),
                true,
                false,
                null,
                new HashMap<String, String>(),
                null,
                file,
                path.replace("/", ""));
    }

    private RestCallServiceCallback getUploadCallback(final File file) {
        return new RestCallServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(JsonElement jsonElement) {
                hideProgressDialog();
                Log.d(MessagesNewThreadFragment.class.getName(), jsonElement != null ? jsonElement.toString() : "no data");
                if (jsonElement != null && jsonElement.isJsonObject()) {
                    AttachmentUploadModel uploadModel = DtoHelper.getConvertedDTO(AttachmentUploadModel.class, jsonElement.getAsJsonObject());
                    String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
                    attachmentPostModel = new AttachmentPostModel();
                    attachmentPostModel.setNodeId(uploadModel.getNodeId());
                    attachmentPostModel.setDescription(file.getName());
                    attachmentPostModel.setFormat(MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(extension));
                    if(attachmentPostModel.getFormat() == null && "json".equals(extension)){
                        attachmentPostModel.setFormat("application/json");
                    }

                    attachmentFile = null;
                    postNewMessage(provider, subjectInput.getText().toString(), messageInput.getText().toString());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                hideProgressDialog();
                showErrorNotification(errorMessage);
                Log.d(MessagesNewThreadFragment.class.getName(), errorMessage);
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
