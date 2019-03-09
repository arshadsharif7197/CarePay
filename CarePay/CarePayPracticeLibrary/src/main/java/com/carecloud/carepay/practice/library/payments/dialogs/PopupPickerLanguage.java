package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.adapters.LanguageAdapter;

public class PopupPickerLanguage extends PopupWindow {
    private Context context;
    private RecyclerView languageRecycler;
    private View languageContainer;
    private boolean showOnTop;
    private TextView languageSwitch;

    /**
     * Constructor
     *
     * @param context Context
     * @param showOnTop Show on the top of other view
     */
    public PopupPickerLanguage(Context context, boolean showOnTop) {
        super(context);
        this.context = context;
        this.showOnTop = showOnTop;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_picker_language, null, false);
        languageContainer = view.findViewById(R.id.languageContainer);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setWidth(context.getResources().getDimensionPixelSize(R.dimen.languageContainerWidth));
        setHeight(context.getResources().getDimensionPixelSize(R.dimen.languageContainerHeight));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        languageRecycler = view.findViewById(R.id.languageList);
        languageRecycler.setLayoutManager(layoutManager);
        if (this.showOnTop) {
            languageContainer.setBackgroundResource(R.drawable.popup_picker_bg_top_new);
        } else {
            languageContainer.setBackgroundResource(R.drawable.popup_picker_bg_new);
        }
        // to be able to hide the dialog when spinner is clicked again
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        languageSwitch.setClickable(true);
                    }
                }, 100);
            }
        });
    }

    public void setAdapter(LanguageAdapter adapter) {
        languageRecycler.setAdapter(adapter);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        languageSwitch = (TextView) anchor;
        languageSwitch.setClickable(false);
    }
}
