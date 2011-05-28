/*
 * Copyright (c) 2011 Roberto Tyley
 *
 * This file is part of 'android-pinned-header-listviews'.
 *
 * android-pinned-header-listviews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * android-pinned-header-listviews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.android.listviews.pinnedheader;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class PinnedHeaderLayout extends ViewGroup implements PinnedHeaderTrait.HeaderViewGroupAttacher {

    private Drawable shadowDrawable; // getResources().getDrawable(R.drawable.black_white_gradient);
    private static final String TAG = "PHL";

    private PinnedHeaderTrait pinnedHeaderTrait;

    public PinnedHeaderLayout(Context context) {
        super(context);
    }

    public PinnedHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PinnedHeaderLayout);
        shadowDrawable=typedArray.getDrawable(R.styleable.PinnedHeaderLayout_shadowDrawable);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initListView();
    }

    private void initListView() {
        pinnedHeaderTrait = new PinnedHeaderTrait((ExpandableListView) getChildAt(0), this, this);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        View headerView = getHeaderView();
        if (headerView!=null && headerView.getVisibility()==VISIBLE && shadowDrawable!=null) {
            shadowDrawable.setBounds(0, headerView.getBottom(), getWidth(), 15 + headerView.getBottom());
            shadowDrawable.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View v = getChildAt(0);
        // measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
        measureChild(v, widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(
                resolveSize(v.getMeasuredWidth(), widthMeasureSpec),
                resolveSize(v.getMeasuredHeight(), heightMeasureSpec));

        View headerView = getHeaderView();
        if (headerView != null) {
            measureChild(headerView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    private View getHeaderView() {
        return pinnedHeaderTrait == null ? null : pinnedHeaderTrait.getHeaderView();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View v = getChildAt(0);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        pinnedHeaderTrait.configureHeaderView();
    }
    
    public void attach(View header) {
        View currentHeader = getChildAt(1);
        if (currentHeader == null) {
            addHeaderAndTriggerMeasure(header);
        } else if (currentHeader!=header) {
            removeViewAt(1);
            addHeaderAndTriggerMeasure(header);
        }
    }

    private void addHeaderAndTriggerMeasure(View header) {
        addView(header, 1);
        header.requestLayout();
    }
}
