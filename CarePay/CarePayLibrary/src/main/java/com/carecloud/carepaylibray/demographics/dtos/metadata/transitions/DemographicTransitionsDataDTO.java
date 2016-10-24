package com.carecloud.carepaylibray.demographics.dtos.metadata.transitions;

import com.carecloud.carepaylibray.base.models.BaseTransitionsDataModel;

/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 * Model for transitions data.
 */
class DemographicTransitionsDataDTO extends BaseTransitionsDataModel {

    public DemographicTransitionsPropertiesDTO getProperties(){
        return super.getProperties(DemographicTransitionsPropertiesDTO.class);
    }

}
