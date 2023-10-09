package com.bcaf.yagp.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.IntRange
import androidx.annotation.Nullable

class CircularAnimatedDrawable(
    view: View,
    borderWidth: Float,
    arcColor: Int
) : Drawable(), Animatable {

    private val mAnimatedView: View = view
    private val mBorderWidth: Float = borderWidth
    private val mPaint: Paint = Paint()
    private val fBounds = RectF()

    private var mValueAnimatorAngle: ValueAnimator = ValueAnimator()
    private var mValueAnimatorSweep: ValueAnimator = ValueAnimator()

    private var mCurrentGlobalAngle: Float = 0f
    private var mCurrentSweepAngle: Float = 0f
    private var mCurrentGlobalAngleOffset: Float = 0f

    private var mModeAppearing: Boolean = true
    private var mRunning: Boolean = false

    private val MIN_SWEEP_ANGLE: Float = 30f
    private val ANGLE_ANIMATOR_DURATION = 2000L
    private val SWEEP_ANIMATOR_DURATION = 900L


    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = borderWidth
        mPaint.color = arcColor

        setupAnimations()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        val halfBorderWidth = mBorderWidth / 2
        fBounds.left = bounds.left + halfBorderWidth
        fBounds.right = bounds.right - halfBorderWidth
        fBounds.top = bounds.top + halfBorderWidth
        fBounds.bottom = bounds.bottom - halfBorderWidth
    }

    override fun draw(canvas: Canvas) {
        val startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset
        var sweepAngle = mCurrentSweepAngle
        if (!mModeAppearing) {
            sweepAngle = 360f - sweepAngle - MIN_SWEEP_ANGLE
        } else {
            sweepAngle += MIN_SWEEP_ANGLE
        }

        canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint)
    }

    override fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(@Nullable colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun start() {
        if (mRunning) {
            return
        }

        mRunning = true
        mValueAnimatorAngle.start()
        mValueAnimatorSweep.start()
    }

    override fun stop() {
        if (!mRunning) {
            return
        }

        mRunning = false
        mValueAnimatorAngle.cancel()
        mValueAnimatorSweep.cancel()
    }

    override fun isRunning(): Boolean {
        return mRunning
    }

    private fun setupAnimations() {
        // Angle Animator
        mValueAnimatorAngle = ValueAnimator.ofFloat(0f, 360f)
        mValueAnimatorAngle.interpolator = LinearInterpolator()
        mValueAnimatorAngle.duration = ANGLE_ANIMATOR_DURATION
        mValueAnimatorAngle.repeatCount = ValueAnimator.INFINITE
        mValueAnimatorAngle.addUpdateListener { animation ->
            Log.i("test", animation.animatedValue.toString())
            mCurrentGlobalAngle = animation.animatedValue as Float
            mAnimatedView.invalidate()
        }

        // Sweep Animator
        mValueAnimatorSweep = ValueAnimator.ofFloat(0f, 360f - 2 * MIN_SWEEP_ANGLE)
        mValueAnimatorSweep.interpolator = DecelerateInterpolator()
        mValueAnimatorSweep.duration = SWEEP_ANIMATOR_DURATION.toLong()
        mValueAnimatorSweep.repeatCount = ValueAnimator.INFINITE
        mValueAnimatorSweep.addUpdateListener { animation ->
            mCurrentSweepAngle = animation.animatedValue as Float
            invalidateSelf()
        }

        mValueAnimatorSweep.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator) {
                toggleAppearingMode()
            }
        })
    }

    private fun toggleAppearingMode() {
        mModeAppearing = !mModeAppearing
        if (mModeAppearing) {
            mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + MIN_SWEEP_ANGLE) % 360
        }
    }
}