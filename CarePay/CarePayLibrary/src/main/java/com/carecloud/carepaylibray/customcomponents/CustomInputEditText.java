package com.carecloud.carepaylibray.customcomponents;

/**
 * Created by harshal_patil on 9/26/2016.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import com.carecloud.carepaylibrary.R;

public class CustomInputEditText extends TextInputLayout {
    private Context context;
    EditText input;

    public CustomInputEditText(Context context) {
        super(context);
        this.context=context;
        addViews();
    }

    public CustomInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        addViews();
    }

    public CustomInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        addViews();

    }
    private void addViews() {
        TextInputLayout.LayoutParams params=new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        input= new EditText(context);
        input.setLayoutParams(params);
        input.setTextColor(ContextCompat.getColor(context, R.color.blue_cerulian));
        //input.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        this.setHintTextAppearance(R.style.TextAppearence_App_TextInputLayout);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/proximanova_semibold.otf");
        setTypeface(tf);

        this.addView(input);
    }
    public void changeInputType(int inputType){
        input.setInputType(inputType);
    }
}