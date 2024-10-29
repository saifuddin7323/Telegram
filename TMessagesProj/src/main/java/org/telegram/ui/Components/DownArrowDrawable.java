package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class DownArrowDrawable extends Drawable {
    private final Paint paint;
    private final int arrowWidth;
    private final int arrowHeight;
    private final int marginBetweenArrows;
    private final Path arrowPath;  // Path to define the arrow shape

    public DownArrowDrawable() {
        // Initialize paint for the arrows
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Theme.getColor(Theme.key_featuredStickers_buttonText));  // Set the color of the arrow (adjust as needed)
        paint.setStyle(Paint.Style.STROKE);  // Outline style to create the line-based arrow
        paint.setStrokeWidth(AndroidUtilities.dp(1.1F));  // Thickness of the arrow lines
        // Set the stroke cap to ROUND to create a rounded arrow tip
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        // Set dimensions for the arrows
        arrowWidth = AndroidUtilities.dp(10);  // Width of the arrow
        arrowHeight = AndroidUtilities.dp(5);  // Height of each arrow
        marginBetweenArrows = AndroidUtilities.dp(1);  // Space between the two arrows

        // Initialize the path for the arrows
        arrowPath = new Path();
    }

    @Override
    public void draw(Canvas canvas) {
        // Calculate center point for the arrows
        int centerX = getBounds().centerX();
        int startY = getBounds().top + 6;

        // Draw the first arrow
        drawArrow(canvas, centerX, startY);

        // Draw the second arrow, slightly below the first
        drawArrow(canvas, centerX, startY + arrowHeight + marginBetweenArrows);
    }

    private void drawArrow(Canvas canvas, int centerX, int topY) {
        int leftX = centerX - arrowWidth / 2;
        int rightX = centerX + arrowWidth / 2;
        int bottomY = topY + arrowHeight;

        // Reset and define the arrow shape as a filled path (sharp, precise design)
        arrowPath.reset();
        arrowPath.moveTo(leftX, topY);  // Left corner of the arrow
        arrowPath.lineTo(centerX, bottomY);  // Bottom center (tip of the arrow)
        arrowPath.lineTo(rightX, topY);  // Right corner of the arrow
        // Draw the arrow
        canvas.drawPath(arrowPath, paint);

    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return arrowWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return 2 * arrowHeight + marginBetweenArrows;
    }
}
