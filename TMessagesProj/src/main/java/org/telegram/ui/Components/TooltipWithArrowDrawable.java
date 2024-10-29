package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class TooltipWithArrowDrawable extends LinearLayout {

    private View anchor;
    private ViewPropertyAnimator animator;
    private boolean showing;
    private final TextView textView;
    private ObjectAnimator textAnimator;
    private ObjectAnimator tooltipAnimator;
    private final Paint trianglePaint;
    private final Path trianglePath;

    public TooltipWithArrowDrawable(Context context, ViewGroup parentView, int backgroundColor, int textColor) {
        super(context);
        setOrientation(HORIZONTAL);  // Layout for icon and text
        setGravity(Gravity.CENTER_VERTICAL);  // Align icon and text vertically

        // Create the stacked arrow drawable and set it as the left compound drawable
        DownArrowDrawable downArrowDrawable = new DownArrowDrawable();
        // Initialize the triangle paint and path
        trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trianglePaint.setColor(backgroundColor);
        trianglePath = new Path();

        // Text setup
        textView = new TextView(context);
        textView.setTextColor(textColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(12), AndroidUtilities.dp(16), AndroidUtilities.dp(12));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setCompoundDrawablesWithIntrinsicBounds(downArrowDrawable, null, null, null);
        textView.setCompoundDrawablePadding(AndroidUtilities.dp(12));
        addView(textView);

        // Background and layout
        setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(8), backgroundColor));
        parentView.addView(this, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.BOTTOM, 5, 0, 5, 3));
        setVisibility(GONE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the triangle at the bottom-center of the tooltip
        int triangleHeight = AndroidUtilities.dp(8);
        int triangleWidth = AndroidUtilities.dp(20);

        trianglePath.reset();
        trianglePath.moveTo(getWidth() / 2f - triangleWidth / 2f, getHeight() - 0.5F);  // Starting point of triangle
        trianglePath.lineTo(getWidth() / 2f + triangleWidth / 2f, getHeight() - 0.5F);  // Bottom right point
        trianglePath.lineTo(getWidth() / 2f, getHeight() + triangleHeight);  // Bottom-center point
        trianglePath.close();

        canvas.drawPath(trianglePath, trianglePaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateTooltipPosition();
    }

    private void updateTooltipPosition() {
        if (anchor == null) {
            return;
        }
        int left = 0;

        View containerView = (View) getParent();
        View view = anchor;

        while (view != containerView) {
            left += view.getLeft();
            view = (View) view.getParent();
        }
        int x = left + anchor.getWidth() / 2 - getMeasuredWidth() / 2;
        if (x < 0) {
            x = 0;
        } else if (x + getMeasuredWidth() > containerView.getMeasuredWidth()) {
            x = containerView.getMeasuredWidth() - getMeasuredWidth() - AndroidUtilities.dp(16);
        }
        setTranslationX(x);
        setTranslationY(AndroidUtilities.dp(-75));
        setVisibility(VISIBLE);
    }

    public void show(View anchor, String message) {
        if (anchor == null) {
            return;
        }
        this.anchor = anchor;
        textView.setText(message);  // Set the tooltip text
        updateTooltipPosition();
        showing = true;

        // Start the up-down animation for both text and icon
        startAnimation();

        if (animator != null) {
            animator.setListener(null);
            animator.cancel();
            animator = null;
        }
        if (getVisibility() != VISIBLE) {
            setAlpha(0f);
            setVisibility(VISIBLE);
            animator = animate().setDuration(300).alpha(1f).setListener(null);
            animator.start();
        }
    }

    private void startAnimation() {
        // Get the current Y position after setting translationY (e.g., -206)
        float currentY = getTranslationY();
        // Animate the text up and down
        textAnimator = ObjectAnimator.ofFloat(textView, "translationY", 0f, -10f, 0f);
        textAnimator.setDuration(1000);
        textAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        textAnimator.start();

        // Animate the whole tooltip (this view) up and down
        tooltipAnimator = ObjectAnimator.ofFloat(this, "translationY", currentY, currentY - 10f, currentY);
        tooltipAnimator.setDuration(1000);
        tooltipAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        tooltipAnimator.start();
    }

    public void hide() {
        if (showing) {
            if (animator != null) {
                animator.setListener(null);
                animator.cancel();
                animator = null;
            }
            // Stop animations when hiding
            if (textAnimator != null) textAnimator.cancel();
            if (tooltipAnimator != null) tooltipAnimator.cancel();

            setVisibility(View.GONE);
        }
        showing = false;
    }
}