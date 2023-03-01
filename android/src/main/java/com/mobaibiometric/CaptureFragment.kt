package com.mobaibiometric

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import bio.mobai.library.biometrics.capturesession.*
import bio.mobai.library.biometrics.enums.MBCaptureSessionStatus
import bio.mobai.library.biometrics.enums.MBFaceStatus
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.mobaibiometric.databinding.FragmentCaptureBinding

interface SessionServiceListener {
  fun onCaptureFinished(result: MBCaptureSessionResult)
}

@SuppressLint("SetTextI18n")
class CaptureFragment : Fragment(R.layout.fragment_capture) , MBCaptureSessionServiceListener {
    private var captureSessionService: MBCaptureSessionService? = null
    private lateinit var mBinding : FragmentCaptureBinding
    var sessionServiceListener: SessionServiceListener? = null

  @SuppressLint("UnsafeOptInUsageError")
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    mBinding = FragmentCaptureBinding.inflate(inflater,container,false)

    if (requireActivity() is CaptureActivity) {
      (requireActivity() as CaptureActivity).verifyCameraPermission()
    }

    val options = StorageCaptureOptions.get()

    options?.let {
      captureSessionService = MBCaptureSessionService(requireContext(), this, it, this)
      mBinding.container.addView(captureSessionService!!.getCaptureSessionView())
      if ((requireActivity() as CaptureActivity).isPermissionGranted) {
        captureSessionService!!.startCamera()

        if (!it.automaticCapture) {
          mBinding.btnStartCapture.visibility = View.VISIBLE
          mBinding.btnStartCapture.setOnClickListener {
            captureSessionService!!.startCaptureSession()
          }
        }
      }
    }

    return mBinding.root
  }

  override fun onCaptureFinished(result: MBCaptureSessionResult?){
        requireActivity().runOnUiThread {
          val params = Arguments.createMap().apply {
            putString("eventProperty", "someValue")
          }
          if (result != null) {
            sessionServiceListener?.onCaptureFinished(result)
          }
        }
    }
    override fun onCaptureSessionStatusChanged(captureStatus: MBCaptureSessionStatus) {
        requireActivity().runOnUiThread {
            displayCaptureStatus(captureStatus)
        }
    }

    override fun onValidating(faceStatus: MBFaceStatus) {
        requireActivity().runOnUiThread {
            displayFaceStatus(faceStatus)
        }
    }

    override fun onFailure(errorEnum: MBCaptureSessionError) {
        requireActivity().runOnUiThread {
            if (errorEnum == MBCaptureSessionError.UNABLE_TO_OPEN_CAMERA) {
                mBinding.llCapture.visibility = View.GONE
                mBinding.llFace.visibility = View.GONE
                mBinding.etCaptureError.visibility = View.VISIBLE
                mBinding.etCaptureError.text = "An error have occurred while opening the camera."
            }
        }
    }

    override fun onCaptureStarted() {
        Toast.makeText(requireContext(),"Capture started",Toast.LENGTH_LONG).show()
    }
    private fun displayCaptureStatus(status: MBCaptureSessionStatus) {
        when(status) {
            MBCaptureSessionStatus.READY -> {
                mBinding.etCaptureStatus.text = "Ready To Capture"
            }
            MBCaptureSessionStatus.CAPTURING -> {
                mBinding.etCaptureStatus.text = "Capturing"
            }
            MBCaptureSessionStatus.CAPTURE_COMPLETED -> {
                mBinding.etCaptureStatus.text = "Capture Completed"
            }
            MBCaptureSessionStatus.WAITING -> {
                mBinding.etCaptureStatus.text = "Validating"
            }
            MBCaptureSessionStatus.CAMERA_READY -> {
                mBinding.etCaptureStatus.text = "Camera Ready"
            }
          else -> {

          }
        }
    }


    private fun displayFaceStatus(status: MBFaceStatus) {
        when(status) {
            MBFaceStatus.VALID -> {
                mBinding.etFaceStatus.text = "Valid Face"
            }
            MBFaceStatus.NOT_FOUND -> {
                mBinding.etFaceStatus.text = "No Face Found"
            }
            MBFaceStatus.TOO_MANY -> {
                mBinding.etFaceStatus.text = "More Than One Face"
            }
            MBFaceStatus.TOO_FAR_RIGHT -> {
                mBinding.etFaceStatus.text = "Too Far Right"
            }
            MBFaceStatus.TOO_FAR_LEFT -> {
                mBinding.etFaceStatus.text = "Too Far Left"
            }
            MBFaceStatus.TOO_FAR_DOWN-> {
                mBinding.etFaceStatus.text = "Too Far Down"
            }
            MBFaceStatus.TOO_FAR_UP-> {
                mBinding.etFaceStatus.text = "Too Far Up"
            }
            MBFaceStatus.TOO_CLOSE -> {
                mBinding.etFaceStatus.text = "Too Close"
            }
            MBFaceStatus.TOO_FAR_AWAY -> {
                mBinding.etFaceStatus.text = "Too Far Away"
            }
        }
    }

}
