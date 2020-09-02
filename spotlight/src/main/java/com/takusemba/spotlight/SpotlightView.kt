package com.takusemba.spotlight

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.ofFloat
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import kotlin.math.max

/**
 * [SpotlightView] starts/finishes [Spotlight], and starts/finishes a current [Target].
 */
internal class SpotlightView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @ColorRes backgroundColor: Int = R.color.background
) : FrameLayout(context, attrs, defStyleAttr) {

  private var textViewContainer: RelativeLayout? = null
  private val backgroundPaint by lazy {
    Paint().apply { color = ContextCompat.getColor(context, backgroundColor) }
  }

  private val shapePaint by lazy {
    Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
  }

  private val effectPaint by lazy { Paint() }

  private val invalidator = AnimatorUpdateListener { invalidate() }

  private var shapeAnimator: ValueAnimator? = null
  private var effectAnimator: ValueAnimator? = null
  private var textAnimator: ValueAnimator? = null
  private var target: Target? = null

  init {
    setWillNotDraw(false)
    setLayerType(View.LAYER_TYPE_HARDWARE, null)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
    val currentTarget = target
    val currentShapeAnimator = shapeAnimator
    val currentEffectAnimator = effectAnimator
    if (currentTarget != null && currentEffectAnimator != null) {
      currentTarget.effect.draw(
          canvas = canvas,
          point = currentTarget.anchor,
          value = currentEffectAnimator.animatedValue as Float,
          paint = effectPaint
      )
    }
    if (currentTarget != null && currentShapeAnimator != null) {
      currentTarget.shape.draw(
          canvas = canvas,
          point = currentTarget.anchor,
          value = currentShapeAnimator.animatedValue as Float,
          paint = shapePaint
      )
    }

    textViewContainer?.let { textViewContainer ->
      if (textViewContainer.x + textViewContainer.width > width) {

        if (textViewContainer.width > width - 20) {
          textViewContainer.x = 10F
          val lp = textViewContainer.layoutParams
          lp.width = width - 20
          textViewContainer.layoutParams = lp
        } else {
          textViewContainer.x = width - 10F - textViewContainer.width
        }
      }
      if (textViewContainer.y + textViewContainer.height + 10 > height) {
        target?.let { target ->
          textViewContainer.y = target.anchor.y - target.height / 2 - 25 - textViewContainer.height
        }
      }
    }
  }

  /**
   * Starts [Spotlight].
   */
  fun startSpotlight(
      duration: Long,
      interpolator: TimeInterpolator,
      listener: Animator.AnimatorListener
  ) {
    val objectAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
      setDuration(duration)
      setInterpolator(interpolator)
      addListener(listener)
    }
    objectAnimator.start()
  }

  /**
   * Finishes [Spotlight].
   */
  fun finishSpotlight(
      duration: Long,
      interpolator: TimeInterpolator,
      listener: Animator.AnimatorListener
  ) {
    val objectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
      setDuration(duration)
      setInterpolator(interpolator)
      addListener(listener)
    }
    objectAnimator.start()
  }

  /**
   * Starts the provided [Target].
   */
  fun startTarget(target: Target, listener: Animator.AnimatorListener) {
    removeAllViews()
    target.overlay?.let {
      addView(it, MATCH_PARENT, MATCH_PARENT)
    }

    target.text?.let {
      val textViewContainer = LayoutInflater.from(context).inflate(R.layout.textview_message,
          null) as RelativeLayout
      this.textViewContainer = textViewContainer
      textViewContainer.findViewById<TextView>(R.id.textView)?.apply {
        text = it.text
      }
      val x = target.anchor.x - target.width / 2 - 20
      textViewContainer.x = max(x, 0F)
      val yBelow = target.anchor.y + target.height / 2 + 25
      if (yBelow + 50 > height) {
        val yAbove = target.anchor.y - target.height / 2 - 25
        textViewContainer.y = yAbove
      } else {
        textViewContainer.y = yBelow
      }
      val lp = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
      textViewContainer.layoutParams = lp
      textViewContainer.gravity = Gravity.CENTER_HORIZONTAL
      addView(textViewContainer)
      textViewContainer.alpha = 0F
    }

    setOnClickListener {
      target.listener?.onClicked()
    }

    this.target = target
    this.shapeAnimator = ofFloat(0f, 1f).apply {
      duration = target.shape.duration
      interpolator = target.shape.interpolator
      addUpdateListener(invalidator)
      addListener(listener)
    }
    this.effectAnimator = ofFloat(0f, 1f).apply {
      duration = target.effect.duration
      interpolator = target.effect.interpolator
      repeatMode = target.effect.repeatMode
      repeatCount = INFINITE
      addUpdateListener(invalidator)
      addListener(listener)
    }
    if (target.text != null) {
      this.textAnimator = ofFloat(0f, 1f).apply {
        duration = target.shape.duration
        addUpdateListener {
          val alpha = it.animatedValue as Float
          textViewContainer?.alpha = alpha
        }
      }
    }
    shapeAnimator?.start()
    effectAnimator?.start()
    textAnimator?.start()
  }

  /**
   * Finishes the current [Target].
   */
  fun finishTarget(listener: Animator.AnimatorListener) {
    val currentTarget = target ?: return
    val currentShapeAnimator = shapeAnimator ?: return
    shapeAnimator = ofFloat(currentShapeAnimator.animatedValue as Float, 0f).apply {
      duration = currentTarget.shape.duration
      interpolator = currentTarget.shape.interpolator
      addUpdateListener(invalidator)
      addListener(listener)
    }
    effectAnimator?.cancel()
    effectAnimator = null
    shapeAnimator?.start()
    if (currentTarget.text != null) {
      textAnimator = ofFloat(1f, 0f).apply {
        duration = currentTarget.shape.duration
        addUpdateListener {
          val alpha = it.animatedValue as Float
          textViewContainer?.alpha = alpha
        }
      }

      textAnimator?.start()
    }
  }
}
