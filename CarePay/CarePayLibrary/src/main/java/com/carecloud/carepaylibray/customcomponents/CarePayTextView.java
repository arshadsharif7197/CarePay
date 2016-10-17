package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFontAttribute.*;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFont.*;
/**
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is a custom textview component that allow t set custom font from assets
 */

public class CarePayTextView extends TextView {
    Context context;
    int fontAttribute;

    public CarePayTextView(Context context) {
        super(context);
        this.context=context;
    }

    public CarePayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(attrs);
    }

    public CarePayTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init(attrs);
    }

    public int getFontAttribute() {
        return fontAttribute;
    }

    public void setFontAttribute(int fontAttribute) {
        this.fontAttribute = fontAttribute;
        invalidate();
        requestLayout();
    }

    private void init(AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CarePayCustomAttrs,
                0, 0);
        try {
            fontAttribute = a.getInteger(R.styleable.CarePayCustomAttrs_customAssetFont, 0);
            String assetFontName="";
            switch (fontAttribute){
                case GOTHAM_ROUNDED_BOLD:{
                    assetFontName= FONT_GOTHAM_ROUNDED_BOLD;
                    break;
                }
                case GOTHAM_ROUNDED_BOOK:{
                    assetFontName= FONT_GOTHAM_ROUNDED_BOOK;
                    break;
                }
                case GOTHAM_ROUNDED_MEDIUM:{
                    assetFontName= FONT_GOTHAM_ROUNDED_MEDIUM;
                    break;
                }
                case PROXIMA_NOVA_EXTRA_BOLD:{
                    assetFontName= FONT_PROXIMA_NOVA_EXTRA_BOLD;
                    break;
                }
                case PROXIMA_NOVA_LIGHT:{
                    assetFontName= FONT_PROXIMA_NOVA_LIGHT;
                    break;
                }
                case PROXIMA_NOVA_SEMI_BOLD:{
                    assetFontName= FONT_PROXIMA_NOVA_SEMI_BOLD;
                    break;
                }
                default:
                    assetFontName= FONT_PROXIMA_NOVA_REGULAR;
            }

            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), assetFontName);
            this.setTypeface(tf);

        } finally {
            a.recycle();
        }
    }
}