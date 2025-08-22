/*
 *
 *  * Copyright (c) 2025 Mobarok Hossain. All rights reserved.
 *  *
 *  * Author: Mobarok Hossain
 *  * Email: mobarokop001@gmail.com, mobarokop001@gmail.com
 *  * Work Experience: Android Developer | Java & Kotlin | App Optimization | UI/UX
 *  *
 *  * This file is part of a proprietary project developed by Mobarok Hossain.
 *  * Unauthorized copying, modification, or distribution is strictly prohibited.
 *  *
 *  * Last Updated: 23/08/25, 12:21 AM
 *
 */

package com.sunriseview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import java.time.LocalTime;
import java.util.Locale;

public class SunriseView extends View {

    // ====== Public model types ======
    public static final class Time {
        public final int hour;   // 0..23
        public final int minute; // 0..59
        public Time(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }
    }

    public interface LabelFormatter {
        String format(Time t); // e.g. 05:30 or 5:30 AM
    }

    // ====== Defaults ======
    private static final int DEFAULT_TRACK_COLOR = 0xFFFFFFFF;           // white
    private static final int DEFAULT_SHADOW_COLOR = 0x32FFFFFF;          // white with alpha
    private static final int DEFAULT_SUN_COLOR = 0xFFFFC107;             // amber-ish
    private static final int DEFAULT_LABEL_COLOR = 0xFFFFFFFF;

    // dp/sp defaults
    private static final float DEFAULT_TRACK_WIDTH_DP = 2f;
    private static final float DEFAULT_SUN_RADIUS_DP = 10f;
    private static final float DEFAULT_LABEL_SIZE_SP = 14f;
    private static final float DEFAULT_LABEL_H_OFFSET_DP = 12f;
    private static final float DEFAULT_LABEL_V_OFFSET_DP = 4f;

    // ====== Paints & geometry ======
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sunPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    private final RectF arcBounds = new RectF();
    private float arcRadius;     // semicircle radius (excluding sunRadius)

    // ====== Configurable props ======
    private float ratio = 0f;    // 0f = sunrise (left), 1f = sunset (right)
    private float sunRadius;
    @ColorInt private int trackColor;
    @ColorInt private int shadowColor;
    @ColorInt private int sunColor;
    @ColorInt private int labelColor;
    private float trackWidth;
    private float labelTextSize;
    private float labelHOffset;
    private float labelVOffset;
    private PathEffect trackEffect = new DashPathEffect(new float[]{15f, 15f}, 1f);
    private Paint.Style sunStyle = Paint.Style.FILL;

    // Labels
    @Nullable private Time sunriseTime;
    @Nullable private Time sunsetTime;
    private LabelFormatter labelFormatter = t -> String.format(Locale.getDefault(), "%02d:%02d", t.hour, t.minute);

    private Drawable sunDrawable;


    // Animator (optional)
    @Nullable private ValueAnimator animator;

    public SunriseView(Context context) {
        this(context, null);
    }

    public SunriseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunriseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Defaults (convert dp/sp)
        trackWidth = dp(DEFAULT_TRACK_WIDTH_DP);
        sunRadius = dp(DEFAULT_SUN_RADIUS_DP);
        labelTextSize = sp(DEFAULT_LABEL_SIZE_SP);
        labelHOffset = dp(DEFAULT_LABEL_H_OFFSET_DP);
        labelVOffset = dp(DEFAULT_LABEL_V_OFFSET_DP);
        sunDrawable = ContextCompat.getDrawable(context, R.drawable.ic_sun);

        trackColor = DEFAULT_TRACK_COLOR;
        shadowColor = DEFAULT_SHADOW_COLOR;
        sunColor = DEFAULT_SUN_COLOR;
        labelColor = DEFAULT_LABEL_COLOR;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SunriseSunsetView, defStyleAttr, 0);
            trackColor = a.getColor(R.styleable.SunriseSunsetView_ssv_trackColor, trackColor);
            sunDrawable = a.getDrawable(R.styleable.SunriseSunsetView_ssv_sunDrawable);
            trackWidth = a.getDimension(R.styleable.SunriseSunsetView_ssv_trackWidth, trackWidth);
            shadowColor = a.getColor(R.styleable.SunriseSunsetView_ssv_shadowColor, shadowColor);
            sunColor = a.getColor(R.styleable.SunriseSunsetView_ssv_sunColor, sunColor);
            sunRadius = a.getDimension(R.styleable.SunriseSunsetView_ssv_sunRadius, sunRadius);
            labelColor = a.getColor(R.styleable.SunriseSunsetView_ssv_labelColor, labelColor);
            labelTextSize = a.getDimension(R.styleable.SunriseSunsetView_ssv_labelTextSize, labelTextSize);
            labelHOffset = a.getDimension(R.styleable.SunriseSunsetView_ssv_labelHorizontalOffset, labelHOffset);
            labelVOffset = a.getDimension(R.styleable.SunriseSunsetView_ssv_labelVerticalOffset, labelVOffset);
            a.recycle();
        }

        // Track
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);

        // Shadow fill
        shadowPaint.setStyle(Paint.Style.FILL);

        // Sun
        sunPaint.setStyle(sunStyle);

        // Text
        textPaint.setTextAlign(Paint.Align.LEFT);

        //sunDrawable.setTint(sunColor);


        applyColorsAndSizes();
        setWillNotDraw(false);
    }

    private void applyColorsAndSizes() {
        trackPaint.setColor(trackColor);
        trackPaint.setStrokeWidth(trackWidth);
        trackPaint.setPathEffect(trackEffect);

        shadowPaint.setColor(shadowColor);

        sunPaint.setColor(sunColor);

        textPaint.setColor(labelColor);
        textPaint.setTextSize(labelTextSize);
    }

    // ====== Measure & layout ======
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int) dp(120);  // fallback if wrap_content
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        float availableWidth = width - paddingLeft - paddingRight;
        // Leave sunRadius margin on both sides so the sun stays inside
        arcRadius = (availableWidth - 2f * sunRadius) / 2f;
        if (arcRadius < 0) arcRadius = 0;

        // Semicircle bounding box must be a full circle (width = height = diameter)
        float diameter = arcRadius * 2f;
        float left = paddingLeft + sunRadius;
        float top = paddingTop + sunRadius;
        arcBounds.set(left, top, left + diameter, top + diameter);

        // ✅ force height = width / 2 (half circle)

        // Step 2: আমাদের width কে parentWidth/3 করা হবে
        int finalWidth = width / 4;

        // Step 3: height হবে width/2
        int height = (int) (finalWidth * 2.3);

        setMeasuredDimension(width, height);
    }


    // ====== Drawing ======
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1) Track (semicircle, bottom half baseline)
        canvas.drawArc(arcBounds, 180f, 180f, false, trackPaint);

        // 2) Shadow under arc from left to current ratio
        float cx = arcBounds.centerX();
        float cy = arcBounds.centerY();
        double angle = Math.PI * (1.0 - clamp01(ratio)); // 180° -> 0° as ratio 0..1
        float sunX = (float) (cx + arcRadius * Math.cos(angle));
        float sunY = (float) (cy - arcRadius * Math.sin(angle));
        float baseY = arcBounds.bottom; // baseline (flat line under arc)

        Path fill = new Path();
        fill.moveTo(arcBounds.left, baseY);
        // Sweep negative from 180° towards 0° as ratio grows
        fill.arcTo(arcBounds, 180f, -180f * clamp01(ratio), true);
        fill.lineTo(sunX, baseY);
        fill.close();
        canvas.drawPath(fill, shadowPaint);

        // 3) Sun
        //canvas.drawCircle(sunX, sunY, sunRadius, sunPaint);

        // Draw Sun Drawable instead of circle
        if (sunDrawable != null) {
            int sunSize = 80; // px size
            sunDrawable.setBounds((int) (sunX - sunSize / 2), (int) (sunY - sunSize / 2),
                    (int) (sunX + sunSize / 2), (int) (sunY + sunSize / 2));
            sunDrawable.draw(canvas);
        }

        // 4) Labels
        if (sunriseTime != null && sunsetTime != null) {
            Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
            float baseline = baseY - fm.bottom - labelVOffset;

            // Sunrise (left)
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(formatTime(sunriseTime),
                    arcBounds.left + sunRadius + labelHOffset,
                    baseline,
                    textPaint);

            // Sunset (right)
            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(formatTime(sunsetTime),
                    arcBounds.right - sunRadius - labelHOffset,
                    baseline,
                    textPaint);
        }
    }

    // ====== Helpers ======
    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    private float sp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, v, getResources().getDisplayMetrics());
    }

    private static float clamp01(float f) {
        return Math.max(0f, Math.min(1f, f));
    }

    private String formatTime(Time t) {
        if (labelFormatter != null) return labelFormatter.format(t);
        return String.format(Locale.getDefault(), "%02d:%02d", t.hour, t.minute);
    }

    // ====== Public API ======
    /** ratio in [0..1], 0=left(sunrise), 1=right(sunset) */
    public void setRatio(int sunriseHour, int sunriseMinute, int sunsetHour, int sunsetMinute, LocalTime now) {
        float ratio = 0f;
        int start = sunriseHour * 60 + sunriseMinute;
        int end   = sunsetHour * 60 + sunsetMinute;
        int cur   = now.getHour() * 60 + now.getMinute();

        if (end <= start) ratio = 0f;             // invalid input guard
        if (cur <= start) ratio = 0f;             // before sunrise
        if (cur >= end) ratio = 1f;               // after sunset

        ratio =  (cur - start) / (float) (end - start);
        this.ratio = clamp01(ratio);
        animateRatio(0f, ratio, 1200);
        invalidate();
    }



    public float getRatio() {
        return ratio;
    }

    public void animateRatio(float from, float to, long durationMs) {
        if (animator != null) animator.cancel();
        animator = ValueAnimator.ofFloat(clamp01(from), clamp01(to));
        animator.setDuration(durationMs);
        animator.addUpdateListener(a -> {
            ratio = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void setSunriseTime(@Nullable Time t) {
        this.sunriseTime = t;
        invalidate();
    }

    public void setSunsetTime(@Nullable Time t) {
        this.sunsetTime = t;
        invalidate();
    }

    public void setLabelFormatter(@Nullable LabelFormatter f) {
        this.labelFormatter = f;
        invalidate();
    }

    public void setSunRadius(float px) {
        this.sunRadius = Math.max(0f, px);
        requestLayout();
        invalidate();
    }

    public void setSunColor(@ColorInt int color) {
        this.sunColor = color;
        sunPaint.setColor(color);
        invalidate();
    }

    public void setShadowColor(@ColorInt int color) {
        this.shadowColor = color;
        shadowPaint.setColor(color);
        invalidate();
    }

    public void setTrackColor(@ColorInt int color) {
        this.trackColor = color;
        trackPaint.setColor(color);
        invalidate();
    }

    public void setTrackWidth(float px) {
        this.trackWidth = Math.max(1f, px);
        trackPaint.setStrokeWidth(trackWidth);
        invalidate();
    }

    public void setTrackPathEffect(@Nullable PathEffect effect) {
        this.trackEffect = effect;
        trackPaint.setPathEffect(effect);
        invalidate();
    }

    public void setSunPaintStyle(Paint.Style style) {
        this.sunStyle = style != null ? style : Paint.Style.FILL;
        sunPaint.setStyle(this.sunStyle);
        invalidate();
    }

    public void setLabelTextColor(@ColorInt int color) {
        this.labelColor = color;
        textPaint.setColor(color);
        invalidate();
    }

    public void setLabelTextSizePx(float px) {
        this.labelTextSize = px;
        textPaint.setTextSize(px);
        invalidate();
    }

    public void setLabelHorizontalOffset(float px) {
        this.labelHOffset = px;
        invalidate();
    }

    public void setLabelVerticalOffset(float px) {
        this.labelVOffset = px;
        invalidate();
    }
}

