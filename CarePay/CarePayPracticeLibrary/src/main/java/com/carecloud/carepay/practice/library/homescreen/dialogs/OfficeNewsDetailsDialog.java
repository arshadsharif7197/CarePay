package com.carecloud.carepay.practice.library.homescreen.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenOfficeNewsDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OfficeNewsDetailsDialog extends BaseDialogFragment {

    private List<HomeScreenOfficeNewsDTO> officeNewsList;
    private List<View> officeNewsPages;
    private int position;
    private RadioGroup pages;

    /**
     * Constructor
     *
     * @param officeNewsList data
     */
    public static OfficeNewsDetailsDialog newInstance(List<HomeScreenOfficeNewsDTO> officeNewsList,
                                                      int position) {
        Bundle args = new Bundle();
        args.putSerializable("officeNewsList", (Serializable) officeNewsList);
        args.putInt("position", position);
        OfficeNewsDetailsDialog fragment = new OfficeNewsDetailsDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        officeNewsList = (List<HomeScreenOfficeNewsDTO>) args.getSerializable("officeNewsList");
        position = args.getInt("position");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addView(view);
        initializeView(view);
        handleException();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_office_news_details, container, false);
    }

    @SuppressWarnings("AccessStaticViaInstance")
    private void handleException() {
        Thread thread = Thread.currentThread();
        thread.setDefaultUncaughtExceptionHandler(new SystemUtil());
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    private void initializeView(View view) {
        if (officeNewsList != null && !officeNewsList.isEmpty()) {
            NewsPagerAdapter adapter = new NewsPagerAdapter();
            ViewPager newsArticle = view.findViewById(R.id.office_news_viewpager);
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
    private void addView(View view) {
        officeNewsPages = new ArrayList<>();
        pages = view.findViewById(R.id.office_news_page_indicator);
        RadioGroup.LayoutParams params;

        for (int i = 0; i < officeNewsList.size(); i++) {
            HomeScreenOfficeNewsDTO officeNewsDTO = officeNewsList.get(i);
            WebView webView = new WebView(getContext());
            webView.loadUrl(officeNewsDTO.getPayload().getNewsUrl());
            officeNewsPages.add(webView);

            RadioButton newsPages = new RadioButton(getContext());
            newsPages.setId(i);
            newsPages.setButtonDrawable(getResources().getDrawable(R.drawable.office_news_page_indicator));
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
