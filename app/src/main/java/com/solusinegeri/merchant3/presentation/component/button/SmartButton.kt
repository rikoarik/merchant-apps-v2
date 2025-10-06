package com.solusinegeri.merchant3.presentation.component.button

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.solusinegeri.merchant3.R

/**
 * Smart Button:
 * - Auto disable/enable saat diklik (opsional - bisa dimatikan)
 * - Loading state dengan spinner
 * - Success/Error state
 * - Animasi aman & dihentikan tegas saat state berubah
 */
open class SmartButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialButton(context, attrs, defStyleAttr) {

    enum class ButtonState { NORMAL, LOADING, SUCCESS, ERROR }

    private var currentState = ButtonState.NORMAL
    val isLoading: Boolean get() = currentState == ButtonState.LOADING

    private var originalText: CharSequence? = null

    // Spinner
    private val spinnerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val spinnerRect = RectF()
    private var spinnerRotation = 0f
    private var spinnerSweep = 0f
    private var spinnerAnimator: ObjectAnimator? = null
    private var sweepAnimator: ObjectAnimator? = null
    private var isSpinnerAnimating = false

     // Colors
     private val primaryColor: Int get() = com.solusinegeri.merchant3.core.utils.DynamicColors.getPrimaryColor(context)
     private val successColor = ContextCompat.getColor(context, R.color.success)
     private val errorColor = ContextCompat.getColor(context, R.color.error)
     private val disabledColor = ContextCompat.getColor(context, R.color.text_secondary)
     private val disabledTextColor = ContextCompat.getColor(context, R.color.text_secondary)

    // Behaviour
    private var autoLoadingEnabled: Boolean = true

    init {
        setupSpinnerPaint()
        setupButtonAppearance()
        originalText = text
        minimumHeight = resources.getDimensionPixelSize(R.dimen.login_button_height)
    }

    /** Aktif/nonaktifkan auto loading ketika performClick() */
    fun setAutoLoadingEnabled(enabled: Boolean) {
        autoLoadingEnabled = enabled
    }

    private fun setupSpinnerPaint() {
        spinnerPaint.style = Paint.Style.STROKE
        spinnerPaint.strokeWidth = 4f
        spinnerPaint.strokeCap = Paint.Cap.ROUND
        spinnerPaint.color = Color.WHITE
    }

    private fun setupButtonAppearance() {
        cornerRadius = resources.getDimensionPixelSize(R.dimen.button_corner_radius)

        val minTextSize = resources.getDimensionPixelSize(R.dimen.button_text_size_min)
        if (textSize < minTextSize) textSize = minTextSize.toFloat()

        setTextColor(Color.WHITE)
        textAlignment = TEXT_ALIGNMENT_CENTER
        gravity = Gravity.CENTER
        setGravity(Gravity.CENTER)
        setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom)
    }

    /** Jalankan block di main thread */
    private fun runOnUi(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block() else post { block() }
    }

    /** Ganti state (thread-safe) */
    fun setState(state: ButtonState) {
        runOnUi {
            if (currentState == state) return@runOnUi
            currentState = state
            updateButtonAppearance()
        }
    }

    /** Set loading dengan auto-disable/enable */
    fun setLoading(loading: Boolean) {
        runOnUi {
            if (loading) {
                setState(ButtonState.LOADING)
                setDisabledAppearance(false) // disabled visual saat loading
            } else {
                setState(ButtonState.NORMAL)
                setDisabledAppearance(true)  // balik enable
            }
        }
    }

    /** Visual disabled/enabled */
    private fun setDisabledAppearance(enabled: Boolean) {
        isEnabled = enabled
        if (!enabled && currentState == ButtonState.NORMAL) {
            setBackgroundColor(disabledColor)
            setTextColor(disabledTextColor)
            alpha = 0.6f
        } else {
            alpha = 1.0f
            updateButtonAppearance()
        }
    }

     /** Success state (auto kembali NORMAL) */
     fun setSuccess(message: String? = null) {
         runOnUi {
             setState(ButtonState.SUCCESS)
             if (message != null) text = message
             setState(ButtonState.NORMAL)
             text = originalText
             isEnabled = true
         }
     }

     /** Error state (auto kembali NORMAL) */
     fun setError(message: String? = null) {
         runOnUi {
             setState(ButtonState.ERROR)
             if (message != null) text = message
             setState(ButtonState.NORMAL)
             text = originalText
             isEnabled = true
         }
     }

    /** Terapkan tampilan sesuai state (hentikan animasi dahulu agar tegas) */
    private fun updateButtonAppearance() {
        when (currentState) {
            ButtonState.NORMAL -> {
                toggleSpinnerAnimation(false)
                setBackgroundColor(primaryColor)
                if (text.isNullOrEmpty()) text = originalText
                isEnabled = true
            }
            ButtonState.LOADING -> {
                setBackgroundColor(primaryColor)
                text = ""
                isEnabled = false
                toggleSpinnerAnimation(true)
            }
            ButtonState.SUCCESS -> {
                toggleSpinnerAnimation(false)
                setBackgroundColor(successColor)
                isEnabled = false
            }
             ButtonState.ERROR -> {
                 toggleSpinnerAnimation(false)
                 setBackgroundColor(errorColor)
                 isEnabled = false
             }
        }
        ensureTextCentered()
        invalidate()
    }

    /** Pastikan teks terpusat */
    private fun ensureTextCentered() {
        textAlignment = View.TEXT_ALIGNMENT_CENTER
        gravity = Gravity.CENTER
        setGravity(Gravity.CENTER)
    }

    /** Start/stop animasi spinner (tanpa setFrameDelay global) */
    private fun toggleSpinnerAnimation(enable: Boolean) {
        if (enable) {
            if (isSpinnerAnimating) return
            isSpinnerAnimating = true

            spinnerAnimator = ObjectAnimator.ofFloat(this, "spinnerRotation", 0f, 360f).apply {
                duration = 1000L
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                start()
            }

            sweepAnimator = ObjectAnimator.ofFloat(this, "spinnerSweep", 60f, 300f, 60f).apply {
                duration = 1200L
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                start()
            }
        } else {
            if (!isSpinnerAnimating) return
            isSpinnerAnimating = false

            spinnerAnimator?.cancel()
            sweepAnimator?.cancel()
            spinnerAnimator = null
            sweepAnimator = null

            spinnerRotation = 0f
            spinnerSweep = 0f
            invalidate()
        }
    }

    /** Render */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (currentState == ButtonState.LOADING && isSpinnerAnimating) {
            drawSpinner(canvas)
        }
    }

    /** Gambar spinner */
    private fun drawSpinner(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(width, height) / 6f

        spinnerRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        canvas.drawArc(spinnerRect, spinnerRotation, spinnerSweep, false, spinnerPaint)
    }

    /** Dipanggil animator (property) */
    @Suppress("unused")
    fun setSpinnerRotation(rotation: Float) {
        spinnerRotation = rotation
        if (isSpinnerAnimating) invalidate() else invalidate() // paksa redraw frame terakhir juga
    }

    /** Dipanggil animator (property) */
    @Suppress("unused")
    fun setSpinnerSweep(sweep: Float) {
        spinnerSweep = sweep
        if (isSpinnerAnimating) invalidate() else invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        toggleSpinnerAnimation(false)
        removeCallbacks(null)
    }

    /** Reset ke NORMAL */
    fun reset() {
        runOnUi {
            setState(ButtonState.NORMAL)
            text = originalText
            isEnabled = true
        }
    }

    /** Pastikan teks tetap center saat di-set */
    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        ensureTextCentered()
    }

     /** Set enable + tampilan */
     fun setButtonEnabled(enabled: Boolean) {
         super.setEnabled(enabled)
         setDisabledAppearance(enabled)
     }
     
     /** Update warna button secara realtime */
     fun updateColors() {
         runOnUi {
             updateButtonAppearance()
         }
     }

    /**
     * Auto-loading opsional: kalau aktif, klik akan set LOADING.
     * Disarankan kontrol loading dari ViewModel:
     *  - button.setLoading(true) saat mulai API
     *  - button.setSuccess()/setError()/setLoading(false) saat selesai
     */
    override fun performClick(): Boolean {
        if (currentState == ButtonState.LOADING) return false
        val result = super.performClick()
        if (autoLoadingEnabled && currentState == ButtonState.NORMAL && isEnabled) {
            setLoading(true)
        }
        return result
    }
}
