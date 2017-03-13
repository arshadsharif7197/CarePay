package com.carecloud.carepay.practice.library.util;

import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.payments.models.XPatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.XPendingBalanceDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 3/13/17.
 */

public class PracticeUtil {

    public static Map<String, String> getProfilePhotoMap(List<XPatientBalanceDTO> patientBalances) {
        Map<String, String> map = new HashMap<>();

        for (XPatientBalanceDTO patientBalanceDTO: patientBalances) {
            DemographicPayloadInfoDTO demographics = patientBalanceDTO.getDemographics();
            String photoUrl = demographics.getPayload().getPersonalDetails().getProfilePhoto();

            if (null == photoUrl || photoUrl.isEmpty()) {
                continue;
            }

            for (XPendingBalanceDTO pendingBalanceDTO: patientBalanceDTO.getBalances()) {

                String patientId = pendingBalanceDTO.getMetadata().getPatientId();
                if (!map.containsKey(patientId)) {
                    map.put(patientId, photoUrl);
                    break;
                }
            }
        }

        return map;
    }

}
