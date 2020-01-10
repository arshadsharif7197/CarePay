/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.carecloud.carepay.patient.utils;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;


/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 * This class assumes that the {@link android.Manifest.permission#USE_FINGERPRINT}
 * permission has already been granted. (As of API 23 this permission is normal instead of dangerous
 * and is granted at install time.)
 */
@SuppressWarnings("MissingPermission")
public class FingerprintUiHelper extends FingerprintManagerCompat.AuthenticationCallback {

    private final FingerprintManagerCompat mFingerprintManager;
    private final Callback mCallback;
    private CancellationSignal mCancellationSignal;
    private boolean mSelfCancelled;

    public FingerprintUiHelper(FingerprintManagerCompat fingerprintManager, Callback callback) {
        mFingerprintManager = fingerprintManager;
        mCallback = callback;
    }

    public static boolean isFingerprintAuthAvailable(FingerprintManagerCompat fingerprintManagerCompat) {
        return fingerprintManagerCompat.isHardwareDetected() && fingerprintManagerCompat.hasEnrolledFingerprints();
    }

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable(mFingerprintManager)) {
            return;
        }
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        mFingerprintManager.authenticate(cryptoObject, 0 /* flags */, mCancellationSignal, this, null);
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            mCallback.onError(errString.toString());
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        mCallback.onError(helpString.toString());
    }

    @Override
    public void onAuthenticationFailed() {
        mCallback.onError("Fingerprint not recognized. Try again");
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        mCallback.onAuthenticated();
    }

    public interface Callback {

        void onAuthenticated();

        void onError(String error);
    }
}
