package com.carecloud.carepay.patient.messages;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepay.patient.messages.fragments.MessagesNewThreadFragment;
import com.carecloud.carepay.patient.messages.models.AttachmentPostModel;
import com.carecloud.carepay.patient.messages.models.AttachmentUploadModel;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingModelDto;
import com.carecloud.carepay.patient.messages.models.MessagingPostModel;
import com.carecloud.carepay.patient.messages.models.MessagingThreadDTO;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.patient.myhealth.BaseViewModel;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.JsonElement;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 2019-08-12.
 */
public class MessagesViewModel extends BaseViewModel {

    private MutableLiveData<MessagingModelDto> messagesDto;
    private MutableLiveData<MessagingThreadDTO> createThreadObservable;
    private MutableLiveData<MessagingModelDto> threadsObservable;
    private MutableLiveData<MessagingThreadDTO> threadMessagesObservable;
    private MutableLiveData<MessagingThreadDTO> newMessageInThreadObservable;

    public MessagesViewModel(Application application) {
        super(application);
    }

    public MutableLiveData<MessagingModelDto> getMessagesDto() {
        return getMessagesDto(null, false);
    }

    public MutableLiveData<MessagingModelDto> getMessagesDto(TransitionDTO messagesTransition, boolean refresh) {
        if (messagesDto == null || refresh) {
            messagesDto = new MutableLiveData<>();
            loadDto(messagesTransition);
        }
        return messagesDto;
    }

    private void loadDto(TransitionDTO messagesTransition) {
        Map<String, String> queryMap = new HashMap<>();
        ((CarePayApplication) getApplication()).getWorkflowServiceHelper().execute(messagesTransition,
                new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        setSkeleton(true);
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        setSkeleton(false);
                        messagesDto.setValue(DtoHelper.getConvertedDTO(MessagingModelDto.class, workflowDTO));
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        setSkeleton(false);
                        setErrorMessage(exceptionMessage);
                    }
                }, queryMap);
    }

    public MutableLiveData<MessagingModelDto> getThreadsObservable() {
        if (threadsObservable == null) {
            threadsObservable = new MutableLiveData<>();
        }
        return threadsObservable;
    }

    public MutableLiveData<MessagingThreadDTO> getThreadMessagesObservable(boolean refresh) {
        if (threadMessagesObservable == null || refresh) {
            threadMessagesObservable = new MutableLiveData<>();
        }
        return threadMessagesObservable;
    }

    public MutableLiveData<MessagingThreadDTO> getNewMessageInThreadObservable(boolean refresh) {
        if (newMessageInThreadObservable == null || refresh) {
            newMessageInThreadObservable = new MutableLiveData<>();
        }
        return newMessageInThreadObservable;
    }

    public MutableLiveData<MessagingModelDto> getThreads(long page, long size) {
        if (threadsObservable == null) {
            threadsObservable = new MutableLiveData<>();
        }
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("page", String.valueOf(page < 1 ? 1 : page));//first page is 1
        queryMap.put("limit", String.valueOf(size < 15 ? 15 : size));//default size if 15, should not be less than that

        TransitionDTO inbox = messagesDto.getValue().getMetadata().getLinks().getInbox();
        ((CarePayApplication) getApplication()).getWorkflowServiceHelper()
                .execute(inbox, new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        setLoading(false);
                        MessagingModelDto messagingModel = DtoHelper.getConvertedDTO(MessagingModelDto.class, workflowDTO);
                        threadsObservable.setValue(messagingModel);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        setLoading(false);
                        setErrorMessage(errorMessage);
                    }
                }, queryMap);
        return threadsObservable;
    }

    public void getThreadMessages(Messages.Reply thread) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("message_id", thread.getId());
        TransitionDTO messages = messagesDto.getValue().getMetadata().getLinks().getMessage();
        ((CarePayApplication) getApplication()).getWorkflowServiceHelper()
                .execute(messages, new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        setLoading(false);
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        setLoading(false);
                        MessagingThreadDTO messagingThreadDTO = DtoHelper
                                .getConvertedDTO(MessagingThreadDTO.class, workflowDTO);
                        threadMessagesObservable.setValue(messagingThreadDTO);
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        setLoading(false);
                        setErrorMessage(exceptionMessage);
                    }
                }, queryMap);
    }

    public void postNewMessageInThread(Messages.Reply thread, String message) {
        message = message.replace("\n", "<br/>");
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("message_id", thread.getId());

        MessagingPostModel postModel = new MessagingPostModel();
        String userId = messagesDto.getValue().getPayload().getInbox().getUserId();
        postModel.getParticipant().setUserId(userId);
        postModel.getParticipant().setName(messagesDto.getValue().getPayload().lookupName(thread, userId));
        postModel.setMessage(message);

        TransitionDTO replyTransition = messagesDto.getValue().getMetadata().getLinks().getReply();
        ((CarePayApplication) getApplication()).getWorkflowServiceHelper().execute(replyTransition,
                new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        setLoading(true);
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        setLoading(false);
                        MessagingThreadDTO messagingThreadDTO = DtoHelper
                                .getConvertedDTO(MessagingThreadDTO.class, workflowDTO);
                        newMessageInThreadObservable.setValue(messagingThreadDTO);
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        setLoading(false);
                        setErrorMessage(exceptionMessage);
                    }
                },
                DtoHelper.getStringDTO(postModel), queryMap);

    }

    public MutableLiveData<MessagingModelDto> deleteMessageThread(Messages.Reply thread) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("message_id", thread.getId());
        TransitionDTO deleteMessage = messagesDto.getValue().getMetadata().getLinks().getDeleteMessage();
        ((CarePayApplication) getApplication()).getWorkflowServiceHelper()
                .execute(deleteMessage, new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        MessagingModelDto messagingModel = DtoHelper.getConvertedDTO(MessagingModelDto.class, workflowDTO);
                        messagesDto.setValue(messagingModel);
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        setErrorMessage(exceptionMessage);
                    }
                }, queryMap);
        return messagesDto;
    }

    public MutableLiveData<MessagingThreadDTO> postNewMessage(ProviderContact provider,
                                                              File attachmentFile,
                                                              AttachmentPostModel attachmentPostModel,
                                                              String subject,
                                                              String message) {
        if (createThreadObservable == null) {
            createThreadObservable = new MutableLiveData<>();
        }
        if (attachmentFile != null) {
            attachmentPostModel = new AttachmentPostModel();
            uploadFile(provider, attachmentFile, attachmentPostModel, subject, message);
            return createThreadObservable;
        }

        message = message.replace("\n", "<br/>");
        MessagingPostModel postModel = new MessagingPostModel();
        Messages.Participant participant = postModel.getParticipant();
        participant.setName(provider.getName());
        participant.setUserId(provider.getId());
        participant.setLinkedPatientId(provider.getPatientId());

        postModel.setMessage(message);
        postModel.setSubject(subject);

        if (attachmentPostModel != null) {
            attachmentPostModel.setPatientId(provider.getPatientId());
            attachmentPostModel.setPracticeId(provider.getBusinessEntityId());
            postModel.setAttachments(new ArrayList<>());
            postModel.getAttachments().add(attachmentPostModel);
        }

        TransitionDTO newMessage = messagesDto.getValue().getMetadata().getLinks().getNewMessage();
        ((CarePayApplication) getApplication()).getWorkflowServiceHelper().execute(newMessage,
                postNewMessageCallback(), DtoHelper.getStringDTO(postModel));
        return createThreadObservable;

    }

    private WorkflowServiceCallback postNewMessageCallback() {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setLoading(false);
                MessagingThreadDTO messagingThreadDTO = DtoHelper.getConvertedDTO(MessagingThreadDTO.class, workflowDTO);
                createThreadObservable.setValue(messagingThreadDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                setLoading(false);
                setErrorMessage(exceptionMessage);
            }
        };
    }

    private void uploadFile(ProviderContact provider,
                            File file,
                            AttachmentPostModel attachmentPostModel,
                            String subject,
                            String message) {
        TransitionDTO transitionDTO = messagesDto.getValue().getMetadata().getLinks().getUploadAttachment();
        Uri uri = Uri.parse(transitionDTO.getUrl());
        String path = uri.getPath();
        String baseUrl = transitionDTO.getUrl();
        if (baseUrl != null && path != null) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - path.length());
        }
        RestCallServiceHelper restCallServiceHelper = new RestCallServiceHelper(((CarePayApplication) getApplication())
                .getAppAuthorizationHelper(), ((CarePayApplication) getApplication()).getApplicationMode());
        restCallServiceHelper.executeRequest(RestDef.POST,
                baseUrl,
                getUploadCallback(provider, file, attachmentPostModel, subject, message),
                true,
                false,
                null,
                new HashMap<>(),
                null,
                file,
                path.replace("/", ""));
    }

    private RestCallServiceCallback getUploadCallback(ProviderContact provider, File file,
                                                      AttachmentPostModel attachmentPostModel,
                                                      String subject,
                                                      String message) {
        return new RestCallServiceCallback() {
            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(JsonElement jsonElement) {
                setLoading(false);
                Log.d(MessagesNewThreadFragment.class.getName(), jsonElement != null ? jsonElement.toString() : "no data");
                if (jsonElement != null && jsonElement.isJsonObject()) {
                    AttachmentUploadModel uploadModel = DtoHelper.getConvertedDTO(AttachmentUploadModel.class,
                            jsonElement.getAsJsonObject());
                    String extension = MimeTypeMap.getFileExtensionFromUrl(URLEncoder
                            .encode(file.getAbsolutePath()).replace("+", "%20"));
                    attachmentPostModel.setNodeId(uploadModel.getNodeId());
                    attachmentPostModel.setDescription(file.getName());
                    attachmentPostModel.setFormat(extension);
                    if (attachmentPostModel.getFormat() == null && "json".equals(extension)) {
                        attachmentPostModel.setFormat("application/json");
                    }
                    postNewMessage(provider, null, attachmentPostModel, subject, message);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                setErrorMessage(errorMessage);
                Log.e(MessagesNewThreadFragment.class.getName(), errorMessage);
            }
        };
    }
}
