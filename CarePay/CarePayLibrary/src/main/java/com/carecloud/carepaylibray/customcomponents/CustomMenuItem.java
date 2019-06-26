package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;

/**
 * @author pjohnson on 2/19/19.
 */
public class CustomMenuItem extends RelativeLayout {

    public CustomMenuItem(Context context) {
        super(context);
        init(null);
    }

    public CustomMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomMenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_item_menu, this);
        ImageView menuItemImage = findViewById(R.id.menuIconImageView);
        TextView menuIconLabelTextView = findViewById(R.id.menuIconLabelTextView);

        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.CustomMenuItem);
        Drawable itemIcon = attributes.getDrawable(R.styleable.CustomMenuItem_imageSource);
        if (itemIcon != null) {
            menuItemImage.setImageDrawable(itemIcon);
        } else {
            menuItemImage.setVisibility(GONE);
        }
        menuIconLabelTextView.setText(Label.getLabel(attributes.getString(R.styleable.CustomMenuItem_labelTextKey)));
        attributes.recycle();
    }

    public void select() {
        TextView menuIconLabelTextView = findViewById(R.id.menuIconLabelTextView);
        menuIconLabelTextView.setSelected(true);
        ImageView menuItemImage = findViewById(R.id.menuIconImageView);
        menuItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void setAlertCounter(int counter) {
        TextView badgeCounter = findViewById(R.id.badgeCounter);
        badgeCounter.setText(String.valueOf(counter));
        badgeCounter.setVisibility(VISIBLE);
    }

    public void hideBadgeCounter() {
        TextView badgeCounter = findViewById(R.id.badgeCounter);
        badgeCounter.setVisibility(GONE);
    }
}
