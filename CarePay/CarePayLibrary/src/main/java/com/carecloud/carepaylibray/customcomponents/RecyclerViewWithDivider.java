package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.carecloud.carepaylibrary.R;

/**
 * Created by kkannan on 2/22/17.
 * <p>
 * Custom RecyclerView with divider decoration.
 */
public class RecyclerViewWithDivider extends RecyclerView {

    /**
     * Instantiates a new Recycler view with divider.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public RecyclerViewWithDivider(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDivider(context);
    }

    /**
     * Instantiates a new Recycler view with divider.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public RecyclerViewWithDivider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDivider(context);
    }

    /**
     * Instantiates a new Recycler view with divider.
     *
     * @param context the context
     */
    public RecyclerViewWithDivider(Context context) {
        super(context);
        setDivider(context);

    }

    /**
     * Sets divider.
     *
     * @param context the context
     */
    private void setDivider(Context context) {
        Drawable dividerDrawable = ContextCompat.getDrawable(context, R.drawable.light_gray_divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new com.carecloud.carepaylibray
                .itemdecorations.DividerItemDecoration(dividerDrawable);
        this.addItemDecoration(dividerItemDecoration);
    }
}
