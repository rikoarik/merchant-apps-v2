package com.solusinegeri.merchant3.presentation.component.loading

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.solusinegeri.merchant3.R

class LoadingPage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()

    private val primaryColor = ContextCompat.getColor(context, R.color.primary_color)
    private val backgroundColor = Color.parseColor("#33000000")

    private var sweepAngle = 0f
    private var rotationAngle = 0f
    private var scale = 1f

    private var sweepAnimator: ObjectAnimator? = null
    private var rotationAnimator: ObjectAnimator? = null
    private var scaleAnimator: ObjectAnimator? = null

    init {
        setupPaint()
        startAnimations()
    }

    private fun setupPaint() {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        paint.strokeCap = Paint.Cap.ROUND
    }

    private fun startAnimations() {
        sweepAnimator = ObjectAnimator.ofFloat(this, "sweepAngle", 30f, 300f).apply {
            duration = 1200
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            start()
        }

        rotationAnimator = ObjectAnimator.ofFloat(this, "rotationAngle", 0f, 360f).apply {
            duration = 1800
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }

        scaleAnimator = ObjectAnimator.ofFloat(this, "scale", 1f, 1.1f, 1f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(width, height) / 4f * scale

        // Background lingkaran semi transparan
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius + 24, paint)

        // Arc spinner
        paint.color = primaryColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f

        rectF.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        canvas.drawArc(rectF, rotationAngle, sweepAngle, false, paint)
    }

    fun setSweepAngle(angle: Float) {
        sweepAngle = angle
        invalidate()
    }

    fun setRotationAngle(angle: Float) {
        rotationAngle = angle
        invalidate()
    }

    fun setScale(scale: Float) {
        this.scale = scale
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimations()
    }

    private fun stopAnimations() {
        sweepAnimator?.cancel()
        rotationAnimator?.cancel()
        scaleAnimator?.cancel()
    }
}
