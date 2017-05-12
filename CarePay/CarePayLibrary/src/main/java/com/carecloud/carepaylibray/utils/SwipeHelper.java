package com.carecloud.carepaylibray.utils;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.carecloud.carepaylibray.customcomponents.SwipeViewHolder;

/**
 * Created by lmenendez on 5/11/17
 */

public class SwipeHelper extends ItemTouchHelper.Callback {

    public interface SwipeHelperListener{
        void clearViews();

    }

    private static String TAG = SwipeHelper.class.getName();

    private View lastSwipeView = null;

    private float clearWidth = 0;
    private float maxSwipe = 0;

    private boolean bounceBack = false;

    private SwipeHelperListener listener;

    public SwipeHelper(SwipeHelperListener listener){
        this.listener = listener;
    }


    public void clearLastSwipeView(){
        Log.d(TAG, "Clear Last Swiped: " + (lastSwipeView !=null));
        if(lastSwipeView !=null){
            lastSwipeView.setLeft(0);
        }
    }


    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.d(TAG, "Selection Changed");
        if(viewHolder!=null) {
            maxSwipe = 0;

            clearLastSwipeView();
            if(listener!=null) {
                listener.clearViews();
            }

            SwipeViewHolder swipeViewHolder = (SwipeViewHolder) viewHolder;

            View swipeView = swipeViewHolder.getSwipeableView();
            if(lastSwipeView == null || !lastSwipeView.equals(swipeView)){
                Log.d(TAG, "Set new last View");
                lastSwipeView = swipeView;
            }

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

        float swipeDistance = Math.abs(distanceX);
        if( swipeDistance > maxSwipe){
            maxSwipe = swipeDistance;
            Log.d(TAG, "Max Swipe Distance: "+maxSwipe);
        }
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//            clearLastSwipeView();
        Log.d(TAG, "Get Movement Flags & Set Touch Listener");
        recyclerView.setOnTouchListener(getSwipeTouchListener((SwipeViewHolder) viewHolder));
        setClearWidth(viewHolder);
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        int absoluteDirection = bounceBack ? 0 : super.convertToAbsoluteDirection(flags, layoutDirection);
        Log.d(TAG, "Set Absolute Swipe Direction: "+absoluteDirection);
        return absoluteDirection;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    private View.OnTouchListener getSwipeTouchListener(final SwipeViewHolder swipeViewHolder){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.d(TAG, "Triggered Touch Listener");

                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    bounceBack = true;
                    if (maxSwipe > clearWidth) {
                        lastSwipeView.setLeft((int)-clearWidth);
                    }else{
                        swipeViewHolder.doViewAction();
                    }
                } else {
                    if(event.getAction() == MotionEvent.ACTION_DOWN && maxSwipe > 0){
                        maxSwipe = 0;
                    }
                    bounceBack = false;
                }
                Log.d(TAG, "Set Bounceback: "+bounceBack);
                return false;
            }
        };
    }


    private void setClearWidth(RecyclerView.ViewHolder viewHolder) {
        if(clearWidth == 0) {
            SwipeViewHolder swipeViewHolder = (SwipeViewHolder) viewHolder;
            clearWidth = swipeViewHolder.getSwipeWidth();
            Log.d(TAG, "Set Clear Width: "+clearWidth);
        }
    }





}
