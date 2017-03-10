package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.carecloud.carepaylibray.utils.StringUtil;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by Jahirul Bhuiyan on 10/7/2016.
 */

public abstract class CustomEditTextBase extends TextInputLayout {
    Context context;
    EditText input;

    public CustomEditTextBase(Context context) {
        super(context);
        this.context=context;
        addViews();
    }

    public CustomEditTextBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        addViews();
    }

    public CustomEditTextBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        addViews();

    }
    public void addViews() {
        TextInputLayout.LayoutParams params=new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        input= new EditText(context);
        input.setLayoutParams(params);
        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                CustomEditTextBase.super.getOnFocusChangeListener().onFocusChange(view,b);
                if(b) {
                    CustomEditTextBase.this.setHint( CustomEditTextBase.this.getHint().toString().toUpperCase());// WordUtils.capitalize(CustomInputEditText.this.getHint().toString()));
                }else if(!b && StringUtil.isNullOrEmpty(input.getText().toString())){
                    CustomEditTextBase.this.setHint( WordUtils.capitalizeFully(CustomEditTextBase.this.getHint().toString()));
                }
            }

        });
        this.addView(input);
    }
    public void changeInputType(int inputType){
        input.setInputType(inputType);
    }
}
