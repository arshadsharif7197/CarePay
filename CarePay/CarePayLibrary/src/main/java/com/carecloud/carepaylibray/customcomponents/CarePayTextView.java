package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.carecloud.carepaylibrary.R;

import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFont.FONT_GOTHAM_ROUNDED_BOOK;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFont.FONT_GOTHAM_ROUNDED_MEDIUM;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFont.FONT_PROXIMA_NOVA_EXTRA_BOLD;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFont.FONT_PROXIMA_NOVA_LIGHT;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFont.FONT_PROXIMA_NOVA_REGULAR;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFont.FONT_PROXIMA_NOVA_SEMI_BOLD;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFont.FONT_GOTHAM_ROUNDED_BOLD;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFontAttribute.GOTHAM_ROUNDED_BOLD;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFontAttribute.GOTHAM_ROUNDED_BOOK;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFontAttribute.GOTHAM_ROUNDED_MEDIUM;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFontAttribute.PROXIMA_NOVA_EXTRA_BOLD;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFontAttribute.PROXIMA_NOVA_LIGHT;
import static com.carecloud.carepaylibray.customcomponents.CustomAssetStyleable.CustomAssetFontAttribute.PROXIMA_NOVA_SEMI_BOLD;

/**
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is a custom textview component that allow t set custom font from assets
 */

public class CarePayTextView extends TextView {
    Context context;
    int fontAttribute;

    /**
     * Public constructor with context
     * @param context
     */
    public CarePayTextView(Context context) {
        super(context);
        this.context=context;
    }

    /**
     * Public constructor with context and Attribute.
     * All the custom styleable declare are apply here also.
     * @param context
     * @param attrs
     */
    public CarePayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(attrs);
    }

    /**
     * Public constructor with context, Attributes and default attributes.
     * All the custom styleable declare are apply here also.
     * Default attributes also apply here
     * @param context
     * @param attrs
     * @param defStyleAttr
     */

    public CarePayTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init(attrs);
    }

    /**
     * get applied font
     * @return
     */
    public int getFontAttribute() {
        return fontAttribute;
    }

    /**
     * set font attribute dynamically
     * @param fontAttribute
     */
    public void setFontAttribute(int fontAttribute) {
        this.fontAttribute = fontAttribute;
        invalidate();
        requestLayout();
    }

    /**
     * initialize
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CarePayCustomAttrs,
                0, 0);
        try {
            fontAttribute = typedArray.getInteger(R.styleable.CarePayCustomAttrs_customAssetFont, 0);
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
            typedArray.recycle();
        }
    }
}