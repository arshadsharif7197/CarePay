package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.adapters.LanguageAdapter;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;

import java.util.List;

public class PopupPickerLanguage extends PopupWindow {

    private final List<OptionDTO> languages;
    private Context context;
    private RecyclerView languageRecycler;
    private View languageContainer;
    private boolean showOnTop;
    private TextView languageSwitch;
    private LanguageAdapter.LanguageInterface callback;

    /**
     * Constructor
     *  @param context   Context
     * @param showOnTop Show on the top of other view
     * @param languageInterface
     */
    public PopupPickerLanguage(Context context, boolean showOnTop,
                               List<OptionDTO> languages,
                               LanguageAdapter.LanguageInterface languageInterface) {
        super(context);
        this.context = context;
        this.showOnTop = showOnTop;
        this.languages = languages;
        this.callback = languageInterface;
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
        setHeight(context.getResources().getDimensionPixelSize(R.dimen.languageContainerHeight) + (65 * languages.size()));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
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
        setAdapter();
    }

    private void setAdapter() {
        String selectedLanguageStr = ApplicationPreferences.getInstance().getUserLanguage();
        OptionDTO selectedLanguage = languages.get(0);
        for (OptionDTO language : languages) {
            if (selectedLanguageStr.equals(language.getCode())) {
                selectedLanguage = language;
            }
        }
        LanguageAdapter languageAdapter = new LanguageAdapter(languages, selectedLanguage);
        languageAdapter.setCallback(new LanguageAdapter.LanguageInterface() {
            @Override
            public void onLanguageSelected(OptionDTO language) {
                dismiss();
                callback.onLanguageSelected(language);
            }
        });
        languageRecycler.setAdapter(languageAdapter);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        languageSwitch = (TextView) anchor;
        languageSwitch.setClickable(false);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        languageSwitch = (TextView) anchor;
        languageSwitch.setClickable(false);
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        languageSwitch = (TextView) anchor;
        languageSwitch.setClickable(false);
    }
}
