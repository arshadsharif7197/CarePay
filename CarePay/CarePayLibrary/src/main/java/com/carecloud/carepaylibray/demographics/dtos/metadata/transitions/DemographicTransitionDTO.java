package com.carecloud.carepaylibray.demographics.dtos.metadata.transitions;

import com.carecloud.carepaylibray.base.models.BaseTransitionsModel;

/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 * MOdel for demographics transition.
 */

class DemographicTransitionDTO extends BaseTransitionsModel {
    public DemographicTransitionsDataObjectDTO getData(){
        return super.getData(DemographicTransitionsDataObjectDTO.class);
    }
}
