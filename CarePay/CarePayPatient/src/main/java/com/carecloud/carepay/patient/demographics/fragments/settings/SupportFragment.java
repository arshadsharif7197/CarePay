package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;

public class SupportFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        initializeToolbar(view);
        initializeHelpContent(view);
        initializeEmailButton(view);
        initializePhoneButton(view);
    }

    private void initializeToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("support_label"));
    }

    private void initializeHelpContent(View view) {
        TextView textView = (TextView) view.findViewById(R.id.support_help_content);
        TextView bottomTextView = (TextView) view.findViewById(R.id.support_help_content_bottom);
        String html = Label.getLabel("support_help_content_top");
        String bottomText = Label.getLabel("Support_help_content_bottom");

        CharSequence sequence;
        if (Build.VERSION.SDK_INT > 23) {
            sequence = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            sequence = Html.fromHtml(html);
        }


        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        textView.setText(strBuilder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        bottomTextView.setText(bottomText);
        bottomTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initializeEmailButton(View view) {
        Button button = (Button) view.findViewById(R.id.email_us_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailIntent();
            }
        });
    }

    private void initializePhoneButton(View view) {
        Button button = (Button) view.findViewById(R.id.call_us_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("tel:" + Label.getLabel("support_phone")));
                startActivity(Intent.createChooser(intent, Label.getLabel("support_choose_client_label")));
            }
        });
    }

    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                openEmailIntent();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    private void openEmailIntent() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Label.getLabel("support_email_address"), null));
        intent.putExtra(Intent.EXTRA_SUBJECT, Label.getLabel("support_email_subject"));
        startActivity(Intent.createChooser(intent, Label.getLabel("support_choose_client_label")));
    }
}
