package com.mobaibiometric

import bio.mobai.library.biometrics.capturesession.MBCaptureSessionResult

object StorageMBCaptureSessionResult {
  private var bitmap: MBCaptureSessionResult? = null

  fun save(bitmap: MBCaptureSessionResult) {
    this.bitmap = bitmap
  }

  fun get() : MBCaptureSessionResult? {
    return this.bitmap
  }
}
