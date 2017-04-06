package com.carecloud.carepay.patient.tutorial.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.carecloud.carepay.patient.R;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

/**
 * Draws circles (one for each view). The current view position is filled and
 * others are only stroked.
 */
public class CirclePageIndicator extends View implements PageIndicator {
    private static final int INVALID_POINTER = -1;

    private float radius;
    private final Paint paintPageFill = new Paint(ANTI_ALIAS_FLAG);
    private final Paint paintStroke = new Paint(ANTI_ALIAS_FLAG);
    private final Paint paintFill = new Paint(ANTI_ALIAS_FLAG);
    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener listener;
    private int currentPage;
    private int snapPage;
    private float pageOffset;
    private int scrollState;
    private int orientation;
    private boolean centered;
    private boolean snap;

    private int touchSlop;
    private float lastMotionX = -1;
    private int activePointerId = INVALID_POINTER;
    private boolean isDragging;

    public CirclePageIndicator(Context context) {
        this(context, null);
    }

    /**
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public CirclePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.vpiCirclePageIndicatorStyle);
    }

    /**
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the view. Can be 0 to not look for defaults.
     */
    public CirclePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            return;
        }

        //Load defaults from resources
        final Resources res = getResources();
        final int defaultPageColor = res.getColor(R.color.default_circle_indicator_page_color);
        final int defaultFillColor = res.getColor(R.color.default_circle_indicator_fill_color);
        final int defaultOrientation = res.getInteger(R.integer.default_circle_indicator_orientation);
        final int defaultStrokeColor = res.getColor(R.color.default_circle_indicator_stroke_color);
        final float defaultStrokeWidth = res.getDimension(R.dimen.default_circle_indicator_stroke_width);
        final float defaultRadius = res.getDimension(R.dimen.default_circle_indicator_radius);
        final boolean defaultCentered = res.getBoolean(R.bool.default_circle_indicator_centered);
        final boolean defaultSnap = res.getBoolean(R.bool.default_circle_indicator_snap);

        //Retrieve styles attributes
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CirclePageIndicator, defStyle, 0);

        centered = array.getBoolean(R.styleable.CirclePageIndicator_centered, defaultCentered);
        orientation = array.getInt(R.styleable.CirclePageIndicator_android_orientation, defaultOrientation);
        paintPageFill.setStyle(Style.FILL);
        paintPageFill.setColor(array.getColor(R.styleable.CirclePageIndicator_pageColor, defaultPageColor));
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setColor(array.getColor(R.styleable.CirclePageIndicator_strokeColor, defaultStrokeColor));
        paintStroke.setStrokeWidth(array.getDimension(R.styleable.CirclePageIndicator_strokeWidth, defaultStrokeWidth));
        paintFill.setStyle(Style.FILL);
        paintFill.setColor(array.getColor(R.styleable.CirclePageIndicator_fillColor, defaultFillColor));
        radius = array.getDimension(R.styleable.CirclePageIndicator_radius, defaultRadius);
        snap = array.getBoolean(R.styleable.CirclePageIndicator_snap, defaultSnap);

        Drawable background = array.getDrawable(R.styleable.CirclePageIndicator_android_background);
        if (background != null) {
            setBackgroundDrawable(background);
        }

        array.recycle();

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    /**
     * @param orientation new orientation
     */
    public void setOrientation(int orientation) {
        switch (orientation) {
            case HORIZONTAL:
            case VERTICAL:
                this.orientation = orientation;
                requestLayout();
                break;

            default:
                throw new IllegalArgumentException("Orientation must be either HORIZONTAL or VERTICAL.");
        }
    }

    public int getOrientation() {
        return orientation;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (viewPager == null) {
            return;
        }
        final int count = viewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }

        if (currentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }

        int longSize;
        int longPaddingBefore;
        int longPaddingAfter;
        int shortPaddingBefore;
        if (orientation == HORIZONTAL) {
            longSize = getWidth();
            longPaddingBefore = getPaddingLeft();
            longPaddingAfter = getPaddingRight();
            shortPaddingBefore = getPaddingTop();
        } else {
            longSize = getHeight();
            longPaddingBefore = getPaddingTop();
            longPaddingAfter = getPaddingBottom();
            shortPaddingBefore = getPaddingLeft();
        }

        final float fourRadius = radius * 4;
        final float shortOffset = shortPaddingBefore + radius;
        float longOffset = longPaddingBefore + radius;
        if (centered) {
            longOffset += ((longSize - longPaddingBefore - longPaddingAfter) / 2.0f) - ((count * fourRadius) / 2.0f);
        }

        float denterX;
        float denterY;

        float pageFillRadius = radius;
        if (paintStroke.getStrokeWidth() > 0) {
            pageFillRadius -= paintStroke.getStrokeWidth() / 2.0f;
        }

        //Draw stroked circles
        for (int i = 0; i < count; i++) {
            float drawLong = longOffset + (i * fourRadius);
            if (orientation == HORIZONTAL) {
                denterX = drawLong;
                denterY = shortOffset;
            } else {
                denterX = shortOffset;
                denterY = drawLong;
            }
            // Only paint fill if not completely transparent
            if (paintPageFill.getAlpha() > 0) {
                canvas.drawCircle(denterX, denterY, pageFillRadius, paintPageFill);
            }

            // Only paint stroke if a stroke width was non-zero
            if (pageFillRadius != radius) {
                canvas.drawCircle(denterX, denterY, radius, paintStroke);
            }
        }

        //Draw the filled circle according to the current scroll
        float centerX = (snap ? snapPage : currentPage) * fourRadius;
        if (!snap) {
            centerX += pageOffset * fourRadius;
        }
        if (orientation == HORIZONTAL) {
            denterX = longOffset + centerX;
            denterY = shortOffset;
        } else {
            denterX = shortOffset;
            denterY = longOffset + centerX;
        }
        canvas.drawCircle(denterX, denterY, radius, paintFill);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        if (super.onTouchEvent(ev)) {
            return true;
        }
        if ((viewPager == null) || (viewPager.getAdapter().getCount() == 0)) {
            return false;
        }

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = MotionEventCompat.getPointerId(ev, 0);
                lastMotionX = ev.getX();
                break;

            case MotionEvent.ACTION_MOVE: {
                final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                final float x = MotionEventCompat.getX(ev, activePointerIndex);
                final float deltaX = x - lastMotionX;

                if (!isDragging && Math.abs(deltaX) > touchSlop) {
                    isDragging = true;
                }

                if (isDragging) {
                    lastMotionX = x;
                    if (viewPager.isFakeDragging() || viewPager.beginFakeDrag()) {
                        viewPager.fakeDragBy(deltaX);
                    }
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isDragging) {
                    final int count = viewPager.getAdapter().getCount();
                    final int width = getWidth();
                    final float halfWidth = width / 2f;
                    final float sixthWidth = width / 6f;

                    if ((currentPage > 0) && (ev.getX() < halfWidth - sixthWidth)) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            viewPager.setCurrentItem(currentPage - 1);
                        }
                        return true;
                    } else if ((currentPage < count - 1) && (ev.getX() > halfWidth + sixthWidth)) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            viewPager.setCurrentItem(currentPage + 1);
                        }
                        return true;
                    }
                }

                isDragging = false;
                activePointerId = INVALID_POINTER;
                if (viewPager.isFakeDragging()) {
                    viewPager.endFakeDrag();
                }
                break;

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                lastMotionX = MotionEventCompat.getX(ev, index);
                activePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == activePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                lastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, activePointerId));
                break;

            default:
                // Nothing but Code Climate Cares
        }

        return true;
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (viewPager == view) {
            return;
        }
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(null);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        viewPager = view;
        viewPager.setOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void setCurrentItem(int item) {
        if (viewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        viewPager.setCurrentItem(item);
        currentPage = item;
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        scrollState = state;

        if (listener != null) {
            listener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPage = position;
        pageOffset = positionOffset;
        invalidate();

        if (listener != null) {
            listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (snap || scrollState == ViewPager.SCROLL_STATE_IDLE) {
            currentPage = position;
            snapPage = position;
            invalidate();
        }

        if (listener != null) {
            listener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.listener = listener;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (orientation == HORIZONTAL) {
            setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
        } else {
            setMeasuredDimension(measureShort(widthMeasureSpec), measureLong(heightMeasureSpec));
        }
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureLong(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (viewPager == null)) {
            //We were told how big to be
            result = specSize;
        } else {
            //Calculate the width according the views count
            final int count = viewPager.getAdapter().getCount();
            result = (int) (getPaddingLeft() + getPaddingRight()
                    + (count * 2 * radius) + (count - 1) * radius + 1);
            //Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureShort(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            //We were told how big to be
            result = specSize;
        } else {
            //Measure the height
            result = (int) (2 * radius + getPaddingTop() + getPaddingBottom() + 1);
            //Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPage = savedState.currentPage;
        snapPage = savedState.currentPage;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPage = currentPage;
        return savedState;
    }

    private static class SavedState extends BaseSavedState {
        int currentPage;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPage = in.readInt();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPage);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
