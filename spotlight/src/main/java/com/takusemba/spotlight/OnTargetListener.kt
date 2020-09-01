package com.takusemba.spotlight

/**
 * Listener to notify the state of Target.
 */
interface OnTargetListener {

  /**
   * Called when Target is started
   */
  fun onStarted()

  /**
   * Called when user taps somewhere on window
   */
  fun onClicked()

  /**
   * Called when Target is started
   */
  fun onEnded()
}
