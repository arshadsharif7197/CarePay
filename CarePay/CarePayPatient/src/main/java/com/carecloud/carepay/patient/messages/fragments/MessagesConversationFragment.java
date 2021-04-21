package com.carecloud.carepay.patient.messages.fragments;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.MessagesViewModel;
import com.carecloud.carepay.patient.messages.adapters.MessagesConversationAdapter;
import com.carecloud.carepay.patient.messages.models.MessageAttachment;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingModelDto;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.common.DocumentDetailFragment;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.FileDownloadUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.PermissionsUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 7/5/17
 */

public class MessagesConversationFragment extends BaseFragment implements MessagesConversationAdapter.ConversationCallback {

    private MessageNavigationCallback callback;
    private Messages.Reply thread;
    private List<Messages.Reply> messages = new ArrayList<>();

    private RecyclerView recyclerView;
    private EditText messageTextInput;
    private View sendButton;

    private boolean refreshing = true;

    private MessageAttachment selectedAttachment;
    private String selectedAttachmentFormat;
    private String practiceName;
    private MessagingModelDto messagingDto;
    private MessagesViewModel viewModel;
    private long enqueueId;

    /**
     * Get new instance of MessagesConversationFragment
     *
     * @param thread base thread
     * @return new MessagesConversationFragment
     */
    public static MessagesConversationFragment newInstance(Messages.Reply thread, String practiceName) {

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, thread);
        args.putString("practiceName", practiceName);

        MessagesConversationFragment fragment = new MessagesConversationFragment();
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
        thread = DtoHelper.getConvertedDTO(Messages.Reply.class, getArguments());
        this.practiceName = getArguments().getString("practiceName");
        setUpViewModel();
    }

    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(getActivity()).get(MessagesViewModel.class);
        messagingDto = viewModel.getMessagesDto().getValue();
        viewModel.getThreadMessagesObservable(true).observe(this, messagingThreadDTO
                -> updateThreadMessages(messagingThreadDTO.getPayload()));
        viewModel.getNewMessageInThreadObservable(true).observe(this, messagingThreadDTO
                -> {
            updateThreadMessages(messagingThreadDTO.getPayload());
            MixPanelUtil.logEvent(getString(R.string.event_message_reply));
            messageTextInput.setText(null);
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_conversation_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        initToolbar(view);
        recyclerView = view.findViewById(R.id.messages_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnLayoutChangeListener((view1, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (recyclerView.getAdapter() != null) {
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            }
        });

        sendButton = view.findViewById(R.id.button_send);
        sendButton.setOnClickListener(view12 -> {
            refreshing = true;
            viewModel.postNewMessageInThread(thread, messageTextInput.getText().toString());
        });
        sendButton.setEnabled(false);
        sendButton.setClickable(false);

        messageTextInput = view.findViewById(R.id.message_input);
        messageTextInput.addTextChangedListener(messageInputListener);

        TextView threadProviderTextView = view.findViewById(R.id.threadProviderTextView);
        if (practiceName != null && !practiceName.isEmpty()) {
            threadProviderTextView.setText(digProvider(thread) + " | " + practiceName);
        } else {
            threadProviderTextView.setText(digProvider(thread));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshing = true;
        viewModel.getThreadMessages(thread);
        callback.displayToolbar(false, null);
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(thread.getSubject());

        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> {
            SystemUtil.hideSoftKeyboard(getActivity());
            getActivity().onBackPressed();
        });
    }

    private void setAdapter() {
        MessagesConversationAdapter adapter = (MessagesConversationAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setMessages(messages);
        } else {
            adapter = new MessagesConversationAdapter(getContext(), this, messages,
                    messagingDto.getPayload().getInbox().getUserId(), messagingDto.getMetadata());
            recyclerView.setAdapter(adapter);
        }

        recyclerView.scrollToPosition(messages.size() - 1);
    }

    /**
     * update messages
     *
     * @param thread new message thread
     */
    public void updateThreadMessages(Messages.Reply thread) {
        if (messages.isEmpty() || refreshing) {
            messages = getMessagesList(thread);
        } else {
            messages.addAll(thread.getReplies());
        }

        setAdapter();

        refreshing = false;
    }

    private List<Messages.Reply> getMessagesList(Messages.Reply thread) {
        List<Messages.Reply> messages = thread.getReplies();
        messages.add(0, thread);//need to add the OP to the list of replies because its not included
        return messages;
    }

    private TextWatcher messageInputListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            boolean enabled = !StringUtil.isNullOrEmpty(editable.toString());
            sendButton.setEnabled(enabled);
            sendButton.setClickable(enabled);
        }
    };

    private String digProvider(Messages.Reply thread) {
        List<Messages.Reply> replies = thread.getReplies();
        if (!replies.isEmpty()) {
            for (Messages.Reply reply : replies) {
                for (Messages.Participant participant : reply.getParticipants()) {
                    if (participant.getType() != null && participant.getType().equals("provider")) {
                        return participant.getName();
                    }
                }
            }
        }

        for (Messages.Participant participant : thread.getParticipants()) {
            if (!participant.getUserId().equals(messagingDto.getPayload().getInbox().getUserId())) {
                return participant.getName();
            }
        }

        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionsUtil.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadAttachment(selectedAttachment, selectedAttachmentFormat);
            }
        }
    }

    @Override
    public void downloadAttachment(MessageAttachment attachment, String attachmentFormat) {
        if (!PermissionsUtil.checkPermissionStorageWrite(this)) {
            selectedAttachment = attachment;
            selectedAttachmentFormat = attachmentFormat;
            return;
        }

        TransitionDTO fetchAttachment = messagingDto.getMetadata().getLinks().getFetchAttachment();

        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", HttpConstants.getApiStartKey());
        headers.put("username", getAppAuthorizationHelper().getCurrUser());
        headers.put("Authorization", getAppAuthorizationHelper().getIdToken());

        Uri uri = Uri.parse(fetchAttachment.getUrl());
        uri = uri.buildUpon()
                .appendQueryParameter("nodeid", attachment.getDocument().getDocumentHandler())
                .build();

        enqueueId = FileDownloadUtil.downloadFile(getContext(),
                uri.toString(),
                attachment.getDocument().getName(),
                attachmentFormat,
                attachment.getDocument().getDescription(),
                headers);

        selectedAttachment = null;
        selectedAttachmentFormat = null;
        new CustomMessageToast(getContext(), Label.getLabel("loading_message"),
                CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();
    }

    @Override
    public void openImageDetailView(MessageAttachment attachment) {
        TransitionDTO fetchAttachment = messagingDto.getMetadata().getLinks().getFetchAttachment();
        Uri uri = Uri.parse(fetchAttachment.getUrl());
        uri = uri.buildUpon()
                .appendQueryParameter("nodeid", attachment.getDocument().getDocumentHandler())
                .build();
        DocumentDetailFragment fragment = DocumentDetailFragment.newInstance(uri.toString(), true);
        fragment.show(getFragmentManager(), "detail");
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            //TODO:Implement this listner if required

            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                new CustomMessageToast(getContext(), "Download Completed.",
                        CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();

                /*DownloadManager.Query query = new DownloadManager.Query();
                DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

                query.setFilterById(enqueueId);
                final Cursor cursor = dm.query(query);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                        String downloadLocalUri = cursor
                                .getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        if (downloadLocalUri != null) {
                            String downloadMimeType = cursor.getString(cursor
                                    .getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                            cursor.close();
                            FileDownloadUtil
                                    .openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType);
                        }
                        enqueueId = -1;
                    } else if (DownloadManager.STATUS_PAUSED == cursor.getInt(columnIndex)) {
                        //TODO: test if this can occur
                    } else {
                        Log.e("DownloadManager", "Status: " + cursor.getInt(columnIndex));
                    }
                }
                cursor.close();*/
            } else {
                new CustomMessageToast(getContext(), Label.getLabel("messaging.conversation.attachment.image.retry"),
                        CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(receiver);
        if (enqueueId > 0) {
            DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            dm.remove(enqueueId);
        }
        super.onStop();
    }
}
