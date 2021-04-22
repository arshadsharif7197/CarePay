package com.carecloud.carepay.patient.messages.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.models.MessageAttachment;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingMetadataDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.LinkMovementCallbackMethod;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.ReLinkify;
import com.carecloud.carepaylibray.utils.StringUtil;

import org.xml.sax.XMLReader;

import java.util.Date;
import java.util.List;

/**
 * Created by lmenendez on 7/5/17
 */

public class MessagesConversationAdapter extends RecyclerView.Adapter<MessagesConversationAdapter.ViewHolder>
        implements LinkMovementCallbackMethod.OnClickCallback {

    private static final int TYPE_SENT = 111;
    private static final int TYPE_RECEIVED = 222;

    public static final String[] FORMAT_IMAGE = {"png", "image/png", "jpg", "image/jpg", "jpeg", "image/jpeg",
            "gif", "image/gif", "tif", "image/tif", "tiff", "image/tiff", "bmp", "image/bmp"};

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
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

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                holder.messageText.setText(Html.fromHtml(message.getBody(), null, new ConversationTagHandler()));
            } else {
                holder.messageText.setText(Html.fromHtml(message.getBody(), Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH, null, new ConversationTagHandler()));
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        holder.rvAttachments.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.rvAttachments.setHasFixedSize(true);
        if (!message.getAttachments().isEmpty()) {
            holder.rvAttachments.setVisibility(View.VISIBLE);
            AttachmentsAdapter attachmentsAdapter = new AttachmentsAdapter(context, message.getAttachments(), new AttachmentsAdapter.ConversationCallback() {
                @Override
                public void downloadAttachment(MessageAttachment attachment, String attachmentFormat) {
                    callback.downloadAttachment(attachment, attachmentFormat);
                }

                @Override
                public void openImageDetailView(MessageAttachment attachment) {
                    callback.openImageDetailView(attachment);
                }
            }, messagingMetadata);

            holder.rvAttachments.setAdapter(attachmentsAdapter);
        } else {
            holder.rvAttachments.setVisibility(View.GONE);
        }
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
        RecyclerView rvAttachments;

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
            rvAttachments = itemView.findViewById(R.id.message_attachments_recycler);
        }
    }

    public interface ConversationCallback {
        void downloadAttachment(MessageAttachment attachment, String attachmentFormat);

        void openImageDetailView(MessageAttachment attachment);
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
