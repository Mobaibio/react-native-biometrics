package com.mobaibiometric

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionError
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionOptions
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionResult
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionService
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionServiceListener
import bio.mobai.library.biometrics.enums.MBFaceStatus
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import java.io.ByteArrayOutputStream
@SuppressLint("UnsafeOptInUsageError")
class MobaiBiometricFragment(
  private val options: MBCaptureSessionOptions,
  private val context: ThemedReactContext
): Fragment(), MBCaptureSessionServiceListener {
  private lateinit var captureSessionService: MBCaptureSessionService

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    super.onCreateView(inflater, container, savedInstanceState)
    captureSessionService = MBCaptureSessionService(requireContext(), requireActivity() as LifecycleOwner, options, this)
    return captureSessionService.getCaptureSessionView()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    captureSessionService.startCamera()
  }



  override fun onCaptureFinished(result: MBCaptureSessionResult?) {
    result?.let {
      val value = mapOf(CAPTURE_FINISHED_PARAM_KEY to it)
      sendNativeEvent(value)
    }
  }

  override fun onCaptureStarted() {
    val value = mapOf(CAPTURE_STARTED_PARAM_KEY to getString(R.string.text_capture_started))
    sendNativeEvent(value)
  }

  override fun onCountdown(timeCounter: Int) {
    val value = mapOf(CAPTURE_COUNT_DOWN_PARAM_KEY to "$timeCounter")
    sendNativeEvent(value)
  }

  override fun onCaptureProgress(captureProgressCounter: Float) {
    val value = mapOf(CAPTURE_PROGRESS_PARAM_KEY to "$captureProgressCounter")
    sendNativeEvent(value)
  }

  override fun onFailure(errorEnum: MBCaptureSessionError) {
    val value = mapOf(FACE_STATUS_PARAM_KEY to errorEnum.name)
    sendNativeEvent(value)
  }

  override fun onValidating(faceStatus: MBFaceStatus) {
    val value = mapOf(FACE_STATUS_PARAM_KEY to faceStatus.name)
    sendNativeEvent(value)
  }


  @Suppress("DEPRECATION")
  private fun sendNativeEvent(value: Map<String, Any>) {
    val param = Arguments.createMap()
    if (value.containsKey(FACE_STATUS_PARAM_KEY)) {
      param.putString(FACE_STATUS_PARAM_KEY, value[FACE_STATUS_PARAM_KEY] as String)
    } else if (value.containsKey(CAPTURE_STARTED_PARAM_KEY)) {
      param.putString(CAPTURE_STARTED_PARAM_KEY, value[CAPTURE_STARTED_PARAM_KEY] as String)
    } else if (value.containsKey(CAPTURE_FAILURE_PARAM_KEY)) {
      param.putString(CAPTURE_FAILURE_PARAM_KEY, value[CAPTURE_FAILURE_PARAM_KEY] as String)
    } else if (value.containsKey(CAPTURE_PROGRESS_PARAM_KEY)) {
      param.putString(CAPTURE_PROGRESS_PARAM_KEY, value[CAPTURE_PROGRESS_PARAM_KEY] as String)
    } else if (value.containsKey(CAPTURE_COUNT_DOWN_PARAM_KEY)) {
      param.putString(CAPTURE_COUNT_DOWN_PARAM_KEY, value[CAPTURE_COUNT_DOWN_PARAM_KEY] as String)
    } else if (value.containsKey(CAPTURE_FINISHED_PARAM_KEY)) {


      val mbCaptureSessionResult = Arguments.createMap()

      val rows = Arguments.createArray()
      val bitmapString = (value[CAPTURE_FINISHED_PARAM_KEY] as MBCaptureSessionResult).frames.map {
        ImageUtil.convert(it)
      }
      bitmapString.forEach {
        rows.pushString(it)
      }

      val faceImageString = ImageUtil.convert((value[CAPTURE_FINISHED_PARAM_KEY] as MBCaptureSessionResult).faceImage)

      mbCaptureSessionResult.putString("image", faceImageString)
      mbCaptureSessionResult.putArray("padData", rows)
      param.putMap(CAPTURE_FINISHED_PARAM_KEY, mbCaptureSessionResult)
    }
    param?.let {
      val reactContext = context as ReactContext
      reactContext.getJSModule(RCTEventEmitter::class.java).receiveEvent(id, "topChange", it)
    }
  }

  private companion object {
    const val FACE_STATUS_PARAM_KEY = "messageFaceStatus"
    const val CAPTURE_STARTED_PARAM_KEY = "messageCaptureStarted"
    const val CAPTURE_FINISHED_PARAM_KEY = "messageCaptureFinished"
    const val CAPTURE_FAILURE_PARAM_KEY = "massageCaptureFailed"
    const val CAPTURE_PROGRESS_PARAM_KEY = "messageCaptureProgress"
    const val CAPTURE_COUNT_DOWN_PARAM_KEY = "messageCaptureCountDown"
  }

  private object ImageUtil {
    @Throws(IllegalArgumentException::class)
    fun convert(bitmap: Bitmap?): String {
      val outputStream = ByteArrayOutputStream()
      bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
      return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
  }
}
