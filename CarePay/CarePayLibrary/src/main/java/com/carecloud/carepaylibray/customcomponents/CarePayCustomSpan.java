package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

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
import static com.carecloud.carepaylibray.constants.CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD;


/**
 * Created by lmenendez on 5/15/17
 */

public class CarePayCustomSpan extends MetricAffectingSpan {

    private Context context;
    private Typeface typeface;

    public CarePayCustomSpan(Context context, int fontAttribute){
        this.context = context;
        setFont(fontAttribute);
    }

    private void setFont(int fontAttribute){
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

        this.typeface = Typeface.createFromAsset(context.getAssets(), assetFontName);
    }


    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, typeface);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, typeface);
    }


    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }

}
