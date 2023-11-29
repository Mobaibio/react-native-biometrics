package com.mobaibiometric

import android.content.res.Resources
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionOptions
import bio.mobai.library.biometrics.capturesession.MBPreviewScaleType
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.annotations.ReactPropGroup

class MobaiBiometricViewManager(private val callerContext: ReactApplicationContext): SimpleViewManager<FrameLayout>() {
 private var captureSessionOptions = MBCaptureSessionOptions.Builder().build()
 private lateinit var context: ThemedReactContext
 private var propHeight = Resources.getSystem().displayMetrics.heightPixels
 private  var propWidth = Resources.getSystem().displayMetrics.widthPixels

  override fun getName() = REACT_CLASS

  /**
   * Return a basic FrameLayout which will later hold the Fragment
   */
  override fun createViewInstance(context: ThemedReactContext): FrameLayout {
    this.context = context
    return FrameLayout(context)
  }

  /**
   * Map the "create" command to an integer
   */
  override fun getCommandsMap() = mapOf("create" to COMMAND_CREATE)

  @ReactProp(name = "options")
  fun setOptions(view: FrameLayout, options: ReadableMap) {
    captureSessionOptions = mapOptions(options)
  }
  @ReactPropGroup(names = ["width", "height"], customType = "Style")
  fun setStyle(view: FrameLayout, index: Int, value: Int) {
    if (index == 0) propWidth = value
    if (index == 1) propHeight = value
  }
  private fun mapOptions(options: ReadableMap): MBCaptureSessionOptions {
    val tempOptions = MBCaptureSessionOptions.Builder().scaleType(MBPreviewScaleType.FILL_CENTER)

    if (options.hasKey("autoCaptureEnabled")) {
      tempOptions.automaticCapture(options.getBoolean("autoCaptureEnabled"))
    }
    if (options.hasKey("numberOfFramesBeforeCapture")) {
      tempOptions.framesBeforeCapture(options.getInt("numberOfFramesBeforeCapture"))
    }
    if (options.hasKey("numberOfFrameToCollect")) {

      tempOptions.framesToCollect(options.getInt("numberOfFrameToCollect"))
    }
    if (options.hasKey("frameInterval")) {
      tempOptions.frameInterval(options.getInt("frameInterval"))
    }
    if (options.hasKey("timeBeforeAutomaticCapture")) {
      tempOptions.timeBeforeCapture(options.getInt("timeBeforeAutomaticCapture"))
    }
    if (options.hasKey("previewScaleType")) {
      when(options.getInt("previewScaleType")) {
        1 -> {tempOptions.scaleType(MBPreviewScaleType.FILL_CENTER)}
        2 -> {tempOptions.scaleType(MBPreviewScaleType.FIT_CENTER)}
      }
    }

    return tempOptions.build()
  }

  /**
   * Handle "create" command (called from JS) and call createFragment method
   */
  override fun receiveCommand(
    root: FrameLayout, commandId: String, args: ReadableArray?
  ) {
    super.receiveCommand(root, commandId, args)
    val reactNativeViewId = requireNotNull(args).getInt(0)

    when (commandId.toInt()) {
      COMMAND_CREATE -> createFragment(root, reactNativeViewId)
    }
  }

  /**
   * Replace your React Native view with a custom fragment
   */
  private fun createFragment(root: FrameLayout, reactNativeViewId: Int) {
    val parentView = root.findViewById<ViewGroup>(reactNativeViewId)
    setupLayout(parentView)

    val biometricFragment = MobaiBiometricFragment(captureSessionOptions, context)
    val activity = callerContext.currentActivity as FragmentActivity
    activity.supportFragmentManager.beginTransaction()
      .replace(reactNativeViewId, biometricFragment, reactNativeViewId.toString()).commit()
  }

  private fun setupLayout(view: View) {
    Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
      override fun doFrame(frameTimeNanos: Long) {
        manuallyLayoutChildren(view)
        view.viewTreeObserver.dispatchOnGlobalLayout()
        Choreographer.getInstance().postFrameCallback(this)
      }
    })
  }
  /**
   * Layout all children properly
   */
  private fun manuallyLayoutChildren(view: View) {
    view.measure(
      View.MeasureSpec.makeMeasureSpec(propWidth, View.MeasureSpec.EXACTLY),
      View.MeasureSpec.makeMeasureSpec(propHeight, View.MeasureSpec.EXACTLY)
    )
    view.layout(0, 0, propWidth, propHeight)
  }

  override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any> {
    return mapOf("topChange" to mapOf("phasedRegistrationNames" to mapOf("bubbled" to "onChange")))
  }

  companion object {
    const val REACT_CLASS = "RCTMBCaptureSessionView"
    private const val COMMAND_CREATE = 1
  }
}
