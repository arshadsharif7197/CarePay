package com.carecloud.carepaylibray.demographics.models;

import com.carecloud.carepaylibray.base.models.BaseTransitionsModel;
import com.google.gson.Gson;

/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public class DemographicTransitionModel extends BaseTransitionsModel {

    public DemographicTransitionsDataObjectModel getTransitionsData(){

        Gson gson = new Gson();
        return gson.fromJson(super.getData(), DemographicTransitionsDataObjectModel.class);
    }

}
