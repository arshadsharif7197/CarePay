package com.carecloud.carepay.patient.purchases.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.base.BaseFragment;

/**
 * Created by lmenendez on 2/8/17
 */

public class PurchaseFragment extends BaseFragment {

    private View noPurchaseLayout;// this should be available here to access it for show/hide from other methods
    private WebView shoppingWebView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_purchase, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        noPurchaseLayout = view.findViewById(R.id.no_purchase_layout);
        noPurchaseLayout.setVisibility(View.GONE);

        shoppingWebView = (WebView) view.findViewById(R.id.shoppingWebView);
        WebSettings settings = shoppingWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        shoppingWebView.loadData("<div id=\"my-store-11831089\"></div><div> <script type=\"text/javascript\" src=\"https://app.ecwid.com/script.js?11831089\" charset=\"utf-8\"></script><script type=\"text/javascript\"> xProductBrowser(\"categoriesPerRow=1\",\"views=grid(60,1) list(60)\",\"categoryView=grid\",\"searchView=list\",\"id=my-store-11831089\");</script></div>",
                "text/html",
                "utf-8");
    }

    public boolean handleBackButton(){
        if(shoppingWebView.canGoBack()){
            shoppingWebView.goBack();
            return true;
        }
        return false;
    }
}
