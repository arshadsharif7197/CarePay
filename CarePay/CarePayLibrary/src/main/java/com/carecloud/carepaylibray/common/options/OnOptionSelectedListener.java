package com.carecloud.carepaylibray.common.options;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;

/**
 * @author pjohnson on 2019-05-22.
 */
public interface OnOptionSelectedListener {
    void onOptionSelected(DemographicsOption option, int position);
}
