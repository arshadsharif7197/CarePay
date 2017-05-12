package com.carecloud.carepaylibray.customcomponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lmenendez on 5/11/17.
 */

public abstract class SwipeViewHolder extends RecyclerView.ViewHolder {

    public SwipeViewHolder(View itemView) {
        super(itemView);
    }

    public abstract int getSwipeWidth();

    public abstract View getSwipeableView();
}
