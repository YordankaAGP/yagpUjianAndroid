package com.bcaf.yagp.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.bcaf.yagp.R

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {
    lateinit var text: TextView
    private var gradientDrawable: GradientDrawable
    private lateinit var mAnimatedDrawable: CircularAnimatedDrawable

    private var state = State.IDLE
    private var isMorphing = false

    private enum class State {
        PROGRESS, IDLE
    }

    init {
        gradientDrawable = ContextCompat.getDrawable(context, R.drawable.shape_default) as GradientDrawable
        background = gradientDrawable
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (state === State.PROGRESS && !isMorphing) {
            drawIndeterminateProgress(canvas)
        }
    }

    private fun drawIndeterminateProgress(canvas: Canvas) {
        if (!::mAnimatedDrawable.isInitialized || !mAnimatedDrawable.isRunning) {
            val arcWidth = 10F
            mAnimatedDrawable = CircularAnimatedDrawable(
                this,
                arcWidth,
                Color.WHITE
            )
            val offset = (width - height) / 2
            val right = width - offset
            val bottom = height
            val top = 0
            mAnimatedDrawable.setBounds(offset, top, right, bottom)
            mAnimatedDrawable.callback = this
            mAnimatedDrawable.start()
        } else {
            mAnimatedDrawable.draw(canvas)
        }
    }

    fun startAnimation() {
        if (state !== State.IDLE) return

        val initialWidth = width
        val initialHeight = height
        val initialCornerRadius = gradientDrawable.cornerRadius
        val finalCornerRadius: Float = 1000F
        var animatorSet = AnimatorSet()

        state = State.PROGRESS
        isMorphing = true
        this.setText(null)
        isClickable = false
        val toWidth = 200

        val cornerAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            gradientDrawable,
            "cornerRadius",
            initialCornerRadius,
            finalCornerRadius
        )

        val widthAnimation = ValueAnimator.ofInt(initialWidth, toWidth)
        widthAnimation.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = layoutParams
            layoutParams.width = value
            setLayoutParams(layoutParams)
        }

        val heightAnimation = ValueAnimator.ofInt(initialHeight, toWidth)
        heightAnimation.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = layoutParams
            layoutParams.height = value
            setLayoutParams(layoutParams)
        }

        animatorSet = AnimatorSet()
        animatorSet.duration = 300
        animatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isMorphing = false
            }
        })
        animatorSet.start()
    }
}
