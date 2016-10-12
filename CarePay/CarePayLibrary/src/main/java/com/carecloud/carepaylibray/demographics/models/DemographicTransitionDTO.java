package com.carecloud.carepaylibray.demographics.models;

import com.carecloud.carepaylibray.base.models.BaseTransitionsModel;

/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 * MOdel for demographics transition.
 */
public class DemographicTransitionDTO extends BaseTransitionsModel {
    public DemographicTransitionsDataObjectDTO getData(){
        return super.getData(DemographicTransitionsDataObjectDTO.class);
    }

}
