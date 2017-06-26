package com.electapps.fontviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;



/**
 * Created by leo on 10/30/2015.
 */
public class FontEditText extends EditText {

    public FontEditText(Context context) {
        super(context);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray ta = ctx.obtainStyledAttributes(attrs, R.styleable.FontTextView);
        String customFont = ta.getString(R.styleable.FontTextView_customFont);
        setCustomFont(ctx, customFont);
        ta.recycle();
    }

    /**
     * Set a custom font for this view
     * @param ctx context
     * @param asset font file name in root if app assets directory
     * @return true if font is set
     */
    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "Could not get typeface: " + ex.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }
}
