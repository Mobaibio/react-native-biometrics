package com.mobaibiometric


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcel
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionResult
import com.mobaibiometric.MobaiBiometricModule.Companion.BITMAP_IMAGE
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.reflect.KParameter

class CaptureActivity : AppCompatActivity(), SessionServiceListener {
  var isPermissionGranted = false

  @SuppressLint("UnsafeOptInUsageError")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_capture)

    val captureFragment = CaptureFragment()
    captureFragment.sessionServiceListener = this
    supportFragmentManager.beginTransaction()
      .add(R.id.frame_layout, captureFragment)
      .commit()
  }
  /**
   * Check and request Camera Permission
   * @param, The context it is called from
   */
  fun verifyCameraPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
      isPermissionGranted = true
    }else{
      ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }
  }//END

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == CAMERA_PERMISSION_CODE){
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Permission granted" , Toast.LENGTH_LONG).show()
        isPermissionGranted = true
      }
    }
  }

  companion object{
    private const val CAMERA_PERMISSION_CODE = 1
  }

  @SuppressLint("Recycle")
  override fun onCaptureFinished(result: MBCaptureSessionResult) {
    val returnIntent = Intent()

    StorageMBCaptureSessionResult.save(result)
    returnIntent.putExtra(MobaiBiometricModule.FILE_PATHS, RESULT_OK)
    setResult(MobaiBiometricModule.IMAGE_PICKER_REQUEST, returnIntent)
    finish()
  }
}


object ImageUtil {
  @Throws(IllegalArgumentException::class)
  fun convert(bitmap: Bitmap?): String {
    val outputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)

    return base64String
  }
}
