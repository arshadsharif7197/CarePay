package com.carecloud.carepay.practice.library.customdialog;

import com.carecloud.carepay.practice.library.practicesetting.models.PracticeSettingDTO;

/**
 * Created by sudhir_pingale on 11/10/2016.
 */

public interface IConfirmPracticeAppPin {

    void onPinConfirmationCheck(boolean isCorrectPin, String pin);
}
