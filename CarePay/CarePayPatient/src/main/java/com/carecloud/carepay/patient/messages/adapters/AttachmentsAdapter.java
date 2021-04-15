package com.carecloud.carepay.patient.messages.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.models.MessageAttachment;
import com.carecloud.carepay.patient.messages.models.MessagingMetadataDTO;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.PicassoRoundedCornersExifTransformation;
import com.google.android.gms.common.util.ArrayUtils;
import com.squareup.picasso.Callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.ViewHolder> {

    public interface ConversationCallback {
        void downloadAttachment(MessageAttachment attachment, String attachmentFormat);

        void openImageDetailView(MessageAttachment attachment);
    }

    private Context context;
    private List<MessageAttachment> attachmentList;
    private ConversationCallback callback;
    private final MessagingMetadataDTO messagingMetadata;
    private ISession iSession;

    /**
     * Constructor
     *
     * @param context        context
     * @param attachmentList list of attachmentList
     * @param callback       callback
     */
    public AttachmentsAdapter(Context context, List<MessageAttachment> attachmentList, ConversationCallback callback, MessagingMetadataDTO messagingMetadata) {
        this.context = context;
        this.attachmentList = attachmentList;
        this.callback = callback;
        this.messagingMetadata = messagingMetadata;
        this.iSession = (ISession) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attachment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MessageAttachment attachment = attachmentList.get(position);
        String attachmentFormat = attachment.getDocument().getDocumentFormat();
        if (attachmentFormat != null && attachmentFormat.contains("/")) {
            attachmentFormat = MimeTypeMap.getSingleton().getExtensionFromMimeType(attachmentFormat);
        }
        if (attachmentFormat == null) {
            attachmentFormat = MimeTypeMap.getFileExtensionFromUrl(attachment.getDocument().getName());
        }

        holder.fileAttachmentExtension.setText(attachmentFormat);
        holder.fileAttachmentName.setText(attachment.getDocument().getName());

        if (ArrayUtils.contains(MessagesConversationAdapter.FORMAT_IMAGE, attachmentFormat.toLowerCase())) {
            TransitionDTO fetchAttachment = messagingMetadata.getLinks().getFetchAttachment();

            Map<String, String> headers = new HashMap<>();
            headers.put("x-api-key", HttpConstants.getApiStartKey());
            headers.put("username", iSession.getAppAuthorizationHelper().getCurrUser());
            headers.put("Authorization", iSession.getAppAuthorizationHelper().getIdToken());

            Uri uri = Uri.parse(fetchAttachment.getUrl());
            uri = uri.buildUpon()
                    .appendQueryParameter("nodeid", attachment.getDocument().getDocumentHandler())
                    .build();

            PicassoHelper.setHeaders(headers);
            PicassoHelper.getPicassoInstance(context)
                    .load(uri)
                    .placeholder(R.drawable.bg_glitter_rounded)
                    .transform(new PicassoRoundedCornersExifTransformation(24, 10, uri.toString(), headers))
                    .into(holder.imageAttachment, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imageAttachment.setVisibility(View.VISIBLE);
                            holder.attachmentProgress.setVisibility(View.GONE);
                            holder.imageAttachment.setOnClickListener(getImageClickListener(attachment));
                        }

                        @Override
                        public void onError() {
                            holder.imageAttachment.setVisibility(View.GONE);
                            holder.fileAttachmentLayout.setVisibility(View.VISIBLE);
                            holder.attachmentProgress.setVisibility(View.GONE);
                        }
                    });
            holder.imageAttachment.setVisibility(View.VISIBLE);
            holder.attachmentProgress.setVisibility(View.VISIBLE);
        } else {
            holder.fileAttachmentLayout.setVisibility(View.VISIBLE);
        }

        setAttachmentClickListener(holder.fileAttachmentLayout, attachment, attachmentFormat);
        holder.imageAttachment.setOnLongClickListener(getDownloadListener(attachment, attachmentFormat));

    }

    private View.OnClickListener getImageClickListener(final MessageAttachment attachment) {
        return view -> callback.openImageDetailView(attachment);
    }

    private View.OnLongClickListener getDownloadListener(final MessageAttachment attachment, final String attachmentFormat) {
        return view -> {
            view.startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    actionMode.setTitle(attachment.getDocument().getName());
                    actionMode.getMenuInflater().inflate(R.menu.messages_action_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menu_download) {
                        callback.downloadAttachment(attachment, attachmentFormat);
                    }
                    actionMode.finish();
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {

                }
            });
            return true;
        };
    }

    private void setAttachmentClickListener(View view, final MessageAttachment attachment, final String attachmentFormat) {
        view.setOnClickListener(view1 -> callback.downloadAttachment(attachment, attachmentFormat));
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }

    /**
     * Get a attachment by position
     *
     * @param position position
     * @return Thread
     */
    public MessageAttachment getAttachment(int position) {
        if (attachmentList.size() > position && position > -1) {
            return attachmentList.get(position);
        }
        return null;
    }

    /**
     * set attachmentList and reset recycler
     *
     * @param attachmentList new attachmentList list
     */
    public void setAttachmentList(List<MessageAttachment> attachmentList) {
        this.attachmentList = attachmentList;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        View fileAttachmentLayout;
        TextView fileAttachmentExtension;
        TextView fileAttachmentName;
        ImageView imageAttachment;
        View attachmentProgress;

        ViewHolder(View itemView) {
            super(itemView);
            fileAttachmentLayout = itemView.findViewById(R.id.attachmentFileLayout);
            fileAttachmentExtension = itemView.findViewById(R.id.attachmentExtension);
            fileAttachmentName = itemView.findViewById(R.id.attachmentName);
            imageAttachment = itemView.findViewById(R.id.attachmentImage);
            attachmentProgress = itemView.findViewById(R.id.attachmentProgress);
        }
    }
}
