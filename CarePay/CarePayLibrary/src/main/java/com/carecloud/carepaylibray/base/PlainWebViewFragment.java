package com.carecloud.carepaylibray.base;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.carecloud.carepaylibrary.R;

public class PlainWebViewFragment extends BaseFragment {
    private static final String KEY_URL = "key_url";

    private String url;

    public static PlainWebViewFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(KEY_URL, url);

        PlainWebViewFragment fragment = new PlainWebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        url = args.getString(KEY_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.plain_webview_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initToolbar(view);

        WebView webView = (WebView) view.findViewById(R.id.web_view);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new LoaderClient());
        showProgressDialog();

        webView.loadUrl(url);

    }

    private void initToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent_apt));
    }

    protected class LoaderClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView webView, String url) {
            hideProgressDialog();
        }
    }

}
