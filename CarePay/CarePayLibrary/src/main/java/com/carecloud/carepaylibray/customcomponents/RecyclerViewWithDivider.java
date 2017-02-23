package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.itemDecorations.DividerItemDecoration;

/**
 * Created by kkannan on 2/22/17.
 */

public class RecyclerViewWithDivider extends RecyclerView {

    public RecyclerViewWithDivider(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDivider(context);
    }

    public RecyclerViewWithDivider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDivider(context);
    }

    public RecyclerViewWithDivider(Context context) {
        super(context);
        setDivider(context);

    }

    /**
     * Sets divider.
     *
     * @param context the context
     */
    private void setDivider(Context context)
    {
        Drawable dividerDrawable =  ContextCompat.getDrawable(context, R.drawable.light_gray_divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        this.addItemDecoration(dividerItemDecoration);
    }
}
