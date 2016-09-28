package com.carecloud.carepaylibray.demographics.models;

import com.carecloud.carepaylibray.base.models.BaseTransitionsModel;

/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public class DemographicTransitionModel extends BaseTransitionsModel {
    public DemographicTransitionsDataObjectModel getData(){
        return super.getData(DemographicTransitionsDataObjectModel.class);
    }

}
