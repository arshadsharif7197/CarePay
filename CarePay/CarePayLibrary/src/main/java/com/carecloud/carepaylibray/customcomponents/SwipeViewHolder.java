package com.carecloud.carepaylibray.customcomponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lmenendez on 5/11/17.
 */

public abstract class SwipeViewHolder extends RecyclerView.ViewHolder {

    public interface OnActionListener {
        void onAction(SwipeViewHolder holder);
    }

    private OnActionListener actionListener;

    public SwipeViewHolder(View itemView) {
        super(itemView);
    }

    public void setOnActionListener(OnActionListener actionListener){
        this.actionListener = actionListener;
    }

    public void doViewAction(){
        if(actionListener!=null){
            actionListener.onAction(this);
        }
    }

    public abstract int getSwipeWidth();

    public abstract View getSwipeableView();


}
