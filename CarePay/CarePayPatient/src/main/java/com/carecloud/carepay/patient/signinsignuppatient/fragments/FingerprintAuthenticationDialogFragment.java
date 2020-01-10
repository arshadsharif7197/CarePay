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

package com.carecloud.carepay.patient.signinsignuppatient.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.signinsignuppatient.FingerPrintInterface;
import com.carecloud.carepay.patient.utils.FingerprintUiHelper;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.utils.EncryptionUtil;


/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 */
public class FingerprintAuthenticationDialogFragment extends DialogFragment
        implements FingerprintUiHelper.Callback {


    private static final long ERROR_TIMEOUT_MILLIS = 500;
    private static final long SUCCESS_DELAY_MILLIS = 1300;

    private Stage mStage = Stage.FINGERPRINT;

    private FingerprintManagerCompat.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private FingerPrintInterface callback;
    private ImageView mIcon;
    private TextView statusTextView;

    public FingerprintAuthenticationDialogFragment() {
    }

    public static FingerprintAuthenticationDialogFragment newInstance() {
        return new FingerprintAuthenticationDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FingerPrintInterface) {
            callback = (FingerPrintInterface) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);

        // We register a new user account here. Real apps should do this with proper UIs.
        enroll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fingerprint, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(Label.getLabel("signin.fingerPrintDialog.title.label.dialogTitle"));
        Button mCancelButton = view.findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(view1 -> dismiss());
        mIcon = view.findViewById(R.id.fingerPrintImageView);
        statusTextView = view.findViewById(R.id.fingerprint_status);
        FingerprintManagerCompat fingerPrintManager = FingerprintManagerCompat.from(getContext());
        mFingerprintUiHelper = new FingerprintUiHelper(fingerPrintManager, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mStage == Stage.FINGERPRINT) {
            mFingerprintUiHelper.startListening(mCryptoObject);
        }
    }

    public void setStage(Stage stage) {
        mStage = stage;
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    /**
     * Sets the crypto object to be passed in when authenticating with fingerprint.
     */
    public void setCryptoObject(FingerprintManagerCompat.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }

    /**
     * Enrolls a user to the fake backend.
     */
    private void enroll() {
    }

    @Override
    public void onAuthenticated() {
        statusTextView.removeCallbacks(mResetErrorTextRunnable);
        mIcon.setImageResource(R.drawable.ic_fingerprint_success);
        mIcon.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        statusTextView.setText(Label.getLabel("signin.fingerPrintDialog.title.label.fingerPrintRecognized"));
        mIcon.postDelayed(() -> {
            String user = ApplicationPreferences.getInstance().getUserName();
            String pwd = EncryptionUtil.decrypt(getContext(), ApplicationPreferences.getInstance().getUserPassword(), user);
            callback.authenticate(user, pwd);
            dismiss();
        }, SUCCESS_DELAY_MILLIS);
    }

    @Override
    public void onError(final String error) {
        mIcon.postDelayed(() -> {
            mIcon.setImageResource(R.drawable.ic_fingerprint_error);
            mIcon.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            statusTextView.setText(error);
            statusTextView.removeCallbacks(mResetErrorTextRunnable);
            statusTextView.postDelayed(mResetErrorTextRunnable, SUCCESS_DELAY_MILLIS);
        }, ERROR_TIMEOUT_MILLIS);

    }

    private Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null) {
//            statusTextView.setTextColor(
//                    statusTextView.getResources().getColor(R.color.hint_color));
                statusTextView.setText(Label.getLabel("signin.fingerPrintDialog.textView.label.touchSensor"));
                mIcon.setImageResource(R.drawable.ic_touch_id);
                mIcon.setBackground(getResources().getDrawable(R.drawable.button_blue_fill_background));
            }
        }
    };

    /**
     * Enumeration to indicate which authentication method the user is trying to authenticate with.
     */
    public enum Stage {
        FINGERPRINT,
        NEW_FINGERPRINT_ENROLLED,
        PASSWORD
    }
}
