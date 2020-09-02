package com.takusemba.spotlightsample

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.OnTargetListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target

class MainActivity : AppCompatActivity() {

  private var spotlight: Spotlight? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<View>(R.id.start).setOnClickListener {
      val targets = ArrayList<Target>()

      // first target
      val firstRoot = FrameLayout(this)
      val first = layoutInflater.inflate(R.layout.layout_target, firstRoot)
      val firstView = findViewById<View>(R.id.one)
      val firstTarget = Target.Builder()
          .setView(firstView)
          .setText("HELLO FROM AWESOME NEW FEATURE")
          .setOnTargetListener(object : OnTargetListener {
            override fun onStarted() {
            }

            override fun onClicked() {
              spotlight?.next()
            }

            override fun onEnded() {
            }
          })
          .build()

      targets.add(firstTarget)

      // second target
      val secondRoot = FrameLayout(this)
      val second = layoutInflater.inflate(R.layout.layout_target, secondRoot)
      val secondTarget = Target.Builder()
          .setView(findViewById<View>(R.id.two))
          .setText(
              "HELLO FROM AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME NEW FEATURE FOR YOU!!")
          .setOnTargetListener(object : OnTargetListener {
            override fun onStarted() {
            }

            override fun onClicked() {
              spotlight?.next()
            }

            override fun onEnded() {
            }
          })
          .build()

      targets.add(secondTarget)

      // third target
      val thirdRoot = FrameLayout(this)
      val third = layoutInflater.inflate(R.layout.layout_target, thirdRoot)
      val thirdTarget = Target.Builder()
          .setView(findViewById<View>(R.id.three))
          .setText(
              "HELLO FROM AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME AWESOME NEW FEATURE FOR YOU!!")
          .setOnTargetListener(object : OnTargetListener {
            override fun onStarted() {
            }

            override fun onClicked() {
              spotlight?.finish()
            }

            override fun onEnded() {
            }
          })
          .build()

      targets.add(thirdTarget)

      // create spotlight
      val spotlight = Spotlight.Builder(this@MainActivity)
          .setTargets(targets)
          .setBackgroundColor(R.color.spotlightBackground)
          .setDuration(1000L)
          .setAnimation(DecelerateInterpolator(2f))
          .setOnSpotlightListener(object : OnSpotlightListener {
            override fun onStarted() {
            }

            override fun onEnded() {
            }
          })
          .build()

      spotlight.start()
      this.spotlight = spotlight

      val nextTarget = View.OnClickListener { spotlight.next() }

      val closeSpotlight = View.OnClickListener { spotlight.finish() }

      first.findViewById<View>(R.id.close_target).setOnClickListener(nextTarget)
      second.findViewById<View>(R.id.close_target).setOnClickListener(nextTarget)
      third.findViewById<View>(R.id.close_target).setOnClickListener(nextTarget)

      first.findViewById<View>(R.id.close_spotlight).setOnClickListener(closeSpotlight)
      second.findViewById<View>(R.id.close_spotlight).setOnClickListener(closeSpotlight)
      third.findViewById<View>(R.id.close_spotlight).setOnClickListener(closeSpotlight)
    }
  }
}
