package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import com.carecloud.carepaylibrary.R;

import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.FONT_GOTHAM_ROUNDED_BOLD;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.FONT_GOTHAM_ROUNDED_BOOK;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.FONT_GOTHAM_ROUNDED_LIGHT;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.FONT_GOTHAM_ROUNDED_MEDIUM;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.FONT_PROXIMA_NOVA_EXTRA_BOLD;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.FONT_PROXIMA_NOVA_LIGHT;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.FONT_PROXIMA_NOVA_REGULAR;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.FONT_PROXIMA_NOVA_SEMI_BOLD;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.GOTHAM_ROUNDED_BOLD;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.GOTHAM_ROUNDED_BOOK;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.GOTHAM_ROUNDED_LIGHT;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.GOTHAM_ROUNDED_MEDIUM;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.PROXIMA_NOVA_EXTRA_BOLD;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.PROXIMA_NOVA_LIGHT;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.PROXIMA_NOVA_REGULAR;
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD;

/**
 * Created by lmenendez on 3/2/17.
 */

public class CarePayTextInputLayout extends TextInputLayout {
    private Context context;
    private int fontAttribute;

    /**
     * Constructor
     * @param context context
     */
    public CarePayTextInputLayout(Context context) {
        super(context);
        this.context = context;
        init(null);

    }

    /**
     * Constructor
     * @param context context
     * @param attrs attributes
     */
    public CarePayTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);

    }

    /**
     * Constructor
     * @param context context
     * @param attrs attibutes
     * @param defStyleAttr style
     */
    public CarePayTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);

    }

    /**
     * get applied font
     *
     * @return font attribute id
     */
    public int getFontAttribute() {
        return fontAttribute;
    }

    /**
     * set font attribute dynamically
     *
     * @param fontAttribute styleable attributes
     */
    public void setFontAttribute(int fontAttribute) {
        this.fontAttribute = fontAttribute;
        setFont();
        invalidate();
        requestLayout();
    }


    private void init(AttributeSet attrs) {
        try {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CarePayCustomAttrs,
                    0, 0);
            fontAttribute = typedArray.getInteger(R.styleable.CarePayCustomAttrs_customAssetFont, 0);
            typedArray.recycle();
        } catch (Exception exception) {
            fontAttribute = PROXIMA_NOVA_REGULAR;
        }
        setFont();
    }

    private void setFont(){
        String assetFontName = "";
        switch (fontAttribute) {
            case GOTHAM_ROUNDED_BOLD: {
                assetFontName = FONT_GOTHAM_ROUNDED_BOLD;
                break;
            }
            case GOTHAM_ROUNDED_BOOK: {
                assetFontName = FONT_GOTHAM_ROUNDED_BOOK;
                break;
            }
            case GOTHAM_ROUNDED_MEDIUM: {
                assetFontName = FONT_GOTHAM_ROUNDED_MEDIUM;
                break;
            }
            case GOTHAM_ROUNDED_LIGHT: {
                assetFontName = FONT_GOTHAM_ROUNDED_LIGHT;
                break;
            }
            case PROXIMA_NOVA_EXTRA_BOLD: {
                assetFontName = FONT_PROXIMA_NOVA_EXTRA_BOLD;
                break;
            }
            case PROXIMA_NOVA_LIGHT: {
                assetFontName = FONT_PROXIMA_NOVA_LIGHT;
                break;
            }
            case PROXIMA_NOVA_SEMI_BOLD: {
                assetFontName = FONT_PROXIMA_NOVA_SEMI_BOLD;
                break;
            }
            default:
                assetFontName = FONT_PROXIMA_NOVA_REGULAR;
        }

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), assetFontName);
        this.setTypeface(tf);
    }


}
