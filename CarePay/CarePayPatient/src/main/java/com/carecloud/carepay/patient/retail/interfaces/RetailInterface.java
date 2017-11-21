package com.carecloud.carepay.patient.retail.interfaces;

import com.carecloud.carepay.patient.retail.models.RetailModel;
import com.carecloud.carepay.patient.retail.models.RetailPracticeDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;

/**
 * Created by lmenendez on 11/20/17
 */

public interface RetailInterface {

    void displayRetailStore(RetailModel retailModel, RetailPracticeDTO retailPractice, UserPracticeDTO practiceDTO);

    void displayToolbar(boolean visibility);
}
