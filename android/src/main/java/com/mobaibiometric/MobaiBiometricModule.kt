package com.mobaibiometric

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionOptions
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionResult
import com.facebook.react.bridge.*
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.facebook.react.modules.core.DeviceEventManagerModule


class MobaiBiometricModule(val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  private val activityEventListener =
    object : BaseActivityEventListener() {
      override fun onActivityResult(
        activity: Activity?,
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
      ) {
        if (requestCode == IMAGE_PICKER_REQUEST) {
          val resultOK = intent?.getIntExtra(FILE_PATHS, 1)

          val getData = StorageMBCaptureSessionResult.get()
          if (resultOK == AppCompatActivity.RESULT_OK && getData != null) {
            val rows = Arguments.createArray()
            val bitmapString = getData.frames
              .map { ImageUtil.convert(it) }

            bitmapString.forEach {
              rows.pushString(it)
            }

            val params = Arguments.createMap()
            params.putString("image", bitmapString.first())
            params.putArray("padData", rows)

            sendEvent(reactContext, "onCaptureFinished", params)
          }
        }
    }
}

  init {
    reactContext.addActivityEventListener(activityEventListener)
  }

  override fun getConstants(): Map<String, Any>? {
    val constants: MutableMap<String, Any> = HashMap()
    constants["EVENT_SUCCESS"] = "onCaptureFinished"
    constants["EVENT_FAILURE"] = "onFailureWithErrorMessage"
    return constants
  }

  @ReactMethod
  fun launch(options: ReadableMap) {
    StorageCaptureOptions.save(mapOptions(options))
    runOnUiThread {
      val activity = currentActivity

      val intent = Intent(
        reactApplicationContext,
        CaptureActivity::class.java
      )

      try {
        activity?.startActivityForResult(intent, IMAGE_PICKER_REQUEST)
      } catch (t: Throwable) {
        val params = Arguments.createMap()
        params.putString("errorDescription", "unable to start activity")
        sendEvent(reactContext, "onFailureWithErrorMessage", params)
      }
    }
  }

  fun mapOptions(options: ReadableMap) : MBCaptureSessionOptions {
    val tempOptions = MBCaptureSessionOptions.Builder()



    if (options.hasKey("autoCaptureEnabled")) {
      tempOptions.automaticCapture(options.getBoolean("autoCaptureEnabled"))
    } else if (options.hasKey("numberOfFramesBeforeCapture")) {
//      tempOptions = MBCaptureSessionOptions.Builder().numberOfFramesBeforeCapture(options.getInt("numberOfFramesBeforeCapture")).build()
    } else if (options.hasKey("numberOfFrameToCollect")) {
      tempOptions.framesToCollect(options.getInt("numberOfFrameToCollect"))
    } else if (options.hasKey("frameInterval")) {
      tempOptions.frameInterval(options.getInt("frameInterval"))
    } else if (options.hasKey("faceQualityEnabled")) {
//      tempOptions = MBCaptureSessionOptions.Builder().faceQualityEnabled(options.getBoolean("faceQualityEnabled")).build()
    } else if (options.hasKey("timeBeforeAutomaticCapture")) {
//      tempOptions = MBCaptureSessionOptions.Builder().timeBeforeAutomaticCapture(options.getBoolean("timeBeforeAutomaticCapture")).build()
    } else if (options.hasKey("isDebugging")) {
//      tempOptions = MBCaptureSessionOptions.Builder().isDebugging(options.getBoolean("timeBeforeAutomaticCapture")).build()
    }

    return tempOptions.build()
  }

  fun sendEvent(reactContext: ReactContext, eventName: String, MBCaptureSessionResult: WritableMap) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, MBCaptureSessionResult)
  }

  companion object {
    const val IMAGE_PICKER_REQUEST = 1
    const val BITMAP_IMAGE = "BITMAP_IMAGE"
    const val FILE_PATHS = "FILE_PATHS"
    const val NAME = "MobaiReactNative"
  }
}
