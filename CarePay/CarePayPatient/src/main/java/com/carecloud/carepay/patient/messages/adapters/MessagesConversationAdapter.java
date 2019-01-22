package com.carecloud.carepay.patient.messages.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.models.MessageAttachment;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingMetadataDTO;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.LinkMovementCallbackMethod;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.PicassoRoundedCornersExifTransformation;
import com.carecloud.carepaylibray.utils.ReLinkify;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.android.gms.common.util.ArrayUtils;
import com.squareup.picasso.Callback;

import org.xml.sax.XMLReader;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 7/5/17
 */

public class MessagesConversationAdapter extends RecyclerView.Adapter<MessagesConversationAdapter.ViewHolder> implements LinkMovementCallbackMethod.OnClickCallback {

    private static final int TYPE_SENT = 111;
    private static final int TYPE_RECEIVED = 222;

    public static final String[] FORMAT_IMAGE = {"png", "image/png", "jpg", "image/jpg", "jpeg", "image/jpeg", "gif", "image/gif", "tif", "image/tif", "tiff", "image/tiff", "bmp", "image/bmp"};

    private static final String USER_TYPE_PROVIDER = "provider";
    private static final String USER_TYPE_STAFF = "user";

    private Context context;
    private ConversationCallback callback;
    private List<Messages.Reply> messages;
    private String userId;
    private final MessagingMetadataDTO messagingMetadata;
    private ISession iSession;


    /**
     * constuctor
     *
     * @param context  context
     * @param messages messages list
     * @param userId   current user id
     */
    public MessagesConversationAdapter(Context context, ConversationCallback callback,
                                       List<Messages.Reply> messages,
                                       String userId, MessagingMetadataDTO messagingMetadata) {
        this.context = context;
        this.callback = callback;
        this.messages = messages;
        this.userId = userId;
        this.messagingMetadata = messagingMetadata;
        this.iSession = (ISession) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == TYPE_SENT) {
            view = inflater.inflate(R.layout.item_messages_sent, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_messages_received, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.messageText.setAutoLinkMask(0);
        holder.messageText.setMovementMethod(null);
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Messages.Reply message = messages.get(position);
        Messages.Reply lastMessage = null;
        if (position > 0) {
            lastMessage = messages.get(position - 1);
        }

        Date date = DateUtil.getInstance().setDateRaw(message.getCreatedDate()).shiftDateToGMT().getDate();
        holder.timeHeader.setText(DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal());
        holder.timeHeader.setVisibility(View.VISIBLE);


        if (holder.metaView != null) {
            holder.participantInitials.setText(StringUtil.getShortName(message.getAuthor().getName()));
            holder.participantInitials.setVisibility(View.VISIBLE);

            holder.participantName.setText(StringUtil.captialize(message.getAuthor().getName()).trim());
            String participantPosition = getPosition(message.getAuthor());
            if (!StringUtil.isNullOrEmpty(participantPosition)) {
                holder.participantPosition.setText(", " + participantPosition);
            }

            holder.metaView.setVisibility(View.VISIBLE);
        }

        String time = DateUtil.getInstance().getDateAsMonthDayTime();
        holder.timeStamp.setText(time);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            holder.messageText.setText(Html.fromHtml(message.getBody(), null, new ConversationTagHandler()));
        } else {
            holder.messageText.setText(Html.fromHtml(message.getBody(), Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH, null, new ConversationTagHandler()));
        }

        ReLinkify.addLinks(holder.messageText, ReLinkify.ALL);

        holder.messageText.setMovementMethod(LinkMovementCallbackMethod.getInstance(this));
        holder.messageText.setLinksClickable(true);

        if (lastMessage != null) {
            Date lastDate = DateUtil.getInstance().setDateRaw(lastMessage.getCreatedDate()).getDate();
            if (DateUtil.isSameDay(date, lastDate)) {
                holder.timeHeader.setVisibility(View.GONE);
            }
            if (lastMessage.getAuthor().getUserId().equals(message.getAuthor().getUserId()) && holder.metaView != null) {
                holder.participantImage.setVisibility(View.INVISIBLE);
                holder.participantInitials.setVisibility(View.INVISIBLE);

                holder.metaView.setVisibility(View.GONE);
            }
        }

        holder.fileAttachmentLayout.setVisibility(View.GONE);
        holder.imageAttachment.setVisibility(View.GONE);
        if (!message.getAttachments().isEmpty()) {
            //currently only supporting one attachment
            MessageAttachment attachment = message.getAttachments().get(0);
            String attachmentFormat = attachment.getDocument().getDocumentFormat();
            if (attachmentFormat != null && attachmentFormat.contains("/")) {
                attachmentFormat = MimeTypeMap.getSingleton().getExtensionFromMimeType(attachmentFormat);
            }
            if(attachmentFormat == null){
                attachmentFormat = MimeTypeMap.getFileExtensionFromUrl(attachment.getDocument().getName());
            }

            holder.fileAttachmentExtension.setText(attachmentFormat);
            holder.fileAttachmentName.setText(attachment.getDocument().getName());

            if (ArrayUtils.contains(FORMAT_IMAGE, attachmentFormat)) {
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
                        .transform(new PicassoRoundedCornersExifTransformation(14, 10))
                        .into(holder.imageAttachment, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.imageAttachment.setVisibility(View.VISIBLE);
                                holder.attachmentProgress.setVisibility(View.GONE);
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

            holder.fileAttachmentLayout.setOnLongClickListener(getDownloadListener(attachment, attachmentFormat));
            holder.imageAttachment.setOnLongClickListener(getDownloadListener(attachment, attachmentFormat));

        }
    }

    private View.OnLongClickListener getDownloadListener(final MessageAttachment attachment, final String attachmentFormat){
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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
                        if(menuItem.getItemId()== R.id.menu_download){
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
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        Messages.Reply message = messages.get(position);
        if (message.getAuthor().getUserId().equals(userId)) {
            return TYPE_SENT;
        }
        return TYPE_RECEIVED;
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * set new messages list
     *
     * @param messages new messages list
     */
    public void setMessages(List<Messages.Reply> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }


    private String getPosition(Messages.Participant participant) {
        String type = participant.getType();
        if (type != null) {
            switch (type) {
                case USER_TYPE_STAFF:
                    return Label.getLabel("messaging_user_staff");
                case USER_TYPE_PROVIDER:
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public void onClick(String url) {
        Uri uri = Uri.parse(url);
        ISession iSession = (ISession) context;

        if (uri.getHost() != null && uri.getHost().contains("carecloud")) {
            char queryAppend;
            if (uri.getQuery() != null) {
                queryAppend = '&';
            } else {
                queryAppend = '?';
            }
            uri = Uri.parse(url + queryAppend + "token=" + iSession.getAppAuthorizationHelper().getIdToken());
            MixPanelUtil.logEvent(context.getString(R.string.event_messages_openCarecloudLink),
                    context.getString(R.string.param_link_type), uri.toString());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeHeader;
        ImageView participantImage;
        TextView participantInitials;
        View metaView;
        TextView participantName;
        TextView participantPosition;
        TextView timeStamp;
        TextView messageText;
        View fileAttachmentLayout;
        TextView fileAttachmentExtension;
        TextView fileAttachmentName;
        ImageView imageAttachment;
        View attachmentProgress;

        ViewHolder(View itemView) {
            super(itemView);
            timeHeader = itemView.findViewById(R.id.time_header);
            participantImage = itemView.findViewById(R.id.participant_image);
            participantInitials = itemView.findViewById(R.id.participant_initials);
            metaView = itemView.findViewById(R.id.message_metadata);
            participantName = itemView.findViewById(R.id.participant_name);
            participantPosition = itemView.findViewById(R.id.participant_position);
            timeStamp = itemView.findViewById(R.id.time_stamp);
            messageText = itemView.findViewById(R.id.message_text);
            fileAttachmentLayout = itemView.findViewById(R.id.attachmentFileLayout);
            fileAttachmentExtension = itemView.findViewById(R.id.attachmentExtension);
            fileAttachmentName = itemView.findViewById(R.id.attachmentName);
            imageAttachment = itemView.findViewById(R.id.attachmentImage);
            attachmentProgress = itemView.findViewById(R.id.attachmentProgress);
        }
    }

    public interface ConversationCallback {
        void downloadAttachment(MessageAttachment attachment, String attachmentFormat);
    }

    private class ConversationTagHandler implements Html.TagHandler {
        private static final String TAG_STRIKE = "strike";
        private static final String TAG_STRIKE_S = "s";
        private static final String TAG_DEL = "del";

        private static final String TAG_ROW = "tr";
        private static final String TAG_COLUMN = "td";
        private static final String TAG_LIST = "li";
        private static final String TAG_LINK = "a";

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (opening) {
                if (tag.equalsIgnoreCase(TAG_STRIKE_S) || tag.equalsIgnoreCase(TAG_STRIKE) || tag.equalsIgnoreCase(TAG_DEL)) {
                    markStart(output, new StrikeStyle());
                } else if (tag.equalsIgnoreCase(TAG_ROW)) {
                    markStart(output, new RowStyle());
                } else if (tag.equalsIgnoreCase(TAG_COLUMN)) {
                    markStart(output, new ColumnStyle());
                } else if (tag.equalsIgnoreCase(TAG_LIST)) {
                    markStart(output, new ListStyle());
                }
            } else {
                if (tag.equalsIgnoreCase(TAG_STRIKE_S) || tag.equalsIgnoreCase(TAG_STRIKE) || tag.equalsIgnoreCase(TAG_DEL)) {
                    markEnd(output, StrikeStyle.class);
                } else if (tag.equalsIgnoreCase(TAG_ROW)) {
                    markEnd(output, RowStyle.class);
                } else if (tag.equalsIgnoreCase(TAG_COLUMN)) {
                    markEnd(output, ColumnStyle.class);
                } else if (tag.equalsIgnoreCase(TAG_LIST)) {
                    markEnd(output, ListStyle.class);
                }
            }
        }

        private void markStart(Editable output, Object style) {
            int start = output.length();
            output.setSpan(style, start, start, Spanned.SPAN_MARK_MARK);
        }

        private void markEnd(Editable output, Class style) {
            Object opening = getLastOccurrence(output, style);
            int start = output.getSpanStart(opening);
            int end = output.length();

            output.removeSpan(opening);
            if (start != end) {
                if (style == StrikeStyle.class) {
                    output.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (style == ListStyle.class) {
                    output.insert(start, "• ");
                    output.append("\n");
                } else if (style == RowStyle.class) {
                    output.append("\n");
                } else if (style == ColumnStyle.class) {
                    output.append("   ");
                }
            }
        }

        private Object getLastOccurrence(Editable output, Class style) {
            Object[] styleTags = output.getSpans(0, output.length(), style);
            if (styleTags.length < 1) {
                return null;
            }
            for (int i = styleTags.length; i > 0; i++) {
                if (output.getSpanFlags(styleTags[i - 1]) == Spanned.SPAN_MARK_MARK) {
                    return styleTags[i - 1];
                }
            }
            return null;
        }

        private class StrikeStyle {
        }

        private class RowStyle {
        }

        private class ColumnStyle {
        }

        private class ListStyle {
        }

    }
}
