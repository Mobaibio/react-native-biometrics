package com.mobaibiometric

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionOptions
import bio.mobai.library.biometrics.capturesession.MBFaceStatusTexts
import bio.mobai.library.biometrics.capturesession.MBPreviewScaleType
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.BaseActivityEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.facebook.react.bridge.WritableMap
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
    if (options.hasKey("faceStatusTexts")) {
        options.getMap("faceStatusTexts")?.let {
          tempOptions.faceStatusLabelTexts(getInputTexts(it))
        }
    }
    if (options.hasKey("isDebugging")) {
      tempOptions.debugging(options.getBoolean("isDebugging"))
    }
    if (options.hasKey("previewScaleType")) {
      when(options.getInt("previewScaleType")) {
        1 -> {tempOptions.scaleType(MBPreviewScaleType.FILL_CENTER)}
        2 -> {tempOptions.scaleType(MBPreviewScaleType.FIT_CENTER)}
      }
    }
    if (options.hasKey("showCountdownLabel")) {
      tempOptions.countdownTimerLabel(options.getBoolean("showCountdownLabel"))
    }
    if (options.hasKey("showProgressBar")) {
      tempOptions.progressBar(options.getBoolean("showProgressBar"))
    }
    if (options.hasKey("showFaceStatusLabel")) {
      tempOptions.faceStatusLabel(options.getBoolean("showFaceStatusLabel"))
    }
    if (options.hasKey("countdownLabelText")) {
      tempOptions.countdownLabelText(options.getString("countdownLabelText").toString())
    }
    return tempOptions.build()
  }

  private fun getInputTexts(inputText: ReadableMap): MBFaceStatusTexts {
    var faceTooFarAway = "Face too far away"
    var faceTooFarUp = "Face too far up"
    var faceTooFarDown = "Face too far down"
    var faceTooFarLeft = "Face too far left"
    var faceTooFarRight = "Face too far right"
    var faceTooClose = "Face too close"
    var faceNotFound = "Face not found"
    var tooManyFaces = "Too many faces"
    var validFace = "Valid face"

    if (inputText.hasKey("faceTooFarAway")) {
      faceTooFarAway = inputText.getString("faceTooFarAway").toString()
    }
    if (inputText.hasKey("faceTooFarUp")) {
      faceTooFarUp = inputText.getString("faceTooFarUp").toString()
    }
    if (inputText.hasKey("faceTooFarDown")) {
      faceTooFarDown = inputText.getString("faceTooFarDown").toString()
    }
    if (inputText.hasKey("faceTooFarLeft")) {
      faceTooFarLeft = inputText.getString("faceTooFarLeft").toString()
    }
    if (inputText.hasKey("faceTooFarRight")) {
      faceTooFarRight = inputText.getString("faceTooFarRight").toString()
    }
    if (inputText.hasKey("faceTooClose")) {
      faceTooClose = inputText.getString("faceTooClose").toString()
    }
    if (inputText.hasKey("faceNotFound")) {
      faceNotFound = inputText.getString("faceNotFound").toString()
    }
    if (inputText.hasKey("tooManyFaces")) {
      tooManyFaces = inputText.getString("tooManyFaces").toString()
    }
    if (inputText.hasKey("validFace")) {
      validFace = inputText.getString("validFace").toString()
    }
    return MBFaceStatusTexts(
      faceTooFarAway,
      faceTooFarUp,
      faceTooFarDown,
      faceTooFarLeft,
      faceTooFarRight,
      faceTooClose,
      faceNotFound,
      tooManyFaces,
      validFace
    )
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
