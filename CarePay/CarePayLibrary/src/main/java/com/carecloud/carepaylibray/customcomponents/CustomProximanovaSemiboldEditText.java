package com.carecloud.carepaylibray.customcomponents;

/**
 * Created by harshal_patil on 9/26/2016.
 * @Deprecated use CarePayTextView
 */



import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.EditText;

import com.carecloud.carepaylibrary.R;

@Deprecated
public class CustomProximanovaSemiboldEditText extends CustomEditTextBase {
    Context context;
    EditText input;

    public CustomProximanovaSemiboldEditText(Context context) {
        super(context);
        this.context=context;
        addViews();
    }

    public CustomProximanovaSemiboldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        addViews();
    }

    public CustomProximanovaSemiboldEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        addViews();
    }
    @Override
    public void addViews() {
        super.addViews();
        input.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        //input.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        this.setHintTextAppearance(R.style.TextAppearence_App_TextInputLayout);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/proximanova_semibold.otf");
        setTypeface(tf);
    }
}