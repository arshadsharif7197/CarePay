package com.carecloud.carepay.practice.library.signin.interfaces;

import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionUserPractice;
import com.carecloud.carepaylibray.interfaces.DTOInterface;

/**
 * @author pjohnson on 1/05/17.
 */
public interface SelectPracticeCallback extends DTOInterface {

    void onSelectPracticeCanceled();

    void onSelectPracticeLocationCanceled(PracticeSelectionUserPractice selectedPractice);
}