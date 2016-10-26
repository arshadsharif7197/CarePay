package com.carecloud.carepay.practice.library.customcomponent;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

/**
 * Created by Jahirul on 12/7/2015.
 * Drag-Drop Shadow Builder
 */

public class AppDragShadowBuilder extends View.DragShadowBuilder {

    private Point scaleFactor;

    // Defines the constructor for myDragShadowBuilder
    public AppDragShadowBuilder(View view) {

        // Stores the View parameter passed to myDragShadowBuilder.
        super(view);

    }

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        // Defines local variables
        int width;
        int height;

        // Sets the width of the shadow to half the width of the original View
        width = getView().getWidth()-50;

        // Sets the height of the shadow to half the height of the original View
        height = getView().getHeight() -10;

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height);
        // Sets size parameter to member that will be used for scaling shadow image.
        scaleFactor = size;

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / 2, height / 2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {

        // Draws the ColorDrawable in the Canvas passed in from the system.
        canvas.scale(scaleFactor.x / (float) getView().getWidth(), scaleFactor.y / (float) getView().getHeight());
        getView().draw(canvas);
    }

}
