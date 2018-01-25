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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.LinkMovementCallbackMethod;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.ReLinkify;
import com.carecloud.carepaylibray.utils.StringUtil;

import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lmenendez on 7/5/17
 */

public class MessagesConversationAdapter extends RecyclerView.Adapter<MessagesConversationAdapter.ViewHolder> implements LinkMovementCallbackMethod.OnClickCallback {

    private static final int TYPE_SENT = 111;
    private static final int TYPE_RECEIVED = 222;

    private static final String USER_TYPE_PROVIDER = "provider";
    private static final String USER_TYPE_STAFF = "user";

    private Context context;
    private List<Messages.Reply> messages = new ArrayList<>();
    private String userId;


    /**
     * constuctor
     *
     * @param context  context
     * @param messages messages list
     * @param userId   current user id
     */
    public MessagesConversationAdapter(Context context, List<Messages.Reply> messages, String userId) {
        this.context = context;
        this.messages = messages;
        this.userId = userId;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
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
            holder.participantPosition.setText(getPosition(message.getAuthor()));

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
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
            MixPanelUtil.logEvent(context.getString(R.string.event_messages_openCarecloudLink),
                    context.getString(R.string.param_link_type), uri.toString());
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

        ViewHolder(View itemView) {
            super(itemView);
            timeHeader = (TextView) itemView.findViewById(R.id.time_header);
            participantImage = (ImageView) itemView.findViewById(R.id.participant_image);
            participantInitials = (TextView) itemView.findViewById(R.id.participant_initials);
            metaView = itemView.findViewById(R.id.message_metadata);
            participantName = (TextView) itemView.findViewById(R.id.participant_name);
            participantPosition = (TextView) itemView.findViewById(R.id.participant_position);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            messageText = (TextView) itemView.findViewById(R.id.message_text);

        }
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
