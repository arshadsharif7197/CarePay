package com.carecloud.carepay.patient.retail.interfaces;

import com.carecloud.carepay.patient.retail.models.RetailModel;
import com.carecloud.carepay.patient.retail.models.RetailPracticeDTO;

/**
 * Created by lmenendez on 11/20/17
 */

public interface RetailInterface {

    void displayRetailStore(RetailModel retailModel, RetailPracticeDTO retailPractice);

    void displayToolbar(boolean visibility);
}
