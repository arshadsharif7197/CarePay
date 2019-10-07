package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SearchView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is a custom textview component that allow t set custom font from assets
 */

public class CarePaySearchView extends SearchView {
    Context context;
    int fontAttribute;

    /**
     * Public constructor with context
     *
     * @param context sender context
     */
    public CarePaySearchView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    /**
     * Public constructor with context and Attribute.
     * All the custom styleable declare are apply here also.
     *
     * @param context sender context
     * @param attrs   styleable attributes
     */
    public CarePaySearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    /**
     * Public constructor with context, Attributes and default attributes.
     * All the custom styleable declare are apply here also.
     * Default attributes also apply here
     *
     * @param context      sender context
     * @param attrs        styleable attributes
     * @param defStyleAttr styleable default attributes
     */

    public CarePaySearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);

    }

    /**
     * get applied font
     *
     * @return current font attribute
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

    /**
     * initialize
     *
     * @param attrs styleable attributes
     */
    private void init(AttributeSet attrs) {
        TypedArray typedArray;
        try {
            typedArray = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CarePayCustomAttrs,
                    0, 0);
            fontAttribute = typedArray.getInteger(R.styleable.CarePayCustomAttrs_customAssetFont, 0);
            typedArray.recycle();
        } catch (Exception exception) {
            fontAttribute = PROXIMA_NOVA_REGULAR;
        }
        setFont();


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] attributesArray = new int[]{android.R.attr.textColor};
            typedArray = context.obtainStyledAttributes(attrs, attributesArray);
            int textColor = typedArray.getResourceId(0, R.color.text_color);
            typedArray.recycle();

            ImageView closeIcon = (ImageView) findViewById(R.id.search_close_btn);
            ImageView searchIcon = (ImageView) findViewById(R.id.search_mag_icon);

            searchIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, textColor)));
            closeIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, textColor)));
        }

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

        TextView searchText = (TextView) findViewById(R.id.search_src_text);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), assetFontName);
        searchText.setTypeface(tf);
    }

    @Override
    public void setInputType(int inputType){
        super.setInputType(inputType);
        setFont();
        invalidate();
        requestLayout();
    }

}