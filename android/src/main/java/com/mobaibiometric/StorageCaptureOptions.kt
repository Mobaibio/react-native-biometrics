package com.mobaibiometric

import bio.mobai.library.biometrics.capturesession.MBCaptureSessionOptions
import bio.mobai.library.biometrics.capturesession.MBCaptureSessionResult

object StorageCaptureOptions {
  private var mbCaptureSessionOptions: MBCaptureSessionOptions? = null

  fun save(mbCaptureSessionOptions: MBCaptureSessionOptions) {
    this.mbCaptureSessionOptions = mbCaptureSessionOptions
  }

  fun get() : MBCaptureSessionOptions? {
    return this.mbCaptureSessionOptions
  }
}
