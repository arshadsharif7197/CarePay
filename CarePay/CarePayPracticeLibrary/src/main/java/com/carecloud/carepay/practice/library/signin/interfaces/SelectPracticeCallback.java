package com.carecloud.carepay.practice.library.signin.interfaces;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.interfaces.DTOInterface;
import com.carecloud.carepaylibray.signinsignup.dto.Partners;

/**
 * @author pjohnson on 1/05/17.
 */
public interface SelectPracticeCallback extends DTOInterface {

    void onSelectPracticeCanceled();

    void onSelectPracticeLocationCanceled(UserPracticeDTO selectedPractice);

    void onSelectPracticeManagement(Partners selectedPracticeManagement);
}