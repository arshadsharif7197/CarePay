package com.carecloud.carepay.practice.library.practicesetting.services;

import com.carecloud.carepay.practice.library.practicesetting.models.PracticeSettingDTO;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by prem_mourya on 10/24/2016.
 */

public interface PracticeSettingService {
    @GET(value = "dev/workflow/shamrock/practice_mode/practice_settings/information")
    Call<PracticeSettingDTO> getPracticeSettingInformation();
}
