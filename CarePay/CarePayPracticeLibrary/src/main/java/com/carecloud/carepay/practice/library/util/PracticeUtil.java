package com.carecloud.carepay.practice.library.util;

import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 3/13/17.
 */

public class PracticeUtil {

    /**
     * Create a map of all image urls as provided by the Demographics portion of patient balance payload
     * @param patientBalances patient balance list payload
     * @return map of urls
     */
    public static Map<String, String> getProfilePhotoMap(List<PatientBalanceDTO> patientBalances) {
        Map<String, String> map = new HashMap<>();

        for (PatientBalanceDTO patientBalanceDTO: patientBalances) {
            DemographicPayloadInfoDTO demographics = patientBalanceDTO.getDemographics();
            String photoUrl = demographics.getPayload().getPersonalDetails().getProfilePhoto();

            if (null == photoUrl || photoUrl.isEmpty()) {
                continue;
            }

            for (PendingBalanceDTO pendingBalanceDTO: patientBalanceDTO.getBalances()) {

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
