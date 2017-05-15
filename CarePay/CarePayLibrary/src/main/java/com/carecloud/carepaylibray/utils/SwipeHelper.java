package com.carecloud.carepaylibray.utils;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.carecloud.carepaylibray.customcomponents.SwipeViewHolder;

/**
 * Created by lmenendez on 5/11/17
 */

public class SwipeHelper extends ItemTouchHelper.Callback {

    public interface SwipeHelperListener{
        void startNewSwipe();

        void viewSwipeCompleted(SwipeViewHolder holder);

    }

    private static String TAG = SwipeHelper.class.getName();


    private SwipeHelperListener listener;

    /**
     * Constructor
     * @param listener callback for swipe start, end actions
     */
    public SwipeHelper(SwipeHelperListener listener){
        this.listener = listener;
    }


    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.d(TAG, "Selection Changed");
        if(viewHolder!=null && actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

//            clearLastSwipeView();
            if(listener!=null) {
                listener.startNewSwipe();
            }

            SwipeViewHolder swipeViewHolder = (SwipeViewHolder) viewHolder;
            View swipeView = swipeViewHolder.getSwipeableView();

            getDefaultUIUtil().onSelected(swipeView);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
        Log.d(TAG, "Clearing Views");
        SwipeViewHolder swipeViewHolder = (SwipeViewHolder) viewHolder;
        getDefaultUIUtil().clearView(swipeViewHolder.getSwipeableView());

    }


    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float distanceX, float distanceY, int actionState, boolean isCurrentlyActive) {
        Log.d(TAG, "Drawing Views");
        SwipeViewHolder swipeViewHolder = (SwipeViewHolder) viewHolder;
        View rowLayout = swipeViewHolder.getSwipeableView();

        getDefaultUIUtil().onDraw(canvas, recyclerView, rowLayout, distanceX, distanceY, actionState, isCurrentlyActive);

    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        Log.d(TAG, "Get Movement Flags");
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d(TAG, "View Swiped");
        SwipeViewHolder swipeViewHolder = (SwipeViewHolder) viewHolder;
        swipeViewHolder.displayUndoOption();
        if(listener!=null){
            listener.viewSwipeCompleted(swipeViewHolder);
        }
    }


}
