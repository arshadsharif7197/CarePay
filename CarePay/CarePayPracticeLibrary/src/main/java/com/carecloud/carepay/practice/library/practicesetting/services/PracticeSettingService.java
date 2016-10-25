package com.carecloud.carepay.practice.library.practicesetting.services;

import com.carecloud.carepay.practice.library.practicesetting.models.PracticeSettingDTO;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by prem_mourya on 10/24/2016.
 */

public interface PracticeSettingService {
    @GET(value = "dev/workflow/carepay/practice_mode/practice_settings/information")
    Call<PracticeSettingDTO> getPracticeSettingInformation();
}
