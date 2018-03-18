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

public class BounceHelper extends ItemTouchHelper.Callback {

    public interface BounceHelperListener{
        void startNewSwipe();
    }

    private static String TAG = BounceHelper.class.getName();

    private SwipeViewHolder lastSwipeView = null;

    private float clearWidth = 0;
    private float maxSwipe = 0;

    private boolean bounceBack = false;

    private BounceHelperListener listener;

    /**
     * Constructor
     * @param listener callback for swipe start, end actions
     */
    public BounceHelper(BounceHelperListener listener){
        this.listener = listener;
    }


    /**
     * Reset last swiped view offset
     */
    public void clearLastSwipeView(){
        Log.d(TAG, "Clear Last Swiped: " + (lastSwipeView !=null));
        if(lastSwipeView !=null){
            lastSwipeView.getSwipeableView().setLeft(0);
        }
    }


    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.d(TAG, "Selection Changed");
        if(viewHolder!=null) {
            clearLastSwipeView();
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
        if(maxSwipe > clearWidth) {
            lastSwipeView = swipeViewHolder;
        }
        maxSwipe = 0;
        bounceBack =false;
    }


    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float distanceX, float distanceY, int actionState, boolean isCurrentlyActive) {
        Log.d(TAG, "Drawing Views");
        SwipeViewHolder swipeViewHolder = (SwipeViewHolder) viewHolder;
        View rowLayout = swipeViewHolder.getSwipeableView();

        getDefaultUIUtil().onDraw(canvas, recyclerView, rowLayout, distanceX, distanceY, actionState, isCurrentlyActive);

        float swipeDistance = Math.abs(distanceX);
        if( swipeDistance > maxSwipe){
            maxSwipe = swipeDistance;
            Log.d(TAG, "Max Swipe Distance: "+maxSwipe);
        }else if(bounceBack){
            swipeViewHolder.getSwipeableView().setLeft((int) -clearWidth);
        }
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.d(TAG, "Get Movement Flags & Set Touch Listener");

        setClearWidth(viewHolder);
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        int absoluteDirection = maxSwipe > clearWidth ? 0 : super.convertToAbsoluteDirection(flags, layoutDirection);
        Log.d(TAG, "Set Absolute Swipe Direction: "+absoluteDirection);
        if(absoluteDirection == 0){
            bounceBack = true;
        }
        return absoluteDirection;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d(TAG, "Swipe Completed");
    }


    private void setClearWidth(RecyclerView.ViewHolder viewHolder) {
        if(clearWidth == 0) {
            SwipeViewHolder swipeViewHolder = (SwipeViewHolder) viewHolder;
            clearWidth = swipeViewHolder.getSwipeWidth();
            Log.d(TAG, "Set Clear Width: "+clearWidth);
        }
    }

}
