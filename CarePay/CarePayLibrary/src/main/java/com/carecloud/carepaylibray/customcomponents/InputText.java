package com.carecloud.carepaylibray.customcomponents;

/**
 * Created by Jahirul Bhuiyan on 8/24/2016.
 */
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

public class InputText extends TextInputLayout {
    private Context context;
    EditText input;
    public InputText(Context context) {
        super(context);
        addViews();
    }
    public InputText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addViews();
    }

    public InputText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addViews();

    }
    private void addViews() {
        inflate(context, R.layout.custom_component_inputtext, this);
        input= (EditText) findViewById(R.id.input);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(100)});
    }
    public void changeInputType(int inputType){
        input.setInputType(inputType);
    }
}
