package com.takusemba.spotlight

import android.graphics.Point
import android.graphics.PointF
import android.os.Build
import android.util.SizeF
import android.view.View
import androidx.annotation.RequiresApi
import com.takusemba.spotlight.effet.Effect
import com.takusemba.spotlight.effet.EmptyEffect
import com.takusemba.spotlight.shape.Circle
import com.takusemba.spotlight.shape.RoundedRectangle
import com.takusemba.spotlight.shape.Shape

/**
 * Target represents the spot that Spotlight will cast.
 */
class Target(
    val anchor: PointF,
    val width: Float,
    val height: Float,
    val text: Text?,
    val shape: Shape,
    val effect: Effect,
    val overlay: View?,
    var listener: OnTargetListener?
) {

  class Text(val text: CharSequence) { }

  /**
   * [Builder] to build a [Target].
   * All parameters should be set in this [Builder].
   */
  class Builder {

    private var anchor: PointF = DEFAULT_ANCHOR
    private var width: Float = 0F
    private var height: Float = 0F
    private var text: Text? = null
    private var shape: Shape = DEFAULT_SHAPE
    private var effect: Effect = DEFAULT_EFFECT
    private var overlay: View? = null
    private var listener: OnTargetListener? = null

    /**
     * Sets a pointer to start a [Target].
     */
    fun setView(view: View): Builder = apply {
      val location = IntArray(2)
      view.getLocationInWindow(location)
      val x = location[0] + view.width / 2f
      val y = location[1] + view.height / 2f
      setAnchor(x, y)
      setSize(view)
    }

    /**
     * Sets a pointer to start a [Target].
     */
    fun setSize(view: View): Builder = apply {
      this.width = view.width.toFloat()
      this.height = view.height.toFloat()
    }

    /**
     * Sets a pointer to start a [Target].
     */
    fun setAnchor(view: View): Builder = apply {
      val location = IntArray(2)
      view.getLocationInWindow(location)
      val x = location[0] + view.width / 2f
      val y = location[1] + view.height / 2f
      setAnchor(x, y)
    }

    /**
     * Sets an anchor point to start [Target].
     */
    fun setAnchor(x: Float, y: Float): Builder = apply {
      setAnchor(PointF(x, y))
    }

    /**
     * Sets an anchor point to start [Target].
     */
    fun setAnchor(anchor: PointF): Builder = apply {
      this.anchor = anchor
    }

    /**
     * Sets a text to start a [Target].
     */
    fun setText(text: CharSequence): Builder = apply {
      this.text = Text(text = text)
    }

    /**
     * Sets [shape] of the spot of [Target].
     */
    fun setShape(shape: Shape): Builder = apply {
      this.shape = shape
    }

    /**
     * Sets [effect] of the spot of [Target].
     */
    fun setEffect(effect: Effect): Builder = apply {
      this.effect = effect
    }

    /**
     * Sets [overlay] to be laid out to describe [Target].
     */
    fun setOverlay(overlay: View): Builder = apply {
      this.overlay = overlay
    }

    /**
     * Sets [OnTargetListener] to notify the state of [Target].
     */
    fun setOnTargetListener(listener: OnTargetListener): Builder = apply {
      this.listener = listener
    }

    fun build(): Target {

      val shape = RoundedRectangle(height = height + 20, width = width + 20, radius = 10F)

      return Target(
          anchor = anchor,
          width = width,
          height = height,
          text = text,
          shape = shape,
          effect = effect,
          overlay = overlay,
          listener = listener
      )
    }

    companion object {

      private val DEFAULT_ANCHOR = PointF(0f, 0f)

      private val DEFAULT_SHAPE = Circle(100f)

      private val DEFAULT_EFFECT = EmptyEffect()
    }
  }
}