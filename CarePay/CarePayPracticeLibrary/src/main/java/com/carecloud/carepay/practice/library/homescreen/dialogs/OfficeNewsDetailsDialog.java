package com.carecloud.carepay.practice.library.homescreen.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenOfficeNewsDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

public class OfficeNewsDetailsDialog extends Dialog {

    private Context context;
    private List<HomeScreenOfficeNewsDTO> officeNewsList;
    private List<View> officeNewsPages;
    private int position;
    private RadioGroup pages;

    /**
     * Constructor
     *
     * @param context        context
     * @param officeNewsList data
     */
    public OfficeNewsDetailsDialog(Context context, List<HomeScreenOfficeNewsDTO> officeNewsList,
                                   int position) {
        super(context);
        this.context = context;
        this.position = position;
        this.officeNewsList = officeNewsList;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_office_news_details);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        addView();
        initializeView();
        handleException();
    }

    @SuppressWarnings("AccessStaticViaInstance")
    private void handleException() {
        Thread thread = Thread.currentThread();
        thread.setDefaultUncaughtExceptionHandler(new SystemUtil());
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    private void initializeView() {
        if (officeNewsList != null && !officeNewsList.isEmpty()) {
            NewsPagerAdapter adapter = new NewsPagerAdapter();
            ViewPager newsArticle = findViewById(R.id.office_news_viewpager);
            newsArticle.setAdapter(adapter);

            newsArticle.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrollStateChanged(int state) {
                }

                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    RadioButton pageIndicator = (RadioButton) pages.getChildAt(position);
                    pageIndicator.setChecked(true);
                }
            });

            newsArticle.setCurrentItem(position, true);
        }

        findViewById(R.id.office_news_details_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void addView() {
        officeNewsPages = new ArrayList<>();
        pages = findViewById(R.id.office_news_page_indicator);
        RadioGroup.LayoutParams params;

        for (int i = 0; i < officeNewsList.size(); i++) {
            HomeScreenOfficeNewsDTO officeNewsDTO = officeNewsList.get(i);
            WebView webView = new WebView(context);
            webView.loadUrl(officeNewsDTO.getPayload().getNewsUrl());
            officeNewsPages.add(webView);

            RadioButton newsPages = new RadioButton(context);
            newsPages.setId(i);
            newsPages.setButtonDrawable(context.getResources().getDrawable(R.drawable.office_news_page_indicator));
            params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 10, 0);
            pages.addView(newsPages, params);
        }

        ((RadioButton) pages.getChildAt(position)).setChecked(true);
    }

    @SuppressWarnings("deprecation")
    private class NewsPagerAdapter extends PagerAdapter {
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(officeNewsPages.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {

        }

        @Override
        public int getCount() {
            return officeNewsPages.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(officeNewsPages.get(arg1), 0);
            return officeNewsPages.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }
    }
}
