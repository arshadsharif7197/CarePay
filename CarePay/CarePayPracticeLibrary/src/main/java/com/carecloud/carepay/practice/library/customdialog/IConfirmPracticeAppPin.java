package com.carecloud.carepay.practice.library.customdialog;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;

/**
 * Created by sudhir_pingale on 11/10/2016.
 */

public interface IConfirmPracticeAppPin {

    void onPinConfirmationCheck(boolean isCorrectPin, String pin, TransitionDTO transitionDTO);
}
